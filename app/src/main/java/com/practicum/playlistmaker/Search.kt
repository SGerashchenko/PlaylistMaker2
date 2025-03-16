package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Search : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var clearSearchBar: ImageView
    private lateinit var recycleViewTrack: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var errorSearchLayout: LinearLayout
    private lateinit var errorSearchImage: ImageView
    private lateinit var errorSearchText: TextView
    private lateinit var updateSearchButton: Button
    private lateinit var searchHistoryTextView: TextView
    private lateinit var searchHistoryButton: Button
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var progressBar: ProgressBar

    private val tracks = ArrayList<Track>()
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService: iTunesApi = retrofit.create(iTunesApi::class.java)

    private var searchRequest: String = SEARCH_REQUEST
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }

    companion object {
        private const val APP_PREFERENCES = "app_preferences"
        private const val SEARCH_REQUEST = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        sharedPrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        inputEditText = findViewById(R.id.search_input)
        clearSearchBar = findViewById(R.id.clearButton)
        recycleViewTrack = findViewById(R.id.recycleViewTrack)
        errorSearchLayout = findViewById(R.id.errorSearchLayout)
        errorSearchImage = findViewById(R.id.errorSearchImage)
        errorSearchText = findViewById(R.id.errorSearchText)
        updateSearchButton = findViewById(R.id.updateSearchButton)
        searchHistoryTextView = findViewById(R.id.youSearchHistory)
        searchHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progressBar)

        recycleViewTrack.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(tracks)
        recycleViewTrack.adapter = trackAdapter

        findViewById<ImageView>(R.id.returnmenu).setOnClickListener {
            finish()
        }

        clearSearchBar.setOnClickListener {
            inputEditText.text.clear()
            hideKeyboard()
            errorSearchLayout.visibility = View.GONE
            showHistory()
        }

        updateSearchButton.setOnClickListener { search() }

        searchHistoryButton.setOnClickListener {
            SearchHistory(sharedPrefs).clearHistory()
            showHistory()
        }

        setupTextWatcher()

        inputEditText.setText(searchRequest)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            } else {
                false
            }
        }

        trackAdapter.onItemClick = { track ->
            if (clickDebounce()) {
                SearchHistory(sharedPrefs).addNewTrack(track)
                val audioPlayerIntent = Intent(this, AudioPlayer::class.java).apply {
                    putExtra("Track", Gson().toJson(track))
                }
                startActivity(audioPlayerIntent)
            }
        }
    }

    private fun setupTextWatcher() {
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearSearchBar.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchRequest = s.toString()
                handler.removeCallbacks(searchRunnable)

                // Проверяем, что строка не пустая и не состоит только из пробелов
                if (!s.isNullOrEmpty() && s.trim().isNotEmpty()) {
                    handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
                }
                if (s.isNullOrEmpty()) {
                    showHistory()
                } else {
                    searchHistoryTextView.visibility = View.GONE
                    searchHistoryButton.visibility = View.GONE
                    tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistory() {
        val history = SearchHistory(sharedPrefs).read()
        if (history.isNotEmpty()) {
            searchHistoryTextView.visibility = View.VISIBLE
            searchHistoryButton.visibility = View.VISIBLE
        } else {
            searchHistoryTextView.visibility = View.GONE
            searchHistoryButton.visibility = View.GONE
        }
        tracks.clear()
        tracks.addAll(history)
        trackAdapter.notifyDataSetChanged()
    }

    private fun search() {
        // Проверяем, что строка не пустая и не состоит только из пробелов
        if (inputEditText.text.trim().isEmpty()) return

        // Прячем заглушку перед новым поиском
        errorSearchLayout.visibility = View.GONE

        progressBar.visibility = View.VISIBLE
        iTunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                    progressBar.visibility = View.GONE
                    tracks.clear()
                    if (response.isSuccessful) {
                        response.body()?.results?.let {
                            if (it.isNotEmpty()) {
                                tracks.addAll(it)
                            } else {
                                showEmptyResultsLayout()
                            }
                        }
                    } else {
                        showError()
                    }
                    trackAdapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    showError()
                }
            })
    }

    private fun showEmptyResultsLayout() {
        errorSearchText.setText(R.string.errorsearchTextView)
        errorSearchImage.setImageResource(R.drawable.error__searchd)
        updateSearchButton.visibility = View.GONE
        errorSearchLayout.visibility = View.VISIBLE
    }

    private fun showError() {
        errorSearchLayout.visibility = View.VISIBLE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }
}

