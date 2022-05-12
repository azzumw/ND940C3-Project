package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File


const val STATUS_KEY = "status"
const val DOWNLOAD_ID_KEY = "download_id"
const val NOTIFICATION_ID_KEY = "notification_id"
class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var currentUrl:String
    lateinit var cursor: Cursor


    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = getSystemService(NotificationManager::class.java)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createNotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name))

        custom_button.setOnClickListener {

            if (radioButtonIsSelected){
                download()

            }else{
                Toast.makeText(this,getString(R.string.no_radio_option_selected_message),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.e("MainActivity On Receive: ",id.toString())
            val intent = Intent(this@MainActivity,DetailActivity::class.java)
            // query download status
            // query download status
            var statusString = "FAILED"
            if (cursor.moveToNext()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                Log.e("MainActivity Status Download: ", status.toString())
                cursor.close();

                 if(status == DownloadManager.STATUS_SUCCESSFUL){
                        statusString = "SUCCESS"
                }
            }


            intent.putExtra(DOWNLOAD_ID_KEY,id)
            intent.putExtra(NOTIFICATION_ID_KEY, NOTIFICATION_ID)
            intent.putExtra(STATUS_KEY,statusString)

            pendingIntent = PendingIntent.getActivity(context,NOTIFICATION_ID,intent,PendingIntent.FLAG_UPDATE_CURRENT)

            action = NotificationCompat.Action(R.drawable.ic_assistant_black_24dp,
            getString(R.string.notification_button),pendingIntent)

            val builder = NotificationCompat.Builder(this@MainActivity, CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                .setContentText(getString(R.string.notification_description))
                .addAction(action)
                .setAutoCancel(true)

            notificationManager.notify(NOTIFICATION_ID,builder.build())

//            notificationManager.sendNotification(this@MainActivity,getString(R.string.notification_description))

        }
    }

    private fun download() {

        Toast.makeText(this,"downloading... $currentUrl",Toast.LENGTH_LONG).show()

        val request =
            DownloadManager.Request(Uri.parse(currentUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
//                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager


        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        cursor =
            downloadManager.query(DownloadManager.Query().setFilterById(downloadID))

        Log.e("MainActivity DOWNLOAD ID:","$downloadID")
        Log.e("MainActivity Cursor:","$cursor")

    }

    companion object {

        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val LOADAPP_URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val RETROFIT_URL = "https://github.com/square/retrofit"

        private const val CHANNEL_ID = "channelId"
    }

    fun onRadioButtonClicked(view: View) {

        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked
            if (checked) radioButtonIsSelected = true

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_glide ->
                    if (checked) {
                        // Pirates are the best
                        currentUrl = GLIDE_URL
                    }
                R.id.radio_loadapp ->
                    if (checked) {
                        // Ninjas rule
                        currentUrl = LOADAPP_URL
                    }
                else -> if (checked) {
                    currentUrl = RETROFIT_URL
                }
            }
        }
    }


    private fun createNotificationChannel(channelId:String,channelName:String){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_LOW)

            notificationManager.createNotificationChannel(channel)
        }
    }

}
