package com.jetbrains.isaev.issues;

import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.ui.ParsedException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ilya.Isaev on 30.07.2014.
 */

public class StackTraceElement {
    @NotNull
    private String declaringClass;
    @NotNull
    private String methodName;
    @NotNull
    //todo some files my have same name!!
    private String fileName;
    private ParsedException exception;
    private StackTraceElement prev;
    private StackTraceElement next;
    private byte order;
    private boolean onPlace = false;
    @Nullable
    private PlacementInfo placementInfo;
    private int lineNumber;
    private long ID;
    private long exceptionID;
    private int issueID;
    private BTIssue issue;
    private static final ObjectMapper mapper = new ObjectMapper();

    public StackTraceElement(long stElementID, int issueID, String declaringClass, String methodName, String fileName, int lineNumber, long exceptionID, byte order, boolean onPlace, String placementJson) {
        this(stElementID, declaringClass, methodName, fileName, lineNumber);
        this.exceptionID = exceptionID;
        this.issueID = issueID;
        this.order = order;
        this.onPlace = onPlace;
        if (placementJson != null && placementJson.length() != 0) {
            try {
                placementInfo = mapper.readValue(placementJson, PlacementInfo.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public byte getOrder() {
        return order;
    }

    public void setOrder(byte order) {
        this.order = order;
    }

    public long getExceptionID() {
        return exceptionID;
    }

    public void setExceptionID(long exceptionID) {
        this.exceptionID = exceptionID;
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
        if (exception == null) exception = GlobalVariables.getInstance().dao.getException(exceptionID);
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

    public boolean isOnPlace() {
        return onPlace;
    }

    public void setOnPlace(boolean onPlace) {
        this.onPlace = onPlace;
    }

    public PlacementInfo getPlacementInfo() {
        return placementInfo;
    }

    public void setPlacementInfo(@Nullable PlacementInfo placementInfo) {
        this.placementInfo = placementInfo;
    }

    public String getWritablePlacementInfo() {
        String result = "";
        if (placementInfo != null)
            try {
                result = mapper.writeValueAsString(placementInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return result;
    }

    public int getIssueID() {
        return issueID;
    }

    public void setIssueID(int issueID) {
        this.issueID = issueID;
    }

    public BTIssue getIssue() {
        if (issue == null) issue = GlobalVariables.getInstance().dao.getIssue(issueID);
        return issue;
    }

    public void setIssue(BTIssue issue) {
        this.issue = issue;
    }
}
