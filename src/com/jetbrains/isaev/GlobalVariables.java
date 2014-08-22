package com.jetbrains.isaev;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorGutterComponentEx;
import com.intellij.openapi.fileEditor.EditorDataProvider;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.isaev.dao.IssuesDAO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Ilya.Isaev on 01.08.2014.
 */
public class GlobalVariables {
    private static GlobalVariables instance;

    public static Project project;
    public static IssuesDAO dao;

    public GlobalVariables(Project project) {
        GlobalVariables.project = project;
        FileEditorManager.getInstance(project).registerExtraEditorDataProvider(new EditorDataProvider() {
            @Nullable
            @Override
            public Object getData(@NotNull String s, @NotNull Editor editor, @NotNull VirtualFile virtualFile) {
                EditorGutterComponentEx comp = (EditorGutterComponentEx) editor.getGutter();
                comp.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("GIVE ME MANA SOOOKA");
                    }
                });
                return null;
            }
        }, null);
        GlobalVariables.instance = this;
    }

    public GlobalVariables(Project project, IssuesDAO instance) {
        this(project);
        GlobalVariables.dao = instance;
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
