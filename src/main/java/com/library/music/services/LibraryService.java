package com.library.music.services;

import com.library.music.dao.MusicDaoImpl;
import com.library.music.exceptions.DatabaseErrorException;
import com.library.music.exceptions.InvalidInputException;
import com.library.music.exceptions.NoMusicFoundException;
import com.library.music.exceptions.NullInputException;
import com.library.music.model.Music;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class LibraryService {

    private MusicDaoImpl musicDao;
    private List<Music> musicList;
    private List<String> singleList;


    @Autowired
    public LibraryService(MusicDaoImpl musicDao) {
        this.musicDao = musicDao;
        musicList = new ArrayList<>();
        singleList = new ArrayList<>();
    }

    public void checkInput(String input, String errMessage) throws NullInputException {
        if(input.isEmpty() || input.isBlank()){
            throw new NullInputException(400,errMessage);
        }
    }

    public void checkEmptyList(List<?> list, String errMessage) throws NoMusicFoundException {
        if(list.size() == 0){
            throw new NoMusicFoundException(404,errMessage);
        }
    }

    public CompletableFuture<?> getMusicById(String musicId) throws NoMusicFoundException, NullInputException, InvalidInputException {

        checkInput(musicId, "music id is blank");

        Music music;
        try {
            int id = Integer.parseInt(musicId);
            music = musicDao.getById(id);

            System.out.println(music.getMusicId()+music.getMusicName());
            if(music.getMusicName() == null){
                throw new NoMusicFoundException(404,"No music files in Library");
            }
        }
        catch (NumberFormatException e){
            throw new InvalidInputException(407,"Invalid music id provided");
        }
        return CompletableFuture.completedFuture(music);

    }

    public CompletableFuture<?> getAllMusic() throws NoMusicFoundException {

        musicList = musicDao.getAll();
        checkEmptyList(musicList, "library is empty");
        return CompletableFuture.completedFuture(musicList);
    }


    public CompletableFuture<?> getMusicByGenre(String genre) throws NullInputException, NoMusicFoundException {

        checkInput(genre,"genre not provided");
        musicList = musicDao.getByGenre(genre);
        if(musicList.size() == 0){
            throw new NoMusicFoundException(404,"No music found");
        }
        return CompletableFuture.completedFuture(musicList);
    }

    public List<Music> getMusicByArtist(String artistName) throws NullInputException, NoMusicFoundException {

        checkInput(artistName, "Artist name not provided");

        musicDao.getByArtistName(artistName).forEach(musicList::add);
        checkEmptyList(musicList, "No music found");
        return musicList;

    }

    public CompletableFuture<?> getAllGenres() throws NoMusicFoundException {

        singleList = musicDao.getGenre();
        checkEmptyList(singleList, "No music found");
        return CompletableFuture.completedFuture(singleList);

    }

    public CompletableFuture<?> getAllArtists() throws NoMusicFoundException {

        singleList = musicDao.getArtist();
        checkEmptyList(singleList, "No music found");
        return CompletableFuture.completedFuture(singleList);

    }

    public CompletableFuture<?> updateMusicTrack(String musicId, Music newMusic) throws DatabaseErrorException {

        Integer retValue = musicDao.updateMusic(Integer.valueOf(musicId),newMusic);
        if(retValue == 1)
            return CompletableFuture.completedFuture(true);
        else
            throw new DatabaseErrorException(701,"Error updating the value.");

    }

    public CompletableFuture<?> insertMusic(Music newMusic) throws DatabaseErrorException {

        Integer insertId = musicDao.insertMusicInLibrary(newMusic);
        if(insertId == 0)
            throw new DatabaseErrorException(702, "Error inserting record.");
        return CompletableFuture.completedFuture(insertId);
    }

    public CompletableFuture<?> deleteMusic(String musicId) throws DatabaseErrorException {

        int deleted = musicDao.deleteMusic(Integer.valueOf(musicId));

        if(deleted > 0)
            return CompletableFuture.completedFuture(deleted);
        throw new DatabaseErrorException(703, "Error deleting record.");

    }
}
