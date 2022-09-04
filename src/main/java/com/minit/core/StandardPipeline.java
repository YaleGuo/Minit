package com.minit.core;

import java.io.IOException;

import javax.servlet.ServletException;

import com.minit.Container;
import com.minit.Logger;
import com.minit.Pipeline;
import com.minit.Request;
import com.minit.Response;
import com.minit.Valve;
import com.minit.ValveContext;

public class StandardPipeline implements Pipeline{
public StandardPipeline() {
    this(null);
}
public StandardPipeline(Container container) {
    super();
    setContainer(container);
}
protected Valve basic = null;
protected Container container = null;
protected int debug = 0;
protected String info = "com.minit.core.StandardPipeline/0.1";

protected Valve valves[] = new Valve[0];
public String getInfo() {
    return (this.info);
}
public Container getContainer() {
    return (this.container);
}

public void setContainer(Container container) {
    this.container = container;
}

public Valve getBasic() {
    return (this.basic);
}

public void setBasic(Valve valve) {
    // Change components if necessary
    Valve oldBasic = this.basic;
    if (oldBasic == valve)
        return;

    // Start the new component if necessary
    if (valve == null)
        return;
    valve.setContainer(container);
    this.basic = valve;

}


public void addValve(Valve valve) {
    // Add this Valve to the set associated with this Pipeline
    synchronized (valves) {
        Valve results[] = new Valve[valves.length +1];
        System.arraycopy(valves, 0, results, 0, valves.length);
        valve.setContainer(container);
        results[valves.length] = valve;
        valves = results;
    }

}


public Valve[] getValves() {
    if (basic == null)
        return (valves);
    synchronized (valves) {
        Valve results[] = new Valve[valves.length + 1];
        System.arraycopy(valves, 0, results, 0, valves.length);
        results[valves.length] = basic;
        return (results);
    }
}

public void invoke(Request request, Response response)
    throws IOException, ServletException {
    System.out.println("StandardPipeline invoke()");

    // Invoke the first Valve in this pipeline for this request
    (new StandardPipelineValveContext()).invokeNext(request, response);

}

public void removeValve(Valve valve) {
    synchronized (valves) {
        // Locate this Valve in our list
        int j = -1;
        for (int i = 0; i < valves.length; i++) {
            if (valve == valves[i]) {
                j = i;
                break;
            }
        }
        if (j < 0)
            return;

        valve.setContainer(null);
        
        // Remove this valve from our list
        Valve results[] = new Valve[valves.length - 1];
        int n = 0;
        for (int i = 0; i < valves.length; i++) {
            if (i == j)
                continue;
            results[n++] = valves[i];
        }
        valves = results;
    }

}

protected void log(String message) {
    Logger logger = null;
    if (container != null)
        logger = container.getLogger();
    if (logger != null)
        logger.log("StandardPipeline[" + container.getName() + "]: " +
                   message);
    else
        System.out.println("StandardPipeline[" + container.getName() +
                           "]: " + message);

}

protected void log(String message, Throwable throwable) {

    Logger logger = null;
    if (container != null)
        logger = container.getLogger();
    if (logger != null)
        logger.log("StandardPipeline[" + container.getName() + "]: " +
                   message, throwable);
    else {
        System.out.println("StandardPipeline[" + container.getName() +
                           "]: " + message);
        throwable.printStackTrace(System.out);
    }

}

protected class StandardPipelineValveContext implements ValveContext {
    protected int stage = 0;
    public String getInfo() {
        return info;
    }
    public void invokeNext(Request request, Response response)
        throws IOException, ServletException {

        System.out.println("StandardPipelineValveContext invokeNext()");

        int subscript = stage;
        stage = stage + 1;

        // Invoke the requested Valve for the current request thread
        if (subscript < valves.length) {
            valves[subscript].invoke(request, response, this);
        } else if ((subscript == valves.length) && (basic != null)) {
            basic.invoke(request, response, this);
        } else {
            throw new ServletException("standardPipeline.noValve");
        }

    }
}
}
