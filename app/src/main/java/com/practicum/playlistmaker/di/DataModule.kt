package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.practicum.playlistmaker.favorites.data.db.AppTrackDatabase
import com.practicum.playlistmaker.history.data.api.HistoryStorageApi
import com.practicum.playlistmaker.history.data.storage.HistoryStorageImpl
import com.practicum.playlistmaker.player.data.PlayerApiImpl
import com.practicum.playlistmaker.player.domain.api.PlayerApi
import com.practicum.playlistmaker.playlist.data.db.AppPlaylistsDatabase
import com.practicum.playlistmaker.playlist.data.storage.FilesStorageApi
import com.practicum.playlistmaker.playlist.data.storage.FilesStorageImpl
import com.practicum.playlistmaker.search.data.api.SearchApi
import com.practicum.playlistmaker.search.data.network.ITunesApi
import com.practicum.playlistmaker.search.data.network.SearchApiImpl
import com.practicum.playlistmaker.settings.data.api.SettingsStorageApi
import com.practicum.playlistmaker.settings.data.impl.SettingsStorageImpl
import com.practicum.playlistmaker.util.CustomDateTypeAdapter
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

const val PREFERENCES = "playlist_maker_preferences"

val dataModule = module {
    single {
        androidContext()
            .getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<SettingsStorageApi> {
        SettingsStorageImpl(get())
    }

    factory { MediaPlayer() }

    factory<PlayerApi> {
        PlayerApiImpl(get())
    }

    single<HistoryStorageApi> {
        HistoryStorageImpl(get())
    }

    single<ITunesApi> {

        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(Date::class.java, CustomDateTypeAdapter())
                        .create()
                )
            )
            .build()
            .create(ITunesApi::class.java)
    }

    single<SearchApi> {
        SearchApiImpl(get(), androidContext())
    }

    single<FilesStorageApi>{
        FilesStorageImpl()
    }

    single {
        Room.databaseBuilder(androidContext(), AppTrackDatabase::class.java, "database.db")
            .build()
    }

    single {
        Room.databaseBuilder(androidContext(), AppPlaylistsDatabase::class.java, "playlistsDatabase.db")
            .build()
    }

}