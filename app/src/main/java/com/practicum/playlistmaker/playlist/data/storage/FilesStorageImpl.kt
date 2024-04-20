package com.practicum.playlistmaker.playlist.data.storage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class FilesStorageImpl(): FilesStorageApi {
    override fun saveFile(inputStream: InputStream, externalDir: File): String {

            //создаём экземпляр класса File, который указывает на нужный каталог
            val filePath = File(externalDir, "playlist_maker_album")
            //создаем каталог, если он не создан
            if (!filePath.exists()){
                filePath.mkdirs()
            }
            //создаём экземпляр класса File, который указывает на файл внутри каталога
            val file = File(filePath, generateUniqueFileName())

            // создаём исходящий поток байтов в созданный выше файл
            val outputStream = FileOutputStream(file)
            // записываем картинку с помощью BitmapFactory
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.toUri().toString()
    }

    private fun generateUniqueFileName() : String{

        val uuid: UUID = UUID.randomUUID()

        return uuid.toString()
    }
}