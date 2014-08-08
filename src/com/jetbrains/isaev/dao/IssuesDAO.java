package com.jetbrains.isaev.dao;

import com.jetbrains.isaev.issues.StackTraceElement;
import com.jetbrains.isaev.state.BTAccount;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.state.BTProject;

import java.util.List;
import java.util.Map;

/**
 * Created by Ilya.Isaev on 31.07.2014.
 */
public interface IssuesDAO {

    public abstract List<BTIssue> getIssues();

    public abstract List<BTProject> getProjects();

    public abstract List<BTAccount> getAccounts();

    public abstract Map<String, List<StackTraceElement>> getMethodNameToSTElement();

    public abstract Map<String, List<StackTraceElement>> getClassNameToSTElement();

    public abstract Map<String, List<StackTraceElement>> getFileNameToSTElement();

    public abstract void saveAccounts(List<BTAccount> accountsFromUI);

    public abstract void saveState();

    public abstract void saveIssues(List<BTIssue> issues);
}
