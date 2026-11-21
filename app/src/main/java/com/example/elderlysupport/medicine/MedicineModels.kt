package com.example.elderlysupport.medicine

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

data class RecognizedMedicine(
    val name: String,
    val expiry: MedicineExpiry,
    val originalOcrText: String
)

data class MedicineProposal(
    val proposedName: String?,
    val proposedExpiry: MedicineExpiry?,
    val originalOcrText: String
)

enum class ExpiryStatus { EXPIRED, EXPIRING_SOON, VALID }

data class MedicineExpiry(val month: YearMonth, val date: LocalDate? = null) {
    fun storageValue(): String = date?.toString() ?: month.toString()

    fun displayValue(): String = date?.format(dayFormat) ?: month.format(monthFormat)

    fun status(today: LocalDate = LocalDate.now()): ExpiryStatus {
        val lastDay = date ?: month.atEndOfMonth()
        return when {
            lastDay < today -> ExpiryStatus.EXPIRED
            month <= YearMonth.from(today).plusMonths(2) -> ExpiryStatus.EXPIRING_SOON
            else -> ExpiryStatus.VALID
        }
    }

    private companion object {
        val dayFormat = DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.ITALY)
        val monthFormat = DateTimeFormatter.ofPattern("MM/uuuu", Locale.ITALY)
    }
}
