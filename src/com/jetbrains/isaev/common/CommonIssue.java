package com.jetbrains.isaev.common;

import com.jetbrains.isaev.ui.ParsedException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Xottab
 * Date: 18.07.2014
 */
public class CommonIssue implements Serializable {
    private String title;
    private String description;
    private List<ParsedException> exceptions = new ArrayList<>();

    public CommonIssue() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ParsedException> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<ParsedException> exceptions) {
        this.exceptions = exceptions;
    }
}
