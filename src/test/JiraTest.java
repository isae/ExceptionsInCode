package test;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Ilya.Isaev on 18.08.2014.
 */
public class JiraTest {

    public static void main(String[] args) throws URISyntaxException {
        final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
        final URI jiraServerUri = new URI("https://jira.spring.io");
        final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, "xottab", "isaev123");
        final NullProgressMonitor pm = new NullProgressMonitor();
        Iterable<BasicProject> projects = restClient.getProjectClient().getAllProjects(pm);
        for (BasicProject proj : projects) {
            System.out.println(proj.getId() + " " + proj.getName() + " " + proj.getKey());
        }

    }

}
