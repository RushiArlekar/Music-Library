package com.library.music.dao;

import com.library.music.model.Music;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
//@DataJdbcTest(properties = {"spring.datasource.url=jdbc:mysql://librarydb:3306/springboot_library","spring.datasource.username=root","spring.datasource.password=Library@123"})
@DataJdbcTest(properties = {"spring.datasource.url=jdbc:mysql://localhost:3306/springboot_library","spring.datasource.username=springbootuser","spring.datasource.password=mysqlspringbootpassword"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MusicDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private MusicDaoImpl musicDao;

    @Autowired
    public MusicDaoImplTest(JdbcTemplate jdbcTemplate) {
        this.musicDao = new MusicDaoImpl(jdbcTemplate);
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeAll
    public static void init(){
        System.out.println("Testing Music DAO Implementation Class");
    }

    @BeforeEach
    public void beforeEach(TestInfo info){
        System.out.println("test " + info.getTags() + " started!");
    }

    @Test
    @Tag("test-get-by-id")
    void getById() {

        Music music = musicDao.getById(1);

        assertThat(music).isNotNull();

    }

    @Test
    @Tag("test-get-all-tracks")
    void getAll() {
        List<Music> musicList = musicDao.getAll();
        musicList.forEach(music -> System.out.println(music));

        assertThat(musicList.size()).isGreaterThan(0);
    }

    @Test
    @Tag("test-get-all-by-music-genre")
    void getByGenre() {

        String genre = "Sad";
        List<Music> musicList = musicDao.getByGenre(genre);
        List<Music> tempList = musicList.stream().filter(music -> music.getMusicGenre().equals(genre)).collect(Collectors.toList());

        assertFalse(musicList.isEmpty());
        assertEquals(tempList,musicList);
    }

    @Test
    @Tag("test-get-all-by-invalid-music-genre")
    void getByInvalidGenre() {

        String genre = "Scary";
        List<Music> musicList = musicDao.getByGenre(genre);

        assertTrue(musicList.isEmpty());
    }

    @Test
    @Tag("test-get-all-genre-categories")
    void getGenres() {

        List<String> genreList = musicDao.getGenre();
        assertThat(genreList.size()).isGreaterThan(0);
    }

    @Test
    @Tag("test-get-all-artist-categories")
    void getArtists() {

        List<String> artistList = musicDao.getArtist();
        assertThat(artistList.size()).isGreaterThan(0);
    }

    @Test
    @Tag("test-get-music-by-artist-name")
    void getByArtistName() {

        String artistName = "artist 1";
        List<Music> musicList = musicDao.getByArtistName(artistName);
        List<Music> tempList = musicList.stream().filter(music -> music.getArtistName().equals(artistName)).collect(Collectors.toList());

        assertFalse(musicList.isEmpty());
        assertEquals(tempList,musicList);

    }

    @Test
    @Tag("test-insert-music-into-library")
    void insertMusicInLibrary() {

        Music newMusic = new Music("music 12", "Sad", "artist 4");
        Integer insert = musicDao.insertMusicInLibrary(newMusic);

        assertThat(insert).isGreaterThan(0);

    }

    @Test
    @Tag("test-get-all-by-invalid-artist-name")
    void getByInvalidArtist() {

        String artistName = "artist 10";
        List<Music> musicList = musicDao.getByArtistName(artistName);

        assertTrue(musicList.isEmpty());
    }

    @Test
    @Tag("test-delete-music-from-library")
    void deleteMusic() {

        Integer musidId = 10;
        Integer delete = musicDao.deleteMusic(musidId);

        assertThat(delete).isEqualTo(1);

    }

    @Test
    @Tag("test-update-music-in-library")
    void updateMusic() {

        Integer musicId = 2;
        Music newMusic = new Music("Tennu Me", "Bollywood", "Shaan");
        Integer delete = musicDao.updateMusic(musicId, newMusic);

        assertThat(delete).isEqualTo(1);

    }

    @AfterEach
    public void afterEach(TestInfo info){
        System.out.println("test " + info.getTags() + " complete");
    }

    @AfterAll
    public static void finish(){
        System.out.println("Class testing Finished!");
    }
}