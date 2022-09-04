package com.minit.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.minit.Container;
import com.minit.ContainerEvent;
import com.minit.ContainerListener;
import com.minit.Context;
import com.minit.Loader;
import com.minit.Logger;
import com.minit.Request;
import com.minit.Response;
import com.minit.SessionEvent;
import com.minit.SessionListener;
import com.minit.Wrapper;
import com.minit.connector.HttpRequestFacade;
import com.minit.connector.HttpResponseFacade;
import com.minit.connector.http.HttpConnector;
import com.minit.connector.http.HttpRequestImpl;
import com.minit.logger.FileLogger;
import com.minit.startup.BootStrap;

public class StandardContext extends ContainerBase implements Context{
	HttpConnector connector = null;
	Map<String,String> servletClsMap = new ConcurrentHashMap<>(); //servletName - ServletClassName
	Map<String,StandardWrapper> servletInstanceMap = new ConcurrentHashMap<>();//servletName - servletWrapper

    private Map<String,ApplicationFilterConfig> filterConfigs = new ConcurrentHashMap<>();
    private Map<String,FilterDef> filterDefs = new ConcurrentHashMap<>();
    private FilterMap filterMaps[] = new FilterMap[0];
    
	private ArrayList<ContainerListenerDef> listenerDefs = new ArrayList<>();
	private ArrayList<ContainerListener> listeners = new ArrayList<>();


	public StandardContext() {
        super();
        pipeline.setBasic(new StandardContextValve());

		log("Container created.");
		
		
	}
	
	
	public void addContainerListener(ContainerListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }
	public void removeContainerListener(ContainerListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
    public void fireContainerEvent(String type, Object data) {
        if (listeners.size() < 1)
            return;
        ContainerEvent event = new ContainerEvent(this, type, data);
        ContainerListener list[] = new ContainerListener[0];
        synchronized (listeners) {
            list = (ContainerListener[]) listeners.toArray(list);
        }
        for (int i = 0; i < list.length; i++)
            ((ContainerListener) list[i]).containerEvent(event);

    }
    public void addListenerDef(ContainerListenerDef listenererDef) {
        synchronized (listenerDefs) {
        	listenerDefs.add(listenererDef);
        }
    }

	public String getInfo() {
		return "Minit Servlet Context, vesion 0.1";
	}

	public HttpConnector getConnector() {
		return connector;
	}
	public void setConnector(HttpConnector connector) {
		this.connector = connector;
	}

	public void invoke(Request request, Response response)
			throws IOException, ServletException {
	    System.out.println("StandardContext invoke()");

		super.invoke(request, response);
	}
	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setDisplayName(String displayName) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getDocBase() {
		return this.docbase;
	}
	@Override
	public void setDocBase(String docBase) {
		this.docbase = docBase;
	}
	@Override
	public String getPath() {
		return this.path;
	}
	@Override
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getSessionTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void setSessionTimeout(int timeout) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getWrapperClass() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setWrapperClass(String wrapperClass) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Wrapper createWrapper() {
		// TODO Auto-generated method stub
		return null;
	}
	public Wrapper getWrapper(String name){
		StandardWrapper servletWrapper = servletInstanceMap.get(name); 
		if ( servletWrapper == null) {
			String servletClassName = this.servletClsMap.get(name);
			servletWrapper = new StandardWrapper(servletClassName,this);
			//servletWrapper.setParent(this);
			
			this.servletInstanceMap.put(name, servletWrapper);
			
		}
		return servletWrapper;
	}
	@Override
	public String findServletMapping(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String[] findServletMappings() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void reload() {
		// TODO Auto-generated method stub
		
	}
    public void addFilterDef(FilterDef filterDef) {
        filterDefs.put(filterDef.getFilterName(), filterDef);
    }

    public void addFilterMap(FilterMap filterMap) {
        // Validate the proposed filter mapping
        String filterName = filterMap.getFilterName();
        String servletName = filterMap.getServletName();
        String urlPattern = filterMap.getURLPattern();
        if (findFilterDef(filterName) == null)
            throw new IllegalArgumentException("standardContext.filterMap.name"+filterName);
        if ((servletName == null) && (urlPattern == null))
            throw new IllegalArgumentException("standardContext.filterMap.either");
        if ((servletName != null) && (urlPattern != null))
            throw new IllegalArgumentException("standardContext.filterMap.either");
        // Because filter-pattern is new in 2.3, no need to adjust
        // for 2.2 backwards compatibility
        if ((urlPattern != null) && !validateURLPattern(urlPattern))
            throw new IllegalArgumentException("standardContext.filterMap.pattern"+urlPattern);

        // Add this filter mapping to our registered set
        synchronized (filterMaps) {
            FilterMap results[] =new FilterMap[filterMaps.length + 1];
            System.arraycopy(filterMaps, 0, results, 0, filterMaps.length);
            results[filterMaps.length] = filterMap;
            filterMaps = results;
        }
    }
    
    public FilterDef findFilterDef(String filterName) {
            return ((FilterDef) filterDefs.get(filterName));
    }
    public FilterDef[] findFilterDefs() {
        synchronized (filterDefs) {
            FilterDef results[] = new FilterDef[filterDefs.size()];
            return ((FilterDef[]) filterDefs.values().toArray(results));
        }
    }
    public FilterMap[] findFilterMaps() {
        return (filterMaps);
    }
    public void removeFilterDef(FilterDef filterDef) {
            filterDefs.remove(filterDef.getFilterName());
    }


    public void removeFilterMap(FilterMap filterMap) {
        synchronized (filterMaps) {
            // Make sure this filter mapping is currently present
            int n = -1;
            for (int i = 0; i < filterMaps.length; i++) {
                if (filterMaps[i] == filterMap) {
                    n = i;
                    break;
                }
            }
            if (n < 0)
                return;

            // Remove the specified filter mapping
            FilterMap results[] = new FilterMap[filterMaps.length - 1];
            System.arraycopy(filterMaps, 0, results, 0, n);
            System.arraycopy(filterMaps, n + 1, results, n,
                             (filterMaps.length - 1) - n);
            filterMaps = results;

        }
    }

    public boolean filterStart() {
    	System.out.println("Context Filter Start..........");
        // Instantiate and record a FilterConfig for each defined filter
        boolean ok = true;
        synchronized (filterConfigs) {
            filterConfigs.clear();
            Iterator<String> names = filterDefs.keySet().iterator();
            while (names.hasNext()) {
                String name = names.next();
                ApplicationFilterConfig filterConfig = null;
                try {
                    filterConfig = new ApplicationFilterConfig
                      (this, (FilterDef) filterDefs.get(name));
                    filterConfigs.put(name, filterConfig);
                } catch (Throwable t) {
                    ok = false;
                }
            }
        }

        return (ok);

    }
    public FilterConfig findFilterConfig(String name) {
            return (filterConfigs.get(name));
    }
    private boolean validateURLPattern(String urlPattern) {
        if (urlPattern == null)
            return (false);
        if (urlPattern.startsWith("*.")) {
            if (urlPattern.indexOf('/') < 0)
                return (true);
            else
                return (false);
        }
        if (urlPattern.startsWith("/"))
            return (true);
        else
            return (false);

    }
    
    public boolean listenerStart() {
    	System.out.println("Context Listener Start..........");
        boolean ok = true;
        synchronized (listeners) {
            listeners.clear();
            Iterator<ContainerListenerDef> defs = listenerDefs.iterator();
            while (defs.hasNext()) {
            	ContainerListenerDef def = defs.next();
                ContainerListener listener = null;
                try {
                    // Identify the class loader we will be using
                    String listenerClass = def.getListenerClass();
                    Loader classLoader = null;
                    classLoader = this.getLoader();

                    ClassLoader oldCtxClassLoader =
                        Thread.currentThread().getContextClassLoader();

                    // Instantiate a new instance of this filter and return it
                    Class<?> clazz = classLoader.getClassLoader().loadClass(listenerClass);
                    listener = (ContainerListener) clazz.newInstance();

                    addContainerListener(listener);
                } catch (Throwable t) {
                	t.printStackTrace();
                    ok = false;
                }
            }
        }

        return (ok);

    }
    
	public void start(){
		fireContainerEvent("Container Started",this);

        Logger logger = new FileLogger();
        setLogger(logger);
        
        //scan web.xml
        String file = System.getProperty("minit.base") + File.separator + 
        		this.docbase + File.separator + "WEB-INF" + File.separator + "web.xml";
        
        SAXReader reader = new SAXReader();
        Document document;
		try {
			document = reader.read(file);
			Element root = document.getRootElement();

			//listeners
			List<Element> listeners = root.elements("listener");
	        for (Element listener : listeners) {
	            Element listenerclass = listener.element("listener-class");
	            String listenerclassname = listenerclass.getText();
	            System.out.println("listenerclassname: " + listenerclassname);

	            //load listeners
	            ContainerListenerDef listenerDef = new ContainerListenerDef();
	            listenerDef.setListenerName(listenerclassname);
	            listenerDef.setListenerClass(listenerclassname);
	            addListenerDef(listenerDef);
	        }
            listenerStart();

			//filters
			List<Element> filters = root.elements("filter");
	        for (Element filter : filters) {
	            Element filetername = filter.element("filter-name");
	            String fileternamestr = filetername.getText();
	            Element fileterclass = filter.element("filter-class");
	            String fileterclassstr = fileterclass.getText();
	            System.out.println("filter " + fileternamestr + fileterclassstr);

	            //load filters
	            FilterDef filterDef = new FilterDef();
	            filterDef.setFilterName(fileternamestr);
	            filterDef.setFilterClass(fileterclassstr);
	            addFilterDef(filterDef);
	        }
            
			//filter mappings
			List<Element> filtermaps = root.elements("filter-mapping");
	        for (Element filtermap : filtermaps) {
	            Element filetername = filtermap.element("filter-name");
	            String fileternamestr = filetername.getText();
	            Element urlpattern = filtermap.element("url-pattern");
	            String urlpatternstr = urlpattern.getText();
	            System.out.println("filter mapping " + fileternamestr + urlpatternstr);

	            FilterMap filterMap = new FilterMap();
	            filterMap.setFilterName(fileternamestr);
	            filterMap.setURLPattern(urlpatternstr);
	            addFilterMap(filterMap);
	        }
	        
	        filterStart();

	        //servlet
			List<Element> servlets = root.elements("servlet");
	        for (Element servlet : servlets) {
	            Element servletname = servlet.element("servlet-name");
	            String servletnamestr = servletname.getText();
	            Element servletclass = servlet.element("servlet-class");
	            String servletclassstr = servletclass.getText();
	            Element loadonstartup = servlet.element("load-on-startup");
	            String loadonstartupstr = null;
	            if (loadonstartup != null) {
	            	loadonstartupstr = loadonstartup.getText();
	            }
	            
	            System.out.println("servlet " + servletnamestr + servletclassstr);

	            this.servletClsMap.put(servletnamestr, servletclassstr);
	            if (loadonstartupstr != null) {
		            getWrapper(servletnamestr);
	            }
	            
	        }

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        System.out.println("Context started.........");
	}

    
}
