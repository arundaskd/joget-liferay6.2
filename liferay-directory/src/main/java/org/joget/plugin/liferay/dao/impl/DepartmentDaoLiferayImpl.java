package org.joget.plugin.liferay.dao.impl;

import org.joget.commons.util.PagedList;
import org.joget.directory.dao.DepartmentDao;
import org.joget.directory.dao.EmploymentDao;
import org.joget.directory.dao.OrganizationDao;
import org.joget.directory.model.Department;
import org.joget.plugin.liferay.LiferayDirectoryManagerImpl;
import org.joget.plugin.liferay.util.QueryList;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: ARDI PRIASA
 * Date: 2/4/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class DepartmentDaoLiferayImpl implements DepartmentDao {

    private Map properties;
    private OrganizationDao organizationDao;
    private EmploymentDao employmentDao;


    public DepartmentDaoLiferayImpl(Map properties) {
        this.properties = properties;
    }

    public OrganizationDao getOrganizationDao() {
        if (organizationDao == null) {
            organizationDao = new OrganizationDaoLiferayImpl(properties);
        }
        return organizationDao;
    }

    public EmploymentDao getEmploymentDao() {
        if (employmentDao == null) {
            employmentDao = new EmploymentDaoLiferayImpl(properties);
        }
        return employmentDao;
    }

    public Boolean addDepartment(Department department) {
        return null;
    }

    public Boolean updateDepartment(Department department) {
        return null;
    }

    public Boolean deleteDepartment(String s) {
        return null;
    }

    public Department getDepartment(String deptId) {
        List<Department> departments = new ArrayList<Department>();
        List<Map<String, Object>> maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_DEPT_BY_ID
                , new Object[]{Integer.valueOf(deptId)}
                , new int[]{Types.BIGINT});

        for (Map<String, Object> map : maps) {
            Department department = new Department();
            Long departmentId = (Long) map.get("organizationId");
            department.setId(String.valueOf(departmentId));
            department.setName(map.get("name").toString());
            department.setDescription(map.get("comments").toString());

            String companyId = map.get("companyId").toString();
            department.setOrganization(getOrganizationDao().getOrganization(companyId));

            Long parentId = (Long) map.get("parentOrganizationId");
            if (parentId.compareTo((long) 0) != 0) {
                department.setParent(getDepartment(String.valueOf(parentId)));
            }

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
                        new Object[]{departmentId, roleId}, new int[]{Types.BIGINT, Types.BIGINT}, new RowMapper<Long>() {
                    public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getLong("contactId");
                    }
                });

                if (!hodList.isEmpty()) {
                    Long hodId = hodList.get(0);
                    department.setHod(getEmploymentDao().getEmployment(String.valueOf(hodId)));
                }
            }
            departments.add(department);
        }
        return departments.size() > 0 ? departments.get(0) : null;
    }

    public Department getDepartmentByName(String departmentName) {
        List<Department> departments = new ArrayList<Department>();
        List<Map<String, Object>> maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_DEPT_BY_NAME
                , new Object[]{departmentName}
                , new int[]{Types.VARCHAR});

        for (Map<String, Object> map : maps) {
            Department department = new Department();
            Long departmentId = (Long) map.get("organizationId");
            department.setId(String.valueOf(departmentId));
            department.setName(map.get("name").toString());
            department.setDescription(map.get("comments").toString());

            String companyId = map.get("companyId").toString();
            department.setOrganization(getOrganizationDao().getOrganization(companyId));

            Long parentId = (Long) map.get("parentOrganizationId");
            if (parentId.compareTo((long) 0) != 0) {
                department.setParent(getDepartment(String.valueOf(parentId)));
            }

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
                        new Object[]{departmentId, roleId}, new int[]{Types.BIGINT, Types.BIGINT}, new RowMapper<Long>() {
                    public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getLong("contactId");
                    }
                });

                if (!hodList.isEmpty()) {
                    Long hodId = hodList.get(0);
                    department.setHod(getEmploymentDao().getEmployment(String.valueOf(hodId)));
                }
            }
            departments.add(department);
        }
        return departments.size() > 0 ? departments.get(0) : null;
    }

    public Department getParentDepartment(String id) {
        try {
            Department department = getDepartment(id);
            return department.getParent();
        } catch (Exception e) {
            Logger.getLogger(DepartmentDaoLiferayImpl.class.getName()).log(Level.SEVERE,null,e);
        }
        return null;
    }

    public Department getParentDepartmentByName(String name) {
        try {
            Department department = getDepartmentByName(name);
            return department.getParent();
        } catch (Exception e) {
            Logger.getLogger(DepartmentDaoLiferayImpl.class.getName()).log(Level.SEVERE,null,e);
        }
        return null;
    }

    public Collection<Department> getDepartmentsByParentId(String filterString, String pId, String sort, Boolean desc, Integer start, Integer rows) {
        List<Department> departments = new ArrayList<Department>();
        if (filterString == null) {
            filterString = "";
        }

        List<Map<String, Object>> maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_DEPT_BY_FILTER_PARENT
                , new Object[]{"%" + filterString + "%", "%" + filterString + "%", Integer.valueOf(pId)}
                , new int[]{Types.VARCHAR, Types.VARCHAR, Types.BIGINT});

        for (Map<String, Object> map : maps) {
            Department department = new Department();
            Long deptId = (Long) map.get("organizationId");
            department.setId(String.valueOf(deptId));
            department.setName(map.get("name").toString());
            department.setDescription(map.get("comments").toString());

            String companyId = map.get("companyId").toString();
            department.setOrganization(getOrganizationDao().getOrganization(companyId));

            Long parentId = (Long) map.get("parentOrganizationId");
            if (parentId.compareTo((long) 0) != 0) {
                department.setParent(getDepartment(String.valueOf(parentId)));
            }
            departments.add(department);
        }

        if (departments != null && (sort != null || desc != null || start != null || rows != null)) {
            PagedList<Department> pagedList = new PagedList<Department>(true, departments, sort, desc, start
                    , rows, departments.size());
            return pagedList;
        } else {
            return departments;
        }

    }

    public Long getTotalDepartmentsByParentId(String filterString, String parentId) {
        return Integer.valueOf(getDepartmentsByParentId(filterString, parentId, null, null, null, null).size()).longValue();
    }

    public Collection<Department> getDepartmentsByOrganizationId(String filterString, String organizationId, String sort, Boolean desc, Integer start, Integer rows) {
        List<Department> departments = new ArrayList<Department>();
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        if (filterString == null) {
            filterString = "";
        }

        if (organizationId != null) {
            maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_DEPT_BY_FILTER_ORG
                    , new Object[]{"%" + filterString + "%", "%" + filterString + "%", Integer.valueOf(organizationId)}
                    , new int[]{Types.VARCHAR, Types.VARCHAR, Types.BIGINT});
        } else {
           maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_DEPT_BY_FILTER
                   , new Object[]{"%" + filterString + "%", "%" + filterString + "%"}
                   , new int[]{Types.VARCHAR, Types.VARCHAR});
        }


        for (Map<String, Object> map : maps) {
            Department department = new Department();
            Long deptId = (Long) map.get("organizationId");
            department.setId(String.valueOf(deptId));
            department.setName(map.get("name").toString());
            department.setDescription(map.get("comments").toString());
            Long parentId = (Long) map.get("parentOrganizationId");
            if (parentId.compareTo((long) 0) != 0) {
                department.setParent(getDepartment(String.valueOf(parentId)));
            }
            departments.add(department);
        }

        if (departments != null && (sort != null || desc != null || start != null || rows != null)) {
            PagedList<Department> pagedList = new PagedList<Department>(true, departments, sort, desc, start
                    , rows, departments.size());
            return pagedList;
        } else {
            return departments;
        }
    }

    public Long getTotalDepartmentsByOrganizationId(String filterString, String organizationId) {
        return Integer.valueOf(getDepartmentsByOrganizationId(filterString, organizationId, null, null, null, null).size())
                .longValue();
    }

    public Collection<Department> findDepartments(String s, Object[] objects, String s1, Boolean aBoolean, Integer integer, Integer integer1) {
        return null;
    }

    public Long countDepartments(String s, Object[] objects) {
        return null;
    }
}
