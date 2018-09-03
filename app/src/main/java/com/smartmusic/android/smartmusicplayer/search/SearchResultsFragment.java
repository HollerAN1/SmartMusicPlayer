package com.smartmusic.android.smartmusicplayer.search;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.smartmusic.android.smartmusicplayer.R;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class SearchResultsFragment extends android.support.v4.app.Fragment {

    View mainView = null;
    StickyListHeadersListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if( mainView == null ) {
            // Inflate the layout for this fragment
            mainView = inflater.inflate(R.layout.search_results_layout, container, false);
            listView = mainView.findViewById(R.id.search_results_list_view);

            SearchResultsAdapter searchAdapter = new SearchResultsAdapter(getActivity());
            listView.setAdapter(searchAdapter);

        }
        return mainView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onDestroy();
    }
}
