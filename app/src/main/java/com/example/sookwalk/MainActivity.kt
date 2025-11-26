package com.example.sookwalk

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.sookwalk.navigation.NavGraph
import com.example.sookwalk.presentation.viewmodel.ThemeViewModel
import com.example.sookwalk.ui.theme.SookWalkTheme
import com.example.sookwalk.utils.notification.NotificationHelper
import com.google.android.gms.maps.MapsInitializer
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapsInitializer.initialize(applicationContext)

        enableEdgeToEdge()
        setContent {
            val themeVM: ThemeViewModel = hiltViewModel()
            val isDark by themeVM.isDark.collectAsStateWithLifecycle()
            val navController = rememberNavController()

            // Define a variable to hold the Places API key.
            val apiKey = BuildConfig.PLACES_API_KEY

            // Log an error if apiKey is not set.
            if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
                Log.e("Places test", "No api key")
                finish()

            }

            // Initialize the SDK
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)

            // Create a new PlacesClient instance
            val placesClient = Places.createClient(this)


            val viewModel: PlacesViewModel = hiltViewModel()
            var showSheet by remember { mutableStateOf(true) }

            NotificationHelper.createNotificationChannel(this)
            askNotificationPermission()

            // 알림 클릭 시 실행할 네비게이션 정보
            val navigationFromNotification = intent?.getStringExtra("navigation") ?: null

            SookWalkTheme (
                darkTheme = isDark,
                dynamicColor = false
            ) {
                NavGraph(navController)
            }
        }
    }

    private fun askNotificationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    8765
                )
            }
        }
    }
}
