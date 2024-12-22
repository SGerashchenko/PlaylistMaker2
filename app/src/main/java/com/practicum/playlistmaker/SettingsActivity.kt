package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Назад в главное меню
        val backMenu: ImageView = findViewById(R.id.back_menu)
        backMenu.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }
              val switchDayNight: Switch = findViewById(R.id.switchDayNight)
        switchDayNight.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)}
        }

        val shareApp: ImageView = findViewById(R.id.shareApp)
        shareApp.setOnClickListener {
            val shareAppIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.addressYandex))
            }
            startActivity(Intent.createChooser(shareAppIntent, getString(R.string.choose_sharing_method)))
        }
        val sendSupport: ImageView = findViewById(R.id.support)
        sendSupport.setOnClickListener {
            val email = getString(R.string.myEmail)
            val emailSubject = Uri.encode(getString(R.string.mailSupport))
            val emailText = Uri.encode(getString(R.string.TextSupport))

            val mailUri = Uri.parse("mailto:$email?subject=$emailSubject&body=$emailText")

            val sendSupportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = mailUri
            }

            startActivity(sendSupportIntent)
        }

        val openAgreement = findViewById<ImageView>(R.id.openAgreement)
        openAgreement.setOnClickListener {
            val openAgreementIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.offer))
            }
            startActivity(openAgreementIntent)
        }
    }
}




