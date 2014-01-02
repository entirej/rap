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
import org.eclipse.swt.widgets.Composite;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTAppComponentRenderer;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormContainer;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormSelectedListener;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJStackedPane;
import org.entirej.applicationframework.rwt.renderers.form.EJRWTFormRenderer;
import org.entirej.framework.core.data.controllers.EJPopupFormController;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;

public class EJRWTStackedPaneFormContainer implements EJRWTFormContainer, EJRWTAppComponentRenderer
{
    private EJRWTApplicationManager         _manager;
    private EJRWTEntireJStackedPane         _stackPane;
    private EJRWTFormPopUp                  _formPopup;
    private Map<EJInternalForm, String>     _stackedPages          = new HashMap<EJInternalForm, String>();
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
        return _stackPane;
    }

    protected EJInternalForm getFormByPage(String key)
    {
        Set<Entry<EJInternalForm, String>> entries = _stackedPages.entrySet();
        for (Entry<EJInternalForm, String> entry : entries)
        {
            if (entry.getValue().equals(key))
            {
                return entry.getKey();
            }
        }
        return null;
    }

    public Composite createContainer(Composite parent)
    {
        if (_stackPane != null)
        {
            _stackPane.dispose();
            _stackPane = null;
        }
        _stackPane = new EJRWTEntireJStackedPane(parent, getStyle());

        return _stackPane;
    }

    protected int getStyle()
    {
        return SWT.BORDER;
    }

    @Override
    public EJInternalForm addForm(EJInternalForm form)
    {
        String name = form.getFormController().getEJForm().getName();
        EJInternalForm formByPage = getFormByPage(name);
        if (formByPage != null)
        {
            _stackPane.showPane(name);
            EJRWTFormRenderer renderer = (EJRWTFormRenderer) formByPage.getRenderer();
            renderer.gainInitialFocus();

            formByPage.focusGained();
            for (EJRWTFormSelectedListener listener : _formSelectedListeners)
            {
                listener.fireFormSelected(formByPage);
            }
            return formByPage;
        }
        _stackedPages.put(form, name);

        EJRWTFormRenderer renderer = (EJRWTFormRenderer) form.getRenderer();
        renderer.createControl(_stackPane);
        _stackPane.add(name, renderer.getGuiComponent());
        _stackPane.showPane(name);
        renderer.gainInitialFocus();

        form.focusGained();
        for (EJRWTFormSelectedListener listener : _formSelectedListeners)
        {
            listener.fireFormSelected(form);
        }
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
        return new ArrayList<EJInternalForm>(_stackedPages.keySet());
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
        if (_stackPane != null)
        {
            return getFormByPage(_stackPane.getActiveControlKey());
        }
        return null;
    }

    @Override
    public void closeForm(EJInternalForm form)
    {
        String tabItem = _stackedPages.get(form);
        if (tabItem != null)
        {
            _stackPane.remove(tabItem);
            _stackedPages.remove(form);
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
        for (EJInternalForm form : _stackedPages.keySet())
        {
            if (form.getProperties().getName().equalsIgnoreCase(key))
            {
                EJRWTFormRenderer renderer = (EJRWTFormRenderer) form.getRenderer();

                _stackPane.showPane(_stackedPages.get(form));
                renderer.gainInitialFocus();

                form.focusGained();
                for (EJRWTFormSelectedListener listener : _formSelectedListeners)
                {
                    listener.fireFormSelected(form);
                }
                return form;
            }
        }
        return null;
    }
}
