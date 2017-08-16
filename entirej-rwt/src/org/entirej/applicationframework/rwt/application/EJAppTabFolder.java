package org.entirej.applicationframework.rwt.application;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

class EJAppTabFolder 
{
    /**
     * 
     */
    private final EJRWTApplicationContainer container;
    final CTabFolder         folder;
    final Map<String, CTabItem>   tabPages = new HashMap<String, CTabItem>();
    final Map<String, TabContext>   tabContPages = new HashMap<String, TabContext>();
    
    private String lastSelection;

    EJAppTabFolder(EJRWTApplicationContainer container, CTabFolder folder)
    {
        this.container = container;
        this.folder = folder;
    }

    public boolean showPage(String pageName)
    {
        CTabItem cTabItem = tabPages.get(pageName);
        if (cTabItem != null && cTabItem != null)
        {
            lastSelection = pageName;
            folder.setSelection(cTabItem);
          
            return true;
        }

        return true;
    }
    
    public String getLastSelection()
    {
        return lastSelection;
    }
    
    public void setLastSelection(String lastSelection)
    {
        this.lastSelection = lastSelection;
    }

    public CTabFolder getFolder()
    {
        return folder;
    }

    public void setTabPageVisible(String pageName, boolean visible)
    {
        final CTabItem cTabItem = tabPages.get(pageName);
        if (cTabItem != null)
        {
            if (visible)
            {
               if(cTabItem.isDisposed())
               {
                   TabContext tabContext = tabContPages.get(pageName);
                   int index =  tabContext.index;
                   CTabItem tabItem = (index == -1 || folder.getItemCount() < index) ? new CTabItem(folder, SWT.NONE) : new CTabItem(folder, SWT.NONE, index);
                   tabItem.setText(tabContext.text);
                   tabItem.setToolTipText(tabContext.tooltip);
                   
                   tabItem.setControl(tabContext.control);
                   tabItem.setData("TAB_KEY", pageName);
                   tabPages.put(pageName, tabItem);
               }
            }
            else
            {
                if (cTabItem != null)
                {
                    CTabItem[] items = folder.getItems();
                    int index = 0;
                    for (CTabItem cTabItem2 : items)
                    {
                        if (cTabItem2 == cTabItem)
                        {
                           
                            break;
                        }
                        index++;
                    }
                    Control control = cTabItem.getControl();
                    TabContext context = new TabContext();
                    context.control = control;
                    context.text=(cTabItem.getText());
                    context.tooltip=(cTabItem.getToolTipText());
                    context.index=index;
                    tabContPages.put(pageName, context);
                    Display.getDefault().asyncExec(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            cTabItem.dispose();
                        }
                    });
                }
            }
        }

    }

    
    private class TabContext
    {
        Control control;
        String text;
        String tooltip;
        int index;
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
        if(lastSelection==null)
            lastSelection = key;
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