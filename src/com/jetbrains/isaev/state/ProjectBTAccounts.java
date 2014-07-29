package com.jetbrains.isaev.state;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Xottab
 * Date: 28.07.2014
 */
public class ProjectBTAccounts {
    private List<BTAccount> btAccounts = new ArrayList<>();
    ;

    public List<BTAccount> getBtAccounts() {
        return btAccounts;
    }

    public void setBtAccounts(List<BTAccount> btAccounts) {
        this.btAccounts = btAccounts;
    }
}
