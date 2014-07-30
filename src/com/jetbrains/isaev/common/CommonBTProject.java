package com.jetbrains.isaev.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya.Isaev on 30.07.2014.
 */
public class CommonBTProject implements Serializable {

    private String fullName;
    private String shortName;

    private List<CommonIssue> issues = new ArrayList<>();
    private long lastUpdated;
    private boolean mustBeUpdated = false;

    public CommonBTProject(String projectFullName, String projectShortName) {
        this.fullName = projectFullName;
        this.shortName = projectShortName;
    }

    public CommonBTProject() {
    }

    public CommonBTProject(String name) {
        this.fullName = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<CommonIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<CommonIssue> issues) {
        this.issues = issues;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isMustBeUpdated() {
        return mustBeUpdated;
    }

    public void setMustBeUpdated(boolean mustBeUpdated) {
        this.mustBeUpdated = mustBeUpdated;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
