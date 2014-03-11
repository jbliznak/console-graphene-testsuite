package org.jboss.as.console.testsuite.util;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.fragments.BaseFragment;
import org.jboss.as.console.testsuite.fragments.ResourceTableFragment;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Created by jcechace on 03/03/14.
 */
public class Editor extends BaseFragment {

    /**
     * Returns either a input of type text or textarea element with given identifier (name or id).
     *
     * @param identifier
     * @return a text element
     */
    public WebElement getText(String identifier) {
        return findTextElement(identifier);
    }

    /**
     * Sets the value of given text element.
     *
     * @param identifier
     * @param value
     */
    public void text(String identifier, String value) {
        WebElement input = getText(identifier);
        input.clear();
        Graphene.waitGui().until().element(input).value().equalTo("");
        input.sendKeys(value);
        Graphene.waitGui().until().element(input).value().equalTo(value);
    }

    /**
     * Reads the value from text element with given identifier.
     *
     * @param identifier
     * @return a value of text element
     */
    public String text(String identifier) {
        WebElement input = getText(identifier);

        return input.getAttribute("value");
    }

    /**
     * Returns a password input with given identifier
     *
     * @param identifier
     * @return a password input
     */
    public WebElement getPassword(String identifier) {
        return findInputElement("password", identifier);
    }

    public void password(String identifier, String value) {
        WebElement input = getPassword(identifier);
        input.clear();
        Graphene.waitGui().until().element(input).value().equalTo("");
        input.sendKeys(value);
        System.err.println(input.getText());
    }

    /**
     * Returns a checkbox with given identifier.
     * @param identifier
     * @return a checkbox element
     */
    public WebElement getCheckbox(String identifier) {
        return findInputElement("checkbox", identifier);
    }

    /**
     * Sets the value of checkbox with given identifier.
     *
     * @param identifier
     * @param value
     */
    public void checkbox(String identifier, boolean value) {
        WebElement input = getCheckbox(identifier);
        boolean current = input.isSelected();

        if (value != current) {
            input.click();
        }

        if (value) {
            Graphene.waitGui().until().element(input).is().selected();
        } else {
            Graphene.waitGui().until().element(input).is().not().selected();
        }
    }

    /**
     * Reads  the value of checkbox with given identifier.
     *
     * @param identifier
     * @return value of checkbox
     */
    public boolean  checkbox(String identifier) {
        WebElement input = getCheckbox(identifier);

        return input.isSelected();
    }


    /**
     *  Returns a property editor object found within editor's root.
     *
     * @return a property editor
     */
    public PropertyEditor properties() {
        PropertyEditor properties = Console.withBrowser(browser).getPropertyEditor(root);

        return properties;
    }

    private WebElement findTextElement(String identifier) {
        WebElement text = null;
        try {
            text = findInputElement("text", identifier);
        } catch (NoSuchElementException ignore) {
            String byIdSelector = "textarea[id$='" + identifier + "'], ";
            String byNameSelector = "textarea[name='" + identifier + "'], ";
            By selector = ByJQuery.selector(byIdSelector + ", " + byNameSelector);

            text = findElement(selector, root);
        }

        return text;
    }

    private WebElement findInputElement(String type, String identifier) {
        String byIdSelector = "input[type='" + type + "'][id$='" + identifier + "'], ";
        String byNameSelector = "input[type='" + type + "'][name='" + identifier + "'], ";
        By selector = ByJQuery.selector(byIdSelector + ", " + byNameSelector);

        return findElement(selector, root);
    }

    private WebElement findElement(By selector, WebElement root) {
        return Console.withBrowser(browser).findElement(selector, root);
    }
}
