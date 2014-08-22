package com.jetbrains.isaev.state;

import com.jetbrains.isaev.GlobalVariables;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya.Isaev on 30.07.2014.
 */
public class BTProject {

    @NotNull
    private String fullName;
    @NotNull
    private String shortName;

    private BTAccount btAccount;

    private List<BTIssue> issues = new ArrayList<BTIssue>();
    private Timestamp lastUpdated;
    private boolean mustBeUpdated = false;
    private int projectID;

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    private int accountID;

    public BTProject(@NotNull String projectFullName, @NotNull String projectShortName) {
        this.fullName = projectFullName;
        this.shortName = projectShortName;
        this.mustBeUpdated = false;
    }

    public BTProject(int projectID, int accountID, @NotNull String shortName, @NotNull String longName, @NotNull Timestamp lastUpdated, boolean mustBeUpdated) {
        this.projectID = projectID;
        this.accountID = accountID;
        this.fullName = longName;
        this.shortName = shortName;
        this.lastUpdated = lastUpdated;
        this.mustBeUpdated = mustBeUpdated;
    }

    public BTProject(@NotNull BTAccount account, @NotNull String projectFullName, @NotNull String projectShortName) {
        this(projectFullName, projectShortName);
        this.btAccount = account;
        this.mustBeUpdated = false;
        this.accountID = account.getAccountID();
    }

    @NotNull
    public String getShortName() {
        return shortName;
    }

    public void setShortName(@NotNull String shortName) {
        this.shortName = shortName;
    }

    public List<BTIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<BTIssue> issues) {
        this.issues = issues;
    }

    public Timestamp getLastUpdated() {
        if (lastUpdated == null) lastUpdated = new Timestamp(0);
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isMustBeUpdated() {
        return mustBeUpdated;
    }

    public void setMustBeUpdated(boolean mustBeUpdated) {
        this.mustBeUpdated = mustBeUpdated;
    }

    @NotNull
    public String getFullName() {
        return fullName;
    }

    public void setFullName(@NotNull String fullName) {
        this.fullName = fullName;
    }

    public BTAccount getBtAccount() {
        if (btAccount == null) btAccount = GlobalVariables.getInstance().dao.getAccount(accountID);
        return btAccount;
    }

    public void setBtAccount(BTAccount btAccount) {
        this.btAccount = btAccount;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public int getProjectID() {
        return projectID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BTProject project = (BTProject) o;

        if (btAccount != null ? !btAccount.equals(project.btAccount) : project.btAccount != null) return false;
        if (!fullName.equals(project.fullName)) return false;
        if (!shortName.equals(project.shortName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fullName.hashCode();
        result = 31 * result + (shortName.hashCode());
        result = 31 * result + (btAccount != null ? btAccount.hashCode() : 0);
        return result;
    }
}
