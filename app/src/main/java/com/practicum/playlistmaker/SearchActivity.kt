package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

const val SEARCH_HISTORY_PREFERENCES = "search_history_preferences"

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

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
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var textToSave: String = ""

    private var isClickAllowed = true
    private val searchRunnable = Runnable { search() }
    private val handler = Handler(Looper.getMainLooper())

    lateinit var searchHistory: SearchHistory
    lateinit var searchHistoryAdapter: TracksAdapter
    lateinit var tracksAdapter: TracksAdapter

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_TO_SAVE, textToSave)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textToSave = savedInstanceState.getString(TEXT_TO_SAVE, "")
        binding.searchEditText.setText(textToSave)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchHistory =
            SearchHistory(getSharedPreferences(SEARCH_HISTORY_PREFERENCES, MODE_PRIVATE))
        searchHistoryAdapter = TracksAdapter(searchHistory.tracksSearchHistory, layoutInflater) {
            if (clickDebounce()) {
                openPlayer(it)
            }
        }
        tracksAdapter = TracksAdapter(tracks, layoutInflater) {
            searchHistory.addToSearchHistory(it)
            if (clickDebounce()) {
                openPlayer(it)
            }
        }

        binding.searchToolbar.setNavigationOnClickListener {
            finish()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounce()
                if (binding.searchEditText.hasFocus() && s.isNullOrEmpty() && searchHistory.tracksSearchHistory.isNotEmpty()) {
                    binding.searchHistoryContainer.visibility = ViewGroup.VISIBLE
                    showSearchHistory()
                } else {
                    binding.searchHistoryContainer.visibility = ViewGroup.GONE
                    hideSearchError()
                }
                binding.searchClearButton.visibility =
                    clearButtonVisibility(binding.searchEditText.editableText)
                textToSave = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }

        }
        binding.searchEditText.addTextChangedListener(simpleTextWatcher)

        binding.searchEditText.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus && binding.searchEditText.text.isEmpty() && searchHistory.tracksSearchHistory.isNotEmpty()) {
                binding.searchHistoryContainer.visibility = ViewGroup.VISIBLE
                showSearchHistory()
            } else {
                binding.searchHistoryContainer.visibility = ViewGroup.GONE
                hideSearchError()
            }
        }

        binding.deleteSearchHistoryButton.setOnClickListener {
            searchHistory.deleteSearchHistory()
            searchHistoryAdapter.notifyDataSetChanged()
            binding.searchHistoryContainer.visibility = ViewGroup.GONE
            hideSearchError()
        }

        binding.searchClearButton.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(
                binding.searchEditText.windowToken,
                0
            )
            binding.searchEditText.setText("")
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            binding.searchClearButton.visibility = View.GONE
            hideSearchError()
        }

        setupTracks()

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        binding.updateButton.setOnClickListener {
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
        binding.tracksRecyclerView.adapter = tracksAdapter
    }

    private fun showSearchError() {
        binding.searchErrorContainer.visibility = ViewGroup.VISIBLE
        binding.errorSearchImage.visibility = View.VISIBLE
        binding.errorUnionBigImage.visibility = View.VISIBLE
        binding.errorUnionSmallImage.visibility = View.VISIBLE
        binding.errorInternetImage.visibility = View.GONE
        binding.searchErrorTextView.visibility = View.VISIBLE
        binding.updateButton.visibility = View.GONE
        binding.searchErrorTextView.text = getString(R.string.nothing_found)
        tracks.clear()
        tracksAdapter.notifyDataSetChanged()
    }

    private fun showInternetError() {
        binding.searchErrorContainer.visibility = ViewGroup.VISIBLE
        binding.errorInternetImage.visibility = View.VISIBLE
        binding.updateButton.visibility = View.VISIBLE
        binding.errorSearchImage.visibility = View.GONE
        binding.searchErrorTextView.visibility = View.VISIBLE
        binding.errorUnionBigImage.visibility = View.VISIBLE
        binding.errorUnionSmallImage.visibility = View.VISIBLE
        binding.searchErrorTextView.text =
            getString(R.string.connection_problems)
        tracks.clear()
        tracksAdapter.notifyDataSetChanged()
    }

    private fun hideSearchError() {
        binding.searchErrorContainer.visibility = ViewGroup.GONE
        binding.errorInternetImage.visibility = View.GONE
        binding.errorSearchImage.visibility = View.GONE
        binding.updateButton.visibility = View.GONE
        binding.searchErrorTextView.visibility = View.GONE
        binding.errorUnionBigImage.visibility = View.GONE
        binding.errorUnionSmallImage.visibility = View.GONE
    }

    private fun search() {
        hideSearchError()
        if (binding.searchEditText.text.isNotEmpty()) {
            binding.progressBar.visibility = View.VISIBLE
            hideSearchError()
            tracks.clear()
            iTunesService.search(binding.searchEditText.text.toString())
                .enqueue(object : Callback<ITunesResponse> {
                    override fun onResponse(
                        call: Call<ITunesResponse>,
                        response: Response<ITunesResponse>
                    ) {
                        binding.progressBar.visibility = View.GONE
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
                        binding.progressBar.visibility = View.GONE
                        showInternetError()
                    }

                })
        }
    }

    private fun showSearchHistory() {
        binding.searchHistoryRecyclerView.adapter = searchHistoryAdapter
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(
            Track.INTENT_KEY,
            track
        )
        startActivity(intent)
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}
