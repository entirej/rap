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
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTAppComponentRenderer;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormSelectedListener;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;
import org.entirej.applicationframework.rwt.renderers.form.EJRWTFormRenderer;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;

public class EJRWTSingleFormContainer implements EJRWTAppComponentRenderer
{
    public static final String              FORM_ID                = "FORM_ID";

    private Control                         _control;
    private EJInternalForm                  _form;

    private List<EJRWTFormSelectedListener> _formSelectedListeners = new ArrayList<EJRWTFormSelectedListener>(1);

    @Override
    public Control getGuiComponent()
    {

        return _control;
    }

    public EJInternalForm getForm()
    {
        return _form;
    }

    @Override
    public void createContainer(EJRWTApplicationManager manager, Composite parent, EJFrameworkExtensionProperties rendererprop)
    {
        String formid = null;
        if (rendererprop != null)
        {
            formid = rendererprop.getStringProperty(FORM_ID);
        }

        if (formid != null)
        {
            try
            {
                _form = manager.getFrameworkManager().createInternalForm(formid, null);
                if (_form != null)
                {
                    Composite composite = new Composite(parent, SWT.BORDER);
                    FillLayout fillLayout = new FillLayout();
                    fillLayout.marginHeight = 5;
                    fillLayout.marginWidth = 5;
                    composite.setLayout(fillLayout);
                    EJRWTFormRenderer renderer = (EJRWTFormRenderer) _form.getRenderer();
                    renderer.createControl(composite);
                    EJRWTEntireJGridPane gridPane = renderer.getGuiComponent();
                    gridPane.cleanLayout();
                    gridPane.addFocusListener(new FocusListener()
                    {

                        @Override
                        public void focusLost(FocusEvent arg0)
                        {
                            // ignore
                        }

                        @Override
                        public void focusGained(FocusEvent arg0)
                        {
                            for (EJRWTFormSelectedListener listener : _formSelectedListeners)
                            {
                                listener.fireFormSelected(_form);
                            }
                            _form.focusGained();
                        }
                    });
                    _control = composite;
                    return;
                }
            }
            catch (Exception e)
            {

                manager.getApplicationMessenger().handleException(e, true);
            }
        }

        Label label = new Label(parent, SWT.NONE);
        label.setText("Form could not be found ID#:" + (formid != null ? formid : "<null>"));
        _control = label;
    }

    public void addFormSelectedListener(EJRWTFormSelectedListener selectionListener)
    {
        _formSelectedListeners.add(selectionListener);

    }

    public void removeFormSelectedListener(EJRWTFormSelectedListener selectionListener)
    {
        _formSelectedListeners.remove(selectionListener);
    }
}
