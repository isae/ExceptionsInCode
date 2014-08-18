package com.jetbrains.isaev.ui;

import com.intellij.openapi.util.IconLoader;
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
        return IconLoader.getIcon(PACKAGE_ROOT + "/" + iconRef.imgName + ".png");
    }

    /**
     * Enumeration for all defined images. Forces callers to use one of these value when retrieving
     * icons
     */
    public static enum IconRef {
        YOUTRACK("youtrack"),
        JIRA("jira_icon"),
        WARN("warn"),
        JIRA_SMALL("jira_icon_small"), YOUTRACK_SMALL("youtrack_small");

        private final String imgName;

        IconRef(String imgName) {
            this.imgName = imgName;
        }
    }

}
