package com.example.sookwalk.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SignUpProfileScreen(
    // viewModel: TodoViewModel,
    // navController: NavController,
    // backStackEntry: NavBackStackEntry
) {

    var nickname by remember { mutableStateOf("") }

    // 🔹 랜덤 닉네임 placeholder 생성
    val randomPlaceholder = remember {
        val adjectives = listOf("산책하는", "춤추는", "웃는", "노래하는", "잠자는")
        val nouns = listOf("눈송이", "눈결이", "꽃송이", "눈덩이", "눈꽃송이", "튜리", "로로")
        val number = (1000..9999).random()
        "${adjectives.random()} ${nouns.random()}$number"
    }

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
            TopAppBar(
                title = { Text("") },

                navigationIcon = {
                    IconButton(onClick = {
                        // 뒤로가기 로직
                        // navController?.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "뒤로가기"
                        )
                    }
                },

                // 색상 변경
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background, // 배경색 변경
                )
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
                    Text("다음", style = MaterialTheme.typography.displaySmall)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {

                Column(modifier = Modifier.padding(8.dp)) {

                    // 닉네임 입력 문구
                    Row {
                        Text("닉네임 ", style = MaterialTheme.typography.bodyLarge)
                    }

                    // 닉네임 입력 TextField
                    TextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        modifier = Modifier
                            .fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF4F4F4),
                            focusedContainerColor = Color(0xFFF4F4F4),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.DarkGray
                        ),
                        placeholder = { Text(randomPlaceholder) }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // 버튼을 오른쪽 정렬
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                // 닉네임을 입력받지 않았다면 placeholder 값을 사용
                                val finalNickname =
                                    if (nickname.isBlank()) randomPlaceholder else nickname
                                /* 닉네임 중복 확인 로직 */
                            },
                            shape = RoundedCornerShape(28),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            )
                        ) {
                            Text("중복 확인", style = MaterialTheme.typography.displaySmall)
                        }
                    }
                }

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
