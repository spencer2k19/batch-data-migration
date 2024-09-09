package com.spencer.data_migration.mappers;

import com.spencer.data_migration.models.Publication;
import com.spencer.data_migration.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

public class PublicationRowMapper implements RowMapper<Publication> {
    @Override
    public Publication mapRow(ResultSet rs, int rowNum) throws SQLException {
        Publication publication = new Publication();
        publication.setId(rs.getLong("id"));
        publication.setMedia(rs.getString("media"));
        publication.setContent(rs.getString("content"));
        publication.setCreatedOn(rs.getTimestamp("created_on").toInstant().atZone(ZoneId.systemDefault()));
        publication.setUpdatedOn(rs.getTimestamp("updated_on").toInstant().atZone(ZoneId.systemDefault()));

        Long userId = rs.getLong("user_id");
        if(!rs.wasNull()) {
            User user = new User();
            user.setId(userId);
            publication.setUser(user);
        }
        return publication;
    }
}
