package com.jetbrains.tmp;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import org.jetbrains.annotations.Nullable;

/**
 * User: Xottab
 * Date: 18.07.2014
 */
public class DumbAction extends TextComponentEditorAction {
    public DumbAction() {
        super(new Handler());
    }


    private static class Handler extends EditorWriteActionHandler {
        public void executeWriteAction(Editor editor,
                                       @Nullable Caret caret,
                                       DataContext dataContext) {
            com.intellij.openapi.editor.Document document = editor.getDocument();
            document.setText("ASDRGDOFGKDFSHOKSDGOADFKHOFHKFD\nOSHKDFGHJOKDFJOFDGHKJFJFGJFODKJ");
        }
    }
}
