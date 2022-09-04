package com.minit.connector.http;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.text.StrSubstitutor;

import com.minit.Request;
import com.minit.Response;

public class ServletProcessor {
	private HttpConnector connector;
	
	public ServletProcessor(HttpConnector connector){
		this.connector = connector;
	}

    public void process(Request request, Response response) throws IOException, ServletException {
        System.out.println("ServletProcessor process()");

    	this.connector.getContainer().invoke(request, response);
	}
}