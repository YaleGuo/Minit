package com.minit.connector.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor implements Runnable{
	private Socket socket;
	private boolean available = false;
	private HttpConnector connector;
	//private HttpRequestImpl request;
	//private HttpResponseImpl response;
	private int serverPort = 0;	
    private boolean keepAlive = false;
    private boolean http11 = true;
	
	public HttpProcessor(HttpConnector connector){
		this.connector = connector;
        //this.request = new HttpRequestImpl();
        //this.response = new HttpResponseImpl();
	}
	public void run() {
	    while (true) {
	        // Wait for the next socket to be assigned
	        Socket socket = await();

	        if (socket == null) continue;

	        // Process the request from this socket
	        process(socket);

	        // Finish up this request
	        connector.recycle(this);
	    }
	}
    public void start() {
        Thread thread = new Thread(this);
        thread.start ();
    }
	
	
	public void process(Socket socket) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
            
            keepAlive = true;

            while (keepAlive) {
	            // create Request object and parse
            	HttpRequestImpl request = new HttpRequestImpl(input);
            	//request.setStream(input);
	            request.parse(socket);
	            
	            //handle session
	            if (request.getSessionId()==null || request.getSessionId().equals("")) {
	            	request.getSession(true);
	            }
	
	            // create Response object
	            HttpResponseImpl response = new HttpResponseImpl(output);
	            //response.setStream(output);
	            response.setRequest(request);
	//               response.sendStaticResource();
	            
	            request.setResponse(response);

	    	    try {
	    	    	response.sendHeaders();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

	            // check if this is a request for a servlet or a static resource
	            // a request for a servlet begins with "/servlet/"
	            if (request.getUri().startsWith("/servlet/")) {
	              ServletProcessor processor = new ServletProcessor(this.connector);
	              processor.process(request, response);
		            System.out.println("servlet processor process finished.");
	            }
	            else {
	              StaticResourceProcessor processor = new StaticResourceProcessor();
	              processor.process(request, response);
		            System.out.println("static resource processor process finished.");
	            }
	            
	            System.out.println("processor finish Response().");

	            finishResponse(response);

	            //System.out.println("response header connection------"+response.getHeader("Connection"));
	            //if ( "close".equals(response.getHeader("Connection")) ) {
	                keepAlive = false;
	            //}            
            }

            // Close the socket
            socket.close();
            socket=null;

        } catch (Exception e) {
            e.printStackTrace();
        }

	}
	
	private void finishResponse(HttpResponseImpl response){
	    //try {
			//response.sendHeaders();
			response.finishResponse();
		//} catch (IOException e1) {
		//}		
	}
	
	synchronized void assign(Socket socket) {
//		System.out.println("assign");
//		System.out.println(Thread.currentThread());
		// Wait for the processor to get the previous socket
	    while(available) {
	        try {
	            wait();
	        }catch (InterruptedException e) { }
	    }
	    // Store the newly available Socket and notify our thread
	    this.socket = socket;
	    available = true;
	    notifyAll();
	}
	
	private synchronized Socket await() {
//		System.out.println("await");
//		System.out.println(Thread.currentThread());
	    // Wait for the Connector to provide a new Socket
	    while (!available) { 
	    	try {
	    		wait();
	    	}catch (InterruptedException e) { 
	    	} 
	    }
	    // Notify the Connector that we have received this Socket
	    Socket socket = this.socket;
	    available = false;
	    notifyAll();

	    return (socket);
	}
}
