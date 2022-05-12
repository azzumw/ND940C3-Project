package com.udacity

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


const val CHANNEL_ID = "channelId"
const val NOTIFICATION_ID = 12

fun NotificationManager.sendNotification(context: Context,message:String){

    //create intent
    val intent = Intent(context,DetailActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context,NOTIFICATION_ID,intent,PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(context.getString(R.string.notification_title))
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentText(message)
        .addAction(R.drawable.ic_assistant_black_24dp,
            context.getString(R.string.notification_button),pendingIntent)
        .setAutoCancel(true)


    notify(NOTIFICATION_ID,builder.build())
}