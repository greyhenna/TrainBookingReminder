package com.example.trainbookingreminder.data.model

import android.content.Context
import com.example.trainbookingreminder.R
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

enum class BookingStatus {
    UPCOMING,
    OPENING_TODAY,
    EXPIRED
}

/**
 * Calculated booking dates and notification state for a specific quota.
 */
data class BookingInfo(
    val type: BookingType,
    val openingDateTime: ZonedDateTime,
    val notificationDateTime: ZonedDateTime,
    val isNotificationScheduled: Boolean = false
) {
    val status: BookingStatus
        get() {
            val now = ZonedDateTime.now(openingDateTime.zone)
            return when {
                now.isAfter(openingDateTime) -> BookingStatus.EXPIRED
                now.toLocalDate().isEqual(openingDateTime.toLocalDate()) -> BookingStatus.OPENING_TODAY
                else -> BookingStatus.UPCOMING
            }
        }

    fun formattedOpeningDate(): String {
        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")
        return openingDateTime.format(formatter)
    }

    fun formattedOpeningTime(): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a z")
        return openingDateTime.format(formatter)
    }

    fun formattedNotificationTime(): String {
        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy 'at' hh:mm a")
        return notificationDateTime.format(formatter)
    }

    fun getCountdownText(context: Context): String {
        val now = ZonedDateTime.now(openingDateTime.zone)
        if (now.isAfter(openingDateTime)) {
            return context.getString(R.string.status_window_passed)
        }
        val duration = Duration.between(now, openingDateTime)
        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60

        return when {
            days > 1 -> context.getString(R.string.status_opens_in_days, days)
            days == 1L -> context.getString(R.string.status_opens_in_day, 1)
            hours > 0 -> context.getString(R.string.status_opens_in_hrs_mins, hours, minutes)
            minutes > 0 -> context.getString(R.string.status_opens_in_mins, minutes)
            else -> context.getString(R.string.status_opening_now)
        }
    }
}
