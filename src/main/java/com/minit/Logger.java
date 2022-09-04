package com.minit;

public interface Logger {
    public static final int FATAL = Integer.MIN_VALUE;
    public static final int ERROR = 1;
    public static final int WARNING = 2;
    public static final int INFORMATION = 3;
    public static final int DEBUG = 4;

    public String getInfo();
    public int getVerbosity();
    public void setVerbosity(int verbosity);

    public void log(String message);
    public void log(Exception exception, String msg);
    public void log(String message, Throwable throwable);
    public void log(String message, int verbosity);
    public void log(String message, Throwable throwable, int verbosity);
}
