package org.joget.plugin.liferay.dao.mapper;

import org.joget.directory.model.Role;
import org.joget.directory.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: ARDI PRIASA
 * Date: 1/30/13
 * Time: 11:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserMapper implements RowMapper {

    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User();
        user.setId(String.valueOf(resultSet.getLong("userId")));
        user.setPassword(resultSet.getString("password_"));
        user.setUsername(resultSet.getString("emailAddress"));
        user.setFirstName(resultSet.getString("firstName"));
        user.setLastName(resultSet.getString("lastName"));
        user.setEmail(resultSet.getString("emailAddress"));
        user.setActive(resultSet.getInt("status") == 0?new Integer(1):new Integer(0));
        return user;
    }
}
