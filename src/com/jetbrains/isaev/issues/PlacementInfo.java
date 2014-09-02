package com.jetbrains.isaev.issues;

import com.jetbrains.isaev.notifications.MyHashMap;
import com.jetbrains.isaev.notifications.MyHashSet;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.*;

/**
 * Created by Ilya.Isaev on 22.08.2014.
 */
@JsonIgnoreProperties({"owner"})
public class PlacementInfo {
    private StackTraceElement owner;

    public PlacementInfo(StackTraceElement owner) {
        this.owner = owner;
        this.methods = new MyHashMap<String, Integer>(owner);
        this.absolute = new MyHashSet<Integer>(owner);
    }

    public PlacementInfo() {
    }

    public MyHashMap<String, Integer> methods;
    public MyHashSet<Integer> absolute;

    public MyHashMap<String, Integer> getMethods() {
        return methods;
    }

    public void setMethods(MyHashMap<String, Integer> methods) {
        this.methods = methods;
        if (owner != null)
            owner.mustBeUpdatedOnClose = true;
    }

    public StackTraceElement getOwner() {
        return owner;
    }

    public MyHashSet<Integer> getAbsolute() {
        return absolute;
    }

    public void setAbsolute(MyHashSet<Integer> absolute) {
        this.absolute = absolute;
        if (owner != null)
            owner.mustBeUpdatedOnClose = true;
    }
}
