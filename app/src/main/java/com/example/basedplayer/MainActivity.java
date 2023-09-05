package com.example.basedplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // TODO: a lot...

    RecyclerView recyclerView;
    TextView noSongsText;
    TextView songsTextView;
    TextView btpTitle;
    ImageView btpCover;
    RelativeLayout bottomPlayer;
    RelativeLayout mainActivityLayout;

    MediaPlayer songPlayer = new MediaPlayer();
    boolean firstBoot = true;
    ArrayList<SongModel> songList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyGlobals gob = new MyGlobals(this);
        SharedPreferences prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);



        recyclerView = findViewById(R.id.recyclerView);
        noSongsText = findViewById(R.id.noSongsTextView);
        songsTextView = findViewById(R.id.songsTextView);
        btpTitle = findViewById(R.id.btpTitle);
        btpCover = findViewById(R.id.btpCover);
        mainActivityLayout = findViewById(R.id.mainActivityId);
        bottomPlayer = findViewById(R.id.bottomPlayer);
        bottomPlayer.setVisibility(View.GONE);

        if (!checkPermission()){
            requestPermission();
            return;
        }

        String[] projection = {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null );
        while (cursor.moveToNext()){
            @SuppressLint("Range") SongModel songData = new SongModel(
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
            );

            if (new File(songData.getPath()).exists()){
                songList.add(songData);
            }

            songList.sort(new SongTitleComparator());

            if (songList.size() == 0) {
                noSongsText.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(new SongListAdapter(getApplicationContext(), songList, bottomPlayer, mainActivityLayout, songPlayer));
            }
        }
        cursor.close();


        bottomPlayer.setOnClickListener(v -> {
            if (isFragmentVisible()) {
                hideFragment();
            } else {
                showFragment();
            }
        });


    }

    private boolean isFragmentVisible() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("CpFragment");
        return fragment != null && fragment.isVisible();
    }

    private void showFragment() {
        recyclerView.setVisibility(View.INVISIBLE);
        SharedPreferences prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_up);

        // logic to get the selected song
        SongModel selectedSong = songList.get(prefs.getInt("currentSongIndex", 0));
        CpFragment cpFragment = CpFragment.newInstance(selectedSong);


        transaction.replace(R.id.fragmentContainer, cpFragment, "CpFragment");
        transaction.addToBackStack(null);
        transaction.commit();
        if (firstBoot){
            findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
            firstBoot = false;
        }


    }

    private void hideFragment() {
        recyclerView.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_down, R.anim.slide_down);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("CpFragment");
        if (fragment != null) {
            transaction.hide(fragment);
            transaction.commit();
            if (firstBoot){
                findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
            }

        }
    }

    public SongListAdapter getSongListAdapter() {
        if (recyclerView != null) {
            return (SongListAdapter) recyclerView.getAdapter();
        }
        return null;
    }

    boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return  false;
        }
    }

    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this, "Storage permission denied. Please allow it in settings", Toast.LENGTH_SHORT).show();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},69);
        }
    }

    @Override
    public void onBackPressed() {
        if (isFragmentVisible()) {
            hideFragment();
        } else {
            super.onBackPressed();
            finish();
        }
    }
}