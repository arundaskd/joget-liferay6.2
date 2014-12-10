package org.joget.plugin.liferay;

import com.liferay.portal.PwdEncryptorException;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import org.joget.directory.dao.*;
import org.joget.directory.model.*;
import org.joget.directory.model.service.ExtDirectoryManager;
import org.joget.plugin.liferay.dao.impl.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.beans.PropertyVetoException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: ARDI PRIASA
 * Date: 1/30/13
 * Time: 9:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class LiferayDirectoryManagerImpl implements ExtDirectoryManager {

    private static Map properties;
    private UserDao userDao;
    private GroupDao groupDao;
    private OrganizationDao organizationDao;
    private DepartmentDao departmentDao;
    private EmploymentDao employmentDao;
    private GradeDao gradeDao;
    private RoleDao roleDao;
    private static JdbcTemplate jdbcTemplate;

    public LiferayDirectoryManagerImpl(Map properties) {
        this.properties = properties;
    }

    public static JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) {
            ComboPooledDataSource cpds = new ComboPooledDataSource();
            try {
                cpds.setDriverClass(properties.get("driver").toString());
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
            cpds.setJdbcUrl(properties.get("url").toString());
            cpds.setUser(properties.get("username").toString());
            cpds.setPassword(properties.get("password").toString());
            cpds.setMinPoolSize(1);
            cpds.setMaxPoolSize(20);
            jdbcTemplate = new JdbcTemplate(cpds);
        }
        return jdbcTemplate;
    }

    public static void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        LiferayDirectoryManagerImpl.jdbcTemplate = jdbcTemplate;
    }

    public UserDao getUserDao() {
        if (userDao == null) {
            userDao = new UserDaoLiferayImpl(properties);
        }
        return userDao;
    }

    public OrganizationDao getOrganizationDao() {
        if (organizationDao == null) {
            organizationDao = new OrganizationDaoLiferayImpl(properties);
        }
        return organizationDao;
    }

    public GroupDao getGroupDao() {
        if (groupDao == null) {
            groupDao = new GroupDaoLiferayImpl(properties);
        }
        return groupDao;
    }

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

    public Collection<Group> getGroupsByOrganizationId(String filterString, String organizationId, String sort
            , Boolean desc, Integer start, Integer rows) {
        return getGroupDao().getGroupsByOrganizationId(filterString, organizationId, null, null, null, null);
    }

    public Long getTotalGroupsByOrganizationId(String filterString, String organizationId) {
        return getGroupDao().getTotalGroupsByOrganizationId(filterString, organizationId);
    }

    public Collection<User> getUsers(String filterString, String organizationId, String departmentId, String gardeId
            , String groupId, String roleId, String active, String sort, Boolean desc, Integer start, Integer rows) {
        return getUserDao().getUsers(filterString, organizationId, departmentId, gardeId, groupId, roleId, active, sort, desc, start, rows);
    }

    public Long getTotalUsers(String filterString, String organizationId, String departmentId, String gardeId
            , String groupId, String roleId, String active) {
        return getUserDao().getTotalUsers(filterString, organizationId, departmentId, gardeId, groupId, roleId, active);
    }

    public Collection<Employment> getEmployments(String filterString, String organizationId, String departmentId
            , String gradeId, String sort, Boolean desc, Integer start, Integer rows) {
        return getEmploymentDao().getEmployments(filterString, organizationId, departmentId, gradeId, sort, desc, start, rows);
    }

    public Long getTotalEmployments(String filterString, String organizationId, String departmentId, String gradeId) {
        return getEmploymentDao().getTotalEmployments(filterString, organizationId, departmentId, gradeId);
    }

    public Department getDepartmentByName(String departmentName) {
        return getDepartmentDao().getDepartmentByName(departmentName);
    }

    public Department getParentDepartment(String id) {
        return getDepartmentDao().getParentDepartment(id);
    }

    public Department getParentDepartmentByName(String name) {
        return getDepartmentDao().getParentDepartmentByName(name);
    }

    public Collection<Department> getDepartmentsByParentId(String filterString, String parentId, String sort
            , Boolean desc, Integer start, Integer rows) {
        return getDepartmentDao().getDepartmentsByParentId(filterString, parentId, sort, desc, start, rows);
    }

    public Long getTotalDepartmentsByParentId(String filterString, String parentId) {
        return getDepartmentDao().getTotalDepartmentsByParentId(filterString, parentId);
    }

    public Collection<Department> getDepartmentsByOrganizationId(String filterString, String organizationId, String sort
            , Boolean desc, Integer start, Integer rows) {
        return getDepartmentDao().getDepartmentsByOrganizationId(filterString, organizationId, sort, desc, start, rows);
    }

    public Long getTotalDepartmentnsByOrganizationId(String filterString, String organizationId) {
        return getDepartmentDao().getTotalDepartmentsByOrganizationId(filterString, organizationId);
    }

    public Organization getOrganization(String id) {
        return getOrganizationDao().getOrganization(id);
    }

    public Organization getOrganizationByName(String name) {
        return getOrganizationDao().getOrganizationByName(name);
    }

    public Collection<Organization> getOrganizationsByFilter(String s, String sort, Boolean desc, Integer start
            , Integer rows) {
        return getOrganizationDao().getOrganizationsByFilter(s, sort, desc, start, rows);
    }

    public Long getTotalOrganizationsByFilter(String s) {
        return getOrganizationDao().getTotalOrganizationsByFilter(s);
    }

    public Employment getEmployment(String id) {
        return getEmploymentDao().getEmployment(id);
    }

    public Collection<Group> getGroupsByUserId(String filterString, String userId, String organizationId
            , Boolean inGroup, String sort, Boolean desc, Integer start, Integer rows) {
        return getGroupDao().getGroupsByUserId(filterString, userId, organizationId, inGroup, sort, desc, start, rows);
    }

    public Long getTotalGroupsByUserId(String filterString, String userId, String organizationId, Boolean inGroup) {
        return getGroupDao().getTotalGroupsByUserId(filterString, userId, organizationId, inGroup);
    }

    public boolean authenticate(String username, String password) {
        boolean result = false;
        String hashType = properties.get("hashType").toString();
        String encodeType = properties.get("encodeType").toString();
        String masterUser = properties.get("masterUser").toString();
        String masterPassword = properties.get("masterPassword").toString();

		if (((UserDaoLiferayImpl) getUserDao()).authenticate(hashType, encodeType, username, password)) {
		    result = true;
		}
	
        //master login
        if (username.equals(masterUser) && password.equals(masterPassword)) {
            result = true;
        }
        return result;
    }

    public Group getGroupById(String groupId) {
        return getGroupDao().getGroup(groupId);
    }

    public Group getGroupByName(String groupName) {
        return getGroupDao().getGroupByName(groupName);
    }

    public Collection<Group> getGroupByUsername(String username) {
        User user = getUserDao().getUser(username);
        if (user != null) {
            return getGroupDao().getGroupsByUserId("", user.getId(), null, true, "name", false, null, null);
        }
        return null;
    }

    public Collection<Group> getGroupList() {
        return getGroupDao().getGroupsByOrganizationId(null, null, null, null, null, null);
    }

    public Collection<Group> getGroupList(String filterString, String sort, Boolean desc, Integer start, Integer rows) {
        return getGroupDao().getGroupsByOrganizationId(filterString, null, sort, desc, start, rows);
    }

    public Long getTotalGroups() {
        return getGroupDao().getTotalGroupsByUserId(null, null, null, null);
    }

    public Collection<User> getUserByDepartmentId(String departmentId) {
        return getUserDao().getUsers(null, null, departmentId, null, null, null, null, "username", false, null, null);
    }

    public Collection<User> getUserByGroupId(String groupId) {
        return getUserDao().getUsers(null, null, null, null, groupId, null, null, "username", false, null, null);
    }

    public Collection<User> getUserByGroupName(String groupName) {
        Group group = getGroupDao().getGroupByName(groupName);
        if (group != null) {
            return getUserDao().getUsers(null, null, null, null, group.getId(), null, null, "username", false, null, null);
        }
        return null;    }

    public User getUserById(String userId) {
        return getUserDao().getUserById(userId);
    }

    public Collection<User> getUserByOrganizationId(String organizationId) {
        return getUserDao().getUsers(null, organizationId, null, null, null, null, null, "username", false, null, null);
    }

    public User getUserByUsername(String username) {
        return getUserDao().getUser(username);
    }

    public Collection<User> getUserList() {
        return getUserDao().getUsers(null, null, null, null, null, null, null, null, false, null, null);
    }

    public Collection<User> getUserList(String filterString, String sort, Boolean desc, Integer start, Integer rows) {
        return getUserDao().getUsers(filterString, null, null, null, null, null, null, sort, desc, start, rows);
    }

    public Long getTotalUsers() {
        return getUserDao().getTotalUsers(null, null, null, null, null, null, null);
    }

    public boolean isUserInGroup(String username, String groupName) {
        Group group = getGroupDao().getGroupByName(groupName);

        if (group != null) {
            Collection<User> users = getUserDao().getUsers(username, null, null, null, group.getId(), null, null, null
                    , null, null, null);

            if (users != null) {
                for (User user : users) {
                    if (user.getUsername().equals(username)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Collection<Role> getUserRoles(String username) {
        return getUserByUsername(username).getRoles();
    }

    public User getDepartmentHod(String departmentId) {
        Department department = getDepartmentDao().getDepartment(departmentId);
        if (department != null && department.getHod() != null) {
            return department.getHod().getUser();
        }
        return null;
    }

    public Collection<User> getUserHod(String username) {
        Collection<User> userList = new ArrayList<User>();

        User user = getUserByUsername(username);
        Collection<Employment> employments = user.getEmployments();
        Employment employment = getEmployment(employments.iterator().next().getId());
        Department department = employment.getDepartment();
        User hod = getDepartmentHod(department.getId());
        if (hod != null) {
            userList.add(hod);
        }
        return userList;
    }

    public Department getDepartmentById(String deptId) {
        return getDepartmentDao().getDepartment(deptId);
    }

    public Collection<Department> getDepartmentList() {
        return getDepartmentDao().getDepartmentsByOrganizationId(null, null, null, null, null, null);
    }

    public Collection<Department> getDepartmentList(String sort, Boolean desc, Integer start, Integer rows) {
        return getDepartmentDao().getDepartmentsByOrganizationId(null, null, sort, desc, start, rows);
    }

    public Collection<Department> getDepartmentListByOrganization(String organizationId, String sort, Boolean desc
            , Integer start, Integer rows) {
        return getDepartmentDao().getDepartmentsByOrganizationId(null, organizationId, sort, desc, start, rows);
    }

    public Long getTotalDepartments(String organizationId) {
        return getDepartmentDao().getTotalDepartmentsByOrganizationId(null, organizationId);
    }

    public Grade getGradeById(String s) {
        return null;
    }

    public Collection<Grade> getGradeList() {
        return null;
    }

    public Collection<User> getDepartmentUserByGradeId(String s, String s1) {
        return null;
    }

    public Grade getGradeByName(String s) {
        return null;
    }

    public Collection<Grade> getGradesByOrganizationId(String s, String s1, String s2, Boolean aBoolean, Integer integer
            , Integer integer1) {
        return null;
    }

    public Long getTotalGradesByOrganizationId(String s, String s1) {
        return null;
    }

    public Collection<User> getUsersSubordinate(String s, String s1, Boolean aBoolean, Integer integer, Integer integer1) {
        return null;
    }

    public Long getTotalUsersSubordinate(String s) {
        return null;
    }

    public Collection<User> getUserByGradeId(String s) {
        return null;
    }

    public Collection<User> getUserSubordinate(String s) {
        return null;
    }

    public Collection<User> getUserDepartmentUser(String s) {
        return null;
    }
}
