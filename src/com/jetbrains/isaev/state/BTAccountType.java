package com.jetbrains.isaev.state;

/**
 * Created by Ilya.Isaev on 07.08.2014.
 */
public enum BTAccountType {

    YOUTRACK(1, "Jetbrains YouTrack"), JIRA(2, "Atlassian JIRA");

    public String getName() {
        return name;
    }

    public byte getType() {
        return type;
    }

    private final String name;
    private byte type;

    BTAccountType(int type, String s) {
        this.type = (byte) type;
        this.name = s;
    }

    public static BTAccountType valueOf(byte b) {
        for (BTAccountType type : BTAccountType.values()) {
            if (type.type == b) return type;
        }
        return null;
    }

}
