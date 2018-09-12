package com.smartmusic.android.smartmusicplayer.nowplaying;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

//import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.chibde.visualizer.BarVisualizer;
import com.smartmusic.android.smartmusicplayer.SPMainActivity;
import com.smartmusic.android.smartmusicplayer.SPUtils;
import com.smartmusic.android.smartmusicplayer.events.SongEvent;
import com.smartmusic.android.smartmusicplayer.events.SongPlaybackEventListener;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.R;
import com.smartmusic.android.smartmusicplayer.events.SongShuffleEventListener;
import com.squareup.picasso.Picasso;

import be.rijckaert.tim.animatedvector.FloatingMusicActionButton;
import jp.wasabeef.picasso.transformations.BlurTransformation;


public class NowPlaying extends Fragment implements SongPlaybackEventListener, SongShuffleEventListener {

    /*Views*/
//    private ImageView albumArt;
    private ImageView largeAlbumArt;
    private TextSwitcher songName;
    private TextSwitcher artistName;
    private FloatingMusicActionButton playButton;
    private SeekBar seekBar;
    private TextSwitcher progressCount;
    private TextSwitcher duration;
    private ImageView prevButton;
    private ImageView nextButton;

    // Album
    private ImageView albumArt;
    private ImageView favoriteGhost = null;
    private ImageView favoriteButton;
    private ImageView shuffleButton;

    /* Runnable that updates the current time in the song */
    private Runnable updateTimeRunnable;
    private Runnable favoriteGhostRunnable;

    private Handler mHandler = new Handler();

    private Song currentSong;
    private BarVisualizer visualizer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_now_playing, container, false);
        setRetainInstance(true);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().setTitle(R.string.NOW_PLAYING);

        SPMainActivity.getSongEventHandler().addSongPlaybackEventListener(this);
        SPMainActivity.getSongEventHandler().addSongShuffleEventListener(this);
        currentSong = SPMainActivity.mPlayerService.getCurrentSong();

        initNowPlaying(v);
        return v;
    }


    private void initNowPlaying(View v){
        if( !SPMainActivity.mPlayerService.isMediaPlayerSet() || currentSong == null){
            return;
        }

        // Initialize views
        if( v != null ) {
            largeAlbumArt = (ImageView) v.findViewById(R.id.now_playing_large_album_art);
            songName = (TextSwitcher) v.findViewById(R.id.now_playing_songName_textSwitcher);
            artistName = (TextSwitcher) v.findViewById(R.id.now_playing_artistName_textSwitcher);
            playButton = (FloatingMusicActionButton) v.findViewById(R.id.now_playing_play_button);
            seekBar = (SeekBar) v.findViewById(R.id.now_playing_seekBar);
            progressCount = (TextSwitcher) v.findViewById(R.id.now_playing_progress);
            duration = (TextSwitcher) v.findViewById(R.id.now_playing_duration);
            favoriteButton = (ImageView) v.findViewById(R.id.now_playing_favorite);
            prevButton = (ImageView) v.findViewById(R.id.now_playing_previous_button);
            nextButton = (ImageView) v.findViewById(R.id.now_playing_next_button);
            shuffleButton = (ImageView) v.findViewById(R.id.now_playing_shuffle);
//            visualizer = (BarVisualizer) v.findViewById(R.id.visualizer);
            setUpAlbumArt(v, currentSong);
        }

//        visualizer.setColor(ContextCompat.getColor(getContext(), R.color.pastel_rose));
//        visualizer.setDensity(70);

        // Setup song title view
        songName.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                // create a TextView
                TextView t = new TextView(getContext());

                t.setTextColor(getResources().getColor(R.color.pastel_rose));
                t.setTypeface(SPUtils.getHeaderTypeface(getContext()));
                t.setTextSize(26);
                t.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                t.setGravity(Gravity.CENTER);
                return t;
            }
        });

        // Setup song artist view
        artistName.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                // create a TextView
                TextView t = new TextView(getContext());

                t.setTextColor(Color.WHITE);
                t.setTypeface(SPUtils.getSubtextTypeface(getContext()));
                t.setTextSize(14);
                t.setAllCaps(true);
                t.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                t.setGravity(Gravity.CENTER);
                return t;
            }
        });

        // set the animation type to TextSwitcher
        songName.setInAnimation(SPUtils.getSlideInLeftAnimation(getContext()));
        songName.setOutAnimation(SPUtils.getSlideOutRightAnimation(getContext()));
        artistName.setInAnimation(SPUtils.getSlideInLeftAnimation(getContext()));
        artistName.setOutAnimation(SPUtils.getSlideOutRightAnimation(getContext()));


        progressCount.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                // create a TextView
                TextView t = new TextView(getContext());

                t.setTextColor(Color.WHITE);
                t.setTypeface(SPUtils.getSubtextTypeface(getContext()));
                t.setTextSize(18);
                return t;
            }
        });

        duration.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                // create a TextView
                TextView t = new TextView(getContext());

                t.setTextColor(Color.WHITE);
                t.setTypeface(SPUtils.getSubtextTypeface(getContext()));
                t.setTextSize(18);
                return t;
            }
        });

        progressCount.setInAnimation(SPUtils.getFadeInAnimation(getContext()));
        progressCount.setOutAnimation(SPUtils.getFadeOutAnimation(getContext()));
        duration.setInAnimation(SPUtils.getFadeInAnimation(getContext()));
        duration.setOutAnimation(SPUtils.getFadeOutAnimation(getContext()));

        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                if(!SPMainActivity.mPlayerService.isMediaPlayerSet()){
                    mHandler.postDelayed(this, 1000);
                    return;
                }

                if(SPMainActivity.mPlayerService.isSongPlaying()){
                    playButton.setSelected(true);
                    playButton.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY);
                } else {
                    playButton.setSelected(false);
                    playButton.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE);
                }

                int new_currPos = SPMainActivity.mPlayerService.getMediaPlayer().getCurrentPosition();
                int new_maxPos = SPMainActivity.mPlayerService.getMediaPlayer().getDuration();

                progressCount.setText(SPUtils.milliToTime(new_currPos));
                duration.setText(SPUtils.milliToTime(new_maxPos));
                seekBar.setProgress(new_currPos);


                mHandler.postDelayed(this, 1000);
            }
        };


        if(!SPMainActivity.mPlayerService.isSongPlaying()){
            playButton.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY);
        } else {
            playButton.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE);
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playButton.getCurrentMode() == FloatingMusicActionButton.Mode.PAUSE_TO_PLAY) {
                    SPMainActivity.mPlayerService.pause();
                    playButton.setSelected(false);
                    playButton.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE);
                } else {
                    SPMainActivity.mPlayerService.resume();
                    playButton.setSelected(true);
                    playButton.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY);
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( SPMainActivity.mPlayerService.isMediaPlayerSet() && fromUser){
                    SPMainActivity.mPlayerService.getMediaPlayer().seekTo(progress);
                    seekBar.setProgress(progress);
                    progressCount.setText(SPUtils.milliToTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(updateTimeRunnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(updateTimeRunnable);

                // update timer progress again
                mHandler.postDelayed(updateTimeRunnable, 1000);
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSong.getStats().setFavorited(!currentSong.getStats().isFavorited());

                favoriteGhost.setVisibility(View.VISIBLE);
                if(favoriteGhost.isSelected()){
                    favoriteGhost.setSelected(false);
                    favoriteButton.setSelected(false);
                } else {
                    favoriteGhost.setSelected(true);
                    favoriteButton.setSelected(true);
                }

                favoriteGhost.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.favorite_fade));
                favoriteGhost.animate().setDuration(4000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        favoriteGhost.setVisibility(View.GONE);
                    }
                }).start();
//                favoriteGhost.animate()
//                        .setDuration(3000)
//                        .start();
//                favoriteGhost.setVisibility(View.GONE);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                songName.setInAnimation(inL);
//                songName.setOutAnimation(outR);
//                artistName.setInAnimation(inL);
//                artistName.setOutAnimation(outR);

                SPMainActivity.mPlayerService.playNextSong();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                songName.setInAnimation(inL);
//                songName.setOutAnimation(outR);
//                artistName.setInAnimation(inL);
//                artistName.setOutAnimation(outR);

                SPMainActivity.mPlayerService.playPreviousSong();
            }
        });


        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPMainActivity.mPlayerService.setShuffle(shuffleButton.isSelected());
            }
        });

        updateNowPlaying();

        // After a second start updating the progress bar
        mHandler.postDelayed(updateTimeRunnable, 1000);
    }

    private void updateNowPlaying(){
        if(currentSong == null){
            return;
        }

        //Load large album image
        Picasso.with(getContext())
                .load(currentSong.getAlbumArt())
                .transform(new BlurTransformation(getContext(), 30))
                .error(R.drawable.galaxy)
                .into(largeAlbumArt);


        songName.setText(currentSong.getSongName());
        songName.setSelected(true);

        artistName.setText(currentSong.getArtistName());


        int maxPos = SPMainActivity.mPlayerService.getMediaPlayer().getDuration();
        int currPos = SPMainActivity.mPlayerService.getMediaPlayer().getCurrentPosition();

        seekBar.setMax(maxPos);
        seekBar.setProgress(currPos);
        seekBar.setDrawingCacheBackgroundColor(getResources().getColor(R.color.pastel_rose));
//        seekBar.setBackgroundColor(getResources().getColor(R.color.pastel_rose));


        progressCount.setText(SPUtils.milliToTime(currPos));
        duration.setText(SPUtils.milliToTime(maxPos));


        favoriteButton.setSelected(currentSong.getStats().isFavorited());
        shuffleButton.setSelected(SPMainActivity.mPlayerService.isShuffleOn());

//        int sessionId = SPMainActivity.mPlayerService.getMediaPlayer().getAudioSessionId();
//        if(sessionId != -1)
////        visualizer.setAudioSessionId(sessionId);
//        visualizer.setPlayer(sessionId);
//
//        // After a second start updating the progress bar
//        mHandler.postDelayed(updateTimeRunnable, 1000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SPMainActivity.getSongEventHandler().removeSongPlaybackEventListener(this);
        SPMainActivity.getSongEventHandler().removeSongShuffleEventListener(this);
    }

    public void collapseView(){
        mHandler.removeCallbacks(updateTimeRunnable);

//        FragmentManager fm = getActivity().getSupportFragmentManager();
//
//                fm
//                .beginTransaction()
//                .addSharedElement(playButton, getString(R.string.play_button_transition_name))
//                .addSharedElement(songName, getString(R.string.song_title_transition_name))
//                .addSharedElement(artistName, getString(R.string.artist_name_transition_name))
//                .addSharedElement(cardFrontFragment.getAlbumArt(), getString(R.string.album_art_transition_name))
//                .hide(this)
//                .show(((SPMainActivity)getActivity()).getLibraryFragment())
////                        .replace(R.id.fragment_container, nowPlaying, "nowPlaying")
//                .addToBackStack(null)
//                .commit();

        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void update(){
        updateNowPlaying();
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(updateTimeRunnable);
    }

    private void setUpAlbumArt(View view, final Song song){

        if( view != null ) {
            albumArt = (ImageView) view.findViewById(R.id.now_playing_album_art);
            favoriteGhost = (ImageView) view.findViewById(R.id.now_playing_favorite_ghost);
        }

        Uri uri = song == null ? null
                : song.getAlbumArt();

        Picasso.with(getContext())
                .load(uri)
                .placeholder(R.drawable.temp_album_art)
                .error(R.drawable.temp_album_art)
                .into(albumArt);

//            albumArt.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ((NowPlaying)getParentFragment()).flipCard();
//                }
//            });

        albumArt.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if(song == null) {
                        ((NowPlaying) getParentFragment()).getSongInfo().getStats().setFavorited(!song.getStats().isFavorited());
                    } else {
                        song.getStats().setFavorited(!song.getStats().isFavorited());
                    }
//                        ImageView favoriteButton = ((NowPlaying)getParentFragment()).favoriteButton;

                    favoriteGhost.setVisibility(View.VISIBLE);
                    if(favoriteGhost.isSelected()){
                        favoriteGhost.setSelected(false);
                        favoriteButton.setSelected(false);
                    } else {
                        favoriteGhost.setSelected(true);
                        favoriteButton.setSelected(true);
                    }

                    favoriteGhost.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.favorite_fade));
                    favoriteGhost.animate().setDuration(4000).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            favoriteGhost.setVisibility(View.GONE);
                        }
                    }).start();
                    return super.onDoubleTap(e);
                }

                @Override
                public boolean 	onSingleTapConfirmed(MotionEvent e){
//                        ((NowPlaying)getParentFragment()).flipCard();
                    return true;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        favoriteGhost.setSelected(
                song == null ? ((NowPlaying)getParentFragment()).getSongInfo().getStats().isFavorited()
                        : song.getStats().isFavorited() );

    }

    public Song getSongInfo(){
        return this.currentSong;
    }
    public ImageView getFavoriteButton(){
        return this.favoriteButton;
    }

    @Override
    public void onSongChangeEvent(SongEvent e) {
        currentSong = e.getSource();
        updateNowPlaying();
        setUpAlbumArt(null, e.getSource());
    }

    @Override
    public void onSongStopEvent(SongEvent e) {
        currentSong = null;
        updateNowPlaying();
        setUpAlbumArt(null, null);
    }

    @Override
    public void onShuffleOnEvent(SongEvent e) {
        shuffleButton.setSelected(true);
        shuffleButton.setColorFilter(getResources().getColor(R.color.pastel_rose));
    }

    @Override
    public void onShuffleOffEvent(SongEvent e) {
        shuffleButton.setSelected(false);
        shuffleButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }

} //Now Playing Activity

