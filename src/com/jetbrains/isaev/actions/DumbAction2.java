package com.jetbrains.isaev.actions;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Log;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.content.AlertIcon;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * User: Xottab
 * Date: 20.07.2014
 */
public class DumbAction2 extends AnAction {
    HintManager hints = HintManager.getInstance();

    public DumbAction2() {
        super("_Dumb Action 2");
    }

    private Balloon createBalloon(JComponent panel) {

        /*balloon.addListener(new JBPopupAdapter() {
            @Override
            public void onClosed(LightweightWindowEvent event) {
                Disposer.dispose(EditSignatureBalloon.this);
            }
        });   */
        return JBPopupFactory.getInstance().createDialogBalloonBuilder(panel, "Kotlin signature")
                .setHideOnClickOutside(true)
                .setHideOnKeyOutside(true)
                .setBlockClicksThroughBalloon(true).createBalloon();
    }

    public void a(){

    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        a();
        Project project = e.getProject();
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        String current = null;
        try {
            current = new File(".").getCanonicalPath();
            Log.print("Current dir:" + current);
            System.out.println("Current dir:" + current);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Icon ic = new ImageIcon("C:\\Users\\Xottab\\Desktop\\jetbrainz\\ExceptionsInCode\\images\\icon.png", "My icon");
        Icon icon = new AlertIcon(ic);
        JButton button = new JButton(icon);
        assert editor != null;
        JComponent comp = editor.getContentComponent();

        //   JBPopupFactory.getInstance().createConfirmation("title", "onYes", "noText", new A(),0).showCenteredInCurrentWindow(project);
        // String txt= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
        //  Notifications.Bus.notify(new Notification("Bom Bom","Title","Content", NotificationType.WARNING));
        //   notifications.showErrorHint(editor, "OLOLO!!! RED Hint!!", HintManager.LEFT);
        //notifications.showErrorHint(editor, "OLOLO!!! Second RED Hint!!", HintManager.RIGHT);
      /*  JComponent label1 = MyHintUtils.createErrorLabel("OLOLO!!! RED Hint!!");
        JComponent label2 = MyHintUtils.createErrorLabel("OLOLO!!! Second RED Hint!!");
        RelativePoint p1 = new RelativePoint(new Point(50,50));
        RelativePoint p2 = new RelativePoint(new Point(60,60));
        HintManager.getInstance().showHint(label1, p1, HintManager.UPDATE_BY_SCROLLING, 20000);
        HintManager.getInstance().showHint(label2, p2, HintManager.UPDATE_BY_SCROLLING, 20000);*/
    }

    class A implements Runnable {

        @Override
        public void run() {
            Messages.showMessageDialog("message", "Title", null);

        }
    }
}
