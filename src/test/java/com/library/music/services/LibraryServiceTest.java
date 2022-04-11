package com.library.music.services;

import com.library.music.dao.MusicDaoImpl;
import com.library.music.exceptions.DatabaseErrorException;
import com.library.music.exceptions.InvalidInputException;
import com.library.music.exceptions.NoMusicFoundException;
import com.library.music.exceptions.NullInputException;
import com.library.music.model.Music;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(value = LibraryService.class)
class LibraryServiceTest {

    @InjectMocks
    private LibraryService libraryService;

    @MockBean
    private MusicDaoImpl musicDao;

    @BeforeEach
    void setUp(TestInfo info) {
        System.out.println("test " + info.getTags() + " started!");
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown(TestInfo info) {
        System.out.println("test " + info.getTags() + " complete");
    }

    @Test
    @Tag("find-music-by-id-test")
    public void testFindMusicById() throws Exception {

        Integer musicId = 1;
        Music newMusic = new Music(1,"Dariya","Romantic","Mukharjee");

        when(musicDao.getById(musicId)).thenReturn(newMusic);

        Music actual = (Music) libraryService.getMusicById(musicId.toString()).join();
        Music expected = newMusic;
        assertEquals(expected, actual, ()-> "should return one music track");

    }

    @Test
    @Tag("find-music-by-non-existing-id-test")
    public void testFindMusicByNonExistingId() {

        Integer musicId = 200;
        Music newMusic = new Music(musicId,null,null,null);
        when(musicDao.getById(musicId)).thenReturn(newMusic);

        NoMusicFoundException exception = assertThrows(NoMusicFoundException.class, () -> {
            libraryService.getMusicById(String.valueOf(musicId));
        });

        String expectedMessage = "No music files in Library";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    @Tag("find-music-by-invalid-id-test")
    public void testFindMusicByInvalidId() {

        String musicId = "wow";
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            libraryService.getMusicById(musicId);
        });

        String expectedMessage = "Invalid music id provided";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Nested
    @DisplayName("get-list")
    class nestedTest{

        Music music_1, music_2;
        List<Music> musicList;

        nestedTest(){
            music_1 = new Music("Tere Sang Yara", "Atif Aslam", "Romantic");

            music_2 = new Music("Dil Ka Dariya", "Arijit Singh", "Sad");

            musicList = new ArrayList<>();
            musicList.add(music_1);
            musicList.add(music_2);
        }

        @Test
        @Tag("test-find-all-music")
        public void testFindAllMusic() throws Exception {

            when(musicDao.getAll()).thenReturn(musicList);

            List<Music> expected = musicList;
            List<Music> actual = (List<Music>) libraryService.getAllMusic().join();

            assertEquals(expected, actual, () -> "Should give all music tracks");

        }

        @Test
        @Tag("test-find-all-music-by-genre")
        public void testFindAllByGenre() throws NoMusicFoundException, NullInputException {

            List<Music> genreList = new ArrayList<>();
            genreList.add(music_1);

            when(musicDao.getAll()).thenReturn(musicList);
            when(musicDao.getByGenre("Romantic")).thenReturn(genreList);

            List<Music> expected = genreList;
            List<Music> actual = (List<Music>) libraryService.getMusicByGenre("Romantic").join();

            assertEquals(expected, actual, ()-> "Should give filtered music tracks based on genre");

        }

        @Test
        @Tag("test-find-all-music-by-invalid-genre")
        public void testFindAllByInvalidGenre() throws NoMusicFoundException, NullInputException {

            List<Music> emptyList = new ArrayList<>();

            String genre = "Bollywood";

            when(musicDao.getAll()).thenReturn(musicList);
            when(musicDao.getByGenre(genre)).thenReturn(emptyList);

            List<Music> expected = emptyList;
            Exception exception = assertThrows(NoMusicFoundException.class, ()->{
                libraryService.getMusicByGenre(genre);
            });

            String message = "No music found";
            assertTrue(exception.getMessage().contains(message), ()-> "Should give empty list");

        }

        @Test
        @Tag("test-find-all-artist-names")
        public void testFindAllArtists() throws NoMusicFoundException {

            List<String> artistList = new ArrayList<>();
            artistList.add(music_1.getArtistName());
            artistList.add(music_2.getArtistName());

//            when(musicDao.getAll()).thenReturn(musicList);
            when(musicDao.getArtist()).thenReturn(artistList);

            List<String> expected = artistList;
            List<String> actual = (List<String>) libraryService.getAllArtists().join();

            assertEquals(expected, actual, ()-> "Should give all artists names in the library");

        }

        @Test
        @Tag("get-all-genres-test")
        public void testFindAllGenres() throws NoMusicFoundException {

            List<String> genreList = new ArrayList<>();
            genreList.add(music_1.getMusicGenre());
            genreList.add(music_2.getMusicGenre());

//            when(musicDao.getAll()).thenReturn(musicList);
            when(musicDao.getGenre()).thenReturn(genreList);

            List<String> expected = genreList;
            List<String> actual = (List<String>) libraryService.getAllGenres().join();

            assertEquals(expected, actual, ()-> "Should give all unique genres in the library");

        }



    }

    @Test
    @Tag("update-music-track-test")
    public void testUpdateMusic() throws DatabaseErrorException {

        Integer musicId = 2;
        Music newMusic = new Music("Tennu Me", "Bollywood", "Shaan");
        when(musicDao.updateMusic(musicId,newMusic)).thenReturn(1);

        boolean expected = true;
        boolean actual = (boolean) libraryService.updateMusicTrack(String.valueOf(musicId),newMusic).join();

        assertEquals(expected, actual, ()-> "Should update music in the library");

    }

    @Test
    @Tag("update-non-existing-test")
    public void testUpdateNonExistingMusic() throws DatabaseErrorException {

        Integer musicId = 20;
        Music newMusic = new Music("Tennu Me", "Bollywood", "Shaan");

        Exception exception = assertThrows(DatabaseErrorException.class, ()-> {
            libraryService.updateMusicTrack(String.valueOf(musicId),newMusic);
        });

        String message = "Error updating the value.";
        assertTrue(exception.getMessage().contains(message));

    }

    @Test
    @Tag("test-find-all-when-music-list-is-empty")
    public void testFindAllInEmptyList() {

        List<Music> musicList = new ArrayList<>();
        when(musicDao.getAll()).thenReturn(musicList);

        Exception exception = assertThrows(NoMusicFoundException.class, ()->{
            libraryService.getAllMusic();
        });

        String message = "library is empty";
        assertTrue(exception.getMessage().contains(message));

    }




    @Test
    @Tag("test-insert-music")
    public void testInsertMusic() throws DatabaseErrorException {

        Integer musicId = 20;
        Music newMusic = new Music("new Music", "new Genre", "new artist");
        when(musicDao.insertMusicInLibrary(newMusic)).thenReturn(musicId);

        Integer expected = (Integer) libraryService.insertMusic(newMusic).join();
        assertEquals(expected, musicId, ()-> "should match id 20");

    }

}