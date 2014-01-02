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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class EJRWTEntireJStackedPane extends Composite
{
    private String                     _name;
    private final StackLayout          _stackLayout;
    private final Map<String, Control> _panes = new HashMap<String, Control>();
    private final List<Control>        _pages = new ArrayList<Control>();

    public EJRWTEntireJStackedPane(Composite parent)
    {
        super(parent, SWT.NO_FOCUS);
        _stackLayout = new StackLayout();
        setLayout(_stackLayout);
        _stackLayout.marginHeight = 0;
        _stackLayout.marginWidth = 0;
    }

    public EJRWTEntireJStackedPane(Composite parent, int style)
    {
        super(parent, style);
        _stackLayout = new StackLayout();
        setLayout(_stackLayout);
    }

    @Override
    public StackLayout getLayout()
    {
        return (StackLayout) super.getLayout();
    }

    @Override
    public void setLayout(Layout layout)
    {
        if (!(layout instanceof StackLayout))
        {
            throw new IllegalArgumentException("Only support StackLayout");
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

    public void add(String key, Control control)
    {
        if (_stackLayout.topControl == null)
        {
            _stackLayout.topControl = control;
        }
        _panes.put(key, control);
        _pages.add(control);
    }

    public void showPane(String pane)
    {
        Control control = _panes.get(pane);
        if (control != null)
        {
            _stackLayout.topControl = control;
        }
        layout(true);
    }

    public void remove(String key)
    {
        Control control = _panes.get(key);
        if (control != null)
        {
            int indexOf = _pages.indexOf(control) - 1;
            _panes.remove(key);
            _pages.remove(control);

            control.dispose();
            if (indexOf > 0 && indexOf < _pages.size())
            {
                _stackLayout.topControl = _pages.get(indexOf);
                layout(true);
            }
            else if (_pages.size() > 0)
            {
                _stackLayout.topControl = _pages.get(0);
                layout(true);
            }
        }
    }

    public String getActiveControlKey()
    {
        Set<Entry<String, Control>> entrySet = _panes.entrySet();
        for (Entry<String, Control> entry : entrySet)
        {
            if (entry.getValue() != null && entry.getValue().equals(_stackLayout.topControl))
            {
                return entry.getKey();
            }
        }
        return null;
    }

    public Control getActiveControl()
    {
        return _stackLayout.topControl;
    }
}
