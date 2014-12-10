package org.joget.plugin.liferay.dao.impl;

import org.joget.commons.util.PagedList;
import org.joget.directory.dao.GroupDao;
import org.joget.directory.model.Group;
import org.joget.plugin.liferay.LiferayDirectoryManagerImpl;
import org.joget.plugin.liferay.dao.mapper.GroupMapper;
import org.joget.plugin.liferay.util.QueryList;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ARDI PRIASA
 * Date: 1/31/13
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroupDaoLiferayImpl implements GroupDao {

    private Map properties;

    public GroupDaoLiferayImpl(Map properties) {
        this.properties = properties;
    }

    public Boolean addGroup(Group group) {
        return null;
    }

    public Boolean updateGroup(Group group) {
        return null;
    }

    public Boolean deleteGroup(String s) {
        return null;
    }

    public Group getGroup(String groupId) {
        Group group = (Group) LiferayDirectoryManagerImpl.getJdbcTemplate().queryForObject(QueryList.SELECT_GRP_BY_ID, new Object[]{Integer.valueOf(groupId)}, new GroupMapper());
        return group;
    }

    public Group getGroupByName(String name) {
        List<Group> groupList = LiferayDirectoryManagerImpl.getJdbcTemplate().query(QueryList.SELECT_GRP_BY_NAME,
                new Object[]{name}, new int[]{Types.VARCHAR}
                , new RowMapper<Group>() {
            public Group mapRow(ResultSet resultSet, int i) throws SQLException {
                Group group = new Group();
                Long userGroupId = resultSet.getLong("userGroupId");
                group.setId(String.valueOf(userGroupId));
                group.setName(resultSet.getString("name"));
                group.setDescription(resultSet.getString("description"));
                return group;
            }
        });

        if (!groupList.isEmpty()) {
            return groupList.get(0);
        }
        return null;
    }

    public Collection<Group> getGroupsByOrganizationId(String filterString, String organizationId, String sort, Boolean desc, Integer start, Integer rows) {
        return getGroupsByUserId(filterString, null, organizationId, null, sort, desc, start, rows);
    }

    public Long getTotalGroupsByOrganizationId(String filterString, String organizationId) {
        return getTotalGroupsByUserId(filterString, null, null, null);
    }

    public Collection<Group> getGroupsByUserId(String filterString, String userId, String organizationId, Boolean inGroup, String sort, Boolean desc, Integer start, Integer rows) {
        List<Group> groups = new ArrayList<Group>();
        List<Map<String, Object>> maps;
        if (filterString == null) {
            filterString = "";
        }
        if (userId != null && organizationId == null) {
            maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_GRP_BY_USER
                    , new Object[]{"%" + filterString + "%", "%" + filterString + "%", Integer.valueOf(userId)}
                    , new int[]{Types.VARCHAR, Types.VARCHAR, Types.BIGINT});


        } else if (userId != null && organizationId != null) {
            maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_GRP_BY_USER_COMPANY
                    , new Object[]{"%" + filterString + "%", "%" + filterString + "%"
                    , Integer.valueOf(userId), Integer.valueOf(organizationId)}
                    , new int[]{Types.VARCHAR, Types.VARCHAR, Types.BIGINT, Types.BIGINT});
        } else {
            maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_GRP_BY_FILTER
                    , new Object[]{"%" + filterString + "%", "%" + filterString + "%"}
                    , new int[]{Types.VARCHAR, Types.VARCHAR});
        }

        for (Map<String, Object> map : maps) {
            Group group = new Group();
            Long userGroupId = (Long) map.get("userGroupId");
            group.setId(String.valueOf(userGroupId));
            group.setName(map.get("name").toString());
            group.setDescription(map.get("description").toString());
            groups.add(group);
        }

        if (groups != null && (sort != null || desc != null || start != null || rows != null)) {
            PagedList<Group> pagedList = new PagedList<Group>(true, groups, sort, desc, start, rows, groups.size());
            return pagedList;
        } else {
            return groups;
        }
    }

    public Long getTotalGroupsByUserId(String filterString, String userId, String organizationId, Boolean inGroup) {
        return Integer.valueOf(getGroupsByUserId(filterString, userId, organizationId, inGroup, null, null, null, null).size())
                .longValue();
    }

    public Collection<Group> findGroups(String s, Object[] objects, String s1, Boolean aBoolean, Integer integer, Integer integer1) {
        return null;
    }

    public Long countGroups(String s, Object[] objects) {
        return null;
    }
}
