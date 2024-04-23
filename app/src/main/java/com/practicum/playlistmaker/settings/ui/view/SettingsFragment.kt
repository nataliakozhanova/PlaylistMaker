package com.practicum.playlistmaker.settings.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.models.SettingsState
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SettingsViewModel> {
        parametersOf(
            getString(R.string.link_share_app),
            getString(R.string.link_terms_of_usage),
            getString(R.string.to),
            getString(R.string.subject),
            getString(R.string.message)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configureSwitcherState(settingsState: SettingsState) {
        binding.themeSwitcherSwitch.setChecked((settingsState as SettingsState.DarkMode).isDark)
    }

}