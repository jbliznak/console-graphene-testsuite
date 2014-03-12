package org.jboss.as.console.testsuite.tests.console;

import org.jboss.as.console.testsuite.fragments.shared.modals.SettingsWindow;
import junit.framework.Assert;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.jboss.as.console.testsuite.pages.runtime.OverviewPage;
import org.jboss.as.console.testsuite.tests.util.ConfigUtils;
import org.jboss.as.console.testsuite.util.Console;

/**
 * Created by jcechace on 15/02/14.
 */
@RunWith(Arquillian.class)
@Category(SharedTest.class)
public class GoogleAnalyticsTestCase {

    @Drone
    private WebDriver browser;

    @Page
    private OverviewPage overviewPage;

    @Before
    public void setup() {
        Graphene.goTo(OverviewPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
        browser.manage().deleteAllCookies();
    }

    @Test()
    @InSequence(0)
    public void defaultValue() {
        SettingsWindow settings = overviewPage.getFooter().openSettings();
        // Cookie should not be present
            assertCookieNotPresent();
        // Test checkbox value
        if (ConfigUtils.isEAP()) {
            assertCheckboxValue(settings, false);
        } else {
            assertCheckboxValue(settings, true);
        }
    }


    @Test
    @InSequence(1)
    public void enableAndDisable() {
        SettingsWindow settings = overviewPage.getFooter().openSettings();

        boolean enabled = settings.getAnalytics();

        if (enabled) {  // analytics currently enabled
            disableAndAssert();
            enableAndAssert();
        } else {        // analytics currently disabled
            enableAndAssert();
            disableAndAssert();
        }
    }

    private void enableAndAssert() {
        SettingsWindow settings = overviewPage.getFooter().openSettings();
        settings.setAnalytics(true);
        settings.save(true);

        assertCookieValue(true);
        assertCheckboxValue(overviewPage.getFooter().openSettings(), true);
    }

    private void disableAndAssert() {
        SettingsWindow settings = overviewPage.getFooter().openSettings();
        settings.setAnalytics(false);
        settings.save(true);

        assertCookieValue(false);
        assertCheckboxValue(overviewPage.getFooter().openSettings(), false);
    }

    private void assertCheckboxValue(SettingsWindow settings, boolean explected) {
        boolean enabled = settings.getAnalytics();
        settings.close();

        if (explected) {
            Assert.assertTrue("Analytics should be enabled (settings checkbox).", enabled);
        } else {
            Assert.assertFalse("Analytics should be enabled (settings checkbox).", enabled);
        }
    }


    private Cookie getCookie() {
        String name = PropUtils.get("settings.analytics.cookie");
        Cookie cookie = browser.manage().getCookieNamed(name);
        return cookie;
    }

    private void assertCookieNotPresent() {
        Assert.assertNull("Cookie named as7_ui_analytics should not be present.", getCookie());
    }

    private void assertCookieValue(boolean expected) {
        boolean cookieVal = Boolean.parseBoolean(getCookie().getValue());
        if (expected) {
            Assert.assertTrue("Analytics should be enabled (cookie value).", cookieVal);
        } else {
            Assert.assertFalse("Analytics should be disabled (cookie value).", cookieVal);
        }
    }

}
