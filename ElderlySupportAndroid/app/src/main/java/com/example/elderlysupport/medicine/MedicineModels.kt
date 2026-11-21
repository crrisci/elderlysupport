package com.example.elderlysupport.medicine

import java.time.YearMonth

data class RecognizedMedicine(
    val name: String,
    val expiryMonth: YearMonth,
    val originalOcrText: String
)

data class MedicineProposal(
    val proposedName: String?,
    val proposedExpiryMonth: YearMonth?,
    val originalOcrText: String
)

enum class ExpiryStatus { EXPIRED, EXPIRING_SOON, VALID }

fun expiryStatus(month: YearMonth, today: java.time.LocalDate = java.time.LocalDate.now()): ExpiryStatus {
    val currentMonth = YearMonth.from(today)
    return when {
        month < currentMonth -> ExpiryStatus.EXPIRED
        month <= currentMonth.plusMonths(2) -> ExpiryStatus.EXPIRING_SOON
        else -> ExpiryStatus.VALID
    }
}
