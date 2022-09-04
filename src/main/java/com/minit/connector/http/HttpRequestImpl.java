package com.minit.connector.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import com.minit.Connector;
import com.minit.Context;
import com.minit.Request;
import com.minit.Response;
import com.minit.Wrapper;
import com.minit.session.StandardSessionFacade;

public class HttpRequestImpl implements HttpServletRequest, Request{
	private InputStream input;
	private SocketInputStream sis;
	private String uri;
	private String queryString;
	InetAddress address;
	int port;
	private boolean parsed = false;
	protected HashMap<String,String> headers = new HashMap<>();
	protected Map<String,String[]> parameters = new ConcurrentHashMap<>();
	HttpRequestLine requestLine = new HttpRequestLine();
	Cookie[] cookies;
	HttpSession session;
	String sessionid;
	StandardSessionFacade sessionFacade;
	private HttpResponseImpl response;
	String docbase;
	
	public String getDocbase() {
		return docbase;
	}
	public void setDocbase(String docbase) {
		this.docbase = docbase;
	}
	public HttpRequestImpl() {
	}
	public HttpRequestImpl(InputStream input) {
	    this.input = input;
	    this.sis = new SocketInputStream(this.input,2048);
	}
	
	public void setStream(InputStream input){
	    this.input = input;
	    this.sis = new SocketInputStream(this.input,2048);		
	}
	public void setResponse(HttpResponseImpl response) {
		this.response = response;
	}
	public void parse(Socket socket) {
		try {
			parseConnection(socket);
			this.sis.readRequestLine(requestLine);
			parseRequestLine();
            parseHeaders();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseRequestLine() {
		// Parse any query parameters out of the request URI      
		int question = requestLine.indexOf("?");
	    if (question >= 0) {
	        queryString=new String(requestLine.uri, question + 1, requestLine.uriEnd - question - 1);
	        uri = new String(requestLine.uri, 0, question);
	        //handle session
	        String tmp = ";"+DefaultHeaders.JSESSIONID_NAME+"=";
	        int semicolon = uri.indexOf(tmp);
	        if (semicolon >= 0) {
	        	sessionid = uri.substring(semicolon+tmp.length());
	        	uri = uri.substring(0, semicolon);
	        }
	        //get context from uri
	        int contextslash = uri.indexOf("/", 1);
	        if (contextslash != -1) {
		        this.docbase = uri.substring(1, contextslash);
		        uri = uri.substring(contextslash);
	        }
	    } else {
	        queryString = null;
	        uri = new String(requestLine.uri, 0, requestLine.uriEnd);
	        String tmp = ";"+DefaultHeaders.JSESSIONID_NAME+"=";
	        int semicolon = uri.indexOf(tmp);
	        if (semicolon >= 0) {
	        	sessionid = uri.substring(semicolon+tmp.length());
	        	uri = uri.substring(0, semicolon);
	        }
	        //get context from uri
	        int contextslash = uri.indexOf("/", 1);
	        if (contextslash != -1) {
		        this.docbase = uri.substring(1, contextslash);
		        uri = uri.substring(contextslash);
	        }
	    }
	    //parseParameters();
	    System.out.println("uri:"+uri);
	    System.out.println("querystring:"+queryString);		
	}
	
	private void parseConnection(Socket socket){
        address = socket.getInetAddress();
        port = socket.getPort();		
	}
	
	private void parseHeaders() throws IOException,ServletException {
	        while (true) {
	            HttpHeader header = new HttpHeader();

	            // Read the next header
	            sis.readHeader(header);
	            if (header.nameEnd == 0) {
	                if (header.valueEnd == 0) {
	                    return;
	                } else {
	                    throw new ServletException("httpProcessor.parseHeaders.colon");
	                }
	            }

	            String name = new String(header.name,0,header.nameEnd);
	            String value = new String(header.value, 0, header.valueEnd);
	            name = name.toLowerCase();
	            //value = value.toLowerCase();
	            
//	            System.out.println("parseHeaders()  ------- "+name + ":"+value);

	            // Set the corresponding request headers
	            if (name.equals(DefaultHeaders.ACCEPT_LANGUAGE_NAME)) {
	                headers.put(name, value);
	            } else if (name.equals(DefaultHeaders.CONTENT_LENGTH_NAME)) {
	                headers.put(name, value);
	            } else if (name.equals(DefaultHeaders.CONTENT_TYPE_NAME)) {
	                headers.put(name, value);
	              
	            } else if (name.equals(DefaultHeaders.HOST_NAME)) {
	                headers.put(name, value);
	            } else if (name.equals(DefaultHeaders.CONNECTION_NAME)) {
	                headers.put(name, value);
	                if (value.equals("close")) {
	                    response.setHeader("Connection", "close");
	                }
	            } else if (name.equals(DefaultHeaders.TRANSFER_ENCODING_NAME)) {
	                headers.put(name, value);
	            } else if (name.equals(DefaultHeaders.COOKIE_NAME)) {
	                headers.put(name, value);
	                Cookie[] cookiearr = parseCookieHeader (value);
	                this.cookies = cookiearr;
	                //check jsessionid
	                for (int i = 0; i < cookies.length; i++) {
	                    if (cookies[i].getName().equals("jsessionid")){
	                    	this.sessionid=cookies[i].getValue();
	                        //this.session = HttpConnector.sessions.get(this.sessionid);
	                    }
	                }
	            } else {
	                headers.put(name, value);
	            }
	        }

	    }

	public  Cookie[] parseCookieHeader(String header) {
		//System.out.println("parseCookieHeader***********"+header);
	    if ((header == null) || (header.length() < 1) )
	        return (new Cookie[0]);
        ArrayList<Cookie> cookieal = new ArrayList<>();
        while (header.length() > 0) {
            int semicolon = header.indexOf(';');
            if (semicolon < 0)
                semicolon = header.length();
            if (semicolon == 0)
                    break;
            
            String token = header.substring(0, semicolon);
//            System.out.println("parseCookieHeader token ***********"+token);
    	        
            if (semicolon < header.length())
                header = header.substring(semicolon + 1);
            else
                header = "";
            
            try {
                int equals = token.indexOf('=');
                if (equals > 0) {
                    String name = token.substring(0, equals).trim();
                    String value = token.substring(equals+1).trim();
                    //System.out.println("parse headers :" + name + " "+value);
                    cookieal.add(new Cookie(name, value));
                } 
            } catch (Throwable e) {
            }
    	}
	    return ((Cookie[]) cookieal.toArray (new Cookie [cookieal.size()]));
	}	
	  
	protected void parseParameters() {
	    String encoding = getCharacterEncoding();
	    //System.out.println(encoding);
	    if (encoding == null)
	      encoding = "ISO-8859-1";

	    // Parse any parameters specified in the query string
	    String qString = getQueryString();
	    //System.out.println("getQueryString:"+qString);
	    if (qString != null) {
	        byte[] bytes = new byte[qString.length()];
		    try {
		        bytes=qString.getBytes(encoding);
		        parseParameters(this.parameters, bytes, encoding);
		    } catch (UnsupportedEncodingException e) {
		    	e.printStackTrace();;
		    }
	    }

	    // Parse any parameters specified in the input stream
	    String contentType = getContentType();
	    if (contentType == null)
	      contentType = "";
	    int semicolon = contentType.indexOf(';');
	    if (semicolon >= 0) {
	      contentType = contentType.substring(0, semicolon).trim();
	    }
	    else {
	      contentType = contentType.trim();
	    }
	    if ("POST".equals(getMethod()) && (getContentLength() > 0)
	      && "application/x-www-form-urlencoded".equals(contentType)) {
	      try {
	        int max = getContentLength();
	        int len = 0;
	        byte buf[] = new byte[getContentLength()];
	        ServletInputStream is = getInputStream();
	        while (len < max) {
	          int next = is.read(buf, len, max - len);
	          if (next < 0 ) {
	            break;
	          }
	          len += next;
	        }
	        is.close();
	        if (len < max) {
	          throw new RuntimeException("Content length mismatch");
	        }
	        parseParameters(this.parameters, buf, encoding);
	      }
	      catch (UnsupportedEncodingException ue) {
	        ;
	      }
	      catch (IOException e) {
	        throw new RuntimeException("Content read fail");
	      }
	    }
	  }

    private byte convertHexDigit( byte b ) {
        if ((b >= '0') && (b <= '9')) return (byte)(b - '0');
        if ((b >= 'a') && (b <= 'f')) return (byte)(b - 'a' + 10);
        if ((b >= 'A') && (b <= 'F')) return (byte)(b - 'A' + 10);
        return 0;
    }
	public void parseParameters(Map<String,String[]> map, byte[] data, String encoding)
	        throws UnsupportedEncodingException {
		if (parsed) 
			return;
		//System.out.println(data);
	    if (data != null && data.length > 0) {
            int    pos = 0;
            int    ix = 0;
            int    ox = 0;
            String key = null;
            String value = null;
            while (ix < data.length) {
                byte c = data[ix++];
                switch ((char) c) {
                case '&':
                    value = new String(data, 0, ox, encoding);
                    if (key != null) {
                    	putMapEntry(map,key, value);
                        key = null;
                    }
                    ox = 0;
                    break;
                case '=':
                    key = new String(data, 0, ox, encoding);
                    ox = 0;
                    break;
                case '+':
                    data[ox++] = (byte)' ';
                    break;
                case '%':
                    data[ox++] = (byte)((convertHexDigit(data[ix++]) << 4)
                                    + convertHexDigit(data[ix++]));
                    break;
                default:
                    data[ox++] = c;
                }
            }
            //The last value does not end in '&'.  So save it now.
            if (key != null) {
                value = new String(data, 0, ox, encoding);
                putMapEntry(map,key, value);
            }
	    }
	    parsed = true;
	}

	private static void putMapEntry( Map<String,String[]> map, String name, String value) {
        String[] newValues = null;
        String[] oldValues = (String[]) map.get(name);
        if (oldValues == null) {
            newValues = new String[1];
            newValues[0] = value;
        } else {
            newValues = new String[oldValues.length + 1];
            System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
            newValues[oldValues.length] = value;
        }
        map.put(name, newValues);
    }
	
	public String getUri() {
	    return this.uri;
	}

	@Override
	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return headers.get(DefaultHeaders.TRANSFER_ENCODING_NAME);
	}

	@Override
	public int getContentLength() {
		return Integer.parseInt(headers.get(DefaultHeaders.CONTENT_LENGTH_NAME));
	}

	@Override
	public long getContentLengthLong() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getContentType() {
		return headers.get(DefaultHeaders.CONTENT_TYPE_NAME);
	}

	@Override
	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return this.sis;
	}

	@Override
	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<Locale> getLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameter(String name) {
	    parseParameters();
	    String values[] = (String[]) parameters.get(name);
	    if (values != null)
	      return (values[0]);
	    else
	      return (null);	}

	@Override
	public Map<String, String[]> getParameterMap() {
	    parseParameters();
	    return (this.parameters);
	}

	@Override
	public Enumeration<String> getParameterNames() {
	    parseParameters();
	    return (Collections.enumeration(parameters.keySet()));
	}

	@Override
	public String[] getParameterValues(String name) {
	    parseParameters();
	    String values[] = (String[]) parameters.get(name);
	    if (values != null)
	      return (values);
	    else
	      return null;
	}

	@Override
	public String getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRealPath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getServerPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAsyncStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String changeSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cookie[] getCookies() {
		return this.cookies;
	}

	@Override
	public long getDateHeader(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHeader(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> getHeaders(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIntHeader(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMethod() {
		// TODO Auto-generated method stub
		return new String(this.requestLine.method,0,this.requestLine.methodEnd);
	}

	@Override
	public Part getPart(String arg0) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQueryString() {
		// TODO Auto-generated method stub
		return this.queryString;
	}

	@Override
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuffer getRequestURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServletPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession() {
		return this.sessionFacade;
	}

	@Override
	public HttpSession getSession(boolean create) {
		if (sessionFacade != null)
			return sessionFacade;
		if (sessionid != null) {
			System.out.println("get Session id : " + sessionid);
			session = HttpConnector.sessions.get(sessionid);
			if (session != null) {
				sessionFacade = new StandardSessionFacade(session);
				return sessionFacade;
			} else {
				session = HttpConnector.createSession();
				sessionFacade = new StandardSessionFacade(session);
				return sessionFacade;
			}
		} else {
			session = HttpConnector.createSession();
			sessionFacade = new StandardSessionFacade(session);
			sessionid = session.getId();
			return sessionFacade;			
		}
	}
	
	public String getSessionId() {
		return this.sessionid;
	}
	

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void login(String arg0, String arg1) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
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
	public ServletRequest getRequest() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Response getResponse() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setResponse(Response response) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Socket getSocket() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setSocket(Socket socket) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Wrapper getWrapper() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setWrapper(Wrapper wrapper) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ServletInputStream createInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void finishRequest() throws IOException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void recycle() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setContentLength(int length) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setContentType(String type) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setProtocol(String protocol) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setRemoteAddr(String remote) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setScheme(String scheme) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setServerPort(int port) {
		// TODO Auto-generated method stub
		
	}

}
