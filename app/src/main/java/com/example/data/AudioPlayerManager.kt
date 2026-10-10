package com.example.data

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioState(
  val isPlaying: Boolean = false,
  val isLoading: Boolean = false,
  val currentAyah: Ayah? = null,
  val surahName: String = "",
  val isRepeating: Boolean = false,
  val errorMessage: String? = null
)

class AudioPlayerManager(private val context: Context) {

  private var mediaPlayer: MediaPlayer? = null
  private var onNextAyahCallback: (() -> Unit)? = null

  private val _state = MutableStateFlow(AudioState())
  val state: StateFlow<AudioState> = _state.asStateFlow()

  fun playAyah(ayah: Ayah, surahName: String, onNext: (() -> Unit)? = null) {
    onNextAyahCallback = onNext
    val audioUrl = if (ayah.audioUrl.isNotEmpty()) {
      ayah.audioUrl
    } else {
      "https://cdn.islamic.network/quran/audio/128/ar.alafasy/${ayah.globalNumber}.mp3"
    }

    _state.value = _state.value.copy(
      isLoading = true,
      isPlaying = false,
      currentAyah = ayah,
      surahName = surahName,
      errorMessage = null
    )

    try {
      mediaPlayer?.stop()
      mediaPlayer?.release()
      mediaPlayer = null

      mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
          AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        )
        setDataSource(audioUrl)
        setOnPreparedListener { mp ->
          _state.value = _state.value.copy(isLoading = false, isPlaying = true)
          mp.start()
        }
        setOnCompletionListener {
          if (_state.value.isRepeating) {
            it.seekTo(0)
            it.start()
          } else {
            _state.value = _state.value.copy(isPlaying = false)
            onNextAyahCallback?.invoke()
          }
        }
        setOnErrorListener { _, what, extra ->
          Log.e("AudioPlayer", "Error: $what, extra: $extra")
          _state.value = _state.value.copy(
            isLoading = false,
            isPlaying = false,
            errorMessage = "অডিও প্লে করা সম্ভব হয়নি। ইন্টারনেট সংযোগ যাচাই করুন।"
          )
          true
        }
        prepareAsync()
      }
    } catch (e: Exception) {
      Log.e("AudioPlayer", "Exception in playAyah", e)
      _state.value = _state.value.copy(
        isLoading = false,
        isPlaying = false,
        errorMessage = "ত্রুটি: ${e.localizedMessage}"
      )
    }
  }

  fun togglePlayPause() {
    mediaPlayer?.let { mp ->
      if (mp.isPlaying) {
        mp.pause()
        _state.value = _state.value.copy(isPlaying = false)
      } else {
        mp.start()
        _state.value = _state.value.copy(isPlaying = true)
      }
    }
  }

  fun toggleRepeat() {
    val newRepeat = !_state.value.isRepeating
    _state.value = _state.value.copy(isRepeating = newRepeat)
  }

  fun stop() {
    try {
      mediaPlayer?.stop()
      mediaPlayer?.release()
    } catch (_: Exception) {}
    mediaPlayer = null
    _state.value = AudioState()
  }

  fun release() {
    stop()
  }
}
