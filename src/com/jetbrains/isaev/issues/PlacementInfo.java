package com.jetbrains.isaev.issues;

/**
 * Created by Ilya.Isaev on 22.08.2014.
 */
public class PlacementInfo {
    private String methodSignature;
    private int relativeLine = 0;

    public PlacementInfo(String methodSignature, int relativeLine) {
        this.methodSignature = methodSignature;
        this.relativeLine = relativeLine;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public int getRelativeLine() {
        return relativeLine;
    }

    public void setRelativeLine(int relativeLine) {
        this.relativeLine = relativeLine;
    }
}
