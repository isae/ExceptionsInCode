package com.jetbrains.isaev.issues;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jetbrains.isaev.ui.ParsedException;

import java.io.Serializable;

/**
 * Created by Ilya.Isaev on 30.07.2014.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class StackTraceElement implements Serializable {
    private String declaringClass;
    private String methodName;
    private String fileName;
    private int lineNumber;
    @JsonManagedReference(value = "next")
    private StackTraceElement next;
    @JsonBackReference(value = "next")
    private StackTraceElement prev;
    @JsonBackReference(value = "trace")
    private ParsedException exception;

    public StackTraceElement(String className, String methodName, String fileName, int lineNumber) {
        this.declaringClass = className;
        this.methodName = methodName;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }


    public StackTraceElement() {
    }

    public static StackTraceElement wrap(java.lang.StackTraceElement element) {
        return new StackTraceElement(element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber());
    }

    @Override
    public String toString() {
        return "StackTraceElement{" +
                "declaringClass='" + declaringClass + '\'' +
                ", methodName='" + methodName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", lineNumber=" + lineNumber +
                '}';
    }

    public StackTraceElement getPrev() {
        return prev;
    }

    public void setPrev(StackTraceElement prev) {
        this.prev = prev;
    }

    public StackTraceElement getNext() {
        return next;
    }

    public void setNext(StackTraceElement next) {
        this.next = next;
    }

    public ParsedException getException() {
        return exception;
    }

    public void setException(ParsedException exception) {
        this.exception = exception;
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
