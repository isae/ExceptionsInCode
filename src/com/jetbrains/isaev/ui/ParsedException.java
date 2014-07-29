package com.jetbrains.isaev.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * User: Xottab
 * Date: 23.07.2014
 */
public class ParsedException {
    @NotNull
    private String name;
    @Nullable
    private String optionalMessage;
    @NotNull
    private List<StackTraceElement> stacktrace;

    public ParsedException(@NotNull String name, @NotNull List<StackTraceElement> stacktrace) {
        this.name = name;
        this.stacktrace = stacktrace;
    }

    public ParsedException() {
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public List<StackTraceElement> getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(@NotNull List<StackTraceElement> stacktrace) {
        this.stacktrace = stacktrace;
    }

    @Nullable
    public String getOptionalMessage() {
        return optionalMessage;
    }

    public void setOptionalMessage(@Nullable String optionalMessage) {
        this.optionalMessage = optionalMessage;
    }
}

