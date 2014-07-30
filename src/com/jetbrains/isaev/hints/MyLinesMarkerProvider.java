package com.jetbrains.isaev.hints;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.impl.IconLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
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
        String currentClass = null;
        for (PsiElement element : elements) {
            if (element instanceof PsiMethod) {
                PsiMethod method = (PsiMethod) element;
                String st = method.getContainingClass().getQualifiedName() == null ? "" : method.getContainingClass().getQualifiedName();
                st += "." + method.getName();
                Project project = element.getProject();
                NavigationGutterIconBuilder<PsiElement> builder =
                        NavigationGutterIconBuilder.create(icon).
                                setTargets(element).setPopupTitle("GOVNO Title").
                                setTooltipText(st);
                result.add(builder.createLineMarkerInfo(method.getNameIdentifier()));
            }
        }
    }
}
