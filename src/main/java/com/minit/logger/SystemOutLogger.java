package com.minit.logger;

public class SystemOutLogger extends LoggerBase {
protected static final String info =
    "com.minit.logger.SystemOutLogger/1.0";

public void log(String msg) {
    System.out.println(msg);
}

}
