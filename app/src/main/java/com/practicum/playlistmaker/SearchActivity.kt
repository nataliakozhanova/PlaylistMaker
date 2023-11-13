package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val TEXT_TO_SAVE = "TEXT_TO_SAVE"
    }

    private var textToSave: String = ""

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_TO_SAVE, textToSave)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textToSave = savedInstanceState.getString(TEXT_TO_SAVE, "")
        val inputEditText = findViewById<EditText>(R.id.input_edit_text)
        inputEditText.setText(textToSave)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val searchArrowBackButton = findViewById<Toolbar>(R.id.toolbar)

        searchArrowBackButton.setNavigationOnClickListener {
            finish()
        }


        val inputEditText = findViewById<EditText>(R.id.input_edit_text)
        val clearButton = findViewById<ImageView>(R.id.clear_button)

         val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(inputEditText.editableText)
                textToSave = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }

        }
            inputEditText.addTextChangedListener(simpleTextWatcher)

        clearButton.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.setText("")
            inputEditText.isCursorVisible = false
            clearButton.visibility = View.GONE
        }
    }




    private fun clearButtonVisibility(editable: Editable?): Int {
        return if (editable.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}
