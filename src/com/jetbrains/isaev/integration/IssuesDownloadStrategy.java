package com.jetbrains.isaev.integration;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.state.BTProject;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Ilya.Isaev on 07.08.2014.
 */
public abstract class IssuesDownloadStrategy extends Task.Backgroundable {
    @NotNull
    protected final BTProject[] btProjects;



    public IssuesDownloadStrategy(@NotNull BTProject[] projects) {
        super(GlobalVariables.getInstance().project, "Updating issues list");
        this.btProjects = projects;
    }

    @Override
    public abstract void run(@NotNull ProgressIndicator indicator);
}
