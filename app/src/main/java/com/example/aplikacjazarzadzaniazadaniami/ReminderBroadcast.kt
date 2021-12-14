package com.example.aplikacjazarzadzaniazadaniami

import android.app.Application

public class ReminderBroadcast : Application() {

    public val channel_1 = "channel1"
    val channel_2 = "channel2"

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }

    private fun createNotificationChannel(){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//
//            val channel2 = NotificationChannel(channel_2, "Channel 2", importance)
//            channel2.description = "hehe1"
//
//
//            notificationManager?.createNotificationChannel(channel2)
//        }
    }
}