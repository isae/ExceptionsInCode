package com.jetbrains.isaev.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jetbrains.isaev.state.CommonBTAccount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya.Isaev on 30.07.2014.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class CommonBTProject implements Serializable {

    private String fullName;
    private String shortName;

    @JsonBackReference
    private CommonBTAccount btAccount;

    @JsonManagedReference
    private List<BTIssue> issues = new ArrayList<>();
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

    public List<BTIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<BTIssue> issues) {
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
