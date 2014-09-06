package com.jetbrains.isaev.ui;

import com.intellij.ui.components.JBCheckBox;
import com.jetbrains.isaev.state.BTProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * User: Xottab
 * Date: 29.07.2014
 */
public class SelectableItem {
    public BTProject project;
    public JBCheckBox checkbox;

    public SelectableItem(BTProject project) {
        this.project = project;
        this.checkbox = getCheckBox(project);
    }

    public static JBCheckBox getCheckBox(final BTProject project) {
        JBCheckBox result = new JBCheckBox();
        result.setAlignmentX(Component.LEFT_ALIGNMENT);
        result.setAlignmentY(Component.CENTER_ALIGNMENT);
        result.setSelected(project.isMustBeUpdated());
        result.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                project.setMustBeUpdated(!project.isMustBeUpdated());
                super.mouseClicked(e);
            }
        });
        return result;
    }

    public String getCustomFieldName() {
        if (project.getCustomFieldName() == null) return "none";
        return project.getCustomFieldName();
    }

}
