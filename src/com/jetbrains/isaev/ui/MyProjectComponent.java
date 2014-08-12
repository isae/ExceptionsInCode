package com.jetbrains.isaev.ui;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.jetbrains.isaev.dao.PersistentMapIssuesDAO;
import com.jetbrains.isaev.dao.SerializableIssuesDAO;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Ilya.Isaev on 30.07.2014.
 */
public class MyProjectComponent implements ProjectComponent {
    private static final Logger logger = Logger.getInstance(MyProjectComponent.class);

    public MyProjectComponent(Project project) {
        GlobalVariables.project = project;
    }

    public void initComponent() {
        GlobalVariables.dao = IssuesDAO.getInstance(IssuesDAO.StorageType.PERSIST);
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        GlobalVariables.dao.saveState();
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
