package com.jetbrains.isaev.ui;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.jetbrains.isaev.integration.youtrack.client.*;
import com.jetbrains.isaev.state.BTAccount;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.state.BTProject;
import org.codehaus.jackson.map.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.ExpandVetoException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ilya.Isaev on 06.09.2014.
 */
public class CommitCustomFieldInfoTask extends Task.Backgroundable {
    public static boolean alreadyRunning = false;
    private final List<BTProject> projects;
    private static final ObjectMapper mapper = new ObjectMapper();
    private YouTrackClient client;

    public CommitCustomFieldInfoTask(List<BTProject> projects) {
        super(GlobalVariables.project, "tmp", false);
        this.projects = projects;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        /*if (projects != null && projects.size() > 0) {
            alreadyRunning = true;
            double currentFraction = 0;
            double fractionDelim = 1 / projects.size();
            progressIndicator.setFraction(currentFraction);
            progressIndicator.setText("Loading issues from database");
            for (BTProject project : projects) {
                if (project.getCustomFieldName() != null) {
                    BTAccount account = project.getBtAccount();
                    client = new YouTrackClientFactory().getClient(account.getDomainName());
                    if (!account.isAsGuest()) client.login(account.getLogin(), account.getPassword());
                    List<BTIssue> issues = IssuesDAO.getInstance().getAllProjectIssues(project.getProjectID());
                    double tmpFractionsDelim = fractionDelim / issues.size();
                    for (BTIssue issue : issues) {
                        YouTrackIssue ytIssue = client.getIssue(issue.getNumber());
                        LinkedList<String> tmp = new LinkedList<String>();
                        String placementJson = null;
                        try {
                            placementJson = mapper.writeValueAsString(issue.getWritableIssuePlacementInfo());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (placementJson != null) {
                            tmp.add(placementJson);
                        }
                        ytIssue.getCustomFieldsValues().put(project.getCustomFieldName(), tmp);
                        client.updateIssueSingleField(ytIssue, project.getCustomFieldName(), tmp);
                        currentFraction += tmpFractionsDelim;
                        progressIndicator.setFraction(currentFraction);
                    }
                } else {
                    currentFraction += fractionDelim;
                    progressIndicator.setFraction(currentFraction);
                }
            }
            alreadyRunning = false;
        }*/
    }
}
