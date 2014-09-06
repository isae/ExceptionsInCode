package com.jetbrains.isaev.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.CellRendererPanel;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.labels.ActionLink;
import com.intellij.ui.components.panels.HorizontalBox;
import com.intellij.uiDesigner.core.Spacer;
import com.jetbrains.isaev.state.BTProject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Xottab
 * Date: 29.07.2014
 */
public class ProjectListChooser extends DefaultTableCellRenderer {
    private static final JBLabel customFieldLabel = new JBLabel("Custom field: ");


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component result = null;
        SelectableItem item = (SelectableItem) table.getModel().getValueAt(row, 1);
        switch (column) {
            case 0: {
                result = (JBCheckBox) value;
                break;
            }
            case 1: {
                JBLabel label = new JBLabel(item.project.getFullName());
                label.setFont(new Font(label.getFont().getFontName(), Font.PLAIN, 14));
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                label.setAlignmentY(Component.CENTER_ALIGNMENT);
                //label.setBorder(new EmptyBorder(0, 5, 0, 0));
                result = label;
                break;
            }
            case 2: {
              /*  ActionLink actionLink = new ActionLink(item.getCustomFieldName(), new AnAction() {
                    @Override
                    public void actionPerformed(AnActionEvent anActionEvent) {
                        Messages.showInfoMessage("LOH", "PIDR!");
                    }
                });*/
                JLabel link = (JLabel) value;
                link.setAlignmentX(RIGHT);
                result = link;
                break;
            }
        }
        return result;
    }
}
