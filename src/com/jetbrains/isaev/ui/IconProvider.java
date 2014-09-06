package com.jetbrains.isaev.ui;

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ui.AnimatedIcon;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * User: Xottab
 * Date: 28.07.2014
 */
public class IconProvider {
    @NonNls
    private static final String PACKAGE_ROOT = "/com/jetbrains/isaev/resources/icons";

    /**
     * Retrieve an icon from its reference
     *
     * @param iconRef the icon reference
     * @return the loaded icon
     */
    public static Icon getIcon(@NotNull IconRef iconRef) {
        return IconLoader.getIcon(getIconUrl(iconRef));
    }

    public static String getIconUrl(IconRef iconRef) {
        String tmp = PACKAGE_ROOT + "/" + iconRef.imgName;
        if (iconRef != IconRef.LOADING) tmp += ".png";
        return tmp;
    }

    /**
     * Enumeration for all defined images. Forces callers to use one of these value when retrieving
     * icons
     */
    public static enum IconRef {
        YOUTRACK("youtrack"),
        JIRA("jira_icon"),
        WARN("warn"),
        WARN_MULTIPLE("warn_mult"),
        JIRA_SMALL("jira_icon_small"),
        YOUTRACK_SMALL("youtrack_small"),
        LOADING("loading.gif");

        private final String imgName;

        IconRef(String imgName) {
            this.imgName = imgName;
        }
    }

}
