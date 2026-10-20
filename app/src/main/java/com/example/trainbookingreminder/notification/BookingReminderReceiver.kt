package com.example.trainbookingreminder.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import androidx.core.app.NotificationCompat
import com.example.trainbookingreminder.MainActivity
import com.example.trainbookingreminder.R

class BookingReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == ACTION_BOOKING_REMINDER) {
            val title = intent.getStringExtra(EXTRA_TITLE) ?: "IRCTC Booking Reminder"
            val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Booking opens in 15 minutes! Be ready on IRCTC."
            val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 1001)

            showNotification(context, title, message, notificationId)
        }
    }

    private fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = CHANNEL_ID

        val customSoundUri = android.net.Uri.parse("android.resource://" + context.packageName + "/" + R.raw.notification_tone)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val channel = NotificationChannel(
            channelId,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for upcoming IRCTC General & Tatkal booking windows"
            enableVibration(true)
            setSound(customSoundUri, audioAttributes)
        }
        notificationManager.createNotificationChannel(channel)

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(customSoundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val ACTION_BOOKING_REMINDER = "com.example.trainbookingreminder.ACTION_BOOKING_REMINDER"
        const val CHANNEL_ID = "irctc_booking_reminders_channel"
        const val CHANNEL_NAME = "IRCTC Booking Reminders"

        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }
}
