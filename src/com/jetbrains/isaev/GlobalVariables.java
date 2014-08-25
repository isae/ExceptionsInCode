package com.jetbrains.isaev;

import com.intellij.ide.actions.OpenFileAction;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.impl.EditorChangeAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.ex.EditorGutterComponentEx;
import com.intellij.openapi.fileChooser.actions.FileChooserAction;
import com.intellij.openapi.fileEditor.EditorDataProvider;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.impl.http.RemoteFileEditorActionProvider;
import com.intellij.openapi.graph.option.ConstraintManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.sun.jna.platform.mac.MacFileUtils;
import com.sun.jnlp.FileOpenServiceImpl;
import javafx.application.Application;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
