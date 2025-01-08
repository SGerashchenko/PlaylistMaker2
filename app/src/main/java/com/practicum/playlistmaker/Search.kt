package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
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
    private var searchRequest: String = SEARCH_REQUEST

    private val tracks = ArrayList<Track>()
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService: iTunesApi = retrofit.create(iTunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        inputEditText = findViewById(R.id.search_input)
        clearSearchBar = findViewById(R.id.clearButton)
        recycleViewTrack = findViewById(R.id.recycleViewTrack)
        errorSearchLayout = findViewById(R.id.errorSearchLayout)
        errorSearchImage = findViewById(R.id.errorSearchImage)
        errorSearchText = findViewById(R.id.errorSearchText)
        updateSearchButton = findViewById(R.id.updateSearchButton)

        recycleViewTrack.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(tracks)
        recycleViewTrack.adapter = trackAdapter

        findViewById<ImageView>(R.id.returnmenu).setOnClickListener {
            finish()
        }

        clearSearchBar.setOnClickListener {
            inputEditText.text.clear()
            hideKeyboard()
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            errorSearchLayout.visibility = View.GONE
        }

        updateSearchButton.setOnClickListener { search() }

        inputEditText.setText(searchRequest)
        inputEditText.requestFocus()
        showKeyboard()

        setupTextWatcher()
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            } else {
                false
            }
        }
    }

    private fun setupTextWatcher() {
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRequest = s.toString()
                updateClearButtonVisibility(searchRequest)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateClearButtonVisibility(text: CharSequence?) {
        clearSearchBar.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }

    private fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputEditText.requestFocus()
        imm.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun search() {
        iTunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                    when (response.code()) {
                        200 -> {
                            response.body()?.results?.let {
                                if (it.isNotEmpty()) {
                                    errorSearchLayout.visibility = View.GONE
                                    tracks.clear()
                                    tracks.addAll(it)
                                    trackAdapter.notifyDataSetChanged()
                                } else {
                                    showEmptyResultsLayout()
                                }
                            }
                        }
                        else -> {
                            showErrorInternetLayout()
                        }
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    showErrorInternetLayout()
                }
            })
    }

    private fun showEmptyResultsLayout() {
        tracks.clear()
        trackAdapter.notifyDataSetChanged()
        errorSearchText.setText(R.string.errorsearchTextView)
        errorSearchImage.setImageResource(R.drawable.error__searchd)
        updateSearchButton.visibility = View.GONE
        errorSearchLayout.visibility = View.VISIBLE
    }

    private fun showErrorInternetLayout() {
        tracks.clear()
        trackAdapter.notifyDataSetChanged()
        errorSearchText.setText(R.string.errornetTextView)
        errorSearchImage.setImageResource(R.drawable.error_netd)
        updateSearchButton.visibility = View.VISIBLE
        errorSearchLayout.visibility = View.VISIBLE
    }

    companion object {
        private const val SEARCH_BAR = "SEARCH_BAR"
        private const val SEARCH_REQUEST = ""
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_BAR, searchRequest)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchRequest = savedInstanceState.getString(SEARCH_BAR, SEARCH_REQUEST).orEmpty()
    }
}
