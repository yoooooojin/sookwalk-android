package com.example.sookwalk.data.repository

import com.example.sookwalk.data.remote.dto.RankDto
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.collections.mapIndexed

class RankingRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    // firestore에 바로 접근 가능
    private fun deptCollection() =
        db.collection("rankings").document("current")
            .collection("departments")

    private fun collegeCollection() =
        db.collection("rankings").document("current")
            .collection("colleges")

    fun observeDeptRanking(): Flow<List<RankDto>> =
        callbackFlow {
            val reg = deptCollection()
                .orderBy("walkCount", Query.Direction.DESCENDING)
                .addSnapshotListener { snap, e ->
                    if (e != null) {
                        close(e)
                        return@addSnapshotListener
                    }
                    if (snap == null) return@addSnapshotListener

                    val list = toRankList(snap.documents)
                    trySend(list).isSuccess
                }

            awaitClose { reg.remove() }
        }

    fun observeCollegeRanking(): Flow<List<RankDto>> =
        callbackFlow {
            val reg = collegeCollection()
                .orderBy("walkCount", Query.Direction.DESCENDING)
                .addSnapshotListener { snap, e ->
                    if (e != null) {
                        close(e)
                        return@addSnapshotListener
                    }
                    if (snap == null) return@addSnapshotListener

                    val list = toRankList(snap.documents)
                    trySend(list).isSuccess
                }

            awaitClose { reg.remove() }
        }

    private fun toRankList(docs: List<DocumentSnapshot>): List<RankDto> {
        var lastWalkCount: Long? = null
        var lastRank = 0

        return docs.mapIndexed { index, doc ->
            val walkCount = (doc.get("walkCount") as? Number)?.toLong() ?: 0L

            val rank = if (lastWalkCount == null || walkCount != lastWalkCount) {
                lastRank = index + 1
                lastWalkCount = walkCount
                lastRank
            } else {
                lastRank
            }

            RankDto(
                id = doc.id,
                name = doc.getString("name") ?: "",
                walkCount = walkCount,
                rank = rank
            )
        }
    }
}