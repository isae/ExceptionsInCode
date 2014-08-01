package com.jetbrains.isaev.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.jetbrains.isaev.ui.AddNewReportsSourcesDialog;

/**
 * User: Xottab
 * Date: 25.07.2014
 */
public class AddNewBugtrackersAction extends AnAction {
    public AddNewBugtrackersAction() {
        super("Add sources of issues");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);

        final AddNewReportsSourcesDialog dialog = new AddNewReportsSourcesDialog();
        dialog.show();
        if (!dialog.isOK()) {
            return;
        }
    }
}
