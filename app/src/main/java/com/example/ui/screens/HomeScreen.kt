package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.LastRead
import com.example.ui.QuranViewModel
import com.example.ui.components.toBengaliDigits
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicGreenContainer
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun HomeScreen(
  viewModel: QuranViewModel,
  onNavigateToParas: () -> Unit,
  onNavigateToSurahs: () -> Unit,
  onNavigateToLastRead: () -> Unit,
  onNavigateToDuas: () -> Unit,
  onNavigateToTasbih: () -> Unit,
  onNavigateToBookmarks: () -> Unit,
  onNavigateToNamaz: () -> Unit,
  onOpenSurah: (Int) -> Unit
) {
  val lastRead by viewModel.lastRead.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // 1. Top Islamic Header Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(IslamicGreenDark, IslamicGreenPrimary)
            )
          )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Bismillah Calligraphy (Mandatory)
          Text(
            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            style = MaterialTheme.typography.headlineSmall.copy(
              color = IslamicGold,
              fontWeight = FontWeight.Bold,
              fontSize = 25.sp,
              lineHeight = 36.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 6.dp)
          )

          // Subtitle (Mandatory)
          Text(
            text = "পবিত্র আল-কুরআন শরীফ",
            style = MaterialTheme.typography.titleLarge.copy(
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 22.sp
            ),
            textAlign = TextAlign.Center
          )

          Text(
            text = "পূর্ণাঙ্গ ৩০ পারা ও ১১৪টি সূরা • বিশুদ্ধ বাংলা অনুবাদ",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 12.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
          )

          // Decorative Hero Banner
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, IslamicGold.copy(alpha = 0.8f)),
            color = IslamicGreenDark
          ) {
            Box {
              Image(
                painter = painterResource(id = R.drawable.img_quran_hero_1791649932306),
                contentDescription = "Islamic Mosque Arch",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(Color.Transparent, IslamicGreenDark.copy(alpha = 0.75f))
                    )
                  )
              )
              Column(
                modifier = Modifier
                  .align(Alignment.BottomStart)
                  .padding(12.dp)
              ) {
                Text(
                  text = "“নিশ্চয় এই কুরআন প্রদর্শন করে সেই পথ যা সরলতম”",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = IslamicGold,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                  )
                )
                Text(
                  text = "সূরা আল-ইসরা: ৯",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp
                  )
                )
              }
            }
          }
        }
      }
    }

    // 2. The 4 Primary Feature Buttons (Mandatory from prompt)
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp)
      ) {
        Text(
          text = "প্রধান সেবাসমূহ",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          ),
          modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Button 1: ৩০ পারা
          HomeMainButton(
            title = "৩০ পারা",
            subtitle = "পারা অনুযায়ী পড়ুন",
            icon = Icons.Default.AutoStories,
            iconBgColor = IslamicGreenPrimary,
            tag = "btn_30_para",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToParas
          )

          // Button 2: ১১৪টি সূরা
          HomeMainButton(
            title = "১১৪টি সূরা",
            subtitle = "সূরা তালিকা ও অর্থ",
            icon = Icons.Default.CollectionsBookmark,
            iconBgColor = IslamicGoldDark,
            tag = "btn_114_surah",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToSurahs
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Button 3: শেষ যেখানে পড়েছি
          HomeMainButton(
            title = "শেষ যেখানে পড়েছি",
            subtitle = lastRead?.let { "${it.surahNameBangla} : ${toBengaliDigits(it.ayahNumber)}" } ?: "পড়া শুরু করুন",
            icon = Icons.Default.History,
            iconBgColor = Color(0xFFC05621),
            tag = "btn_last_read",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToLastRead
          )

          // Button 4: ইসলামিক দোয়া
          HomeMainButton(
            title = "ইসলামিক দোয়া",
            subtitle = "কালিমা ও দৈনন্দিন আমল",
            icon = Icons.Default.Mosque,
            iconBgColor = Color(0xFF2C7A7B),
            tag = "btn_islamic_dua",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToDuas
          )
        }
      }
    }

    // 3. Continue Reading Card (If Last Read exists)
    if (lastRead != null) {
      item {
        lastRead?.let { lr ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 6.dp)
              .testTag("card_continue_reading")
              .clickable { onNavigateToLastRead() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.5f))
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
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(IslamicGreenPrimary),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(22.dp)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = "শেষ পঠিত স্থান",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = IslamicGoldDark,
                      fontWeight = FontWeight.Bold
                    )
                  )
                  Text(
                    text = "${lr.surahNameBangla} • আয়াত ${toBengaliDigits(lr.ayahNumber)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  )
                  Text(
                    text = "পারা ${toBengaliDigits(lr.paraNumber)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(20.dp),
                color = IslamicGreenPrimary
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "চালিয়ে যান",
                    style = MaterialTheme.typography.labelMedium.copy(
                      color = Color.White,
                      fontWeight = FontWeight.Bold
                    )
                  )
                }
              }
            }
          }
        }
      }
    }

    // 4. Daily Blessed Ayah of the Day
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .testTag("card_daily_ayah"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = IslamicGoldDark,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "আজকের নির্বাচিত আয়াত",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = IslamicGreenPrimary
                )
              )
            }
            Text(
              text = "সূরা আল-বাক্বারাহ: ২৫৫",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Arabic
          Text(
            text = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ",
            style = MaterialTheme.typography.bodyLarge.copy(
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              lineHeight = 32.sp
            ),
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(6.dp))

          // Bengali
          Text(
            text = "“আল্লাহ, তিনি ছাড়া কোনো সত্য উপাস্য নেই; তিনি চিরঞ্জীব, সর্বসত্তার ধারক। তন্দ্রা বা নিদ্রা তাঁকে স্পর্শ করে না।”",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 22.sp
            )
          )
        }
      }
    }

    // 5. Additional Islamic Utilities
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Text(
          text = "ইসলামিক আমল ও টুলস",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          ),
          modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Digital Tasbih Card
          QuickUtilityCard(
            title = "ডিজিটাল তাসবীহ",
            subtitle = "জিকির ও গণনা",
            icon = Icons.Default.TouchApp,
            tag = "card_quick_tasbih",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToTasbih
          )

          // Namaz Guide Card
          QuickUtilityCard(
            title = "নামাজের নিয়ম",
            subtitle = "রাকাত ও ফরজ-সুন্নত",
            icon = Icons.Default.Mosque,
            tag = "card_quick_namaz",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToNamaz
          )

          // Bookmarks Card
          QuickUtilityCard(
            title = "সংরক্ষিত আয়াত",
            subtitle = "পছন্দের বুকমার্ক",
            icon = Icons.Default.Bookmark,
            tag = "card_quick_bookmark",
            modifier = Modifier.weight(1f),
            onClick = onNavigateToBookmarks
          )
        }
      }
    }

    // 6. Popular Surahs Quick Jump
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Text(
          text = "নিয়মিত পঠিত সূরাসমূহ",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          ),
          modifier = Modifier.padding(bottom = 10.dp)
        )

        val popularSurahs = listOf(
          Pair(1, "সূরা আল-ফাতিহা"),
          Pair(36, "সূরা ইয়াসীন"),
          Pair(55, "সূরা আর-রহমান"),
          Pair(67, "সূরা আল-মুলক"),
          Pair(112, "সূরা আল-ইখলাস")
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          popularSurahs.take(3).forEach { (num, name) ->
            Surface(
              modifier = Modifier
                .weight(1f)
                .clickable { onOpenSurah(num) },
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = name,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  ),
                  textAlign = TextAlign.Center
                )
                Text(
                  text = "সূরা $num",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = IslamicGoldDark,
                    fontSize = 10.sp
                  )
                )
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun HomeMainButton(
  title: String,
  subtitle: String,
  icon: ImageVector,
  iconBgColor: Color,
  tag: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .testTag(tag)
      .height(115.dp)
      .clickable { onClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(iconBgColor),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = Color.White,
          modifier = Modifier.size(22.dp)
        )
      }

      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          ),
          maxLines = 1
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          ),
          maxLines = 1
        )
      }
    }
  }
}

@Composable
fun QuickUtilityCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  tag: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .testTag(tag)
      .clickable { onClick() },
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
        .padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(IslamicGreenPrimary.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = IslamicGreenPrimary,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        ),
        textAlign = TextAlign.Center,
        maxLines = 1
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 9.sp
        ),
        textAlign = TextAlign.Center,
        maxLines = 1
      )
    }
  }
}
