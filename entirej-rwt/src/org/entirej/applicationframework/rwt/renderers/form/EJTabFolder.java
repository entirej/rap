package org.entirej.applicationframework.rwt.renderers.form;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;
import org.entirej.framework.core.data.controllers.EJCanvasController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.containers.interfaces.EJCanvasPropertiesContainer;
import org.entirej.framework.core.properties.interfaces.EJCanvasProperties;
import org.entirej.framework.core.properties.interfaces.EJTabPageProperties;

class EJTabFolder implements ITabFolder
{
    /**
     * 
     */
    private final EJRWTFormRenderer ejrwtFormRenderer;
    final CTabFolder         folder;
    final EJCanvasController canvasController;
    final Map<String, Tab>   tabPages = new HashMap<String, Tab>();

    private AtomicBoolean fireEvents = new AtomicBoolean(true);
    
    private String lastSelection;
    
    EJTabFolder(EJRWTFormRenderer ejrwtFormRenderer, CTabFolder folder, EJCanvasController canvasController)
    {
        super();
        this.ejrwtFormRenderer = ejrwtFormRenderer;
        this.folder = folder;
        this.canvasController = canvasController;
    }

    @Override
    public boolean canFireEvent()
    {
        return fireEvents.get();
    }
    
    public void showPage(String pageName)
    {
        try {
            fireEvents.set(false);
            Tab cTabItem = tabPages.get(pageName);
            if (cTabItem != null && cTabItem.item != null)
            {
                lastSelection = pageName;
                cTabItem.createTabData();
                folder.setSelection(cTabItem.item);
    
                EJ_RWT.setAttribute(folder, "ej-item-selection", pageName);
            }
        }
        finally {
            fireEvents.set(true);
        }

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
        final Tab cTabItem = tabPages.get(pageName);
        if (cTabItem != null)
        {
            if (visible)
            {
                if (cTabItem.item == null)
                {
                    Display.getDefault().asyncExec(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            cTabItem.create(true);

                        }
                    });
                }
            }
            else
            {
                if (cTabItem.item != null)
                {
                    CTabItem[] items = folder.getItems();
                    int index = 0;
                    for (CTabItem cTabItem2 : items)
                    {
                        if (cTabItem2 == cTabItem.item)
                        {
                            cTabItem.index = index;
                            break;
                        }
                        index++;
                    }
                    Display.getDefault().asyncExec(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            cTabItem.remove();
                        }
                    });
                }
            }
        }

    }

    public Tab newTab(EJTabPageProperties page)
    {
        return new EJTabFolder.Tab(page);
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
        return tabPages.get(key).item;
    }

    public void put(String key, ITab value)
    {
        tabPages.put(key,(Tab) value);
        if(lastSelection==null)
            key =lastSelection;
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

    class Tab implements ITabFolder.ITab
    {
        final AtomicBoolean       init  = new AtomicBoolean(true);
        CTabItem                  item;
        int                       index = -1;
        EJRWTEntireJGridPane      pageCanvas;
        final EJTabPageProperties page;

        public Tab(EJTabPageProperties page)
        {
            this.page = page;
        }

        void remove()
        {
            if (item != null && !item.isDisposed())
            {
                item.dispose();
            }
            item = null;
        }
        
        @Override
        public void setIndex(int index)
        {
            this.index = index;
            
        }

      public  void create(boolean innerBuild)
        {

            final CTabItem tabItem = (index == -1 || folder.getItemCount() < index) ? new CTabItem(folder, SWT.NONE) : new CTabItem(folder, SWT.NONE, index);
            tabItem.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
            tabItem.setData("TAB_KEY", page.getName());
            
            EJ_RWT.setTestId(tabItem, page.getName());
            pageCanvas = new EJRWTEntireJGridPane(folder, page.getNumCols());
            pageCanvas.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
            tabItem.setText(page.getPageTitle() != null && page.getPageTitle().length() > 0 ? page.getPageTitle() : page.getName());
            tabItem.setControl(pageCanvas);
            final EJCanvasPropertiesContainer containedCanvases = page.getContainedCanvases();

            init.set(innerBuild || folder.getSelection() == null);

            if (init.get())
            {
                for (EJCanvasProperties pageProperties : containedCanvases.getAllCanvasProperties())
                {
                    EJTabFolder.this.ejrwtFormRenderer.createCanvas(pageCanvas, pageProperties, canvasController);
                }
                pageCanvas.layout(true);
            }
            else
            {

                folder.addSelectionListener(new SelectionListener()
                {

                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        if (folder.getSelection() != tabItem)
                            return;

                        createTabData();
                        folder.removeSelectionListener(this);
                    }

                    @Override
                    public void widgetDefaultSelected(SelectionEvent e)
                    {
                        widgetSelected(e);

                    }
                });
            }
            if (folder.getSelection() == null)
            {
                folder.setSelection(tabItem);
            }

            item = tabItem;
            tabItem.getControl().setEnabled(page.isEnabled());

        }

        private void createTabData()
        {
            if (!init.get())
            {
                init.set(true);

                final EJCanvasPropertiesContainer containedCanvases = page.getContainedCanvases();
                for (EJCanvasProperties pageProperties : containedCanvases.getAllCanvasProperties())
                {
                    EJTabFolder.this.ejrwtFormRenderer.createCanvas(pageCanvas, pageProperties, canvasController);
                }
                pageCanvas.layout(true);
            }

        }
    }

    public void setTabPageBadge(String tabPageName, String badge)
    {
        Tab cTabItem = tabPages.get(tabPageName);
        if (cTabItem != null && cTabItem.item != null)
        {
            cTabItem.item.setData(RWT.BADGE, badge);
        }

    }
    
    @Override
    public void setTabPageVa(String tabPageName, String visualAttributeName)
    {
        Tab cTabItem = tabPages.get(tabPageName);
        if (cTabItem != null && cTabItem.item != null)
        {
            
            
            //https://bugs.eclipse.org/bugs/show_bug.cgi?id=561155
            cTabItem.item.setData(EJ_RWT.CUSTOM_VARIANT, visualAttributeName);
            
            
        }
        
    }

    

}