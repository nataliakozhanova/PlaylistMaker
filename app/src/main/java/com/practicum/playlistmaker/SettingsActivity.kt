package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar


class SettingsActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val settingsArrowBackButton = findViewById<Toolbar>(R.id.settings_toolbar)
        settingsArrowBackButton.setNavigationOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
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
            //writeIntent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.to))
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

}