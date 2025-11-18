package com.example.sookwalk.presentation.screens.member

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sookwalk.R
import com.example.sookwalk.presentation.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun MyPageEditScreen() {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // 갤러리에서 콘텐츠를 가져오는 런처
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
            showBottomSheet = false
        }
    )

    // 권한을 요청하는 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                // 권한이 허용되면 갤러리 런처를 실행
                galleryLauncher.launch("image/*")
            } else {
                // 권한이 거부되면 바텀 시트를 닫음
                showBottomSheet = false
            }
        }
    )

    // 안드로이드 버전에 따라 필요한 권한을 정의
    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var nickname by remember { mutableStateOf("") }
    var isAvailableNickname by remember { mutableStateOf("") }
    var major by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val departments = listOf(
        "IP·콘텐츠전공", "IT공학전공", "K-POP산업경영전공", "게임콘텐츠디자인전공", "공공인재학전공", "과학저널리즘전공"
    )

    val filtered = remember(major) {
        if (major.isBlank()) departments else departments.filter { it.contains(major, ignoreCase = true) }
    }

    Scaffold(
        topBar = { TopBar(screenName = "마이페이지", onMenuClick = { }) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(0.9f).padding(innerPadding).align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(0.7f).aspectRatio(1f)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(selectedImageUri ?: R.drawable.test).crossfade(true).build(),
                            contentDescription = "Profile Image",
                            modifier = Modifier.fillMaxSize().padding(12.dp).clip(CircleShape).clickable { showBottomSheet = true },
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.default_profile_image)
                        )

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "수정 버튼",
                            modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-16).dp, y = (-16).dp).clip(CircleShape).clickable { showBottomSheet = true }.background(MaterialTheme.colorScheme.tertiary).padding(12.dp),
                            tint = Color.White
                        )
                    }
                }
                // ... (닉네임, 학과 수정 등 나머지 코드는 동일)
                 item { // 닉네임 수정
                    Column(modifier = Modifier.padding(8.dp),) {
                        Text("닉네임", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                        Spacer(modifier = Modifier.width(4.dp))
                        TextField(
                            value = nickname, onValueChange = {nickname = it},
                            placeholder = { Text("변경할 닉네임을 입력하세요") },
                            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally).padding(start = 4.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF4F4F4), focusedContainerColor = Color(0xFFF4F4F4), unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, cursorColor = Color.DarkGray)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Text(isAvailableNickname, color = Color.Red, style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.width(4.dp))
                            Button(
                                onClick = { /* 중복 확인 여부 로직 */ },
                                shape = RoundedCornerShape(28),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary, contentColor = Color.White),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text("중복 확인", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                item {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row { Text("소속 학부 ", style = MaterialTheme.typography.bodyLarge) }
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(modifier = Modifier.padding(8.dp)) {
                            TextField(
                                value = major, onValueChange = { major = it; expanded = true },
                                placeholder = { Text("소속 학부를 입력하세요") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = { Icon(Icons.Default.Search, contentDescription = "검색 아이콘") },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF4F4F4), focusedContainerColor = Color(0xFFF4F4F4), unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, cursorColor = Color.DarkGray)
                            )
                            if (expanded && filtered.isNotEmpty()) {
                                Column {
                                    filtered.forEach { dept ->
                                        val annotated = buildAnnotatedString {
                                            val startIndex = dept.indexOf(major, ignoreCase = true)
                                            if (startIndex >= 0) {
                                                val endIndex = startIndex + major.length
                                                append(dept.substring(0, startIndex))
                                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(dept.substring(startIndex, endIndex)) }
                                                append(dept.substring(endIndex))
                                            } else append(dept)
                                        }
                                        Text(
                                            text = annotated,
                                            modifier = Modifier.fillMaxWidth().clickable { major = dept; expanded = false }.padding(vertical = 8.dp, horizontal = 12.dp),
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = { /* 이전 페이지로 넘어가는 로직 */ },
                            shape = RoundedCornerShape(28),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary, contentColor = Color.White),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("수정 완료", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("프로필 사진 설정", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 16.dp))
                    HorizontalDivider(modifier = Modifier.padding(bottom = 4.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().clickable {
                            // 권한 상태 확인
                            when (ContextCompat.checkSelfPermission(context, permissionToRequest)) {
                                PackageManager.PERMISSION_GRANTED -> {
                                    // 권한이 이미 있으면 갤러리 실행
                                    galleryLauncher.launch("image/*")
                                }
                                else -> {
                                    // 권한이 없으면 권한 요청 팝업 띄우기
                                    permissionLauncher.launch(permissionToRequest)
                                }
                            }
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("앨범에서 사진 선택", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 12.dp))
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().clickable {
                            selectedImageUri = null
                            showBottomSheet = false
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("프로필 사진 삭제", style = MaterialTheme.typography.bodyLarge, color = Color.Red, modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }
        }
    }
}