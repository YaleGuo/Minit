package com.minit.logger;

/**
 * Implementation of <b>Logger</b> that appends log messages to a file
 * named {prefix}.{date}.{suffix} in a configured directory, with an
 * optional preceding timestamp.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.8 $ $Date: 2002/06/09 02:19:43 $
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import com.minit.util.StringManager;

public class FileLogger extends LoggerBase {
private String date = "";
private String directory = "logs";
protected static final String info = "com.minit.logger.FileLogger/0.1";
private String prefix = "minit.";
private StringManager sm = StringManager.getManager(Constants.Package);
private boolean started = false;
private String suffix = ".log";
private boolean timestamp = true;
private PrintWriter writer = null;

public String getDirectory() {
    return (directory);
}

public void setDirectory(String directory) {
    String oldDirectory = this.directory;
    this.directory = directory;
}

public String getPrefix() {
    return (prefix);
}

public void setPrefix(String prefix) {
    String oldPrefix = this.prefix;
    this.prefix = prefix;
}

public String getSuffix() {
    return (suffix);
}

public void setSuffix(String suffix) {
    String oldSuffix = this.suffix;
    this.suffix = suffix;
}

public boolean getTimestamp() {
    return (timestamp);
}

public void setTimestamp(boolean timestamp) {
    boolean oldTimestamp = this.timestamp;
    this.timestamp = timestamp;
}

public void log(String msg) {
    // Construct the timestamp we will use, if requested
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    String tsString = ts.toString().substring(0, 19);
    String tsDate = tsString.substring(0, 10);

    // If the date has changed, switch log files
    if (!date.equals(tsDate)) {
        synchronized (this) {
            if (!date.equals(tsDate)) {
                close();
                date = tsDate;
                open();
            }
        }
    }

    // Log this message, timestamped if necessary
    if (writer != null) {
        if (timestamp) {
            writer.println(tsString + " " + msg);
        } else {
            writer.println(msg);
        }
    }

}

private void close() {
    if (writer == null)
        return;
    writer.flush();
    writer.close();
    writer = null;
    date = "";
}

private void open() {
    File dir = new File(directory);
    if (!dir.isAbsolute())
        dir = new File(System.getProperty("catalina.base"), directory);
    dir.mkdirs();

    // Open the current log file
    try {
        String pathname = dir.getAbsolutePath() + File.separator +
            prefix + date + suffix;
        writer = new PrintWriter(new FileWriter(pathname, true), true);
    } catch (IOException e) {
        writer = null;
    }

}

}