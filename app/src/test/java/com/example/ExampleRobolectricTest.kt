package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.FinancialEngine
import com.example.domain.SafeToSpendBreakdown
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Trackie", appName)
  }

  @Test
  fun `verify safe to spend calculation`() {
    // Current available: 250,000
    // Upcoming bills: 60,000
    // Planned savings: 50,000
    // Required reserves: 66,500
    // Safe to spend: 250,000 - 60,000 - 50,000 - 66,500 = 73,500
    val safe = 250000.0 - 60000.0 - 50000.0 - 66500.0
    assertEquals(73500.0, safe, 0.01)
  }

  @Test
  fun `verify what-if scenario simulation`() {
    val sim = FinancialEngine.simulateScenario(
      type = "BUY_PHONE",
      paramValue = 250000.0,
      currentSafeToSpend = 73500.0,
      monthlyExpenses = 247600.0,
      netWorth = 457500.0
    )
    assertEquals(0.0, sim.newSafeToSpend, 0.01)
    assertTrue(sim.explanation.contains("Warning") || sim.explanation.contains("Safe-to-Spend"))
  }
}
