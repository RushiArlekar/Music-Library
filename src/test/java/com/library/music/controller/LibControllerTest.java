package com.library.music.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.music.exceptions.InvalidInputException;
import com.library.music.exceptions.NoMusicFoundException;
import com.library.music.model.Music;
import com.library.music.services.LibraryService;
import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.annotation.ServletSecurity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = LibController.class)
@AutoConfigureMockMvc
@ServletSecurity
class LibControllerTest {

    @MockBean
    private LibraryService libraryService;

    @InjectMocks
    private LibController libController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Music> testMusicList;
    ArgumentCaptor<Music> musicArgumentCaptor;

    @BeforeAll
    public static void init(){
        System.out.println("Testing Music DAO Implementation Class");
    }

    @BeforeEach
    public void beforeEach(TestInfo info){

        musicArgumentCaptor = ArgumentCaptor.forClass(Music.class);

//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();

        System.out.println("test " + info.getTags() + " started!");
        this.testMusicList = new ArrayList<>();
    }

    @Test
    @Tag("get-music-test")
    public void testGetMusic() throws Throwable {

        Integer musicId = 1;
        Music newMusic = new Music(musicId,"Tere Sang Yara","Romantic","Atif Aslam");

        CompletableFuture music = CompletableFuture.completedFuture(newMusic);
        when(libraryService.getMusicById(String.valueOf(musicId)))
                .thenReturn(music);
        String getUrl = "/library/music/track/1";

        MvcResult musicResult = mockMvc.perform(
                MockMvcRequestBuilders.get(getUrl)
        ).andExpect(status().isOk()).andReturn();

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(libraryService).getMusicById(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(musicId.toString());

        String expected = objectMapper.writeValueAsString(newMusic);
        String actual = musicResult.getResponse().getContentAsString();

        assertEquals(expected, actual, () -> "should return single music record");

    }

    @Test
    @Ignore
    @Tag("test-get-music-with-non-existing-id")
    public void testGetMusicWithNonExistingId() throws Exception {

        Integer musicId = 300;

        when(libraryService.getMusicById(String.valueOf(musicId))).thenThrow(new NoMusicFoundException(404,"No music files found"));
        String getUrl = "/library/music/track/"+musicId;

        MvcResult resultedMusic = mockMvc.perform(
                MockMvcRequestBuilders.get(getUrl)
        ).andExpect(status().isNotFound()).andReturn();

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(libraryService).getMusicById(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(musicId.toString());

        String expected = objectMapper.writeValueAsString(HttpStatus.NOT_FOUND.value());
        String actual = objectMapper.writeValueAsString(resultedMusic.getResponse().getStatus());
        assertEquals(expected, actual, () -> "should return no records found, 404 not found");
    }

    @Test
    @Tag("test-get-music-with-string-id")
    public void testGetMusicInvalidId() throws Exception {

        String musicId = "yt";
        String getUrl = "/library/music/track/"+musicId;

        when(libraryService.getMusicById(musicId)).thenThrow(new InvalidInputException(407,"Invalid music id provided"));

        MvcResult resultedMusic = mockMvc.perform(
                MockMvcRequestBuilders.get(getUrl)
        ).andExpect(status().isNotFound()).andReturn();

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(libraryService).getMusicById(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(musicId);

        String expected = "Invalid music id provided";
        String actual = resultedMusic.getResponse().getContentAsString();

        assertEquals(expected, actual, () -> "should return invalid id error, Bad request");

    }

    @Test
    @Tag("test-find-all-music-on-empty-library")
    public void testGetAllOnEmptyLibrary() throws Exception {

        String getUrl = "/library/music";

        when(libraryService.getAllMusic()).thenThrow(new NoMusicFoundException(404,"library is empty"));

        MvcResult resultedMusic = mockMvc.perform(
                MockMvcRequestBuilders.get(getUrl)
        ).andExpect(status().isNotFound()).andReturn();

        String expected = "library is empty";
        String actual = resultedMusic.getResponse().getContentAsString();
        assertEquals(expected, actual, () -> "should return no tracks as library is empty");

    }

    @Test
    @Tag("test-get-all-genres")
    public void getAllGenres() throws Exception {

        List<String > genreList = Arrays.asList("genre 1", "genre 2", "genre 3");
        CompletableFuture musicList = CompletableFuture.completedFuture(genreList);
        when(libraryService.getAllGenres()).thenReturn(musicList);

        String url = "/library/music/genres";
        MvcResult genreResult = mockMvc.perform(MockMvcRequestBuilders
                        .get(url))
                .andExpect(status().isOk()).andReturn();

        int expected = genreList.size();
        int actual = genreResult.getResponse().getContentAsString().split(",").length;

        assertEquals(expected, actual,()->"should match size of 2 lists");

    }

    @Test
    @Tag("test-get-all-artists")
    public void getAllArtists() throws Exception {

        List<String > artistList = Arrays.asList("artist 1", "artist 2", "artist 3");
        CompletableFuture musicList = CompletableFuture.completedFuture(artistList);
        when(libraryService.getAllArtists()).thenReturn(musicList);

        String url = "/library/music/artists";
        MvcResult genreResult = mockMvc.perform(MockMvcRequestBuilders
                        .get(url))
                .andExpect(status().isOk()).andReturn();

        int expected = artistList.size();
        int actual = genreResult.getResponse().getContentAsString().split(",").length;

        assertEquals(expected, actual,()->"should match size of 2 lists");

    }

    @Test
    @Tag("insert-music-into-library")
    public void insertMusicInLibrary() throws Exception {

        Integer musicId = 50;
        Music newMusic = new Music("new music", "new genre","new artist");
        CompletableFuture newMusicId = CompletableFuture.completedFuture(musicId);
        when(libraryService.insertMusic(any(Music.class))).thenReturn(newMusicId);

        String url = "/library/music/track";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post(url)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsBytes(newMusic))
                        .contentType("application/json")
                )
                .andExpect(status().isOk())
                .andReturn();

        verify(libraryService, times(1)).insertMusic(musicArgumentCaptor.capture());
        assertThat(musicArgumentCaptor.getValue().getMusicName()).isEqualTo("new music");
        assertThat(musicArgumentCaptor.getValue().getMusicId()).isEqualTo(null);

        String actual = result.getResponse().getContentAsString();
        String expected = objectMapper.writeValueAsString(musicId);
        assertEquals(expected,actual,()-> "should match the id");

    }

    @Test
    @Tag("get-all-music-tracks")
    void getAllMusicTracks() throws Exception {

        testMusicList.add(
                new Music("music 1", "genre 1", "artist 1")
        );
        testMusicList.add(
                new Music("music 2", "genre 2", "artist 1")
        );
        testMusicList.add(
                new Music("music 3", "genre 1", "artist 2")
        );
        testMusicList.add(
                new Music("music 4", "genre 3", "artist 1")
        );

        String getUrl = "/library/music";
        CompletableFuture musicList = CompletableFuture.completedFuture(testMusicList);
        when(libraryService.getAllMusic()).thenReturn(musicList);

        MvcResult resultedMusic = mockMvc.perform(
                MockMvcRequestBuilders.get(getUrl)
        ).andExpect(status().isOk()).andReturn();

        String expected = objectMapper.writeValueAsString(testMusicList);
        String actual = resultedMusic.getResponse().getContentAsString();
        assertEquals(expected, actual, () -> "should return music list");

    }

    @Test
    @Tag("get-music-by-genre")
    void getMusicTrackByGenre() throws Exception {

        testMusicList.add(
                new Music("music 1", "genre 1", "artist 1")
        );
        testMusicList.add(
                new Music("music 3", "genre 1", "artist 2")
        );

        String getUrl = "/library/music/genre/genre 1";
        CompletableFuture musicGenreList = CompletableFuture.completedFuture(testMusicList);
        when(libraryService.getMusicByGenre("genre 1")).thenReturn(musicGenreList);

        MvcResult resultedMusic = mockMvc.perform(
                MockMvcRequestBuilders.get(getUrl)
        ).andExpect(status().isOk()).andReturn();

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(libraryService).getMusicByGenre(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo("genre 1");

        String expected = objectMapper.writeValueAsString(testMusicList);
        String actual = resultedMusic.getResponse().getContentAsString();
        assertEquals(expected, actual, () -> "should return music list");

    }

    @Test
    @Tag("update-music-track")
    void updateMusicTrack() throws Exception {
        String musicId = "4";
        String url = "/library/music/track/"+musicId;
        Music newMusic = new Music(Integer.parseInt(musicId), "changed name", "changed genre", "changed artistName");

        CompletableFuture booleann = CompletableFuture.completedFuture(true);
        when(libraryService.updateMusicTrack(any(String.class),any(Music.class))).thenReturn(booleann);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .put(url)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newMusic))
        ).andExpect(status().isOk()).andReturn();

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(libraryService).updateMusicTrack(stringArgumentCaptor.capture(),musicArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(musicId);
        assertThat(musicArgumentCaptor.getValue().getMusicId()).isEqualTo(Integer.valueOf(musicId));

        String actual = result.getResponse().getContentAsString();
        String expected = objectMapper.writeValueAsString(true);

        assertEquals(expected, actual);

    }

    @Test
    @Tag("delete-music-track")
    void deleteMusicTrack() throws Exception {

        String musicId = "4";
        String url = "/library/music/track/"+musicId;

        CompletableFuture response = CompletableFuture.completedFuture(1);
        when(libraryService.deleteMusic(any(String.class))).thenReturn(response);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .delete(url)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        ).andExpect(status().isOk()).andReturn();

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(libraryService).deleteMusic(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(musicId);

        String actual = result.getResponse().getContentAsString();
        String expected = objectMapper.writeValueAsString(1);

        assertEquals(expected, actual, ()-> "should return 1");

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