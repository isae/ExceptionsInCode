package com.jetbrains.isaev.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.issues.StackTraceElement;
import com.jetbrains.isaev.state.BTAccount;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.state.BTProject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

        return state.getIssues();
    }


    @Override
    public List<BTProject> getProjects() {
        return state.getProjects();
    }

    @Override
    public List<BTAccount> getAccounts() {
        return state.getAccounts();
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
        state.setAccounts(accountsFromUI);
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
