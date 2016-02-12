/*******************************************************************************
 * Copyright 2013 Mojave Innovations GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Mojave Innovations GmbH - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.application.form.containers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTAppComponentRenderer;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormContainer;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormSelectedListener;
import org.entirej.applicationframework.rwt.renderers.form.EJRWTFormRenderer;
import org.entirej.framework.core.data.controllers.EJPopupFormController;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.EJCoreFormProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;

public class EJRWTTabPaneFormContainer implements EJRWTFormContainer, EJRWTAppComponentRenderer
{
    private EJRWTApplicationManager         _manager;
    private CTabFolder                      _folder;
    private EJRWTFormPopUp                  _formPopup;
    private Map<EJInternalForm, CTabItem>   _tabPages              = new HashMap<EJInternalForm, CTabItem>();
    private List<EJRWTFormSelectedListener> _formSelectedListeners = new ArrayList<EJRWTFormSelectedListener>(1);

    @Override
    public void createContainer(EJRWTApplicationManager manager, Composite parent, EJFrameworkExtensionProperties rendererprop)
    {
        _manager = manager;
        createContainer(parent);
    }

    @Override
    public Composite getGuiComponent()
    {
        return _folder;
    }

    protected EJInternalForm getFormByTab(CTabItem tabItem)
    {
        Set<Entry<EJInternalForm, CTabItem>> entries = _tabPages.entrySet();
        for (Entry<EJInternalForm, CTabItem> entry : entries)
        {
            if (entry.getValue().equals(tabItem))
            {
                return entry.getKey();
            }
        }
        return null;
    }

    protected int getTabOrientation()
    {
        return SWT.TOP;
    }

    public Composite createContainer(Composite parent)
    {
        if (_folder != null)
        {
            _folder.dispose();
            _folder = null;
        }

        int style = SWT.FLAT | SWT.BORDER | getTabOrientation() | SWT.CLOSE;

        _folder = new CTabFolder(parent, style);

        _folder.addCTabFolder2Listener(new CTabFolder2Adapter()
        {
            @Override
            public void close(CTabFolderEvent event)
            {
                event.doit = false;
                if (event.item instanceof CTabItem)
                {
                    EJInternalForm form = getFormByTab((CTabItem) event.item);
                    if (form != null)
                    {
                        try
                        {
                            form.close();
                        }
                        catch (Exception e)
                        {
                            form.getFrameworkManager().getApplicationManager().handleException(e);
                        }
                    }
                }
            }
        });

        _folder.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                CTabItem selection = _folder.getSelection();
                if (selection != null && selection.getData() instanceof EJInternalForm)
                {
                    EJInternalForm form = (EJInternalForm) selection.getData();
                    for (EJRWTFormSelectedListener listener : _formSelectedListeners)
                    {
                        listener.fireFormSelected(form);
                    }
                    form.focusGained();
                }
            }
        });
        return _folder;
    }

    @Override
    public EJInternalForm addForm(EJInternalForm form)
    {
        CTabItem tabItem = new CTabItem(_folder, SWT.NONE);
        _tabPages.put(form, tabItem);
        tabItem.setData(form);

        EJRWTFormRenderer renderer = (EJRWTFormRenderer) form.getRenderer();
        final ScrolledComposite scrollComposite = new ScrolledComposite(_folder, SWT.V_SCROLL | SWT.H_SCROLL);
        renderer.createControl(scrollComposite);
        scrollComposite.setContent(renderer.getGuiComponent());
        scrollComposite.setExpandHorizontal(true);
        scrollComposite.setExpandVertical(true);
        scrollComposite.setMinSize(form.getProperties().getFormWidth(), form.getProperties().getFormHeight());

        final EJCoreFormProperties coreFormProperties = form.getProperties();
        tabItem.setText(coreFormProperties.getTitle() == null ? coreFormProperties.getName() : coreFormProperties.getTitle());
        tabItem.setControl(scrollComposite);
        _folder.setSelection(tabItem);
        renderer.gainInitialFocus();
        return form;
    }

    @Override
    public void openPopupForm(EJPopupFormController popupController)
    {
        _formPopup = new EJRWTFormPopUp(_manager.getShell(), popupController);
        _formPopup.showForm();
    }

    @Override
    public void popupFormClosed()
    {
        if (_formPopup != null)
        {
            _formPopup.close();
            _formPopup = null;
        }
    }

    @Override
    public Collection<EJInternalForm> getAllForms()
    {
        return new ArrayList<EJInternalForm>(_tabPages.keySet());
    }

    @Override
    public boolean containsForm(String formName)
    {
        Collection<EJInternalForm> opendForms = getAllForms();
        for (EJInternalForm form : opendForms)
        {
            if (form.getProperties().getName().equalsIgnoreCase(formName))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public EJInternalForm getActiveForm()
    {
        if (_folder != null)
        {
            CTabItem selection = _folder.getSelection();
            if (selection != null && selection.getData() instanceof EJInternalForm)
            {
                return (EJInternalForm) selection.getData();
            }
        }
        return null;
    }

    @Override
    public void closeForm(EJInternalForm form)
    {
        CTabItem tabItem = _tabPages.get(form);
        if (tabItem != null)
        {
            _tabPages.remove(form);
            tabItem.dispose();
        }
    }
    
    @Override
    public void updateFormTitle(EJInternalForm form)
    {
        CTabItem tabItem = _tabPages.get(form);
        if (tabItem != null)
        {
            tabItem.setText(form.getProperties().getTitle());
        }
    }

    public boolean closeAllForms()
    {
        Collection<EJInternalForm> opendForms = getAllForms();
        for (EJInternalForm form : opendForms)
        {
            closeForm(form);
        }

        return true;
    }

    @Override
    public void addFormSelectedListener(EJRWTFormSelectedListener selectionListener)
    {
        _formSelectedListeners.add(selectionListener);
    }

    @Override
    public void removeFormSelectedListener(EJRWTFormSelectedListener selectionListener)
    {
        _formSelectedListeners.remove(selectionListener);
    }

    @Override
    public EJInternalForm switchToForm(String key)
    {
        for (EJInternalForm form : _tabPages.keySet())
        {
            if (form.getProperties().getName().equalsIgnoreCase(key))
            {
                EJRWTFormRenderer renderer = (EJRWTFormRenderer) form.getRenderer();

                _folder.setSelection(_tabPages.get(form));
                renderer.gainInitialFocus();
                return form;
            }
        }
        return null;
    }
}
