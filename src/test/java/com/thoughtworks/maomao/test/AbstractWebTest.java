package com.thoughtworks.maomao.test;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractWebTest {

    private Server server;
    HttpClient client;

    @Before
    public void startServer() throws Exception {
        server = new Server(11090);

        String webAppPath = "src/test/webapp";
        String contextPath = "/noam-mvc";
        WebAppContext context = new WebAppContext();
        context.setResourceBase(webAppPath);
        context.setDescriptor(webAppPath + "/web.xml");
        context.setContextPath(contextPath);
        context.setParentLoaderPriority(true);

        server.setHandler(context);
        server.start();

        client = new HttpClient();
        client.start();
    }

    @After
    public void stopServer() throws Exception {
        if (client != null)
            client.stop();
        if (server != null)
            server.stop();
        server = null;
    }

}
