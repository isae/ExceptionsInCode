package com.jetbrains.isaev.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.jetbrains.isaev.ui.AddNewReportsSourcesDialog;

/**
 * Created by Ilya.Isaev on 06.08.2014.
 */
public class ShowAllProjectIssuesAction extends AnAction {
    public ShowAllProjectIssuesAction() {
        super("Show all issues in project");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);

      /*  final AddNewReportsSourcesDialog dialog = new AddNewReportsSourcesDialog();
        dialog.show();
        if (!dialog.isOK()) {
            return;
        }*/
    }
}
