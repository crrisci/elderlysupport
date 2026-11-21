package com.example.elderlysupport.medicine

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MedicineParserTest {
    @Test fun readsLabelAndKeepsPrintedDay() {
        val result = MedicineParser.parse("Tachipirina\nPROD 03/2025 SCAD\n10/11/2026")
        assertEquals("TACHIPIRINA", result.proposedName)
        val expiry = requireNotNull(result.proposedExpiry)
        assertEquals("2026-11-10", expiry.storageValue())
        assertEquals(ExpiryStatus.EXPIRED, expiry.status(LocalDate.of(2026, 11, 11)))
    }

    @Test fun handlesMonthPrecisionAndTwoMonthThreshold() {
        val expiry = requireNotNull(MedicineParser.parse("EXP 01/2027").proposedExpiry)
        assertEquals("01/2027", expiry.displayValue())
        assertEquals(ExpiryStatus.EXPIRING_SOON, expiry.status(LocalDate.of(2026, 11, 1)))
        assertEquals(ExpiryStatus.VALID, expiry.status(LocalDate.of(2026, 10, 31)))
    }

    @Test fun leavesUnknownNameAndUnlabelledOrInvalidDateForReview() {
        val result = MedicineParser.parse("Prodotto di esempio\nLOT 03/2026")
        assertNull(result.proposedName)
        assertNull(result.proposedExpiry)
        assertNull(MedicineParser.readExpiry("31/02/2027"))
        assertNull(MedicineParser.readExpiry("32/03/2027"))
    }
}
