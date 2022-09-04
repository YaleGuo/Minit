package com.minit.connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.minit.connector.http.HttpResponseImpl;

public class HttpResponseFacade implements HttpServletResponse {
	  private HttpServletResponse response;
	  
	  public HttpResponseFacade(HttpResponseImpl response) {
	    this.response = response;
	  }

	  public void addDateHeader(String name, long value) {
	    response.addDateHeader(name, value);
	  }

	  public void addHeader(String name, String value) {
	    response.addHeader(name, value);
	  }

	  public void addIntHeader(String name, int value) {
	    response.addIntHeader(name, value);
	  }

	  public boolean containsHeader(String name) {
	    return response.containsHeader(name);
	  }

	  public String encodeRedirectURL(String url) {
	    return response.encodeRedirectURL(url);
	  }

	  public String encodeRedirectUrl(String url) {
	    return response.encodeRedirectUrl(url);
	  }

	  public String encodeUrl(String url) {
	    return response.encodeUrl(url);
	  }

	  public String encodeURL(String url) {
	    return response.encodeURL(url);
	  }

	  public void flushBuffer() throws IOException {
	     response.flushBuffer();
	  }

	  public int getBufferSize() {
	    return response.getBufferSize();
	  }

	  public String getCharacterEncoding() {
	    return response.getCharacterEncoding();
	  }

	    @Override
	    public String getContentType() {
	        return null;  //To change body of implemented methods use File | Settings | File Templates.
	    }

	    @Override
	    public void setCharacterEncoding(String s) {
	        //To change body of implemented methods use File | Settings | File Templates.
	    }

	    public boolean isCommitted() {
	    return response.isCommitted();
	  }

	  public void reset() {
	    response.reset();
	  }

	  public void resetBuffer() {
	    response.resetBuffer();
	  }

	  public void sendError(int sc) throws IOException {
	    response.sendError(sc);
	  }

	  public void sendError(int sc, String message) throws IOException {
	    response.sendError(sc, message);
	  }

	  public void sendRedirect(String location) throws IOException {
	    response.sendRedirect(location);
	  }

	  public void setBufferSize(int size) {
	    response.setBufferSize(size);
	  }

	  public void setContentLength(int length) {
	    response.setContentLength(length);
	  }

	  public void setContentType(String type) {
	    response.setContentType(type);
	  }

	  public void setDateHeader(String name, long value) {
	    response.setDateHeader(name, value);
	  }

	  public void setHeader(String name, String value) {
	    response.setHeader(name, value);
	  }

	  public void setIntHeader(String name, int value) {
	    response.setIntHeader(name, value);
	  }

	  public void setStatus(int sc) {
	    response.setStatus(sc);
	  }

	  public void setStatus(int sc, String message) {
	    response.setStatus(sc, message);
	  }

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return response.getLocale();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return response.getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		// TODO Auto-generated method stub
		return response.getWriter();
	}

	@Override
	public void setContentLengthLong(long arg0) {
		// TODO Auto-generated method stub
		response.setContentLengthLong(arg0);
	}

	@Override
	public void setLocale(Locale arg0) {
		// TODO Auto-generated method stub
		response.setLocale(arg0);
	}

	@Override
	public void addCookie(Cookie arg0) {
		// TODO Auto-generated method stub
		response.addCookie(arg0);
	}

	@Override
	public String getHeader(String arg0) {
		// TODO Auto-generated method stub
		return response.getHeader(arg0);
	}

	@Override
	public Collection<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return response.getHeaderNames();
	}

	@Override
	public Collection<String> getHeaders(String arg0) {
		// TODO Auto-generated method stub
		return response.getHeaders(arg0);
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return response.getStatus();
	}
	}
