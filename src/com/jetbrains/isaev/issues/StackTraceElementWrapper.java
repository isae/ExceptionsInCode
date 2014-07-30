package com.jetbrains.isaev.issues;

import java.io.Serializable;

/**
 * Created by Ilya.Isaev on 30.07.2014.
 */
public class StackTraceElementWrapper implements Serializable {

    private String declaringClass;
    private String methodName;
    private String fileName;
    private int lineNumber;

    public StackTraceElementWrapper(String className, String methodName, String fileName, int lineNumber) {
        this.declaringClass = className;
        this.methodName = methodName;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public StackTraceElementWrapper() {
    }

    public static StackTraceElementWrapper wrap(StackTraceElement element) {
        return new StackTraceElementWrapper(element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber());
    }

    public String getDeclaringClass() {
        return declaringClass;
    }

    public void setDeclaringClass(String declaringClass) {
        this.declaringClass = declaringClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
