package com.minit.valves;

import java.io.IOException;

import javax.servlet.ServletException;

import com.minit.Container;
import com.minit.Request;
import com.minit.Response;
import com.minit.Valve;

public abstract class ValveBase implements Valve {
protected Container container = null;
protected int debug = 0;
protected static String info = "com.minit.valves.ValveBase/0.1";

public Container getContainer() {
    return (container);
}

public void setContainer(Container container) {
    this.container = container;
}

public int getDebug() {
    return (this.debug);
}

public void setDebug(int debug) {
    this.debug = debug;
}

public String getInfo() {
    return (info);
}

}