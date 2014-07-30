package com.jetbrains.isaev.state;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Xottab
 * Date: 28.07.2014
 */
public class ProjectBTAccounts {
    private List<CommonBTAccount> btAccounts = new ArrayList<>();

    public List<CommonBTAccount> getBtAccounts() {
        return btAccounts;
    }

    public void setBtAccounts(List<CommonBTAccount> btAccounts) {
        this.btAccounts = btAccounts;
    }
}
