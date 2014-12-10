package org.joget.plugin.liferay.util;

import org.joget.directory.model.Role;

/**
 * Created with IntelliJ IDEA.
 * User: ARDI PRIASA
 * Date: 1/31/13
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class Roles   {

    public static  Role _getNormalUser() {
        Role normalUser = new Role();
        normalUser.setId("ROLE_USER");
        normalUser.setName("User");
        normalUser.setDescription("Normal user");
        return normalUser;
    }

    public static Role _getAdminUser() {
        Role normalUser = new Role();
        normalUser.setId("ROLE_ADMIN");
        normalUser.setName("Admin");
        normalUser.setDescription("Administrator");
        return normalUser;
    }

}
