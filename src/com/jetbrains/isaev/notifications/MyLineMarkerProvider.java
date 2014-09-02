package com.jetbrains.isaev.notifications;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.impl.IconLineMarkerProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.*;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.jetbrains.isaev.issues.PlacementInfo;
import com.jetbrains.isaev.issues.StackTraceElement;
import com.jetbrains.isaev.state.BTIssue;
import org.jetbrains.annotations.NotNull;

import static com.jetbrains.isaev.utils.LineMarkerUtils.*;
import static com.jetbrains.isaev.utils.LineMarkerUtils.hash;

import java.util.*;

/**
 * User: Xottab
 * Date: 21.07.2014
 */
public class MyLineMarkerProvider extends IconLineMarkerProvider implements DumbAware {
    private static final Logger logger = Logger.getInstance(MyLineMarkerProvider.class);
    private IssuesDAO dao = GlobalVariables.getInstance().dao;
    // private static HashMap<Integer, HashMap<Long, StackTraceElement>> fileToStElement = new HashMap<Integer, HashMap<Long, StackTraceElement>>(30);
    public static HashMap<Integer, HashMap<Integer, ArrayList<ReportedExceptionLineMarkerInfo>>> markerState = new HashMap<Integer, HashMap<Integer, ArrayList<ReportedExceptionLineMarkerInfo>>>(30);
    public static Editor currentEditor = null;

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
        addLineMarkers(elements);
    }

    private void addLineMarkers(List<PsiElement> elements) {
        ApplicationManager.getApplication().assertReadAccessAllowed();
        if (elements.isEmpty() || DumbService.getInstance(elements.get(0).getProject()).isDumb()) {
            return;
        }

        /*class A {

        }*/
        currentEditor = GlobalVariables.getSelectedEditor();
        for (PsiElement element : elements) {
            if (element instanceof PsiMethod) {
                addMethodMarkers((PsiMethod) element);
            }
            if (element instanceof PsiClass)
                addClassMarkers((PsiClass) element);
        }/*
        // now we divided elements to class and methods. Methods may be not only in outer, but in inner and anonymous
        //todo ПОДУМАТЬ ПРО ВЛОЖЕННЫЕ КЛАССЫ!!!!!!!
        if (currentClass != null) {
            if (!markerState.containsKey(hash((PsiJavaFile) currentClass.getContainingFile()))) {
                markerState.put(hash((PsiJavaFile) currentClass.getContainingFile()), new HashMap<Integer, ReportedExceptionLineMarkerInfo>());
                String currentClassName = currentClass.getQualifiedName();
                List<StackTraceElement> elementList = dao.getClassNameToSTElement(currentClassName);
                Set<BTIssue> issues = new HashSet<BTIssue>();
                for (StackTraceElement element : elementList) issues.add(element.getException().getIssue());
                if (!issues.isEmpty()) {
                    // if (!markerState.get(hash((PsiJavaFile) currentClass.getContainingFile())).containsKey(hash(currentClass))) {
                    ReportedExceptionLineMarkerInfo tmp = new ReportedExceptionLineMarkerInfo(currentClass.getNameIdentifier(), issues, (PsiJavaFile) currentClass.getContainingFile(), currentEditor, ReportedExceptionLineMarkerInfo.Type.METHOD);
                    markerState.get(hash((PsiJavaFile) currentClass.getContainingFile())).put(hash(currentClass.getNameIdentifier()), tmp);
                    tmp.updateUI();
                    //  }
                    // result.add(tmp);
                }
                //logger.warn("Class name is: " + currentClassName);
            }
        }
        if (!methods.isEmpty()) markMethods(methods);*/
    }

    private void addMethodMarkers(PsiMethod method) {
        String fullClassName = getDbClassName(method.getContainingClass());
        List<StackTraceElement> elements = dao.getMethodNameToSTElement(fullClassName, method.getName());
        int fileHash = hash((PsiJavaFile) method.getContainingFile());
        //checkMapToMap(fileToStElement, fileHash);
        checkMapToMap(markerState, fileHash);
        HashMap<Integer, HashMap<Long, StackTraceElement>> newMarkersFill = new HashMap<Integer, HashMap<Long, StackTraceElement>>();
        HashMap<Integer, ArrayList<ReportedExceptionLineMarkerInfo>> existingMarkers = markerState.get(fileHash);
        checkMapToList(existingMarkers, hash(method));
        ArrayList<ReportedExceptionLineMarkerInfo> alreadyDrawn = existingMarkers.get(hash(method));
        if (alreadyDrawn.isEmpty()) {
            for (StackTraceElement stElement : elements) {
                if (stElement.getPlacementInfo() == null) {
                    PlacementInfo info = new PlacementInfo(stElement);
                    stElement.setPlacementInfo(info);
                }
                PlacementInfo plInfo = stElement.getPlacementInfo();
                if (stElement.getPlacementInfo().getMethods().isEmpty()) {
                    plInfo.getMethods().put(getMethodSignatureString(method), 0);//todo heuristics
                }
                Integer row = plInfo.getMethods().get(getMethodSignatureString(method));
                if (row != null) {
                    int absoluteRow = getRowByElement(method, currentEditor) + row;
                    checkMapToMap(newMarkersFill, absoluteRow);
                    //todo check if line marker info already exists on this row
                    HashMap<Long, StackTraceElement> info = newMarkersFill.get(absoluteRow);
                    info.put(stElement.getID(), stElement);

                }/*
                for (Integer absoluteRow : plInfo.absolute) {
                    checkMapToMap(newMarkersFill, absoluteRow);
                    //todo check if line marker info already exists on this row
                    HashMap<Long, StackTraceElement> info = newMarkersFill.get(absoluteRow);
                    info.put(stElement.getID(), stElement);
                }*/
            }
            Map<Integer, ReportedExceptionLineMarkerInfo> newMarkers = new HashMap<Integer, ReportedExceptionLineMarkerInfo>();
            for (Map.Entry<Integer, HashMap<Long, StackTraceElement>> entry : newMarkersFill.entrySet()) {
                ReportedExceptionLineMarkerInfo info = new ReportedExceptionLineMarkerInfo(method, entry.getKey() - getRowByElement(method, currentEditor), newMarkersFill.get(entry.getKey()), collectIssues(newMarkersFill.get(entry.getKey())), (PsiJavaFile) method.getContainingFile(), currentEditor, ReportedExceptionLineMarkerInfo.Type.METHOD);
                alreadyDrawn.add(info);
                newMarkers.put(entry.getKey(), info);
            }
            drawMarkers(newMarkers);
        } else {
            Map<Integer, ReportedExceptionLineMarkerInfo> map = new HashMap<Integer, ReportedExceptionLineMarkerInfo>();
            for (ReportedExceptionLineMarkerInfo info : alreadyDrawn) {
                map.put(info.getLine(), info);
            }
            drawMarkers(map);
        }
    }

    private void drawMarkers(Map<Integer, ReportedExceptionLineMarkerInfo> newMarkers) {
        for (Map.Entry<Integer, ReportedExceptionLineMarkerInfo> entry : newMarkers.entrySet()) {
            entry.getValue().updateUI(entry.getKey());
        }
    }

    private void addClassMarkers(PsiClass clazz) {
        PsiClass element = getMostOuterClass(clazz);
        String fullClassName = getDbClassName(element);
        List<StackTraceElement> elements = dao.getClassNameToSTElement(fullClassName);
        int fileHash = hash((PsiJavaFile) element.getContainingFile());
        checkMapToMap(markerState, fileHash);
        HashMap<Integer, HashMap<Long, StackTraceElement>> newMarkersFill = new HashMap<Integer, HashMap<Long, StackTraceElement>>();
        HashMap<Integer, ArrayList<ReportedExceptionLineMarkerInfo>> existingMarkers = markerState.get(fileHash);
        checkMapToList(existingMarkers, hash(element));
        ArrayList<ReportedExceptionLineMarkerInfo> alreadyDrawn = existingMarkers.get(hash(element));
        if (alreadyDrawn.isEmpty()) {
            for (StackTraceElement stElement : elements) {
                if (stElement.getPlacementInfo() == null) {
                    PlacementInfo info = new PlacementInfo(stElement);
                    stElement.setPlacementInfo(info);
                }
                PlacementInfo plInfo = stElement.getPlacementInfo();
                if (plInfo.getAbsolute().isEmpty()) {
                    int row = getRowByElement(element, currentEditor);
                    plInfo.getAbsolute().add(row);
                }
                for (Integer absoluteRow : plInfo.getAbsolute()) {
                    checkMapToMap(newMarkersFill, absoluteRow);
                    //todo check if line marker info already exists on this row
                    HashMap<Long, StackTraceElement> info = newMarkersFill.get(absoluteRow);
                    info.put(stElement.getID(), stElement);
                }
            }
            Map<Integer, ReportedExceptionLineMarkerInfo> newMarkers = new HashMap<Integer, ReportedExceptionLineMarkerInfo>();
            for (Map.Entry<Integer, HashMap<Long, StackTraceElement>> entry : newMarkersFill.entrySet()) {
                ReportedExceptionLineMarkerInfo info = new ReportedExceptionLineMarkerInfo(element, entry.getKey(), newMarkersFill.get(entry.getKey()), collectIssues(newMarkersFill.get(entry.getKey())), (PsiJavaFile) element.getContainingFile(), currentEditor, ReportedExceptionLineMarkerInfo.Type.CLASS);
                newMarkers.put(entry.getKey(), info);
                alreadyDrawn.add(info);
            }
            drawMarkers(newMarkers);
        } else {
            Map<Integer, ReportedExceptionLineMarkerInfo> map = new HashMap<Integer, ReportedExceptionLineMarkerInfo>();
            for (ReportedExceptionLineMarkerInfo info : alreadyDrawn) {
                int line = info.getLine();
                map.put(line, info);
            }
            drawMarkers(map);
        }
    }


    private HashMap<Integer, BTIssue> collectIssues(HashMap<Long, StackTraceElement> elements) {
        HashMap<Integer, BTIssue> result = new HashMap<Integer, BTIssue>();
        for (Map.Entry<Long, StackTraceElement> entry : elements.entrySet()) {
            BTIssue issue = entry.getValue().getIssue();
            if (!result.containsKey(issue.getIssueID())) {
                result.put(issue.getIssueID(), issue);
            }
        }
        return result;
    }

    /*private void addMethodMarkers(PsiMethod method) {
        PsiClass clazz = method.getContainingClass();
        Map<PsiMethodCallExpression, HashSet<BTIssue>> tempResult = new HashMap<PsiMethodCallExpression, HashSet<BTIssue>>();
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
                        checkMapToSet(tempResult, anchor);
                        tempResult.get(anchor).add(element.getException().getIssue());
                        toAdd.put(element, true);
                    }
                }
            }
            HashMap<Integer, BTIssue> tmp = new HashMap<Integer, BTIssue>();
            for (StackTraceElement element : elements) {
                if (toAdd.get(element) == null) {
                    tmp.add(element.getException().getIssue());
                }
            }
            // Pair<Integer, PsiElement> rangePair = getCorrectPsiAnchor(method);
            PsiElement range = method;//rangePair.second;
            int hash = hash(method);//rangePair.first;
            if (!tmp.isEmpty() && !markerState.get(hash((PsiJavaFile) range.getContainingFile())).containsKey(hash)) {
                ReportedExceptionLineMarkerInfo info = new ReportedExceptionLineMarkerInfo(range, tmp, (PsiJavaFile) range.getContainingFile(), currentEditor, ReportedExceptionLineMarkerInfo.Type.METHOD);
                markerState.get(hash((PsiJavaFile) range.getContainingFile())).put(hash, info);
                for (BTIssue issue : tmp)
                    for (ParsedException p : issue.getExceptions().values())
                        for (StackTraceElement el : p.getStacktrace().values()) {
                            if (!el.getPlacementInfo().methods.containsKey(getMethodSignatureString(method))) {
                                el.getPlacementInfo().methods.put(getMethodSignatureString(method), 0);
                            }
                        }
                info.updateUI(currentEditor.offsetToLogicalPosition(range.getTextOffset()).line);
            }
            for (Map.Entry<PsiMethodCallExpression, HashSet<BTIssue>> entry : tempResult.entrySet()) {
                PsiMethodCallExpression el = entry.getKey();
                if (!markerState.get(hash((PsiJavaFile) el.getContainingFile())).containsKey(hash(el))) {
                    ReportedExceptionLineMarkerInfo info = new ReportedExceptionLineMarkerInfo(el, entry.getValue(), (PsiJavaFile) entry.getKey().getContainingFile(), currentEditor, ReportedExceptionLineMarkerInfo.Type.METHOD);
                    markerState.get(hash((PsiJavaFile) el.getContainingFile())).put(hash(el), info);
                    for (BTIssue issue : entry.getValue())
                        for (ParsedException p : issue.getExceptions().values())
                            for (StackTraceElement el2 : p.getStacktrace().values()) {
                                if (!el2.getPlacementInfo().methods.containsKey(getMethodSignatureString(method))) {
                                    el2.getPlacementInfo().methods.put(getMethodSignatureString(method), 0);
                                }
                            }
                    info.updateUI(currentEditor.offsetToLogicalPosition(range.getTextOffset()).line);
                }
            }

            System.out.println("method_____________________");
            for (StackTraceElement element : elements) {
                System.out.println("json: " + element.getWritablePlacementInfo());
            }
            System.out.println("_____________________\n");
        }
    }*/


}
