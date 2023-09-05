package com.example.basedplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;
import java.lang.annotation.Target;
import java.util.ArrayList;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {

    ArrayList<SongModel> songList;
    Context context;
    MyGlobals gob = new MyGlobals(context);
    private SongModel currentlyPlayingSong;
    private MediaPlayer songPlayer;
    ImageView btpCover;
    RelativeLayout bottomPlayer;
    private GestureDetectorCompat gestureDetector;

    final boolean[] isBottomPlayerShown = {false};
    Handler handler = new Handler();

    private int currentlyPlayingPosition = -1;
    private MediaPlayer mediaPlayer;

    // Modify the constructor to accept the MediaPlayer
    public SongListAdapter(Context context, ArrayList<SongModel> songList, RelativeLayout bottomPlayer, MediaPlayer mediaPlayer) {
        this.songList = songList;
        this.context = context;
        this.bottomPlayer = bottomPlayer;
        this.mediaPlayer = mediaPlayer;
    }

    public void setCurrentlyPlayingPosition(int position) {
        currentlyPlayingPosition = position;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_item_layout,parent,false);
        return new SongListAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SharedPreferences prefs = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        SongModel songData = songList.get(position);
        holder.titleTextView.setText(songData.getTitle());
        holder.artistTextView.setText(songData.getArtist());
        Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);

        TextView btpTitle = bottomPlayer.findViewById(R.id.btpTitle);
        ImageView btpCover = bottomPlayer.findViewById(R.id.btpCover);
        ImageButton btpPlayPause = bottomPlayer.findViewById(R.id.btpPlayPause);

        if (songPlayer != null && songPlayer.isPlaying()){
            btpPlayPause.setImageResource(R.drawable.pause_icon);
        }
        if (songPlayer != null && !songPlayer.isPlaying() && songPlayer != null){
            btpPlayPause.setImageResource(R.drawable.play_icon);
        }


        btpPlayPause.setOnClickListener(v -> {
            gob.clickEffectResize(btpPlayPause, context);
            if (songPlayer != null && songPlayer.isPlaying()){
                pauseSong(songData);
                btpPlayPause.setImageResource(R.drawable.play_icon);
                btpTitle.setSelected(false);
            }else if (songPlayer != null && !songPlayer.isPlaying()){
                resumeSong(songData);
                btpPlayPause.setImageResource(R.drawable.pause_icon);
                btpTitle.setSelected(true);
            }
        });


        int durationInt = Integer.parseInt(songData.getDuration());
        int songSeconds = durationInt / 1000;
        int songMinutes = songSeconds / 60;
        songSeconds %= 60;
        Glide.with(context)
                .load(songData.getAlbumArtUri())
                .placeholder(R.drawable.music_note_box2) // default image
                .error(R.drawable.music_note_box2) // error image if loading fails
                .into(holder.songCoverImageView);

        if (position == currentlyPlayingPosition) {
            int nowPlayingColor = ContextCompat.getColor(context, R.color.faded_blue);
            holder.itemView.setBackgroundColor(nowPlayingColor);
        } else {
            int defaultColor = ContextCompat.getColor(context, R.color.dark_grey_100);
            holder.itemView.setBackgroundColor(defaultColor);
        }

        holder.itemView.setOnClickListener(view -> {
            gob.clickEffectDarken(holder.itemView);
            ImageView itemViewCover =  holder.itemView.findViewById(R.id.songCoverImageView);
            if (!isBottomPlayerShown[0]){
                bottomPlayer.setVisibility(View.VISIBLE);
                bottomPlayer.setAnimation(slideUp);
                isBottomPlayerShown[0] = true;
            }
            editor.putString("albumArtUri", songData.getAlbumArtUri().toString());
            editor.putInt("currentSongIndex", position);
            editor.putString("songTitle", songData.getTitle());
            editor.apply();

            playSong(songData);
            btpPlayPause.setImageResource(R.drawable.pause_icon);
            String title = songData.getTitle();
            String artist = songData.getArtist();
            String combinedText = title + " ・ " + artist;
            SpannableString spannableString = new SpannableString(combinedText);
            int separatorColor = ContextCompat.getColor(context, R.color.darker_white);
            spannableString.setSpan(new ForegroundColorSpan(separatorColor), title.length() + 3, combinedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            btpTitle.setText(spannableString);
            btpCover.setImageDrawable(holder.songCoverImageView.getDrawable());
            btpTitle.setSelected(false);
            handler.postDelayed( () -> btpTitle.setSelected(true), 2000);

            setCurrentlyPlayingPosition(position);

            // Notify the adapter to update the UI
            notifyDataSetChanged();
            setCurrentlyPlayingSong(songData);
        });
    }

    public void setCurrentlyPlayingSong(SongModel songData) {
        currentlyPlayingSong = songData;
    }

    public void togglePlayback() {
        if (songPlayer != null) {
            if (songPlayer.isPlaying()) {
                pauseSong(currentlyPlayingSong);
            } else {
                resumeSong(currentlyPlayingSong);
            }
        }
    }


    public void playSong(SongModel songData) {
        // Stop the currently playing song if there is one
        if (songPlayer != null && songPlayer.isPlaying()) {
            songPlayer.stop();
            songPlayer.release();
            songPlayer = null;
        }

        songPlayer = new MediaPlayer();

        try {
            songPlayer.setDataSource(songData.getPath());

            songPlayer.prepare();
            songPlayer.start();

            songPlayer.setOnCompletionListener(mp -> {
                songPlayer.release();
                songPlayer = null;
            });
        } catch (IOException e) {
            e.printStackTrace();
            gob.showToast("Error on playing the song");
        }
    }

    public void pauseSong(SongModel songData) {
        if (songPlayer != null && songPlayer.isPlaying()) {
            songPlayer.pause();
        }
    }

    public void resumeSong (SongModel songData){
        if (songPlayer != null && !songPlayer.isPlaying()){
            songPlayer.start();
        }
    }

    public void stopSong (SongModel songData){
        if (songPlayer != null && songPlayer.isPlaying()){
            songPlayer.stop();
            songPlayer.release();
            songPlayer = null;
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView;
        TextView artistTextView;

        TextView durationTextView;
        ImageView songCoverImageView;
        public ViewHolder(View itemView){
            super(itemView);
            titleTextView = itemView.findViewById(R.id.songTitleTextView);
            artistTextView = itemView.findViewById(R.id.songArtistTextView);
            songCoverImageView = itemView.findViewById(R.id.songCoverImageView);
        }
    }




}
