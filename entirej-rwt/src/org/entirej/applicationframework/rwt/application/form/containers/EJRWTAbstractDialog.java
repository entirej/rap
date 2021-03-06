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
package org.entirej.applicationframework.rwt.application.form.containers;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public abstract class EJRWTAbstractDialog extends EJRWTTrayDialog implements Serializable, ITrayPane
{
    private Shell _parent;
    private int   _selectedButtonId = -1;
    private int   counter           = 0;
    private Label statusBar;

    public EJRWTAbstractDialog(final Shell parent)
    {
        super(parent);
        _parent = parent;
        setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);

        setBlockOnOpen(false);
    }

    public void centreLocation()
    {
        Rectangle shellBounds = _parent.getBounds();
        Point dialogSize = getShell().getSize();
        getShell().setLocation(shellBounds.x + (shellBounds.width - dialogSize.x) / 2, shellBounds.y + (shellBounds.height - dialogSize.y) / 2);
    }

    public abstract void createBody(Composite parent);

    @Override
    protected Control createDialogArea(final Composite parent)
    {
        Composite composite = (Composite) super.createDialogArea(parent);
        createBody(composite);
        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(final Composite parent)
    {

    }
    
    

    public void activateDialog()
    {
        // getShell().forceFocus();

        final ServerPushSession pushSession = new ServerPushSession();
        pushSession.start();
        final Display dp = Display.getDefault();
        new java.util.Timer().schedule(new java.util.TimerTask()
        {
            @Override
            public void run()
            {

                dp.asyncExec(new Runnable()
                {
                    AtomicBoolean b = new AtomicBoolean(true);

                    @Override
                    public void run()
                    {

                        pushSession.stop();
                    }
                });

            }
        }, 100);

    }
    
    @Override
    public int open()
    {
        EJRWTDialogContext.get().open(this);
        return super.open();
    }
    
    @Override
    public boolean close()
    {
        EJRWTDialogContext.get().close(this);
        return super.close();
    }

    protected Control createButtonBar(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        // create a layout with spacing and margins appropriate for the font
        // size.
        GridLayout layout = new GridLayout();
        layout.numColumns = 0; // this is incremented by createButton
        layout.makeColumnsEqualWidth = false;
        layout.marginWidth = 0;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        composite.setLayout(layout);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL);

        composite.setLayoutData(data);
        composite.setFont(parent.getFont());

        // create help control if needed
        if (isHelpAvailable())
        {
            data = new GridData(SWT.FILL, SWT.CENTER, true, false);
            composite.setLayoutData(data);
            Control helpControl = createHelpControl(composite);
            helpControl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL));
        }
        

        // Add the buttons to the button bar.
        createButtonsForButtonBar(composite);
        if (composite.getChildren().length == 0)
        {
            data.heightHint = 0;
            data.widthHint = 0;
        }
        
        
     // create status control if needed
        if (isStatusMessageSupported())
        {
            Composite statusComp = new Composite(parent, SWT.NONE);
            FillLayout fillLayout = new FillLayout();
            fillLayout.marginHeight = 0;
            fillLayout.spacing = 0;
            fillLayout.marginWidth = 0;
            statusComp.setLayout(fillLayout);
            data = new GridData(SWT.FILL, SWT.CENTER, true, false);
            data.heightHint =16;
            data.exclude = true;
            GridData dataLayout = data; 
            statusComp.setLayoutData(data);
            statusBar = new Label(statusComp,SWT.NONE) {
                
                @Override
                public void setText(String text)
                {
                    dataLayout.exclude  = text==null || text.trim().isEmpty();
                    super.setText(text);
                    parent.layout(true);
                }
                
            };
            statusBar.setData(EJ_RWT.MARKUP_ENABLED,true);
        }
        return composite;
    }

    protected  boolean isStatusMessageSupported()
    {
        return false;

    }
    
    public void setStatus(String message)
    {
        if(isStatusMessageSupported()&& !statusBar.isDisposed()) {
            statusBar.setText(message==null?"":message); 
        }
    }
    
    @Override
    protected void buttonPressed(final int buttonId)
    {
        _selectedButtonId = buttonId;
        super.buttonPressed(buttonId);
    }

    protected Button createButton(Composite parent, int id, String label, boolean defaultButton,boolean bypassDefaultForLOV)
    {

        Button button = super.createButton(parent, id, label, defaultButton);
        if (defaultButton)
        {
            
                Listener[] listeners = button.getListeners(SWT.Selection);
                for (Listener l : listeners)
                {
                    button.removeListener(SWT.Selection, l);
                    button.removeListener(SWT.DefaultSelection, l);
                    
                    Listener proxy = e->{
                        
                        Display.getDefault().asyncExec(() ->{
                        if(EJRWTDialogContext.get().isCurrent(EJRWTAbstractDialog.this))
                            l.handleEvent(e);
                        });
                    };
                    

                    button.addListener(SWT.Selection,proxy );
                    button.addListener(SWT.DefaultSelection,proxy);
                    
                }
                
        }

        return button;
    }

    public void setButtonEnable(final int buttonId, boolean enabled)
    {
        Button button = getButton(buttonId);

        if (button != null && !button.isDisposed())
        {
            button.setEnabled(enabled);
        }
    }

    public void setButtonVisible(final int buttonId, boolean enabled)
    {
        Button button = getButton(buttonId);

        if (button != null && !button.isDisposed())
        {
            button.setVisible(enabled);

        }
    }

    public void setButtonLabel(final int buttonId, String label)
    {
        Button button = getButton(buttonId);

        if (button != null && !button.isDisposed())
        {
            button.setText(label == null ? "" : label);

        }
    }

    public void validate()
    {

    }

    public void canceled()
    {
        close();
    }

    @Override
    protected boolean canHandleShellCloseEvent()
    {
        return true;
    }

    @Override
    public void handleShellCloseEvent()
    {
        canceled();
    }

    @Override
    public void create()
    {
        super.create();

    }
}
