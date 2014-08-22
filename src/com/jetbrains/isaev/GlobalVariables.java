package com.jetbrains.isaev;

import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorGutterComponentEx;
import com.intellij.openapi.fileEditor.EditorDataProvider;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.isaev.dao.IssuesDAO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Callable;

/**
 * Created by Ilya.Isaev on 01.08.2014.
 */
public class GlobalVariables {
    private static GlobalVariables instance;

    public static Project project;
    public static IssuesDAO dao;

    public GlobalVariables(Project project) {
        GlobalVariables.project = project;
        GlobalVariables.instance = this;
    }

    public GlobalVariables(Project project, IssuesDAO instance) {
        this(project);
        GlobalVariables.dao = instance;
    }

    public static Editor getSelectedEditor() {
        return (Editor) ApplicationManager.getApplication().runReadAction(new Computable<Object>() {
            @Override
            public Object compute() {
                return FileEditorManager.getInstance(project).getSelectedTextEditor();
            }
        });
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
