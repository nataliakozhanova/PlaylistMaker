package com.practicum.playlistmaker.playlist.data.storage

import java.io.File
import java.io.InputStream

interface FilesStorageApi {
    fun saveFile(inputStream: InputStream, externalDir: File): String
}