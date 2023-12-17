package com.practicum.playlistmaker


import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CustomDateTypeAdapter : TypeAdapter<Date>() {

    companion object {
        const val FORMAT_PATTERN = "yyyy-MM-DD'T'hh:mm:ss'Z'"
    }

    private val formatter = SimpleDateFormat(FORMAT_PATTERN, Locale.getDefault())
    override fun write(out: JsonWriter?, value: Date) {
        out?.value(formatter.format(value))

    }

    override fun read(`in`: JsonReader?): Date {
        return formatter.parse(`in`?.nextString())
    }


}
