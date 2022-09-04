package com.minit.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minit.Request;
import com.minit.Response;
import com.minit.ValveContext;
import com.minit.connector.HttpRequestFacade;
import com.minit.connector.HttpResponseFacade;
import com.minit.connector.http.HttpRequestImpl;
import com.minit.connector.http.HttpResponseImpl;
import com.minit.valves.ValveBase;

final class StandardContextValve extends ValveBase {
private static final String info =
    "org.apache.catalina.core.StandardContextValve/1.0";
public String getInfo() {
    return (info);
}
public void invoke(Request request, Response response, ValveContext valveContext)
    throws IOException, ServletException {

    System.out.println("StandardContextValve invoke()");

	StandardWrapper servletWrapper = null;
	String uri = ((HttpRequestImpl)request).getUri();
	String servletName = uri.substring(uri.lastIndexOf("/") + 1);
	StandardContext context = (StandardContext)getContainer();
	
	servletWrapper = (StandardWrapper)context.getWrapper(servletName); 

	try {
		System.out.println("Call service()");

		servletWrapper.invoke(request, response);
	}
	catch (Exception e) {
		System.out.println(e.toString());
	}
	catch (Throwable e) {
		System.out.println(e.toString());
	}

}


}