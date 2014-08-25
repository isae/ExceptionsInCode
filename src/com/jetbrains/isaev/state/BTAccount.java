package com.jetbrains.isaev.state;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Xottab
 * Date: 25.07.2014
 */
public class BTAccount {
    private boolean asGuest;
    @NotNull
    private String domainName;
    @NotNull
    private String login;
    @NotNull
    private String password;
    @NotNull
    private BTAccountType type;

    private List<BTProject> projects;
    private int accountID;

    public BTAccount(int accountID, String domainName, String login, String password, BTAccountType type, boolean asGuest) {
        this(domainName, login, password, type, asGuest);
        this.accountID = accountID;
    }

    public BTAccount(@NotNull String domainName, @NotNull String login, @NotNull String password, @NotNull BTAccountType type, boolean asGuest) {
        this.asGuest = asGuest;
        this.domainName = domainName;
        this.login = login;
        this.password = password;
        this.type = type;
    }

    @NotNull
    public BTAccountType getType() {
        return type;
    }

    public void setType(@NotNull BTAccountType type) {
        this.type = type;
    }

    @NotNull
    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(@NotNull String domainName) {
        this.domainName = domainName;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String login) {
        this.login = login;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public List<BTProject> getProjects() {
        if (projects == null) {
            projects = new ArrayList<BTProject>(0);
        }
        return projects;
    }

    public void setProjects(List<BTProject> projects) {
        this.projects = projects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BTAccount btAccount = (BTAccount) o;

        return domainName.equals(btAccount.domainName) && login.equals(btAccount.login) && password.equals(btAccount.password) && type == btAccount.type;

    }

    @Override
    public int hashCode() {
        int result = domainName.hashCode();
        result = 31 * result + login.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public boolean isAsGuest() {
        return asGuest;
    }

    public void setAsGuest(boolean asGuest) {
        this.asGuest = asGuest;
    }

}
