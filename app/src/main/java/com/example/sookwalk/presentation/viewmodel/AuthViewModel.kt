package com.example.sookwalk.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.local.entity.user.UserEntity
import com.example.sookwalk.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository): ViewModel(){
    val currentUser: StateFlow<UserEntity?>
            = repository.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // 5초간 구독이 없으면 중단
        initialValue = null )// 초기값 설정

    // 회원 가입
    fun insertNewAccount(user: UserEntity){
        viewModelScope.launch{
            repository.insertNewAccount(user)
        }
    }

    // 회원 삭제
    fun deleteAccount(user: UserEntity){
        viewModelScope.launch{
            repository.deleteAccount(user)
        }
    }
}