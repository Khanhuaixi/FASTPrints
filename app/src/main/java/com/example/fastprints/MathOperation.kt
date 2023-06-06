package com.example.fastprints

import java.math.BigDecimal

class MathOperation {

    //MakePayment
    fun calculateTotalPayment(unitPrice: Double, totalCopies: Int): BigDecimal {
        //multiply
        val totalPrice = unitPrice * totalCopies
        //set to 2 decimal places using BigDecimal setScale
        return BigDecimal(totalPrice).setScale(2)
    }
}