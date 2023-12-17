package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
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
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

const val SEARCH_HISTORY_PREFERENCES = "search_history_preferences"

class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .registerTypeAdapter(Date::class.java, CustomDateTypeAdapter())
                    .create()
            )
        )
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)
    val tracks = ArrayList<Track>()

    companion object {
        private const val TEXT_TO_SAVE = "TEXT_TO_SAVE"
    }

    private var textToSave: String = ""

    lateinit var searchHistory: SearchHistory
    lateinit var searchHistoryAdapter: TracksAdapter
    lateinit var tracksAdapter: TracksAdapter

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

    lateinit var searchHistoryContainerLL: LinearLayout
    lateinit var deleteHistoryButton: Button

    override fun onSaveInstanceState(outState: Bundle) {
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

        searchHistory =
            SearchHistory(getSharedPreferences(SEARCH_HISTORY_PREFERENCES, MODE_PRIVATE))
        searchHistoryAdapter = TracksAdapter(searchHistory.tracksSearchHistory) {
            openPlayer(it)
        }
        tracksAdapter = TracksAdapter(tracks) {
            searchHistory.addToSearchHistory(it)
            openPlayer(it)
        }


        searchArrowBackButton = findViewById(R.id.toolbar)
        inputEditText = findViewById(R.id.input_edit_text)
        clearButton = findViewById(R.id.clear_button)

        errorUnionBigIV = findViewById(R.id.error_union_big)
        errorSearchIV = findViewById(R.id.error_search)
        errorInternetIV = findViewById(R.id.error_internet)
        errorUnionSmallIV = findViewById(R.id.error_union_small)
        errorTextPlaceholderTV = findViewById(R.id.error_text_placeholder)
        updateButton = findViewById(R.id.update_button)
        errorContainerLL = findViewById(R.id.error_container)

        searchArrowBackButton.setNavigationOnClickListener {
            finish()
        }

        searchHistoryContainerLL = findViewById(R.id.search_history_container)
        deleteHistoryButton = findViewById(R.id.delete_history_button)

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (inputEditText.hasFocus() && s.isNullOrEmpty() && searchHistory.tracksSearchHistory.isNotEmpty()) {
                    searchHistoryContainerLL.visibility = ViewGroup.VISIBLE
                    showSearchHistory()
                } else {
                    searchHistoryContainerLL.visibility = ViewGroup.GONE
                }
                clearButton.visibility = clearButtonVisibility(inputEditText.editableText)
                textToSave = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }

        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus && inputEditText.text.isEmpty() && searchHistory.tracksSearchHistory.isNotEmpty()) {
                searchHistoryContainerLL.visibility = ViewGroup.VISIBLE
                showSearchHistory()
            } else {
                searchHistoryContainerLL.visibility = ViewGroup.GONE
            }
        }

        deleteHistoryButton.setOnClickListener {
            searchHistory.deleteSearchHistory()
            searchHistoryAdapter.notifyDataSetChanged()
            searchHistoryContainerLL.visibility = ViewGroup.GONE
        }

        clearButton.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.setText("")
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            clearButton.visibility = View.GONE
            hideSearchError()
            hideInternetError()
        }

        setupTracks()

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        updateButton.setOnClickListener {
            search()
        }


    }

    override fun onStop() {
        super.onStop()
        searchHistory.writeSearchHistory()
    }


    private fun clearButtonVisibility(editable: Editable?): Int {
        return if (editable.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun setupTracks() {
        val tracksRV = findViewById<RecyclerView>(R.id.tracks_recycler_view)
        tracksRV.adapter = tracksAdapter
    }

    private fun showSearchError() {
        errorUnionBigIV.visibility = View.VISIBLE
        errorSearchIV.visibility = View.VISIBLE
        errorUnionSmallIV.visibility = View.VISIBLE
        errorTextPlaceholderTV.visibility = View.VISIBLE
        errorContainerLL.visibility = View.VISIBLE
        tracks.clear()
        tracksAdapter.notifyDataSetChanged()
        errorTextPlaceholderTV.text = getString(R.string.nothing_found)
    }

    private fun hideSearchError() {
        errorContainerLL.visibility = View.GONE
        errorUnionBigIV.visibility = View.GONE
        errorSearchIV.visibility = View.GONE
        errorUnionSmallIV.visibility = View.GONE
        errorTextPlaceholderTV.visibility = View.GONE
    }

    private fun showInternetError() {
        errorContainerLL.visibility = View.VISIBLE
        errorUnionBigIV.visibility = View.VISIBLE
        errorInternetIV.visibility = View.VISIBLE
        errorUnionSmallIV.visibility = View.VISIBLE
        errorTextPlaceholderTV.visibility = View.VISIBLE
        updateButton.visibility = View.VISIBLE
        tracks.clear()
        tracksAdapter.notifyDataSetChanged()
        errorTextPlaceholderTV.text = getString(R.string.connection_problems)
    }

    private fun hideInternetError() {
        errorContainerLL.visibility = View.GONE
        errorUnionBigIV.visibility = View.GONE
        errorInternetIV.visibility = View.GONE
        errorUnionSmallIV.visibility = View.GONE
        errorTextPlaceholderTV.visibility = View.GONE
        updateButton.visibility = View.GONE
    }

    private fun search() {
        hideSearchError()
        hideInternetError()
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
                            } else {
                                showSearchError()
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

    private fun showSearchHistory() {
        val searchHistoryRV = findViewById<RecyclerView>(R.id.search_history_recycler_view)
        searchHistoryRV.adapter = searchHistoryAdapter
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(
            "track",
            track
        )
        startActivity(intent)
    }

}
