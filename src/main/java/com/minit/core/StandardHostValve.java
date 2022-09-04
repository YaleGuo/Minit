package com.minit.core;

import java.io.IOException;

import javax.servlet.ServletException;

import com.minit.Request;
import com.minit.Response;
import com.minit.ValveContext;
import com.minit.connector.http.HttpRequestImpl;
import com.minit.valves.ValveBase;

public class StandardHostValve extends ValveBase{

	@Override
	public void invoke(Request request, Response response, ValveContext context) throws IOException, ServletException {
	    System.out.println("StandardHostValve invoke()");

		String docbase = ((HttpRequestImpl)request).getDocbase();
		System.out.println("StandardHostValve invoke getdocbase : " + docbase);
		StandardHost host = (StandardHost)getContainer();
		
		StandardContext servletContext = host.getContext(docbase); 
		try {
			servletContext.invoke(request, response);
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
		catch (Throwable e) {
			System.out.println(e.toString());
		}
	}

}
