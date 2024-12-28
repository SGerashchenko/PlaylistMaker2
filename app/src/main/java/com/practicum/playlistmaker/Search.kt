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

        // Назад в главное меню
        findViewById<ImageView>(R.id.returnmenu).setOnClickListener {
            finish() //
        }

        // Кнопка очистки с фокусом и скрытием клавиатуры
        clearSearchBar.setOnClickListener {
            inputEditText.text.clear()
            hideKeyboard() //
        }

        inputEditText.requestFocus()
        showKeyboard()

        setupTextWatcher()

        inputEditText.setText(searchRequest)
        updateClearButtonVisibility(searchRequest)
    }


    private fun setupTextWatcher() {
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не используется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRequest = s.toString()
                updateClearButtonVisibility(searchRequest)
            }

            override fun afterTextChanged(s: Editable?) {
                // Не используется
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