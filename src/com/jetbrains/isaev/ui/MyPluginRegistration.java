package com.jetbrains.isaev.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

/**
 * User: Xottab
 * Date: 21.07.2014
 */
public class MyPluginRegistration implements ApplicationComponent {
    // Returns the component name (any unique string value).
    @NotNull
    public String getComponentName() {
        return "MyPlugin";
    }


    // If you register the MyPluginRegistration class in the <application-components> section of
// the plugin.xml file, this method is called on IDEA start-up.
    public void initComponent() {
        ActionManager am = ActionManager.getInstance();
    }

    // Disposes system resources.
    public void disposeComponent() {
    }
}
