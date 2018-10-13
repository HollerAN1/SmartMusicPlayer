package com.smartmusic.android.smartmusicplayer.utils;

import android.view.MotionEvent;

import com.smartmusic.android.smartmusicplayer.SPMainActivity;

/**
 * Class to handle Smart Player motion events repeated throughout code
 * Such as swipe and drag.
 */
public class SPMotionUtils {
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;


    public static boolean swipeChangeSong(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe Right
                        SPMainActivity.mPlayerService.playPreviousSong();
                    } else {
                        // Swipe Left
                        SPMainActivity.mPlayerService.playNextSong();
                    }
                    result = true;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
}
