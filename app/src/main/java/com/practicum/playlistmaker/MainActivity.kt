package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val searchButton: Button = findViewById(R.id.search)
        searchButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
               val displayIntent = Intent(this@MainActivity, Search::class.java)
                startActivity(displayIntent)                   }        })

        val mediaButton: Button = findViewById(R.id.media_button)
       mediaButton.setOnClickListener {
         val displayIntent = Intent(this, Media::class.java)
           startActivity(displayIntent)
       }
        val settingsButton: Button = findViewById(R.id.settings_button)
        settingsButton.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)}}


}



