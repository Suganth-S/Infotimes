package com.suganth.infotimes.ui.db

import androidx.room.TypeConverter
import com.suganth.infotimes.ui.models.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String{
        return source.name
    }

    /**
     * whenever we have a string then we want to convert that String to our source class
     */
    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}