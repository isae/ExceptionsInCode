package com.jetbrains.isaev.state;

/**
 * Created by Ilya.Isaev on 07.08.2014.
 */
public enum BTAccountType {

    YOUTRACK(1), JIRA(2);

    BTAccountType(int type) {
        this.type = (byte) type;
    }

    public static BTAccountType valueOf(byte b) {
        for (BTAccountType type : BTAccountType.values()) {
            if (type.type == b) return type;
        }
        return null;
    }

    public byte type;
}
