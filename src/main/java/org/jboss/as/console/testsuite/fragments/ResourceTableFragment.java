package org.jboss.as.console.testsuite.fragments;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by jcechace on 23/02/14.
 */
public class ResourceTableFragment extends BaseFragment {

    // TODO: use properties (this is only temporary implementation)

    public ResourceTableRowFragment getRow(int index) {
        List<WebElement> rowElements = getRowElements();

        WebElement rowRoot = rowElements.get(index);

        ResourceTableRowFragment row = Graphene.createPageFragment(ResourceTableRowFragment.class,
                rowRoot);
        return row;
    }

    public ResourceTableRowFragment getLastRow() {
        List<WebElement> rowElements = getRowElements();
        WebElement rowRoot = rowElements.get(rowElements.size() -1);

        ResourceTableRowFragment row = Graphene.createPageFragment(ResourceTableRowFragment.class,
                rowRoot);
        return row;
    }



    public ResourceTableRowFragment getRowByText(int index, String text) {
        By selector = getRowByTextSelector(index, text);
        WebElement rowRoot = root.findElement(selector);

        ResourceTableRowFragment row = Graphene.createPageFragment(ResourceTableRowFragment.class,
                rowRoot);
        return row;
    }

    public ResourceTableRowFragment selectRowByText(int index, String text) {
        ResourceTableRowFragment row = getRowByText(index, text);
        row.click();

        // TODO: replace timeout waiting
        Graphene.waitModel().withTimeout(1500, TimeUnit.MILLISECONDS);

        return row;
    }


    private List<WebElement> getRowElements(boolean fail) {
        // TODO: workaround - there is no cellTableRow class, thus odd and even row need to be selected separately
        By selector = new ByJQuery("tr.cellTableEvenRow, tr.cellTableOddRow");
        List<WebElement> rowElements = root.findElements(selector);

        if (rowElements.isEmpty()) {
            throw new NoSuchElementException("Cannot locate element using: " + selector);
        }

        return rowElements;
    }

    private List<WebElement> getRowElements() {
        return getRowElements(true);
    }

    private By getRowByTextSelector(int index, String text) {
        // TODO: workaround - there is no cellTableRow class ...
        // TODO: find a better way than xpath
        // TODO: remove this method after resolving previous issues
        String row = "tr[contains(@class, 'cellTableEvenRow') or " +
                "contains(@class, 'cellTableOddRow')]";
        String cell = "td[contains(@class, 'cellTableCell')]";
        String containsText = "*[contains(text(), '" + text + "')]";
        By selector = By.xpath(".//" + row + "//" + cell + "["+ (index + 1) + "]" +
                "/descendant-or-self::" + containsText + "/ancestor::" + row);

        return selector;
    }


}

