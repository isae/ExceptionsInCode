package com.jetbrains.issues;

import com.jetbrains.ParsedException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Xottab
 * Date: 22.07.2014
 */
public class StacktraceManager {
    private static final String STACKTRACE_PATTERN = "^.+Exception[^\\n]++(\\s+at .++)+";
    private static final String HEADLINE_PATTERN = "([\\w\\.]+)(:(.*))?";
    private static final String TRACE_PATTERN = "\\s*at\\s+([\\w\\.$_]+)\\.([\\w$_]+)\\((.*java)?:(\\d+)\\)(\\n|\\r\\n)";
    private static Pattern stacktracePattern;
    private static Pattern headlinePattern;
    private static Pattern tracePattern;
    private static Matcher stacktraceMatcher;
    private static Matcher headlineMatcher;
    private static Matcher traceMatcher;
    private static StacktraceManager instance;

    private StacktraceManager() {
        stacktracePattern = Pattern.compile(STACKTRACE_PATTERN, Pattern.MULTILINE);
        headlinePattern = Pattern.compile(HEADLINE_PATTERN);
        tracePattern = Pattern.compile(TRACE_PATTERN);
    }

    public static StacktraceManager getInstance() {
        if (instance == null) {
            instance = new StacktraceManager();
        }
        return instance;
    }

    public static void setStacktraceMatcher(String s) {
        if (stacktraceMatcher == null) {
            stacktraceMatcher = stacktracePattern.matcher(s);
        } else {
            stacktraceMatcher.reset(s);
        }
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

    public boolean containsStackTrace(String s) {
        setStacktraceMatcher(s);
        return stacktraceMatcher.matches();
    }

    private List<String> divideByStacktraces(String text) {
        setStacktraceMatcher(text);
        List<String> result = new ArrayList<>(3);
        while (stacktraceMatcher.find()) {
            result.add(stacktraceMatcher.group());
        }
        return result;
    }

    public ParsedException[] parseAllExceptions(String text) {
        List<String> stacktraces = divideByStacktraces(text);
        ParsedException[] result = new ParsedException[stacktraces.size()];
        int pos = 0;
        for (String trace : stacktraces) {
            ParsedException tmp = new ParsedException();
            setHeadlineMatcher(trace);
            setTraceMatcher(trace);
            if (headlineMatcher.find()) {
                tmp.setName(headlineMatcher.group(1));
                if (headlineMatcher.group(3) != null) {
                    tmp.setOptionalMessage(headlineMatcher.group(3)/*.substring(2)*/);
                }
            }
            // "at package.class.method(source.java:123)"
            List<StackTraceElement> stackTrace = new ArrayList<>();
            while (traceMatcher.find()) {
                String className = traceMatcher.group(1);
                String methodName = traceMatcher.group(2);
                String sourceFile = traceMatcher.group(3);
                int lineNum = Integer.parseInt(traceMatcher.group(4));
                stackTrace.add(new StackTraceElement(className, methodName,
                        sourceFile, lineNum));
            }
            tmp.setStacktrace(stackTrace);
            result[pos++] = tmp;
        }
        return result;
    }

  /*  private static String generate_$() {
        Exception[] exceptions = {new ClassCastException(),
                new NullPointerException(), new IOException("foo")};
        StringWriter writer = new StringWriter();
        for (Exception exception : exceptions) {
            try {
                throw exception;
            } catch (Exception e) {
                e.printStackTrace(new PrintWriter(writer));
                writer.append(" ");
            }
        }
        return writer.getBuffer().toString();
    }


    public static void main(String[] args) {
        StacktraceManager manager = getInstance();
        String examples = generate_$();
        ParsedException[] list = manager.parseAllExceptions(examples);
        boolean f = true;
    }*/
}
