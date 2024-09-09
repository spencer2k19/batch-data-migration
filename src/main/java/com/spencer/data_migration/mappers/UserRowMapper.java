package com.spencer.data_migration.mappers;

import com.spencer.data_migration.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

@Slf4j
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setIdentity(rs.getString("identity"));
        user.setBio(rs.getString("bio"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setCreatedOn(rs.getTimestamp("created_on").toInstant().atZone(ZoneId.systemDefault()));
        user.setUpdatedOn(rs.getTimestamp("updated_on").toInstant().atZone(ZoneId.systemDefault()));
        return user;
    }
}