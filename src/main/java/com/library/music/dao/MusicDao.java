package com.library.music.dao;


import com.library.music.model.Music;

import java.util.List;

public interface MusicDao {

    Music getById(Integer musicId);
    List<Music> getAll();
    List<Music> getByGenre(String genre);
    List<Music> getByArtistName(String artistName);
    Integer insertMusicInLibrary(Music music);
    Integer deleteMusic(Integer musicId);
    List<String> getGenre();
    List<String> getArtist();
    Integer updateMusic(Integer Id, Music music);

}
