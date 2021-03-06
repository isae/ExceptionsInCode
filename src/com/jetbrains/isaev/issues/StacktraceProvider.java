package com.jetbrains.isaev.issues;

import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.jetbrains.isaev.ui.ParsedException;
import org.apache.velocity.runtime.directive.Parse;

import java.util.*;
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
    // private static final String HEADLINE_PATTERN = "[\\t\\n\\r\\s]+(([A-Za-z_$]+[\\w_$]*\\.)+([A-Za-z_$]+[\\w_$]*))\\s*(:(?![\\d]+\\))([^\\n]+))?[\\t\\n\\r\\s]"; //why so slow???
    private static final String HEADLINE_PATTERN = "(([\\w_$]+\\.)+[\\w_$]+)\\s*(:(?![\\d]+\\))([^\\n]+))?[\\t\\n\\r\\s]";
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
            issuesDAO = GlobalVariables.getInstance().dao;
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


    public Map<Integer, ParsedException> parseAllExceptions(String text) {
        setHeadlineMatcher(text);
        setTraceMatcher(text);
        List<ParsedException> result = new ArrayList<ParsedException>();
        List<Integer> headPositions = new ArrayList<Integer>();
        while (headlineMatcher.find()) {
            headPositions.add(headlineMatcher.start());
            ParsedException tmp = new ParsedException(headlineMatcher.group(1), headlineMatcher.group(4));
            result.add(tmp);
        }
        for (int i = 0; i < headPositions.size(); i++) {
            int next = i == headPositions.size() - 1 ? text.length() : headPositions.get(i + 1);
            Matcher m = traceMatcher.region(headPositions.get(i), next);
            List<StackTraceElement> stackTrace = new ArrayList<StackTraceElement>();
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
                    files = FilenameIndex.getFilesByName(GlobalVariables.getInstance().project, sourceFile, GlobalSearchScope.projectScope(GlobalVariables.getInstance().project));
                } finally {
                    token.finish();
                }
                for (PsiFile file : files)
                    if (file.getName().equals(sourceFile)) {
                        f = true;
                        break;
                    }
                if (f) {
                    className = removeAnonimousMarks(className);
                    StackTraceElement element = new StackTraceElement(className, methodName,
                            sourceFile, lineNum);
                    element.setException(result.get(i));
                    stackTrace.add(element);
                    break;
                }
            }
            if (stackTrace.size() > 1) {
                for (int j = 0; j < stackTrace.size(); i++) {
                    stackTrace.get(j).setOrder((byte) (j + 1));
                }
            }
            if (stackTrace.size() > 0) {
                Map<Integer, StackTraceElement> elementMap = new HashMap<Integer, StackTraceElement>();
                for (StackTraceElement el : stackTrace) {
                    elementMap.put(el.hashCode(), el);
                }
                result.get(i).setStacktrace(elementMap);
            }
        }
        Iterator<ParsedException> iter = result.iterator();
        while (iter.hasNext()) {
            ParsedException tmp = iter.next();
            if (tmp.getStacktrace().size() == 0) {
                iter.remove();
            }
        }
        Map<Integer, ParsedException> finalResult = new HashMap<Integer, ParsedException>();
        for (ParsedException exception : result) finalResult.put(exception.hashCode(), exception);
        return finalResult;
    }

    private String removeAnonimousMarks(String className) {
        StringBuilder tmp = new StringBuilder();
        String[] s = className.split("\\.");
        for (int i = 0; i < s.length - 1; i++) tmp.append(s[i]).append(".");
        tmp.append(s[s.length - 1].split("\\$")[0]);
        return tmp.toString();
    }

    public Map<Integer, ParsedException> parseAllTestExceptions(String text) {
        setHeadlineMatcher(text);
        setTraceMatcher(text);
        List<ParsedException> result = new ArrayList<ParsedException>();
        List<Integer> headPositions = new ArrayList<Integer>();
        while (headlineMatcher.find()) {
            headPositions.add(headlineMatcher.start());
            ParsedException tmp = new ParsedException(headlineMatcher.group(1), headlineMatcher.group(4));
            result.add(tmp);
        }
        for (int i = 0; i < headPositions.size(); i++) {
            int next = i == headPositions.size() - 1 ? text.length() : headPositions.get(i + 1);
            Matcher m = traceMatcher.region(headPositions.get(i), next);
            List<StackTraceElement> stackTrace = new ArrayList<StackTraceElement>();
            String sourceFile = null;
            while (m.find()) {
                String className = m.group(1);
                String methodName = m.group(3);
                sourceFile = m.group(4);
                int lineNum = Integer.parseInt(m.group(5));
                    className = removeAnonimousMarks(className);
                    StackTraceElement element = new StackTraceElement(className, methodName,
                            sourceFile, lineNum);
                    element.setException(result.get(i));
                    stackTrace.add(element);
            }
            if (stackTrace.size() > 1) {
                for (int j = 0; j < stackTrace.size(); i++) {
                    stackTrace.get(j).setOrder((byte) (j + 1));
                }
            }
            if (stackTrace.size() > 0) {
                Map<Integer, StackTraceElement> elementMap = new HashMap<Integer, StackTraceElement>();
                for (StackTraceElement el : stackTrace) {
                    elementMap.put(el.hashCode(), el);
                }
                result.get(i).setStacktrace(elementMap);
            }
        }
        Iterator<ParsedException> iter = result.iterator();
        while (iter.hasNext()) {
            ParsedException tmp = iter.next();
            if (tmp.getStacktrace().size() == 0) {
                iter.remove();
            }
        }
        Map<Integer, ParsedException> finalResult = new HashMap<Integer, ParsedException>();
        for (ParsedException exception : result) finalResult.put(exception.hashCode(), exception);
        return finalResult;
    }

}
