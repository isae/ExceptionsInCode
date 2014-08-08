package com.jetbrains.isaev.integration.youtrack;

import com.intellij.openapi.progress.ProgressIndicator;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.jetbrains.isaev.dao.SerializableIssuesDAO;
import com.jetbrains.isaev.integration.IssuesUploadStrategy;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackClient;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackClientFactory;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackIssue;
import com.jetbrains.isaev.issues.StacktraceProvider;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.state.BTProject;
import com.jetbrains.isaev.ui.ParsedException;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Ilya.Isaev on 07.08.2014.
 */
public class YouTrackIssuesUploadStrategy extends IssuesUploadStrategy {
    private static final int ISSUES_AT_ONE_TIME = 1000;
    private static final java.lang.String YOUTRACK_DATE_FORMAT_STRING = "yyyy-MM-dd'T'hh:mm:ss";
    private static final java.lang.String STATE = " %23Open %23%7BIn Progress%7D ";
    private static IssuesDAO dao = SerializableIssuesDAO.getInstance();
    private static StacktraceProvider provider = StacktraceProvider.getInstance();
    private static long to;
    private static long from;
    private final YouTrackClient client;// = new YouTrackClientFactory().;

    public YouTrackIssuesUploadStrategy(@NotNull BTProject project) {
        super(project);
        client = new YouTrackClientFactory().getClient(project.getBtAccount().getDomainName());
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
                return new ArrayList<>(0);
            }
            int mid = max / 2;
            result = getIssuesAvoidBugged(filter, after, mid, updatedAfter, errors, wikifyDescription);
            result.addAll(getIssuesAvoidBugged(filter, after + mid, max - mid, updatedAfter, errors, wikifyDescription));
        }
        return result;

    }

    BTIssue processIssue(YouTrackIssue issue, Map<String, YouTrackIssue> mappedIssues) {
        List<ParsedException> parsedExceptions = provider.parseAllExceptions(issue.getSummary() + " " + issue.getDescription());
        if (parsedExceptions.size() != 0) {
            BTIssue is = new BTIssue();
            for (ParsedException ex : parsedExceptions) ex.setIssue(is);
            is.setDescription(mappedIssues.get(issue.getId()).getDescription());
            is.setTitle(issue.getSummary());
            is.setNumber(issue.getId());
            is.setExceptions(parsedExceptions);
            //todo this is wrong, last update field should be taken from youTrack, current way can slow process
            is.setLastUpdated(to);
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
        List<Integer> errors = new ArrayList<>();
        List<BTIssue> parsedIssues = new ArrayList<>();
        for (int after = 0; after < issuesNumber; after += ISSUES_AT_ONE_TIME) {
            List<YouTrackIssue> issues = getIssuesAvoidBugged(STATE, after, ISSUES_AT_ONE_TIME, from, errors, false);
            List<YouTrackIssue> wikifiedIssues = getIssuesAvoidBugged(STATE, after, ISSUES_AT_ONE_TIME, from, errors, true);
            Map<String, YouTrackIssue> mappedWikiIssues = wikifiedIssues.stream().collect(Collectors.toMap(YouTrackIssue::getId, Function.<YouTrackIssue>identity()));
            issues.forEach((is) -> {
                BTIssue issue = processIssue(is, mappedWikiIssues);
                if (issue != null) parsedIssues.add(issue);
            });
            indicator.setFraction(indicator.getFraction() + fractionInc);
            indicator.setText("There are " + parsedIssues.size() + " issues with exceptions were founded so far");
        }
        indicator.setText("Loading founded issues to database");
        btProject.getIssues().addAll(parsedIssues);
        btProject.setLastUpdated(to);
        dao.saveIssues(parsedIssues);
        indicator.setFraction(1);
        indicator.setText("Completed updating of project " + btProject);
    }

    private String getFilterString(long to) {
        StringBuilder builder = new StringBuilder();
        long from;
        from = btProject.getLastUpdated();
        DateFormat format = new SimpleDateFormat(YOUTRACK_DATE_FORMAT_STRING, new Locale("ru"));
        String dateFrom = format.format(new Date(from));
        String dateTo = format.format(new Date(to));
        builder.append("updated: ").append(dateFrom).append(" .. ").append(dateTo).append(" project: ").append(btProject.getShortName()).append(STATE);
        return builder.toString();
    }
}