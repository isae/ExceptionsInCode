package com.jetbrains.integration.youtrack.client;

public class IssueLink {

    public static final String DEPEND_LINK_TYPE = "Depend";

    public static final String SUBTASK_LINK_TYPE = "Subtask";

    public static final String DUPLICATE_LINK_TYPE = "Duplicate";

    public static final String RELATES_LINK_TYPE = "Relates";

    private String value;

    private String type;

    private String role;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
