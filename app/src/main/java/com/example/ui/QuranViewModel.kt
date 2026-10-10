package com.example.ui

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AudioPlayerManager
import com.example.data.AudioState
import com.example.data.Ayah
import com.example.data.Bookmark
import com.example.data.DuaData
import com.example.data.DuaItem
import com.example.data.LastRead
import com.example.data.Para
import com.example.data.QuranMetadata
import com.example.data.QuranRepository
import com.example.data.RevelationType
import com.example.data.Surah
import com.example.data.TasbihPreset
import com.example.ui.theme.QuranReadingTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SurahFilter {
  ALL,
  MAKKI,
  MADANI
}

enum class ReadingMode {
  SURAH,
  PARA
}

class QuranViewModel(application: Application) : AndroidViewModel(application) {

  val repository = QuranRepository(application)
  val audioPlayer = AudioPlayerManager(application)

  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = application.getSystemService(Application.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    application.getSystemService(Application.VIBRATOR_SERVICE) as? Vibrator
  }

  // Preferences from repository
  val arabicFontSize: StateFlow<Float> = repository.arabicFontSize
  val banglaFontSize: StateFlow<Float> = repository.banglaFontSize
  val readingTheme: StateFlow<QuranReadingTheme> = repository.readingTheme
  val showTranslation: StateFlow<Boolean> = repository.showTranslation
  val bookmarks: StateFlow<List<Bookmark>> = repository.bookmarks
  val lastRead: StateFlow<LastRead?> = repository.lastRead

  // Audio State
  val audioState: StateFlow<AudioState> = audioPlayer.state

  // Surah list filtering
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _surahFilter = MutableStateFlow(SurahFilter.ALL)
  val surahFilter: StateFlow<SurahFilter> = _surahFilter.asStateFlow()

  val filteredSurahs: StateFlow<List<Surah>> =
    combine(_searchQuery, _surahFilter) { query, filter ->
      val base = when (filter) {
        SurahFilter.ALL -> QuranMetadata.allSurahs
        SurahFilter.MAKKI -> QuranMetadata.allSurahs.filter { it.revelationType == RevelationType.MAKKI }
        SurahFilter.MADANI -> QuranMetadata.allSurahs.filter { it.revelationType == RevelationType.MADANI }
      }
      if (query.isBlank()) {
        base
      } else {
        val q = query.trim().lowercase()
        base.filter { surah ->
          surah.nameBangla.lowercase().contains(q) ||
            surah.nameEnglish.lowercase().contains(q) ||
            surah.nameArabic.contains(q) ||
            surah.meaningBangla.lowercase().contains(q) ||
            surah.number.toString() == q
        }
      }
    }.stateIn(viewModelScope, SharingStarted.Lazily, QuranMetadata.allSurahs)

  val allParas: List<Para> = repository.getAllParas()

  // Reader Screen State
  private val _currentAyahs = MutableStateFlow<List<Ayah>>(emptyList())
  val currentAyahs: StateFlow<List<Ayah>> = _currentAyahs.asStateFlow()

  private val _isLoadingAyahs = MutableStateFlow(false)
  val isLoadingAyahs: StateFlow<Boolean> = _isLoadingAyahs.asStateFlow()

  private val _currentSurah = MutableStateFlow<Surah?>(null)
  val currentSurah: StateFlow<Surah?> = _currentSurah.asStateFlow()

  private val _currentPara = MutableStateFlow<Para?>(null)
  val currentPara: StateFlow<Para?> = _currentPara.asStateFlow()

  private val _readingMode = MutableStateFlow(ReadingMode.SURAH)
  val readingMode: StateFlow<ReadingMode> = _readingMode.asStateFlow()

  // Tasbih State
  private val _tasbihCount = MutableStateFlow(0)
  val tasbihCount: StateFlow<Int> = _tasbihCount.asStateFlow()

  private val _selectedTasbihPreset = MutableStateFlow(DuaData.tasbihPresets[0])
  val selectedTasbihPreset: StateFlow<TasbihPreset> = _selectedTasbihPreset.asStateFlow()

  private val _tasbihTarget = MutableStateFlow(33)
  val tasbihTarget: StateFlow<Int> = _tasbihTarget.asStateFlow()

  // Duas State
  private val _selectedDuaCategory = MutableStateFlow("সব")
  val selectedDuaCategory: StateFlow<String> = _selectedDuaCategory.asStateFlow()

  val filteredDuas: StateFlow<List<DuaItem>> =
    _selectedDuaCategory.combine(_selectedDuaCategory) { cat, _ ->
      if (cat == "সব") DuaData.allDuas else DuaData.allDuas.filter { it.category == cat }
    }.stateIn(viewModelScope, SharingStarted.Lazily, DuaData.allDuas)

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSurahFilter(filter: SurahFilter) {
    _surahFilter.value = filter
  }

  fun setDuaCategory(category: String) {
    _selectedDuaCategory.value = category
  }

  fun openSurah(surahNumber: Int, initialAyahNumber: Int = 1) {
    val surah = QuranMetadata.getSurah(surahNumber) ?: return
    _readingMode.value = ReadingMode.SURAH
    _currentSurah.value = surah
    _currentPara.value = null
    _isLoadingAyahs.value = true

    viewModelScope.launch {
      val ayahs = repository.getAyahsForSurah(surahNumber)
      _currentAyahs.value = ayahs
      _isLoadingAyahs.value = false
      repository.saveLastRead(surah.number, surah.nameBangla, initialAyahNumber, surah.startPara)
    }
  }

  fun openPara(paraNumber: Int) {
    val para = repository.getAllParas().find { it.number == paraNumber } ?: return
    _readingMode.value = ReadingMode.PARA
    _currentPara.value = para
    _currentSurah.value = null
    _isLoadingAyahs.value = true

    viewModelScope.launch {
      val ayahs = repository.getAyahsForPara(paraNumber)
      _currentAyahs.value = ayahs
      _isLoadingAyahs.value = false
      repository.saveLastRead(para.startSurahNumber, para.startSurahName, 1, para.number)
    }
  }

  fun updateLastRead(ayah: Ayah) {
    val surahName = _currentSurah.value?.nameBangla ?: "সূরা ${ayah.surahNumber}"
    repository.saveLastRead(ayah.surahNumber, surahName, ayah.numberInSurah, ayah.paraNumber)
  }

  fun toggleBookmark(ayah: Ayah) {
    val surahName = _currentSurah.value?.nameBangla ?: "সূরা ${ayah.surahNumber}"
    repository.toggleBookmark(ayah, surahName)
  }

  fun isBookmarked(ayah: Ayah): Boolean {
    return repository.isBookmarked(ayah.surahNumber, ayah.numberInSurah)
  }

  // Audio Playback
  fun playAyah(ayah: Ayah) {
    val surahName = _currentSurah.value?.nameBangla ?: "সূরা ${ayah.surahNumber}"
    audioPlayer.playAyah(ayah, surahName) {
      playNextAyah()
    }
  }

  fun playNextAyah() {
    val list = _currentAyahs.value
    val current = audioPlayer.state.value.currentAyah ?: return
    val currentIndex = list.indexOfFirst { it.globalNumber == current.globalNumber }
    if (currentIndex != -1 && currentIndex + 1 < list.size) {
      playAyah(list[currentIndex + 1])
    }
  }

  fun playPreviousAyah() {
    val list = _currentAyahs.value
    val current = audioPlayer.state.value.currentAyah ?: return
    val currentIndex = list.indexOfFirst { it.globalNumber == current.globalNumber }
    if (currentIndex > 0) {
      playAyah(list[currentIndex - 1])
    }
  }

  fun toggleAudioPlayPause() {
    audioPlayer.togglePlayPause()
  }

  fun stopAudio() {
    audioPlayer.stop()
  }

  fun toggleAudioRepeat() {
    audioPlayer.toggleRepeat()
  }

  // Settings
  fun setArabicFontSize(size: Float) = repository.setArabicFontSize(size)
  fun setBanglaFontSize(size: Float) = repository.setBanglaFontSize(size)
  fun setReadingTheme(theme: QuranReadingTheme) = repository.setReadingTheme(theme)
  fun setShowTranslation(show: Boolean) = repository.setShowTranslation(show)

  // Tasbih
  fun incrementTasbih() {
    _tasbihCount.value += 1
    triggerVibration(40)
  }

  fun resetTasbih() {
    _tasbihCount.value = 0
    triggerVibration(80)
  }

  fun selectTasbihPreset(preset: TasbihPreset) {
    _selectedTasbihPreset.value = preset
    _tasbihTarget.value = preset.defaultTarget
    _tasbihCount.value = 0
  }

  fun setTasbihTarget(target: Int) {
    _tasbihTarget.value = target
  }

  private fun triggerVibration(durationMs: Long) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(durationMs)
      }
    } catch (_: Exception) {}
  }

  override fun onCleared() {
    super.onCleared()
    audioPlayer.release()
  }
}
