package com.jetbrains.isaev.issues;

import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.jetbrains.isaev.dao.SerializableIssuesDAO;
import com.jetbrains.isaev.ui.ParsedException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Xottab
 * Date: 22.07.2014
 */
public class StacktraceProvider {
    /**
     * <ol>
     * <li>some whitespaces</li>
     * <li>some words, followed by '.' (0 or more), not started from digit, then another such word and 0 or more spaces</li>
     * <li>optional: colon, that must not be followed by ">any number<)" and can be followed by any string before '\n' </li>
     * </ol>
     */
    private static final String HEADLINE_PATTERN = "[\\t\\n\\r\\s]+(([A-Za-z_$]+[\\w_$]*\\.)+([A-Za-z_$]+[\\w_$]*))\\s*(:(?![\\d]+\\))([^\\n]+))?[\\t\\n\\r\\s]";
    /**
     * <ol>
     * <li>at followed by 1 ore more spaces, tabs, and new lines</li>
     * <li>some words, followed by '.' (0 or more), then another word and 0 or more spaces/new lines/carriage returns</li>
     * <li>opening parenthess</li>
     * <li>some words, '.java'</li>
     * <li>:number</li>
     * <li>closing parenthess</li>
     * </ol>
     */
    private static final String TRACE_PATTERN = "at[\\s\\t\\r\\n]+(([\\w_$]+\\.)+[\\w_$]+)\\s*\\.([\\w$_]+)[\\s\\n\\r]*\\(([\\w\\d$_]+\\.java):(\\d+)\\)"; //слабоумие и отвага
    private static Pattern headlinePattern;
    private static Pattern tracePattern;
    private static Matcher headlineMatcher;
    private static Matcher traceMatcher;
    private static StacktraceProvider instance;
    private IssuesDAO issuesDAO;

    private StacktraceProvider(boolean f) {
        headlinePattern = Pattern.compile(HEADLINE_PATTERN);
        tracePattern = Pattern.compile(TRACE_PATTERN);
        if (f)
            issuesDAO = SerializableIssuesDAO.getInstance();
    }

    public static StacktraceProvider getInstance() {
        if (instance == null) {
            instance = new StacktraceProvider(true);
        }
        return instance;
    }

    public static StacktraceProvider getTestInstance() {
        if (instance == null) {
            instance = new StacktraceProvider(false);
        }
        return instance;
    }


    public static void setHeadlineMatcher(String s) {
        if (headlineMatcher == null) {
            headlineMatcher = headlinePattern.matcher(s);
        } else {
            headlineMatcher.reset(s);
        }
    }

    public static void setTraceMatcher(String s) {
        if (traceMatcher == null) {
            traceMatcher = tracePattern.matcher(s);
        } else {
            traceMatcher.reset(s);
        }
    }


    public List<ParsedException> parseAllExceptions(String text) {
        setHeadlineMatcher(text);
        setTraceMatcher(text);
        List<ParsedException> result = new ArrayList<>();
        List<Integer> headPositions = new ArrayList<>();
        while (headlineMatcher.find()) {
            headPositions.add(headlineMatcher.start());
            ParsedException tmp = new ParsedException();
            tmp.setName(headlineMatcher.group(1));
            if (headlineMatcher.group(4) != null) {
                tmp.setOptionalMessage(headlineMatcher.group(4));
            }
            result.add(tmp);
        }
        for (int i = 0; i < headPositions.size(); i++) {
            int next = i == headPositions.size() - 1 ? text.length() : headPositions.get(i + 1);
            Matcher m = traceMatcher.region(headPositions.get(i), next);
            List<StackTraceElement> stackTrace = new ArrayList<>();
            String sourceFile = null;
            while (m.find()) {
                String className = m.group(1);
                String methodName = m.group(3);
                sourceFile = m.group(4);
                int lineNum = Integer.parseInt(m.group(5));
                boolean f = false;
                AccessToken token = null;
                PsiFile[] files;
                try {
                    token = ApplicationManager.getApplication().acquireReadActionLock();
                    files = FilenameIndex.getFilesByName(GlobalVariables.project, sourceFile, GlobalSearchScope.projectScope(GlobalVariables.project));
                } finally {
                    token.finish();
                }
                for (PsiFile file : files)
                    if (file.getName().equals(sourceFile)) {
                        f = true;
                        break;
                    }
                if (f) {
                    StackTraceElement element = new StackTraceElement(className, methodName,
                            sourceFile, lineNum);
                    element.setException(result.get(i));
                    stackTrace.add(element);
                    break;
                }
            }

            result.get(i).setStacktrace(stackTrace);
        }
        Iterator<ParsedException> iter = result.iterator();
        while (iter.hasNext()) {
            ParsedException tmp = iter.next();
            if (tmp.getStacktrace().size() == 0) {
                iter.remove();
            }
        }
        return result;
    }

    public List<ParsedException> parseAllTestExceptions(String text) {
        setHeadlineMatcher(text);
        setTraceMatcher(text);
        List<ParsedException> result = new ArrayList<>();
        List<Integer> headPositions = new ArrayList<>();
        while (headlineMatcher.find()) {
            headPositions.add(headlineMatcher.start());
            ParsedException tmp = new ParsedException();
            tmp.setName(headlineMatcher.group(1));
            if (headlineMatcher.group(4) != null) {
                tmp.setOptionalMessage(headlineMatcher.group(4));
            }
            result.add(tmp);
        }
        for (int i = 0; i < headPositions.size(); i++) {
            int next = i == headPositions.size() - 1 ? text.length() : headPositions.get(i + 1);
            Matcher m = traceMatcher.region(headPositions.get(i), next);
            List<StackTraceElement> stackTrace = new ArrayList<>();
            String sourceFile = null;
            while (m.find()) {
                String className = m.group(1);
                String methodName = m.group(3);
                sourceFile = m.group(4);
                int lineNum = Integer.parseInt(m.group(5));
                boolean f = false;
                PsiFile[] files;
                StackTraceElement element = new StackTraceElement(className, methodName,
                        sourceFile, lineNum);
                element.setException(result.get(i));
                stackTrace.add(element);
            }

            result.get(i).setStacktrace(stackTrace);
        }
        Iterator<ParsedException> iter = result.iterator();
        while (iter.hasNext()) {
            ParsedException tmp = iter.next();
            if (tmp.getStacktrace().size() == 0) {
                iter.remove();
            }
        }
        return result;
    }

}
