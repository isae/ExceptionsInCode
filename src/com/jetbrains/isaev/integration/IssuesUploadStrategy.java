package com.jetbrains.isaev.integration;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.state.BTProject;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Ilya.Isaev on 07.08.2014.
 */
public abstract class IssuesUploadStrategy extends Task.Backgroundable {
    @NotNull
    protected final BTProject btProject;

    public IssuesUploadStrategy(@NotNull BTProject project) {
        super(GlobalVariables.project, "Updating issues list");
        this.btProject = project;
    }

    @Override
    public abstract void run(@NotNull ProgressIndicator indicator);
}
