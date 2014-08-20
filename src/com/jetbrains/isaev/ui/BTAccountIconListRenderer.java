package com.jetbrains.isaev.ui;

import com.intellij.ui.ListCellRendererWrapper;
import com.jetbrains.isaev.state.BTAccountType;

import javax.swing.*;

/**
 * Created by Ilya.Isaev on 18.08.2014.
 */
public class BTAccountIconListRenderer extends ListCellRendererWrapper<BTAccountType> {
    @Override
    public void customize(JList list, BTAccountType value, int index, boolean selected, boolean hasFocus) {
        setText(value.getName());
        Icon icon = null;
        switch (value) {
            case YOUTRACK: {
                icon = IconProvider.getIcon(IconProvider.IconRef.YOUTRACK_SMALL);
                break;
            }/*
            case JIRA: {
                icon = IconProvider.getIcon(IconProvider.IconRef.JIRA_SMALL);
                break;
            }*/
        }
        setIcon(icon);
    }
}
