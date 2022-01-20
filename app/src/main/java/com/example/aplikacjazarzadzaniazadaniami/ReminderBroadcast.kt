package com.example.aplikacjazarzadzaniazadaniami

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        var id: Int?
        var title: String?
        var desc: String?

        var extras = intent?.extras

        id = extras?.getInt("VALUE_ID")
        title = extras?.getString("VALUE_TITLE")
        desc = extras?.getString("VALUE_DESC")

        Log.d("Check id", id.toString())

        if(id != 0 && id != null) {

            val notification: Notification =
                NotificationCompat.Builder(context as Context, "channel_$id")
                    .setSmallIcon(R.drawable.ikonapow)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ikonapow))
                    .setContentTitle(title)
                    .setContentText(desc)
                    .setVibrate(longArrayOf(1L, 2L, 3L))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()

            val notificationManager: NotificationManagerCompat =
                NotificationManagerCompat.from(context)
            notificationManager?.notify(id, notification)
        }
    }
}