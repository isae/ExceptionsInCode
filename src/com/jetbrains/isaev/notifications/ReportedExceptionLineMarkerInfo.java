package com.jetbrains.isaev.notifications;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.MergeableLineMarkerInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.MarkupModelEx;
import com.intellij.openapi.editor.ex.RangeHighlighterEx;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.MarkupEditorFilterFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Function;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.issues.*;
import com.jetbrains.isaev.issues.StackTraceElement;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.ui.IconProvider;
import com.jetbrains.isaev.utils.LineMarkerUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ilya.Isaev on 05.08.2014.
 */
public class ReportedExceptionLineMarkerInfo extends MergeableLineMarkerInfo<PsiElement> {
    private final HashMap<Integer, BTIssue> issues;
    HashMap<Long, com.jetbrains.isaev.issues.StackTraceElement> stElements;
    private List<BTIssue> issuesToShow;
    private boolean popupListValid = true;
    private int relativePosition;
    private static com.intellij.openapi.diagnostic.Logger logger = Logger.getInstance(ReportedExceptionLineMarkerInfo.class);
    @Nullable
    private RangeHighlighterEx myHighlighter;

    public void addStackTraceElement(StackTraceElement stElement) {
        stElements.put(stElement.getID(), stElement);
        popupListValid = false;
    }

    public static enum Type {
        CLASS,
        METHOD
    }

    private Type type;
    private boolean myDisposed;
    int currentLine = -1;//may be wrong
    private Editor editor;
    public PsiJavaFile file;

    public ReportedExceptionLineMarkerInfo(PsiElement range, int i, HashMap<Long, StackTraceElement> stElements, HashMap<Integer, BTIssue> issues, PsiJavaFile file, Editor editor, Type type) {
        super(
                range,
                range.getTextRange(),
                IconProvider.getIcon(issues.size() > 1 ? IconProvider.IconRef.WARN_MULTIPLE : IconProvider.IconRef.WARN),
                Pass.UPDATE_OVERRIDEN_MARKERS,
                getMarkerTooltip(issues, type),
                getNaviHandler(issues, editor.getContentComponent().getLocationOnScreen()),
                GutterIconRenderer.Alignment.RIGHT);
        this.issues = issues;
        this.file = file;
        this.editor = editor;
        this.type = type;
        this.relativePosition = i;
        this.stElements = stElements;
        MyNavigationHandler handler = (MyNavigationHandler) getNavigationHandler();
        handler.anchor = this;
    }

    static class MyNavigationHandler implements GutterIconNavigationHandler<PsiElement> {

        private final HashMap<Integer, BTIssue> set;
        public ReportedExceptionLineMarkerInfo anchor;
        private Point gutterLocation;

        public MyNavigationHandler(HashMap<Integer, BTIssue> set, Point locationOnScreen) {
            this.set = set;
            this.gutterLocation = locationOnScreen;
        }

        @Override
        public void navigate(MouseEvent e, PsiElement elt) {
            IssuesPopupList list = new IssuesPopupList(anchor);
            JBPopup popup = JBPopupFactory.getInstance().createListPopupBuilder(list).setCancelOnClickOutside(false).setCloseOnEnter(false).createPopup();
            list.setContainingPopup(popup);
            list.addListener();
            Point p = e.getPoint();
            p.x = gutterLocation.x;
            popup.show(new RelativePoint(e.getComponent(), p));

        }
    }

    @Nullable
    @Override
    public GutterIconRenderer createGutterRenderer() {
        return new MyDraggableGutterIconRenderer(this);
    }

    private static GutterIconNavigationHandler<PsiElement> getNaviHandler(final HashMap<Integer, BTIssue> set, Point locationOnScreen) {
        return new MyNavigationHandler(set, locationOnScreen);
    }


    private static Function<? super PsiElement, String> getMarkerTooltip(final HashMap<Integer, BTIssue> set, final Type type) {
        return new Function<PsiElement, String>() {
            @Override
            public String fun(PsiElement psiElement) {
                return set.size() + " issues here ";
            }
        };
    }

    @Override
    public boolean canMergeWith(@NotNull MergeableLineMarkerInfo info) {
        if (!(info instanceof ReportedExceptionLineMarkerInfo)) return false;//true;
        PsiElement otherElement = info.getElement();

        PsiElement myElement = getElement();
        return otherElement != null && myElement != null;
    }

    @Nullable
    private VirtualFile getFile() {
        return file.getVirtualFile();
    }

    private void removeHighlighter() {
        if (myHighlighter != null) {
            myHighlighter.dispose();
            myHighlighter = null;
        }
    }

    @Nullable
    public Document getDocument() {
        VirtualFile file = getFile();
        if (file == null) return null;
        return FileDocumentManager.getInstance().getDocument(file);
    }

    public int getLine() {
        int result = -2;
        if (myHighlighter != null) {
            result = editor.offsetToLogicalPosition(myHighlighter.getAffectedAreaStartOffset()).line;
        } else result = editor.offsetToLogicalPosition(getElement().getTextOffset()).line;
        currentLine = result;
        return currentLine;
    }

    public void updateSTElementsPlacementInfo() {
        int line = getLine();
        for (StackTraceElement element : stElements.values()) {
            PlacementInfo info = element.getPlacementInfo();
            if (type == Type.CLASS && currentLine != line) {
                info.getAbsolute().remove(currentLine);
                info.getAbsolute().add(line);
            }
        }
    }

    public void updateUI(final int line) {
        int initialLine = currentLine;
        currentLine = getLine();
        if (initialLine != -1)
            relativePosition += line - currentLine;
        currentLine = line;
        for (StackTraceElement element : stElements.values()) {
            PlacementInfo info = element.getPlacementInfo();
            if (type == Type.METHOD) {
                String signature = LineMarkerUtils.getMethodSignatureString((com.intellij.psi.PsiMethod) getElement());
                info.getMethods().put(signature, relativePosition);
            } else {
                if (initialLine != -1)
                    info.getAbsolute().remove(initialLine);
                info.getAbsolute().add(currentLine);
            }
        }
        final ReportedExceptionLineMarkerInfo info = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Document document = getDocument();
                if (document == null) {
                    return;
                }
                if (myHighlighter != null) {
                    removeHighlighter();
                }

                MarkupModelEx markupModel;
                markupModel = (MarkupModelEx) DocumentMarkupModel.forDocument(document, GlobalVariables.project, true);
                myHighlighter = markupModel.addPersistentLineHighlighter(line, HighlighterLayer.SYNTAX, null);
                if (myHighlighter == null) {
                    return;
                }
                myHighlighter.setGutterIconRenderer(new MyDraggableGutterIconRenderer(info));
                myHighlighter.setEditorFilter(MarkupEditorFilterFactory.createIsNotDiffFilter());
            }
        });
    }


    @Override
    public Icon getCommonIcon(@NotNull List<MergeableLineMarkerInfo> infos) {
        return IconProvider.getIcon(IconProvider.IconRef.JIRA_SMALL);
    }

    public HashMap<Integer, BTIssue> getIssues() {
        return issues;
    }

    public Editor getEditor() {
        return editor;
    }

    @Override
    public Function<? super PsiElement, String> getCommonTooltip(@NotNull List<MergeableLineMarkerInfo> infos) {
        return new Function<PsiElement, String>() {
            @Override
            public String fun(PsiElement element) {
                return "Multiple method overrides";
            }
        };
    }

    public void updateUI() {
        updateUI(editor.offsetToLogicalPosition(getElement().getTextOffset()).line);
    }
}