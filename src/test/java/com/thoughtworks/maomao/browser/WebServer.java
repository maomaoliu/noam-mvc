package com.thoughtworks.maomao.browser;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.net.MalformedURLException;

public class WebServer {

    private Server jetty;


    public WebServer(int port, String webApp, String contextPath) throws MalformedURLException {
        jetty = new Server(port);
        String webAppPath = new File(webApp).getAbsolutePath();
        WebAppContext context = new WebAppContext();
        context.setResourceBase(webAppPath);
        context.setDescriptor(webAppPath + "/web.xml");
        context.setContextPath(contextPath);
        context.setParentLoaderPriority(true);
        jetty.setHandler(context);

    }

    public WebServer start() throws Exception {
        jetty.start();
        return this;
    }

    public void stop() {
        try {
            jetty.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        final WebServer server;
        if (args.length == 0) {
            server = new WebServer(10190, "src/test/webapp", "/noam-mvc");
        } else {
            server = new WebServer(Integer.parseInt(args[0]), args[1], args[2]);
        }
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                server.stop();
            }
        }));
    }
}

