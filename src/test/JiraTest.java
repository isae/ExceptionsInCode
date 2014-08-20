package test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Ilya.Isaev on 18.08.2014.
 */
public class JiraTest {

    public static void main(String[] args) throws URISyntaxException {
        /*final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        final URI jiraServerUri = new URI("https://jira.spring.io");
        final com.atlassian.jira.rest.client.api.JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, "xottab", "isaev123");
        try {
            final Issue issue = restClient.getIssueClient().getIssue("TST-7").claim();
            System.out.println(issue);
        } finally {
            // cleanup the restClient
            restClient.close();
        }
       *//* final NullProgressMonitor pm = new NullProgressMonitor();
        Iterable<BasicProject> projects = restClient.getProjectClient().getAllProjects(pm);
        List<Project> projectList = new ArrayList<>();
        projects.forEach(pr -> projectList.add(restClient.getProjectClient().getProject(pr.getKey(), pm)));
        restClient.getIssueClient().getIssue("", pm);
        for (BasicProject proj : projectList) {
            System.out.println(proj.getId() + " " + proj.getName() + " " + proj.getKey());
        }*//*
*/
    }

}
