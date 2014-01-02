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
package org.entirej.applicationframework.rwt.application;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTApplicationComponent;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormChosenEvent;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormChosenListener;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormClosedListener;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormOpenedListener;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormSelectedListener;
import org.entirej.framework.core.internal.EJInternalForm;

/**
 * A {@link EJRWTApplicationBarLayoutComponent} is a component used within the
 * ApplicationContainer and is used to hold different ApplicationComponents
 */
public class EJRWTApplicationBarLayoutComponent implements EJRWTFormOpenedListener, EJRWTFormClosedListener, EJRWTFormSelectedListener, EJRWTFormChosenListener
{
    private Composite                     _gridLayout;

    private EJRWTApplicationComponent     _area1Component;
    private EJRWTApplicationComponent     _area2Component;
    private EJRWTApplicationComponent     _area3Component;
    private EJRWTApplicationComponent     _area4Component;

    private Composite                     col1;
    private Composite                     col2;
    private Composite                     col3;
    private Composite                     col4;

    private List<EJRWTFormChosenListener> _formChosenListeners;

    EJRWTApplicationBarLayoutComponent(Composite parent, int style)
    {
        _formChosenListeners = new ArrayList<EJRWTFormChosenListener>();
        _gridLayout = new Composite(parent, style);
        GridLayout layout = new GridLayout(4, true);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginRight = 0;
        layout.marginLeft = 0;
        _gridLayout.setLayout(layout);
        col1 = createCol(_gridLayout);
        col2 = createCol(_gridLayout);
        col3 = createCol(_gridLayout);
        col4 = createCol(_gridLayout);
    }

    public Composite getComponent()
    {
        return _gridLayout;
    }

    public static Composite createCol(Composite parent)
    {

        Composite label = new Composite(parent, SWT.NO_FOCUS);
        GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
        GridLayout layout = new GridLayout(1, true);
        layout.marginWidth = 0;
        layout.marginRight = 0;
        layout.marginHeight = 0;
        layout.marginLeft = 0;
        label.setLayout(layout);
        label.setLayoutData(gridData);
        return label;
    }

    public void setArea1(EJRWTApplicationComponent component)
    {
        if (_area1Component != null)
        {
            _area1Component.removeFormChosenListener(this);
            _area1Component = null;
        }

        if (component != null)
        {
            _area1Component = component;
            _area1Component.addFormChosenListener(this);
            GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
            component.createComponent(col1).setLayoutData(gridData);
        }
    }

    public void setArea2(EJRWTApplicationComponent component)
    {
        if (_area2Component != null)
        {
            _area2Component.removeFormChosenListener(this);
            _area2Component = null;
        }

        if (component != null)
        {
            _area2Component = component;
            _area2Component.addFormChosenListener(this);
            GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
            component.createComponent(col2).setLayoutData(gridData);
        }
    }

    public void setArea3(EJRWTApplicationComponent component)
    {
        if (_area3Component != null)
        {
            _area3Component.removeFormChosenListener(this);
            _area3Component = null;
        }

        if (component != null)
        {
            _area3Component = component;
            _area3Component.addFormChosenListener(this);
            GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
            component.createComponent(col3).setLayoutData(gridData);
        }
    }

    public void setArea4(EJRWTApplicationComponent component)
    {
        if (_area4Component != null)
        {
            _area4Component.removeFormChosenListener(this);
            _area4Component = null;
        }

        if (component != null)
        {
            _area4Component = component;
            _area4Component.addFormChosenListener(this);
            GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
            component.createComponent(col4).setLayoutData(gridData);
        }
    }

    /**
     * Called if this form component is used to choose forms to open
     * <p>
     * The form component can be used to choose forms to be opened by the
     * framework. If this is the case, then the component must call this method
     * to inform all listeners that a form has been chosen. If this component is
     * not used to choose forms then this method need not be implemented
     * 
     * @param formChosenListener
     *            The listener to add
     */
    public void addFormChosenListener(EJRWTFormChosenListener formChosenListener)
    {
        if (formChosenListener == null)
        {
            return;
        }

        _formChosenListeners.add(formChosenListener);
    }

    /**
     * Used to remove a {@link EJSwingFormChosenListener} from this layout
     * component
     * <p>
     * 
     * @param formChosenListener
     *            The listener to remove
     */
    public void removeFormChosenListener(EJRWTFormChosenListener formChosenListener)
    {
        if (formChosenListener == null)
        {
            return;
        }

        _formChosenListeners.remove(formChosenListener);
    }

    @Override
    public void fireFormOpened(EJInternalForm openedForm)
    {
        if (_area1Component != null)
        {
            _area1Component.fireFormOpened(openedForm);
        }

        if (_area2Component != null)
        {
            _area2Component.fireFormOpened(openedForm);
        }

        if (_area3Component != null)
        {
            _area3Component.fireFormOpened(openedForm);
        }

        if (_area4Component != null)
        {
            _area4Component.fireFormOpened(openedForm);
        }
    }

    @Override
    public void fireFormSelected(EJInternalForm selectedForm)
    {
        if (_area1Component != null)
        {
            _area1Component.fireFormSelected(selectedForm);
        }

        if (_area2Component != null)
        {
            _area2Component.fireFormSelected(selectedForm);
        }

        if (_area3Component != null)
        {
            _area3Component.fireFormSelected(selectedForm);
        }

        if (_area4Component != null)
        {
            _area4Component.fireFormSelected(selectedForm);
        }
    }

    @Override
    public void fireFormClosed(EJInternalForm closedForm)
    {
        if (_area1Component != null)
        {
            _area1Component.fireFormClosed(closedForm);
        }

        if (_area2Component != null)
        {
            _area2Component.fireFormClosed(closedForm);
        }

        if (_area3Component != null)
        {
            _area3Component.fireFormClosed(closedForm);
        }

        if (_area4Component != null)
        {
            _area4Component.fireFormClosed(closedForm);
        }
    }

    @Override
    public void formChosen(EJRWTFormChosenEvent event)
    {
        for (EJRWTFormChosenListener listener : _formChosenListeners)
        {
            listener.formChosen(event);
        }
    }

}
