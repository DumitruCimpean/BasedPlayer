package com.example.basedplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class CurrentlyPlayingSong extends AppCompatActivity {
    ConstraintLayout content;
    private MediaPlayer songPlayer;
    private ImageButton cpPausePlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currently_playing_song);

        SharedPreferences prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        ImageView coverArt = findViewById(R.id.albumCoverImageView);
        String albumArtUri = prefs.getString("albumArtUri", "R.drawable.music_note");

        Glide.with(this)
                .load(albumArtUri)
                .placeholder(R.drawable.music_note_box2) // Set a default image if necessary
                .error(R.drawable.music_note_box2) // Set an error image if loading fails
                .into(coverArt);

        content = findViewById(R.id.baseLayoutCP);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        content.startAnimation(slideUp);

        songPlayer = new MediaPlayer();

        // Initialize other UI components
        cpPausePlay = findViewById(R.id.cpPausePlay);

        // Set an OnClickListener for the pause/play button
        cpPausePlay.setOnClickListener(v -> togglePlayback());


    }

    private void togglePlayback() {
        if (songPlayer.isPlaying()) {
            pauseSong();
        } else {
            playSong();
        }
    }

    // Play the song
    private void playSong() {
        try {
            songPlayer.setDataSource("path");

            // Prepare and start playback
            songPlayer.prepare();
            songPlayer.start();

            // Update the button icon to show pause
            cpPausePlay.setImageResource(R.drawable.pause_icon);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any errors or exceptions
        }
    }

    // Pause the song
    private void pauseSong() {
        if (songPlayer.isPlaying()) {
            // Pause the song if it's currently playing
            songPlayer.pause();
        }
        // Update the button icon to show play
        cpPausePlay.setImageResource(R.drawable.play_icon);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_down, R.anim.slide_down);

    }
}