package com.practicum.playlistmaker.presentation

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

const val THEME_PREFERENCES = "playlist_maker_preferences"
const val THEME_KEY = "key_for_app_theme"

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)

        binding.settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        configureSwitcherState(sharedPrefs)
        binding.themeSwitcherSwitch.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPrefs.edit()
                .putBoolean(THEME_KEY, checked)
                .apply()
        }

        binding.shareAppButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.link_share_app))
            startActivity(Intent.createChooser(shareIntent, ""))
        }

        binding.writeToSupportButton.setOnClickListener {
            val writeIntent = Intent(Intent.ACTION_SENDTO)
            writeIntent.data = Uri.parse("mailto:" + getString(R.string.to))
            writeIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject))
            writeIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message))
            startActivity(writeIntent)
        }

        binding.termsOfUseButton.setOnClickListener {
            val browseIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_terms_of_usage)))
            startActivity(browseIntent)
        }

    }

    private fun configureSwitcherState(sharedPrefs: SharedPreferences) {
        if (!sharedPrefs.contains(THEME_KEY)) {
            binding.themeSwitcherSwitch.setChecked((applicationContext as App).isSystemDarkMode())
        } else {
            binding.themeSwitcherSwitch.setChecked(sharedPrefs.getBoolean(THEME_KEY, false))
        }
    }
}