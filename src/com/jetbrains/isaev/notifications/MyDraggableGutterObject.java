package com.jetbrains.isaev.notifications;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.impl.FileStatusMap;
import com.intellij.codeInsight.daemon.impl.SlowLineMarkersPass;
import com.intellij.codeInsight.daemon.impl.SlowLineMarkersPassFactory;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.GutterDraggableObject;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.isaev.GlobalVariables;

import java.awt.*;

/**
 * Created by Ilya.Isaev on 21.08.2014.
 */
public class MyDraggableGutterObject implements GutterDraggableObject {
    private final ReportedExceptionLineMarkerInfo info;
    private static FileStatusMap fileStatus = new FileStatusMap(GlobalVariables.getInstance().getProject());


    public MyDraggableGutterObject(ReportedExceptionLineMarkerInfo info) {
        this.info = info;
    }

    /**
     * Called when the icon is dropped over the specified line.
     *
     * @param line the line over which the icon has been dropped.
     * @param file the DnD target file
     * @return true if the drag and drop operation has completed successfully, false otherwise.
     * @since 10.0.3
     */
    @Override
    public boolean copy(int line, VirtualFile file) {
        info.updateUI(line);

        return true;
    }

    /**
     * Returns the cursor to show when the drag is over the specified line.
     *
     * @param line the line over which the drag is performed.
     * @return the cursor to show.
     */
    @Override
    public Cursor getCursor(int line) {
        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    }
}
