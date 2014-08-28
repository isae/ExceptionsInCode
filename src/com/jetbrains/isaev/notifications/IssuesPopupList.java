package com.jetbrains.isaev.notifications;

import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBList;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.ui.BTIssueShowDialog;
import com.jetbrains.isaev.ui.CopyMarkerToLineBaloon;

import javax.swing.*;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ilya.Isaev on 05.08.2014.
 */
public class IssuesPopupList extends JBList {
    private JBPopup containingPopup;

    public IssuesPopupList(HashMap<Integer, BTIssue> issueSet) {
        super(new ArrayList<BTIssue>(issueSet.values()));
        addMouseListener(new MouseAdapter() {

            public void leftClick(MouseEvent e) {
                if (getModel().getSize() > 0) {
                    int index = locationToIndex(e.getPoint());
                    BTIssue item = (BTIssue) getModel().getElementAt(index);
                    if (containingPopup != null) containingPopup.cancel();
                    new BTIssueShowDialog(item).show();
                }
            }

            public void rightClick(MouseEvent e) {
                Point p = IssuesPopupList.this.getLocationOnScreen();
                p.x += IssuesPopupList.this.getWidth();
                JBPopupFactory.getInstance().createBalloonBuilder(new CopyMarkerToLineBaloon().panel1)
                        .setHideOnClickOutside(true)
                        .setHideOnKeyOutside(true)
                        .setBlockClicksThroughBalloon(true)
                        .createBalloon().show(new RelativePoint(p), Balloon.Position.atRight);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    rightClick(e);
                } else {
                    leftClick(e);
                }
            }


        });
    }

    public JBPopup getContainingPopup() {
        return containingPopup;
    }

    public void setContainingPopup(JBPopup containingPopup) {
        this.containingPopup = containingPopup;
    }
}
