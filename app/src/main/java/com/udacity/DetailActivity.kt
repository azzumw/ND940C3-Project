package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {


    private  lateinit var filenameTextView : TextView
    private  lateinit var statusTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        filenameTextView = findViewById(R.id.filename_value)
        statusTextView = findViewById(R.id.status_value)
        val inc = findViewById<View>(R.id.inc)
        val button = inc.findViewById<FloatingActionButton>(R.id.fab)
        button.setOnClickListener {
            goBack()
        }

        val downloadID = intent.extras?.getLong(DOWNLOAD_ID_KEY)!!
        val notificationId = intent.extras?.getInt(NOTIFICATION_ID_KEY)
        val status = intent.extras?.getString(STATUS_KEY)
        val filename = intent.extras?.getString(FILE_NAME_KEY)

        filenameTextView.text = filename
        statusTextView.text = status.toString()

        val nm = getSystemService(NotificationManager::class.java) as NotificationManager

        nm.cancel(notificationId!!)

    }

    private fun goBack(){
        Toast.makeText(this,"Lets do it!",Toast.LENGTH_LONG).show()
        onSupportNavigateUp()
    }
}
