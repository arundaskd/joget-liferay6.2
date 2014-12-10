package org.joget.plugin.liferay.util;

/**
 * Created with IntelliJ IDEA.
 * User: ARDI PRIASA
 * Date: 1/30/13
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class QueryList {
    public static String COUNT_USER = "SELECT COUNT(*) FROM User_";
    public static String SELECT_USER_BY_USERNAME = "SELECT * FROM User_ WHERE emailAddress =?";
    public static String SELECT_USER_BY_USERID = "SELECT * FROM User_ WHERE userId =?";

    public static String SELECT_USER_BY_USERNAME_PWD =
            "SELECT COUNT(*) FROM User_ WHERE password_=? AND emailAddress=? and status <> ?";
    public static String SELECT_PWD = "SELECT password_  FROM User_ WHERE emailAddress=?";
    public static String SELECT_ORG_BY_ID = "SELECT *  FROM Organization_  WHERE organizationId =?";
    public static String SELECT_ORG_BY_NAME = "SELECT *  FROM Organization_  WHERE name =?";

    public static String SELECT_ORG_BY_FILTER =
            "SELECT *  FROM Organization_  WHERE name like ? or comments like ?";

    public static String SELECT_DEPT_BY_FILTER_ORG =
            "SELECT *  FROM Organization_  WHERE (name like ? or comments like ?) AND companyId = ?";

    public static String SELECT_DEPT_BY_FILTER_PARENT =
            "SELECT *  FROM Organization_  WHERE (name like ? or comments like ?) AND parentOrganizationId = ?";

    public static String SELECT_DEPT_BY_ID = "SELECT *  FROM Organization_  WHERE organizationId =?";

    public static String SELECT_PARENT_DEPT = "SELECT o.* FROM Organization_ o " +
            "WHERE o.organizationId  " +
            "IN (SELECT parentOrganizationId FROM Organization_ WHERE organizationId = ?)";

    public static String SELECT_DEPT_BY_NAME = "SELECT *  FROM Organization_  WHERE name =?";

    public static String SELECT_USER_BY_FILTER =
            "SELECT * FROM User_ WHERE emailAddress LIKE ? OR firstName LIKE ? OR lastName LIKE ?";

    public static String SELECT_USER_BY_ORG = "SELECT u.* FROM User_ u WHERE " +
            "(u.emailAddress LIKE ? OR u.firstName LIKE ? OR u.lastName LIKE ?) " +
            "AND " +
            "u.userId IN (SELECT uo.userId FROM Users_Orgs uo WHERE uo.organizationId=?)";

    public static String SELECT_USER_BY_COMPANY = "SELECT u.* FROM User_ u " +
            "WHERE (u.emailAddress LIKE ? OR u.firstName LIKE ? OR u.lastName LIKE ?) AND u.companyId = ? ";

    public static String SELECT_USER_BY_GRP = "SELECT u.* FROM User_ u WHERE " +
            "(u.emailAddress LIKE ? OR u.firstName LIKE ? OR u.lastName LIKE ?) " +
            "AND " +
            "u.userId IN (SELECT uo.userId FROM Users_UserGroups uo WHERE uo.userGroupId=?)";

    public static String SELECT_ORG_BY_USER =
            "SELECT o.* FROM Organization_ o WHERE o.organizationId IN " +
                    "(SELECT organizationId FROM Users_Orgs WHERE userId = ?)";

    public static String SELECT_COMPANY_BY_USER =
            "SELECT c.companyId,a.name FROM Company c,Account_ a,User_ u " +
                    "WHERE c.companyId = a.companyId " +
                    "AND c.companyId = u.companyId " +
                    "AND u.userId = ?";

    public static String SELECT_GRP_BY_USER =
            "SELECT o.* FROM UserGroup o WHERE (o.name LIKE ? OR o.description LIKE ?) " +
                    "AND o.userGroupId IN (SELECT userGroupId FROM Users_UserGroups WHERE userId = ?)";

    public static String SELECT_GRP_BY_USER_COMPANY =
            "SELECT o.* FROM UserGroup o WHERE (o.name LIKE ? OR o.description LIKE ?) " +
                    "AND o.userGroupId IN (SELECT userGroupId FROM Users_UserGroups WHERE userId = ?) " +
                    "AND o.companyId = ?";

    public static String SELECT_GRP_BY_FILTER = "SELECT o.* FROM UserGroup o WHERE o.name LIKE ? OR o.description LIKE ?";

    public static String SELECT_GRP_BY_ID = "SELECT o.* FROM UserGroup o WHERE o.userGroupId = ?";

    public static String SELECT_GRP_BY_NAME = "SELECT o.* FROM UserGroup o WHERE o.name = ?";

    public static String SELECT_EMP_BY_FILTER = "SELECT u.*, c.contactId, c.employeeNumber FROM User_ u, Contact_ c " +
            "WHERE u.contactId = c.contactId AND (u.emailAddress LIKE ? OR u.firstName LIKE ? OR u.lastName LIKE ?)";

//    public static String SELECT_EMP_BY_ORG = "SELECT u.*, c.contactId, c.employeeNumber FROM User_ u, Contact_ c " +
//            "WHERE u.contactId = c.contactId AND (u.emailAddress LIKE ? OR u.firstName LIKE ? OR u.lastName LIKE ?) AND " +
//            "u.userId IN (SELECT uo.userId FROM Users_Orgs uo WHERE uo.organizationId = ?)";

    public static String SELECT_EMP_BY_DEPT = "SELECT u.*, c.contactId, c.employeeNumber FROM User_ u, Contact_ c " +
            "WHERE u.contactId = c.contactId AND (u.emailAddress LIKE ? OR u.firstName LIKE ? OR u.lastName LIKE ?) AND " +
            "u.userId IN (SELECT uo.userId FROM Users_Orgs uo WHERE uo.organizationId = ?)";

    public static String SELECT_EMP_BY_ORG_DEPT = "SELECT u.*, c.contactId, c.employeeNumber FROM User_ u, Contact_ c " +
            "WHERE u.contactId = c.contactId AND (u.emailAddress LIKE ? OR u.firstName LIKE ? OR u.lastName LIKE ?) AND " +
            "u.userId IN (SELECT uo.userId FROM Users_Orgs uo WHERE uo.organizationId = ?) AND " +
            "u.companyId = ?";

    public static String SELECT_EMP_BY_ORG = "SELECT u.*, c.contactId, c.employeeNumber FROM User_ u, Contact_ c " +
            "WHERE u.contactId = c.contactId AND (u.emailAddress LIKE ? OR u.firstName LIKE ? OR u.lastName LIKE ?) AND " +
            "u.companyId = ?";

    public static String SELECT_EMP = "SELECT u.*, c.contactId, c.employeeNumber FROM User_ u, Contact_ c " +
            "WHERE u.contactId = c.contactId AND u.contactId = ?";

    public static String SELECT_EMP_BY_USERID = "SELECT u.*, c.contactId, c.employeeNumber FROM User_ u, Contact_ c " +
            "WHERE u.contactId = c.contactId AND u.userId = ?";

    public static String SELECT_COMPANY_BY_ID = "SELECT c.companyId,a.name FROM Company c,Account_ a WHERE " +
            "c.companyId = a.companyId AND c.companyId = ?";

    public static String SELECT_COMPANY_BY_NAME = "SELECT c.companyId,a.name FROM Company c,Account_ a WHERE " +
            "c.companyId = a.companyId AND a.name = ?";

    public static String SELECT_COMPANY_BY_FILTER = "SELECT c.companyId,a.name FROM Company c,Account_ a WHERE " +
            "c.companyId = a.companyId AND a.name LIKE ?";

    public static String SELECT_ROLEID_BY_NAME = "SELECT r.roleId FROM Role_ r WHERE r.name = ?";

    public static String SELECT_HOD_ID = "SELECT c.contactId FROM User_ u,UserGroupRole ug,Contact_ c " +
            "WHERE " +
            "u.contactId = c.contactId AND " +
            "u.userId = ug.userId AND " +
            "u.userId IN (SELECT userId FROM Users_Orgs WHERE organizationId = ?) AND " +
            "ug.userId IN  (SELECT userId FROM UserGroupRole WHERE roleId = ?)";

    public static String SELECT_DEPTID_BY_USERID = "SELECT organizationId FROM Users_Orgs WHERE userId = ?";
    public static String SELECT_DEPT_BY_FILTER = "SELECT *  FROM Organization_  WHERE name like ? or comments like ?";
    public static String SELECT_ROLE_BY_ID = "SELECT r.* FROM Role_ r WHERE r.roleId " +
            "IN (SELECT roleId FROM Users_Roles WHERE userId=?)";
}
