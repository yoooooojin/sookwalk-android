package com.example.sookwalk.presentation.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sookwalk.ui.theme.Black
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun EmailAuthScreen( onSignedIn: () -> Unit ) {
    val auth = remember { FirebaseAuth.getInstance() }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    Column (Modifier.padding(16.dp)) {
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            scope.launch {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { message = "가입 완료: ${it.user?.email}" }
                    .addOnFailureListener {
                        message = "가입 실패: ${it.message}"
                    }
            }
        }) { Text("회원가입") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            scope.launch {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { onSignedIn() }
                    .addOnFailureListener { message = "로그인 실패: ${it.message}" }
            }
        }) { Text("로그인") }
        message?.let { Text(it, color = Black) }
    }
}