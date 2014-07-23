/**
 @author: amarch
 */

package com.jetbrains.integration.youtrack.client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name = "projects")
public class YouTrackProjectsList {

    private List<YouTrackProject> projects = new LinkedList<YouTrackProject>();

    @XmlElement(name = "project", type = YouTrackProject.class)
    public List<YouTrackProject> getProjects() {
        return projects;
    }

    public void setProjects(List<YouTrackProject> projects) {
        this.projects = projects;
    }

}
