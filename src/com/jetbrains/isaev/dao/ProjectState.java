package com.jetbrains.isaev.dao;

import com.jetbrains.isaev.common.CommonBTProject;
import com.jetbrains.isaev.issues.StackTraceElement;
import com.jetbrains.isaev.state.CommonBTAccount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ilya.Isaev on 01.08.2014.
 */
public class ProjectState implements Serializable {
    private List<CommonBTProject> projects;
    private List<CommonBTAccount> accounts;
    private Map<String, List<StackTraceElement>> methodNameToSTElement;
    private Map<String, List<StackTraceElement>> classNameToSTElement;
    private Map<String, List<StackTraceElement>> fileNameToSTElement;


    public List<CommonBTProject> getProjects() {
        if (projects == null) projects = new ArrayList<>();
        return projects;
    }

    public void setProjects(List<CommonBTProject> projects) {
        this.projects = projects;
    }

    public List<CommonBTAccount> getAccounts() {
        if (accounts == null) accounts = new ArrayList<>();
        return accounts;
    }

    public void setAccounts(List<CommonBTAccount> accounts) {
        this.accounts = accounts;
    }

    public Map<String, List<StackTraceElement>> getMethodNameToSTElement() {
        if (methodNameToSTElement == null) return new HashMap<>();
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
