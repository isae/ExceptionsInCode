package com.jetbrains.isaev.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;

/**
 * Created by Ilya.Isaev on 06.08.2014.
 */
public class ShowAllIssuesToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        toolWindow.getComponent().add(new AllIssuesToolWindowList());
    }
}
