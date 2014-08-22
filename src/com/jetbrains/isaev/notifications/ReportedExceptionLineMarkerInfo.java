package com.jetbrains.isaev.notifications;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.MergeableLineMarkerInfo;
import com.intellij.codeInsight.daemon.impl.MarkerType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorGutterComponentEx;
import com.intellij.openapi.editor.ex.MarkupModelEx;
import com.intellij.openapi.editor.ex.RangeHighlighterEx;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.MarkupEditorFilterFactory;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Function;
import com.intellij.xdebugger.ui.DebuggerColors;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.ui.IconProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;

/**
 * Created by Ilya.Isaev on 05.08.2014.
 */
public class ReportedExceptionLineMarkerInfo extends MergeableLineMarkerInfo<PsiElement> {
    Set<BTIssue> set;
    private static com.intellij.openapi.diagnostic.Logger logger = com.intellij.openapi.diagnostic.Logger.getInstance(ReportedExceptionLineMarkerInfo.class);
    @Nullable
    private RangeHighlighterEx myHighlighter;
    private boolean myDisposed;
    private int currentLine = -1;
    public PsiJavaFile file;

    public ReportedExceptionLineMarkerInfo(PsiElement range, Set<BTIssue> issueSet, PsiJavaFile file) {
        super(
                range,
                range.getTextRange(),
                IconProvider.getIcon(issueSet.size() > 1 ? IconProvider.IconRef.WARN_MULTIPLE : IconProvider.IconRef.WARN),
                Pass.UPDATE_OVERRIDEN_MARKERS,
                getMarkerTooltip(issueSet),
                getNaviHandler(issueSet),
                GutterIconRenderer.Alignment.RIGHT);
        this.set = issueSet;
        this.file = file;
    }

    @Nullable
    @Override
    public GutterIconRenderer createGutterRenderer() {
        return new MyDraggableGutterIconRenderer(this);
    }

    private static GutterIconNavigationHandler<PsiElement> getNaviHandler(final Set<BTIssue> issueSet) {
        return new GutterIconNavigationHandler<PsiElement>() {
            @Override
            public void navigate(MouseEvent e, PsiElement elt) {
                IssuesPopupList list = new IssuesPopupList(issueSet);
                JBPopup popup = JBPopupFactory.getInstance().createListPopupBuilder(list).setCloseOnEnter(false).createPopup();
                list.setContainingPopup(popup);
                popup.show(new RelativePoint(e));
                //System.out.println("Source is: " + comp.);
            }
        };
    }


    private static Function<? super PsiElement, String> getMarkerTooltip(final Set<BTIssue> set) {
        return new Function<PsiElement, String>() {
            @Override
            public String fun(PsiElement psiElement) {
                return set.size() + " issues here";
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

    public void updateUI(int line) {
        if (myDisposed || ApplicationManager.getApplication().isUnitTestMode()) {
            return;
        }
        if (currentLine != line) {
            currentLine = line;
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
            myHighlighter.setGutterIconRenderer(new MyDraggableGutterIconRenderer(this));
            myHighlighter.setEditorFilter(MarkupEditorFilterFactory.createIsNotDiffFilter());
        }
    }


    @Override
    public Icon getCommonIcon(@NotNull List<MergeableLineMarkerInfo> infos) {
        return IconProvider.getIcon(IconProvider.IconRef.JIRA_SMALL);
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

}