package com.jetbrains.isaev.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.jetbrains.isaev.state.BTIssue;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.URISyntaxException;

public class BTIssueShowDialog extends DialogWrapper {
    private JPanel contentPane;
    private JTextArea summaryField;
    private JTextPane descriptionField;
    private JButton button1;
    private JCheckBox mustBeShownCheckBox;
    private JEditorPane editorPane1;
    private BTIssue issue;

    private static void open(URI uri) {
        if (Desktop.isDesktopSupported() && uri != null) {
            try {
                Desktop.getDesktop().browse(uri);
            } catch (Exception t) {
                t.printStackTrace();
            }
        }
    }

    @Override
    protected void doOKAction() {
        issue.setTitle(summaryField.getText());
        issue.setDescription(descriptionField.getText());
        issue.setMustBeShown(mustBeShownCheckBox.isSelected());
        IssuesDAO.getInstance().updateIssue(this.issue);
        super.doOKAction();
    }

    public BTIssueShowDialog(final BTIssue issue) {
        super(GlobalVariables.getInstance().project, false);
        this.issue = issue;
        setSize(800, 600);
        setTitle(issue.getNumber());
        summaryField.setText(issue.getTitle());
        descriptionField.setText(issue.getDescription());
        mustBeShownCheckBox.setSelected(issue.isMustBeShown());
        init();
        String domain = issue.getProject().getBtAccount().getDomainName();
        final String tmp = domain + "/issue/" + issue.getNumber();
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
