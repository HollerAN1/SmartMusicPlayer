package com.smartmusic.android.smartmusicplayer;

import java.text.SimpleDateFormat;

/**
 * A utility class with static
 * methods that can be used throughout
 * the app.
 */
public class SPUtils {

    public static SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyyMMdd");


    /**
     * Converts millisecond time to minutes:seconds
     * @param milliseconds the number of milliseconds
     * @return
     */
    public static String milliToTime(int milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
}
