package com.jetbrains.isaev.integration.youtrack.client;

import java.util.Date;

/**
 * Created by Ilya.Isaev on 18.08.2014.
 */
public enum YouTrackCustomFieldType {
    BUILD_SINGLE("build[1]", false, true, String.class),
    BUILD_MULTI("build[*]", false, false, String.class),
    ENUM_SINGLE("enum[1]", false, true, String.class),
    ENUM_MULTI("enum[*]", false, false, String.class),
    USER_SINGLE("user[1]", false, true, String.class),
    USER_MULTI("user[*]", false, false, String.class),
    GROUP_SINGLE("group[1]", false, true, String.class),
    GROUP_MULTI("group[*]", false, false, String.class),
    OWNED_SINGLE("ownedField[1]", false, true, String.class),
    OWNED_MULTI("ownedField[*]", false, false, String.class),
    STATE("state[1]", false, true, String.class),
    VERSION_SINGLE("version[1]", false, true, String.class),
    VERSION_MULTI("version[*]", false, false, String.class),
    DATE("date", true, true, Date.class),
    INTEGER("integer", true, true, Integer.class),
    STRING("string", true, true, String.class),
    FLOAT("float", true, true, Float.class),
    PERIOD("period", true, true, String.class);

    private String name;

    private boolean isSimple;

    private boolean singleField;

    private Class fieldValuesClass;

    YouTrackCustomFieldType(String name, boolean isSimple, boolean singleField, Class fieldVObject) {
        this.name = name;
        this.isSimple = isSimple;
        this.singleField = singleField;
        this.fieldValuesClass = fieldVObject;
    }

    public static YouTrackCustomFieldType getTypeByName(String name) {
        for (YouTrackCustomFieldType type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public boolean isSimple() {
        return isSimple;
    }

    public boolean singleField() {
        return singleField;
    }

    public Class getFieldValuesClass() {
        return fieldValuesClass;
    }

    public void setFieldValuesClass(Class fieldValuesClass) {
        this.fieldValuesClass = fieldValuesClass;
    }

    public String toString() {
        return name;
    }

}