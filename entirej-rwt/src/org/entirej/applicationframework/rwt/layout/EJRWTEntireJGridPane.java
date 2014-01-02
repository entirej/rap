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
/**
 * 
 */
package org.entirej.applicationframework.rwt.layout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public class EJRWTEntireJGridPane extends Composite
{
    private String _name;

    public EJRWTEntireJGridPane(Composite parent, int cols)
    {
        super(parent, SWT.NO_FOCUS);
        GridLayout layout = new GridLayout(cols, false);
        setLayout(layout);
    }

    public EJRWTEntireJGridPane(Composite parent, int cols, int style)
    {
        super(parent, style | SWT.NO_FOCUS);
        GridLayout layout = new GridLayout(cols, false);
        layout.marginBottom = 0;
        layout.marginHeight = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginWidth = 0;

        setLayout(layout);
    }

    @Override
    public GridLayout getLayout()
    {
        return (GridLayout) super.getLayout();
    }

    @Override
    public void setLayout(Layout layout)
    {
        if (!(layout instanceof GridLayout))
        {
            throw new IllegalArgumentException("Only GridLayout supported");
        }
        super.setLayout(layout);
    }

    public String getPaneName()
    {
        return _name;
    }

    public void setPaneName(String name)
    {
        _name = name;
    }

    public void add(EJRWTGridLayoutComponent component)
    {
        component.createComposite(this);
    }

    public void add(EJRWTGridLayoutComponent comp1, EJRWTGridLayoutComponent comp2)
    {
        add(comp1);
        add(comp2);
    }

    public void cleanLayout()
    {
        cleanLayoutHorizontal();
        cleanLayoutVertical();
    }

    public void cleanLayoutHorizontal()
    {
        GridLayout layout = getLayout();
        layout.marginWidth = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
    }

    public void cleanLayoutVertical()
    {
        GridLayout layout = getLayout();
        layout.marginHeight = 0;
        layout.marginBottom = 0;
        layout.marginTop = 0;
    }
}
