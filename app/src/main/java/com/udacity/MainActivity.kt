package com.udacity

import android.app.DownloadManager
import android.app.DownloadManager.COLUMN_STATUS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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


const val TAG = "MainActivity"
const val STATUS_KEY = "status"
const val DOWNLOAD_ID_KEY = "download_id"
const val NOTIFICATION_ID_KEY = "notification_id"
const val FILE_NAME_KEY = "file_name"
class MainActivity : AppCompatActivity() {


    private var downloadID: Long = 0
    private lateinit var currentUrl:String
    private lateinit var filename:String



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

            val intent = Intent(this@MainActivity,DetailActivity::class.java)

            var statusString = getString(R.string.download_failed_string)

            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE)
                    as DownloadManager
            val cursor = downloadManager.query(DownloadManager
                .Query()
                .setFilterById(downloadID))



            if (cursor.moveToFirst()) {
                val valueOfStatus = cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS))
                    cursor.columnNames.forEach { println(it) }
                val localUri = cursor.getString(cursor.getColumnIndex("uri"))
                Log.e(TAG,"uri: $localUri")
                if (valueOfStatus==DownloadManager.STATUS_SUCCESSFUL) {
                    statusString =  getString(R.string.download_success_string)

                }
            }

            cursor.close()

            intent.putExtra(DOWNLOAD_ID_KEY,id)
            intent.putExtra(NOTIFICATION_ID_KEY, NOTIFICATION_ID)
            intent.putExtra(STATUS_KEY,statusString)
            intent.putExtra(FILE_NAME_KEY,filename)


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

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        Log.e("MainActivity DOWNLOAD ID:","$downloadID")
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
                        filename = getString(R.string.radio_option_1)
                    }
                R.id.radio_loadapp ->
                    if (checked) {
                        // Ninjas rule
                        currentUrl = LOADAPP_URL
                        filename = getString(R.string.radio_option_2)
                    }
                else -> if (checked) {
                    currentUrl = RETROFIT_URL
                    filename = getString(R.string.radio_option_3)
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

    companion object {

        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val LOADAPP_URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val RETROFIT_URL = "https://github.com/square/retrofit"

        private const val CHANNEL_ID = "channelId"
    }

//    private fun getDownloadStatus() {
//        val query = DownloadManager.Query()
//        query.setFilterById(downloadID)
//        cursor = (getSystemService(DOWNLOAD_SERVICE) as DownloadManager)
//            .query(query)
//        if (cursor.moveToFirst()) {
//            val timer = Timer()
//            timer.schedule(object : TimerTask() {
//                override fun run() {
//                    query.setFilterById(downloadID)
//                    val cursor = (getSystemService(DOWNLOAD_SERVICE) as DownloadManager)
//                        .query(query)
//                    cursor.moveToFirst()
//                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
//                    if (status == DownloadManager.STATUS_RUNNING) {
//                        Log.i("DM_STATUS", "status is " + " running")
//                    } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
////                            statusString = "SUCCESS"
//
//                        Log.i("DM_STATUS", "status is " + " success")
//                        timer.cancel()
//                    }
//                }
//            }, 100, 1)
//        }
//    }

}
