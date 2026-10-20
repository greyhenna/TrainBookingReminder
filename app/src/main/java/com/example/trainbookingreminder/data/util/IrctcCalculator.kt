package com.example.trainbookingreminder.data.util

import com.example.trainbookingreminder.data.model.BookingInfo
import com.example.trainbookingreminder.data.model.BookingType
import java.time.LocalDate
import java.time.ZoneId

/**
 * Utility class to calculate exact IRCTC booking opening dates and notification times.
 * IRCTC calculations are standardized against Indian Standard Time (Asia/Kolkata).
 */
object IrctcCalculator {

    val IST_ZONE_ID: ZoneId = ZoneId.of("Asia/Kolkata")

    /**
     * Calculates the exact booking opening ZonedDateTime and reminder time
     * for a given quota type based on the user-selected Journey Date.
     */
    fun calculateBookingInfo(
        journeyDate: LocalDate,
        type: BookingType,
        isNotificationScheduled: Boolean = false
    ): BookingInfo {
        val openingDate = journeyDate.minusDays(type.advanceDays)
        val openingDateTime = openingDate.atTime(type.openingTime).atZone(IST_ZONE_ID)
        val notificationDateTime = openingDateTime.minusMinutes(type.reminderLeadMinutes)

        return BookingInfo(
            type = type,
            openingDateTime = openingDateTime,
            notificationDateTime = notificationDateTime,
            isNotificationScheduled = isNotificationScheduled
        )
    }

    /**
     * Calculates booking details for all three IRCTC quotas for a given journey date.
     */
    fun calculateAllBookings(
        journeyDate: LocalDate,
        scheduledMap: Map<BookingType, Boolean> = emptyMap()
    ): List<BookingInfo> {
        return BookingType.entries.map { type ->
            calculateBookingInfo(
                journeyDate = journeyDate,
                type = type,
                isNotificationScheduled = scheduledMap[type] ?: false
            )
        }
    }
}
