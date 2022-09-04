package com.minit;

public interface Connector {
    public Container getContainer();
    public void setContainer(Container container);
    public String getInfo();
    public String getScheme();
    public void setScheme(String scheme);
    public Request createRequest();
    public Response createResponse();
    public void initialize();

}
