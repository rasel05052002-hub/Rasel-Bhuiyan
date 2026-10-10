package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.QuranReadingTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class QuranRepository(private val context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)

  private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .build()

  private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
  val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

  private val _lastRead = MutableStateFlow<LastRead?>(null)
  val lastRead: StateFlow<LastRead?> = _lastRead.asStateFlow()

  private val _arabicFontSize = MutableStateFlow(28f)
  val arabicFontSize: StateFlow<Float> = _arabicFontSize.asStateFlow()

  private val _banglaFontSize = MutableStateFlow(16f)
  val banglaFontSize: StateFlow<Float> = _banglaFontSize.asStateFlow()

  private val _readingTheme = MutableStateFlow(QuranReadingTheme.LIGHT)
  val readingTheme: StateFlow<QuranReadingTheme> = _readingTheme.asStateFlow()

  private val _showTranslation = MutableStateFlow(true)
  val showTranslation: StateFlow<Boolean> = _showTranslation.asStateFlow()

  init {
    loadSettings()
    loadBookmarks()
    loadLastRead()
  }

  private fun loadSettings() {
    _arabicFontSize.value = prefs.getFloat("arabic_font_size", 28f)
    _banglaFontSize.value = prefs.getFloat("bangla_font_size", 16f)
    val themeStr = prefs.getString("reading_theme", QuranReadingTheme.LIGHT.name) ?: QuranReadingTheme.LIGHT.name
    _readingTheme.value = try {
      QuranReadingTheme.valueOf(themeStr)
    } catch (_: Exception) {
      QuranReadingTheme.LIGHT
    }
    _showTranslation.value = prefs.getBoolean("show_translation", true)
  }

  fun setArabicFontSize(size: Float) {
    _arabicFontSize.value = size
    prefs.edit().putFloat("arabic_font_size", size).apply()
  }

  fun setBanglaFontSize(size: Float) {
    _banglaFontSize.value = size
    prefs.edit().putFloat("bangla_font_size", size).apply()
  }

  fun setReadingTheme(theme: QuranReadingTheme) {
    _readingTheme.value = theme
    prefs.edit().putString("reading_theme", theme.name).apply()
  }

  fun setShowTranslation(show: Boolean) {
    _showTranslation.value = show
    prefs.edit().putBoolean("show_translation", show).apply()
  }

  private fun loadBookmarks() {
    val jsonString = prefs.getString("bookmarks_json", null) ?: return
    try {
      val jsonArray = org.json.JSONArray(jsonString)
      val list = mutableListOf<Bookmark>()
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          Bookmark(
            id = obj.getString("id"),
            surahNumber = obj.getInt("surahNumber"),
            surahNameBangla = obj.getString("surahNameBangla"),
            ayahNumber = obj.getInt("ayahNumber"),
            textArabic = obj.getString("textArabic"),
            textBangla = obj.getString("textBangla"),
            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
          )
        )
      }
      _bookmarks.value = list
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun toggleBookmark(ayah: Ayah, surahName: String) {
    val id = "${ayah.surahNumber}:${ayah.numberInSurah}"
    val current = _bookmarks.value.toMutableList()
    val existingIndex = current.indexOfFirst { it.id == id }
    if (existingIndex >= 0) {
      current.removeAt(existingIndex)
    } else {
      current.add(
        0,
        Bookmark(
          id = id,
          surahNumber = ayah.surahNumber,
          surahNameBangla = surahName,
          ayahNumber = ayah.numberInSurah,
          textArabic = ayah.textArabic,
          textBangla = ayah.textBangla
        )
      )
    }
    _bookmarks.value = current
    saveBookmarks(current)
  }

  fun isBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
    val id = "$surahNumber:$ayahNumber"
    return _bookmarks.value.any { it.id == id }
  }

  private fun saveBookmarks(list: List<Bookmark>) {
    try {
      val jsonArray = org.json.JSONArray()
      for (item in list) {
        val obj = JSONObject().apply {
          put("id", item.id)
          put("surahNumber", item.surahNumber)
          put("surahNameBangla", item.surahNameBangla)
          put("ayahNumber", item.ayahNumber)
          put("textArabic", item.textArabic)
          put("textBangla", item.textBangla)
          put("timestamp", item.timestamp)
        }
        jsonArray.put(obj)
      }
      prefs.edit().putString("bookmarks_json", jsonArray.toString()).apply()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun loadLastRead() {
    val surahNum = prefs.getInt("last_surah_num", -1)
    if (surahNum != -1) {
      _lastRead.value = LastRead(
        surahNumber = surahNum,
        surahNameBangla = prefs.getString("last_surah_name", "আল-ফাতিহা") ?: "আল-ফাতিহা",
        ayahNumber = prefs.getInt("last_ayah_num", 1),
        paraNumber = prefs.getInt("last_para_num", 1),
        timestamp = prefs.getLong("last_read_time", System.currentTimeMillis())
      )
    }
  }

  fun saveLastRead(surahNumber: Int, surahName: String, ayahNumber: Int, paraNumber: Int) {
    val last = LastRead(surahNumber, surahName, ayahNumber, paraNumber, System.currentTimeMillis())
    _lastRead.value = last
    prefs.edit()
      .putInt("last_surah_num", surahNumber)
      .putString("last_surah_name", surahName)
      .putInt("last_ayah_num", ayahNumber)
      .putInt("last_para_num", paraNumber)
      .putLong("last_read_time", last.timestamp)
      .apply()
  }

  fun getAllSurahs(): List<Surah> = QuranMetadata.allSurahs

  fun getAllParas(): List<Para> = ParaMetadata.allParas

  suspend fun getAyahsForSurah(surahNumber: Int): List<Ayah> = withContext(Dispatchers.IO) {
    // 1. Check local file cache first
    val cacheFile = File(context.cacheDir, "surah_$surahNumber.json")
    if (cacheFile.exists()) {
      try {
        val parsed = parseSurahJson(cacheFile.readText(), surahNumber)
        if (parsed.isNotEmpty()) return@withContext parsed
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }

    // 2. Check bundled authentic dataset
    val bundled = BundledQuranData.getAyahsForSurah(surahNumber)
    if (bundled != null && bundled.isNotEmpty()) {
      // If surah has more ayahs than bundled (e.g. Al-Baqarah), try fetch full surah in background or return bundled
      val surahMeta = QuranMetadata.getSurah(surahNumber)
      if (surahMeta != null && bundled.size >= surahMeta.numberOfAyahs) {
        return@withContext bundled
      }
    }

    // 3. Try fetch from authentic Quran API (Uthmani Arabic + Bengali translation)
    try {
      val url = "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,bn.bengali"
      val request = Request.Builder().url(url).build()
      val response = okHttpClient.newCall(request).execute()
      if (response.isSuccessful) {
        val body = response.body?.string()
        if (!body.isNullOrEmpty()) {
          cacheFile.writeText(body)
          val ayahs = parseSurahJson(body, surahNumber)
          if (ayahs.isNotEmpty()) return@withContext ayahs
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

    // Fallback: return bundled if present, or fallback single ayah with bismillah
    if (bundled != null && bundled.isNotEmpty()) {
      return@withContext bundled
    }

    val surahMeta = QuranMetadata.getSurah(surahNumber)
    listOf(
      Ayah(
        numberInSurah = 1,
        globalNumber = surahNumber,
        surahNumber = surahNumber,
        textArabic = if (surahNumber == 9) "بَرَاءَةٌ مِّنَ اللَّهِ وَرَسُولِهِ" else "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
        textBangla = if (surahNumber == 9) "আল্লাহ ও তাঁর রাসূলের পক্ষ থেকে সম্পর্কচ্ছেদের ঘোষণা।" else "শুরু করছি পরম করুণাময় অসীম দয়ালু আল্লাহর নামে। (ইন্টারনেট চালু থাকলে সম্পূর্ণ সূরার সকল আয়াত দ্রুত সংরক্ষিত হবে)",
        paraNumber = surahMeta?.startPara ?: 1
      )
    )
  }

  suspend fun getAyahsForPara(paraNumber: Int): List<Ayah> = withContext(Dispatchers.IO) {
    val cacheFile = File(context.cacheDir, "para_$paraNumber.json")
    if (cacheFile.exists()) {
      try {
        val parsed = parseParaJson(cacheFile.readText(), paraNumber)
        if (parsed.isNotEmpty()) return@withContext parsed
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }

    val bundled = BundledQuranData.getAyahsForPara(paraNumber)
    if (bundled.isNotEmpty()) {
      return@withContext bundled
    }

    try {
      val url = "https://api.alquran.cloud/v1/juz/$paraNumber/editions/quran-uthmani,bn.bengali"
      val request = Request.Builder().url(url).build()
      val response = okHttpClient.newCall(request).execute()
      if (response.isSuccessful) {
        val body = response.body?.string()
        if (!body.isNullOrEmpty()) {
          cacheFile.writeText(body)
          val ayahs = parseParaJson(body, paraNumber)
          if (ayahs.isNotEmpty()) return@withContext ayahs
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

    val paraMeta = ParaMetadata.getPara(paraNumber)
    listOf(
      Ayah(
        numberInSurah = 1,
        globalNumber = 1,
        surahNumber = paraMeta?.startSurahNumber ?: 1,
        textArabic = paraMeta?.nameArabic ?: "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
        textBangla = "${paraMeta?.nameBangla ?: ""} - পারা $paraNumber (সূরা ${paraMeta?.startSurahName ?: ""} থেকে সূরা ${paraMeta?.endSurahName ?: ""})",
        paraNumber = paraNumber
      )
    )
  }

  private fun parseSurahJson(jsonStr: String, surahNumber: Int): List<Ayah> {
    val root = JSONObject(jsonStr)
    val data = root.getJSONArray("data")
    val arabicEdition = data.getJSONObject(0)
    val bengaliEdition = data.getJSONObject(1)

    val arabicAyahs = arabicEdition.getJSONArray("ayahs")
    val bengaliAyahs = bengaliEdition.getJSONArray("ayahs")

    val result = mutableListOf<Ayah>()
    val count = minOf(arabicAyahs.length(), bengaliAyahs.length())
    val surahMeta = QuranMetadata.getSurah(surahNumber)

    for (i in 0 until count) {
      val arObj = arabicAyahs.getJSONObject(i)
      val bnObj = bengaliAyahs.getJSONObject(i)

      val numInSurah = arObj.getInt("numberInSurah")
      val globalNum = arObj.getInt("number")
      var arText = arObj.getString("text").trim()
      val bnText = bnObj.getString("text").trim()
      val juzNum = arObj.optInt("juz", surahMeta?.startPara ?: 1)

      // In Uthmani edition, Surah 1 verse 1 is Bismillah. In other surahs, verse 1 often starts with Bismillah prefix
      if (surahNumber != 1 && surahNumber != 9 && numInSurah == 1) {
        val bismillahPrefix = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
        if (arText.startsWith(bismillahPrefix)) {
          arText = arText.removePrefix(bismillahPrefix).trim()
        }
      }

      val audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/$globalNum.mp3"

      result.add(
        Ayah(
          numberInSurah = numInSurah,
          globalNumber = globalNum,
          surahNumber = surahNumber,
          textArabic = arText,
          textBangla = bnText,
          audioUrl = audioUrl,
          paraNumber = juzNum
        )
      )
    }
    return result
  }

  private fun parseParaJson(jsonStr: String, paraNumber: Int): List<Ayah> {
    val root = JSONObject(jsonStr)
    val data = root.getJSONArray("data")
    val arabicEdition = data.getJSONObject(0)
    val bengaliEdition = data.getJSONObject(1)

    val arabicAyahs = arabicEdition.getJSONArray("ayahs")
    val bengaliAyahs = bengaliEdition.getJSONArray("ayahs")

    val result = mutableListOf<Ayah>()
    val count = minOf(arabicAyahs.length(), bengaliAyahs.length())

    for (i in 0 until count) {
      val arObj = arabicAyahs.getJSONObject(i)
      val bnObj = bengaliAyahs.getJSONObject(i)

      val surahObj = arObj.optJSONObject("surah")
      val surahNum = surahObj?.optInt("number") ?: 1
      val numInSurah = arObj.getInt("numberInSurah")
      val globalNum = arObj.getInt("number")
      val arText = arObj.getString("text").trim()
      val bnText = bnObj.getString("text").trim()
      val audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/$globalNum.mp3"

      result.add(
        Ayah(
          numberInSurah = numInSurah,
          globalNumber = globalNum,
          surahNumber = surahNum,
          textArabic = arText,
          textBangla = bnText,
          audioUrl = audioUrl,
          paraNumber = paraNumber
        )
      )
    }
    return result
  }
}
