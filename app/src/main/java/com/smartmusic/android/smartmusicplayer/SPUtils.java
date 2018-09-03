package com.smartmusic.android.smartmusicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

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


    public static Typeface getHeaderTypeface(Context context){
        Typeface tf = ResourcesCompat.getFont(context, R.font.exo_medium);
        return tf;
    }

    public static Typeface getSubtextTypeface(Context context){
        Typeface tf = ResourcesCompat.getFont(context, R.font.comfortaa_light);
        return tf;
    }

    public static Typeface getTitleTypeface(Context context){
        Typeface tf = ResourcesCompat.getFont(context, R.font.archivo_black);
        return tf;
    }

    public static Animation getSlideInLeftAnimation(Context context){
        Animation inL = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        return inL;
    }

    public static Animation getSlideOutRightAnimation(Context context){
        Animation outR = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
        return outR;
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
