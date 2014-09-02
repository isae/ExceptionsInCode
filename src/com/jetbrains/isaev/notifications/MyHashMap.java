package com.jetbrains.isaev.notifications;

import com.jetbrains.isaev.issues.*;
import com.jetbrains.isaev.issues.StackTraceElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashMap;

/**
 * Created by Ilya.Isaev on 01.09.2014.
 */
@JsonIgnoreProperties({"owner"})
public class MyHashMap<K, V> extends HashMap<K, V> {
    private StackTraceElement owner;


    public MyHashMap() {
        super();
    }

    public MyHashMap(StackTraceElement owner) {
        super();
        this.owner = owner;
    }

    @Override
    public V put(K key, V value) {
        if (owner != null)
            owner.mustBeUpdatedOnClose = true;
        return super.put(key, value);
    }

    @Override
    public V remove(Object key) {
        if (owner != null)
            owner.mustBeUpdatedOnClose = true;
        return super.remove(key);
    }
}