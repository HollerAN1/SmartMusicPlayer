package com.smartmusic.android.smartmusicplayer.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.SPMainActivity;
import com.smartmusic.android.smartmusicplayer.SongEvent;
import com.smartmusic.android.smartmusicplayer.SongEventListener;
import com.smartmusic.android.smartmusicplayer.database.SPRepository;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.nowplaying.NowPlaying;
import com.smartmusic.android.smartmusicplayer.R;
import com.squareup.picasso.Picasso;


public class Library extends Fragment implements SongEventListener {

    View mainView = null;

    /*
      A Handler allows you to send and process Message and Runnable
      objects associated with a thread's MessageQueue. Each Handler
      instance is associated with a single thread and that thread's
      message queue. When you create a new Handler, it is bound to
      the thread / message queue of the thread that is creating it
      -- from that point on, it will deliver messages and runnables
      to that message queue and execute them as they come out of the
      message queue.

       There are two main uses for a Handler: (1) to schedule messages
       and runnables to be executed as some point in the future; and
       (2) to enqueue an action to be performed on a different thread
       than your own.
     */
    private Handler myHandler = new Handler();

    private LibraryPagerAdapter mlibraryPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private static View nowPlayingBar;

    /*NowPlayingBar child views*/
    private TextView nowPlayingSongName;
    private TextView nowPlayingArtistName;
    private ImageView nowPlayingAlbumArt;
    private FloatingActionButton nowPlayingActionButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.LIBRARY));
        setRetainInstance(true);

        if( mainView == null ) {
            // Inflate the layout for this fragment
            mainView = inflater.inflate(R.layout.fragment_library, container, false);

//            setUpRecyclerView(mainView);

            // Checks user permissions then loads
            // songs into an ArrayList to be passed
            // into an adapter.
//            checkUserPermission();

            // ViewPager and its adapters use support library
            // fragments, so use getSupportFragmentManager.
            mlibraryPagerAdapter =
                    new LibraryPagerAdapter(
                            getFragmentManager(), getContext());

            mViewPager = (ViewPager) mainView.findViewById(R.id.library_view_pager);
            mTabLayout = (TabLayout) mainView.findViewById(R.id.library_tab_layout);
            mTabLayout.setupWithViewPager(mViewPager, true);
            mViewPager.setAdapter(mlibraryPagerAdapter);

            initPlayingBar(mainView);

            SPMainActivity.getSongEventHandler().addSongEventListener(this);
        }

        return mainView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SPMainActivity.getSongEventHandler().removeSongEventListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SPMainActivity.mPlayerService != null) {
            updateNowPlayingBar(SPMainActivity.mPlayerService.getCurrentSong());
        }
    }

    private void initPlayingBar(View mainView){
        nowPlayingBar = mainView.findViewById(R.id.includedNowPlayingLayout);
        nowPlayingSongName = (TextView) mainView.findViewById(R.id.now_playing_small_songName);
        nowPlayingArtistName = (TextView) mainView.findViewById(R.id.now_playing_small_artistName);
        nowPlayingAlbumArt = (ImageView) mainView.findViewById(R.id.image_album_art);
        nowPlayingActionButton = (FloatingActionButton) mainView.findViewById(R.id.now_playing_small_play_button);



        if(SPMainActivity.mPlayerService == null || !SPMainActivity.mPlayerService.isSongPlaying()) {
            nowPlayingBar.animate().translationY(nowPlayingBar.getHeight())
                    .alpha(0.0f)
                    .setDuration(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            nowPlayingBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            nowPlayingBar.setVisibility(View.VISIBLE);
        }


        nowPlayingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView smallName = (TextView)getView().findViewById(R.id.now_playing_small_songName);
                final TextView smallArtist = (TextView)getView().findViewById(R.id.now_playing_small_artistName);
                final ImageView smallAlbumArt = (ImageView)getView().findViewById(R.id.image_album_art);
                final ImageView smallPlayButton = (FloatingActionButton)getView().findViewById(R.id.now_playing_small_play_button);

                NowPlaying nowPlayingFrag = (NowPlaying)getActivity().getSupportFragmentManager().findFragmentByTag(getString(R.string.NOW_PLAYING_TAG));

                //Make sure Now Playing is instantiated
                if(nowPlayingFrag == null){
                    nowPlayingFrag = new NowPlaying();
                }

                ViewCompat.setTransitionName(smallName, getString(R.string.song_title_transition_name));
                ViewCompat.setTransitionName(smallArtist, getString(R.string.artist_name_transition_name));
                ViewCompat.setTransitionName(smallPlayButton, getString(R.string.play_button_transition_name));
                ViewCompat.setTransitionName(smallAlbumArt, getString(R.string.album_art_transition_name));

                // Fade out the old fragment
//                Fade exitFade = new Fade();
//                exitFade.setDuration(3000);
//                setExitTransition(exitFade);

                TransitionSet enterTransitionSet = new TransitionSet();
                enterTransitionSet.addTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
                enterTransitionSet.setDuration(1000);
//                enterTransitionSet.setStartDelay(FADE_DEFAULT_TIME);
                nowPlayingFrag.setSharedElementEnterTransition(enterTransitionSet);
//                nowPlayingFrag.setSharedElementReturnTransition(enterTransitionSet);


                nowPlayingFrag.setEnterTransition(new Slide(Gravity.BOTTOM).setDuration(1000));
                nowPlayingFrag.setExitTransition(new Slide(Gravity.BOTTOM).setDuration(1000));
//                ChangeBounds cb = new ChangeBounds();

                // Defines enter transition only for shared element
//                ChangeBounds changeBoundsTransition = (ChangeBounds)TransitionInflater.from(getContext()).inflateTransition(R.transition.change_bounds);
//                ChangeBounds changeBoundsTransition = new ChangeBounds();
//                nowPlayingFrag.setSharedElementEnterTransition(changeBoundsTransition);

                // Prevent transitions for overlapping
                nowPlayingFrag.setAllowEnterTransitionOverlap(true);
                nowPlayingFrag.setAllowReturnTransitionOverlap(true);


                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm
                        .beginTransaction()
                        .addSharedElement(smallPlayButton, getString(R.string.play_button_transition_name))
                        .addSharedElement(smallName, getString(R.string.song_title_transition_name))
                        .addSharedElement(smallArtist, getString(R.string.artist_name_transition_name))
                        .addSharedElement(smallAlbumArt, getString(R.string.album_art_transition_name))
                        .replace(R.id.fragment_container, nowPlayingFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });

        nowPlayingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayBtnClick(v);
            }
        });
    }

    public static void hideNowPlayingBar(){
        nowPlayingBar.animate().translationY(nowPlayingBar.getHeight())
                .alpha(0.0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        nowPlayingBar.setVisibility(View.GONE);
                    }
                });
    }

    public static void showNowPlayingBar(){
        nowPlayingBar.animate().translationY(0)
                .alpha(1.0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationEnd(animation);
                        nowPlayingBar.setVisibility(View.VISIBLE);
                    }
                });
    }

    public static void toggleNowPlayingBar(){
        if(SPMainActivity.mPlayerService.isSongPlaying()){
//        if(nowPlayingBar.getVisibility() == View.GONE){
            nowPlayingBar.animate().translationY(0)
                    .alpha(1.0f)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationEnd(animation);
                            nowPlayingBar.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            nowPlayingBar.animate().translationY(nowPlayingBar.getHeight())
                    .alpha(0.0f)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            nowPlayingBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    public void updateNowPlayingBar(Song s){
        if(s != null && nowPlayingBar != null) {
            nowPlayingSongName.setText(s.getSongName());
            nowPlayingArtistName.setText(s.getArtistName());


            Uri uri = s.getAlbumArt();
            Picasso.with(getContext())
                    .load(uri)
                    .placeholder(R.drawable.temp_album_art)
                    .error(R.drawable.temp_album_art)
                    .into(nowPlayingAlbumArt);

            if(SPMainActivity.mPlayerService.isSongPlaying()){
                nowPlayingActionButton.setSelected(true);
            } else {
                nowPlayingActionButton.setSelected(false);
            }
        }
//        toggleNowPlayingBar();

    }



//    private void checkUserPermission(){
//        if(Build.VERSION.SDK_INT>=23){
//            if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED){
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
//                return;
//            } else if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED){
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 456);
//            }
//        }
//        loadSongs();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            case 123:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    loadSongs();
//                }else{
//                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
//                    checkUserPermission();
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        }
//
//    }

//    private void loadSongs(){
//        /*Uniform Resource Identifier (URI)
//        A Uri object is usually used to tell a ContentProvider what
//        we want to access by reference. It is an immutable one-to-one
//         mapping to a resource or data. The method Uri.parse creates
//          a new Uri object from a properly formated String.
//        * */
//        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; //locates media
//        String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
//        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,null,null);
//        if(cursor != null){
//            if(cursor.moveToFirst()){
//                do{
//                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
//                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
//                    String track = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
//                    String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
//                    String year = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
//                    String album= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
//
//                    Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
//                    Uri albumArt = ContentUris.withAppendedId(albumArtUri, cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID)));
//
//
//                    Song s = new Song(name,artist, album, url, albumArt);
//                    _songs.add(s);
//
//
//                    updateArtists(s);
//                    updateAlbums(s);
//
//                }while (cursor.moveToNext());
//            }
//
//            cursor.close();
//
//        }
//    }

    /**
     * Controls the small button in the bar
     * at the bottom of the screen
     * @param v The view
     */
    public void onPlayBtnClick(View v){
        if(SPMainActivity.mPlayerService.isMediaPlayerSet()){
            if(v.isSelected()){
                SPMainActivity.mPlayerService.pause();
                v.setSelected(false);
            }
            else{
                SPMainActivity.mPlayerService.resume();
                v.setSelected(true);
            }
        }
    }

//    private void updateArtists(Song s){
//        for(ArtistModel a : _artists){
//            if(a.getArtistName().equals(s.getArtistName())){
//                a.addSong(s);
//                return;
//            }
//        }
//        ArtistModel artist = new ArtistModel(s.getArtistName());
//        artist.addSong(s);
//        _artists.add(artist);
//    }

//    private void updateAlbums(Song s){
//        for(AlbumModel a : _albums){
//            if(a.getAlbumName().equals(s.getAlbumname())){
//                a.addSong(s);
//                return;
//            }
//        }
//        String artistname;
//        if(s.getAlbumname().equals("<unknown>")){
//            artistname = "<unknown>";
//        } else {
//            artistname = s.getArtistName();
//        }
//        AlbumModel al = new AlbumModel(artistname, s.getAlbumname(), s.getAlbumArt());
//        al.addSong(s);
//        _albums.add(al);
//    }

    @Override
    public void onSongChangeEvent(SongEvent e) {
        updateNowPlayingBar(e.getSource());
        showNowPlayingBar();
    }

    @Override
    public void onShuffleOnEvent(SongEvent e) {}

    @Override
    public void onShuffleOffEvent(SongEvent e) {}

    @Override
    public void onSongStopEvent(SongEvent e) {
        hideNowPlayingBar();
    }

    @Override
    public void onSongAddedEvent(SongEvent e) {

    }

    @Override
    public void onSongRemovedEvent(SongEvent e) {

    }
}



