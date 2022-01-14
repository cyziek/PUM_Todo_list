package com.example.aplikacjazarzadzaniazadaniami

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val id = AddToList.getId()
        val title = AddToList.getTitle()
        val desc = AddToList.getDesc()

        val notification: Notification =
            NotificationCompat.Builder(context as Context, "channel_$id")
                .setSmallIcon(R.drawable.ic_baseline_clear_24)
                .setContentTitle(title)
                .setContentText(desc)
                .setVibrate(longArrayOf(1L, 2L, 3L))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManager?.notify(id as Int, notification)
    }
}