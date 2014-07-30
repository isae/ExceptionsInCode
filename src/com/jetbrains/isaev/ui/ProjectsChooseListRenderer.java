package com.jetbrains.isaev.ui;

import com.intellij.ui.components.JBCheckBox;
import com.jetbrains.isaev.common.CommonBTProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Xottab
 * Date: 29.07.2014
 */
public class ProjectsChooseListRenderer implements ListCellRenderer<SelectableItem<CommonBTProject>> {

    @Override
    public Component getListCellRendererComponent(JList<? extends SelectableItem<CommonBTProject>> list, SelectableItem<CommonBTProject> value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value.checkbox == null) {
            final JBCheckBox checkBox = new JBCheckBox(value.value.getFullName());
            value.checkbox = checkBox;
            checkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {


                }
            });
        }
        return value.checkbox;
    }

}
