package com.practicum.playlistmaker.favorites.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.favorites.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.favorites.presentation.models.FavoritesState
import kotlinx.coroutines.launch

class LibraryFavoritesViewModel(private val favoritesInteractor: FavoritesInteractor) :
    ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesState>()
    fun observeState(): LiveData<FavoritesState> = stateLiveData

    fun getFavorites() {
        viewModelScope.launch {
            favoritesInteractor
                .getAllFavorites()
                .collect { favoriteTracks ->
                    if (favoriteTracks.isEmpty()) {
                        renderFavoritesState(FavoritesState.Empty)
                    } else {
                        renderFavoritesState(FavoritesState.Content(favoriteTracks))
                    }
                }
        }
    }

    private fun renderFavoritesState(favoritesState: FavoritesState) {
        stateLiveData.postValue(favoritesState)
    }

}