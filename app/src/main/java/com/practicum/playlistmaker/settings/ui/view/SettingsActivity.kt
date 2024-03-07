package com.practicum.playlistmaker.settings.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.models.SettingsState
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel by viewModel<SettingsViewModel> {
        parametersOf(
            getString(R.string.link_share_app),
            getString(R.string.link_terms_of_usage),
                getString(R.string.to),
                getString(R.string.subject),
                getString(R.string.message)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        configureSwitcherState(viewModel.getState())

        binding.themeSwitcherSwitch.setOnCheckedChangeListener { switcher, checked ->
            viewModel.changeSettings(checked)
        }

        binding.shareAppButton.setOnClickListener {

            startActivity(viewModel.getSharingIntent())
        }

        binding.writeToSupportButton.setOnClickListener {

            startActivity(viewModel.getWriteToSupportIntent())
        }

        binding.termsOfUseButton.setOnClickListener {

            startActivity(viewModel.getTermsIntent())
        }

    }

    private fun configureSwitcherState(settingsState: SettingsState) {
        binding.themeSwitcherSwitch.setChecked((settingsState as SettingsState.DarkMode).isDark)
    }
}