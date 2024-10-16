package org.entirej.applicationframework.rwt.renderers.form;

import org.eclipse.swt.widgets.Control;
import org.entirej.framework.core.properties.interfaces.EJTabPageProperties;

public interface ITabFolder
{

    void showPage(String pageName);

    void setTabPageVisible(String pageName, boolean visible);

    Control getFolder();
    
    boolean canFireEvent();

    interface ITab
    {

        void create(boolean b);

        void setIndex(int index);

    }

    ITab newTab(EJTabPageProperties page);

    String getActiveKey();

    void setTabPageBadge(String tabPageName, String badge);

    void put(String name, ITab tab);

    void setTabPageVa(String tabPageName, String visualAttributeName);

    void updatePageLayout(String pageName);
}
