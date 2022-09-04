package com.minit;

import java.io.IOException;

import javax.servlet.ServletException;

public interface ValveContext {
    public String getInfo();
    public void invokeNext(Request request, Response response) throws IOException, ServletException;
}