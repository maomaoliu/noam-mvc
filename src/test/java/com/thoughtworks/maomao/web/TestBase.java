package com.thoughtworks.maomao.web;


import com.thoughtworks.maomao.browser.Browser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TestBase {
    public Browser browser;
    private String address = "http://localhost:10190/noam-mvc";

    @Before
    public void initializeWebDriver() throws IOException {
        browser = new Browser(address, false);
        browser.open("/");
    }

    @After
    public void closeBrowser() {
        browser.stop();
    }

    @Test
    public void shouldOpenBrowser(){
        assertThat(browser.getCurrentUrl(), is(address+"/"));
    }
}
