package com.example.sookwalk.data.repository

import android.graphics.Bitmap
import com.example.sookwalk.data.local.dao.FavoriteDao
import com.example.sookwalk.data.local.dao.SearchHistoryDao
import com.example.sookwalk.data.local.entity.map.CategoryWithCount
import com.example.sookwalk.data.local.entity.map.FavoriteCategoryEntity
import com.example.sookwalk.data.local.entity.map.SavedPlaceEntity
import com.example.sookwalk.data.local.entity.map.SearchHistoryEntity
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class MapRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val searchHistoryDao: SearchHistoryDao,
    private val placesClient: PlacesClient,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    val allCategories: Flow<List<CategoryWithCount>> = favoriteDao.getAllCategories()
    val searchHistory: Flow<List<SearchHistoryEntity>> = searchHistoryDao.getSearchHistory()

    private fun userCol() = auth.currentUser?.uid?.let { uid ->
        db.collection("users").document(uid)
    }

    fun getPlacesByCategory(categoryId: Long) = favoriteDao.getPlacesByCategory(categoryId)

    suspend fun addCategory(name: String, color: Long) {
        // 1. Room에 저장 (로컬)
        val category = FavoriteCategoryEntity(name = name, iconColor = color)
        favoriteDao.insertCategory(category)

        // 2. Firebase에 저장 (서버 백업)
        userCol()?.collection("favorite_categories")?.add(
            mapOf(
                "name" to name,
                "iconColor" to color,
                "createdAt" to System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteCategory(category: FavoriteCategoryEntity) {
        // (1) 로컬 Room에서 삭제
        favoriteDao.deleteCategory(category)

        // (2) Firestore에서 삭제 (remoteId가 있을 때만)
        if (category.remoteId.isNotEmpty()) {
            try {
                val userRef = userCol() ?: return

                // 카테고리 문서 삭제
                userRef.collection("favorite_categories").document(category.remoteId).delete()

                // [심화] 카테고리 안에 있는 장소들도 서버에서 지워줘야 완벽함 (Subcollection을 쓴다면)
                // 지금 구조상 saved_places를 따로 관리한다면, 해당 카테고리 remoteId를 가진 장소들을 찾아서 지워야 합니다.
                // 여기서는 카테고리 문서 자체만 지우는 것으로 처리합니다.
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun searchPlaces(query: String): List<SavedPlaceEntity> {
        val placeFields = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.FORMATTED_ADDRESS, Place.Field.LOCATION, Place.Field.TYPES)
        val request = SearchByTextRequest.builder(query, placeFields).setMaxResultCount(10).build()

        return try {
            val response = placesClient.searchByText(request).await()
            response.places.map { place ->
                SavedPlaceEntity(
                    categoryId = -1,
                    placeId = place.id ?: "",
                    name = place.displayName ?: "이름 없음",
                    address = place.formattedAddress ?: "",
                    category = place.placeTypes?.firstOrNull() ?: "장소",
                    latitude = place.location?.latitude ?: 0.0,
                    longitude = place.location?.longitude ?: 0.0
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getAutocomplete(query: String, token: AutocompleteSessionToken?): List<AutocompletePrediction> {
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .setCountries("KR")
            .build()
        return try {
            placesClient.findAutocompletePredictions(request).await().autocompletePredictions
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPlacePhotos(placeId: String): List<Bitmap> = coroutineScope {
        try {
            val fields = listOf(Place.Field.PHOTO_METADATAS)
            val placeResponse = placesClient.fetchPlace(FetchPlaceRequest.builder(placeId, fields).build()).await()
            val metadatas = placeResponse.place.photoMetadatas?.take(3) ?: return@coroutineScope emptyList()

            metadatas.map { metadata ->
                async {
                    try {
                        val request = FetchPhotoRequest.builder(metadata).setMaxWidth(500).setMaxHeight(500).build()
                        placesClient.fetchPhoto(request).await().bitmap
                    } catch (e: Exception) { null }
                }
            }.awaitAll().filterNotNull()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun savePlaceToCategories(place: SavedPlaceEntity, categoryIds: List<Long>) {
        val userRef = userCol()

        categoryIds.forEach { localCatId ->
            // 1. 카테고리의 remoteId를 알아내야 함 (서버 저장을 위해)
            val category = favoriteDao.getCategoryById(localCatId) ?: return@forEach
            val categoryRemoteId = category.remoteId

            // 2. 장소용 remoteId 생성
            val newDocRef = userRef?.collection("saved_places")?.document()
            val placeRemoteId = newDocRef?.id ?: ""

            // 3. 로컬 저장
            val newPlace = place.copy(
                id = 0,
                categoryId = localCatId,
                remoteId = placeRemoteId
            )
            favoriteDao.insertPlace(newPlace)

            // 4. 서버 저장 (여기서 categoryRemoteId를 추가!)
            if (placeRemoteId.isNotEmpty() && userRef != null) {
                // Entity를 Map으로 변환하거나, 별도 DTO를 쓰는 게 좋지만
                // 간단하게 Entity 필드 + categoryRemoteId를 합쳐서 저장
                val firestoreData = hashMapOf(
                    "remoteId" to placeRemoteId,
                    "categoryRemoteId" to categoryRemoteId, // ★ 이 연결고리가 필수!
                    "placeId" to place.placeId,
                    "name" to place.name,
                    "address" to place.address,
                    "category" to place.category,
                    "latitude" to place.latitude,
                    "longitude" to place.longitude
                )
                newDocRef?.set(firestoreData)
            }
        }
    }

    suspend fun syncFromFirebase() {
        val userRef = userCol() ?: return // 로그인 안 했으면 중단

        try {
            // ==========================================
            // 1단계: 카테고리 먼저 가져오기
            // ==========================================
            val catSnapshot = userRef.collection("favorite_categories").get().await()

            // "서버의 remoteId"를 "로컬의 id(Long)"로 바꿔주는 맵 (장소 연결용)
            val remoteToLocalMap = mutableMapOf<String, Long>()

            catSnapshot.documents.forEach { doc ->
                val remoteId = doc.id // 문서 ID
                val name = doc.getString("name") ?: ""
                val color = doc.getLong("iconColor") ?: 0L

                // 1-1. 이미 로컬에 있는 카테고리인지 확인
                val existingCat = favoriteDao.getCategoryByRemoteId(remoteId)

                val localId: Long = if (existingCat != null) {
                    // 이미 있으면: 업데이트 (선택사항) 하거나 기존 ID 사용
                    // dao.update(existingCat.copy(name = name, iconColor = color)) // 필요 시 주석 해제
                    existingCat.id
                } else {
                    // 없으면: 새로 삽입
                    val newCat = FavoriteCategoryEntity(
                        id = 0, // 0으로 넣으면 자동생성
                        name = name,
                        iconColor = color,
                        remoteId = remoteId
                    )
                    favoriteDao.insertCategory(newCat) // insert 후 생성된 ID 반환하도록 DAO 수정하면 좋음
                    // insertCategory가 Long을 반환하지 않는다면, 다시 조회해야 함.
                    // (편의상 여기서는 insert가 Long(rowId)을 반환한다고 가정하거나, 다시 조회합니다)
                    favoriteDao.getCategoryByRemoteId(remoteId)?.id ?: 0L
                }

                // 맵에 저장해둠 ("cat_123" -> 1번)
                if (localId != 0L) {
                    remoteToLocalMap[remoteId] = localId
                }
            }

            // ==========================================
            // 2단계: 장소 가져오기
            // ==========================================
            val placeSnapshot = userRef.collection("saved_places").get().await()

            placeSnapshot.documents.forEach { doc ->
                val remoteId = doc.id
                val placeData = doc.toObject(SavedPlaceEntity::class.java) ?: return@forEach

                // Firestore에 저장된 "카테고리의 remoteId"를 가져와야 함
                // (주의: savePlaceToCategories 할 때 categoryRemoteId 필드를 추가로 저장했어야 함)
                // 만약 저장을 안 했다면, 로컬 복구 시 어떤 카테고리인지 알 수 없음.
                // 여기서는 Firestore에 'categoryRemoteId'라는 필드가 있다고 가정합니다.
                val parentCategoryRemoteId = doc.getString("categoryRemoteId")

                // 2-1. 이 장소가 속할 로컬 카테고리 ID 찾기
                val targetLocalCategoryId = remoteToLocalMap[parentCategoryRemoteId]

                if (targetLocalCategoryId != null) {
                    // 2-2. 이미 있는지 확인
                    val existingPlace = favoriteDao.getPlaceByRemoteId(remoteId)

                    if (existingPlace == null) {
                        // 2-3. 없으면 삽입 (로컬 카테고리 ID로 연결!)
                        val newPlace = placeData.copy(
                            id = 0,
                            categoryId = targetLocalCategoryId, // ★ 여기서 연결됨
                            remoteId = remoteId
                        )
                        favoriteDao.insertPlace(newPlace)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun addSearchHistory(query: String) {
        searchHistoryDao.insertHistory(SearchHistoryEntity(query = query))

    }

    suspend fun deleteSearchHistory(history: SearchHistoryEntity) {
        searchHistoryDao.deleteHistory(history)
    }
}