package com.jetbrains.isaev.notifications;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.editor.markup.GutterDraggableObject;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.ui.IconProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by Ilya.Isaev on 21.08.2014.
 */
public class MyDraggableGutterIconRenderer extends LineMarkerInfo.LineMarkerGutterIconRenderer {

    public MyDraggableGutterIconRenderer(@NotNull LineMarkerInfo info) {
        super(info);
    }

    @Nullable
    @Override
    public GutterDraggableObject getDraggableObject() {
        return new MyDraggableGutterObject();
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return super.getIcon();
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
