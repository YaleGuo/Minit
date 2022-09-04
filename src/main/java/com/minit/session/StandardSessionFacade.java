package com.minit.session;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class StandardSessionFacade implements HttpSession{
	private HttpSession session;
	
	public StandardSessionFacade(HttpSession session) {
		this.session = session;
	}

	@Override
	public long getCreationTime() {
		// TODO Auto-generated method stub
		return session.getCreationTime();
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return session.getId();
	}

	@Override
	public long getLastAccessedTime() {
		// TODO Auto-generated method stub
		return session.getLastAccessedTime();
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return session.getServletContext();
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		// TODO Auto-generated method stub
		session.setMaxInactiveInterval(interval);
	}

	@Override
	public int getMaxInactiveInterval() {
		// TODO Auto-generated method stub
		return session.getMaxInactiveInterval();
	}

	@Override
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return session.getSessionContext();
	}

	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return session.getAttribute(name);
	}

	@Override
	public Object getValue(String name) {
		// TODO Auto-generated method stub
		return session.getValue(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return session.getAttributeNames();
	}

	@Override
	public String[] getValueNames() {
		// TODO Auto-generated method stub
		return session.getValueNames();
	}

	@Override
	public void setAttribute(String name, Object value) {
		// TODO Auto-generated method stub
		session.setAttribute(name, value);
	}

	@Override
	public void putValue(String name, Object value) {
		// TODO Auto-generated method stub
		session.putValue(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		// TODO Auto-generated method stub
		session.removeAttribute(name);
	}

	@Override
	public void removeValue(String name) {
		// TODO Auto-generated method stub
		session.removeValue(name);
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		session.invalidate();
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return session.isNew();
	}

}
