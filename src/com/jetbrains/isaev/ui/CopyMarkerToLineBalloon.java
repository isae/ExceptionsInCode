package com.jetbrains.isaev.ui;

import com.jetbrains.isaev.state.BTIssue;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Ilya.Isaev on 28.08.2014.
 */
public class CopyMarkerToLineBalloon {
    public JPanel panel1;
    public JTextField textField1;
    public JButton okButton;
    public JTextArea textArea1;

    public CopyMarkerToLineBalloon(BTIssue issue) {
        textArea1.setText(issue.getTitle());
    }
}
