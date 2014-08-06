package com.jetbrains.isaev.state;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Xottab
 * Date: 25.07.2014
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class BTAccount implements Serializable {

    private String domainName;
    private String login;
    private String password;
    private BTAccountType type;

    @JsonManagedReference(value = "projects")
    private List<BTProject> projects;

    public BTAccount() {
    }

    public BTAccount(String domainName, String login, String password) {

        this.domainName = domainName;
        this.login = login;
        this.password = password;
    }

    public BTAccountType getType() {
        return type;
    }

    public void setType(BTAccountType type) {
        this.type = type;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<BTProject> getProjects() {
        if (projects == null) {
            projects = new ArrayList<>(0);
        }
        return projects;
    }

    public void setProjects(List<BTProject> projects) {
        this.projects = projects;
    }

    enum BTAccountType {
        YOUTRACK, JIRA
    }
}