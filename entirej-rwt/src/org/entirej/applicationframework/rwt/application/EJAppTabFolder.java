package org.entirej.applicationframework.rwt.application;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

class EJAppTabFolder 
{
    /**
     * 
     */
    private final EJRWTApplicationContainer container;
    final CTabFolder         folder;
    final Map<String, CTabItem>   tabPages = new HashMap<String, CTabItem>();

    EJAppTabFolder(EJRWTApplicationContainer container, CTabFolder folder)
    {
        this.container = container;
        this.folder = folder;
    }

    public void showPage(String pageName)
    {
        CTabItem cTabItem = tabPages.get(pageName);
        if (cTabItem != null && cTabItem != null)
        {
         
            folder.setSelection(cTabItem);
        }

    }

    public CTabFolder getFolder()
    {
        return folder;
    }

    public void setTabPageVisible(String pageName, boolean visible)
    {
//        final CTabItem cTabItem = tabPages.get(pageName);
//        if (cTabItem != null)
//        {
//            if (visible)
//            {
//                
//            }
//            else
//            {
//                if (cTabItem.item != null)
//                {
//                    CTabItem[] items = folder.getItems();
//                    int index = 0;
//                    for (CTabItem cTabItem2 : items)
//                    {
//                        if (cTabItem2 == cTabItem.item)
//                        {
//                            cTabItem.index = index;
//                            break;
//                        }
//                        index++;
//                    }
//                    Display.getDefault().asyncExec(new Runnable()
//                    {
//
//                        @Override
//                        public void run()
//                        {
//                            cTabItem.remove();
//                        }
//                    });
//                }
//            }
//        }

    }

    

    void clear()
    {
        tabPages.clear();
    }

    boolean containsKey(String key)
    {
        return tabPages.containsKey(key);
    }

    CTabItem get(String key)
    {
        return tabPages.get(key);
    }

    public void put(String key, CTabItem value)
    {
        tabPages.put(key,(CTabItem) value);
    }

    void remove(String key)
    {
        tabPages.remove(key);
    }

    public String getActiveKey()
    {
        CTabItem selection = folder.getSelection();
        if (selection != null)
        {
            return (String) selection.getData("TAB_KEY");
        }
        return null;
    }

   

    public void setTabPageBadge(String tabPageName, String badge)
    {
        CTabItem cTabItem = tabPages.get(tabPageName);
        if (cTabItem != null)
        {
            cTabItem.setData(RWT.BADGE, badge);
        }

    }

    

}