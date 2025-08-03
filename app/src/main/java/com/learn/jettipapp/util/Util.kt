package com.learn.jettipapp.util

// Menyederhanakan logika kalkulasi tip.
// Pengecekan `totalBill > 1` dan `toString().isNotEmpty()` tidak diperlukan.
// Cukup pastikan tagihan bernilai positif untuk menghindari hasil negatif.
fun calculateTotalTip(
    totalBill: Double,
    tipPercentage: Int
): Double {
    return if (totalBill > 0) (totalBill * tipPercentage) / 100 else 0.0
}

fun calculateTotalPerPerson(
    totalBill: Double,
    splitBy: Int,
    tipPercentage: Int
): Double {
    // Kalkulasi total tagihan (termasuk tip)
    val bill = calculateTotalTip(totalBill = totalBill, tipPercentage = tipPercentage) + totalBill
    // Bagi total tagihan dengan jumlah orang
    return (bill / splitBy)
}