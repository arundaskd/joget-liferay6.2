package org.joget.plugin.liferay.dao.mapper;

import org.joget.directory.model.Group;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: ARDI PRIASA
 * Date: 1/31/13
 * Time: 5:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroupMapper implements RowMapper {

    public Group mapRow(ResultSet resultSet, int i) throws SQLException {
        Group group = new Group();
        Long userGroupId = resultSet.getLong("userGroupId");
        group.setId(String.valueOf(userGroupId));
        group.setName(resultSet.getString("name"));
        group.setDescription(resultSet.getString("description"));
        return group;
    }
}
