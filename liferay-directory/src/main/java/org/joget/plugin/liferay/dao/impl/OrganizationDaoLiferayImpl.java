package org.joget.plugin.liferay.dao.impl;

import org.joget.commons.util.PagedList;
import org.joget.directory.dao.OrganizationDao;
import org.joget.directory.model.Organization;
import org.joget.plugin.liferay.LiferayDirectoryManagerImpl;
import org.joget.plugin.liferay.dao.mapper.OrganizationMapper;
import org.joget.plugin.liferay.util.QueryList;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ARDI PRIASA
 * Date: 1/31/13
 * Time: 11:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class OrganizationDaoLiferayImpl implements OrganizationDao {

    private Map properties;

    public OrganizationDaoLiferayImpl(Map properties) {
        this.properties = properties;
    }

    public Map getProperties() {
        return properties;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }

    public Boolean addOrganization(Organization organization) {
        return null;
    }

    public Boolean updateOrganization(Organization organization) {
        return null;
    }

    public Boolean deleteOrganization(String s) {
        return null;
    }

    public Organization getOrganization(String id) {
        Organization organization = (Organization) LiferayDirectoryManagerImpl.getJdbcTemplate().queryForObject(QueryList.SELECT_COMPANY_BY_ID, new Object[]{id}
                , new OrganizationMapper());
        return organization;
    }

    public Organization getOrganizationByName(String name) {
        Organization organization = (Organization) LiferayDirectoryManagerImpl.getJdbcTemplate().queryForObject(QueryList.SELECT_COMPANY_BY_NAME, new Object[]{name}
                , new OrganizationMapper());
        return organization;
    }

    public Collection<Organization> getOrganizationsByFilter(String filterString, String sort, Boolean desc, Integer start, Integer rows) {
        List<Organization> organizations = new ArrayList<Organization>();
        if (filterString == null) {
            filterString = "";
        }

        List<Map<String, Object>> maps = LiferayDirectoryManagerImpl.getJdbcTemplate().queryForList(QueryList.SELECT_COMPANY_BY_FILTER
                , new Object[]{"%" + filterString + "%"}
                , new int[]{Types.VARCHAR});

        for (Map<String, Object> map : maps) {
            Organization organization = new Organization();
            organization.setId(map.get("companyId").toString());
            organization.setName(map.get("name").toString());
            organizations.add(organization);
        }

        if (organizations != null && (sort != null || desc != null || start != null || rows != null)) {
            PagedList<Organization> pagedList = new PagedList<Organization>(true, organizations, sort, desc, start
                    , rows, organizations.size());
            return pagedList;
        } else {
            return organizations;
        }
    }

    public Long getTotalOrganizationsByFilter(String filter) {
        return Integer.valueOf(getOrganizationsByFilter(filter, null, null, null, null).size()).longValue();
    }

    public Collection<Organization> findOrganizations(String s, Object[] objects, String s1, Boolean aBoolean, Integer integer, Integer integer1) {
        return null;
    }

    public Long countOrganizations(String s, Object[] objects) {
        return null;
    }
}
