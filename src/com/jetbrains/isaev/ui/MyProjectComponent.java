package com.jetbrains.isaev.ui;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.notification.Notifications;
import com.intellij.notification.impl.ui.NotificationsUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.jetbrains.isaev.notifications.MyLineMarkerProvider;
import com.jetbrains.isaev.notifications.ReportedExceptionLineMarkerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.management.Notification;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ilya.Isaev on 30.07.2014.
 */
public class MyProjectComponent implements ProjectComponent {
    private static final Logger logger = Logger.getInstance(MyProjectComponent.class);

    public MyProjectComponent(Project project) {
        GlobalVariables variables = new GlobalVariables(project);
        variables.setDao(IssuesDAO.getInstance());
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        for (HashMap<Integer, ArrayList<ReportedExceptionLineMarkerInfo>> m : MyLineMarkerProvider.markerState.values())
            for (ArrayList<ReportedExceptionLineMarkerInfo> l : m.values())
                for (ReportedExceptionLineMarkerInfo info : l)
                    info.updateSTElementsPlacementInfo();
        IssuesDAO.getInstance().saveState();
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
