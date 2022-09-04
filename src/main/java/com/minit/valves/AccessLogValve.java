package com.minit.valves;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minit.Request;
import com.minit.Response;
import com.minit.ValveContext;
import com.minit.connector.http.HttpResponseImpl;

public final class AccessLogValve extends ValveBase {

	public static final String COMMON_ALIAS = "common";
    public static final String COMMON_PATTERN = "%h %l %u %t \"%r\" %s %b";
    public static final String COMBINED_ALIAS = "combined";
    public static final String COMBINED_PATTERN = "%h %l %u %t \"%r\" %s %b \"%{Referer}i\" \"%{User-Agent}i\"";
    
public AccessLogValve() {
    super();
    setPattern("common");
}
private String dateStamp = "";
private String directory = "logs";
protected static final String info =
    "com.minit.valves.AccessLogValve/0.1";
protected static final String months[] =
{ "Jan", "Feb", "Mar", "Apr", "May", "Jun",
  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
private boolean common = false;
private boolean combined = false;
private String pattern = null;
private String prefix = "access_log.";
private String suffix = "";
private PrintWriter writer = null;
private DateTimeFormatter dateFormatter = null;
private DateTimeFormatter dayFormatter = null;
private DateTimeFormatter monthFormatter = null;
private DateTimeFormatter yearFormatter = null;
private DateTimeFormatter timeFormatter = null;
private String timeZone = null;
private LocalDate currentDate = null;
private String space = " ";
private long rotationLastChecked = 0L;

public String getDirectory() {
    return (directory);
}

public void setDirectory(String directory) {
    this.directory = directory;
}

public String getInfo() {
    return (this.info);
}

public String getPattern() {
    return (this.pattern);
}

public void setPattern(String pattern) {
    if (pattern == null)
        pattern = "";
    if (pattern.equals(COMMON_ALIAS))
        pattern = COMMON_PATTERN;
    if (pattern.equals(COMBINED_ALIAS))
        pattern = COMBINED_PATTERN;
    this.pattern = pattern;

    if (this.pattern.equals(COMMON_PATTERN))
        common = true;
    else
        common = false;

    if (this.pattern.equals(COMBINED_PATTERN))
        combined = true;
    else
        combined = false;

}

public String getPrefix() {
    return (prefix);
}

public void setPrefix(String prefix) {
    this.prefix = prefix;
}

public String getSuffix() {
    return (suffix);
}

public void setSuffix(String suffix) {
    this.suffix = suffix;
}

public void invoke(Request request, Response response, ValveContext context)
    throws IOException, ServletException {
    // Pass this request on to the next valve in our pipeline
    context.invokeNext(request, response);

    LocalDate date = getDate();
    StringBuffer result = new StringBuffer();

    // Check to see if we should log using the "common" access log pattern
    if (common || combined) {
        String value = null;

        ServletRequest req = request.getRequest();
        HttpServletRequest hreq = null;
        if (req instanceof HttpServletRequest)
            hreq = (HttpServletRequest) req;

        result.append(req.getRemoteAddr());

        result.append(" - ");

        if (hreq != null)
            value = hreq.getRemoteUser();
        if (value == null)
            result.append("- ");
        else {
            result.append(value);
            result.append(space);
        }

        result.append("[");
        result.append(dayFormatter.format(date));            // Day
        result.append('/');
        result.append(lookup(monthFormatter.format(date))); // Month
        result.append('/');
        result.append(yearFormatter.format(date));            // Year
        result.append(':');
        result.append(timeFormatter.format(date));        // Time
        result.append(space);
        result.append(timeZone);                            // Time Zone
        result.append("] \"");

        result.append(hreq.getMethod());
        result.append(space);
        result.append(hreq.getRequestURI());
        if (hreq.getQueryString() != null) {
            result.append('?');
            result.append(hreq.getQueryString());
        }
        result.append(space);
        result.append(hreq.getProtocol());
        result.append("\" ");

        result.append(((HttpResponseImpl) response).getStatus());

        result.append(space);

        int length = response.getContentCount();

        if (length <= 0)
            value = "-";
        else
            value = "" + length;
        result.append(value);

        if (combined) {
            result.append(space);
            result.append("\"");
            String referer = hreq.getHeader("referer");
            if(referer != null)
                result.append(referer);
            else
                result.append("-");
            result.append("\"");

            result.append(space);
            result.append("\"");
            String ua = hreq.getHeader("user-agent");
            if(ua != null)
                result.append(ua);
            else
                result.append("-");
            result.append("\"");
        }

    } else {
        // Generate a message based on the defined pattern
        boolean replace = false;
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            if (replace) {
                result.append(replace(ch, date, request, response));
                replace = false;
            } else if (ch == '%') {
                replace = true;
            } else {
                result.append(ch);
            }
        }
    }
    log(result.toString(), date);

}

private synchronized void close() {

    if (writer == null)
        return;
    writer.flush();
    writer.close();
    writer = null;
    dateStamp = "";

}

public void log(String message, LocalDate date) {
    // Only do a logfile switch check once a second, max.
    long systime = System.currentTimeMillis();
    if ((systime - rotationLastChecked) > 1000) {
        // We need a new currentDate
        currentDate = LocalDate.now();
        rotationLastChecked = systime;

        // Check for a change of date
        String tsDate = dateFormatter.format(currentDate);

        // If the date has changed, switch log files
        if (!dateStamp.equals(tsDate)) {
            synchronized (this) {
                if (!dateStamp.equals(tsDate)) {
                    close();
                    dateStamp = tsDate;
                    open();
                }
            }
        }

    }

    // Log this message
    if (writer != null) {
        writer.println(message);
    }

}

private String lookup(String month) {
    int index;
    try {
        index = Integer.parseInt(month) - 1;
    } catch (Throwable t) {
        index = 0;  // Can not happen, in theory
    }
    return (months[index]);
}

private synchronized void open() {
    // Create the directory if necessary
    File dir = new File(directory);
    if (!dir.isAbsolute())
        dir = new File(System.getProperty("minit.base"), directory);
    dir.mkdirs();

    // Open the current log file
    try {
        String pathname = dir.getAbsolutePath() + File.separator +
            prefix + dateStamp + suffix;
        writer = new PrintWriter(new FileWriter(pathname, true), true);
    } catch (IOException e) {
        writer = null;
    }
}

private String replace(char pattern, LocalDate date, Request request,
                       Response response) {

    String value = null;

    ServletRequest req = request.getRequest();
    HttpServletRequest hreq = null;
    if (req instanceof HttpServletRequest)
        hreq = (HttpServletRequest) req;
    ServletResponse res = response.getResponse();
    HttpServletResponse hres = null;
    if (res instanceof HttpServletResponse)
        hres = (HttpServletResponse) res;

    if (pattern == 'a') {
        value = req.getRemoteAddr();
    } else if (pattern == 'A') {
        value = "127.0.0.1";        // FIXME
    } else if (pattern == 'b') {
        int length = response.getContentCount();
        if (length <= 0)
            value = "-";
        else
            value = "" + length;
    } else if (pattern == 'B') {
        value = "" + response.getContentLength();
    } else if (pattern == 'h') {
        value = req.getRemoteHost();
    } else if (pattern == 'H') {
        value = req.getProtocol();
    } else if (pattern == 'l') {
        value = "-";
    } else if (pattern == 'm') {
        if (hreq != null)
            value = hreq.getMethod();
        else
            value = "";
    } else if (pattern == 'p') {
        value = "" + req.getServerPort();
    } else if (pattern == 'q') {
        String query = null;
        if (hreq != null)
            query = hreq.getQueryString();
        if (query != null)
            value = "?" + query;
        else
            value = "";
    } else if (pattern == 'r') {
        StringBuffer sb = new StringBuffer();
        if (hreq != null) {
            sb.append(hreq.getMethod());
            sb.append(space);
            sb.append(hreq.getRequestURI());
            if (hreq.getQueryString() != null) {
                sb.append('?');
                sb.append(hreq.getQueryString());
            }
            sb.append(space);
            sb.append(hreq.getProtocol());
        } else {
            sb.append("- - ");
            sb.append(req.getProtocol());
        }
        value = sb.toString();
    } else if (pattern == 'S') {
        if (hreq != null)
            if (hreq.getSession(false) != null)
                value = hreq.getSession(false).getId();
            else value = "-";
        else
            value = "-";
    } else if (pattern == 's') {
        if (hres != null)
            value = "" + ((HttpResponseImpl) response).getStatus();
        else
            value = "-";
    } else if (pattern == 't') {
        StringBuffer temp = new StringBuffer("[");
        temp.append(dayFormatter.format(date));             // Day
        temp.append('/');
        temp.append(lookup(monthFormatter.format(date)));   // Month
        temp.append('/');
        temp.append(yearFormatter.format(date));            // Year
        temp.append(':');
        temp.append(timeFormatter.format(date));            // Time
        temp.append(' ');
        temp.append(timeZone);                              // Timezone
        temp.append(']');
        value = temp.toString();
    } else if (pattern == 'u') {
        if (hreq != null)
            value = hreq.getRemoteUser();
        if (value == null)
            value = "-";
    } else if (pattern == 'U') {
        if (hreq != null)
            value = hreq.getRequestURI();
        else
            value = "-";
    } else if (pattern == 'v') {
        value = req.getServerName();
    } else {
        value = "???" + pattern + "???";
    }

    if (value == null)
        return ("");
    else
        return (value);

}

private LocalDate getDate() {
    // Only create a new Date once per second, max.
    long systime = System.currentTimeMillis();
    if ((systime - currentDate.getLong(ChronoField.MILLI_OF_SECOND)) > 1000) {
        currentDate = LocalDate.now();
    }

    return currentDate;
}

}