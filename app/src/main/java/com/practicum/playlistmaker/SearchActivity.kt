package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)
    val tracks = ArrayList<Track>()

    companion object {
        private const val TEXT_TO_SAVE = "TEXT_TO_SAVE"
    }

    private var textToSave: String = ""

    var tracksAdapter = TracksAdapter(tracks)
    lateinit var inputEditText: EditText
    lateinit var searchArrowBackButton: Toolbar
    lateinit var clearButton: ImageView

    lateinit var errorUnionBigIV: ImageView
    lateinit var errorSearchIV: ImageView
    lateinit var errorInternetIV: ImageView
    lateinit var errorUnionSmallIV: ImageView
    lateinit var errorTextPlaceholderTV: TextView
    lateinit var updateButton: Button
    lateinit var errorContainerLL: LinearLayout

    override

    fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_TO_SAVE, textToSave)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textToSave = savedInstanceState.getString(TEXT_TO_SAVE, "")
        inputEditText.setText(textToSave)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchArrowBackButton = findViewById(R.id.toolbar)
        inputEditText = findViewById(R.id.input_edit_text)
        clearButton = findViewById(R.id.clear_button)

        errorUnionBigIV = findViewById<ImageView>(R.id.error_union_big)
        errorSearchIV = findViewById<ImageView>(R.id.error_search)
        errorInternetIV = findViewById<ImageView>(R.id.error_internet)
        errorUnionSmallIV = findViewById<ImageView>(R.id.error_union_small)
        errorTextPlaceholderTV = findViewById<TextView>(R.id.error_text_placeholder)
        updateButton = findViewById<Button>(R.id.update_button)
        errorContainerLL = findViewById(R.id.error_container)

        searchArrowBackButton.setNavigationOnClickListener {
            finish()
        }


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
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            clearButton.visibility = View.GONE
        }

        setupTracks()

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }
        updateButton.setOnClickListener{
            errorContainerLL.visibility = View.GONE
            errorInternetIV.visibility = View.GONE
            errorTextPlaceholderTV.visibility = View.GONE
            updateButton.visibility = View.GONE
            search()
        }
    }


    private fun clearButtonVisibility(editable: Editable?): Int {
        return if (editable.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun setupTracks() {
        val rvTracks = findViewById<RecyclerView>(R.id.tracks_recycler_view)
        rvTracks.adapter = tracksAdapter
    }

    private fun showSearchError(text: String) {
        if (text.isNotEmpty()) {
            errorUnionBigIV.visibility = View.VISIBLE
            errorSearchIV.visibility = View.VISIBLE
            errorUnionSmallIV.visibility = View.VISIBLE
            errorTextPlaceholderTV.visibility = View.VISIBLE
            errorContainerLL.visibility = View.VISIBLE
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            errorTextPlaceholderTV.text = text

        } else {
            errorContainerLL.visibility = View.GONE
            errorSearchIV.visibility = View.GONE
            errorTextPlaceholderTV.visibility = View.GONE
        }
    }

    private fun showInternetError() {
        errorUnionBigIV.visibility = View.VISIBLE
        errorInternetIV.visibility = View.VISIBLE
        errorUnionSmallIV.visibility = View.VISIBLE
        errorTextPlaceholderTV.visibility = View.VISIBLE
        updateButton.visibility = View.VISIBLE
        tracks.clear()
        tracksAdapter.notifyDataSetChanged()
        errorTextPlaceholderTV.text = getString(R.string.connection_problems)
    }

    private fun search() {
        iTunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>,
                    response: Response<ITunesResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.clear()
                                tracks.addAll(response.body()?.results!!)
                                tracksAdapter.notifyDataSetChanged()
                                showSearchError("")
                            } else {

                                showSearchError(getString(R.string.nothing_found))
                            }

                        }

                        else -> {
                            showInternetError()
                        }
                    }

                }

                override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                    showInternetError()
                }

            })
    }

}
