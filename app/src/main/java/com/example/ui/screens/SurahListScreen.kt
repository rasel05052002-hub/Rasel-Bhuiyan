package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RevelationType
import com.example.data.Surah
import com.example.ui.QuranViewModel
import com.example.ui.SurahFilter
import com.example.ui.components.AyahNumberBadge
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.components.toBengaliDigits
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun SurahListScreen(
  viewModel: QuranViewModel,
  onBackClick: () -> Unit,
  onSelectSurah: (Int) -> Unit
) {
  val surahs by viewModel.filteredSurahs.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val currentFilter by viewModel.surahFilter.collectAsState()

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = "১১৪টি সূরা",
        subtitle = "পবিত্র কুরআন শরীফ",
        onBackClick = onBackClick
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .testTag("surah_search_input"),
        placeholder = { Text("সূরা খুঁজুন (নাম, অর্থ বা নম্বর)...") },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "খুঁজুন",
            tint = IslamicGreenPrimary
          )
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.setSearchQuery("") }) {
              Icon(imageVector = Icons.Default.Clear, contentDescription = "মুছুন")
            }
          }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = IslamicGreenPrimary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        singleLine = true
      )

      // Filter Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = currentFilter == SurahFilter.ALL,
          onClick = { viewModel.setSurahFilter(SurahFilter.ALL) },
          label = { Text("সকল (${QuranViewModel::class.java.simpleName.let { "১১৪" }})") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = IslamicGreenPrimary,
            selectedLabelColor = Color.White
          )
        )
        FilterChip(
          selected = currentFilter == SurahFilter.MAKKI,
          onClick = { viewModel.setSurahFilter(SurahFilter.MAKKI) },
          label = { Text("মাক্কী (৮৬)") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = IslamicGreenPrimary,
            selectedLabelColor = Color.White
          )
        )
        FilterChip(
          selected = currentFilter == SurahFilter.MADANI,
          onClick = { viewModel.setSurahFilter(SurahFilter.MADANI) },
          label = { Text("মাদানী (২৮)") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = IslamicGreenPrimary,
            selectedLabelColor = Color.White
          )
        )
      }

      // Surah List
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(surahs, key = { it.number }) { surah ->
          SurahListItem(
            surah = surah,
            onClick = { onSelectSurah(surah.number) }
          )
        }
        item {
          Spacer(modifier = Modifier.height(20.dp))
        }
      }
    }
  }
}

@Composable
fun SurahListItem(
  surah: Surah,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("surah_item_${surah.number}")
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
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left info: Number & Bangla Info
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AyahNumberBadge(number = surah.number)

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = surah.nameBangla,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (surah.revelationType == RevelationType.MAKKI)
                IslamicGold.copy(alpha = 0.2f) else IslamicGreenPrimary.copy(alpha = 0.15f)
            ) {
              Text(
                text = surah.revelationType.banglaLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (surah.revelationType == RevelationType.MAKKI)
                    IslamicGoldDark else IslamicGreenPrimary,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Text(
            text = "${surah.nameEnglish} • অর্থ: ${surah.meaningBangla}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp
            )
          )

          Text(
            text = "আয়াত: ${toBengaliDigits(surah.numberOfAyahs)} • পারা: ${toBengaliDigits(surah.startPara)}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = IslamicGoldDark,
              fontSize = 11.sp
            )
          )
        }
      }

      // Right info: Arabic Calligraphy Name
      Text(
        text = surah.nameArabic,
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
