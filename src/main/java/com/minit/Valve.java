package com.minit;

import java.io.IOException;

import javax.servlet.ServletException;

public interface Valve {
    public String getInfo();
    public Container getContainer();
    public void setContainer(Container container);
    public void invoke(Request request, Response response,ValveContext context)
        throws IOException, ServletException;
}