package com.omwan.latestadditions.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to access sqlite database for user playlist data.
 */
@Component
public class UserPlaylistRepository {
    private static final Logger s_Logger = LogManager.getLogger();

    @Value("${sqlite.url}")
    private String url;
    
    public List<UserPlaylist> findByUserId(String userId) {
        List<UserPlaylist> playlists = new ArrayList<>();
        String selectSql = "select * from user_playlist where user_id = ?";

        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement insertStmt = connection.prepareStatement(selectSql)
        ) {
            insertStmt.setString(1, userId);

            ResultSet rs = insertStmt.executeQuery();
            while (rs.next()) {
                UserPlaylist userPlaylist = new UserPlaylist();
                userPlaylist.setUserId(rs.getString("user_id"));
                userPlaylist.setPlaylistId(rs.getString("playlist_id"));
                playlists.add(userPlaylist);
            }
        } catch (SQLException e) {
            s_Logger.warn("Exception occurred while saving to sqlite", e);
        }

        return playlists;
    }

    public int deleteByPlaylistId(String playlistId) {
        String deleteSql = "delete from user_playlist where playlist_id = ?";
        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement insertStmt = connection.prepareStatement(deleteSql)
        ) {
            insertStmt.setString(1, playlistId);

            insertStmt.execute();
        } catch (SQLException e) {
            s_Logger.warn("Exception occurred while saving to sqlite", e);
        }

        return 0;
    }

    public void save(UserPlaylist userPlaylist) {
        String insertSql = "insert into user_playlist values(?, ?)";
        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement insertStmt = connection.prepareStatement(insertSql)
        ) {
            insertStmt.setString(1, userPlaylist.getUserId());
            insertStmt.setString(2, userPlaylist.getPlaylistId());

            insertStmt.execute();
        } catch (SQLException e) {
            s_Logger.warn("Exception occurred while saving to sqlite", e);
        }
    }

}
