package com.jetbrains.isaev.issues;

import com.fasterxml.jackson.annotation.*;
import com.jetbrains.isaev.ui.ParsedException;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Created by Ilya.Isaev on 30.07.2014.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class StackTraceElement implements Serializable {
    @NotNull
    private String declaringClass;
    @NotNull
    private String methodName;
    @NotNull
    //todo some files my have same name!!
    private String fileName;
    private int lineNumber;
    @JsonManagedReference(value = "next")
    private StackTraceElement next;
    @JsonBackReference(value = "next")
    private StackTraceElement prev;
    @JsonBackReference(value = "trace")
    private ParsedException exception;

    public StackTraceElement(@NotNull String className, @NotNull String methodName, @NotNull String fileName, int lineNumber) {
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

    @JsonIgnore
    public String getFullMethodName() {
        return declaringClass + "." + methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StackTraceElement element = (StackTraceElement) o;

        return lineNumber == element.lineNumber && declaringClass.equals(element.declaringClass) && fileName.equals(element.fileName) && methodName.equals(element.methodName);

    }

    @Override
    public int hashCode() {
        int result = declaringClass.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + fileName.hashCode();
        result = 31 * result + lineNumber;
        return result;
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

    @NotNull
    public String getDeclaringClass() {
        return declaringClass;
    }

    public void setDeclaringClass(@NotNull String declaringClass) {
        this.declaringClass = declaringClass;
    }

    @NotNull
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(@NotNull String methodName) {
        this.methodName = methodName;
    }

    @NotNull
    public String getFileName() {
        return fileName;
    }

    public void setFileName(@NotNull String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
