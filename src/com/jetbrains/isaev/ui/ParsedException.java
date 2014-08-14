package com.jetbrains.isaev.ui;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.j256.ormlite.table.DatabaseTable;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.issues.StackTraceElement;
import com.jetbrains.isaev.state.BTIssue;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: Xottab
 * Date: 23.07.2014
 */
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")

public class ParsedException implements Serializable {
    @NotNull
    private String name;
    @NotNull
    private String optionalMessage;
    private BTIssue issue;
    private int issueID;
    @NotNull
    private Map<Integer, StackTraceElement> stacktrace = new HashMap<>();
    private long exceptionID;

    public ParsedException(int issueID,String name, long exceptionID, String optionalMessage) {
        this(name, optionalMessage);
        this.exceptionID = exceptionID;
        this.issueID = issueID;
    }

    public ParsedException(@NotNull String name, String optionalMessage) {
        this.name = name;
        this.optionalMessage = optionalMessage == null ? "" : optionalMessage;
    }

    @NotNull
    public BTIssue getIssue() {
        if (issue == null) issue = GlobalVariables.dao.getIssue(issueID);
        if(issue==null){
            boolean f = true;
        }
        return issue;
    }

    public void setIssue(@NotNull BTIssue issue) {
        this.issue = issue;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getOptionalMessage() {
        return optionalMessage;
    }

    public void setOptionalMessage(@NotNull String optionalMessage) {
        this.optionalMessage = optionalMessage;
    }

    public void setExceptionID(long exceptionID) {
        this.exceptionID = exceptionID;
    }

    public long getExceptionID() {
        return exceptionID;
    }

    @NotNull
    public Map<Integer, StackTraceElement> getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(@NotNull Map<Integer, StackTraceElement> stacktrace) {
        this.stacktrace = stacktrace;
    }

    /**
     * must not be used before object persisted to db
     */
    public void orderStacktrace() {
        if (stacktrace.size() > 1) {
            Map<Long, StackTraceElement> map = stacktrace.values().stream().collect(Collectors.toMap(StackTraceElement::getID, el -> el));
            map.values().forEach((el) -> {
                if (el.getNextID() != 0) {
                    StackTraceElement element = map.get(el.getNextID());
                    element.setPrev(el);
                    el.setNext(element);
                }
                if (el.getPrevID() != 0) {
                    StackTraceElement element = map.get(el.getPrevID());
                    element.setNext(el);
                    el.setPrev(element);
                }
            });
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParsedException exception = (ParsedException) o;

        if (issue != null ? !issue.equals(exception.issue) : exception.issue != null) return false;
        if (!name.equals(exception.name)) return false;
        if (!optionalMessage.equals(exception.optionalMessage)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + optionalMessage.hashCode();
        result = 31 * result + (issue != null ? issue.hashCode() : 0);
        return result;
    }
}

