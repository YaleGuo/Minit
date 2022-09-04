package com.minit.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.minit.Context;
import com.minit.Loader;

final class ApplicationFilterConfig implements FilterConfig {
	
    public ApplicationFilterConfig(Context context, FilterDef filterDef)
        throws ClassCastException, ClassNotFoundException,
               IllegalAccessException, InstantiationException,
               ServletException {
        super();
        this.context = context;
        setFilterDef(filterDef);
    }

    private Context context = null;
    private Filter filter = null;
    private FilterDef filterDef = null;

    public String getFilterName() {
        return (filterDef.getFilterName());
    }

    public String getInitParameter(String name) {
        Map<String,String> map = filterDef.getParameterMap();
        if (map == null)
            return (null);
        else
            return ((String) map.get(name));

    }

    public Enumeration<String> getInitParameterNames() {
        Map<String,String> map = filterDef.getParameterMap();
        if (map == null)
            return Collections.enumeration(new ArrayList<String>());
        else
            return (Collections.enumeration(map.keySet()));

    }

    public ServletContext getServletContext() {
        return (this.context.getServletContext());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("ApplicationFilterConfig[");
        sb.append("name=");
        sb.append(filterDef.getFilterName());
        sb.append(", filterClass=");
        sb.append(filterDef.getFilterClass());
        sb.append("]");
        return (sb.toString());
    }

    Filter getFilter() throws ClassCastException, ClassNotFoundException,
        IllegalAccessException, InstantiationException, ServletException {

        // Return the existing filter instance, if any
        if (this.filter != null)
            return (this.filter);

        // Identify the class loader we will be using
        String filterClass = filterDef.getFilterClass();
        Loader classLoader = null;
        classLoader = context.getLoader();

        ClassLoader oldCtxClassLoader =
            Thread.currentThread().getContextClassLoader();

        // Instantiate a new instance of this filter and return it
        Class clazz = classLoader.getClassLoader().loadClass(filterClass);
        this.filter = (Filter) clazz.newInstance();
        filter.init(this);
        return (this.filter);

    }

    FilterDef getFilterDef() {
        return (this.filterDef);
    }

    void release() {
        if (this.filter != null)
            filter.destroy();
        this.filter = null;
     }

    void setFilterDef(FilterDef filterDef)
        throws ClassCastException, ClassNotFoundException,
               IllegalAccessException, InstantiationException,
               ServletException {
        this.filterDef = filterDef;
        if (filterDef == null) {
            // Release any previously allocated filter instance
            if (this.filter != null)
                this.filter.destroy();
            this.filter = null;
        } else {
            // Allocate a new filter instance
            Filter filter = getFilter();
        }
    }
}