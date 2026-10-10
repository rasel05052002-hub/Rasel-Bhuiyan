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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.data.Ayah
import com.example.ui.QuranViewModel
import com.example.ui.components.AyahNumberBadge
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.components.toBengaliDigits
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun BookmarksScreen(
  viewModel: QuranViewModel,
  onBackClick: () -> Unit,
  onOpenAyah: (Int, Int) -> Unit
) {
  val bookmarks by viewModel.bookmarks.collectAsState()

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = "বুকমার্ককৃত আয়াতসমূহ",
        subtitle = "পবিত্র কুরআন শরীফ",
        onBackClick = onBackClick
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      if (bookmarks.isEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(IslamicGreenPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Bookmark,
              contentDescription = null,
              tint = IslamicGreenPrimary,
              modifier = Modifier.size(32.dp)
            )
          }
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "এখনো কোনো আয়াত বুকমার্ক করা হয়নি",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "কুরআন পড়ার সময় পছন্দের আয়াতের বুকমার্ক আইকনে চাপ দিয়ে এখানে সংরক্ষণ করুন।",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(bookmarks, key = { it.id }) { bm ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("bookmark_item_${bm.id}")
                .clickable { onOpenAyah(bm.surahNumber, bm.ayahNumber) },
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
              ),
              elevation = CardDefaults.cardElevation(1.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    AyahNumberBadge(number = bm.ayahNumber)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                      text = "${bm.surahNameBangla} • আয়াত ${toBengaliDigits(bm.ayahNumber)}",
                      style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = IslamicGreenPrimary
                      )
                    )
                  }

                  IconButton(
                    onClick = {
                      val ayah = Ayah(
                        numberInSurah = bm.ayahNumber,
                        globalNumber = 0,
                        surahNumber = bm.surahNumber,
                        textArabic = bm.textArabic,
                        textBangla = bm.textBangla
                      )
                      viewModel.toggleBookmark(ayah)
                    },
                    modifier = Modifier.size(32.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.BookmarkRemove,
                      contentDescription = "বুকমার্ক মুছুন",
                      tint = Color.Red.copy(alpha = 0.7f),
                      modifier = Modifier.size(20.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = bm.textArabic,
                  style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    lineHeight = 30.sp,
                    color = MaterialTheme.colorScheme.onSurface
                  ),
                  textAlign = TextAlign.Right,
                  modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = bm.textBangla,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                  )
                )
              }
            }
          }
          item {
            Spacer(modifier = Modifier.height(20.dp))
          }
        }
      }
    }
  }
}
