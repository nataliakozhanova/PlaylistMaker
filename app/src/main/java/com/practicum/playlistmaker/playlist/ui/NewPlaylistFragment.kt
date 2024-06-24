package com.practicum.playlistmaker.playlist.ui

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.playlist.presentation.view_model.NewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.InputStream

open class NewPlaylistFragment : Fragment() {

    protected lateinit var managedPlaylistViewModel: NewPlaylistViewModel

    private var _binding: FragmentNewPlaylistBinding? = null
    protected val binding get() = _binding!!

    protected var coverUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCreateViewModel()

        binding.createPlaylistButton.isEnabled = false

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.newPlaylistToolbar)
        binding.newPlaylistToolbar.setNavigationOnClickListener {
            onBackPressedNavigation()
        }

        val dispatcher = requireActivity().onBackPressedDispatcher
        dispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressedNavigation()
            }
        })

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                coverUri = uri
                if (uri != null) {
                    binding.addPhotoImageView.isVisible = false
                    binding.playListCoverImageView.isVisible = true
                    renderCover(uri.toString())
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.addPhotoImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.playListCoverImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }



        binding.createPlaylistButton.setOnClickListener {
            onSavePlaylistClick()
        }

        binding.playlistNameInputEditText.doOnTextChanged { text, _, _, _ ->
            binding.createPlaylistButton.isEnabled = !text.isNullOrEmpty()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createPlaylist() {
        val name = binding.playlistNameInputEditText.text.toString()
        val description = binding.playlistDescriptionInputEditText.text?.toString() ?: ""
        var inputStream: InputStream? = null
        val externalDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        coverUri?.let {inputStream = requireActivity().contentResolver.openInputStream(coverUri!!)  }
        managedPlaylistViewModel.savePlaylistInfo(name, description, inputStream, externalDir!!)
    }

    private fun showToast(playlistName: String) {
        val message = getString(R.string.toast_playlist_created, playlistName)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showDialog() {

        val isName = binding.playlistNameInputEditText.text.toString().isEmpty()
        val isDescription = binding.playlistDescriptionInputEditText.text.toString().isEmpty()
        if (isName && isDescription && coverUri == null) {
            findNavController().navigateUp()
        } else {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.playlist_dialog_title)
                .setMessage(R.string.playlist_dialog_message)
                .setNeutralButton(R.string.playlist_dialog_neutral) { dialog, which ->
                    dialog.dismiss()
                }.setPositiveButton(R.string.playlist_dialog_positive) { dialog, which ->
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                .create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
        }

    }

    protected open fun onCreateViewModel(){
        managedPlaylistViewModel = viewModel<NewPlaylistViewModel>().value
    }

    protected open fun onBackPressedNavigation() {
        showDialog()
    }

    protected open fun onSavePlaylistClick(){
        createPlaylist()
        val playlistName = binding.playlistNameInputEditText.text.toString()
        showToast(playlistName)
        findNavController().navigateUp()
    }

    protected fun renderCover(uri: String){
        val trackCornerRadius: Int =
            requireActivity().applicationContext.resources.getDimensionPixelSize(R.dimen.dp8)
        Glide.with(requireActivity().applicationContext)
            .load(uri)
            .transform(CenterCrop(), RoundedCorners(trackCornerRadius))
            .into(binding.playListCoverImageView)
    }
}