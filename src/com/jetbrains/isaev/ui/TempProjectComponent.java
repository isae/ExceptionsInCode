package com.jetbrains.isaev.ui;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.dao.SerializableIssuesDAO;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Ilya.Isaev on 30.07.2014.
 */
public class TempProjectComponent implements ProjectComponent {
    private static final Logger logger = Logger.getInstance(TempProjectComponent.class);

    public TempProjectComponent(Project project) {
        GlobalVariables.project = project;
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        SerializableIssuesDAO.getInstance().storeData();
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "TempProjectComponent";
    }

    public void projectOpened() {
        //printJavaFilesRecursive();// called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }
}
