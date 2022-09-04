package test;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class HelloServlet implements Servlet{

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		res.setCharacterEncoding("UTF-8");
		String doc = "<!DOCTYPE html> \n" +
	            "<html>\n" +
	            "<head><meta charset=\"utf-8\"><title>Test</title></head>\n"+
	            "<body bgcolor=\"#f0f0f0\">\n" +
	            "<h1 align=\"center\">" + "Hello World ÄãºÃ" + "</h1>\n";
		System.out.println(doc);
	    res.getWriter().println(doc);
	    //res.getWriter().flush();
	    //res.getWriter().close();
	}

	@Override
	public void destroy() {
	}

	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	@Override
	public String getServletInfo() {
		return null;
	}

	@Override
	public void init(ServletConfig arg0) throws ServletException {
	}
}
