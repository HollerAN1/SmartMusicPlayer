package com.smartmusic.android.smartmusicplayer;

import android.support.v4.app.Fragment;

public class SPFragment extends Fragment implements SPOnBackPressedListener {

    @Override
    public void onStart() {
        super.onStart();
        ((SPMainActivity)getActivity()).setOnBackPressedListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((SPMainActivity)getActivity()).setOnBackPressedListener(null);
    }

    @Override
    public void pressedBack() {

    }
}
