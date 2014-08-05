package com.jetbrains.isaev.notifications;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.impl.IconLineMarkerProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.*;
import com.jetbrains.isaev.dao.SerializableIssuesDAO;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.ui.IconProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

/**
 * User: Xottab
 * Date: 21.07.2014
 */
public class MyLinesMarkerProvider extends IconLineMarkerProvider implements DumbAware {
    private static final Logger logger = Logger.getInstance(MyLinesMarkerProvider.class);
    static Icon icon = IconProvider.getIcon(IconProvider.IconRef.WARN);
    Icon icon2 = IconProvider.getIcon(IconProvider.IconRef.YOUTRACK);
    private SerializableIssuesDAO issuesDAO = SerializableIssuesDAO.getInstance();
    private Map<Integer, Boolean> markersBySignature = new HashMap<>();
    private Map<String, Boolean> markersByMName = new HashMap<>();
    private PsiJavaFile currentlyOpened = null;

    private static int methodHash(@NotNull PsiMethod m) {
        return m.getSignature(PsiSubstitutor.EMPTY).hashCode();
    }

    private LineMarkerInfo getLineMarkerInfo(@NotNull PsiMethod method) {
        PsiClass clazz = method.getContainingClass();

        PsiJavaFile file = (PsiJavaFile) clazz.getContainingFile();
        String fullMethodName = file.getPackageName() + "." + clazz.getName() + "." + method.getName();
        List<com.jetbrains.isaev.issues.StackTraceElement> elements = issuesDAO.getMethodNameToSTElement().get(fullMethodName);
        Set<BTIssue> issueSet = new HashSet<>();
        if (elements != null) {
            for (com.jetbrains.isaev.issues.StackTraceElement element : elements)
                issueSet.add(element.getException().getIssue());
            PsiElement range;
            if (method.isPhysical()) {
                range = method.getNameIdentifier();
            } else {
                final PsiElement navigationElement = method.getNavigationElement();
                if (navigationElement instanceof PsiNameIdentifierOwner) {
                    range = ((PsiNameIdentifierOwner) navigationElement).getNameIdentifier();
                } else {
                    range = navigationElement;
                }
            }
            if (range == null) range = method;
            return new ReportedExceptionLineMarkerInfo(range, issueSet);
        }
        return null;
    }

    private LineMarkerInfo getLineMarkerInfo(@NotNull PsiMethodCallExpression method) {
        logger.warn("Method call updated: " + method.hashCode());
        return null;
    }

    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
        ApplicationManager.getApplication().assertReadAccessAllowed();

        if (elements.isEmpty() || DumbService.getInstance(elements.get(0).getProject()).isDumb()) {
            return;
        }

        Set<PsiMethod> methods = new HashSet<>();
        for (PsiElement element : elements) {
            if (element instanceof PsiMethod) methods.add((PsiMethod) element);
        }
        if (!methods.isEmpty()) markMethods(methods, result);
    }

    private void markMethods(Set<PsiMethod> methods, Collection<LineMarkerInfo> result) {
        for (PsiMethod method : methods) {

            LineMarkerInfo info = getLineMarkerInfo(method);
            if (info != null) result.add(info);
        }
    }

}
