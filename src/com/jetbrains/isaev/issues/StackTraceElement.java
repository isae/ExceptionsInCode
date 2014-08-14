package com.jetbrains.isaev.issues;

import com.fasterxml.jackson.annotation.*;
import com.j256.ormlite.table.DatabaseTable;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.ui.ParsedException;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Created by Ilya.Isaev on 30.07.2014.
 */
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")

public class StackTraceElement implements Serializable {
    @NotNull
    private String declaringClass;
    @NotNull
    private String methodName;
    @NotNull
    //todo some files my have same name!!
    private String fileName;
    private int lineNumber;
    // @JsonManagedReference(value = "next")
    private StackTraceElement next;
    private long prevID;
    private long nextID;
    //@JsonBackReference(value = "next")
    private StackTraceElement prev;
    // @JsonBackReference(value = "trace")
    private ParsedException exception;
    private long ID;

    public StackTraceElement(long stElementID, String declaringClass, String methodName, String fileName, int lineNumber, long exceptionID) {
        this(stElementID, declaringClass, methodName, fileName, lineNumber);
        this.exceptionID = exceptionID;
    }

    public long getExceptionID() {
        return exceptionID;
    }

    public void setExceptionID(long exceptionID) {
        this.exceptionID = exceptionID;
    }

    private long exceptionID;

    public long getPrevID() {
        return prevID;
    }

    public void setPrevID(long prevID) {
        this.prevID = prevID;
    }

    public long getNextID() {
        return nextID;
    }

    public void setNextID(long nextID) {
        this.nextID = nextID;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public StackTraceElement(@NotNull String className, @NotNull String methodName, @NotNull String fileName, int lineNumber) {
        this.declaringClass = className;
        this.methodName = methodName;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public StackTraceElement(@NotNull String className, @NotNull String methodName, @NotNull String fileName, int lineNumber, long exceptionID) {
        this(className, methodName, fileName, lineNumber);
        this.exceptionID = exceptionID;
    }

    public StackTraceElement(long id, @NotNull String className, @NotNull String methodName, @NotNull String fileName, int lineNumber) {
        this(className, methodName, fileName, lineNumber);
        this.ID = id;
    }

    public String getFullMethodName() {
        return declaringClass + "." + methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StackTraceElement element = (StackTraceElement) o;

        if (lineNumber != element.lineNumber) return false;
        if (!declaringClass.equals(element.declaringClass)) return false;
        if (!fileName.equals(element.fileName)) return false;
        if (!methodName.equals(element.methodName)) return false;

        return true;
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
        if (exception == null) exception = GlobalVariables.dao.getException(exceptionID);
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
