package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun NamazGuideScreen(
  onBackClick: () -> Unit
) {
  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = "নামাজের নিয়ম ও রাকাত",
        subtitle = "৫ ওয়াক্ত নামাজের বিবরণ",
        onBackClick = onBackClick
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // 1. ৫ ওয়াক্ত নামাজের রাকাত সংখ্যা
      item {
        Text(
          text = "৫ ওয়াক্ত নামাজের রাকাত তালিকা",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = IslamicGreenPrimary
          )
        )
      }

      val namazList = listOf(
        NamazInfo("ফজর", "২ রাকাত সুন্নত, ২ রাকাত ফরজ", "মোট ৪ রাকাত"),
        NamazInfo("যোহর", "৪ রাকাত সুন্নত, ৪ রাকাত ফরজ, ২ রাকাত সুন্নত, ২ রাকাত নফল", "মোট ১২ রাকাত"),
        NamazInfo("আসর", "৪ রাকাত সুন্নাতে গায়রে মুয়াক্কাদা, ৪ রাকাত ফরজ", "মোট ৮ রাকাত"),
        NamazInfo("মাগরিব", "৩ রাকাত ফরজ, ২ রাকাত সুন্নত, ২ রাকাত নফল", "মোট ৭ রাকাত"),
        NamazInfo("এশা", "৪ রাকাত সুন্নত, ৪ রাকাত ফরজ, ২ রাকাত সুন্নত, ২ রাকাত নফল, ৩ রাকাত বিতর, ২ রাকাত নফল", "মোট ১৭ রাকাত"),
        NamazInfo("তাহাজ্জুদ ও নফল", "কমপক্ষে ২ রাকাত করে ৮ বা ১২ রাকাত আদায় করা উত্তম", "অতিরিক্ত নফল")
      )

      namazList.forEach { info ->
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
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
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = info.name,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                )
                Text(
                  text = info.details,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                  )
                )
              }
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = IslamicGreenPrimary.copy(alpha = 0.12f)
              ) {
                Text(
                  text = info.total,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = IslamicGreenPrimary,
                    fontWeight = FontWeight.Bold
                  ),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }

      // 2. নামাজের ১৩টি ফরজ
      item {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "নামাজের ১৩টি ফরজ (আহকাম ও আরকান)",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = IslamicGreenPrimary
          )
        )
      }

      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "নামাজের বাইরের ৭ ফরজ (আহকাম):",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = IslamicGoldDark
              )
            )
            val ahkam = listOf(
              "১. শরীর পাক হওয়া",
              "২. কাপড় পাক হওয়া",
              "৩. নামাজের জায়গা পাক হওয়া",
              "৪. সতর বা গোপন অঙ্গ ঢাকা",
              "৫. কিবলামুখী হওয়া",
              "৬. ওয়াক্তমত নামাজ আদায় করা",
              "৭. নামাজের নিয়ত করা"
            )
            ahkam.forEach {
              Text(
                text = it,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.padding(vertical = 2.dp)
              )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Text(
              text = "নামাজের ভেতরের ৬ ফরজ (আরকান):",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = IslamicGoldDark
              )
            )
            val arkan = listOf(
              "১. তাকবীরে তাহরীমা (আল্লাহু আকবার) বলা",
              "২. দাঁড়িয়ে নামাজ পড়া (কিয়াম)",
              "৩. কেরাত পাঠ করা (কুরআন তিলাওয়াত)",
              "৪. রুকু করা",
              "৫. দুই সিজদা করা",
              "৬. শেষ বৈঠক করা"
            )
            arkan.forEach {
              Text(
                text = it,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.padding(vertical = 2.dp)
              )
            }
          }
        }
      }

      // 3. ওযুর ৪ ফরজ
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "ওযুর ৪টি ফরজ:",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = IslamicGoldDark
              )
            )
            val wudu = listOf(
              "১. সম্পূর্ণ মুখমণ্ডল একবার ধৌত করা",
              "২. উভয় হাত কনুইসহ ধৌত করা",
              "৩. মাথার চার ভাগের এক ভাগ মাসেহ করা",
              "৪. উভয় পা টাকনুসহ ধৌত করা"
            )
            wudu.forEach {
              Text(
                text = it,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.padding(vertical = 2.dp)
              )
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

data class NamazInfo(
  val name: String,
  val details: String,
  val total: String
)
