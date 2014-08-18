package com.jetbrains.isaev.integration.youtrack.client;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Ilya.Isaev on 18.08.2014.
 */
public class YouTrackCustomFieldPrototype {
    @NotNull
    private String name;
    @NotNull
    private YouTrackCustomFieldType type;
    private boolean isPrivate;
    private boolean defaultVisibility;
    private boolean autoAttached;

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public YouTrackCustomFieldType getType() {
        return type;
    }

    public void setType(@NotNull YouTrackCustomFieldType type) {
        this.type = type;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isDefaultVisibility() {
        return defaultVisibility;
    }

    public void setDefaultVisibility(boolean defaultVisibility) {
        this.defaultVisibility = defaultVisibility;
    }

    public boolean isAutoAttached() {
        return autoAttached;
    }

    public void setAutoAttached(boolean autoAttached) {
        this.autoAttached = autoAttached;
    }

    public YouTrackCustomFieldPrototype(String name, YouTrackCustomFieldType type, boolean isPrivate, boolean defaultVisibility, boolean autoAttached) {

        this.name = name;
        this.type = type;
        this.isPrivate = isPrivate;
        this.defaultVisibility = defaultVisibility;
        this.autoAttached = autoAttached;
    }
}
