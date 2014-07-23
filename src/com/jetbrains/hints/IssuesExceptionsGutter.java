package com.jetbrains.hints;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.TextAnnotationGutterProvider;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.jetbrains.tmp.DumbAction;
import com.jetbrains.tmp.DumbAction2;
import com.jetbrains.tmp.TextBoxes;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Xottab
 * Date: 21.07.2014
 */
public class IssuesExceptionsGutter implements TextAnnotationGutterProvider {

    private static final Logger logger = Logger.getInstance(IssuesExceptionsGutter.class);
    private final Map<Integer, Integer> exceptions = new ConcurrentHashMap<>();

    private final List<AnAction> popupActions = new ArrayList<>();

    public IssuesExceptionsGutter() {
        logger.warn("_______________________________________");
        popupActions.add(new DumbAction());
        popupActions.add(new DumbAction2());
        popupActions.add(new TextBoxes());
    }


    @Nullable
    @Override
    public String getLineText(int line, Editor editor) {
        logger.warn("Requested line: " + line);
        Integer num = exceptions.get(line);
        return num != null ? String.valueOf(num) : "";
    }

    @Nullable
    @Override
    public String getToolTip(int line, Editor editor) {
        return "Bazinga!! " + line;
    }

    @Override
    public EditorFontType getStyle(int line, Editor editor) {
        return EditorFontType.BOLD;
    }

    @Nullable
    @Override
    public ColorKey getColor(int line, Editor editor) {
        return ColorKey.createColorKey("black");
    }

    @Nullable
    @Override
    public Color getBgColor(int line, Editor editor) {
        return null;
    }

    @Override
    public List<AnAction> getPopupActions(int line, Editor editor) {
        return popupActions;
    }

    @Override
    public void gutterClosed() {
        exceptions.clear();
    }

    public void putExceptionToRow(int row, int number) {
        exceptions.put(row, number);
    }
}
