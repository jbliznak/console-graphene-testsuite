package org.jboss.as.console.testsuite.pages.config;

import org.jboss.arquillian.graphene.page.Location;
import org.jboss.as.console.testsuite.fragments.config.datasources.DatasourceConfigArea;
import org.jboss.as.console.testsuite.fragments.config.datasources.DatasourceWizard;
import org.jboss.as.console.testsuite.pages.ConfigPage;

/**
 * Created by jcechace on 22/02/14.
 */

@Location("#datasources")
public class DatasourcesPage extends ConfigPage {
    public void selectByName(String name) {
        getResourceTable().selectRowByText(0, name);
    }

    @Override
    public DatasourceConfigArea getConfig() {
        return getConfig(DatasourceConfigArea.class);
    }

    public DatasourceWizard addResource() {
        return addResource(DatasourceWizard.class);
    }

    public void switchToXA() {
        switchTab("XA Datasources");
    }

}
