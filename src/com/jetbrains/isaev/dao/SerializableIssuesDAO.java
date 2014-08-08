package com.jetbrains.isaev.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.issues.StackTraceElement;
import com.jetbrains.isaev.state.BTAccount;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.state.BTProject;
import com.jetbrains.isaev.ui.ParsedException;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Ilya.Isaev on 31.07.2014.
 */
public class SerializableIssuesDAO implements IssuesDAO {
    private static final Logger logger = Logger.getInstance(SerializableIssuesDAO.class);
    private static final String DATABASE_PATH = GlobalVariables.project.getBaseDir().getPath() + "/exceptions_database.json";
    private static final File databaseFile = new File(DATABASE_PATH);
    private static SerializableIssuesDAO instance;
    private ObjectMapper mapper = new ObjectMapper();
    private ProjectState state = new ProjectState();


    private SerializableIssuesDAO() {
        try {
            databaseFile.createNewFile();
            state = mapper.readValue(databaseFile, state.getClass());
        } catch (IOException e) {
            logger.warn(e);
            logger.warn(DATABASE_PATH);
        }
    }

    public static IssuesDAO getInstance() {
        if (instance == null) {
            instance = new SerializableIssuesDAO();
        }
        return instance;
    }

    public ProjectState getState() {
        return state;
    }

    public void setState(ProjectState state) {
        this.state = state;
    }

    @Override
    public List<BTIssue> getIssues() {

        return new ArrayList<>(state.getIssues().values());
    }


    @Override
    public List<BTProject> getProjects() {
        return new ArrayList<>(state.getProjects().values());
    }

    @Override
    public List<BTAccount> getAccounts() {
        return new ArrayList<>(state.getAccounts());
    }

    @Override
    public Map<String, List<StackTraceElement>> getMethodNameToSTElement() {
        return state.getMethodNameToSTElement();
    }


    @Override
    public Map<String, List<StackTraceElement>> getClassNameToSTElement() {
        return state.getClassNameToSTElement();
    }


    @Override
    public Map<String, List<StackTraceElement>> getFileNameToSTElement() {
        return state.getFileNameToSTElement();
    }

    @Override
    public void saveAccounts(List<BTAccount> accountsFromUI) {
        accountsFromUI.forEach(state.getAccounts()::add);
    }

    public void saveIssues(List<BTIssue> issues) {
        Map<String, BTIssue> newIssues = new HashMap<>();
        for (BTIssue issue : issues) {
            BTIssue old = state.getIssues().get(issue.getNumber());
            if (old == null || old.getLastUpdated() < issue.getLastUpdated()) {
                newIssues.put(issue.getNumber(), issue);
            }
        }
        newIssues.values().forEach((issue) -> {
            BTIssue old = state.getIssues().get(issue.getNumber());
            Map<String, List<StackTraceElement>> cmap = getClassNameToSTElement();
            Map<String, List<StackTraceElement>> mmap = getMethodNameToSTElement();
            Map<String, List<StackTraceElement>> fmap = getFileNameToSTElement();
            if (old != null) {
                // lets clear some
                old.getExceptions().forEach((ex) -> {
                    ex.getStacktrace().forEach((el) -> {
                        for (Iterator<StackTraceElement> iter = cmap.get(el.getDeclaringClass()).iterator(); iter.hasNext(); ) {
                            if (iter.next().getException().equals(ex)) {
                                iter.remove();
                            }
                        }
                        for (Iterator<StackTraceElement> iter = mmap.get(el.getFullMethodName()).iterator(); iter.hasNext(); ) {
                            if (iter.next().getException().equals(ex)) {
                                iter.remove();
                            }
                        }
                        for (Iterator<StackTraceElement> iter = fmap.get(el.getFileName()).iterator(); iter.hasNext(); ) {
                            if (iter.next().getException().equals(ex)) {
                                iter.remove();
                            }
                        }
                    });
                });
            }
            for (ParsedException exception : issue.getExceptions()) {
                List<StackTraceElement> sTrace = exception.getStacktrace();
                for (int i = 0; i < sTrace.size(); i++) {
                    StackTraceElement element = sTrace.get(i);
                    if (i > 0) element.setPrev(sTrace.get(i - 1));
                    if (i < sTrace.size() - 1) element.setNext(sTrace.get(i + 1));
                    if (!cmap.containsKey(element.getDeclaringClass()))
                        cmap.put(element.getDeclaringClass(), new ArrayList<>());
                    String tmp = element.getFullMethodName();
                    if (!mmap.containsKey(tmp))
                        mmap.put(tmp, new ArrayList<StackTraceElement>());
                    if (!fmap.containsKey(element.getFileName()))
                        fmap.put(element.getFileName(), new ArrayList<StackTraceElement>());
                    cmap.get(element.getDeclaringClass()).add(element);
                    mmap.get(tmp).add(element);
                    fmap.get(element.getFileName()).add(element);
                }
            }
        });
        state.getIssues().putAll(newIssues);
    }

    @Override
    public void saveState() {
        try {
            mapper.writeValue(databaseFile, state);
        } catch (IOException e) {
            logger.warn(e);
        }
    }
}
