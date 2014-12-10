package org.joget.plugin.liferay.dao.impl;

import org.joget.commons.util.PagedList;
import org.joget.directory.dao.DepartmentDao;
import org.joget.directory.dao.EmploymentDao;
import org.joget.directory.dao.OrganizationDao;
import org.joget.directory.dao.UserDao;
import org.joget.directory.model.*;
import org.joget.plugin.liferay.LiferayDirectoryManagerImpl;
import org.joget.plugin.liferay.dao.mapper.OrganizationMapper;
import org.joget.plugin.liferay.util.QueryList;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ARDI PRIASA
 * Date: 2/1/13
 * Time: 9:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class EmploymentDaoLiferayImpl implements EmploymentDao {

    private Map properties;
    private UserDao userDao;
    private OrganizationDao organizationDao;
    private DepartmentDao departmentDao;

    public EmploymentDaoLiferayImpl(Map properties) {
        this.properties = properties;
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

    public DepartmentDao getDepartmentDao() {
        if (departmentDao == null) {
            departmentDao = new DepartmentDaoLiferayImpl(properties);
        }
        return departmentDao;
    }

    public Boolean addEmployment(Employment employment) {
        return null;
    }

    public Boolean updateEmployment(Employment employment) {
        return null;
    }

    public Boolean deleteEmployment(String s) {
        return null;
    }

    public Employment getEmployment(String id) {
        List<Employment> employmentList = new ArrayList<Employment>();
        List<Map<String, Object>> maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_EMP
                , new Object[]{Integer.valueOf(id)}
                , new int[]{Types.BIGINT});

        for (Map<String, Object> map : maps) {
            Employment employment = new Employment();
            Long contactId = (Long) map.get("contactId");
            employment.setId(String.valueOf(contactId));
            String userId = map.get("userId").toString();
            User user = getUserDao().getUserById(userId);
            employment.setUser(user);
            employment.setEmployeeCode(map.get("employeeNumber").toString());
            employment.setRole(map.get("jobTitle").toString());

            Organization organization = (Organization) LiferayDirectoryManagerImpl.getJdbcTemplate()
                    .queryForObject(QueryList.SELECT_COMPANY_BY_USER
                            , new Object[]{userId}
                            , new OrganizationMapper());
            employment.setOrganization(organization);

            List<Map<String, Object>> depts = LiferayDirectoryManagerImpl.getJdbcTemplate()
                    .queryForList(QueryList.SELECT_ORG_BY_USER
                            , new Object[]{Integer.valueOf(userId)}
                            , new int[]{Types.BIGINT});
            List<Department> departments = new ArrayList<Department>();
            for (Map<String, Object> dept : depts) {
                Department department = new Department();
                Long departmentId = (Long) dept.get("organizationId");
                department.setId(String.valueOf(departmentId));
                department.setName(dept.get("name").toString());
                department.setDescription(dept.get("comments").toString());
                departments.add(department);
            }
            if (!departments.isEmpty()) {
                employment.setDepartment(departments.get(0));

                Long roleId = null;
                List<Long> roleList = LiferayDirectoryManagerImpl.getJdbcTemplate().query(QueryList.SELECT_ROLEID_BY_NAME,
                        new Object[]{properties.get("ownerRole").toString()}, new int[]{Types.VARCHAR}
                        , new RowMapper<Long>() {
                    public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getLong("roleId");
                    }
                });

                if (!roleList.isEmpty()) {
                    roleId = roleList.get(0);
                    List<Long> hodList = LiferayDirectoryManagerImpl.getJdbcTemplate().query(QueryList.SELECT_HOD_ID,
                            new Object[]{Integer.valueOf(departments.get(0).getId()), roleId}
                            , new int[]{Types.BIGINT, Types.BIGINT}, new RowMapper<Long>() {
                        public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                            return resultSet.getLong("contactId");
                        }
                    });

                    if (!hodList.isEmpty()) {
                        Long hodId = hodList.get(0);
                        List<Employment> employmentList2 = new ArrayList<Employment>();
                        List<Map<String, Object>> maps2 = LiferayDirectoryManagerImpl.getJdbcTemplate()
                                .queryForList(QueryList.SELECT_EMP
                                        , new Object[]{hodId}
                                        , new int[]{Types.BIGINT});
                        EmploymentReportTo reportTo = new EmploymentReportTo();
                        for (Map<String, Object> map2 : maps2) {
                            Employment employment2 = new Employment();
                            Long contactId2 = (Long) map2.get("contactId");
                            employment2.setId(String.valueOf(contactId2));
                            String userId2 = map2.get("userId").toString();
                            User user2 = getUserDao().getUserById(userId2);
                            employment2.setUser(user2);
                            employment2.setEmployeeCode(map2.get("employeeNumber").toString());
                            employment2.setRole(map2.get("jobTitle").toString());
                            employmentList2.add(employment2);
                        }
                        if (!employmentList2.isEmpty()) {
                            if (hodId.compareTo(Long.valueOf(id)) != 0) {
                                reportTo.setReportTo(employmentList2.get(0));
                                employment.setEmploymentReportTo(reportTo);
                            } else {
                                Set<User> hodUsers = new HashSet<User>();
                                hodUsers.add(employmentList2.get(0).getUser());
                                employment.setHods(hodUsers);
                            }
                        }
                    }
                }
            }
            employmentList.add(employment);
        }

        return employmentList.get(0);
    }

    public Collection<Employment> getEmployments(String filterString, String organizationId, String departmentId, String gradeId, String sort, Boolean desc, Integer start, Integer rows) {
        List<Employment> employmentList = new ArrayList<Employment>();
        if (filterString == null) {
            filterString = "";
        }

        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        if (organizationId == null && departmentId == null) {
            maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_EMP_BY_FILTER
                    , new Object[]{"%" + filterString + "%", "%" + filterString + "%", "%" + filterString + "%"}
                    , new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR});
        } else if (organizationId != null && departmentId == null) {
            maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_EMP_BY_ORG
                    , new Object[]{"%" + filterString + "%", "%" + filterString + "%", "%" + filterString + "%"
                    , Integer.valueOf(organizationId)}
                    , new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BIGINT});
        } else if (organizationId == null && departmentId != null) {
            maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_EMP_BY_DEPT
                    , new Object[]{"%" + filterString + "%", "%" + filterString + "%", "%" + filterString + "%"
                    , Integer.valueOf(departmentId)}
                    , new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BIGINT});
        } else {
            maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_EMP_BY_ORG_DEPT
                    , new Object[]{"%" + filterString + "%", "%" + filterString + "%", "%" + filterString + "%"
                    , Integer.valueOf(departmentId), Integer.valueOf(organizationId)}
                    , new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BIGINT, Types.BIGINT});
        }


        for (Map<String, Object> map : maps) {
            Employment employment = new Employment();
            Long contactId = (Long) map.get("contactId");
            employment.setId(String.valueOf(contactId));
            String userId = map.get("userId").toString();
            User user = getUserDao().getUserById(userId);
            employment.setUser(user);

            employment.setEmployeeCode(map.get("employeeNumber").toString());
            employment.setRole(map.get("jobTitle").toString());
            Organization organization = (Organization) LiferayDirectoryManagerImpl.getJdbcTemplate().queryForObject(QueryList.SELECT_COMPANY_BY_USER
                    , new Object[]{userId}
                    , new OrganizationMapper());
            employment.setOrganization(organization);
            employmentList.add(employment);
        }

        if (employmentList != null && (sort != null || desc != null || start != null || rows != null)) {
            PagedList<Employment> pagedList = new PagedList<Employment>(true, employmentList, sort, desc, start, rows
                    , employmentList.size());
            return pagedList;
        } else {
            return employmentList;
        }
    }

    public Long getTotalEmployments(String filterString, String organizationId, String departmentId, String gradeId) {
        return Integer.valueOf(getEmployments(filterString, organizationId, null, null, null, null, null, null).size()).longValue();
    }

    public Collection<Employment> findEmployments(String s, Object[] objects, String s1, Boolean aBoolean, Integer integer, Integer integer1) {
        return null;
    }

    public Long countEmployments(String s, Object[] objects) {
        return null;
    }

    public Boolean assignUserAsDepartmentHOD(String s, String s1) {
        return null;
    }

    public Boolean unassignUserAsDepartmentHOD(String s, String s1) {
        return null;
    }

    public Boolean assignUserToOrganization(String s, String s1) {
        return null;
    }

    public Boolean unassignUserFromOrganization(String s, String s1) {
        return null;
    }

    public Boolean assignUserToDepartment(String s, String s1) {
        return null;
    }

    public Boolean unassignUserFromDepartment(String s, String s1) {
        return null;
    }

    public Boolean assignUserToGrade(String s, String s1) {
        return null;
    }

    public Boolean unassignUserFromGrade(String s, String s1) {
        return null;
    }

    public Boolean assignUserReportTo(String s, String s1) {
        return null;
    }

    public Boolean unassignUserReportTo(String s) {
        return null;
    }

    public Collection<Employment> getEmploymentsNoHaveOrganization(String s, String s1, Boolean aBoolean, Integer integer, Integer integer1) {
        return null;
    }

    public Long getTotalEmploymentsNoHaveOrganization(String s) {
        return null;
    }

    public Collection<Employment> getEmploymentsNotInDepartment(String s, String s1, String s2, String s3, Boolean aBoolean, Integer integer, Integer integer1) {
        return null;
    }

    public Long getTotalEmploymentsNotInDepartment(String s, String s1, String s2) {
        return null;
    }

    public Collection<Employment> getEmploymentsNotInGrade(String s, String s1, String s2, String s3, Boolean aBoolean, Integer integer, Integer integer1) {
        return null;
    }

    public Long getTotalEmploymentsNotInGrade(String s, String s1, String s2) {
        return null;
    }
}
