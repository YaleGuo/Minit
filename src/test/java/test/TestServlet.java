package test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.minit.connector.http.DefaultHeaders;
import com.minit.session.StandardSession;

public class TestServlet extends HttpServlet{
	static int count = 0;
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		System.out.println("Another TestServlet Enter doGet()");
		System.out.println("Another TestServlet  parameter name : "+request.getParameter("name"));
		
		TestServlet.count++;
		System.out.println("::::::::Another TestServlet call count ::::::::: " + TestServlet.count);
		//if (TestServlet.count>2) {
		//	response.addHeader("Connection", "close");
		//}
		HttpSession session = request.getSession(true);
		String user = (String) session.getAttribute("user");
		System.out.println("get user from session : " + user);
		if (user == null || user.equals("")) {
			session.setAttribute("user", "yale");
		}	
		
		
		
		response.setCharacterEncoding("UTF-8");
		String doc = "<!DOCTYPE html> \n" +
	            "<html>\n" +
	            "<head><meta charset=\"utf-8\"><title>Test</title></head>\n"+
	            "<body bgcolor=\"#f0f0f0\">\n" +
	            "<h1 align=\"center\">" + "Test ÄãºÃ" + "</h1>\n";
		System.out.println(doc);
	    response.getWriter().println(doc);

	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		System.out.println("Enter doGet()");
		System.out.println("parameter name : "+request.getParameter("name"));
		response.setCharacterEncoding("UTF-8");
		String doc = "<!DOCTYPE html> \n" +
	            "<html>\n" +
	            "<head><meta charset=\"utf-8\"><title>Test</title></head>\n"+
	            "<body bgcolor=\"#f0f0f0\">\n" +
	            "<h1 align=\"center\">" + "Test ÄãºÃ" + "</h1>\n";
		System.out.println(doc);
	    response.getWriter().println(doc);

	}
}
