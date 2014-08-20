package com.jetbrains.isaev.notifications;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.impl.IconLineMarkerProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.*;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.jetbrains.isaev.issues.StackTraceElement;
import com.jetbrains.isaev.state.BTIssue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: Xottab
 * Date: 21.07.2014
 */
public class MyLineMarkerProvider extends IconLineMarkerProvider implements DumbAware {
    private static final Logger logger = Logger.getInstance(MyLineMarkerProvider.class);
    private IssuesDAO dao = GlobalVariables.dao;

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

    private <T extends PsiElement> List<T> getAllChildByClass(PsiElement element, Class<T> typeToken) {
        List<T> list = new LinkedList<T>();
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

        Set<PsiMethod> methods = new HashSet<PsiMethod>();
        PsiClass currentClass = null;
        for (PsiElement element : elements) {
            if (element instanceof PsiMethod) methods.add((PsiMethod) element);
            if (element instanceof PsiClass) currentClass = (PsiClass) element;
        }
        if (currentClass != null) {
            String currentClassName = currentClass.getQualifiedName();
            List<StackTraceElement> elementList = dao.getClassNameToSTElement(currentClassName);
            Set<BTIssue> issues = new HashSet<BTIssue>();
            for (StackTraceElement element : elementList) issues.add(element.getException().getIssue());
            if (!issues.isEmpty()) {
                LineMarkerInfo tmp = new ReportedExceptionLineMarkerInfo(currentClass, issues);
                result.add(tmp);
            }
            //logger.warn("Class name is: " + currentClassName);
        }
        if (!methods.isEmpty()) markMethods(methods, result);
    }

    private void markMethods(Set<PsiMethod> methods, Collection<LineMarkerInfo> result) {
        for (PsiMethod method : methods) {
            addMethodMarkers(method, result);
        }
    }

    private void checkContainer(Map<PsiElement, HashSet<BTIssue>> target, PsiElement toCheck) {
        if (!target.containsKey(toCheck)) target.put(toCheck, new HashSet<BTIssue>());
    }

    private void addMethodMarkers(PsiMethod method, Collection<LineMarkerInfo> result) {
        PsiClass clazz = method.getContainingClass();
        Map<PsiElement, HashSet<BTIssue>> tempResult = new HashMap<PsiElement, HashSet<BTIssue>>();
        Map<StackTraceElement, Boolean> toAdd = new HashMap<StackTraceElement, Boolean>();
        PsiJavaFile file = (PsiJavaFile) clazz.getContainingFile();
        String className = file.getPackageName() + "." + clazz.getName();
        String methodName = method.getName();
        List<com.jetbrains.isaev.issues.StackTraceElement> elements = dao.getMethodNameToSTElement(className, methodName);
        if (elements != null) {
            for (com.jetbrains.isaev.issues.StackTraceElement element : elements) {
                com.jetbrains.isaev.issues.StackTraceElement prev = element.getPrev();
                if (prev != null) {
                    List<PsiMethodCallExpression> callExpressions = getAllChildByClass(method, PsiMethodCallExpression.class);
                    int count = 0;
                    PsiMethodCallExpression anchor = null;
                    String s2 = prev.getDeclaringClass() + "." + prev.getMethodName();
                    for (PsiMethodCallExpression expr : callExpressions) {
                        String s1 = className + "." + expr.getMethodExpression().getReferenceName();
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
            Set<BTIssue> tmp = new HashSet<BTIssue>();
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
    }

}
