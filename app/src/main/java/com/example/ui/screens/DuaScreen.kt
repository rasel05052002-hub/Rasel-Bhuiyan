package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DuaItem
import com.example.ui.QuranViewModel
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun DuaScreen(
  viewModel: QuranViewModel,
  onBackClick: () -> Unit
) {
  val context = LocalContext.current
  val duas by viewModel.filteredDuas.collectAsState()
  val selectedCategory by viewModel.selectedDuaCategory.collectAsState()

  val categories = listOf("সব", "কালিমা", "নামাজের দোয়া", "দৈনন্দিন আমল")

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = "ইসলামিক দোয়া ও আমল",
        subtitle = "কালিমা, নামাজের দোয়া ও জিকির",
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
      // Category filter chips
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(categories) { cat ->
          FilterChip(
            selected = selectedCategory == cat,
            onClick = { viewModel.setDuaCategory(cat) },
            label = { Text(cat) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = IslamicGreenPrimary,
              selectedLabelColor = Color.White
            )
          )
        }
      }

      // Duas list
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(duas, key = { it.id }) { dua ->
          DuaItemCard(
            dua = dua,
            onCopy = {
              val clip = ClipData.newPlainText(
                dua.titleBangla,
                "${dua.titleBangla}\n\n${dua.textArabic}\n\nউচ্চারণ: ${dua.pronunciationBangla}\n\nঅর্থ: ${dua.meaningBangla}\n[সূত্র: ${dua.reference}]"
              )
              (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
              Toast.makeText(context, "দোয়া কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
            },
            onShare = {
              val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                  Intent.EXTRA_TEXT,
                  "★ ${dua.titleBangla} ★\n\n${dua.textArabic}\n\nউচ্চারণ: ${dua.pronunciationBangla}\n\nঅর্থ: ${dua.meaningBangla}\n\nসূত্র: ${dua.reference}\nফজিলত: ${dua.virtue}"
                )
                type = "text/plain"
              }
              context.startActivity(Intent.createChooser(sendIntent, "দোয়া শেয়ার করুন"))
            }
          )
        }
        item {
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }
}

@Composable
fun DuaItemCard(
  dua: DuaItem,
  onCopy: () -> Unit,
  onShare: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("dua_item_${dua.id}"),
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
      // Header: Category badge & Title & Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = IslamicGreenPrimary.copy(alpha = 0.12f)
          ) {
            Text(
              text = dua.category,
              style = MaterialTheme.typography.labelSmall.copy(
                color = IslamicGreenPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = dua.titleBangla,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          )
        }

        Row {
          IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "কপি",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
          IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "শেয়ার",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Arabic Text
      Text(
        text = dua.textArabic,
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 22.sp,
          lineHeight = 36.sp
        ),
        textAlign = TextAlign.Right,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Pronunciation
      Text(
        text = "উচ্চারণ: ${dua.pronunciationBangla}",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = IslamicGreenPrimary,
          fontWeight = FontWeight.Medium,
          fontSize = 14.sp
        )
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Meaning
      Text(
        text = "অর্থ: ${dua.meaningBangla}",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 14.sp
        )
      )

      if (dua.virtue.isNotEmpty() || dua.reference.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          if (dua.virtue.isNotEmpty()) {
            Text(
              text = "ফজিলত: ${dua.virtue}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = IslamicGoldDark,
                fontSize = 11.sp
              ),
              modifier = Modifier.weight(1f)
            )
          }
          if (dua.reference.isNotEmpty()) {
            Text(
              text = "সূত্র: ${dua.reference}",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            )
          }
        }
      }
    }
  }
}
