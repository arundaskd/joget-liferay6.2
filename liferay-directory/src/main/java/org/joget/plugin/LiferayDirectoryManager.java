package org.joget.plugin;

import org.joget.apps.app.service.AppUtil;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.directory.model.service.DirectoryManagerPlugin;
import org.joget.plugin.base.ExtDefaultPlugin;
import org.joget.plugin.liferay.LiferayDirectoryManagerImpl;
import org.joget.plugin.property.model.PropertyEditable;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ARDI PRIASA
 * Date: 1/30/13
 * Time: 8:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class LiferayDirectoryManager extends ExtDefaultPlugin implements DirectoryManagerPlugin, PropertyEditable {

    public DirectoryManager getDirectoryManagerImpl(Map properties) {
        return new LiferayDirectoryManagerImpl(properties);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getName() {
        return "Liferay Directory Manager";
    }

    public String getVersion() {
        return "2.0-SNAPSHOT";
    }

    public String getDescription() {
        return "Liferay Directory Manager";
    }

    public String getLabel() {
        return "Liferay Directory Manager";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        String json = AppUtil.readPluginResource(getClass().getName(), "/properties/liferayDirectoryManager.json",
                null, true, "message/liferayDirectoryManager");
        return json;
    }
}
