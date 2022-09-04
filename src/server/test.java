package server;

import java.io.File;

public class test {
    public static final String WEB_ROOT =
            System.getProperty("user.dir") + File.separator + "webroot";

    public static void main(String[] args) {
    	System.out.println(WEB_ROOT);
    }
}
