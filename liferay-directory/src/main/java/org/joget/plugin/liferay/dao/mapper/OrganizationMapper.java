package org.joget.plugin.liferay.dao.mapper;

import org.joget.directory.model.Organization;
import org.joget.directory.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: ARDI PRIASA
 * Date: 1/31/13
 * Time: 11:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class OrganizationMapper implements RowMapper {

    public Organization mapRow(ResultSet resultSet, int i) throws SQLException {
        Organization organization = new Organization();
        organization.setId(String.valueOf(resultSet.getInt("companyId")));
        organization.setName(resultSet.getString("name"));
//        organization.setDescription(resultSet.getString("comments"));
        return organization;
    }
}
