package testSrc;

import com.jetbrains.isaev.integration.youtrack.client.*;
import com.jetbrains.isaev.issues.StacktraceProvider;
import com.jetbrains.isaev.ui.ParsedException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: Xottab
 * Date: 23.07.2014
 */
public class YouTrackTest {
    private static final YouTrackClientFactory clientFactory = new YouTrackClientFactory();
    private static final String JETBRAINS_YOUTRACK_URL = "http://youtrack.jetbrains.com";
    private static final String MY_YOUTRACK_URL = "http://ololo.myjetbrains.com/youtrack";
    private static final String LOCAL_YOUTRACK_URL = "http://localhost:80";
    private static PrintWriter out;

    public static List<YouTrackIssue> getIssuesAvoidBugged(String projectName,
                                                           String filter,
                                                           int after,
                                                           int max,
                                                           long updatedAfter,
                                                           YouTrackClient client,
                                                           List<Integer> errors) {
        List<YouTrackIssue> result;
        try {
            result = client.getIssuesInProject(projectName, filter, after, max, updatedAfter);
        } catch (Exception e) {
            if (max == 1) {
                if (errors != null) errors.add(after + 1);
                return new ArrayList<YouTrackIssue>(0);
            }
            int mid = max / 2;
            result = getIssuesAvoidBugged(projectName, filter, after, mid, updatedAfter, client, errors);
            result.addAll(getIssuesAvoidBugged(projectName, filter, after + mid, max - mid, updatedAfter, client, errors));
        }
        return result;

    }

    public static void main(String[] args) {

        YouTrackClient client = clientFactory.getClient(JETBRAINS_YOUTRACK_URL);
        YouTrackProject proj = null;
        PrintWriter out = null;
        List<YouTrackProject> projects = client.getProjects();
        for (YouTrackProject project : projects) {
            if (project.getProjectShortName().equals("IDEA")) proj = project;
        }
        List<YouTrackIssue> issues = new ArrayList<YouTrackIssue>(110000);
        StacktraceProvider provider = StacktraceProvider.getInstance();

        //#2539;
        int after = 0;
        int counter = 0;
        int finded = 0;
        List<Integer> errors = new ArrayList<Integer>();
        do {
            try {
                out = new PrintWriter(new FileWriter("test.out", true));
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<YouTrackIssue> tempIssues = getIssuesAvoidBugged(proj.getProjectShortName(), "", after, 1000, 0, client, errors);
            finded = tempIssues.size();
            //  issues.addAll(tempIssues);
            after += finded;
            for (YouTrackIssue issue : tempIssues) {

                Map<Integer, ParsedException> parsedExceptions = provider.parseAllTestExceptions(issue.getSummary() + " " + issue.getDescription());
                if (parsedExceptions.size() > 0) {
                    out.println(issue.getId());
                    counter++;
                    out.println(issue.getSummary() + "\n" + issue.getDescription() + "\n_____");
                    for (ParsedException exception : parsedExceptions.values()) {

                        out.println(exception.getName() + ": " + exception.getOptionalMessage());
                        for (com.jetbrains.isaev.issues.StackTraceElement element : exception.getStacktrace().values()) {
                            out.println("at " + element.toString());
                        }
                        out.println("_____");
                    }
                    out.println("\n______________________\n");
                }
            }
            out.close();
        } while (finded > 0);

        out.println("Issues finded: " + finded);


        out.println("Summary: " + counter + " items");
        out.close();
    }
}
