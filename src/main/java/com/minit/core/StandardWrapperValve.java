package com.minit.core;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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

public class StandardWrapperValve extends ValveBase {
	private FilterDef filterDef = null;

	@Override
	public void invoke(Request request, Response response, ValveContext context) throws IOException, ServletException {
		// TODO Auto-generated method stub
		System.out.println("StandardWrapperValve invoke()");
		Servlet instance = ((StandardWrapper)getContainer()).getServlet();

		// Call the filter chain for this request
		// NOTE: This also calls the servlet's service() method
		ApplicationFilterChain filterChain = createFilterChain(request, instance);
		if ((instance != null) && (filterChain != null)) {
			filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
		}
		filterChain.release();
	}

	private ApplicationFilterChain createFilterChain(Request request, Servlet servlet) {
		System.out.println("createFilterChain()");
		if (servlet == null)
			return (null);

		// Create and initialize a filter chain object
		ApplicationFilterChain filterChain = new ApplicationFilterChain();
		filterChain.setServlet(servlet);
		StandardWrapper wrapper = (StandardWrapper) getContainer();

		// Acquire the filter mappings for this Context
		StandardContext context = (StandardContext) wrapper.getParent();
		FilterMap filterMaps[] = context.findFilterMaps();

		// If there are no filter mappings, we are done
		if ((filterMaps == null) || (filterMaps.length == 0))
			return (filterChain);

		// Acquire the information we will need to match filter mappings
		String requestPath = null;
		if (request instanceof HttpServletRequest) {
			//HttpServletRequest hreq =(HttpServletRequest) request.getRequest();
			String contextPath = "";//hreq.getContextPath();
			
			//if (contextPath == null)
			//	contextPath = "";
			String requestURI = ((HttpRequestImpl)request).getUri(); //((HttpServletRequest) request).getRequestURI();
			if (requestURI.length() >= contextPath.length())
				requestPath = requestURI.substring(contextPath.length());
		}
		String servletName = wrapper.getName();
		int n = 0;

		// Add the relevant path-mapped filters to this filter chain
		for (int i = 0; i < filterMaps.length; i++) {
			if (!matchFiltersURL(filterMaps[i], requestPath))
				continue;
			ApplicationFilterConfig filterConfig = (ApplicationFilterConfig)
					context.findFilterConfig(filterMaps[i].getFilterName());
			if (filterConfig == null) {
				;       // FIXME - log configuration problem
				continue;
			}
			filterChain.addFilter(filterConfig);
			n++;
		}

		// Add filters that match on servlet name second
		for (int i = 0; i < filterMaps.length; i++) {
			if (!matchFiltersServlet(filterMaps[i], servletName))
				continue;
			ApplicationFilterConfig filterConfig = (ApplicationFilterConfig)
					context.findFilterConfig(filterMaps[i].getFilterName());
			if (filterConfig == null) {
				;       // FIXME - log configuration problem
				continue;
			}
			filterChain.addFilter(filterConfig);
			n++;
		}

		return (filterChain);

	}

	private boolean matchFiltersURL(FilterMap filterMap, String requestPath) {
		if (requestPath == null)
			return (false);

		// Match on context relative request path
		String testPath = filterMap.getURLPattern();
		if (testPath == null)
			return (false);

		// Case 1 - Exact Match
		if (testPath.equals(requestPath))
			return (true);

		// Case 2 - Path Match ("/.../*")
		if (testPath.equals("/*"))
			return (true);      // Optimize a common case
		if (testPath.endsWith("/*")) {
			String comparePath = requestPath;
			while (true) {
				if (testPath.equals(comparePath + "/*"))
					return (true);
				int slash = comparePath.lastIndexOf('/');
				if (slash < 0)
					break;
				comparePath = comparePath.substring(0, slash);
			}
			return (false);
		}

		// Case 3 - Extension Match
		if (testPath.startsWith("*.")) {
			int slash = requestPath.lastIndexOf('/');
			int period = requestPath.lastIndexOf('.');
			if ((slash >= 0) && (period > slash))
				return (testPath.equals("*." + requestPath.substring(period + 1)));
		}

		// Case 4 - "Default" Match
		return (false); // NOTE - Not relevant for selecting filters
	}

	private boolean matchFiltersServlet(FilterMap filterMap, String servletName) {
		if (servletName == null)
			return (false);
		else
			return (servletName.equals(filterMap.getServletName()));
	}
}
