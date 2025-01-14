package com.practicum.playlistmaker

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        window.statusBarColor = ContextCompat.getColor(this, R.color.background)

                val isDarkTheme = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = if (isDarkTheme) {
                             0
            } else {
             View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        val returnButton: View = findViewById(R.id.returnFromAudioPlayer)
        val trackImage: ImageView = findViewById(R.id.trackImage)
        val trackName: TextView = findViewById(R.id.trackName)
        val artistName: TextView = findViewById(R.id.artistName)
        val trackTime: TextView = findViewById(R.id.trackTime)
        val collectionName: TextView = findViewById(R.id.collectionName)
        val releaseDate: TextView = findViewById(R.id.releaseDate)
        val primaryGenreName: TextView = findViewById(R.id.GenreName)
        val country: TextView = findViewById(R.id.country)

                returnButton.setOnClickListener {
            finish()
        }

        val trackJson = intent.getStringExtra("Track")
        if (trackJson == null) {
            Toast.makeText(this, "Ошибка: данные трека отсутствуют", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val track = Gson().fromJson(trackJson, Track::class.java)

                Glide.with(applicationContext)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        2F,
                        resources.displayMetrics
                    ).toInt()
                )
            )
            .into(trackImage)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)

        if (track.collectionName.isNullOrEmpty()) {
            collectionName.visibility = View.GONE
        } else {
            collectionName.text = track.collectionName
        }

        releaseDate.text = track.releaseDate.replaceAfter('-', "").replace("-", "")
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country
    }
}