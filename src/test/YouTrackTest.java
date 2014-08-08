package test;

import com.jetbrains.isaev.integration.youtrack.client.YouTrackClient;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackClientFactory;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackIssue;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackProject;
import com.jetbrains.isaev.issues.StacktraceProvider;
import com.jetbrains.isaev.ui.ParsedException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Xottab
 * Date: 23.07.2014
 */
public class YouTrackTest {
    private static final YouTrackClientFactory clientFactory = new YouTrackClientFactory();
    private static final String JETBRAINS_YOUTRACK_URL = "http://youtrack.jetbrains.com";
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
                return new ArrayList<>(0);
            }
            int mid = max / 2;
            result = getIssuesAvoidBugged(projectName, filter, after, mid, updatedAfter, client, errors);
            result.addAll(getIssuesAvoidBugged(projectName, filter, after + mid, max - mid, updatedAfter, client, errors));
        }
        return result;

    }

    public static void main(String[] args) {

        YouTrackClient client = clientFactory.getClient(JETBRAINS_YOUTRACK_URL);
        client.login("Ilya.Isaev@jetbrains.com", ".Lu85Ga");
        List<YouTrackProject> projects = client.getProjects();
        YouTrackProject proj = null;
        for (YouTrackProject project : projects) {
            if (project.getProjectShortName().equals("IDEA")) proj = project;
            // out.println("full " + project.getProjectFullName() + " short " + project.getProjectShortName());
        }
        YouTrackIssue issue = client.getIssue("IDEA-95925");
        List<ParsedException> exceptions = StacktraceProvider.getTestInstance().parseAllTestExceptions(issue.getSummary() + " " + issue.getDescription());
        // out.println("\n\n\n");
       /* List<YouTrackIssue> issues = new ArrayList<>(110000);
        StacktraceProvider provider = StacktraceProvider.getInstance();

        //#2539;
        int after = 0;
        int counter = 0;
        int finded = 0;
        List<Integer> errors = new ArrayList<>();
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

                List<ParsedException> parsedExceptions = provider.parseAllExceptions(issue.getSummary() + " " + issue.getDescription());
                if (parsedExceptions.size() > 0) {
                    out.println(issue.getId());
                    counter++;
                    out.println(issue.getSummary() + "\n" + issue.getDescription() + "\n_____");
                    for (ParsedException exception : parsedExceptions) {

                        out.println(exception.getName() + ": " + exception.getOptionalMessage());
                        for (com.jetbrains.isaev.issues.StackTraceElement element : exception.getStacktrace()) {
                            out.println("at " + element.toString());
                        }
                        out.println("_____");
                    }
                    out.println("\n______________________\n");
                }
            }
            out.close();
        } while (finded > 0);
         *//*catch (Exception e) {
            System.out.println("ERROR!! ERROR!! ERROR!!");
            e.printStackTrace();
        }*//*
        out.println("Issues finded: " + finded);
        // YouTrackIssue issue = client.getIssue("IDEA-127736");
        //  ParsedException[] finded = provider.parseAllExceptions(issue.getSummary()+" "+issue.getDescription());


        out.println("Summary: " + counter + " items");
        out.close();*/
    }
}
