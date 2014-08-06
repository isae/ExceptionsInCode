package com.jetbrains.isaev.ui;

import com.intellij.ui.components.JBCheckBox;
import com.jetbrains.isaev.state.BTProject;

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

    public SelectableItem(T value, JBCheckBox checkbox) {
        this.value = value;
        this.checkbox = checkbox;
    }

    public static JBCheckBox getCheckBox(BTProject project) {
        return new JBCheckBox(project.getFullName(), project.isMustBeUpdated());
    }
}
