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
package org.entirej.applicationframework.rwt.application.components;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public abstract class EJRWTAbstractActionList extends Composite implements Serializable
{
    private final List    comboControl;
    private Control       actionControl;
    protected Text        filter;
    private int           actionWidthHint = 0;

    private AtomicBoolean active          = new AtomicBoolean(true);

    public EJRWTAbstractActionList(Composite parent)
    {
        super(parent, SWT.NO_FOCUS);
        setData(EJ_RWT.CUSTOM_VARIANT, "itemgroupclear");
        int numColumns = 1;

        numColumns = 2;

        GridLayoutFactory.swtDefaults().margins(0, 0).extendedMargins(1, 1, 1, 1).spacing(0, 0).numColumns(numColumns).applyTo(this);

        if (hasFilter())
        {

            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
            gridData.heightHint = 13;
            filter = new Text(this, SWT.SEARCH | SWT.ICON_CANCEL);
            filter.setLayoutData(gridData);
            comboControl = createList(this);
            comboControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            super.setFont(comboControl.getFont());
            actionControl = createLabelButtonControl(this);
            comboControl.moveBelow(actionControl);

            filter.addSelectionListener(new SelectionAdapter()
            {
                public void widgetDefaultSelected(SelectionEvent e)
                {
                    if (e.detail == SWT.CANCEL)
                    {
                        clearFilter();
                        refreshData();
                    }
                }
            });
            filter.addModifyListener(new ModifyListener()
            {

                @Override
                public void modifyText(ModifyEvent event)
                {
                    if (active.get())
                        refreshData();

                }
            });
        }
        else
        {
            comboControl = createList(this);

            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            comboControl.setLayoutData(gridData);
            super.setFont(comboControl.getFont());
            actionControl = createLabelButtonControl(this);
        }

        setActionVisible(false);
    }

    public void setActionVisible(boolean visible)
    {
        if (actionControl != null && !actionControl.isDisposed())
        {
            actionControl.setVisible(visible);
            GridData gridData = (GridData) actionControl.getLayoutData();
            gridData.widthHint = visible ? actionWidthHint : 0;
            layout();
        }
    }

    public abstract List createList(Composite parent);

    public abstract Control createActionLabel(Composite parent);

    public abstract void refreshData();

    private Control createLabelButtonControl(Composite parent)
    {

        final Control labelButton = createActionLabel(parent);
        GridData gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
        actionWidthHint = gridData.widthHint;
        gridData.horizontalIndent = 2;
        labelButton.setLayoutData(gridData);
        labelButton.setBackground(parent.getBackground());

        return labelButton;
    }

    protected boolean hasFilter()
    {
        return false;
    }

    public String getFilterText()
    {

        if (filter != null && !filter.isDisposed())
        {
            return filter.getText();
        }

        return "";
    }

    public void clearFilter()
    {
        if (filter != null && !filter.isDisposed())
        {
            try
            {
                active.set(false);
                filter.setText("");
            }
            finally
            {
                active.set(true);
            }
        }
    }

    public List getListControl()
    {
        return comboControl;
    }

    public Control getActionControl()
    {
        return actionControl;
    }

    public String getText()
    {
        if (comboControl != null && !comboControl.isDisposed())
        {
            String[] selection = comboControl.getSelection();
            return (selection != null && selection.length > 0) ? selection[0] : "";
        }
        return "";
    }

    public void setText(String text)
    {
        if (comboControl != null && !comboControl.isDisposed())
        {
            comboControl.setSelection(new String[] { text });
        }
    }

    @Override
    public void setBackground(Color color)
    {
        if (comboControl != null && !comboControl.isDisposed())
        {
            comboControl.setBackground(color);
        }
        if (actionControl != null && !actionControl.isDisposed())
        {
            actionControl.setBackground(color);
        }
        super.setBackground(color);
    }

    @Override
    public void addKeyListener(KeyListener listener)
    {
        comboControl.addKeyListener(listener);
    }

    @Override
    public void setData(String key, Object value)
    {
        if (EJ_RWT.ACTIVE_KEYS.equals(key))
        {
            comboControl.setData(key, value);
        }
        else
        {
            super.setData(key, value);
        }
    }

    @Override
    public void setData(Object data)
    {
        comboControl.setData(data);
    }

    @Override
    public Object getData(String key)
    {
        if (EJ_RWT.ACTIVE_KEYS.equals(key))
        {
            return comboControl.getData(key);
        }

        return super.getData(key);
    }

    @Override
    public void addFocusListener(FocusListener listener)
    {
        comboControl.addFocusListener(listener);
    }
}
