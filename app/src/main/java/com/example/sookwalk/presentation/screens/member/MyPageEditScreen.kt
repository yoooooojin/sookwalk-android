package com.example.sookwalk.presentation.screens.member

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sookwalk.R
import com.example.sookwalk.presentation.components.TopBar
import com.google.common.io.Files.append


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun MyPageEditScreen(
    // viewModel: TodoViewModel,
    // navController: NavController,
    // backStackEntry: NavBackStackEntry
) {

    var nickname by remember { mutableStateOf("") } // 임시로 사용, 실제로는 ViewModel로 구현
    var isAvailableNickname by remember { mutableStateOf("") }

    var major by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val departments = listOf(
        "IP·콘텐츠전공",
        "IT공학전공",
        "K-POP산업경영전공",
        "게임콘텐츠디자인전공",
        "공공인재학전공",
        "과학저널리즘전공"
    )

    // 🔹 입력된 텍스트가 포함된 전공만 필터링
    val filtered = remember(major) {
        if (major.isBlank()) departments
        else departments.filter { it.contains(major, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopBar(
                screenName = "마이페이지",
                onMenuClick = { }
            )
        },

        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    onClick = { /* 페이지 이동 로직 */ },
                    shape = RoundedCornerShape(28),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("수정 완료", style = MaterialTheme.typography.displaySmall)
                }
            }
        }

    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(innerPadding)
                    .align(Alignment.TopCenter), // 중앙 가로, 세로는 맨 위
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 프로필, 사용자 정보
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .aspectRatio(1f)
                    ) {
                        // 사용자 이미지 받아오는 로직 필요
                        Image(
                            // R.drawable.tode는 임시 파일 (깃에 추가 X)
                            painter = painterResource(id = R.drawable.tode),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "수정 버튼",
                            modifier = Modifier
                                .align(Alignment.BottomEnd) // 박스 하단 끝
                                .offset(x = (-16).dp, y = (-16).dp) // 안쪽으로 이동하여 겹치게 함
                                .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                                .padding(12.dp),  // ← 배경 원 크기 증가
                            tint = Color.White
                        )
                    }
                }

                item {
                    // 닉네임 수정
                    Column(
                        modifier = Modifier.padding(8.dp),
                    ) {

                        Text(
                            // 사용자 정보 가져오는 로직 나중에 필요
                            "닉네임",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // 닉네임 입력
                        TextField(
                            value = nickname,
                            onValueChange = {
                                nickname = it
                            },
                            placeholder = { Text("변경할 닉네임을 입력하세요") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(start = 4.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF4F4F4),
                                focusedContainerColor = Color(0xFFF4F4F4),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                cursorColor = Color.DarkGray
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {

                            Text(
                                isAvailableNickname,
                                color = Color.Red,
                                style = MaterialTheme.typography.labelSmall
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Button(
                                onClick = { /* 중복 확인 여부 로직 */ },
                                shape = RoundedCornerShape(28),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text("중복 확인", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                }

                item {

                    Column(modifier = Modifier.padding(8.dp)) {
                        // 소속 학부 입력 문구
                        Row {
                            Text("소속 학부 ", style = MaterialTheme.typography.bodyLarge)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Column(modifier = Modifier.padding(8.dp)) {
                            TextField(
                                value = major,
                                onValueChange = {
                                    major = it
                                    expanded = true
                                },
                                placeholder = { Text("소속 학부를 입력하세요") },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                trailingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = "검색 아이콘")
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color(0xFFF4F4F4),
                                    focusedContainerColor = Color(0xFFF4F4F4),
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    cursorColor = Color.DarkGray
                                )
                            )

                            // 아래쪽 고정 Dropdown Box
                            if (expanded && filtered.isNotEmpty()) {

                                Column {
                                    filtered.forEach { dept ->
                                        val annotated = buildAnnotatedString {
                                            val startIndex = dept.indexOf(major, ignoreCase = true)
                                            if (startIndex >= 0) {
                                                val endIndex = startIndex + major.length
                                                append(dept.substring(0, startIndex))
                                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append(dept.substring(startIndex, endIndex))
                                                }
                                                append(dept.substring(endIndex))
                                            } else append(dept)
                                        }

                                        Text(
                                            text = annotated,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    major = dept
                                                    expanded = false
                                                }
                                                .padding(vertical = 8.dp, horizontal = 12.dp),
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}