package com.minit.core;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;

import com.minit.Container;
import com.minit.Loader;
import com.minit.Logger;
import com.minit.Pipeline;
import com.minit.Request;
import com.minit.Response;
import com.minit.Valve;

public abstract class ContainerBase implements Container,Pipeline {
	protected Map<String,Container> children = new ConcurrentHashMap<>();
	protected Loader loader = null;
	protected String name = null;
	protected Container parent = null;
	protected Logger logger = null;
	protected String path;
	protected String docbase;

	protected Pipeline pipeline = new StandardPipeline(this);
	
    public Pipeline getPipeline() {
        return (this.pipeline);
    }
    public void invoke(Request request, Response response) throws IOException, ServletException {
        System.out.println("ContainerBase invoke()");

            pipeline.invoke(request, response);
    }
    public synchronized void addValve(Valve valve) {
        pipeline.addValve(valve);
    }
    public Valve getBasic() {
        return (pipeline.getBasic());
    }
    public Valve[] getValves() {
        return (pipeline.getValves());
    }
    public synchronized void removeValve(Valve valve) {
        pipeline.removeValve(valve);
    }
    public void setBasic(Valve valve) {
        pipeline.setBasic(valve);
    }

    
	public abstract String getInfo();
	public Loader getLoader() {
		if (loader != null)
			return (loader);
		if (parent != null)
			return (parent.getLoader());
		return (null);
	}
	public synchronized void setLoader(Loader loader) {
		loader.setPath(path);
		loader.setDocbase(docbase);
		loader.setContainer(this);
		Loader oldLoader = this.loader;
		if (oldLoader == loader)
			return;
		this.loader = loader;
	}

	public String getName() {

		return (name);

	}


	public void setName(String name) {
		this.name = name;
	}

	public Container getParent() {
		return (parent);
	}


	public void setParent(Container container) {
		Container oldParent = this.parent;
		this.parent = container;
	}


	public void addChild(Container child) {
		addChildInternal(child);
	}

	private void addChildInternal(Container child) {
		synchronized(children) {
			if (children.get(child.getName()) != null)
				throw new IllegalArgumentException("addChild:  Child name '" +
						child.getName() +
						"' is not unique");
			child.setParent((Container) this);  // May throw IAE
			children.put(child.getName(), child);
		}
	}

	public Container findChild(String name) {
		if (name == null)
			return (null);
		synchronized (children) {       // Required by post-start changes
			return ((Container) children.get(name));
		}

	}


	public Container[] findChildren() {

		synchronized (children) {
			Container results[] = new Container[children.size()];
			return ((Container[]) children.values().toArray(results));
		}

	}

	public void removeChild(Container child) {
		synchronized(children) {
			if (children.get(child.getName()) == null)
				return;
			children.remove(child.getName());
		}
		child.setParent(null);

	}

    public Logger getLogger() {
        if (logger != null)
            return (logger);
        if (parent != null)
            return (parent.getLogger());
        return (null);

    }
    public synchronized void setLogger(Logger logger) {
        // Change components if necessary
        Logger oldLogger = this.logger;
        if (oldLogger == logger)
            return;
        this.logger = logger;

    }

    protected void log(String message) {
        Logger logger = getLogger();
        if (logger != null)
            logger.log(logName() + ": " + message);
        else
            System.out.println(logName() + ": " + message);
    }


    protected void log(String message, Throwable throwable) {
        Logger logger = getLogger();
        if (logger != null)
            logger.log(logName() + ": " + message, throwable);
        else {
            System.out.println(logName() + ": " + message + ": " + throwable);
            throwable.printStackTrace(System.out);
        }

    }

    protected String logName() {
        String className = this.getClass().getName();
        int period = className.lastIndexOf(".");
        if (period >= 0)
            className = className.substring(period + 1);
        return (className + "[" + getName() + "]");
    }
    

}