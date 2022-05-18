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
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.ButtonState.Completed
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*


const val TAG = "MainActivity"
const val STATUS_KEY = "status"
const val DOWNLOAD_ID_KEY = "download_id"
const val NOTIFICATION_ID_KEY = "notification_id"
const val FILE_NAME_KEY = "file_name"
var isDownloadComplete = false
var hasDownloadStarted = Download.NOT_STARTED
var radioButtonIsSelected = false

class MainActivity : AppCompatActivity() {

    private lateinit var loadingButton: LoadingButton

    private var downloadID: Long = 0
    private lateinit var currentUrl: String
    private lateinit var filename: String
    private lateinit var rdGroup: RadioGroup

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        loadingButton = findViewById(R.id.custom_button)
        notificationManager = getSystemService(NotificationManager::class.java)

        rdGroup = findViewById(R.id.radioGroup)

        //set radioButtonIsSelected false when starting this activity
//        rdGroup.clearCheck()
        radioButtonIsSelected = false

        rdGroup.setOnCheckedChangeListener { group, checkedId ->

            if(checkedId==-1)
            isDownloadComplete = false
            hasDownloadStarted = Download.NOT_STARTED

        }

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        notificationManager.createNotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name)
        )

        loadingButton.setOnClickListener {

            if (radioButtonIsSelected) {
                download()

            } else {
                Toast.makeText(
                    this,
                    getString(R.string.no_radio_option_selected_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val intentAction = intent?.action
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intentAction) {
                Log.e("TAG Download Status", "  - completed")
                isDownloadComplete = true
                hasDownloadStarted = Download.NOT_STARTED
            }

            var statusString = getString(R.string.download_failed_string)

            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE)
                    as DownloadManager
            val cursor = downloadManager.query(
                DownloadManager
                    .Query()
                    .setFilterById(downloadID)
            )

            if (cursor.moveToFirst()) {
                val valueOfStatus = cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS))
                cursor.columnNames.forEach { println(it) }
                if (valueOfStatus == DownloadManager.STATUS_SUCCESSFUL) {
                    statusString = getString(R.string.download_success_string)
                }
            }

            cursor.close()

            notificationManager.sendNotification(this@MainActivity,id!!,statusString,filename)
        }
    }

    private fun download() {

        hasDownloadStarted = Download.STARTED

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
        Log.e("MainActivity DOWNLOAD ID:", "$downloadID")
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


    companion object {

        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val LOADAPP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val RETROFIT_URL = "https://github.com/square/retrofit"

        private const val CHANNEL_ID = "channelId"
    }
}
