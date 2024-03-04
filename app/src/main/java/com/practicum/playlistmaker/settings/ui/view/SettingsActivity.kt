package com.practicum.playlistmaker.settings.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.models.SettingsState
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.util.Creator

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this, SettingsViewModel.getViewModelFactory(
                Creator.getSharingInteractor(
                    getString(R.string.link_share_app),
                    getString(R.string.link_terms_of_usage),
                    getString(R.string.to),
                    getString(R.string.subject),
                    getString(R.string.message)
                ),
                Creator.getSettingsInteractor(getApplication())
            )
        )[SettingsViewModel::class.java]

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