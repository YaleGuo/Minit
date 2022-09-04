package com.minit.startup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLClassLoader;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.minit.Loader;
import com.minit.Logger;
import com.minit.connector.http.HttpConnector;
import com.minit.core.ContainerListenerDef;
import com.minit.core.FilterDef;
import com.minit.core.FilterMap;
import com.minit.core.StandardContext;
import com.minit.core.StandardHost;
import com.minit.loader.CommonLoader;
import com.minit.loader.WebappLoader;
import com.minit.logger.FileLogger;

public class BootStrap {
    public static final String MINIT_HOME =
            System.getProperty("user.dir");
    public static String WEB_ROOT =
            System.getProperty("user.dir");
    public static int PORT = 8080;
    private static int debug = 0;

    public static void main(String[] args) {
        if (debug >= 1)
            log(".... startup ....");
        
        //scan server.xml
        //scan web.xml
        String file = MINIT_HOME + File.separator +	"conf" + File.separator + "server.xml";
        
        SAXReader reader = new SAXReader();
        Document document;
		try {
			document = reader.read(file);
			Element root = document.getRootElement();

			Element connectorelement= root.element("Connector");
			Attribute portattribute = connectorelement.attribute("port");
			PORT = Integer.parseInt(portattribute.getText());
			
            Element hostelement = root.element("Host");
			Attribute appbaseattribute = hostelement.attribute("appBase");
			WEB_ROOT =  WEB_ROOT + File.separator + appbaseattribute.getText();
		}
		catch(Exception e) {
		}
		
        System.setProperty("minit.home", MINIT_HOME);
        System.setProperty("minit.base", WEB_ROOT);
        
        HttpConnector connector = new HttpConnector();
        StandardHost container = new StandardHost();
        
        Loader loader = new CommonLoader();
        container.setLoader(loader);
        loader.start();

        connector.setContainer(container);
        container.setConnector(connector);
                
        container.start();
        connector.start();
    }
    private static void log(String message) {
        System.out.print("Bootstrap: ");
        System.out.println(message);
    }

    private static void log(String message, Throwable exception) {
        log(message);
        exception.printStackTrace(System.out);

    }

}