package com.example.basedplayer;

import java.util.Comparator;

public class SongTitleComparator implements Comparator<SongModel> {
    @Override
    public int compare(SongModel song1, SongModel song2) {
        return song1.getTitle().compareTo(song2.getTitle());
    }
}
