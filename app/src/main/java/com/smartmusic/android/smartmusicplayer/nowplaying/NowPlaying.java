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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.smartmusic.android.smartmusicplayer.SPMainActivity;
import com.smartmusic.android.smartmusicplayer.SPUtils;
import com.smartmusic.android.smartmusicplayer.SongEvent;
import com.smartmusic.android.smartmusicplayer.SongEventListener;
import com.smartmusic.android.smartmusicplayer.database.entities.Song;
import com.smartmusic.android.smartmusicplayer.R;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.BlurTransformation;


public class NowPlaying extends Fragment implements SongEventListener {

    /*Views*/
//    private ImageView albumArt;
    private ImageView largeAlbumArt;
    private TextSwitcher songName;
    private TextSwitcher artistName;
    private ImageView playButton;
    private SeekBar seekBar;
    private TextView progressCount;
    private TextView duration;
    private ImageView collapseButton;
    private ImageView prevButton;
    private ImageView nextButton;

    // Album
    private ImageView albumArt;
    private ImageView favoriteGhost = null;
//    private ImageView favoriteButton;

//    private ImageView favoriteGhost;
    private ImageView favoriteButton;
    private ImageView shuffleButton;

    private Runnable updateTimeRunnable;
    private Runnable favoriteGhostRunnable;

    private Handler mHandler = new Handler();

    private Song currentSong;

    // Declare in and out animations and load them using AnimationUtils class
    private Animation inL;
//    private Animation outL;
//    private Animation inR;
    private Animation outR;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_now_playing, container, false);
        setRetainInstance(true);

        getActivity().setTitle("Now Playing");
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SPMainActivity.getSongEventHandler().addSongEventListener(this);
        currentSong = SPMainActivity.mPlayerService.getCurrentSong();

        inL = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        outR = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right);
//        inR = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_right);
//        outL = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_left);


        initNowPlaying(v);
        return v;
    }


    private void initNowPlaying(View v){
        if( !SPMainActivity.mPlayerService.isMediaPlayerSet() || currentSong == null){
            return;
        }

        // initialize views
        if( v != null ) {
            largeAlbumArt = (ImageView) v.findViewById(R.id.now_playing_large_album_art);
            songName = (TextSwitcher) v.findViewById(R.id.now_playing_songName_textSwitcher);
            artistName = (TextSwitcher) v.findViewById(R.id.now_playing_artistName_textSwitcher);
            playButton = (ImageView) v.findViewById(R.id.now_playing_play_button);
            seekBar = (SeekBar) v.findViewById(R.id.now_playing_seekBar);
            progressCount = (TextView) v.findViewById(R.id.now_playing_progress);
            duration = (TextView) v.findViewById(R.id.now_playing_duration);
            collapseButton = (ImageView) v.findViewById(R.id.now_playing_collapse);
            favoriteButton = (ImageView) v.findViewById(R.id.now_playing_favorite);
            prevButton = (ImageView) v.findViewById(R.id.now_playing_previous_button);
            nextButton = (ImageView) v.findViewById(R.id.now_playing_next_button);
            shuffleButton = (ImageView) v.findViewById(R.id.now_playing_shuffle);
            setUpAlbumArt(v, currentSong);
        }


        songName.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // create a TextView
                TextView t = new TextView(getContext());

                t.setTextColor(getResources().getColor(R.color.pastel_rose));
//                t.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/exo_medium.ttf"));
                t.setTextSize(26);
                t.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                t.setGravity(Gravity.CENTER);
                return t;
            }
        });

        artistName.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // create a TextView
                TextView t = new TextView(getContext());

                t.setTextColor(Color.WHITE);
//                t.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/comfortaa_light.xml"));
                t.setTextSize(14);
                t.setAllCaps(true);
                t.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                t.setGravity(Gravity.CENTER);
                return t;
            }
        });

        // set the animation type to TextSwitcher
        songName.setInAnimation(inL);
        songName.setOutAnimation(outR);
        artistName.setInAnimation(inL);
        artistName.setOutAnimation(outR);

        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                if(!SPMainActivity.mPlayerService.isMediaPlayerSet()){
                    mHandler.postDelayed(this, 1000);
                    return;
                }

                if(SPMainActivity.mPlayerService.isSongPlaying()){
                    playButton.setSelected(true);
                } else {
                    playButton.setSelected(false);
                }

                int new_currPos = SPMainActivity.mPlayerService.getMediaPlayer().getCurrentPosition();
                int new_maxPos = SPMainActivity.mPlayerService.getMediaPlayer().getDuration();

                progressCount.setText(SPUtils.milliToTime(new_currPos));
                duration.setText(SPUtils.milliToTime(new_maxPos));
                seekBar.setProgress(new_currPos);


                mHandler.postDelayed(this, 1000);
            }
        };


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SPMainActivity.mPlayerService.isSongPlaying()) {
                    SPMainActivity.mPlayerService.pause();
                    playButton.setSelected(false);
                } else {
                    SPMainActivity.mPlayerService.resume();
                    playButton.setSelected(true);
                }
            }
        });

        collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseView();
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
                songName.setInAnimation(inL);
                songName.setOutAnimation(outR);
                artistName.setInAnimation(inL);
                artistName.setOutAnimation(outR);

                SPMainActivity.mPlayerService.playNextSong();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songName.setInAnimation(inL);
                songName.setOutAnimation(outR);
                artistName.setInAnimation(inL);
                artistName.setOutAnimation(outR);

                SPMainActivity.mPlayerService.playPreviousSong();
            }
        });


        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuffleButton.setSelected(!shuffleButton.isSelected());
                SPMainActivity.mPlayerService.setShuffle(shuffleButton.isSelected());
                if(shuffleButton.isSelected()){
                    shuffleButton.setColorFilter(R.color.pastel_rose, PorterDuff.Mode.SRC_ATOP);
                } else {
                    shuffleButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                }
            }
        });

        updateNowPlaying();

        // After a second start updating the progress bar
        mHandler.postDelayed(updateTimeRunnable, 1000);
    }

    private void updateNowPlaying(){

        //Load large album image
        Picasso.with(getContext())
                .load(currentSong.getAlbumArt())
                .transform(new BlurTransformation(getContext(), 30))
                .error(R.drawable.temp_album_art)
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
        if(shuffleButton.isSelected()){
            shuffleButton.setColorFilter(R.color.pastel_rose, PorterDuff.Mode.SRC_ATOP);
        } else {
            shuffleButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
//
//        // After a second start updating the progress bar
//        mHandler.postDelayed(updateTimeRunnable, 1000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SPMainActivity.getSongEventHandler().removeSongEventListener(this);
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
    public void onSongAddedEvent(SongEvent e) {

    }

    @Override
    public void onSongRemovedEvent(SongEvent e) {

    }

    @Override
    public void onShuffleOnEvent(SongEvent e) {

    }

    @Override
    public void onShuffleOffEvent(SongEvent e) {

    }

} //Now Playing Activity

