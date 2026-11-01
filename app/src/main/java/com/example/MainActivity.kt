package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.data.db.AppDatabase
import com.example.data.repository.KarobarRepository
import com.example.ui.screens.AdDetailScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.screens.CustomerChatScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PostAdScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.KarobarViewModel
import com.example.ui.viewmodel.KarobarViewModelFactory
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

  private lateinit var viewModel: KarobarViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
    val repository = KarobarRepository(
      database.adDao(),
      database.categoryDao(),
      database.appConfigDao(),
      database.chatDao()
    )
    val factory = KarobarViewModelFactory(repository)
    viewModel = ViewModelProvider(this, factory)[KarobarViewModel::class.java]

    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          MainContent(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun MainContent(viewModel: KarobarViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val isAdminAuthenticated by viewModel.isAdminAuthenticated.collectAsState()

  // Handle system back navigation
  BackHandler(enabled = currentScreen !is Screen.Home) {
    viewModel.navigateBack()
  }

  Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
    when (val screen = currentScreen) {
      is Screen.Home -> {
        HomeScreen(
          viewModel = viewModel,
          onAdClick = { adId -> viewModel.navigateTo(Screen.AdDetail(adId)) },
          onPostAdClick = { viewModel.navigateTo(Screen.PostAd) },
          onAdminClick = {
            if (isAdminAuthenticated) {
              viewModel.navigateTo(Screen.AdminDashboard)
            } else {
              viewModel.navigateTo(Screen.AdminLogin)
            }
          },
          onChatClick = { viewModel.navigateTo(Screen.CustomerChat) }
        )
      }
      is Screen.AdDetail -> {
        AdDetailScreen(
          adId = screen.adId,
          viewModel = viewModel,
          onBackClick = { viewModel.navigateBack() }
        )
      }
      is Screen.PostAd -> {
        PostAdScreen(
          viewModel = viewModel,
          onBackClick = { viewModel.navigateBack() }
        )
      }
      is Screen.AdminLogin -> {
        AdminLoginScreen(
          viewModel = viewModel,
          onBackClick = { viewModel.navigateBack() }
        )
      }
      is Screen.AdminDashboard -> {
        AdminDashboardScreen(
          viewModel = viewModel,
          onExitAdmin = { viewModel.logoutAdmin() }
        )
      }
      is Screen.CustomerChat -> {
        CustomerChatScreen(
          viewModel = viewModel,
          onBackClick = { viewModel.navigateBack() }
        )
      }
    }
  }
}
