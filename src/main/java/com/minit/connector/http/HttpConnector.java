package com.minit.connector.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import com.minit.Connector;
import com.minit.Container;
import com.minit.Logger;
import com.minit.Request;
import com.minit.Response;
import com.minit.core.StandardContext;
import com.minit.session.StandardSession;
import com.minit.startup.BootStrap;

public class HttpConnector implements Connector,Runnable {
	private String info = "com.minit.connector.http.HttpConnector/0.1";
	int minProcessors = 3;
	int maxProcessors = 10;
	int curProcessors = 0;
	Deque<HttpProcessor> processors = new ArrayDeque<>();
	public static Map<String,HttpSession> sessions = new ConcurrentHashMap<>();
//	public static ClassLoader loader = null;
	Container container = null;
	private String threadName = null;
	
	public void run() {
		System.out.println(Thread.currentThread());

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(BootStrap.PORT, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

//	    try {
//		      // create a URLClassLoader
//		      URL[] urls = new URL[1];
//		      URLStreamHandler streamHandler = null;
//		      File classPath = new File(HttpServer.WEB_ROOT);
//		      String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString() ;
//		      urls[0] = new URL(null, repository, streamHandler);
//		      loader = new URLClassLoader(urls);
//		    }
//		    catch (IOException e) {
//		      System.out.println(e.toString() );
//		    }

        //initialize processors pool
        for (int i=0; i<minProcessors; i++) {
        	HttpProcessor initprocessor = new HttpProcessor(this);
        	initprocessor.start();
        	processors.push(initprocessor);
        }
        curProcessors = minProcessors;
        
        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();

                // Hand this socket off to an appropriate processor
                HttpProcessor processor = createProcessor();
                if (processor == null) {
                    socket.close();
                    continue;
                }
                processor.assign(socket);

                // Close the socket
                //socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void start() {
    	threadName = "HttpConnector[" + BootStrap.PORT + "]";
    	log("httpConnector.starting  " + threadName);
    	
        Thread thread = new Thread(this);
        thread.start ();
    }

    /**
     * Create (or allocate) and return an available processor for use in
     * processing a specific HTTP request, if possible.  If the maximum
     * allowed processors have already been created and are in use, return
     * <code>null</code> instead.
     */
    private HttpProcessor createProcessor() {
        synchronized (processors) {
            if (processors.size() > 0) {
                return ((HttpProcessor) processors.pop());
            }
            if (curProcessors < maxProcessors) {
                return (newProcessor());
            } else {
                return (null);
            }
        }
    }

	private HttpProcessor newProcessor() {
    	HttpProcessor initprocessor = new HttpProcessor(this);
    	initprocessor.start();
    	processors.push(initprocessor);	
    	curProcessors++;
    	
    	log("newProcessor");
    	
    	return ((HttpProcessor) processors.pop());
	}
	
	void recycle(HttpProcessor processor) {
	    processors.push(processor);
	}
	
	
	public static StandardSession createSession() {
        StandardSession session = new StandardSession();

        // Initialize the properties of the new session and return it
        session.setValid(true);
        session.setCreationTime(System.currentTimeMillis());
        String sessionId = generateSessionId();
        session.setId(sessionId);
        
        sessions.put(sessionId, session);
        
        System.out.println("session creation id : " + sessionId);

        return (session);
    }
	
	protected static synchronized String generateSessionId() {
        // Generate a byte array containing a session identifier
        Random random = new Random();
        long seed = System.currentTimeMillis();
        random.setSeed(seed);
        
        byte bytes[] = new byte[16];
        random.nextBytes(bytes);

        // Render the result as a String of hexadecimal digits
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            byte b1 = (byte) ((bytes[i] & 0xf0) >> 4);
            byte b2 = (byte) (bytes[i] & 0x0f);
            if (b1 < 10)
                result.append((char) ('0' + b1));
            else
                result.append((char) ('A' + (b1 - 10)));
            if (b2 < 10)
                result.append((char) ('0' + b2));
            else
                result.append((char) ('A' + (b2 - 10)));
        }
        return (result.toString());
    }

	private void log(String message) {
        Logger logger = container.getLogger();
        String localName = threadName;
        if (localName == null)
            localName = "HttpConnector";
        if (logger != null)
            logger.log(localName + " " + message);
        else
            System.out.println(localName + " " + message);

    }

    private void log(String message, Throwable throwable) {
        Logger logger = container.getLogger();
        String localName = threadName;
        if (localName == null)
            localName = "HttpConnector";
        if (logger != null)
            logger.log(localName + " " + message, throwable);
        else {
            System.out.println(localName + " " + message);
            throwable.printStackTrace(System.out);
        }

    }

    public Container getContainer() {
    	return this.container;
    }
    
	public void setContainer(Container container) {
		// TODO Auto-generated method stub
		this.container = container;
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return this.info;
	}

	@Override
	public String getScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setScheme(String scheme) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Request createRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response createResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

}
