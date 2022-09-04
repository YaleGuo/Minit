package com.minit.connector.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.minit.Connector;
import com.minit.Context;
import com.minit.Request;
import com.minit.Response;
import com.minit.util.CookieTools;

public class HttpResponseImpl implements HttpServletResponse,Response{
	HttpRequestImpl request;
	OutputStream output;
	PrintWriter writer;
	  
	String contentType = null;
	int contentLength = -1;
	String charset = null;
	String characterEncoding = "UTF-8";
	String protocol = "HTTP/1.1";
	
	Map<String,String> headers = new ConcurrentHashMap<>();
	String message = getStatusMessage(HttpServletResponse.SC_OK);
	int status = HttpServletResponse.SC_OK;

	ArrayList<Cookie> cookies = new ArrayList<>();
	
	public HttpResponseImpl() {
	}
	public HttpResponseImpl(OutputStream output) {
	    this.output = output;
	}
	public void setStream(OutputStream output){
	    this.output = output;		
	}

	public void setRequest(HttpRequestImpl request) {
	    this.request = request;
	}
	
	protected String getStatusMessage(int status) {
	    switch (status) {
	      case SC_OK:
	        return ("OK");
	      case SC_ACCEPTED:
	        return ("Accepted");
	      case SC_BAD_GATEWAY:
	        return ("Bad Gateway");
	      case SC_BAD_REQUEST:
	        return ("Bad Request");
	      case SC_CONTINUE:
	        return ("Continue");
	      case SC_FORBIDDEN:
	        return ("Forbidden");
	      case SC_INTERNAL_SERVER_ERROR:
	        return ("Internal Server Error");
	      case SC_METHOD_NOT_ALLOWED:
	        return ("Method Not Allowed");
	      case SC_NOT_FOUND:
	        return ("Not Found");
	      case SC_NOT_IMPLEMENTED:
	        return ("Not Implemented");
	      case SC_REQUEST_URI_TOO_LONG:
	        return ("Request URI Too Long");
	      case SC_SERVICE_UNAVAILABLE:
	        return ("Service Unavailable");
	      case SC_UNAUTHORIZED:
	        return ("Unauthorized");
	      default:
	        return ("HTTP Response Status " + status);
	    }
	  }

	/**
	 * Send the HTTP response headers, if this has not already occurred.
	 */
	public void sendHeaders() throws IOException {
		//PrintWriter outputWriter = new PrintWriter(new OutputStreamWriter(output,getCharacterEncoding()), true);

		PrintWriter outputWriter = getWriter();

	    // Send the "Status:" header
	    outputWriter.print(this.getProtocol());
	    outputWriter.print(" ");
	    outputWriter.print(status);
	    if (message != null) {
	      outputWriter.print(" ");
	      outputWriter.print(message);
	    }
	    outputWriter.print("\r\n");
	    
	    // Send the content-length and content-type headers (if any)
	    if (getContentType() != null) {
	      outputWriter.print("Content-Type: " + getContentType() + "\r\n");
	    }
	    if (getContentLength() >= 0) {
	      outputWriter.print("Content-Length: " + getContentLength() + "\r\n");
	    }
	    
	    // Send all specified headers (if any)
	    Iterator<String> names = headers.keySet().iterator();
	    while (names.hasNext()) {
	        String name = names.next();
	        String value = headers.get(name);
	        outputWriter.print(name);
	        outputWriter.print(": ");
	        outputWriter.print(value);
	        outputWriter.print("\r\n");
	    }
	      
	    // Add the session ID cookie if necessary
	    HttpSession session = this.request.getSession(false);
	    if (session != null) {
            Cookie cookie = new Cookie(DefaultHeaders.JSESSIONID_NAME,session.getId());
            cookie.setMaxAge(-1);
            addCookie(cookie);
        }

        // Send all specified cookies (if any)
        synchronized (cookies) {
            Iterator<Cookie> items = cookies.iterator();
            while (items.hasNext()) {
                Cookie cookie = (Cookie) items.next();
                outputWriter.print(CookieTools.getCookieHeaderName(cookie)); //set-cookie
                outputWriter.print(": ");
                StringBuffer sbValue = new StringBuffer();
                CookieTools.getCookieHeaderValue(cookie,sbValue); //name=value
                //System.out.println("set cookie jsessionid string : "+sbValue.toString());
                outputWriter.print(sbValue.toString());
                outputWriter.print("\r\n");
            }
        }

      // Send a terminating blank line to mark the end of the headers
	    outputWriter.print("\r\n");
	    outputWriter.flush();
	    //outputWriter.close();
	  }
	
	public void finishResponse() {
		try {
			this.getWriter().flush();
			//this.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		  
	public int getContentLength() {
		// TODO Auto-generated method stub
		return this.contentLength;
	}

	private String getProtocol() {
		// TODO Auto-generated method stub
		return this.protocol;
	}

	public OutputStream getOutput() {
		return this.output;
	}

	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return this.characterEncoding;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return this.contentType;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
	    // autoflush is true, println() will flush,
	    // but print() will not.
		if (writer == null) {
			writer = new PrintWriter(new OutputStreamWriter(output,getCharacterEncoding()), true);
		}
	    return writer;
	}

	@Override
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetBuffer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBufferSize(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		this.characterEncoding = arg0;
	}

	@Override
	public void setContentLength(int length) {
		this.contentLength = length;
	}

	@Override
	public void setContentLengthLong(long length) {
		//this.contentLength = length;
	}

	@Override
	public void setContentType(String arg0) {
		this.contentType = arg0;
	}

	@Override
	public void setLocale(Locale arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCookie(Cookie cookie) {
        synchronized (cookies) {
            Iterator<Cookie> items = cookies.iterator();
            while (items.hasNext()) {
                Cookie tmpcookie = (Cookie) items.next();
                if (tmpcookie.equals(cookie.getName())) {
                	items.remove();
                }
            }
			cookies.add(cookie);
        }

	}

	@Override
	public void addDateHeader(String arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addHeader(String name, String value) {
		headers.put(name, value);
		if (name.toLowerCase()==DefaultHeaders.CONTENT_LENGTH_NAME) { //"content-length"
			setContentLength(Integer.parseInt(value));
		}
		if (name.toLowerCase()==DefaultHeaders.CONTENT_TYPE_NAME) {//"content-type"
			setContentType(value);
		}
	}

	@Override
	public void addIntHeader(String arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsHeader(String name) {
		 return (headers.get(name)!=null);
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeURL(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHeader(String name) {
		// TODO Auto-generated method stub
		return headers.get(name);
	}

	@Override
	public Collection<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return headers.keySet();
	}

	@Override
	public Collection<String> getHeaders(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return this.status;
	}

	@Override
	public void sendError(int arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendError(int arg0, String arg1) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendRedirect(String arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHeader(String name, String value) {
		headers.put(name, value);
		if (name.toLowerCase()==DefaultHeaders.CONTENT_LENGTH_NAME) { //"content-length"
			setContentLength(Integer.parseInt(value));
		}
		if (name.toLowerCase()==DefaultHeaders.CONTENT_TYPE_NAME) {//"content-type"
			setContentType(value);
			
		}
			
	}

	@Override
	public void setIntHeader(String arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStatus(int status) {
		// TODO Auto-generated method stub
		this.status = status;
		this.message = this.getStatusMessage(status);
	}

	@Override
	public void setStatus(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Connector getConnector() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setConnector(Connector connector) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int getContentCount() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setContext(Context context) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Request getRequest() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setRequest(Request request) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ServletResponse getResponse() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public OutputStream getStream() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setError() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isError() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public ServletOutputStream createOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrintWriter getReporter() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void recycle() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sendAcknowledgement() throws IOException {
		// TODO Auto-generated method stub
		
	}

}
