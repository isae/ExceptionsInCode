package com.jetbrains.isaev.notifications;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.impl.IconLineMarkerProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.*;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.jetbrains.isaev.dao.SerializableIssuesDAO;
import com.jetbrains.isaev.issues.StackTraceElement;
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
    private IssuesDAO issuesDAO = SerializableIssuesDAO.getInstance();
    private Map<Integer, Boolean> markersBySignature = new HashMap<>();
    private Map<String, Boolean> markersByMName = new HashMap<>();
    private PsiJavaFile currentlyOpened = null;

    private static int methodHash(@NotNull PsiMethod m) {
        return m.getSignature(PsiSubstitutor.EMPTY).hashCode();
    }

    private static PsiElement getCorrectPsiAnchor(PsiMethod method) {
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
        return range;
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
            PsiElement range = getCorrectPsiAnchor(method);

            return new ReportedExceptionLineMarkerInfo(range, issueSet);
        }
        return null;
    }

    private <T extends PsiElement> List<T> getAllChildByClass(PsiElement element, Class<T> typeToken) {
        List<T> list = new LinkedList<>();
        for (PsiElement elem : element.getChildren()) {
            list.addAll(getAllChildByClass(elem, typeToken));
        }
        if (typeToken.isAssignableFrom(element.getClass())) {
            boolean f = true;
            list.add((T) element);
        }
        return list;
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
        Map<String, PsiMethod> mustBeUpdated = new HashMap<>();
        for (PsiMethod method : methods) {
            addMethodMarkers(method, result, mustBeUpdated);
            //  LineMarkerInfo info = getLineMarkerInfo(method);
            //if (info != null) result.add(info);
        }
    }

    private void checkContainer(Map<PsiElement, HashSet<BTIssue>> target, PsiElement toCheck) {
        if (!target.containsKey(toCheck)) target.put(toCheck, new HashSet<>());
    }

    private void addMethodMarkers(PsiMethod method, Collection<LineMarkerInfo> result, Map<String, PsiMethod> mustBeUpdated) {
        //  if (!mustBeUpdated.containsKey(method.getName())) {
        PsiClass clazz = method.getContainingClass();
        Map<PsiElement, HashSet<BTIssue>> tempResult = new HashMap<>();
        Map<StackTraceElement, Boolean> toAdd = new HashMap<>();
        PsiJavaFile file = (PsiJavaFile) clazz.getContainingFile();
        String fullMethodName = file.getPackageName() + "." + clazz.getName() + "." + method.getName();
        List<com.jetbrains.isaev.issues.StackTraceElement> elements = issuesDAO.getMethodNameToSTElement().get(fullMethodName);
        Set<BTIssue> issueSet = new HashSet<>();
        if (elements != null) {
            for (com.jetbrains.isaev.issues.StackTraceElement element : elements) {
                com.jetbrains.isaev.issues.StackTraceElement prev = element.getPrev();
                issueSet.add(element.getException().getIssue());
                if (prev != null) {
                    List<PsiMethodCallExpression> callExpressions = getAllChildByClass(method, PsiMethodCallExpression.class);
                    int count = 0;
                    PsiMethodCallExpression anchor = null;
                    String s2 = prev.getMethodName();
                    String s3 = element.getMethodName();
                    for (PsiMethodCallExpression expr : callExpressions) {
                        //todo check not only method name but package and class name  too
                        String s1 = expr.getMethodExpression().getReferenceName();
                        if (s1.equals(s2)) {
                            count++;
                            anchor = expr;
                        }
                    }
                    if (count == 1) {
                        checkContainer(tempResult, anchor);
                        tempResult.get(anchor).add(element.getException().getIssue());
                        toAdd.put(element, true);
                    }
                }
            }
            Set<BTIssue> tmp = new HashSet<>();
            for (StackTraceElement element : elements) {
                if (toAdd.get(element) == null) {
                    tmp.add(element.getException().getIssue());
                }
            }
            PsiElement range = getCorrectPsiAnchor(method);
            if (!tmp.isEmpty()) result.add(new ReportedExceptionLineMarkerInfo(range, tmp));
            for (Map.Entry<PsiElement, HashSet<BTIssue>> entry : tempResult.entrySet()) {
                result.add(new ReportedExceptionLineMarkerInfo(entry.getKey(), entry.getValue()));
            }
        }
        // mustBeUpdated.put(method.getName(), method);
        //    }
    }

}
