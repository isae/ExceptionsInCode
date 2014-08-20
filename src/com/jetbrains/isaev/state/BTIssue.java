package com.jetbrains.isaev.state;

import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.dao.ZipUtils;
import com.jetbrains.isaev.ui.ParsedException;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

/**
 * User: Xottab
 * Date: 18.07.2014
 */
public class BTIssue {
    private static final int SHOWN_TITLE_LENGTH = 50;
    private String title;
    private String description;
    private String number;
    private Timestamp lastUpdated;
    private BTProject project;
    private int projectID;
    private int issueID;
    private Map<Integer, ParsedException> exceptions = new HashMap<Integer, ParsedException>();


    public BTIssue(int issueID, String title, String descr , Timestamp lastUpdated, String number, int projectID) {
        this.title = title;
        this.lastUpdated = lastUpdated;
        this.number = number;
        this.description = descr;
        this.projectID = projectID;
        this.issueID = issueID;
    }

    public BTIssue() {

    }

    private static String shortenTitle(String title) {
        return title.length() < SHOWN_TITLE_LENGTH ? title : (title.substring(0, SHOWN_TITLE_LENGTH) + "...");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BTIssue issue = (BTIssue) o;

        if (lastUpdated != null ? !lastUpdated.equals(issue.lastUpdated) : issue.lastUpdated != null) return false;
        if (number != null ? !number.equals(issue.number) : issue.number != null) return false;
        if (title != null ? !title.equals(issue.title) : issue.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (lastUpdated != null ? lastUpdated.hashCode() : 0);
        return result;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public int getIssueID() {
        return issueID;
    }

    public void setIssueID(int issueID) {
        this.issueID = issueID;
    }

    @Override
    public String toString() {
        return number + ": " + shortenTitle(title);
    }

    public String getDrawableDescription() {
        return number + ": " + title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public BTProject getProject() {
        if(project==null) project = GlobalVariables.dao.getProject(projectID);
        return project;
    }

    public void setProject(BTProject project) {
        this.project = project;
    }

   /* public byte[] getZippedDescr() {

        return zippedDescr;
    }

    public void setZippedDescr(byte[] zippedDescr) {
        this.zippedDescr = zippedDescr;
    }*/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
     //   if (description == null) description = ZipUtils.decompress(zippedDescr);
        return description;
    }

    public void setDescription(String description) {
    //    this.zippedDescr = ZipUtils.compress(description);
        this.description = description;
    }

    public Map<Integer, ParsedException> getExceptions() {
        if (exceptions == null) exceptions = new HashMap<Integer, ParsedException>();
        return exceptions;
    }

    public void setExceptions(Map<Integer, ParsedException> exceptions) {
        this.exceptions = exceptions;
    }
}
