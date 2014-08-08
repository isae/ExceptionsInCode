package com.jetbrains.isaev.state;

import com.fasterxml.jackson.annotation.*;
import com.jetbrains.isaev.dao.ZipUtils;
import com.jetbrains.isaev.ui.ParsedException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Xottab
 * Date: 18.07.2014
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@JsonIgnoreProperties({"description"})
public class BTIssue implements Serializable {
    private static final int SHOWN_TITLE_LENGTH = 50;
    private String title;
    private String description;
    private String number;
    private long lastUpdated;
    private byte[] zippedDescr;
    @JsonBackReference(value = "issues")
    private BTProject project;
    @JsonManagedReference(value = "exceptions")
    private List<ParsedException> exceptions = new ArrayList<>();

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

        if (!number.equals(issue.number)) return false;
        if (!project.equals(issue.project)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = number.hashCode();
        result = 31 * result + project.hashCode();
        return result;
    }

    public long getLastUpdated() {

        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return number + ": " + shortenTitle(title);
    }

    @JsonIgnore
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
        return project;
    }

    public void setProject(BTProject project) {
        this.project = project;
    }

    public byte[] getZippedDescr() {

        return zippedDescr;
    }

    public void setZippedDescr(byte[] zippedDescr) {
        this.zippedDescr = zippedDescr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        if (description == null) description = ZipUtils.decompress(zippedDescr);
        return description;
    }

    public void setDescription(String description) {
        this.zippedDescr = ZipUtils.compress(description);
        this.description = description;
    }

    public List<ParsedException> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<ParsedException> exceptions) {
        this.exceptions = exceptions;
    }
}
