package com.udacity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat


const val CHANNEL_ID = "channelId"
const val NOTIFICATION_ID = 12

fun NotificationManager.sendNotification(context: Context,id:Long,statusString:String,filename:String){

    //create intent
    val intent = Intent(context,DetailActivity::class.java)

    intent.putExtra(DOWNLOAD_ID_KEY, id)
    intent.putExtra(NOTIFICATION_ID_KEY, NOTIFICATION_ID)
    intent.putExtra(STATUS_KEY, statusString)
    intent.putExtra(FILE_NAME_KEY, filename)

    val pendingIntent = PendingIntent.getActivity(context,NOTIFICATION_ID,intent,PendingIntent.FLAG_UPDATE_CURRENT)


    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(context.getString(R.string.notification_title))
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentText(context.getString(R.string.notification_description))
        .addAction(R.drawable.ic_assistant_black_24dp,
            context.getString(R.string.notification_button),pendingIntent)
        .setAutoCancel(true)


    notify(NOTIFICATION_ID,builder.build())
}

fun NotificationManager.createNotificationChannel(channelId:String,channelName:String){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val channel = NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_LOW)

        createNotificationChannel(channel)
    }
}