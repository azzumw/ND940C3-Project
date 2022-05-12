package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private  var downloadID:Long = 0L
    private lateinit var textView:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        textView = findViewById(R.id.textView)

        downloadID = intent.extras?.getLong(DOWNLOAD_ID_KEY)!!
        val notificationId = intent.extras?.getInt(NOTIFICATION_ID_KEY)
        val status = intent.extras?.getString(STATUS_KEY)
        textView.text = status.toString()

        val nm = getSystemService(NotificationManager::class.java) as NotificationManager

        nm.cancel(notificationId!!)
    }

}
