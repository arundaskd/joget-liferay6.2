package org.joget.plugin.liferay.dao.impl;

import org.joget.commons.util.PagedList;
import org.joget.directory.dao.DepartmentDao;
import org.joget.directory.dao.EmploymentDao;
import org.joget.directory.dao.UserDao;
import org.joget.directory.model.Employment;
import org.joget.directory.model.Role;
import org.joget.directory.model.User;
import org.joget.plugin.liferay.LiferayDirectoryManagerImpl;
import org.joget.plugin.liferay.util.PasswordEncrypt;
import org.joget.plugin.liferay.util.QueryList;
import org.joget.plugin.liferay.util.Roles;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ARDI PRIASA
 * Date: 1/30/13
 * Time: 9:25 AM
 * To change this template use File | Settings | File Templates.
 * Updated by
 * @author Arun Das Karanath
 * Date: 10/12/2014
 */
public class UserDaoLiferayImpl implements UserDao {

    private Map properties;
    private DepartmentDao departmentDao;
    private EmploymentDao employmentDao;
    public EmploymentDao getEmploymentDao() {
        if (employmentDao == null) {
            employmentDao = new EmploymentDaoLiferayImpl(properties);
        }
        return employmentDao;
    }

    public DepartmentDao getDepartmentDao() {
        if (departmentDao == null) {
            departmentDao = new DepartmentDaoLiferayImpl(properties);
        }
        return departmentDao;
    }

    public boolean authenticate(String algorithm, String encode, String username, String password){
        boolean result = false;
        boolean pwdValid = false;
        List<String> currentPasswordList = LiferayDirectoryManagerImpl.getJdbcTemplate().query(QueryList.SELECT_PWD,
                new Object[]{username}, new int[]{Types.VARCHAR}
                , new RowMapper<String>() {
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("password_");
            }
        });
        if (!currentPasswordList.isEmpty()) {
            String currentPassword = currentPasswordList.get(0);
           String pwd = PasswordEncrypt.encrypt(algorithm, password, currentPassword, 
        		   						encode.equalsIgnoreCase("base64") ? true : false);
			
            if (currentPassword.equals(pwd)) {
                pwdValid = true;
            }

            if (pwdValid) {
                int userCounter = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForInt(QueryList.SELECT_USER_BY_USERNAME_PWD
                        , new Object[]{pwd, username, new Integer(5)}
                        , new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER});
                if (userCounter != 0) {
                    result = true;
                }
            }
        }
        return result;
    }
    
    public UserDaoLiferayImpl(Map properties) {
        this.properties = properties;
    }

    public Boolean addUser(User user) {
        return null;
    }

    public Boolean updateUser(User user) {
        return null;
    }

    public Boolean deleteUser(String s) {
        return null;
    }

    public Boolean assignUserToGroup(String s, String s1) {
        return null;
    }

    public Boolean unassignUserFromGroup(String s, String s1) {
        return null;
    }

    public User getUser(String username) {
        User result = null;
        Set<Role> roles = new HashSet<Role>();
        String masterUser = properties.get("masterUser").toString();
        if (username.equals(masterUser)) {
            User user = new User();
            user.setId(masterUser);
            user.setUsername(masterUser);
            user.setFirstName(masterUser);
            user.setPassword(properties.get("masterUser").toString());
            user.setLastName(masterUser);
            user.setEmail("");
            user.setActive(new Integer(1));
            roles.add(Roles._getAdminUser());
            user.setRoles(roles);
            result = user;
        } else {
            User user = null;
            List<User> userList = LiferayDirectoryManagerImpl.getJdbcTemplate().query(QueryList.SELECT_USER_BY_USERNAME,
                    new Object[]{username}, new int[]{Types.VARCHAR}
                    , new RowMapper<User>() {
                public User mapRow(ResultSet resultSet, int i) throws SQLException {
                    User user = new User();
                    user.setId(String.valueOf(resultSet.getLong("userId")));
                    user.setPassword(resultSet.getString("password_"));
                    user.setUsername(resultSet.getString("emailAddress"));
                    user.setFirstName(resultSet.getString("firstName"));
                    user.setLastName(resultSet.getString("lastName"));
                    user.setEmail(resultSet.getString("emailAddress"));
                    user.setActive(resultSet.getInt("status") == 0 ? new Integer(1) : new Integer(0));
                    return user;
                }
            });
            if (!userList.isEmpty()) {
                user = userList.get(0);
                this.doProcessUser(user);
            }
            result = user;
        }
        return result;
    }

    public User getUserById(String userId) {
        User user = null;
        List<User> userList = LiferayDirectoryManagerImpl.getJdbcTemplate().query(QueryList.SELECT_USER_BY_USERID,
                new Object[]{Integer.valueOf(userId)}, new int[]{Types.BIGINT}
                , new RowMapper<User>() {
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setId(String.valueOf(resultSet.getLong("userId")));
                user.setPassword(resultSet.getString("password_"));
                user.setUsername(resultSet.getString("emailAddress"));
                user.setFirstName(resultSet.getString("firstName"));
                user.setLastName(resultSet.getString("lastName"));
                user.setEmail(resultSet.getString("emailAddress"));
                user.setActive(resultSet.getInt("status") == 0 ? new Integer(1) : new Integer(0));
                return user;
            }
        });
        if (!userList.isEmpty()) {
            user = userList.get(0);
            this.doProcessUser(user);
        }
        return user;
    }

    public User getHodByDepartmentId(String departmentId) {
        User result = null;
        Employment employment = getDepartmentDao().getDepartment(departmentId).getHod();
        if (employment!= null){
             result = getEmploymentDao().getEmployment(employment.getId()).getUser();
        }
        return result;
    }

    public Collection<User> getUsers(String filterString, String organizationId, String departmentId, String gardeId
            , String groupId, String roleId, String active, String sort, Boolean desc, Integer start, Integer rows) {
        List<User> users = new ArrayList<User>();
        if (filterString == null) {
            filterString = "";
        }

        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        if (organizationId == null && groupId == null) {
            maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_USER_BY_FILTER
                    , new Object[]{"%" + filterString + "%", "%" + filterString + "%", "%" + filterString + "%"}
                    , new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR});

        } else if (organizationId != null && groupId == null) {
            maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_USER_BY_COMPANY
                    , new Object[]{"%" + filterString + "%", "%" + filterString + "%", "%" + filterString + "%"
                    , Integer.valueOf(organizationId)}
                    , new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BIGINT});

        } else if (organizationId == null && groupId != null) {
            maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_USER_BY_GRP
                    , new Object[]{"%" + filterString + "%", "%" + filterString + "%", "%" + filterString + "%"
                    , Integer.valueOf(groupId)}
                    , new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BIGINT});
        }

        for (Map<String, Object> map : maps) {
            User user = new User();
            Long userId = (Long) map.get("userId");
            user.setId(String.valueOf(userId));
            user.setPassword(map.get("password_").toString());
            user.setUsername(map.get("emailAddress").toString());
            user.setFirstName(map.get("firstName").toString());
            user.setLastName(map.get("lastName").toString());
            user.setEmail(map.get("emailAddress").toString());
            user.setActive((Integer) map.get("status") == 0 ? new Integer(1) : new Integer(0));
            users.add(user);

        }

        if (users != null && (sort != null || desc != null || start != null || rows != null)) {
            PagedList<User> pagedList = new PagedList<User>(true, users, sort, desc, start, rows, users.size());
            return pagedList;
        } else {
            return users;
        }
    }

    public Long getTotalUsers(String filterString, String organizationId, String departmentId, String gardeId
            , String groupId, String roleId, String active) {
        return Integer.valueOf(getUsers(filterString, organizationId, departmentId, gardeId, groupId, roleId, active
                , null, null, null, null).size()).longValue();
    }

    public Collection<User> getUsersNotInGroup(String s, String s1, String s2, Boolean aBoolean, Integer integer
            , Integer integer1) {
        return null;
    }

    public Long getTotalUsersNotInGroup(String s, String s1) {
        return null;
    }

    public Collection<User> findUsers(String s, Object[] objects, String s1, Boolean aBoolean, Integer integer
            , Integer integer1) {
        return null;
    }

    public Long countUsers(String s, Object[] objects) {
        return null;
    }

    public Collection<User> getUsersSubordinate(String s, String s1, Boolean aBoolean, Integer integer, Integer integer1) {
        return null;
    }

    public Long getTotalUsersSubordinate(String s) {
        return null;
    }

    public User doProcessUser(User user) {
        Set<Role> roles = new HashSet<Role>();
        List<Map<String, Object>> roleMaps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_ROLE_BY_ID
                , new Object[]{Integer.valueOf(user.getId())}
                , new int[]{Types.BIGINT});

        for (Map<String, Object> map : roleMaps) {
            Role role = new Role();
            String name = map.get("name").toString();
            if (name.equalsIgnoreCase("Administrator")) {
                role.setId("ROLE_" + "ADMIN");
            } else if (name.equalsIgnoreCase("User")) {
                role.setId("ROLE_" + "USER");
            } else {
                role.setId("ROLE_" + name.replaceAll("\\s", "").toUpperCase());
            }
            role.setName(name);
            roles.add(role);
        }
        Set<Employment> employmentList = new HashSet<Employment>();
        List<Map<String, Object>> maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_EMP_BY_USERID
                , new Object[]{Integer.valueOf(user.getId())}
                , new int[]{Types.BIGINT});

        for (Map<String, Object> map : maps) {
            Employment employment = new Employment();
            Long contactId = (Long) map.get("contactId");
            employment.setId(String.valueOf(contactId));
            employmentList.add(employment);
        }
        user.setEmployments(employmentList);
        user.setRoles(roles);
        return user;
    }
}
