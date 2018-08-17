package com.smartmusic.android.smartmusicplayer.nowplaying;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.smartmusic.android.smartmusicplayer.SPMainActivity;
import com.smartmusic.android.smartmusicplayer.SongEvent;
import com.smartmusic.android.smartmusicplayer.SongEventListener;
import com.smartmusic.android.smartmusicplayer.SongPlayerService;
import com.smartmusic.android.smartmusicplayer.model.SongInfo;
import com.smartmusic.android.smartmusicplayer.R;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.BlurTransformation;


public class NowPlaying extends Fragment implements SongEventListener {

    /*Views*/
//    private ImageView albumArt;
    private ImageView largeAlbumArt;
    private TextView songName;
    private TextView artistName;
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

    private SongInfo currentSong;

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

        setUpNowPlaying(v);
        return v;
    }

    private void setUpNowPlaying(View v){

        if( !SPMainActivity.mPlayerService.isMediaPlayerSet() || currentSong == null){
            return;
        }

        // initialize views
        if( v != null ) {
            largeAlbumArt = (ImageView) v.findViewById(R.id.now_playing_large_album_art);
            songName = (TextView) v.findViewById(R.id.now_playing_songName);
            artistName = (TextView) v.findViewById(R.id.now_playing_artistName);
            playButton = (ImageView) v.findViewById(R.id.now_playing_play_button);
            seekBar = (SeekBar) v.findViewById(R.id.now_playing_seekBar);
            progressCount = (TextView) v.findViewById(R.id.now_playing_progress);
            duration = (TextView) v.findViewById(R.id.now_playing_duration);
            collapseButton = (ImageView) v.findViewById(R.id.now_playing_collapse);
            favoriteButton = (ImageView) v.findViewById(R.id.now_playing_favorite);
            prevButton = (ImageView) v.findViewById(R.id.now_playing_previous_button);
            nextButton = (ImageView) v.findViewById(R.id.now_playing_next_button);
            setUpAlbumArt(v, currentSong);
        }


        //Load large album image
        Picasso.with(getContext())
                .load(currentSong.getAlbumArt())
                .transform(new BlurTransformation(getContext(), 30))
                .into(largeAlbumArt);


        songName.setText(currentSong.getSongname());
        songName.setSelected(true);
        songName.setHorizontallyScrolling(true);

        artistName.setText(currentSong.getArtistname());

//        if( mediaPlayer.isPlaying() ){
//            playButton.setSelected(true);
//        } else {
//            playButton.setSelected(false);
//        }

        int maxPos = SPMainActivity.mPlayerService.getMediaPlayer().getDuration();
        int currPos = SPMainActivity.mPlayerService.getMediaPlayer().getCurrentPosition();

        seekBar.setMax(maxPos);
        seekBar.setProgress(currPos);


        progressCount.setText(milliToTime(currPos));
        duration.setText(milliToTime(maxPos));


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

                progressCount.setText(milliToTime(new_currPos));
                duration.setText(milliToTime(new_maxPos));
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
                    SPMainActivity.mPlayerService.playSong(currentSong);
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
                    progressCount.setText(milliToTime(progress));
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

//        favoriteGhostRunnable = new Runnable() {
//            @Override
//            public void run() {
//                favoriteGhost.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
//                favoriteGhost.animate().alpha(1.0f).start();
//
//                try{ wait(1000); } catch ( InterruptedException e) { e.printStackTrace(); }
//                favoriteGhost.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));
//
//            }
//        };

        favoriteButton.setSelected(currentSong.isFavorited());


        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSong.toggleFavorite();

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

        // After a second start updating the progress bar
        mHandler.postDelayed(updateTimeRunnable, 1000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SPMainActivity.getSongEventHandler().removeSongEventListener(this);
    }

    /**
     * Converts millisecond time to minutes:seconds
     * @param milliseconds the number of milliseconds
     * @return
     */
    private String milliToTime(int milliseconds) {
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
        setUpNowPlaying(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(updateTimeRunnable);
    }

    private void setUpAlbumArt(View view, final SongInfo song){

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
                        ((NowPlaying) getParentFragment()).getSongInfo().toggleFavorite();
                    } else {
                        song.toggleFavorite();
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
                song == null ? ((NowPlaying)getParentFragment()).getSongInfo().isFavorited()
                        : song.isFavorited() );

    }

    public SongInfo getSongInfo(){
        return this.currentSong;
    }
    public ImageView getFavoriteButton(){
        return this.favoriteButton;
    }

    @Override
    public void onSongChangeEvent(SongEvent e) {
        currentSong = e.getSource();
        setUpNowPlaying(null);
        setUpAlbumArt(null, e.getSource());
    }

    @Override
    public void onSongStopEvent(SongEvent e) {
        currentSong = null;
        setUpNowPlaying(null);
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

