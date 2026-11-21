package com.example.elderlysupport.medicine

import java.time.DateTimeException
import java.time.YearMonth

object MedicineParser {
    // Catalogo SOLO dimostrativo: ampliare usando un elenco reale verificato.
    private val medicineNames = listOf(
        "TACHIPIRINA", "BRUFEN", "OKI", "AUGMENTIN", "EFFERALGAN",
        "MOMENT", "VOLTAREN", "BUSCOPAN", "ZIRTEC", "MAALOX"
    )

    private val expiryLabel = Regex("""(?i)\b(?:SCADENZA|SCAD\.?|EXPIRY|EXP\.?)\b""")
    private val monthYear = Regex("""(?<![\d/.-])(0?[1-9]|1[0-2])\s*[/.-]\s*(20\d{2})(?!\d|[/.-]\d)""")
    private val dayMonthYear = Regex("""(?<![\d/.-])(0?[1-9]|[12]\d|3[01])\s*[/.-]\s*(0?[1-9]|1[0-2])\s*[/.-]\s*(20\d{2})(?!\d|[/.-]\d)""")
    private val yearMonth = Regex("""(?<![\d/.-])(20\d{2})\s*[-/]\s*(0?[1-9]|1[0-2])(?!\d|[/.-]\d)""")

    fun parse(text: String): MedicineProposal {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        val upperText = text.uppercase()
        val recognizedName = medicineNames
            .sortedByDescending { it.length }
            .firstOrNull { candidate ->
                Regex("""(?<![\p{L}])${Regex.escape(candidate)}(?![\p{L}])""").containsMatchIn(upperText)
            }

        val expiry = lines.indices.asSequence()
            .mapNotNull { index ->
                // La data può essere sulla stessa riga oppure su quella subito dopo.
                val label = expiryLabel.find(lines[index]) ?: return@mapNotNull null
                val afterLabel = lines[index].substring(label.range.last + 1)
                readExpiry(afterLabel) ?: lines.getOrNull(index + 1)?.let(::readExpiry)
            }.firstOrNull()

        return MedicineProposal(recognizedName, expiry, text)
    }

    fun readExpiry(value: String): MedicineExpiry? {
        dayMonthYear.find(value)?.let { match ->
            val day = match.groupValues[1].toInt()
            val month = match.groupValues[2].toInt()
            val year = match.groupValues[3].toInt()
            return try {
                val date = java.time.LocalDate.of(year, month, day)
                MedicineExpiry(YearMonth.from(date), date)
            } catch (_: DateTimeException) { null }
        }
        monthYear.find(value)?.let { match ->
            return MedicineExpiry(YearMonth.of(match.groupValues[2].toInt(), match.groupValues[1].toInt()))
        }
        yearMonth.find(value)?.let { match ->
            return MedicineExpiry(YearMonth.of(match.groupValues[1].toInt(), match.groupValues[2].toInt()))
        }
        return null
    }
}
