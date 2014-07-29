package com.jetbrains.isaev.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.jetbrains.isaev.actions.AddNewBugtrackersAction;

/**
 * User: Xottab
 * Date: 25.07.2014
 */
public class ExceptionsInCodePopupSubmenu extends DefaultActionGroup {
    @Override
    public void update(AnActionEvent e) {
        if (getChildrenCount() == 0) {
            add(new AddNewBugtrackersAction());
        }
    }
}
