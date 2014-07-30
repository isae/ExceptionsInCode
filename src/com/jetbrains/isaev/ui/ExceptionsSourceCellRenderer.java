package com.jetbrains.isaev.ui;

import com.intellij.ui.ListCellRendererWrapper;
import com.jetbrains.isaev.state.CommonBTAccount;

import javax.swing.*;

/**
 * User: Xottab
 * Date: 25.07.2014
 */
public class ExceptionsSourceCellRenderer extends ListCellRendererWrapper<CommonBTAccount> {
    @Override
    public void customize(JList list, CommonBTAccount value, int index, boolean selected, boolean hasFocus) {
        setIcon(IconProvider.getIcon(IconProvider.IconRef.YOUTRACK));
        setText(value.getDomainName());
    }
}
