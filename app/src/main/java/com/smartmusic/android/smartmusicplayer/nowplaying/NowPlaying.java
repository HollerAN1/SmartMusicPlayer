package com.smartmusic.android.smartmusicplayer.nowplaying;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.transition.ChangeBounds;
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
import com.smartmusic.android.smartmusicplayer.events.SongPlaybackEvent;
import com.smartmusic.android.smartmusicplayer.events.SongPlaybackEventListener;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.R;
import com.smartmusic.android.smartmusicplayer.events.SongShuffleEvent;
import com.smartmusic.android.smartmusicplayer.events.SongShuffleEventListener;
import com.squareup.picasso.Picasso;

import be.rijckaert.tim.animatedvector.FloatingMusicActionButton;
import jp.wasabeef.picasso.transformations.BlurTransformation;


public class NowPlaying extends Fragment implements SongPlaybackEventListener, SongShuffleEventListener {

    // Song info
    private TextSwitcher songName;
    private TextSwitcher artistName;

    // Seekbar
    private SeekBar seekBar;
    private TextSwitcher progressCount;
    private TextSwitcher duration;

    // Buttons
    private FloatingMusicActionButton playButton;
    private ImageView prevButton;
    private ImageView nextButton;
    private ImageView shuffleButton;

    // Album
    private ImageView albumArtBackground; // Blurred background
    private ImageView albumArt; // Album cardView
    private ImageView favoriteGhost = null;
    private ImageView favoriteButton;

    private Runnable updateTimeRunnable; // updates song time
    private Handler mHandler = new Handler();

    private Song currentSong;
    private BarVisualizer visualizer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        View v =  inflater.inflate(R.layout.fragment_now_playing, container, false);
        setRetainInstance(true);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SPMainActivity.getSongEventHandler().addSongPlaybackEventListener(this);
        SPMainActivity.getSongEventHandler().addSongShuffleEventListener(this);
        currentSong = SPMainActivity.mPlayerService.getCurrentSong();
        setSharedElementEnterTransition(new ChangeBounds());

        initNowPlaying(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        SPMainActivity parentActivity = ((SPMainActivity)getActivity());
        if( parentActivity != null ){
            parentActivity.setActionBarTitle(R.string.NOW_PLAYING);
        }
    }

    /**
     * Initializes all views in the fragment
     * @param v the fragment layout view
     */
    private void initNowPlaying(View v){
        if( !SPMainActivity.mPlayerService.isMediaPlayerSet() || currentSong == null){
            return;
        }

        // Reference views
        if( v != null ) {
            albumArtBackground =     v.findViewById(R.id.now_playing_album_art_background);
            songName =          v.findViewById(R.id.now_playing_songName_textSwitcher);
            artistName =        v.findViewById(R.id.now_playing_artistName_textSwitcher);
            playButton =        v.findViewById(R.id.now_playing_play_button);
            seekBar =           v.findViewById(R.id.now_playing_seekBar);
            progressCount =     v.findViewById(R.id.now_playing_progress);
            duration =          v.findViewById(R.id.now_playing_duration);
            favoriteButton =    v.findViewById(R.id.now_playing_favorite);
            prevButton =        v.findViewById(R.id.now_playing_previous_button);
            nextButton =        v.findViewById(R.id.now_playing_next_button);
            shuffleButton =     v.findViewById(R.id.now_playing_shuffle);
//            visualizer = (BarVisualizer) v.findViewById(R.id.visualizer);
            initAlbumCover(v);
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

        setTextSwitcherAnimations(songName, SPUtils.TSAnimationType.SLIDE);
        setTextSwitcherAnimations(artistName, SPUtils.TSAnimationType.SLIDE);


        // Setup song progress counter
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

        // Setup song duration
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

        setTextSwitcherAnimations(progressCount, SPUtils.TSAnimationType.FADE);
        setTextSwitcherAnimations(duration, SPUtils.TSAnimationType.FADE);

        // Set up runnable to update time.
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
                SPMainActivity.mPlayerService.playNextSong();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPMainActivity.mPlayerService.playPreviousSong();
            }
        });


        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPMainActivity.mPlayerService.setShuffle(!shuffleButton.isSelected());
            }
        });

        update();

        // After a second start updating the progress bar
        mHandler.postDelayed(updateTimeRunnable, 1000);
    }

    /**
     * Sets both in and out animations of a given type to
     * the given textSwitcher.
     * @param textSwitcher the textSwitcher to animate
     * @param type the animation type
     */
    private void setTextSwitcherAnimations(TextSwitcher textSwitcher, SPUtils.TSAnimationType type){
        switch (type){
            case SLIDE:
                textSwitcher.setInAnimation(SPUtils.getSlideInLeftAnimation(getContext()));
                textSwitcher.setOutAnimation(SPUtils.getSlideOutRightAnimation(getContext()));
                break;
            case FADE:
                textSwitcher.setInAnimation(SPUtils.getFadeInAnimation(getContext()));
                textSwitcher.setOutAnimation(SPUtils.getFadeOutAnimation(getContext()));
                break;
        }
    }

    /**
     * Updates Now Playing fragment
     */
    private void updateNowPlaying(){
        if(currentSong == null){
            return;
        }

        //Load large album image
        Picasso.with(getContext())
                .load(currentSong.getAlbumArt())
                .transform(new BlurTransformation(getContext(), 10))
                .placeholder(R.drawable.galaxy)
                .error(R.drawable.galaxy)
                .into(albumArtBackground);


        songName.setText(currentSong.getSongName());
        songName.setSelected(true);

        artistName.setText(currentSong.getArtistName());


        int maxPos = SPMainActivity.mPlayerService.getMediaPlayer().getDuration();
        int currPos = SPMainActivity.mPlayerService.getMediaPlayer().getCurrentPosition();

        seekBar.setMax(maxPos);
        seekBar.setProgress(currPos);
        seekBar.setDrawingCacheBackgroundColor(getResources().getColor(R.color.pastel_rose));

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

    public void update(){
        updateNowPlaying();
        updateAlbumCover();
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(updateTimeRunnable);
    }

    /**
     * Initializes all elements located
     * within the album cover.
     * @param v the album cover view
     */
    private void initAlbumCover(View v){
        if( currentSong == null ){ return; }

        if( v != null ) {
            albumArt = v.findViewById(R.id.now_playing_album_art);
            favoriteGhost = v.findViewById(R.id.now_playing_favorite_ghost);
        }

        albumArt.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){

                @Override
                public boolean onDoubleTap(MotionEvent e) {
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
                    return super.onDoubleTap(e);
                }

                @Override
                public boolean 	onSingleTapConfirmed(MotionEvent e){
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
                currentSong == null ? ((NowPlaying)getParentFragment()).getSongInfo().getStats().isFavorited()
                        : currentSong.getStats().isFavorited() );

        updateAlbumCover();
    }

    private void updateAlbumCover(){
        Uri uri = currentSong == null ? null
                : currentSong.getAlbumArt();

        Picasso.with(getContext())
                .load(uri)
                .placeholder(R.drawable.temp_album_art)
                .error(R.drawable.temp_album_art)
                .into(albumArt);
    }

    public Song getSongInfo(){
        return this.currentSong;
    }

    @Override
    public void onSongChangeEvent(SongPlaybackEvent e) {
        currentSong = e.getSource();
        update();
    }

    @Override
    public void onSongStopEvent(SongPlaybackEvent e) {
        currentSong = null;
        update();
    }


    @Override
    public void onShuffleOnEvent(SongShuffleEvent e) {
        shuffleButton.setSelected(true);
        shuffleButton.setColorFilter(getResources().getColor(R.color.pastel_rose));
    }

    @Override
    public void onShuffleOffEvent(SongShuffleEvent e) {
        shuffleButton.setSelected(false);
        shuffleButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }

} //Now Playing Activity

