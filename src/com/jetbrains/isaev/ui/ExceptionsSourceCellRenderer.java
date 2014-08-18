package com.jetbrains.isaev.ui;

import com.intellij.ui.ListCellRendererWrapper;
import com.jetbrains.isaev.state.BTAccount;

import javax.swing.*;

/**
 * User: Xottab
 * Date: 25.07.2014
 */
public class ExceptionsSourceCellRenderer extends ListCellRendererWrapper<BTAccount> {
    @Override
    public void customize(JList list, BTAccount value, int index, boolean selected, boolean hasFocus) {
        Icon icon = null;
        switch (value.getType()) {
            case YOUTRACK: {
                icon = IconProvider.getIcon(IconProvider.IconRef.YOUTRACK);
                break;
            }
            case JIRA: {
                icon = IconProvider.getIcon(IconProvider.IconRef.JIRA);
                break;
            }
        }
        setIcon(icon);
        setText(value.getDomainName());
    }
}
