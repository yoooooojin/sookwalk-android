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
    private val dao: UserDao
) {

    // 현재 사용중인 유저 정보 가져오기
    val currentUser: Flow<UserEntity?> = dao.getCurrentUser()

    // 회원 가입
    suspend fun insertNewAccount(user: UserEntity) {
        dao.insert(user)
    }

    // 정보 수정 (마이페이지 등)
    suspend fun updateAccount(user: UserEntity) {
        dao.update(user)
    }

    // 회원 탈퇴
    suspend fun deleteAccount(user: UserEntity) {
        dao.delete(user)
    }


    // 이메일 중복 여부 확인
    suspend fun isLoginIdAvailable(loginId: String): Boolean {
        val result = Firebase.firestore.collection("users")
            .whereEqualTo("loginId", loginId.trim()) // 공백 제거
            .get()
            .await()

        return result.isEmpty // 비어있으면 (중복 X 아이디면) 사용 가능
    }
}