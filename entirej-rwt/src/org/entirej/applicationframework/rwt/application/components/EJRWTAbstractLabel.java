/*******************************************************************************
 * Copyright 2013 CRESOFT AG
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
 *     CRESOFT AG - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.application.components;

import java.io.Serializable;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public abstract class EJRWTAbstractLabel extends Composite implements Serializable
{

    private static final long serialVersionUID = 3047819425055529793L;

    private final Label        labelControl;
    private Control           actionControl;
    private Control           actionCustomControl;
    private int               actionWidthHint  = 0;

    public EJRWTAbstractLabel(Composite parent,boolean empty)
    {
        super(parent, SWT.NO_FOCUS);
        setData(EJ_RWT.CUSTOM_VARIANT, "itemgroupclear");

        int numColumns = 1;

       

        if(empty)
            GridLayoutFactory.swtDefaults().margins(0, 0).extendedMargins(0, 0, 0, 0).spacing(0, 0).numColumns(1).applyTo(this);
        else
            GridLayoutFactory.swtDefaults().margins(0, 0).extendedMargins(2, 2, 3, 2).spacing(0, 2).numColumns(numColumns).applyTo(this);

        labelControl = createLabel(this);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        labelControl.setLayoutData(gridData);
        super.setFont(labelControl.getFont());
        actionCustomControl = createCustomLabelButtonControl(this);
        actionControl = createLabelButtonControl(this);
        setActionVisible(false);
    }
    
    public EJRWTAbstractLabel(Composite parent)
    {
        this(parent, false);
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

    public void setCustomActionVisible(boolean visible)
    {
        if (actionCustomControl != null && !actionCustomControl.isDisposed())
        {
            actionCustomControl.setVisible(visible);
            GridData gridData = (GridData) actionCustomControl.getLayoutData();
            gridData.widthHint = visible ? actionWidthHint : 0;
            layout();
        }
    }

    public abstract Label createLabel(Composite parent);

    public abstract Control createActionLabel(Composite parent);

    public abstract Control createCustomActionLabel(Composite parent);

    private Control createLabelButtonControl(Composite parent)
    {

        final Control labelButton = createActionLabel(parent);
        if(labelButton!=null)
        {
            GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
            actionWidthHint = gridData.widthHint;
            gridData.horizontalIndent = 2;
            labelButton.setLayoutData(gridData);
            labelButton.setBackground(parent.getBackground());
        }

        return labelButton;
    }

    private Control createCustomLabelButtonControl(Composite parent)
    {

        final Control labelButton = createCustomActionLabel(parent);
        if (labelButton != null)
        {
            GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

            gridData.horizontalIndent = 2;
            labelButton.setLayoutData(gridData);
            labelButton.setBackground(parent.getBackground());
        }
        return labelButton;
    }

  

    public Label getLabelControl()
    {
        return labelControl;
    }

    public Control getActionControl()
    {
        return actionControl;
    }

    public String getText()
    {
        if (labelControl != null && !labelControl.isDisposed())
        {
            return labelControl.getText();
        }
        return "";
    }

    public void setText(String text)
    {
        if (labelControl != null && !labelControl.isDisposed())
        {
            labelControl.setText(text);
        }
    }

    @Override
    public void setBackground(Color color)
    {
        if (labelControl != null && !labelControl.isDisposed())
        {
            labelControl.setBackground(color);
        }
        if (actionControl != null && !actionControl.isDisposed())
        {
            actionControl.setBackground(color);
        }
        super.setBackground(color);
    }
    @Override
    public void setForeground(Color color)
    {
        if (labelControl != null && !labelControl.isDisposed())
        {
            labelControl.setForeground(color);
        }
    }
    
    @Override
    public Color getForeground()
    {
        if (labelControl != null && !labelControl.isDisposed())
        {
           return labelControl.getForeground();
        }
        return super.getForeground();
    }
    
    @Override
    public void setFont(Font font)
    {
        if (labelControl != null && !labelControl.isDisposed())
        {
            labelControl.setFont(font);
        }
    }
    
    @Override
    public Font getFont()
    {
        if (labelControl != null && !labelControl.isDisposed())
        {
            return labelControl.getFont();
        }
        return super.getFont();
    }

    @Override
    public void addKeyListener(KeyListener listener)
    {
        labelControl.addKeyListener(listener);
    }

    @Override
    public void setData(String key, Object value)
    {
        if (EJ_RWT.ACTIVE_KEYS.equals(key)|| EJ_RWT.PROPERTY_CSS_KEY.equals(key))
        {
            labelControl.setData(key, value);
        }
        else
        {
            super.setData(key, value);
        }
    }

    @Override
    public void setData(Object data)
    {
        labelControl.setData(data);
    }

    @Override
    public Object getData(String key)
    {
        if (EJ_RWT.ACTIVE_KEYS.equals(key))
        {
            return labelControl.getData(key);
        }
        return super.getData(key);
    }

    @Override
    public void addFocusListener(FocusListener listener)
    {
        labelControl.addFocusListener(listener);
    }
}
