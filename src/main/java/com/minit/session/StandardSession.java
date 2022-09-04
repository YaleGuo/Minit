package com.minit.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.minit.Session;
import com.minit.SessionEvent;
import com.minit.SessionListener;

public class StandardSession implements HttpSession,Session{
	private transient ArrayList<SessionListener> listeners = new ArrayList<>();
	private String sessionid;
	private long creationTime;
	private boolean valid;
	private Map<String,Object> attributes = new ConcurrentHashMap<>();

	public void addSessionListener(SessionListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }
	public void removeSessionListener(SessionListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
    public void fireSessionEvent(String type, Object data) {
        if (listeners.size() < 1)
            return;
        SessionEvent event = new SessionEvent(this, type, data);
        SessionListener list[] = new SessionListener[0];
        synchronized (listeners) {
            list = (SessionListener[]) listeners.toArray(list);
        }
        for (int i = 0; i < list.length; i++)
            ((SessionListener) list[i]).sessionEvent(event);

    }
	@Override
	public long getCreationTime() {
		// TODO Auto-generated method stub
		return this.creationTime;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.sessionid;
	}

	@Override
	public long getLastAccessedTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxInactiveInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return this.attributes.get(name);
	}

	@Override
	public Object getValue(String name) {
		// TODO Auto-generated method stub
		return this.attributes.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return Collections.enumeration(this.attributes.keySet());
	}

	@Override
	public String[] getValueNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String name, Object value) {
		// TODO Auto-generated method stub
		this.attributes.put(name, value);
	}

	@Override
	public void putValue(String name, Object value) {
		// TODO Auto-generated method stub
		this.attributes.put(name, value);		
	}

	@Override
	public void removeAttribute(String name) {
		// TODO Auto-generated method stub
		this.attributes.remove(name);
	}

	@Override
	public void removeValue(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		this.valid = false;
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setValid(boolean b) {
		this.valid = b;
	}

	public void setCreationTime(long currentTimeMillis) {
		this.creationTime = currentTimeMillis;
		
	}

	public void setId(String sessionId) {
		this.sessionid = sessionId;
        fireSessionEvent(Session.SESSION_CREATED_EVENT, null);
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNew(boolean isNew) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void access() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void expire() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void recycle() {
		// TODO Auto-generated method stub
		
	}

}
