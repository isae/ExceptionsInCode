package com.jetbrains.isaev;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.jetbrains.isaev.dao.IssuesDAO;
import org.jetbrains.annotations.NotNull;

import javax.jnlp.FileOpenService;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.*;

/**
 * Created by Ilya.Isaev on 01.08.2014.
 */
public class GlobalVariables {
    private static GlobalVariables instance;
    public static final String YOUTRACK_CUSTOM_FIELD_DEFAULT_NAME = "Placement in code";

    @NotNull
    public static Project project;
    @NotNull
    public static IssuesDAO dao;

    public GlobalVariables(@NotNull Project project) {
        GlobalVariables.project = project;
        GlobalVariables.instance = this;
    }

    public GlobalVariables(Project project, @NotNull IssuesDAO instance) {
        this(project);
        GlobalVariables.dao = instance;
    }

    public static Editor getSelectedEditor() {
        RunnableFuture<Editor> future = new FutureTask<Editor>(new Callable<Editor>() {
            @Override
            public Editor call() throws Exception {
                return FileEditorManager.getInstance(project).getSelectedTextEditor();
            }
        });
        SwingUtilities.invokeLater(future);
        Editor result = null;
        try {
            result = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public IssuesDAO getDao() {
        return dao;
    }

    public void setDao(IssuesDAO dao) {
        GlobalVariables.dao = dao;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        GlobalVariables.project = project;
    }

    public static GlobalVariables getInstance() {
        return instance;
    }

}
