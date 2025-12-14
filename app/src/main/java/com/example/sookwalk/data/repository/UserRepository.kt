package com.example.sookwalk.data.repository

import android.util.Log
import com.example.sookwalk.data.local.dao.UserDao
import com.example.sookwalk.data.local.entity.user.UserEntity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    val db = Firebase.firestore("sookwalk")

    // 현재 사용중인 유저 정보 가져오기
    val currentUser: Flow<UserEntity?> = userDao.getCurrentUser()

    // 닉네임 중복 여부 확인
    suspend fun isNicknameAvailable(nickname: String): Boolean {
        return try {
            Log.d("중복확인", "닉네임 중복 확인 시도: '$nickname'")
            val result = db.collection("nicknames")
                .document(nickname.trim()) // 문서 이름으로 비교
                .get()
                .await()
            Log.d("중복확인", "닉네임 중복 확인 시도2: '$nickname'")
            !result.exists() // 비어있으면 (중복 X 아이디면) 사용 가능
        }catch(e: Exception){
            Log.e("nickname", "닉네임 중복 확인 실패", e)
            throw e
        }
    }

    // 닉네임, 학과 변경
    suspend fun updateNicknameAndMajor(newNickname: String, newMajor: String) {
        userDao.updateNicknameAndMajor(newNickname, newMajor)
    }


    suspend fun updateProfileImageUrl(newProfileImageUrl: String) {
        userDao.updateProfileImageUrl(newProfileImageUrl)
    }

}
