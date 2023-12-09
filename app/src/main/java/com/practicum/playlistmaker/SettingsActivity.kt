package com.practicum.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val THEME_KEY = "key_for_app_theme"

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        val settingsArrowBackButton = findViewById<Toolbar>(R.id.settings_toolbar)
        settingsArrowBackButton.setNavigationOnClickListener {
            finish()
        }

        val themeSwitcher = findViewById<Switch>(R.id.settings_switch_theme)

        configureSwitcherState(sharedPrefs)
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPrefs.edit()
                .putBoolean(THEME_KEY, checked)
                .apply()
        }

        val shareAppButton = findViewById<ImageView>(R.id.share_app_button)
        shareAppButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.link_share_app))
            startActivity(Intent.createChooser(shareIntent, ""))
        }
        val writeToSupportButton = findViewById<ImageView>(R.id.write_to_support_button)
        writeToSupportButton.setOnClickListener {
            val writeIntent = Intent(Intent.ACTION_SENDTO)
            writeIntent.data = Uri.parse("mailto:" + getString(R.string.to))
            writeIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject))
            writeIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message))
            startActivity(writeIntent)
        }
        val termsOfUseButton = findViewById<ImageView>(R.id.terms_of_use_button)
        termsOfUseButton.setOnClickListener {
            val browseIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_terms_of_usage)))
            startActivity(browseIntent)
        }

    }

    private fun configureSwitcherState(sharedPrefs: SharedPreferences) {
        val themeSwitcher = findViewById<Switch>(R.id.settings_switch_theme)
        themeSwitcher.setChecked(
            sharedPrefs.getBoolean(
                THEME_KEY,
                (applicationContext as App).isSystemDarkMode()
            )
        )
        if (sharedPrefs.contains(THEME_KEY)) {
            (applicationContext as App).switchTheme(sharedPrefs.getBoolean(THEME_KEY, false))
        }

    }


}