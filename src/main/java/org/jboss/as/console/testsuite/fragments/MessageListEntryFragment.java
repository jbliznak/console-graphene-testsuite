package org.jboss.as.console.testsuite.fragments;

import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.openqa.selenium.By;

/**
 * Represents single message in messages popup window.
 * @author jbliznak@redhat.com
 */
public class MessageListEntryFragment extends PopUpFragment {
    public static final String CLASS_READ_FLAG = PropUtils.get("messagelist.entry.read.class");
    public static final String CLASS_TEXT = PropUtils.get("messagelist.entry.content.class");
    public static final String CLASS_SUCCESSFUL_FLAG = PropUtils.get("messagelist.entry.icon.success.class");

    /**
     * @return whether message is still unread
     */
    public boolean isUnread() {
        return root.findElements(By.className(CLASS_READ_FLAG)).isEmpty();
    }

    /**
     * @return displayed message text
     */
    public String getText() {
        return root.findElement(By.className(CLASS_TEXT)).getText();
    }

    /**
     * @return current message describe successful operation
     */
    public boolean isSuccess() {
        return !root.findElements(By.className(CLASS_SUCCESSFUL_FLAG)).isEmpty();
    }
    
    
    /**
     * Open message.
     * @return opened window with full message
     */
    public <T extends WindowFragment> T open() {
        root.click();
        Console console = Console.withBrowser(browser);
        T w = (T) console.openedWindow();
        return w;
    }
}
