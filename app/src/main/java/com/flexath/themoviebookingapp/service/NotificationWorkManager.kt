package com.flexath.themoviebookingapp.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.flexath.themoviebookingapp.R

class NotificationWorkManager(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val NOTIFICATION_CHANNEL_ID = "NotificationChannelId"
    private val NOTIFICATION_CHANNEL_NAME = "NotificationChannelName"

    override fun doWork(): Result {
        try {
            createNotificationChannel()
            val notification = NotificationCompat.Builder(applicationContext,NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Reminder for Release Date")
                .setContentText("You booked for today")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24dp)
                .build()

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return Result.failure()
            }
            NotificationManagerCompat.from(applicationContext).notify(11,notification)
            return Result.success()
        } catch (e:Exception) {
            return Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,NOTIFICATION_CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}