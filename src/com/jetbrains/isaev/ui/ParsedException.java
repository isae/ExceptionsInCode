package com.jetbrains.isaev.ui;

import com.jetbrains.isaev.issues.StackTraceElementWrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Xottab
 * Date: 23.07.2014
 */
public class ParsedException implements Serializable {
    private String name;
    private String optionalMessage;
    private List<StackTraceElementWrapper> stacktrace = new ArrayList<>();

    public ParsedException(String name, List<StackTraceElementWrapper> stacktrace) {
        this.name = name;
        this.stacktrace = stacktrace;
    }

    public ParsedException() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StackTraceElementWrapper> getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(List<StackTraceElementWrapper> stacktrace) {
        this.stacktrace = stacktrace;
    }

    public String getOptionalMessage() {
        return optionalMessage;
    }

    public void setOptionalMessage(String optionalMessage) {
        this.optionalMessage = optionalMessage;
    }
}

