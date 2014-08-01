package com.jetbrains.isaev.ui;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jetbrains.isaev.common.BTIssue;
import com.jetbrains.isaev.issues.StackTraceElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Xottab
 * Date: 23.07.2014
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class ParsedException implements Serializable {
    private String name;
    private String optionalMessage;
    @JsonBackReference
    private BTIssue issue;
    @JsonManagedReference
    private List<StackTraceElement> stacktrace = new ArrayList<>();

    public ParsedException(String name, List<StackTraceElement> stacktrace) {
        this.name = name;
        this.stacktrace = stacktrace;
    }

    public ParsedException() {
    }

    public BTIssue getIssue() {
        return issue;
    }

    public void setIssue(BTIssue issue) {
        this.issue = issue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StackTraceElement> getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(List<com.jetbrains.isaev.issues.StackTraceElement> stacktrace) {
        this.stacktrace = stacktrace;
    }

    public String getOptionalMessage() {
        return optionalMessage;
    }

    public void setOptionalMessage(String optionalMessage) {
        this.optionalMessage = optionalMessage;
    }
}

