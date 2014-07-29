package com.jetbrains.isaev.ui;

import com.intellij.ui.components.JBCheckBox;

/**
 * User: Xottab
 * Date: 29.07.2014
 */
public class SelectableItem<T> {
    public T value;
    public JBCheckBox checkbox;

    public SelectableItem(T project) {
        this.value = project;
    }
}
