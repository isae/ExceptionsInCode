package com.jetbrains.isaev.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.state.BTIssue;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.*;

public class BTIssueShowDialog extends DialogWrapper {
    private final MyDialog dialog;

    public BTIssueShowDialog(BTIssue item) {
        super(GlobalVariables.project, false);
        dialog = new MyDialog();
        init();
        setTitle(item.getNumber());
        dialog.getSummaryField().setText(item.getTitle());
        dialog.getDescriptionField().setText(item.getDescription());
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return dialog.getContentPane();
    }

    public static class MyDialog extends JDialog {
        private JPanel contentPane;
        private JTextArea summaryField;
        private JTextPane descriptionField;

        public MyDialog() {
            setContentPane(contentPane);
            setModal(true);

            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    onCancel();
                }
            });

// call onCancel() on ESCAPE
            contentPane.registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    onCancel();
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        }

        public JTextArea getSummaryField() {
            return summaryField;
        }

        public JTextPane getDescriptionField() {
            return descriptionField;
        }

        public JPanel getContentPane() {
            return contentPane;
        }

        private void onOK() {
// add your code here
            dispose();
        }

        private void onCancel() {
// add your code here if necessary
            dispose();
        }
    }
}
