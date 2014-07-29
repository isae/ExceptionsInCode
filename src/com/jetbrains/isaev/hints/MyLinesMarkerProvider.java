package com.jetbrains.isaev.hints;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.impl.IconLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.jetbrains.isaev.ui.IconProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

/**
 * User: Xottab
 * Date: 21.07.2014
 */
public class MyLinesMarkerProvider extends IconLineMarkerProvider {
    Icon icon = IconProvider.getIcon(IconProvider.IconRef.WARN);

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
        for (PsiElement element : elements)
            if (element instanceof PsiLiteralExpression) {
                PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
                String value = String.valueOf(literalExpression.getValue());
                Project project = element.getProject();
                NavigationGutterIconBuilder<PsiElement> builder =
                        NavigationGutterIconBuilder.create(icon).
                                setTargets(element).setPopupTitle("GOVNO Title").
                                setTooltipText("Navigate to a simple property");
                result.add(builder.createLineMarkerInfo(element));
            }
    }
}
