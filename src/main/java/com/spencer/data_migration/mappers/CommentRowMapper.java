package com.spencer.data_migration.mappers;

import com.spencer.data_migration.models.Comment;
import com.spencer.data_migration.models.Publication;
import com.spencer.data_migration.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

public class CommentRowMapper implements RowMapper<Comment> {
    @Override
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setContent("content");
        comment.setCreatedOn(rs.getTimestamp("created_on").toInstant().atZone(ZoneId.systemDefault()));
        comment.setUpdatedOn(rs.getTimestamp("updated_on").toInstant().atZone(ZoneId.systemDefault()));

        Long publicationId = rs.getLong("publication_id");
        if(!rs.wasNull()) {
            Publication publication = new Publication();
            publication.setId(publicationId);
            comment.setPublication(publication);
        }


        Long userId = rs.getLong("user_id");
        if(!rs.wasNull()) {
            User user = new User();
            user.setId(userId);
            comment.setUser(user);
        }
        return comment;

    }
}
