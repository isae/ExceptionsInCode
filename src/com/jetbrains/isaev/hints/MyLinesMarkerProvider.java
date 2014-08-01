package com.jetbrains.isaev.hints;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.impl.IconLineMarkerProvider;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
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
    private static final Logger logger = Logger.getInstance(MyLinesMarkerProvider.class);
    Icon icon = IconProvider.getIcon(IconProvider.IconRef.WARN);
    Icon icon2 = IconProvider.getIcon(IconProvider.IconRef.YOUTRACK);

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
        /*String currentClass = null;

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
            if (element instanceof PsiMethodCallExpression) {
                PsiMethodCallExpression method = (PsiMethodCallExpression) element;
                if(method.getMethodExpression().getQualifiedName().equals("t")){
                    NavigationGutterIconBuilder<PsiElement> builder =
                            NavigationGutterIconBuilder.create(icon).
                                    setTargets(element).setTooltipText("GOVNO Title");
                    result.add(builder.createLineMarkerInfo(method));
                }
            }
        }*/
       /* logger.warn(String.valueOf(System.currentTimeMillis()));
            logger.warn("\n_____________\n");
            for (PsiElement element : elements) {
                logger.warn(element.getClass().getName());
            }
            logger.warn("\n_____________\n");*/
    }
}
