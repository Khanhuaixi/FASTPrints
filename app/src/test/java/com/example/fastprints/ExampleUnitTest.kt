package com.example.fastprints

import org.junit.Assert.assertEquals
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun check_MakePayment_Multiplication() {
        val unitPrice = 1.0
        val totalCopies = 5
        val totalPrice = MathOperation().calculateTotalPayment(unitPrice, totalCopies)
        assertThat(totalPrice).isEqualTo(BigDecimal(unitPrice * totalCopies).setScale(2))
    }
}