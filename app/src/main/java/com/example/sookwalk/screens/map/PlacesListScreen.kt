import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Restaurant(
    val name: String,
    val category: String,
    val distance: String,
    val address: String,
    val imageUrls: List<String>,
    val isFavorite: Boolean
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesBottomSheetScreen() {

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val dummyList = remember {
        listOf(
            Restaurant(
                name = "털보네떡꼬치",
                category = "한식",
                distance = "12km",
                address = "서울특별시 용산구 갈월동 93-60",
                imageUrls = listOf("", "", ""),
                isFavorite = true
            ),
            Restaurant(
                name = "땡초떡볶이",
                category = "떡볶이",
                distance = "12km",
                address = "서울특별시 용산구 청파동3가 24-28",
                imageUrls = listOf("", "", ""),
                isFavorite = false
            ),
            Restaurant(
                name = "조현우국밥 숙대점",
                category = "국밥",
                distance = "12km",
                address = "서울특별시 용산구 청파동3가 24-30",
                imageUrls = listOf("", "", ""),
                isFavorite = true
            )
        )
    }

    Scaffold { paddingValues ->
        Box(Modifier.fillMaxSize()) {

            // 여기는 지도 영역 (나중에 Google Map 넣으면 됨)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFEAEAEA))
            )

            ModalBottomSheet(
                onDismissRequest = {},
                sheetState = sheetState,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(width = 40.dp, height = 4.dp)
                            .background(
                                color = Color.LightGray,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            ) {
                PlacesList(dummyList)
            }
        }
    }
}
@Composable
fun PlacesList(list: List<Restaurant>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        items(list) { item ->
            PlacestItem(item)
            Divider(thickness = 8.dp, color = Color(0xFFF5F5F5))
        }
    }
}
@Composable
fun PlacestItem(item: Restaurant) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = item.category,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "favorite",
                tint = if (item.isFavorite) Color(0xFFFFD700) else Color.LightGray
            )
        }

        Text(
            text = "${item.distance} · ${item.address}",
            color = Color.Gray,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(100.dp, 90.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFD9D9D9))
                )
            }
        }
    }
}
