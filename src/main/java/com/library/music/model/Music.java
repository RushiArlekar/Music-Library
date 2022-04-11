package com.library.music.model;

public class Music {

    Integer musicId;
    String musicName;
    String musicGenre;
    String artistName;

    public Music() {
    }

    public Music(String new_music, String new_genre, String new_artist) {
        this.musicName = new_music;
        this.musicGenre = new_genre;
        this.artistName = new_artist;
    }

    public Music(Integer musicId, String musicName, String musicGenre, String artistName) {
        this.musicId = musicId;
        this.musicName = musicName;
        this.musicGenre = musicGenre;
        this.artistName = artistName;
    }

    public Integer getMusicId() {
        return this.musicId;
    }

    public void setMusicId(Integer musicId) {
        this.musicId = musicId;
    }

    public String getMusicName() {
        return this.musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicGenre() {
        return this.musicGenre;
    }

    public void setMusicGenre(String musicGenre) {
        this.musicGenre = musicGenre;
    }

    public String getArtistName() {
        return this.artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
//
//    @Override
//    public String toString() {
//        return "Music{" +
//                "musicId=" + musicId +
//                ", musicName='" + musicName + '\'' +
//                ", musicGenre='" + musicGenre + '\'' +
//                ", artistName='" + artistName + '\'' +
//                '}';
//    }
}
