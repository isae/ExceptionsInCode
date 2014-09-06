package com.jetbrains.isaev.integration.youtrack;

import com.jetbrains.isaev.issues.PlacementInfo;

import java.util.HashMap;

/**
 * Created by Ilya.Isaev on 06.09.2014.
 */
public class IssueCustomFieldPlacementInfo {
    public IssueCustomFieldPlacementInfo() {
    }

    private HashMap<Integer, PlacementInfo> issueInfo;

    public HashMap<Integer, PlacementInfo> getIssueInfo() {
        return issueInfo;
    }

    public void setIssueInfo(HashMap<Integer, PlacementInfo> issueInfo) {
        this.issueInfo = issueInfo;
    }
}
