package com.minit.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ContainerListenerDef {
    private String description = null;

    public String getDescription() {
        return (this.description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String displayName = null;

    public String getDisplayName() {
        return (this.displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private String listenerClass = null;

    public String getListenerClass() {
        return (this.listenerClass);
    }

    public void setListenerClass(String listenerClass) {
        this.listenerClass = listenerClass;
    }

    private String listenerName = null;

    public String getListenerName() {
        return (this.listenerName);
    }

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }
    private Map<String,String> parameters = new ConcurrentHashMap<>();

    public Map<String,String> getParameterMap() {
        return (this.parameters);
    }

    public void addInitParameter(String name, String value) {
        parameters.put(name, value);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("ListenerDef[");
        sb.append("listenerName=");
        sb.append(this.listenerName);
        sb.append(", listenerClass=");
        sb.append(this.listenerClass);
        sb.append("]");
        return (sb.toString());
    }

}