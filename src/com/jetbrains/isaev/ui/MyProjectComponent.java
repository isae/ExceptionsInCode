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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.management.Notification;
import javax.swing.*;

/**
 * Created by Ilya.Isaev on 30.07.2014.
 */
public class MyProjectComponent implements ProjectComponent {
    private static final Logger logger = Logger.getInstance(MyProjectComponent.class);

    public MyProjectComponent(Project project) {
        GlobalVariables variables = new GlobalVariables(project);
        variables.setDao(IssuesDAO.getInstance());
    }

    public void initComponent() {/*
        EditorActionManager.getInstance().setActionHandler(IdeActions.ACTION_EDITOR_ENTER, new EditorActionHandler() {
            private EditorActionHandler handler = EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_ENTER);
            protected void doExecute(Editor editor, @Nullable Caret caret, DataContext dataContext) {
                HintManager hints = HintManager.getInstance();
                hints.showErrorHint(editor, "HEY YOU LOHPIDOR");
                handler.execute(editor, caret, dataContext);
                super.doExecute(editor, caret, dataContext);
            }
        });*/
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
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
