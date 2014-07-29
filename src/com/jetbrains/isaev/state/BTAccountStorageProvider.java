package com.jetbrains.isaev.state;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: Xottab
 * Date: 28.07.2014
 */
@State(
        name = "BTAccounts",
        storages = {
                @Storage(id = "acc", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/bugtrackers.xml", scheme = StorageScheme.DEFAULT)
        }
)
public class BTAccountStorageProvider implements ProjectComponent, PersistentStateComponent<ProjectBTAccounts> {

    private ProjectBTAccounts projectBTAccounts;

    public BTAccountStorageProvider(Project project) {
        projectBTAccounts = new ProjectBTAccounts();
    }

    @Nullable
    @Override
    public ProjectBTAccounts getState() {
        return projectBTAccounts;
    }

    @Override
    public void loadState(ProjectBTAccounts projectBTAccounts) {
        this.projectBTAccounts = projectBTAccounts;
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "BTAccountStorageProvider";
    }

    public void projectOpened() {
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }

}
