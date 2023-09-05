package com.example.basedplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class CpFragment extends Fragment {

    private MediaPlayer songPlayer;
    private ImageButton playPauseButton;


    public static CpFragment newInstance(SongModel song) {
        CpFragment fragment = new CpFragment();
        Bundle args = new Bundle();
        args.putSerializable("selectedSong", song);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cp, container, false);
        SharedPreferences prefs = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        MyGlobals gob = new MyGlobals(getContext());

        playPauseButton = rootView.findViewById(R.id.cpPausePlay);
        TextView songTitleTextView = rootView.findViewById(R.id.cpTitleTextView);
        songTitleTextView.setSelected(true);
        TextView songArtistText = rootView.findViewById(R.id.cpArtistTextView);
        TextView songDurationText = rootView.findViewById(R.id.songDurationTextView);
        ImageView coverArt = rootView.findViewById(R.id.albumCoverImageView);

        boolean toggleIsPlaying = prefs.getBoolean("toggleIsPlaying", false);
        String albumArtUri = prefs.getString("albumArtUri", "R.drawable.music_note");
        String songTitle = prefs.getString("songTitle", "Error loading title");
        String songArtist = prefs.getString("songArtist", "Error loading artist");
        int songHours = prefs.getInt("songHours", 0);
        int songMinutes = prefs.getInt("songMinutes", 0);
        int songSeconds = prefs.getInt("songSeconds", 0);

        songArtistText.setText(songArtist);

        if (songSeconds < 10) {
            if (songMinutes < 10){
                songDurationText.setText(0 + songMinutes + ":" + 0 + songSeconds);
            }else{
                songDurationText.setText(songMinutes + ":" + 0 + songSeconds);
            }
        }else {
            songDurationText.setText(songMinutes + ":" + songSeconds);
        }

        if (songHours > 0){
            songDurationText.setText(songHours + ":" + songMinutes + ":" + songSeconds);
        }

        if (toggleIsPlaying){
            playPauseButton.setImageResource(R.drawable.pause_icon);
        }else {
            playPauseButton.setImageResource(R.drawable.play_icon);
        }
        songTitleTextView.setText(songTitle);

        Glide.with(this)
                .load(albumArtUri)
                .placeholder(R.drawable.music_note_box2) // Set a default image if necessary
                .error(R.drawable.music_note_box2) // Set an error image if loading fails
                .into(coverArt);


        playPauseButton.setOnClickListener(view -> {
            gob.clickEffectResize(playPauseButton, getContext());
            if (getAdapter() != null) {
                getAdapter().togglePlayback();
            }
        });


        return rootView;
    }

    private SongListAdapter getAdapter() {
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            return activity.getSongListAdapter();
        }
        return null;
    }

}

