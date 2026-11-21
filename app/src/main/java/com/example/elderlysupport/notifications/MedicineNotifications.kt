package com.example.elderlysupport.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.elderlysupport.R

/** Notifiche immediate. Il permesso viene richiesto dall'interfaccia dell'app ospitante. */
class MedicineNotifications(context: Context) {
    private val context = context.applicationContext
    private val manager = NotificationManagerCompat.from(this.context)

    enum class Availability { AVAILABLE, PERMISSION_REQUIRED, APP_DISABLED, CHANNEL_DISABLED }
    enum class SendResult { SENT, PERMISSION_REQUIRED, APP_DISABLED, CHANNEL_DISABLED }

    init { createChannel() }

    fun availability(): Availability {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return Availability.PERMISSION_REQUIRED
        if (!manager.areNotificationsEnabled()) return Availability.APP_DISABLED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            manager.getNotificationChannel(CHANNEL_ID)?.importance == NotificationManager.IMPORTANCE_NONE
        ) return Availability.CHANNEL_DISABLED
        return Availability.AVAILABLE
    }

    /** Lo stesso id aggiorna la notifica esistente; id diversi creano notifiche distinte. */
    @SuppressLint("MissingPermission") // availability controlla il permesso; gestiamo anche una revoca concorrente.
    fun showReminder(id: Int, title: String, message: String, onTap: PendingIntent): SendResult {
        createChannel()
        when (availability()) {
            Availability.PERMISSION_REQUIRED -> return SendResult.PERMISSION_REQUIRED
            Availability.APP_DISABLED -> return SendResult.APP_DISABLED
            Availability.CHANNEL_DISABLED -> return SendResult.CHANNEL_DISABLED
            Availability.AVAILABLE -> Unit
        }
        val publicVersion = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_public_title))
            .setContentText(context.getString(R.string.notification_public_message))
            .build()
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(publicVersion)
            .setContentIntent(onTap)
            .setAutoCancel(true)
            .build()
        return try {
            manager.notify(id, notification)
            SendResult.SENT
        } catch (_: SecurityException) {
            SendResult.PERMISSION_REQUIRED
        }
    }

    fun cancel(id: Int) = manager.cancel(id)

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = context.getString(R.string.notification_channel_description)
                    lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
                }
            )
        }
    }

    companion object { const val CHANNEL_ID = "medicine_reminders" }
}
