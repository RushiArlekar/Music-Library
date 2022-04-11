package com.library.music.controller;

import com.library.music.exceptions.DatabaseErrorException;
import com.library.music.exceptions.InvalidInputException;
import com.library.music.exceptions.NoMusicFoundException;
import com.library.music.exceptions.NullInputException;
import com.library.music.model.Music;
import com.library.music.services.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/library")
public class LibController {

    @Autowired
    private LibraryService libraryService;

    @RequestMapping("/health-check")
    public ResponseEntity<?> getApiHealth(){
        return ResponseEntity.ok(1);
    }

    @RequestMapping("/music")
    public ResponseEntity<?> getAllMusicTracks() throws NoMusicFoundException {
        return ResponseEntity.ok(libraryService.getAllMusic().join());
    }

    @RequestMapping(method = RequestMethod.GET,value = "/music/track/{musicId}")
    public ResponseEntity<?> getMusicTrack(@PathVariable String musicId) throws InvalidInputException, NoMusicFoundException, NullInputException {
        return ResponseEntity.ok(libraryService.getMusicById(musicId).join());
    }

    @RequestMapping("/music/genre/{genre}")
    public ResponseEntity<?> getMusicTrackByGenre(@PathVariable String genre) throws NoMusicFoundException, NullInputException {
        return ResponseEntity.ok(libraryService.getMusicByGenre(genre).join());
    }

    @RequestMapping(method = RequestMethod.PUT ,value = "/music/track/{musicId}")
    public ResponseEntity<?> updateMusicTrack(@PathVariable String musicId, @RequestBody Music newMusic) throws DatabaseErrorException {
        return ResponseEntity.ok(libraryService.updateMusicTrack(musicId, newMusic).join());
    }

    @RequestMapping(method = RequestMethod.GET,value = "/music/genres")
    public ResponseEntity<?> getALlGenres() throws NoMusicFoundException {
        return ResponseEntity.ok(libraryService.getAllGenres().join());
    }

    @RequestMapping(method = RequestMethod.GET,value = "/music/artists")
    public ResponseEntity<?> getALlArtists() throws NoMusicFoundException {
        return ResponseEntity.ok(libraryService.getAllArtists().join());
    }

    @RequestMapping(method = RequestMethod.POST,value = "/music/track")
    public ResponseEntity<?> insertMusicTrack(@RequestBody Music music) throws DatabaseErrorException {
        return ResponseEntity.ok(libraryService.insertMusic(music).join());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/music/track/{musicId}")
    public ResponseEntity<?> deleteMusicTrack(@PathVariable String musicId) throws DatabaseErrorException {
        return ResponseEntity.ok(libraryService.deleteMusic(musicId).join());
    }

}
