package com.jetbrains.isaev.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.labels.ActionLink;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.state.BTIssue;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BTIssueShowDialog extends DialogWrapper {
    private JPanel contentPane;
    private JTextArea summaryField;
    private JTextPane descriptionField;
    private JButton button1;
    private JEditorPane editorPane1;
    private BTIssue item;

    private static void open(URI uri) {
        if (Desktop.isDesktopSupported() && uri != null) {
            try {
                Desktop.getDesktop().browse(uri);
            } catch (IOException ignored) {
            }
        }
    }

    public BTIssueShowDialog(final BTIssue item) {
        super(GlobalVariables.getInstance().project, false);
        this.item = item;
        setSize(800, 600);
        setTitle(item.getNumber());
        summaryField.setText(item.getTitle());
        descriptionField.setText(item.getDescription());
        init();
        String domain = item.getProject().getBtAccount().getDomainName();
        final String tmp = domain + "/issue/" + item.getNumber();
        button1.setText(tmp);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                URI uri = null;
                try {
                    uri = new URI(tmp);
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
                final URI finalUri = uri;

                open(finalUri);
            }
        });
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
        super(GlobalVariables.getInstance().project, false);
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
