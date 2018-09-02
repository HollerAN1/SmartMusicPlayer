package com.smartmusic.android.smartmusicplayer.database;

import android.arch.persistence.room.TypeConverter;

import java.sql.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
