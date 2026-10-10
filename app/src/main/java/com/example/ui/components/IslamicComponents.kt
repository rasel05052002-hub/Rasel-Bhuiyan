package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AudioState
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicTopAppBar(
  title: String,
  subtitle: String? = null,
  onBackClick: (() -> Unit)? = null,
  actions: @Composable () -> Unit = {}
) {
  TopAppBar(
    title = {
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        if (!subtitle.isNullOrBlank()) {
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
              color = IslamicGold.copy(alpha = 0.95f),
              fontSize = 12.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    },
    navigationIcon = {
      if (onBackClick != null) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.testTag("nav_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "ফিরে যান",
            tint = Color.White
          )
        }
      }
    },
    actions = { actions() },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = IslamicGreenPrimary,
      titleContentColor = Color.White,
      navigationIconContentColor = Color.White,
      actionIconContentColor = Color.White
    )
  )
}

@Composable
fun AyahNumberBadge(
  number: Int,
  modifier: Modifier = Modifier,
  badgeColor: Color = IslamicGreenPrimary
) {
  Box(
    modifier = modifier
      .size(36.dp)
      .clip(RoundedCornerShape(10.dp))
      .background(badgeColor.copy(alpha = 0.12f))
      .border(1.5.dp, IslamicGold, RoundedCornerShape(10.dp)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = toBengaliDigits(number),
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
        color = badgeColor,
        fontSize = 13.sp
      ),
      textAlign = TextAlign.Center
    )
  }
}

@Composable
fun AudioMiniPlayerBar(
  audioState: AudioState,
  onPlayPauseToggle: () -> Unit,
  onNextClick: () -> Unit,
  onPrevClick: () -> Unit,
  onRepeatToggle: () -> Unit,
  onCloseClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (audioState.currentAyah == null) return

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp),
    shape = RoundedCornerShape(16.dp),
    color = IslamicGreenDark,
    shadowElevation = 8.dp,
    border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Info
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(IslamicGold),
          contentAlignment = Alignment.Center
        ) {
          if (audioState.isLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(20.dp),
              color = IslamicGreenDark,
              strokeWidth = 2.dp
            )
          } else {
            Icon(
              imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = null,
              tint = IslamicGreenDark,
              modifier = Modifier.size(22.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Text(
            text = "${audioState.surahName} : আয়াত ${toBengaliDigits(audioState.currentAyah.numberInSurah)}",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "ক্বারী মিশারী রশিদ আল-আফাসী",
            style = MaterialTheme.typography.bodySmall.copy(
              color = IslamicGold.copy(alpha = 0.9f),
              fontSize = 11.sp
            ),
            maxLines = 1
          )
        }
      }

      // Controls
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onRepeatToggle,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = if (audioState.isRepeating) Icons.Default.RepeatOne else Icons.Default.Repeat,
            contentDescription = "লুপ",
            tint = if (audioState.isRepeating) IslamicGold else Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
          )
        }

        IconButton(
          onClick = onPrevClick,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.SkipPrevious,
            contentDescription = "পূর্ববর্তী আয়াত",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        IconButton(
          onClick = onPlayPauseToggle,
          modifier = Modifier.size(38.dp)
        ) {
          Icon(
            imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = "প্লে / পজ",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }

        IconButton(
          onClick = onNextClick,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = "পরবর্তী আয়াত",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        IconButton(
          onClick = onCloseClick,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "বন্ধ করুন",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

fun toBengaliDigits(number: Int): String {
  val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
  val s = number.toString()
  val sb = StringBuilder()
  for (ch in s) {
    if (ch in '0'..'9') {
      sb.append(bengaliDigits[ch - '0'])
    } else {
      sb.append(ch)
    }
  }
  return sb.toString()
}
