package com.minit.loader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import com.minit.Container;
import com.minit.Loader;
import com.minit.startup.BootStrap;

public class WebappLoader implements Loader{
	ClassLoader classLoader;
	ClassLoader parent;
	String path;
	String docbase;
	Container container;

	public WebappLoader(String docbase) {
		this.docbase = docbase;
	  }
	public WebappLoader(String docbase, ClassLoader parent) {
		this.docbase = docbase;
		this.parent = parent;
	  }

	public Container getContainer() {
		return container;
	}
	public void setContainer(Container container) {
		this.container = container;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDocbase() {
		return docbase;
	}
	public void setDocbase(String docbase) {
		this.docbase = docbase;
	}


	  public ClassLoader getClassLoader() {
	    return classLoader;
	  }

	  public String getInfo() {
	    return "A simple loader";
	  }

	  public void addRepository(String repository) {
	  }

	  public String[] findRepositories() {
	    return null;
	  }

	  public synchronized void start() {
	    System.out.println("Starting WebappLoader");
	    try {
			// create a URLClassLoader
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;
			File classPath = new File(System.getProperty("minit.base"));
			String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString() ;
			if (docbase!=null && !docbase.equals("")) {
				repository = repository + docbase + File.separator;
			}
			repository = repository + "WEB-INF"+File.separator+"classes" + File.separator;
			urls[0] = new URL(null, repository, streamHandler);
			System.out.println("WEbapp classloader Repository : "+repository);
			classLoader = new WebappClassLoader(urls,parent);
	    }
	    catch (Exception e) {
	      System.out.println(e.toString() );
	    }
	  }

	  public void stop() {
	  }

	}