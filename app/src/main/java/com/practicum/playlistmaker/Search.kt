package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Search : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var clearSearchBar: ImageView
    private var searchRequest: String = SEARCH_REQUEST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        inputEditText = findViewById(R.id.search_input)
        clearSearchBar = findViewById(R.id.clearButton)

        findViewById<ImageView>(R.id.returnmenu).setOnClickListener {
            finish()
        }

        clearSearchBar.setOnClickListener {
            inputEditText.text.clear()
            hideKeyboard()
        }

        inputEditText.requestFocus()
        showKeyboard()

        setupTextWatcher()
        inputEditText.setText(searchRequest)
        updateClearButtonVisibility(searchRequest)

        val trackList: MutableList<Track> = mutableListOf(
            Track(
                "Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Billie Jean",
                "Michael Jackson",
                "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            ),
            Track(
                "Stayin' Alive",
                "Bee Gees",
                "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            ),
            Track(
                "Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )

        // Настройка RecyclerView
        val recycleViewTrack = findViewById<RecyclerView>(R.id.recycleViewTrack)
        recycleViewTrack.layoutManager = LinearLayoutManager(this)
        val trackAdapter = TrackAdapter(trackList)
        recycleViewTrack.adapter = trackAdapter
    }

    private fun setupTextWatcher() {
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                           }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRequest = s.toString()
                updateClearButtonVisibility(searchRequest)
            }

            override fun afterTextChanged(s: Editable?) {
                            }
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
