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

        playPauseButton = rootView.findViewById(R.id.cpPausePlay);
        TextView songTitleTextView = rootView.findViewById(R.id.cpTitleTextView);
        ImageView coverArt = rootView.findViewById(R.id.albumCoverImageView);


        String albumArtUri = prefs.getString("albumArtUri", "R.drawable.music_note");
        String songTitle = prefs.getString("songTitle", "Error loading title");
        songTitleTextView.setText(songTitle);

        Glide.with(this)
                .load(albumArtUri)
                .placeholder(R.drawable.music_note_box2) // Set a default image if necessary
                .error(R.drawable.music_note_box2) // Set an error image if loading fails
                .into(coverArt);


        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call a method in the adapter to control playback
                if (getAdapter() != null) {
                    getAdapter().togglePlayback();
                }
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
