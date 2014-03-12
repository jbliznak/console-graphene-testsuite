package org.jboss.as.console.testsuite.tests.configuration.datasources;

import junit.framework.Assert;
import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.config.datasources.ConnectionConfig;
import org.jboss.as.console.testsuite.fragments.config.datasources.DatasourceConfigArea;
import org.jboss.as.console.testsuite.fragments.config.datasources.DatasourceWizard;
import org.jboss.as.console.testsuite.fragments.config.datasources.TestConnectionWindow;
import org.jboss.as.console.testsuite.pages.config.DatasourcesPage;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.Editor;
import org.jboss.as.console.testsuite.util.PropertyEditor;
import org.jboss.qa.management.cli.CliClient;
import org.jboss.qa.management.cli.DSUtils;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.util.Collections;
import java.util.Map;

/**
 * Created by jcechace on 21/02/14.
 */
@RunWith(Arquillian.class)
@Category(SharedTest.class)
public class TestConnectionTestCase {
    @Drone
    private WebDriver browser;

    @Page
    private DatasourcesPage datasourcesPage;

    private static String dsNameValid = "ExampleDS";
    private static String xaDsNameValid;
    private static String dsNameInvalid;
    private static String xaDsNameInvalid;

    private static CliClient cliClient;

    private static final String VALID_URL = "jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1";
    private static final String INVALID_URL = "invalidUrl";

    // Setup

    @BeforeClass
    public static void setup() {  // create needed datasources
        cliClient = new CliClient();
        dsNameInvalid = createDatasource(INVALID_URL, false);
        xaDsNameInvalid = createDatasource(INVALID_URL, true);
        xaDsNameValid = createDatasource(VALID_URL, true);
    }

    @AfterClass
    public static void tearDown() { // remove datasources when finished
        removeDatasource(dsNameInvalid, false);
        removeDatasource(xaDsNameValid, true);
        removeDatasource(xaDsNameInvalid, true);
    }

    @Before
    public void before() {
        Graphene.goTo(DatasourcesPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
    }

    @After
    public void afer() {
        browser.navigate().refresh();
    }

    // Regular DS tests

    @Test
    public void validDatasource() {
        testConnection(dsNameValid, true);
    }

    @Test
    public void invalidDatasource() {
        testConnection(dsNameInvalid, false);
    }

    @Test
    public void validInWizard() {
        String name = RandomStringUtils.randomAlphabetic(6);
        testConnectionInWizard(name, VALID_URL, true);
    }

    @Test
    public void invalidInWizard() {
        String name = RandomStringUtils.randomAlphabetic(6);
        testConnectionInWizard(name, INVALID_URL, false);
    }


    // XA DS tests
    @Test
    public void validXADatasource() {
        datasourcesPage.switchToXA();
        testConnection(xaDsNameValid, true);
    }

    @Test
    public void invalidXADatasource() {
        datasourcesPage.switchToXA();
        testConnection(xaDsNameInvalid, true);
    }

    @Test
    public void validXAInWizard() {
        datasourcesPage.switchToXA();

        String name = RandomStringUtils.randomAlphabetic(6);
        testXAConnectionInWizard(name, VALID_URL, true);
    }

    @Test
    public void invalidXAInWizard() {
        datasourcesPage.switchToXA();

        String name = RandomStringUtils.randomAlphabetic(6);
        testXAConnectionInWizard(name, "invalidUrl", false);
    }

    // Util methods

    private static String createDatasource(String url, boolean xa) {
        String name = RandomStringUtils.randomAlphanumeric(5);

        if (xa) {   // create XA datasource
            // TODO: remove quotes from url once issue with cli is resolved
            Map<String, String> props =  Collections.singletonMap("URL", "\"" +url + "\"");
            DSUtils.addXaDS(cliClient, name, "java:/xa-datasources/" + name, "h2",
                    null, null, props);
            DSUtils.enableXaDS(cliClient, name);
        } else {    // create regular datasource
            DSUtils.addDS(cliClient, name, "java:/datasources/" + name, "h2", url);
            DSUtils.enableDS(cliClient, name);
        }

        return name;
    }

    private static void removeDatasource(String name, boolean xa) {
        if (xa) {   // remove XA datasource
            DSUtils.removeXaDS(cliClient, name);
        } else {    // remove regular detasource
            DSUtils.removeDS(cliClient, name);
        }
    }

    private static void assertNotExists(String name, boolean xa) {
        boolean result;
        if (xa) {   // XA datasource
            result = DSUtils.isXaDsDefined(cliClient, name);
        } else {    // regular datasource
            result = DSUtils.isDsDefined(cliClient, name);
        }

       Assert.assertFalse("Datasource should not exist at this point", result);
    }


    private void testConnection(String name, boolean expected) {
        datasourcesPage.selectByName(name);

        DatasourceConfigArea config = datasourcesPage.getConfig();
        ConnectionConfig connection = config.connectionConfig();
        TestConnectionWindow window = connection.testConnection();

        assertConnectionTest(window, expected);
    }

    private void testConnectionInWizard(String name, String url, boolean expected) {
        DatasourceWizard wizard = datasourcesPage.addResource();
        Editor editor = wizard.getEditor();

        editor.text("name", name);
        editor.text("jndiName", "java:/" + name);

        wizard.next();

        wizard.next();
        editor.text("connectionUrl", url);

        assertConnectionTest(wizard.testConnection(), expected);
        assertNotExists(name, false);
    }

    private void testXAConnectionInWizard(String name, String url, boolean expected) {
        DatasourceWizard wizard = datasourcesPage.addResource();
        Editor editor = wizard.getEditor();

        editor.text("name", name);
        editor.text("jndiName", "java:/" + name);

        wizard.next();

        wizard.next();
        PropertyEditor properties = editor.properties();
        properties.add("URL", url);

        wizard.next();

        assertConnectionTest(wizard.testConnection(), expected);
        assertNotExists(name, true);
    }

    private void assertConnectionTest(TestConnectionWindow window, boolean expected) {
        boolean result = window.isSuccessful();
        window.close();

        if (expected) {
            Assert.assertTrue("Connection test was expected to succeed", result);
        } else {
            Assert.assertFalse("Connection test was expected to fail", result);
        }
    }
}
