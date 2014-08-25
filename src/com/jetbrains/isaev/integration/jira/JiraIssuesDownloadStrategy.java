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
    private final YouTrackClient client;// = new YouTrackClientFactory().;

    public JiraIssuesDownloadStrategy(@NotNull BTProject project) {
        super(project);
        BTAccount acc = project.getBtAccount();
        client = new YouTrackClientFactory().getClient(acc.getDomainName());
    }

    public List<YouTrackIssue> getIssuesAvoidBugged(String filter,
                                                    int after,
                                                    int max,
                                                    long updatedAfter,
                                                    List<Integer> errors, boolean wikifyDescription) {
        List<YouTrackIssue> result;
        try {
            result = client.getIssuesInProject(btProject.getShortName(), filter, after, max, updatedAfter, wikifyDescription);
        } catch (Exception e) {
            if (max == 1) {
                if (errors != null) errors.add(after + 1);
                return new ArrayList<YouTrackIssue>(0);
            }
            int mid = max / 2;
            result = getIssuesAvoidBugged(filter, after, mid, updatedAfter, errors, wikifyDescription);
            result.addAll(getIssuesAvoidBugged(filter, after + mid, max - mid, updatedAfter, errors, wikifyDescription));
        }
        return result;

    }

    BTIssue processIssue(YouTrackIssue issue, Map<String, YouTrackIssue> mappedIssues) {
        Map<Integer, ParsedException> parsedExceptions = provider.parseAllExceptions(issue.getSummary() + " " + issue.getDescription());
        if (parsedExceptions.size() != 0) {
            BTIssue is = new BTIssue();
            is.setProjectID(btProject.getProjectID());
            for (ParsedException ex : parsedExceptions.values()) ex.setIssue(is);
            is.setDescription(mappedIssues.get(issue.getId()).getDescription());
            is.setTitle(issue.getSummary());
            is.setNumber(issue.getId());
            is.setExceptions(parsedExceptions);
            long updated = issue.getSingleField("updated") == null ? to : Long.valueOf(issue.getSingleField("updated"));
            is.setLastUpdated(new Timestamp(updated));
            is.setProject(btProject);
            btProject.getIssues().add(is);
            return is;
        } else return null;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setFraction(0.0);
        indicator.setText("Logging to YouTrack: " + btProject.getBtAccount().getLogin());
        client.login(btProject.getBtAccount().getLogin(), btProject.getBtAccount().getPassword());
        to = System.currentTimeMillis();
        String filterString = getFilterString(to);
        int issuesNumber = client.getNumberOfIssues(filterString);
        indicator.setFraction(0.05);
        indicator.setText(String.valueOf(issuesNumber) + " issues must be proceed");
        double fractionInc = 0.7d / (issuesNumber / ISSUES_AT_ONE_TIME + 1);
        List<Integer> errors = new ArrayList<Integer>();
        List<BTIssue> parsedIssues = new ArrayList<BTIssue>();
        for (int after = 0; after < issuesNumber; after += ISSUES_AT_ONE_TIME) {
            List<YouTrackIssue> issues = getIssuesAvoidBugged(STATE, after, ISSUES_AT_ONE_TIME, from, errors, false);
            List<YouTrackIssue> wikifiedIssues = getIssuesAvoidBugged(STATE, after, ISSUES_AT_ONE_TIME, from, errors, true);
            Map<String, YouTrackIssue> mappedWikiIssues = new HashMap<String, YouTrackIssue>();
            for (YouTrackIssue issue : wikifiedIssues) {
                mappedWikiIssues.put(issue.getId(), issue);
            }
            // wikifiedIssues.stream().collect(Collectors.toMap(YouTrackIssue::getId, Function.<YouTrackIssue>identity()));
            for (YouTrackIssue is : issues) {
                BTIssue issue = processIssue(is, mappedWikiIssues);
                if (issue != null) parsedIssues.add(issue);

            }
            indicator.setFraction(indicator.getFraction() + fractionInc);
            indicator.setText("There are " + parsedIssues.size() + " issues with exceptions were founded so far");
        }
        indicator.setText("Loading founded issues to database");
        btProject.setLastUpdated(new Timestamp(to));
        dao.storeIssues(parsedIssues);
        dao.updateProject(btProject);
        indicator.setFraction(1);
        indicator.setText("Completed updating of project " + btProject);
    }

    private String getFilterString(long to) {
        StringBuilder builder = new StringBuilder();
        long from;
        from = btProject.getLastUpdated().getTime();
        DateFormat format = new SimpleDateFormat(YOUTRACK_DATE_FORMAT_STRING, new Locale("ru"));
        String dateFrom = format.format(new Date(from));
        String dateTo = format.format(new Date(to));
        builder.append("updated: ").append(dateFrom).append(" .. ").append(dateTo).append(" project: ").append(btProject.getShortName()).append(STATE);
        return builder.toString();
    }
}
