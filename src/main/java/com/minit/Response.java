package com.minit;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

public interface Response {
    /**
     * Return the Connector through which this Response is returned.
     */
    public Connector getConnector();


    /**
     * Set the Connector through which this Response is returned.
     *
     * @param connector The new connector
     */
    public void setConnector(Connector connector);


    /**
     * Return the number of bytes actually written to the output stream.
     */
    public int getContentCount();


    /**
     * Return the Context with which this Response is associated.
     */
    public Context getContext();


    /**
     * Set the Context with which this Response is associated.  This should
     * be called as soon as the appropriate Context is identified.
     *
     * @param context The associated Context
     */
    public void setContext(Context context);


    /**
     * Return descriptive information about this Response implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo();


    /**
     * Return the Request with which this Response is associated.
     */
    public Request getRequest();


    /**
     * Set the Request with which this Response is associated.
     *
     * @param request The new associated request
     */
    public void setRequest(Request request);


    /**
     * Return the <code>ServletResponse</code> for which this object
     * is the facade.
     */
    public ServletResponse getResponse();


    /**
     * Return the output stream associated with this Response.
     */
    public OutputStream getStream();


    /**
     * Set the output stream associated with this Response.
     *
     * @param stream The new output stream
     */
    public void setStream(OutputStream stream);

    /**
     * Set the error flag.
     */
    public void setError();


    /**
     * Error flag accessor.
     */
    public boolean isError();


    // --------------------------------------------------------- Public Methods


    /**
     * Create and return a ServletOutputStream to write the content
     * associated with this Response.
     *
     * @exception java.io.IOException if an input/output error occurs
     */
    public ServletOutputStream createOutputStream() throws IOException;


    /**
     * Perform whatever actions are required to flush and close the output
     * stream or writer, in a single operation.
     *
     * @exception java.io.IOException if an input/output error occurs
     */
    public void finishResponse() throws IOException;


    /**
     * Return the content length that was set or calculated for this Response.
     */
    public int getContentLength();


    /**
     * Return the content type that was set or calculated for this response,
     * or <code>null</code> if no content type was set.
     */
    public String getContentType();


    /**
     * Return a PrintWriter that can be used to render error messages,
     * regardless of whether a stream or writer has already been acquired.
     *
     * @return Writer which can be used for error reports. If the response is
     * not an error report returned using sendError or triggered by an
     * unexpected exception thrown during the servlet processing
     * (and only in that case), null will be returned if the response stream
     * has already been used.
     */
    public PrintWriter getReporter();


    /**
     * Release all object references, and initialize instance variables, in
     * preparation for reuse of this object.
     */
    public void recycle();


    /**
     * Reset the data buffer but not any status or header information.
     */
    public void resetBuffer();


    /**
     * Send an acknowledgment of a request.
     *
     * @exception java.io.IOException if an input/output error occurs
     */
    public void sendAcknowledgement()
        throws IOException;


}