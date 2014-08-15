package com.jetbrains.isaev.dao;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.jetbrains.isaev.GlobalVariables;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Ilya.Isaev on 15.08.2014.
 */
public class ProjectData {
    private static final String DB_VERSION = "exceptions_db_version";

    public static int getDbVersion() {
        return Integer.parseInt(PropertiesComponent.getInstance(GlobalVariables.project).getValue(DB_VERSION, "0"));
    }

    public static void setDbVersion(int version) {
        PropertiesComponent.getInstance(GlobalVariables.project).setValue(DB_VERSION, String.valueOf(version));
    }


}
