package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Para
import com.example.ui.QuranViewModel
import com.example.ui.components.AyahNumberBadge
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.components.toBengaliDigits
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun ParaListScreen(
  viewModel: QuranViewModel,
  onBackClick: () -> Unit,
  onSelectPara: (Int) -> Unit
) {
  val paras = viewModel.allParas

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = "৩০ পারা",
        subtitle = "পবিত্র কুরআন শরীফ",
        onBackClick = onBackClick
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(paras, key = { it.number }) { para ->
        ParaListItem(
          para = para,
          onClick = { onSelectPara(para.number) }
        )
      }
      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}

@Composable
fun ParaListItem(
  para: Para,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("para_item_${para.number}")
      .clickable { onClick() },
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(1.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AyahNumberBadge(number = para.number)

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = "পারা ${toBengaliDigits(para.number)} : ${para.nameBangla}",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          )

          Text(
            text = "শুরু: ${para.startSurahName} (${toBengaliDigits(para.startAyah)}) • শেষ: ${para.endSurahName} (${toBengaliDigits(para.endAyah)})",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp
            )
          )

          Text(
            text = "মোট আয়াত: ${toBengaliDigits(para.totalAyahs)} টি",
            style = MaterialTheme.typography.labelSmall.copy(
              color = IslamicGoldDark,
              fontSize = 11.sp
            )
          )
        }
      }

      Text(
        text = para.nameArabic,
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = IslamicGreenPrimary,
          fontSize = 22.sp
        ),
        textAlign = TextAlign.Right
      )
    }
  }
}
