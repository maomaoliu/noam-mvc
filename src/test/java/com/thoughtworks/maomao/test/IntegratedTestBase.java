package com.thoughtworks.maomao.test;


import com.thoughtworks.maomao.browser.Browser;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IntegratedTestBase {
    public Browser browser;

    private Server server;
    private String address = "http://localhost:11090/noam-mvc";

    @Before
    public void initializeWebDriver() throws Exception {
        startServer();
        browser = new Browser(address, false);
        browser.open("/");
    }

    @After
    public void closeBrowser() throws Exception {
        browser.stop();
        stopServer();
    }

    @Test
    public void shouldOpenBrowser(){
        assertThat(browser.getCurrentUrl(), is(address+"/"));
    }

    private void startServer() throws Exception {
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
    }

    private void stopServer() throws Exception {
        if (server != null)
            server.stop();
        server = null;
    }
}
