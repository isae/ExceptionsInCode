package com.jetbrains.actions;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorGutterAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

import java.awt.*;

/**
 * User: Xottab
 * Date: 21.07.2014
 */
public class ExceptionClickedAction extends AnAction implements EditorGutterAction {
    private static final Logger logger = Logger.getInstance(ExceptionClickedAction.class);
    private static Editor lastEditor;
    int currentLine = -1;

    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        assert editor != null;
        HintManager.getInstance().showInformationHint(editor, "You clicked on row " + currentLine);
    }

    @Override
    public void doAction(int lineNum) {
        ProjectManager manager = ProjectManager.getInstance();
        Project[] prs = manager.getOpenProjects();
        logger.warn("test warn " + prs.length);
        Editor editor = null;
        for (Project pr : prs) {
            Editor ed = FileEditorManager.getInstance(pr).getSelectedTextEditor();
            if (ed != null) editor = ed;
            logger.warn("Info: " + pr.isInitialized() + " " + pr.isDefault() + " " + pr.isOpen() + " " + pr.isDisposed() + " " + pr.getName());
        }

        logger.warn("end test");
        logger.warn("Finded editor? " + (editor != null));
        assert editor != null;
        HintManager.getInstance().showErrorHint(editor, "LOH!!!!");

       /* Editor editor = FileEditorManager.getInstance(manager.getOpenProjects()[0]).
        this.actionPerformed(AnActionEvent.createFromInputEvent(this, new MouseEvent()));`
        ProjectManager manager1 = ProjectManager.getInstance();
        assert manager1 != null;
        Project project = manager1.getDefaultProject();
        FileEditorManager manager = FileEditorManager.getInstance(project);
        assert manager != null;
        final Editor editor = manager.getSelectedTextEditor();
        currentLine = lineNum;
        assert editor != null;
        HintManager.getInstance().showInformationHint(editor, "You clicked on row from doAction " + currentLine); */
    }

    @Override
    public Cursor getCursor(int lineNum) {
        return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    }
}
