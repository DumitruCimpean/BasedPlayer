package com.example.basedplayer;

import android.net.Uri;

import java.io.Serializable;

public class SongModel implements Serializable {
    String path;
    String title;
    String album;
    String artist;
    String duration;
    String albumID;

    public SongModel(String path, String title, String album, String artist, String duration, String albumID) {
        this.path = path;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
        this.albumID = albumID;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public String getAlbumID() {
        return albumID;
    }

    public Uri getAlbumArtUri() {
        return Uri.parse("content://media/external/audio/albumart/" + albumID);
    }
}
