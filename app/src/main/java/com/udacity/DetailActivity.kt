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

    private var _binding : ActivityDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        val downloadID = intent.extras?.getLong(DOWNLOAD_ID_KEY)!!
        val notificationId = intent.extras?.getInt(NOTIFICATION_ID_KEY)

        binding.inc.filenameValue.text = intent.extras?.getString(FILE_NAME_KEY)
        binding.inc.statusValue.text = intent.extras?.getString(STATUS_KEY)

        binding.inc.fab.setOnClickListener {
            goBack()
        }

        val nm = getSystemService(NotificationManager::class.java) as NotificationManager

        nm.cancel(notificationId!!)

    }

    private fun goBack(){
//        Toast.makeText(this,"Lets do it!",Toast.LENGTH_LONG).show()
        onSupportNavigateUp()
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        onSupportNavigateUp()
    }
}
