package com.jetbrains.isaev.ui;

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

public class ParsedException {
    @NotNull
    private String name;
    @NotNull
    private String optionalMessage;
    private BTIssue issue;
    private int issueID;
    @NotNull
    private Map<Integer, StackTraceElement> stacktrace = new HashMap<Integer, StackTraceElement>();
    private long exceptionID;
    private static Comparator<? super StackTraceElement> stacktraceOrderComparator = new Comparator<StackTraceElement>() {
        @Override
        public int compare(StackTraceElement o1, StackTraceElement o2) {
            byte or1 = o1.getOrder();
            byte or2 = o2.getOrder();
            return or1 == or2 ? 0 : or1 < or2 ? -1 : 1;
        }
    };

    public ParsedException(int issueID, String name, long exceptionID, String optionalMessage) {
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
        if (issue == null) {
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

        StackTraceElement[] elements = new StackTraceElement[stacktrace.values().size()];
        stacktrace.values().toArray(elements);
        Arrays.sort(elements, stacktraceOrderComparator);
        for (int i = 0; i < elements.length; i++) {
            if (i > 0) {
                elements[i].setPrev(elements[i - 1]);
            }
            if (i < elements.length - 1) {
                elements[i].setNext(elements[i + 1]);
            }
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

