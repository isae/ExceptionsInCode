package com.jetbrains.isaev.state;

import java.io.Serializable;

/**
 * User: Xottab
 * Date: 25.07.2014
 */
public class BTAccount implements Serializable {

    private String domainName;
    private String login;
    private String password;
    private BTAccountType type;

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

    enum BTAccountType {
        YOUTRACK, JIRA
    }
}
