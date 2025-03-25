package com.practicum.playlistmaker.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.search.SearchActivity
import com.practicum.playlistmaker.ui.settings.SettingsActivity
import com.practicum.playlistmaker.ui.media.MediaActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val searchButton: Button = findViewById(R.id.search)
        searchButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
               val displayIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(displayIntent)                   }        })

        val mediaButton: Button = findViewById(R.id.media_button)
       mediaButton.setOnClickListener {
         val displayIntent = Intent(this, MediaActivity::class.java)
           startActivity(displayIntent)
       }
        val settingsButton: Button = findViewById(R.id.settings_button)
        settingsButton.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)}}


}



