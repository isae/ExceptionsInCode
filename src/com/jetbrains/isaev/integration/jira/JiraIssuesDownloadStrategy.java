package com.jetbrains.isaev.integration.jira;

import com.intellij.openapi.progress.ProgressIndicator;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.jetbrains.isaev.integration.IssuesDownloadStrategy;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackClient;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackClientFactory;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackIssue;
import com.jetbrains.isaev.issues.StacktraceProvider;
import com.jetbrains.isaev.state.BTAccount;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.state.BTProject;
import com.jetbrains.isaev.ui.ParsedException;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Ilya.Isaev on 18.08.2014.
 */
public class JiraIssuesDownloadStrategy extends IssuesDownloadStrategy {

    private static final int ISSUES_AT_ONE_TIME = 1000;
    private static final java.lang.String YOUTRACK_DATE_FORMAT_STRING = "yyyy-MM-dd'T'hh:mm:ss";
    private static final java.lang.String STATE = " %23Open %23%7BIn Progress%7D ";
    //  private static final java.lang.String STATE = " %23%7BIn Progress%7D ";
    private static IssuesDAO dao = GlobalVariables.getInstance().dao;
    private static StacktraceProvider provider = StacktraceProvider.getInstance();
    private static long to;
    private static long from;

    public JiraIssuesDownloadStrategy(@NotNull BTProject[] project) {
        super(project);
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
    }
}
