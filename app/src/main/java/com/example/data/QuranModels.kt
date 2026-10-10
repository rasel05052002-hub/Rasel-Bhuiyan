package com.example.data

enum class RevelationType(val banglaLabel: String) {
  MAKKI("মাক্কী"),
  MADANI("মাদানী")
}

data class Surah(
  val number: Int,
  val nameArabic: String,
  val nameBangla: String,
  val nameEnglish: String,
  val meaningBangla: String,
  val revelationType: RevelationType,
  val numberOfAyahs: Int,
  val startPara: Int,
  val endPara: Int
)

data class Para(
  val number: Int,
  val nameArabic: String,
  val nameBangla: String,
  val startSurahNumber: Int,
  val startSurahName: String,
  val startAyah: Int,
  val endSurahNumber: Int,
  val endSurahName: String,
  val endAyah: Int,
  val totalAyahs: Int
)

data class Ayah(
  val numberInSurah: Int,
  val globalNumber: Int,
  val surahNumber: Int,
  val textArabic: String,
  val textBangla: String,
  val audioUrl: String = "",
  val paraNumber: Int = 1,
  val isSajdah: Boolean = false
)

data class Bookmark(
  val id: String,
  val surahNumber: Int,
  val surahNameBangla: String,
  val ayahNumber: Int,
  val textArabic: String,
  val textBangla: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class LastRead(
  val surahNumber: Int,
  val surahNameBangla: String,
  val ayahNumber: Int,
  val paraNumber: Int,
  val timestamp: Long = System.currentTimeMillis()
)

data class DuaItem(
  val id: Int,
  val category: String,
  val titleBangla: String,
  val textArabic: String,
  val pronunciationBangla: String,
  val meaningBangla: String,
  val reference: String,
  val virtue: String = ""
)

data class TasbihPreset(
  val id: Int,
  val arabicText: String,
  val banglaText: String,
  val meaning: String,
  val defaultTarget: Int = 33
)
