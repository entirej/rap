/*******************************************************************************
 * Copyright 2013 CRESOFT AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Contributors: CRESOFT AG - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.renderers.html;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.entirej.applicationframework.rwt.component.EJRWTHtmlView;

public abstract class EJRWTAbstractFilteredHtml extends Composite
{
    private Text          _filterText;
    private EJRWTHtmlView _tableViewer;
    private Composite     _filterComposite;
    private Composite     _parent;
    private Composite     _treeComposite;
    private AtomicBoolean fireActions = new AtomicBoolean(true);

    public EJRWTAbstractFilteredHtml(Composite parent, int treeStyle, boolean textSelect)
    {
        super(parent, SWT.NONE);
        setData(EJ_RWT.CUSTOM_VARIANT, "itemgroupclear");

        this._parent = parent;
        init(treeStyle, textSelect);
    }

    protected void init(int treeStyle, boolean textSelect)
    {
        createControl(_parent, treeStyle, textSelect);
        setFont(_parent.getFont());
    }

    protected void createControl(Composite parent, int treeStyle, boolean textSelect)
    {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        setLayout(layout);
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        _filterComposite = new Composite(this, SWT.NONE);
        _filterComposite.setData(EJ_RWT.CUSTOM_VARIANT, getData(EJ_RWT.CUSTOM_VARIANT));

        GridLayout filterLayout = new GridLayout(2, false);
        filterLayout.marginHeight = 0;
        filterLayout.marginWidth = 0;
        _filterComposite.setLayout(filterLayout);
        _filterComposite.setFont(parent.getFont());

        createFilterControls(_filterComposite);
        _filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        _treeComposite = new Composite(this, SWT.NONE);
        GridLayout treeCompositeLayout = new GridLayout();
        treeCompositeLayout.marginHeight = 0;
        treeCompositeLayout.marginWidth = 0;
        _treeComposite.setLayout(treeCompositeLayout);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        _treeComposite.setLayoutData(data);
        createHtmlViewControl(_treeComposite, treeStyle, textSelect);
    }

    protected Composite createFilterControls(Composite parent)
    {
        createFilterText(parent);
        return parent;
    }

    protected Control createHtmlViewControl(Composite parent, int style, boolean textSelect)
    {
        _tableViewer = doCreateTableViewer(parent, style, textSelect);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        _tableViewer.setLayoutData(data);

        return _tableViewer;
    }

    protected EJRWTHtmlView doCreateTableViewer(Composite parent, int style, boolean textSelect)
    {
        return new EJRWTHtmlView(parent, style, textSelect);
    }

    protected void createFilterText(Composite parent)
    {
        _filterText = doCreateFilterText(parent);

        _filterText.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                Display display = _filterText.getDisplay();
                display.asyncExec(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (!_filterText.isDisposed())
                        {
                            _filterText.selectAll();
                        }
                    }
                });
            }

            @Override
            public void focusLost(FocusEvent e)
            {

            }
        });

        _filterText.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                // on a CR we want to transfer focus to the list
                boolean hasItems = getViewer().getText().length() > 0;
                if (hasItems && e.keyCode == SWT.ARROW_DOWN)
                {
                    _tableViewer.setFocus();
                    return;
                }
            }
        });

        _filterText.addTraverseListener(new TraverseListener()
        {
            @Override
            public void keyTraversed(TraverseEvent e)
            {
                if (e.detail == SWT.TRAVERSE_RETURN)
                {
                    e.doit = false;
                    getViewer().setFocus();
                }
            }
        });

        _filterText.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {

                textChanged();
            }
        });

        if ((_filterText.getStyle() & SWT.ICON_CANCEL) != 0)
        {
            _filterText.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetDefaultSelected(SelectionEvent e)
                {
                    if (e.detail == SWT.ICON_CANCEL)
                    {
                        clearText();
                        textChanged();
                    }
                }
            });
        }

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        // if there is no custom control use full space
        if (!doCreateCustomComponents(_filterComposite))
        {
            gridData.horizontalSpan = 2;
        }
        _filterText.setLayoutData(gridData);
    }

    protected boolean doCreateCustomComponents(Composite parent)
    {
        return false;
    }

    protected Text doCreateFilterText(Composite parent)
    {
        return new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL);
    }

    /**
     * Update the receiver after the text has changed.
     */
    protected void textChanged()
    {
        if (fireActions.get())
            filter(getFilterString());
    }

    public abstract void filter(String filter);

    public void clearText()
    {
        if (getFilterString() != null && getFilterString().trim().length() > 0)
        {
            try
            {
                fireActions.set(false);
                setFilterText("");
            }
            finally
            {
                fireActions.set(true);
            }
        }
    }

    protected void setFilterText(String string)
    {
        if (_filterText != null && !_filterText.isDisposed())
        {
            _filterText.setText(string != null ? string : "");
            selectAll();
        }
    }

    public EJRWTHtmlView getViewer()
    {
        return _tableViewer;
    }

    public Text getFilterControl()
    {
        return _filterText;
    }

    protected String getFilterString()
    {
        return _filterText != null && !_filterText.isDisposed() ? _filterText.getText() : null;
    }

    public void setInitialText(String text)
    {
        try
        {
            fireActions.set(false);
            setFilterText(text);
        }
        finally
        {
            fireActions.set(true);
        }
    }

    protected void selectAll()
    {
        if (_filterText != null)
        {
            _filterText.selectAll();
        }
    }

    public static abstract class FilteredContentProvider
    {
        protected String filter;

        public void setFilter(String filter)
        {
            this.filter = filter;
        }

        public String getFilter()
        {
            return filter;
        }

    }
}
