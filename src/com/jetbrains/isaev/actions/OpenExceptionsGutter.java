package com.jetbrains.isaev.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.editor.Editor;
import com.jetbrains.isaev.notifications.IssuesExceptionsGutter;

/**
 * User: Xottab
 * Date: 21.07.2014
 */
public class OpenExceptionsGutter extends ToggleAction {
    private boolean state;
   /* public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        IssuesExceptionsGutter provider = new IssuesExceptionsGutter();
        provider.putExceptionToRow(2, "Всё плохо");
        provider.putExceptionToRow(4, "На строке 4 полная задница");
        provider.putExceptionToRow(7, "Смерть всем неверным");
        provider.putExceptionToRow(23, "Ололо");
        assert editor != null;
        editor.getGutter().registerTextAnnotation(provider, new ExceptionClickedAction());
    }*/

    @Override
    public boolean isSelected(AnActionEvent e) {
        return state;
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        state = true;
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        IssuesExceptionsGutter provider = new IssuesExceptionsGutter();
        provider.putExceptionToRow(2, 3);
        provider.putExceptionToRow(3, 21);
        provider.putExceptionToRow(5, 14);
        provider.putExceptionToRow(6, 90);
        provider.putExceptionToRow(8, 17);
        provider.putExceptionToRow(10, 5);
        assert editor != null;
        editor.getGutter().registerTextAnnotation(provider, new ExceptionClickedAction());

    }
}
