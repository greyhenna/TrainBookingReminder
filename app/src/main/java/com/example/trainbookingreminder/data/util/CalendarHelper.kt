package com.example.trainbookingreminder.data.util

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import com.example.trainbookingreminder.data.model.BookingInfo
import java.time.format.DateTimeFormatter

object CalendarHelper {

    /**
     * Triggers the system Calendar intent to insert a booking opening event.
     */
    fun addBookingToCalendar(context: Context, bookingInfo: BookingInfo, journeyDateFormatted: String) {
        val beginTime = bookingInfo.openingDateTime.toInstant().toEpochMilli()
        val endTime = bookingInfo.openingDateTime.plusMinutes(30).toInstant().toEpochMilli()

        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a z")
        val openingTimeFormatted = bookingInfo.openingDateTime.format(timeFormatter)

        val quotaTitle = bookingInfo.type.getTitle(context)
        val quotaSubtitle = bookingInfo.type.getSubtitle(context)

        val title = "IRCTC $quotaTitle Opens"
        val description = "IRCTC booking window opens at $openingTimeFormatted for journey date $journeyDateFormatted.\n" +
                "Class Details: $quotaSubtitle\n" +
                "Tip: Log in 5 minutes early on IRCTC Rail Connect App or irctc.co.in with payment details ready."

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.Events.DESCRIPTION, description)
            putExtra(CalendarContract.Events.EVENT_LOCATION, "IRCTC App / irctc.co.in")
            putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        runCatching {
            context.startActivity(intent)
        }
    }
}
