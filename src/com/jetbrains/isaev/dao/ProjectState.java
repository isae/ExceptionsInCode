package com.jetbrains.isaev.dao;

import com.jetbrains.isaev.issues.StackTraceElement;
import com.jetbrains.isaev.state.BTAccount;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.state.BTProject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ilya.Isaev on 01.08.2014.
 */
public class ProjectState implements Serializable {
    private List<BTProject> projects;
    private List<BTAccount> accounts;
    private List<BTIssue> issues;
    private Map<String, List<StackTraceElement>> methodNameToSTElement;
    private Map<String, List<StackTraceElement>> classNameToSTElement;
    private Map<String, List<StackTraceElement>> fileNameToSTElement;


    public List<BTProject> getProjects() {
        if (projects == null) projects = new ArrayList<>();
        return projects;
    }

    public void setProjects(List<BTProject> projects) {
        this.projects = projects;
    }

    public List<BTIssue> getIssues() {
        if (issues == null) issues = new ArrayList<>();
        return issues;
    }

    public void setIssues(List<BTIssue> issues) {
        this.issues = issues;
    }

    public List<BTAccount> getAccounts() {
        if (accounts == null) accounts = new ArrayList<>();
        return accounts;
    }

    public void setAccounts(List<BTAccount> accounts) {
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
}
