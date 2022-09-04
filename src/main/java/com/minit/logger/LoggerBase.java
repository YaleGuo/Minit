package com.minit.logger;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import javax.servlet.ServletException;

import com.minit.Container;
import com.minit.Logger;

public abstract class LoggerBase implements Logger {
protected int debug = 0;
protected static final String info = "com.minit.logger.LoggerBase/1.0";
protected int verbosity = ERROR;
public int getDebug() {
    return (this.debug);
}
public void setDebug(int debug) {
    this.debug = debug;
}

public String getInfo() {
    return (info);
}

public int getVerbosity() {
    return (this.verbosity);
}

public void setVerbosity(int verbosity) {
    this.verbosity = verbosity;
}

public void setVerbosityLevel(String verbosity) {
    if ("FATAL".equalsIgnoreCase(verbosity))
        this.verbosity = FATAL;
    else if ("ERROR".equalsIgnoreCase(verbosity))
        this.verbosity = ERROR;
    else if ("WARNING".equalsIgnoreCase(verbosity))
        this.verbosity = WARNING;
    else if ("INFORMATION".equalsIgnoreCase(verbosity))
        this.verbosity = INFORMATION;
    else if ("DEBUG".equalsIgnoreCase(verbosity))
        this.verbosity = DEBUG;
}

public abstract void log(String msg);
public void log(Exception exception, String msg) {
    log(msg, exception);
}
public void log(String msg, Throwable throwable) {
    CharArrayWriter buf = new CharArrayWriter();
    PrintWriter writer = new PrintWriter(buf);
    writer.println(msg);
    throwable.printStackTrace(writer);
    Throwable rootCause = null;
    if  (throwable instanceof ServletException)
        rootCause = ((ServletException) throwable).getRootCause();
    if (rootCause != null) {
        writer.println("----- Root Cause -----");
        rootCause.printStackTrace(writer);
    }
    log(buf.toString());
}
public void log(String message, int verbosity) {
    if (this.verbosity >= verbosity)
        log(message);
}

public void log(String message, Throwable throwable, int verbosity) {
    if (this.verbosity >= verbosity)
        log(message, throwable);
}

}
