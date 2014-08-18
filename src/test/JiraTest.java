package test;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Ilya.Isaev on 18.08.2014.
 */
public class JiraTest {
    public static void main(String[] args) {
        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        URI jiraServerUri = null;
        try {
            jiraServerUri = new URI("https://jira.spring.io");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, "", "");
      //  com.atlassian.util.concurrent.Promise<Iterable<BasicProject>> projects = restClient.getProjectClient().getAllProjects();
    }
}
