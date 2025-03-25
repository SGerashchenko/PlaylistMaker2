package com.practicum.playlistmaker.ui.player

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var playerState = STATE_DEFAULT
    private var mainThreadHandler: Handler? = null

    @SuppressLint("SuspiciousIndentation")
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
        val startButton: ImageButton = findViewById(R.id.startButton)
        val listenedTime: TextView = findViewById(R.id.listenTime)

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

        // Инициализация MediaPlayer
        mediaPlayer = MediaPlayer()
        mainThreadHandler = Handler(Looper.getMainLooper())

        preparePlayer(track.previewUrl)

        startButton.setOnClickListener {
            playbackControl()

            mainThreadHandler?.postDelayed(
                object : Runnable {
                    override fun run() {
                        when (playerState) {
                            STATE_PLAYING -> {
                                listenedTime.text =
                                    SimpleDateFormat(
                                        "mm:ss",
                                        Locale.getDefault()
                                    ).format(mediaPlayer?.currentPosition ?: 0)
                                mainThreadHandler?.postDelayed(
                                    this,
                                    REFRESH_LISTENED_TIME_DELAY_MILLIS
                                )
                            }

                            STATE_DEFAULT, STATE_PREPARED -> {
                                listenedTime.setText(R.string.listenTime)
                                mainThreadHandler?.removeCallbacksAndMessages(null)
                            }

                            STATE_PAUSED -> mainThreadHandler?.removeCallbacksAndMessages(null)
                        }
                    }
                },
                REFRESH_LISTENED_TIME_DELAY_MILLIS
            )
        }
    }

    private fun preparePlayer(url: String) {
        mediaPlayer?.setDataSource(url)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            findViewById<ImageButton>(R.id.startButton).isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer?.setOnCompletionListener {
            findViewById<ImageButton>(R.id.startButton).setImageResource(R.drawable.play)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer?.start()
        findViewById<ImageButton>(R.id.startButton).setImageResource(R.drawable.audioplayerpausebutton)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer?.pause()
        findViewById<ImageButton>(R.id.startButton).setImageResource(R.drawable.play)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_LISTENED_TIME_DELAY_MILLIS = 500L
    }
}