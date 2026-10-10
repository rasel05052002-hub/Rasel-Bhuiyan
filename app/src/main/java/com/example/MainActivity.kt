package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.QuranViewModel
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.DuaScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NamazGuideScreen
import com.example.ui.screens.ParaListScreen
import com.example.ui.screens.QuranReaderScreen
import com.example.ui.screens.SurahListScreen
import com.example.ui.screens.TasbihScreen
import com.example.ui.theme.QuranAppTheme

enum class Screen {
  HOME,
  SURAH_LIST,
  PARA_LIST,
  READER,
  DUAS,
  TASBIH,
  BOOKMARKS,
  NAMAZ
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: QuranViewModel = viewModel()
      val readingTheme by viewModel.readingTheme.collectAsState()

      QuranAppTheme(readingTheme = readingTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
          QuranAppNavigation(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun QuranAppNavigation(viewModel: QuranViewModel) {
  var currentScreen by remember { mutableStateOf(Screen.HOME) }
  var screenHistory by remember { mutableStateOf(listOf(Screen.HOME)) }

  fun navigateTo(screen: Screen) {
    screenHistory = screenHistory + screen
    currentScreen = screen
  }

  fun navigateBack() {
    if (screenHistory.size > 1) {
      screenHistory = screenHistory.dropLast(1)
      currentScreen = screenHistory.last()
    } else {
      currentScreen = Screen.HOME
    }
  }

  BackHandler(enabled = currentScreen != Screen.HOME) {
    navigateBack()
  }

  AnimatedContent(
    targetState = currentScreen,
    transitionSpec = { fadeIn() togetherWith fadeOut() },
    label = "screen_transition"
  ) { screen ->
    when (screen) {
      Screen.HOME -> HomeScreen(
        viewModel = viewModel,
        onNavigateToParas = { navigateTo(Screen.PARA_LIST) },
        onNavigateToSurahs = { navigateTo(Screen.SURAH_LIST) },
        onNavigateToLastRead = {
          val last = viewModel.lastRead.value
          if (last != null) {
            viewModel.openSurah(last.surahNumber, last.ayahNumber)
          } else {
            viewModel.openSurah(1, 1)
          }
          navigateTo(Screen.READER)
        },
        onNavigateToDuas = { navigateTo(Screen.DUAS) },
        onNavigateToTasbih = { navigateTo(Screen.TASBIH) },
        onNavigateToBookmarks = { navigateTo(Screen.BOOKMARKS) },
        onNavigateToNamaz = { navigateTo(Screen.NAMAZ) },
        onOpenSurah = { surahNumber ->
          viewModel.openSurah(surahNumber, 1)
          navigateTo(Screen.READER)
        }
      )

      Screen.SURAH_LIST -> SurahListScreen(
        viewModel = viewModel,
        onBackClick = { navigateBack() },
        onSelectSurah = { surahNumber ->
          viewModel.openSurah(surahNumber, 1)
          navigateTo(Screen.READER)
        }
      )

      Screen.PARA_LIST -> ParaListScreen(
        viewModel = viewModel,
        onBackClick = { navigateBack() },
        onSelectPara = { paraNumber ->
          viewModel.openPara(paraNumber)
          navigateTo(Screen.READER)
        }
      )

      Screen.READER -> QuranReaderScreen(
        viewModel = viewModel,
        onBackClick = { navigateBack() }
      )

      Screen.DUAS -> DuaScreen(
        viewModel = viewModel,
        onBackClick = { navigateBack() }
      )

      Screen.TASBIH -> TasbihScreen(
        viewModel = viewModel,
        onBackClick = { navigateBack() }
      )

      Screen.BOOKMARKS -> BookmarksScreen(
        viewModel = viewModel,
        onBackClick = { navigateBack() },
        onOpenAyah = { surahNumber, ayahNumber ->
          viewModel.openSurah(surahNumber, ayahNumber)
          navigateTo(Screen.READER)
        }
      )

      Screen.NAMAZ -> NamazGuideScreen(
        onBackClick = { navigateBack() }
      )
    }
  }
}
