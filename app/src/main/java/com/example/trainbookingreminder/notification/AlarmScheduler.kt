package com.example.trainbookingreminder.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.edit
import com.example.trainbookingreminder.data.model.BookingInfo
import com.example.trainbookingreminder.data.model.BookingType

object AlarmScheduler {

    private const val PREFS_NAME = "irctc_reminder_prefs"

    fun scheduleNotification(
        context: Context,
        bookingInfo: BookingInfo,
        journeyDateFormatted: String
    ): ScheduleResult {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val triggerTimeMillis = bookingInfo.notificationDateTime.toInstant().toEpochMilli()
        val nowMillis = System.currentTimeMillis()

        if (triggerTimeMillis <= nowMillis) {
            return ScheduleResult.Error("Notification time is in the past! Please select a future journey date.")
        }

        val quotaTitle = bookingInfo.type.getTitle(context)
        val requestCode = getRequestCode(bookingInfo.type)
        val intent = Intent(context, BookingReminderReceiver::class.java).apply {
            action = BookingReminderReceiver.ACTION_BOOKING_REMINDER
            putExtra(BookingReminderReceiver.EXTRA_TITLE, "⏰ IRCTC $quotaTitle Opens Soon!")
            putExtra(
                BookingReminderReceiver.EXTRA_MESSAGE,
                "$quotaTitle for travel on $journeyDateFormatted opens in 15 minutes at ${bookingInfo.formattedOpeningTime()}. Get ready on IRCTC!"
            )
            putExtra(BookingReminderReceiver.EXTRA_NOTIFICATION_ID, requestCode)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

        try {
            if (canExact) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            }
            saveScheduledState(
                context = context,
                type = bookingInfo.type,
                isScheduled = true,
                timeMillis = triggerTimeMillis
            )
            return ScheduleResult.Success("Reminder set for ${bookingInfo.formattedNotificationTime()}")
        } catch (_: SecurityException) {
            return ScheduleResult.NeedsExactAlarmPermission
        } catch (e: Exception) {
            return ScheduleResult.Error("Failed to set alarm: ${e.localizedMessage}")
        }
    }

    fun cancelNotification(context: Context, type: BookingType) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val requestCode = getRequestCode(type)

        val intent = Intent(context, BookingReminderReceiver::class.java).apply {
            action = BookingReminderReceiver.ACTION_BOOKING_REMINDER
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

        saveScheduledState(
            context = context,
            type = type,
            isScheduled = false,
            timeMillis = 0L
        )
    }

    fun isNotificationScheduled(context: Context, type: BookingType, targetTimeMillis: Long): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isSaved = prefs.getBoolean("scheduled_${type.name}", false)
        val savedTime = prefs.getLong("time_${type.name}", 0L)

        return isSaved && (savedTime == targetTimeMillis) && (targetTimeMillis > System.currentTimeMillis())
    }

    private fun saveScheduledState(context: Context, type: BookingType, isScheduled: Boolean, timeMillis: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putBoolean("scheduled_${type.name}", isScheduled)
            putLong("time_${type.name}", timeMillis)
        }
    }

    private fun getRequestCode(type: BookingType): Int {
        return when (type) {
            BookingType.GENERAL -> 1001
            BookingType.AC_TATKAL -> 1002
            BookingType.SL_TATKAL -> 1003
        }
    }

    sealed class ScheduleResult {
        data class Success(val message: String) : ScheduleResult()
        object NeedsExactAlarmPermission : ScheduleResult()
        data class Error(val message: String) : ScheduleResult()
    }
}
