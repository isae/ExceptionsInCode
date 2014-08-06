package com.jetbrains.isaev.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.state.BTIssue;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class BTIssueShowDialog extends DialogWrapper {
    private JPanel contentPane;
    private JTextArea summaryField;
    private JTextPane descriptionField;

    public BTIssueShowDialog(BTIssue item) {
        super(GlobalVariables.project, false);
        setSize(800, 600);
        setTitle(item.getNumber());
        summaryField.setText(item.getTitle());
        descriptionField.setText(item.getDescription());
        init();


        //  setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        //   addWindowListener(new WindowAdapter() {
        //       public void windowClosing(WindowEvent e) {
        //  onCancel();
        //  }
        //   });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public BTIssueShowDialog() {
        super(GlobalVariables.project, false);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
