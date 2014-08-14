package com.jetbrains.isaev.ui;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.ListSpeedSearch;
import com.intellij.ui.components.JBList;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.jetbrains.isaev.issues.StackTraceElement;
import com.jetbrains.isaev.state.BTIssue;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Ilya.Isaev on 06.08.2014.
 */
public class AllIssuesToolWindowList extends JBList {
    private static IssuesDAO issuesDAO = GlobalVariables.dao;
    Logger logger = Logger.getInstance(AllIssuesToolWindowList.class);
    AllIssuesToolWindowList thisList;
    private DefaultListModel<BTIssue> model = new DefaultListModel<>();
    private DoubleClickListener doubleListener = new DoubleClickListener() {
        @Override
        protected boolean onDoubleClick(MouseEvent event) {
            int index = thisList.locationToIndex(event.getPoint());
            BTIssue issue = model.get(index);

            //Messages.showInfoMessage(GlobalVariables.project, issue.getNumber()+" "+issue.getExceptions().size(), "Title");
            java.util.List<StackTraceElement> stElements = new ArrayList<>();
            issue.getExceptions().values().stream().forEach((ex) -> {
                stElements.addAll(ex.getStacktrace().values().stream().collect(Collectors.toList()));
            });
            Map<String, Pair<PsiJavaFile, StackTraceElement>> files = new HashMap<>();
            for (StackTraceElement element : stElements) {
                for (PsiFile file : FilenameIndex.getFilesByName(GlobalVariables.project, element.getFileName(), GlobalSearchScope.projectScope(GlobalVariables.project))) {
                    if (file instanceof PsiJavaFile) {
                        //todo check not only name but package too
                        files.put(file.getName(), new Pair<>((PsiJavaFile) file, element));
                    }
                }
            }
            JBList links = new JBList(files.values());
            links.setCellRenderer(new ListCellRendererWrapper<Pair<PsiJavaFile, StackTraceElement>>() {

                @Override
                public void customize(JList list, Pair<PsiJavaFile, StackTraceElement> value, int index, boolean selected, boolean hasFocus) {
                    setText(value.first.getPackageName() + "." + value.first.getName());
                }
            });

            JBPopupFactory.getInstance().createListPopupBuilder(links).setItemChoosenCallback(() -> {
                Pair<PsiJavaFile, StackTraceElement> file = (Pair<PsiJavaFile, StackTraceElement>) links.getSelectedValue();
                PsiClass clazz = file.getFirst().getClasses()[0];
                for (PsiMethod method : clazz.findMethodsByName(file.getSecond().getMethodName(), false)) {
                    NavigationUtil.activateFileWithPsiElement(method);
                }
                NavigationUtil.activateFileWithPsiElement(clazz);
            }).createPopup().showCenteredInCurrentWindow(GlobalVariables.project);
            return true;
        }
    };

    public AllIssuesToolWindowList() {
        super();
        thisList = this;
        issuesDAO.getAllIssuesFullState().forEach(model::addElement);
        new ListSpeedSearch(this);
        setModel(model);
        doubleListener.installOn(this);
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellRenderer(new IssueCellRenderer());

    }

    private class IssueCellRenderer extends ListCellRendererWrapper<BTIssue> {

        @Override
        public void customize(JList list, BTIssue value, int index, boolean selected, boolean hasFocus) {
            setText(value.getDrawableDescription());
        }
    }
}
