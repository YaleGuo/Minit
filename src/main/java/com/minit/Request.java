package com.minit;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

public interface Request {
    public Connector getConnector();
    public void setConnector(Connector connector);
    public Context getContext();
    public void setContext(Context context);
    public String getInfo();
    public ServletRequest getRequest();
    public Response getResponse();
    public void setResponse(Response response);
    public Socket getSocket();
    public void setSocket(Socket socket);
    public InputStream getStream();
    public void setStream(InputStream stream);
    public Wrapper getWrapper();
    public void setWrapper(Wrapper wrapper);
    public ServletInputStream createInputStream() throws IOException;
    public void finishRequest() throws IOException;
    public void recycle();
    public void setContentLength(int length);
    public void setContentType(String type);
    public void setProtocol(String protocol);
    public void setRemoteAddr(String remote);
    public void setScheme(String scheme);
    public void setServerPort(int port);
}
