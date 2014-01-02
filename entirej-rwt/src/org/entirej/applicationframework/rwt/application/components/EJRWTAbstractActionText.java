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

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public abstract class EJRWTAbstractActionText extends Composite implements Serializable
{

    private static final long serialVersionUID = 3047819425055529793L;

    private final Text        textControl;
    private Control           actionControl;
    private Control           actionCustomControl;
    private int               actionWidthHint  = 0;

    public EJRWTAbstractActionText(Composite parent)
    {
        super(parent, SWT.NO_FOCUS);

        int numColumns = 1;

        numColumns = 3;

        GridLayoutFactory.swtDefaults().margins(0, 0).extendedMargins(1, 1, 1, 1).spacing(0, 2).numColumns(numColumns).applyTo(this);

        textControl = createText(this);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        textControl.setLayoutData(gridData);
        super.setFont(textControl.getFont());
        actionCustomControl = createCustomLabelButtonControl(this);
        actionControl = createLabelButtonControl(this);
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

    public abstract Text createText(Composite parent);

    public abstract Control createActionLabel(Composite parent);

    public abstract Control createCustomActionLabel(Composite parent);

    private Control createLabelButtonControl(Composite parent)
    {

        final Control labelButton = createActionLabel(parent);
        GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        actionWidthHint = gridData.widthHint;
        gridData.horizontalIndent = 2;
        labelButton.setLayoutData(gridData);
        labelButton.setBackground(parent.getBackground());

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

    public void addModifyListener(ModifyListener listener)
    {
        textControl.addModifyListener(listener);
    }

    public void removeModifyListener(ModifyListener listener)
    {
        textControl.removeModifyListener(listener);
    }

    public Text getTextControl()
    {
        return textControl;
    }

    public Control getActionControl()
    {
        return actionControl;
    }

    public String getText()
    {
        if (textControl != null && !textControl.isDisposed())
        {
            return textControl.getText();
        }
        return "";
    }

    public void setText(String text)
    {
        if (textControl != null && !textControl.isDisposed())
        {
            textControl.setText(text);
        }
    }

    @Override
    public void setBackground(Color color)
    {
        if (textControl != null && !textControl.isDisposed())
        {
            textControl.setBackground(color);
        }
        if (actionControl != null && !actionControl.isDisposed())
        {
            actionControl.setBackground(color);
        }
        setBackground(color);
    }

    @Override
    public void addKeyListener(KeyListener listener)
    {
        textControl.addKeyListener(listener);
    }

    @Override
    public void setData(String key, Object value)
    {
        if (EJ_RWT.ACTIVE_KEYS.equals(key))
        {
            textControl.setData(key, value);
        }
        else
        {
            super.setData(key, value);
        }
    }

    @Override
    public void setData(Object data)
    {
        textControl.setData(data);
    }

    @Override
    public Object getData(String key)
    {
        if (EJ_RWT.ACTIVE_KEYS.equals(key))
        {
            return textControl.getData(key);
        }
        return super.getData(key);
    }

    @Override
    public void addFocusListener(FocusListener listener)
    {
        textControl.addFocusListener(listener);
    }
}
