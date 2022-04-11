package com.library.music.dao;

import com.library.music.model.Music;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MusicDaoImpl implements MusicDao{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public MusicDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String sqlQuery;

    private RowMapper<Music> rowMapper = (rs, rowNum) -> {
        Music music = new Music();
        music.setMusicId(rs.getInt("musicId"));
        music.setMusicName(rs.getString("musicName"));
        music.setMusicGenre(rs.getString("musicGenre"));
        music.setArtistName(rs.getString("artistName"));
        return music;
    };

    private RowMapper<String> getSingleRowMapper(String column){
        RowMapper<String> singleRowMapper = (rs, rowNum) -> {
            String genre = new String();
            genre = rs.getString(column);

            return genre;
        };

        return singleRowMapper;
    }

    @Override
    public Music getById(Integer musicId) {

        sqlQuery = "SELECT * FROM music where musicId = ?";
        Music music = null;
        music = jdbcTemplate.queryForObject(sqlQuery,rowMapper,musicId);

        return music;
    }

    @Override
    public List<Music> getAll() {

        sqlQuery = "select * from music";
        return jdbcTemplate.query(sqlQuery,rowMapper);

    }

    @Override
    public List<Music> getByGenre(String genre) {

        sqlQuery = "SELECT * FROM music where musicGenre = ?";
        return jdbcTemplate.query(sqlQuery,rowMapper,genre);

    }

    @Override
    public List<Music> getByArtistName(String artistName) {

        sqlQuery = "SELECT * FROM music where artistName = ?";
        return jdbcTemplate.query(sqlQuery,rowMapper,artistName);

    }

    @Override
    public Integer insertMusicInLibrary(Music music) {

        sqlQuery = "INSERT INTO music (musicName, musicGenre, artistName) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement(sqlQuery, new String[] {"musicId"});
                        ps.setString(1, music.getMusicName());
                        ps.setString(2,music.getMusicGenre());
                        ps.setString(3,music.getArtistName());
                        return ps;
                    }
                },
                keyHolder
        );

        return keyHolder.getKey().intValue();

    }

    @Override
    public Integer deleteMusic(Integer musicId) {

        sqlQuery = "DELETE FROM music WHERE musicId = ?";
        int delete = jdbcTemplate.update(sqlQuery, musicId);
        return delete;
    }

    @Override
    public List<String> getGenre() {

        sqlQuery = "SELECT DISTINCT musicGenre FROM music";
        List<String> genreList =  jdbcTemplate.query(sqlQuery,getSingleRowMapper("musicGenre"));
        return genreList;

    }

    @Override
    public List<String> getArtist() {

        sqlQuery = "SELECT DISTINCT artistName FROM music";
        List<String> artistList =  jdbcTemplate.query(sqlQuery,getSingleRowMapper("artistName"));
        return artistList;

    }

    @Override
    public Integer updateMusic(Integer musicId, Music music) {

        sqlQuery = "UPDATE music SET musicName = ?, musicGenre = ?, artistName = ? WHERE musicId = ?";

        return jdbcTemplate.update(sqlQuery, music.getMusicName(),music.getMusicGenre(),music.getArtistName(),musicId);

    }
}
