package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ParaMetadata
import com.example.data.QuranMetadata
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("পবিত্র আল-কুরআন শরীফ", appName)
  }

  @Test
  fun `verify full 114 surahs and 30 paras count`() {
    assertEquals(114, QuranMetadata.allSurahs.size)
    assertEquals(30, ParaMetadata.allParas.size)
  }
}
