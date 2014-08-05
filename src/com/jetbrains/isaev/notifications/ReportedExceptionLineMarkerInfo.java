package com.jetbrains.isaev.notifications;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.MergeableLineMarkerInfo;
import com.intellij.codeInsight.daemon.impl.MarkerType;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Function;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.ui.IconProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;

/**
 * Created by Ilya.Isaev on 05.08.2014.
 */
public class ReportedExceptionLineMarkerInfo extends MergeableLineMarkerInfo<PsiElement> {
    private static com.intellij.openapi.diagnostic.Logger logger = com.intellij.openapi.diagnostic.Logger.getInstance(ReportedExceptionLineMarkerInfo.class);

    public ReportedExceptionLineMarkerInfo(@NotNull PsiElement element, Function<? super PsiElement, String> tooltip, GutterIconNavigationHandler<PsiElement> handler) {
        super(element, element.getTextRange(), IconProvider.getIcon(IconProvider.IconRef.WARN), Pass.UPDATE_ALL, tooltip, handler, GutterIconRenderer.Alignment.RIGHT);
    }

    public ReportedExceptionLineMarkerInfo(@NotNull PsiElement element, @NotNull TextRange textRange, Icon icon, int updatePass, @Nullable Function<? super PsiElement, String> tooltipProvider, @Nullable GutterIconNavigationHandler<PsiElement> navHandler, GutterIconRenderer.Alignment alignment) {
        super(element, textRange, icon, updatePass, tooltipProvider, navHandler, alignment);
    }

    public ReportedExceptionLineMarkerInfo(@NotNull PsiElement element, @NotNull MarkerType markerType) {
        super(element, element.getTextRange(), IconProvider.getIcon(IconProvider.IconRef.WARN), Pass.UPDATE_ALL, markerType.getTooltip(),
                markerType.getNavigationHandler(), GutterIconRenderer.Alignment.RIGHT);
    }

    public ReportedExceptionLineMarkerInfo(PsiElement range, Set<BTIssue> issueSet) {
        super(range, range.getTextRange(), IconProvider.getIcon(IconProvider.IconRef.WARN), Pass.UPDATE_OVERRIDEN_MARKERS, getMarkerTooltip(issueSet), getNaviHandler(issueSet), GutterIconRenderer.Alignment.RIGHT);
    }

    private static GutterIconNavigationHandler<PsiElement> getNaviHandler(Set<BTIssue> issueSet) {
        return new GutterIconNavigationHandler<PsiElement>() {
            @Override
            public void navigate(MouseEvent e, PsiElement elt) {
                logger.warn("Click at " + e.getX() + " " + e.getY());
                IssuesPopupList list = new IssuesPopupList(issueSet);
                JBPopup popup = JBPopupFactory.getInstance().createListPopupBuilder(list).setCloseOnEnter(false).createPopup();
                list.setContainingPopup(popup);
                popup.show(new RelativePoint(e));
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


    @Override
    public Icon getCommonIcon(@NotNull List<MergeableLineMarkerInfo> infos) {
        return myIcon;
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