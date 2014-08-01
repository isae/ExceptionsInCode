package com.jetbrains.isaev.common;

import com.fasterxml.jackson.annotation.*;
import com.jetbrains.isaev.dao.ZipUtils;
import com.jetbrains.isaev.ui.ParsedException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Xottab
 * Date: 18.07.2014
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@JsonIgnoreProperties({"description"})
public class BTIssue implements Serializable {
    private String title;
    private String description;
    private byte[] zippedDescr;
    @JsonBackReference
    private CommonBTProject project;
    @JsonManagedReference
    private List<ParsedException> exceptions = new ArrayList<>();

    public BTIssue() {
    }

    public CommonBTProject getProject() {
        return project;
    }

    public void setProject(CommonBTProject project) {
        this.project = project;
    }

    public byte[] getZippedDescr() {

        return zippedDescr;
    }

    public void setZippedDescr(byte[] zippedDescr) {
        this.zippedDescr = zippedDescr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        if (description == null) description = ZipUtils.decompress(zippedDescr);
        return description;
    }

    public void setDescription(String description) {
        this.zippedDescr = ZipUtils.compress(description);
        this.description = description;
    }

    public List<ParsedException> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<ParsedException> exceptions) {
        this.exceptions = exceptions;
    }
}
