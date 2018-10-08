package com.smartmusic.android.smartmusicplayer.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.smartmusic.android.smartmusicplayer.R;
import com.smartmusic.android.smartmusicplayer.SPFragment;
import com.smartmusic.android.smartmusicplayer.utils.SPUtils;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class SearchResultsFragment extends SPFragment {

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
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {
                    SPUtils.closeKeyboard(getContext(), absListView); // Hide keyboard when user starts scrolling.
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                }
            });

        }
        return mainView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onDestroy();
    }

    @Override
    public void pressedBack() {
        super.pressedBack();
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
