package com.learn.jettipapp.util

fun calculateTotalTip(
    totalBill: Double,
    tipPrecentage: Int): Double {
    return if (totalBill > 1 && totalBill.toString().isNotEmpty())
        (totalBill * tipPrecentage) / 100 else 0.0
}