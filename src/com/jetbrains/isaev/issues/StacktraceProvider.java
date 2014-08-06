package com.jetbrains.isaev.issues;

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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Xottab
 * Date: 22.07.2014
 */
public class StacktraceProvider {
    /**
     * <ol>
     * <li>some words, followed by '.' (0 or more), then another word and 0 or more spaces</li>
     * <li>optional: colon, that must not be followed by ">any number<)" and can be followed by any string before '\n' </li>
     * </ol>
     */
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

    private StacktraceProvider() {
        headlinePattern = Pattern.compile(HEADLINE_PATTERN);
        tracePattern = Pattern.compile(TRACE_PATTERN);
        issuesDAO = SerializableIssuesDAO.getInstance();
    }

    public static StacktraceProvider getInstance() {
        if (instance == null) {
            instance = new StacktraceProvider();
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
                PsiFile[] files = FilenameIndex.getFilesByName(GlobalVariables.project, sourceFile, GlobalSearchScope.projectScope(GlobalVariables.project));
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
        for (ParsedException exception : result) {
            List<StackTraceElement> sTrace = exception.getStacktrace();
            for (int i = 0; i < sTrace.size(); i++) {
                StackTraceElement element = sTrace.get(i);
                if (i > 0) element.setPrev(sTrace.get(i - 1));
                if (i < sTrace.size() - 1) element.setNext(sTrace.get(i + 1));
                Map<String, List<StackTraceElement>> cmap = issuesDAO.getClassNameToSTElement();
                Map<String, List<StackTraceElement>> mmap = issuesDAO.getMethodNameToSTElement();
                Map<String, List<StackTraceElement>> fmap = issuesDAO.getFileNameToSTElement();
                if (!cmap.containsKey(element.getDeclaringClass()))
                    cmap.put(element.getDeclaringClass(), new ArrayList<>());
                String tmp = element.getDeclaringClass() + "." + element.getMethodName();
                if (!mmap.containsKey(tmp))
                    mmap.put(tmp, new ArrayList<StackTraceElement>());
                if (!fmap.containsKey(element.getFileName()))
                    fmap.put(element.getFileName(), new ArrayList<StackTraceElement>());
                cmap.get(element.getDeclaringClass()).add(element);
                mmap.get(tmp).add(element);
                fmap.get(element.getFileName()).add(element);
            }
        }
        return result;
    }

}
