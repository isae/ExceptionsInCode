package com.jetbrains.isaev.notifications;

import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.ui.components.JBList;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.ui.BTIssueShowDialog;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

/**
 * Created by Ilya.Isaev on 05.08.2014.
 */
public class IssuesPopupList extends JBList {
    private JBPopup containingPopup;

    public IssuesPopupList(Set<BTIssue> issueSet) {
        super(issueSet);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (getModel().getSize() > 0) {
                    int index = locationToIndex(e.getPoint());
                    BTIssue item = (BTIssue) getModel().getElementAt(index);
                    if (containingPopup != null) containingPopup.cancel();
                    new BTIssueShowDialog(item).show();
                    // Messages.showMessageDialog(GlobalVariables.project, item.getDescription(), item.getNumber() + ": " + item.getTitle(), Messages.getInformationIcon());
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
