package com.jetbrains.isaev.notifications;

import com.jetbrains.isaev.issues.*;
import com.jetbrains.isaev.issues.StackTraceElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashSet;

/**
 * Created by Ilya.Isaev on 01.09.2014.
 */
@JsonIgnoreProperties({"owner"})
public class MyHashSet<T> extends HashSet<T> {
    private StackTraceElement owner;

    public MyHashSet(com.jetbrains.isaev.issues.StackTraceElement owner) {
        super();
        this.owner = owner;
    }

    public MyHashSet() {
        super();
    }

    @Override
    public boolean add(T t) {
        if (owner != null)
            owner.mustBeUpdatedOnClose = true;
        return super.add(t);
    }

    @Override
    public boolean remove(Object o) {
        if (owner != null)
            owner.mustBeUpdatedOnClose = true;
        return super.remove(o);
    }
}
