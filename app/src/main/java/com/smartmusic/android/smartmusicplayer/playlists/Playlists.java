package com.smartmusic.android.smartmusicplayer.playlists;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartmusic.android.smartmusicplayer.R;
import com.smartmusic.android.smartmusicplayer.SPMainActivity;


public class Playlists extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlists, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        SPMainActivity parentActivity = ((SPMainActivity)getActivity());
        if( parentActivity != null ){
            parentActivity.setActionBarTitle(R.string.PLAYLISTS);
        }
    }
}
