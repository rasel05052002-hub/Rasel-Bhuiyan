package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DuaData
import com.example.ui.QuranViewModel
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.components.toBengaliDigits
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun TasbihScreen(
  viewModel: QuranViewModel,
  onBackClick: () -> Unit
) {
  val count by viewModel.tasbihCount.collectAsState()
  val target by viewModel.tasbihTarget.collectAsState()
  val selectedPreset by viewModel.selectedTasbihPreset.collectAsState()

  var showResetDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = "ডিজিটাল তাসবীহ",
        subtitle = "দৈনন্দিন জিকির ও গণনা",
        onBackClick = onBackClick,
        actions = {
          IconButton(
            onClick = { showResetDialog = true },
            modifier = Modifier.testTag("tasbih_reset_button")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "রিসেট",
              tint = Color.White
            )
          }
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // 1. Preset Dhikr Chips
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "জিকির নির্বাচন করুন:",
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          ),
          modifier = Modifier.padding(bottom = 6.dp)
        )
        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(DuaData.tasbihPresets, key = { it.id }) { preset ->
            FilterChip(
              selected = selectedPreset.id == preset.id,
              onClick = { viewModel.selectTasbihPreset(preset) },
              label = { Text(preset.banglaText) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = IslamicGreenPrimary,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }

      // 2. Active Dhikr Display Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.6f))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = selectedPreset.arabicText,
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              color = IslamicGreenPrimary,
              fontSize = 28.sp
            ),
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = selectedPreset.banglaText,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            ),
            textAlign = TextAlign.Center
          )
          Text(
            text = selectedPreset.meaning,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
          )
        }
      }

      // 3. Giant Tasbih Tap Button
      Box(
        modifier = Modifier
          .size(240.dp)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              listOf(IslamicGreenPrimary, IslamicGreenDark)
            )
          )
          .border(4.dp, IslamicGold, CircleShape)
          .shadow(12.dp, CircleShape)
          .clickable { viewModel.incrementTasbih() }
          .testTag("tasbih_counter_tap"),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = toBengaliDigits(count),
            style = MaterialTheme.typography.displayLarge.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White,
              fontSize = 58.sp
            )
          )
          Text(
            text = "লক্ষ্য: ${toBengaliDigits(target)} বার",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = IslamicGold,
              fontWeight = FontWeight.Medium
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "স্পর্শ করুন",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color.White.copy(alpha = 0.7f),
              fontSize = 11.sp
            )
          )
        }
      }

      // 4. Target Selector Buttons
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "গণনার লক্ষ্যমাত্রা:",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          ),
          modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(33, 99, 100, 1000).forEach { t ->
            Surface(
              modifier = Modifier
                .clickable { viewModel.setTasbihTarget(t) },
              shape = RoundedCornerShape(8.dp),
              color = if (target == t) IslamicGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (target == t) IslamicGold else MaterialTheme.colorScheme.outline
              )
            ) {
              Text(
                text = "${toBengaliDigits(t)} বার",
                style = MaterialTheme.typography.labelMedium.copy(
                  color = if (target == t) Color.White else MaterialTheme.colorScheme.onSurface,
                  fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
    }
  }

  if (showResetDialog) {
    AlertDialog(
      onDismissRequest = { showResetDialog = false },
      title = { Text("কাউন্টার রিসেট") },
      text = { Text("আপনি কি তাসবীহ গণনা পুনরায় শূন্য (০) থেকে শুরু করতে চান?") },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.resetTasbih()
            showResetDialog = false
          }
        ) {
          Text("হ্যাঁ, রিসেট করুন", color = Color.Red)
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetDialog = false }) {
          Text("বাতিল")
        }
      }
    )
  }
}
