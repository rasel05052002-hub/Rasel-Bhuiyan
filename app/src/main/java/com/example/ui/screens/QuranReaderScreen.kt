package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Ayah
import com.example.ui.QuranViewModel
import com.example.ui.ReadingMode
import com.example.ui.components.AudioMiniPlayerBar
import com.example.ui.components.AyahNumberBadge
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.components.toBengaliDigits
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicGreenContainer
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary
import com.example.ui.theme.QuranReadingTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
  viewModel: QuranViewModel,
  onBackClick: () -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  val currentAyahs by viewModel.currentAyahs.collectAsState()
  val isLoading by viewModel.isLoadingAyahs.collectAsState()
  val currentSurah by viewModel.currentSurah.collectAsState()
  val currentPara by viewModel.currentPara.collectAsState()
  val readingMode by viewModel.readingMode.collectAsState()
  val audioState by viewModel.audioState.collectAsState()
  val bookmarks by viewModel.bookmarks.collectAsState()

  val arabicFontSize by viewModel.arabicFontSize.collectAsState()
  val banglaFontSize by viewModel.banglaFontSize.collectAsState()
  val readingTheme by viewModel.readingTheme.collectAsState()
  val showTranslation by viewModel.showTranslation.collectAsState()

  var showSettingsSheet by remember { mutableStateOf(false) }

  BackHandler {
    viewModel.stopAudio()
    onBackClick()
  }

  // Scroll to active playing Ayah when it changes
  LaunchedEffect(audioState.currentAyah) {
    audioState.currentAyah?.let { active ->
      val index = currentAyahs.indexOfFirst { it.globalNumber == active.globalNumber }
      if (index >= 0) {
        coroutineScope.launch {
          listState.animateScrollToItem(index + 1) // +1 for header
        }
      }
    }
  }

  val title = if (readingMode == ReadingMode.SURAH) {
    currentSurah?.let { "সূরা ${it.nameBangla}" } ?: "কুরআন পাঠ"
  } else {
    currentPara?.let { "পারা ${toBengaliDigits(it.number)} : ${it.nameBangla}" } ?: "কুরআন পাঠ"
  }

  val subtitle = if (readingMode == ReadingMode.SURAH) {
    currentSurah?.let { "${it.revelationType.banglaLabel} • ${toBengaliDigits(it.numberOfAyahs)} আয়াত" }
  } else {
    currentPara?.let { "মোট আয়াত: ${toBengaliDigits(it.totalAyahs)}" }
  }

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = title,
        subtitle = subtitle,
        onBackClick = {
          viewModel.stopAudio()
          onBackClick()
        },
        actions = {
          IconButton(
            onClick = { showSettingsSheet = true },
            modifier = Modifier.testTag("reader_settings_button")
          ) {
            Icon(
              imageVector = Icons.Default.FormatSize,
              contentDescription = "ফন্ট ও থিম সেটিংস",
              tint = Color.White
            )
          }
        }
      )
    },
    bottomBar = {
      AnimatedVisibility(visible = audioState.currentAyah != null) {
        AudioMiniPlayerBar(
          audioState = audioState,
          onPlayPauseToggle = { viewModel.toggleAudioPlayPause() },
          onNextClick = { viewModel.playNextAyah() },
          onPrevClick = { viewModel.playPreviousAyah() },
          onRepeatToggle = { viewModel.toggleAudioRepeat() },
          onCloseClick = { viewModel.stopAudio() }
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      if (isLoading) {
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          CircularProgressIndicator(
            color = IslamicGreenPrimary,
            strokeWidth = 3.dp
          )
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "পবিত্র আয়াতসমূহ লোড হচ্ছে...",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = MaterialTheme.colorScheme.onBackground
            )
          )
        }
      } else {
        LazyColumn(
          state = listState,
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // 1. Surah Header Card
          item {
            Spacer(modifier = Modifier.height(6.dp))
            SurahHeaderBanner(
              currentSurah = currentSurah,
              currentPara = currentPara,
              readingMode = readingMode
            )
          }

          // 2. Ayah List
          itemsIndexed(currentAyahs, key = { _, ayah -> "${ayah.surahNumber}_${ayah.numberInSurah}" }) { index, ayah ->
            val isPlaying = audioState.currentAyah?.globalNumber == ayah.globalNumber && audioState.isPlaying
            val isBookmarked = bookmarks.any { it.id == "${ayah.surahNumber}:${ayah.numberInSurah}" }

            AyahItemCard(
              ayah = ayah,
              isPlaying = isPlaying,
              isBookmarked = isBookmarked,
              arabicFontSize = arabicFontSize,
              banglaFontSize = banglaFontSize,
              showTranslation = showTranslation,
              onPlayAudio = {
                viewModel.playAyah(ayah)
                viewModel.updateLastRead(ayah)
              },
              onToggleBookmark = { viewModel.toggleBookmark(ayah) },
              onCopyAyah = {
                val clip = ClipData.newPlainText(
                  "Quran Ayah",
                  "${ayah.textArabic}\n\n${ayah.textBangla}\n[সূরা: ${currentSurah?.nameBangla ?: ayah.surahNumber}, আয়াত: ${ayah.numberInSurah}]"
                )
                (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                Toast.makeText(context, "আয়াত কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
              },
              onShareAyah = {
                val sendIntent = Intent().apply {
                  action = Intent.ACTION_SEND
                  putExtra(
                    Intent.EXTRA_TEXT,
                    "${ayah.textArabic}\n\n${ayah.textBangla}\n\n[পবিত্র আল-কুরআন: সূরা ${currentSurah?.nameBangla ?: ayah.surahNumber}, আয়াত ${ayah.numberInSurah}]"
                  )
                  type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "আয়াত শেয়ার করুন"))
              }
            )
          }

          item {
            Spacer(modifier = Modifier.height(60.dp))
          }
        }
      }
    }
  }

  // Reader Settings Modal Bottom Sheet
  if (showSettingsSheet) {
    ModalBottomSheet(
      onDismissRequest = { showSettingsSheet = false },
      sheetState = rememberModalBottomSheetState(),
      containerColor = MaterialTheme.colorScheme.surface,
      dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
      ReaderSettingsSheetContent(
        arabicFontSize = arabicFontSize,
        banglaFontSize = banglaFontSize,
        readingTheme = readingTheme,
        showTranslation = showTranslation,
        onArabicFontSizeChange = { viewModel.setArabicFontSize(it) },
        onBanglaFontSizeChange = { viewModel.setBanglaFontSize(it) },
        onReadingThemeChange = { viewModel.setReadingTheme(it) },
        onShowTranslationChange = { viewModel.setShowTranslation(it) }
      )
    }
  }
}

@Composable
fun SurahHeaderBanner(
  currentSurah: com.example.data.Surah?,
  currentPara: com.example.data.Para?,
  readingMode: ReadingMode
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, IslamicGold.copy(alpha = 0.8f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (readingMode == ReadingMode.SURAH && currentSurah != null) {
        Text(
          text = currentSurah.nameArabic,
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = IslamicGreenPrimary,
            fontSize = 28.sp
          ),
          textAlign = TextAlign.Center
        )
        Text(
          text = "সূরা ${currentSurah.nameBangla} (${currentSurah.nameEnglish})",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          ),
          textAlign = TextAlign.Center
        )
        Text(
          text = "অর্থ: ${currentSurah.meaningBangla} • অবতীর্ণ: ${currentSurah.revelationType.banglaLabel} • আয়াত: ${toBengaliDigits(currentSurah.numberOfAyahs)}",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          ),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 2.dp)
        )
      } else if (currentPara != null) {
        Text(
          text = currentPara.nameArabic,
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = IslamicGreenPrimary,
            fontSize = 28.sp
          ),
          textAlign = TextAlign.Center
        )
        Text(
          text = "পারা ${toBengaliDigits(currentPara.number)} : ${currentPara.nameBangla}",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          ),
          textAlign = TextAlign.Center
        )
      }

      // Bismillah banner (All except Surah 9 At-Tawbah)
      if (currentSurah?.number != 9) {
        HorizontalDivider(
          modifier = Modifier.padding(vertical = 10.dp),
          color = MaterialTheme.colorScheme.outline
        )
        Text(
          text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
          style = MaterialTheme.typography.titleLarge.copy(
            color = IslamicGoldDark,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
          ),
          textAlign = TextAlign.Center
        )
        Text(
          text = "শুরু করছি আল্লাহর নামে যিনি পরম করুণাময়, অতি দয়ালু।",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          ),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 2.dp)
        )
      }
    }
  }
}

@Composable
fun AyahItemCard(
  ayah: Ayah,
  isPlaying: Boolean,
  isBookmarked: Boolean,
  arabicFontSize: Float,
  banglaFontSize: Float,
  showTranslation: Boolean,
  onPlayAudio: () -> Unit,
  onToggleBookmark: () -> Unit,
  onCopyAyah: () -> Unit,
  onShareAyah: () -> Unit
) {
  val cardBorder = if (isPlaying) {
    androidx.compose.foundation.BorderStroke(2.dp, IslamicGold)
  } else {
    androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
  }

  val cardBg = if (isPlaying) {
    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
  } else {
    MaterialTheme.colorScheme.surface
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("ayah_card_${ayah.numberInSurah}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    border = cardBorder
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Top Bar: Ayah Number & Action Icons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          AyahNumberBadge(number = ayah.numberInSurah)
          if (isPlaying) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = IslamicGreenPrimary
            ) {
              Text(
                text = "তিলাওয়াত চলছে",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Play Audio Button
          IconButton(
            onClick = onPlayAudio,
            modifier = Modifier.size(34.dp).testTag("play_ayah_${ayah.numberInSurah}")
          ) {
            Icon(
              imageVector = if (isPlaying) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
              contentDescription = "অডিও শুনুন",
              tint = if (isPlaying) IslamicGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }

          // Bookmark Button
          IconButton(
            onClick = onToggleBookmark,
            modifier = Modifier.size(34.dp).testTag("bookmark_ayah_${ayah.numberInSurah}")
          ) {
            Icon(
              imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
              contentDescription = "বুকমার্ক",
              tint = if (isBookmarked) IslamicGoldDark else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }

          // Copy Button
          IconButton(
            onClick = onCopyAyah,
            modifier = Modifier.size(34.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "কপি করুন",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }

          // Share Button
          IconButton(
            onClick = onShareAyah,
            modifier = Modifier.size(34.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "শেয়ার করুন",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Arabic Text (Right-to-Left, Large font)
      Text(
        text = ayah.textArabic,
        style = MaterialTheme.typography.bodyLarge.copy(
          fontSize = arabicFontSize.sp,
          lineHeight = (arabicFontSize * 1.8f).sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        ),
        textAlign = TextAlign.Right,
        modifier = Modifier.fillMaxWidth()
      )

      // Bengali Translation
      if (showTranslation) {
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = ayah.textBangla,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = banglaFontSize.sp,
            lineHeight = (banglaFontSize * 1.55f).sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

@Composable
fun ReaderSettingsSheetContent(
  arabicFontSize: Float,
  banglaFontSize: Float,
  readingTheme: QuranReadingTheme,
  showTranslation: Boolean,
  onArabicFontSizeChange: (Float) -> Unit,
  onBanglaFontSizeChange: (Float) -> Unit,
  onReadingThemeChange: (QuranReadingTheme) -> Unit,
  onShowTranslationChange: (Boolean) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 12.dp)
  ) {
    Text(
      text = "কুরআন পাঠ সেটিংস",
      style = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      ),
      modifier = Modifier.padding(bottom = 16.dp)
    )

    // Reading Theme Selector
    Text(
      text = "পঠন থিম (কালার মোড):",
      style = MaterialTheme.typography.titleSmall.copy(
        fontWeight = FontWeight.Bold,
        color = IslamicGreenPrimary
      )
    )
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      ThemeOptionCard(
        label = "সবুজ-সাদা",
        isSelected = readingTheme == QuranReadingTheme.LIGHT,
        bgColor = Color.White,
        textColor = Color(0xFF1E2923),
        onClick = { onReadingThemeChange(QuranReadingTheme.LIGHT) },
        modifier = Modifier.weight(1f)
      )
      ThemeOptionCard(
        label = "সেপিয়া কাগজ",
        isSelected = readingTheme == QuranReadingTheme.SEPIA,
        bgColor = Color(0xFFFBF8F1),
        textColor = Color(0xFF2A2318),
        onClick = { onReadingThemeChange(QuranReadingTheme.SEPIA) },
        modifier = Modifier.weight(1f)
      )
      ThemeOptionCard(
        label = "রাতের মোড",
        isSelected = readingTheme == QuranReadingTheme.DARK,
        bgColor = Color(0xFF101915),
        textColor = Color(0xFFEFF5F1),
        onClick = { onReadingThemeChange(QuranReadingTheme.DARK) },
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Arabic Font Size
    Text(
      text = "আরবি ফন্ট সাইজ: ${arabicFontSize.toInt()} pt",
      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
    )
    Slider(
      value = arabicFontSize,
      onValueChange = onArabicFontSizeChange,
      valueRange = 22f..40f,
      steps = 9,
      colors = SliderDefaults.colors(
        thumbColor = IslamicGreenPrimary,
        activeTrackColor = IslamicGreenPrimary
      )
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Bangla Font Size
    Text(
      text = "বাংলা অনুবাদ ফন্ট সাইজ: ${banglaFontSize.toInt()} pt",
      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
    )
    Slider(
      value = banglaFontSize,
      onValueChange = onBanglaFontSizeChange,
      valueRange = 14f..26f,
      steps = 6,
      colors = SliderDefaults.colors(
        thumbColor = IslamicGreenPrimary,
        activeTrackColor = IslamicGreenPrimary
      )
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Show Translation Toggle
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "বাংলা অনুবাদ প্রদর্শন",
          style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "প্রতিটি আয়াতের নিচে বাংলা অনুবাদ দেখাবে",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }
      Switch(
        checked = showTranslation,
        onCheckedChange = onShowTranslationChange,
        colors = SwitchDefaults.colors(
          checkedThumbColor = Color.White,
          checkedTrackColor = IslamicGreenPrimary
        )
      )
    }

    Spacer(modifier = Modifier.height(30.dp))
  }
}

@Composable
fun ThemeOptionCard(
  label: String,
  isSelected: Boolean,
  bgColor: Color,
  textColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .height(46.dp)
      .clickable { onClick() },
    shape = RoundedCornerShape(10.dp),
    color = bgColor,
    border = androidx.compose.foundation.BorderStroke(
      width = if (isSelected) 2.dp else 1.dp,
      color = if (isSelected) IslamicGreenPrimary else Color.Gray.copy(alpha = 0.3f)
    )
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
          color = textColor
        ),
        textAlign = TextAlign.Center
      )
    }
  }
}
