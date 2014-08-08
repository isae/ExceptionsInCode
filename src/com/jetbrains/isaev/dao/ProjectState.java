package com.jetbrains.isaev.dao;

import com.jetbrains.isaev.issues.StackTraceElement;
import com.jetbrains.isaev.state.BTAccount;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.state.BTProject;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Ilya.Isaev on 01.08.2014.
 */
public class ProjectState implements Serializable {
    private int exceptionsDepth;
    private Map<String, BTProject> projects;
    private Set<BTAccount> accounts;
    private Map<String, BTIssue> issues;
    private Map<String, List<StackTraceElement>> methodNameToSTElement;
    private Map<String, List<StackTraceElement>> classNameToSTElement;
    private Map<String, List<StackTraceElement>> fileNameToSTElement;

    public Map<String, BTProject> getProjects() {
        if (projects == null) projects = new HashMap<>();
        return projects;
    }

    public void setProjects(Map<String, BTProject> projects) {
        this.projects = projects;
    }

    public Map<String, BTIssue> getIssues() {
        if (issues == null) issues = new HashMap<>();
        return issues;
    }

    public void setIssues(Map<String, BTIssue> issues) {
        this.issues = issues;
    }

    public Set<BTAccount> getAccounts() {
        if (accounts == null) accounts = new HashSet<>();
        return accounts;
    }

    public void setAccounts(Set<BTAccount> accounts) {
        this.accounts = accounts;
    }

    public Map<String, List<StackTraceElement>> getMethodNameToSTElement() {
        if (methodNameToSTElement == null) methodNameToSTElement = new HashMap<>();
        return methodNameToSTElement;
    }

    public void setMethodNameToSTElement(Map<String, List<StackTraceElement>> methodNameToSTElement) {
        this.methodNameToSTElement = methodNameToSTElement;
    }

    public Map<String, List<StackTraceElement>> getClassNameToSTElement() {
        if (classNameToSTElement == null) classNameToSTElement = new HashMap<>();
        return classNameToSTElement;
    }

    public void setClassNameToSTElement(Map<String, List<StackTraceElement>> classNameToSTElement) {
        this.classNameToSTElement = classNameToSTElement;
    }

    public Map<String, List<StackTraceElement>> getFileNameToSTElement() {
        if (fileNameToSTElement == null) fileNameToSTElement = new HashMap<>();
        return fileNameToSTElement;
    }

    public void setFileNameToSTElement(Map<String, List<StackTraceElement>> fileNameToSTElement) {
        this.fileNameToSTElement = fileNameToSTElement;
    }

    public int getExceptionsDepth() {
        return exceptionsDepth;
    }

    public void setExceptionsDepth(int exceptionsDepth) {
        this.exceptionsDepth = exceptionsDepth;
    }
}
