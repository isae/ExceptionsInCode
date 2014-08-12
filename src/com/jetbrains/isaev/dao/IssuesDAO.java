package com.jetbrains.isaev.dao;

import com.intellij.openapi.diagnostic.Logger;
import com.jetbrains.isaev.issues.StackTraceElement;
import com.jetbrains.isaev.state.BTAccount;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.state.BTProject;

import java.util.List;
import java.util.Map;

/**
 * Created by Ilya.Isaev on 31.07.2014.
 */
public abstract class IssuesDAO {

    public enum StorageType {
        SERIALIZE, PERSIST, DB
    }

    protected static final Logger logger = Logger.getInstance(IssuesDAO.class);
    private static IssuesDAO instance;

    public static IssuesDAO getInstance(StorageType type) {
        if (instance == null) {
            switch (type) {
                case SERIALIZE: {
                    instance = new SerializableIssuesDAO();
                    break;
                }
                case PERSIST: {
                    instance = new PersistentMapIssuesDAO();
                    break;
                }
            }
        }
        return instance;
    }

    public abstract List<BTIssue> getIssues();

    public abstract List<BTProject> getProjects();

    public abstract List<BTAccount> getAccounts();

    public abstract Map<String, List<StackTraceElement>> getMethodNameToSTElement();

    public abstract Map<String, List<StackTraceElement>> getClassNameToSTElement();

    public abstract Map<String, List<StackTraceElement>> getFileNameToSTElement();

    public abstract void updateAccounts(List<BTAccount> accountsFromUI);

    public abstract void saveState();

    public abstract void saveIssues(List<BTIssue> issues);
}
