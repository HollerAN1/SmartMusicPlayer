package com.smartmusic.android.smartmusicplayer.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.smartmusic.android.smartmusicplayer.database.entities.Artist;

import java.text.SimpleDateFormat;

/**
 * A utility class with static
 * methods that can be used throughout
 * the app.
 */
public class SPUtils {

    public enum TSAnimationType {
        SLIDE, FADE
    }

    public static SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyyMMdd");


    /**
     * Converts millisecond time to minutes:seconds
     * @param milliseconds the number of milliseconds
     * @return the string time representation in the format minutes:seconds
     */
    public static String milliToTime(Integer milliseconds) {
        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours =         (milliseconds / (1000 * 60 * 60));
        int minutes =       (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds =       ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
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

    public static String getFormattedSongsAndAlbumsForArtist(Artist a){
        String songCountString = a.getNumSongs() + "";
        if(a.getNumSongs() == 1){
            songCountString = songCountString + " song";
        } else {
            songCountString = songCountString + " songs";
        }

        if(a.getNumAlbums() == 1){
            songCountString = songCountString + " | " + a.getNumAlbums() + " album";
        } else {
            songCountString = songCountString + " | " + a.getNumAlbums() + " albums";
        }

        return songCountString;
    }


    /**
     * Open the soft keyboard
     */
    public static void openKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
    /**
     * Closes the soft keyboard
     */
    public static void closeKeyboard(Context context, View v) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static Animation getSlideInLeftAnimation(Context context){
        return AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
    }

    public static Animation getSlideOutRightAnimation(Context context){
        return AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
    }

    public static Animation getFadeInAnimation(Context context){
        Animation inAnim = AnimationUtils.loadAnimation(context,
                android.R.anim.fade_in);
        inAnim.setDuration(200);
        return inAnim;
    }

    public static Animation getFadeOutAnimation(Context context){
        Animation outAnim = AnimationUtils.loadAnimation(context,
                android.R.anim.fade_out);
        outAnim.setDuration(200);
        return outAnim;
    }
}
