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

import java.io.Serializable;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class EJRWTAbstractDialog extends EJRWTTrayDialog implements Serializable
{
    private Shell _parent;
    private int   _selectedButtonId = -1;

    public EJRWTAbstractDialog(final Shell parent)
    {
        super(parent);
        _parent = parent;
        setShellStyle(getShellStyle() | SWT.RESIZE| SWT.MAX);
       
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
                    @Override
                    public void run()
                    {
                            getShell().forceFocus();
                        pushSession.stop();
                    }
                });

            }
        }, 100);
        
    }
    
    protected Control createButtonBar(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        // create a layout with spacing and margins appropriate for the font
        // size.
        GridLayout layout = new GridLayout();
        layout.numColumns = 0; // this is incremented by createButton
        layout.makeColumnsEqualWidth = true;
        layout.marginWidth = 0;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        composite.setLayout(layout);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END
                        | GridData.VERTICAL_ALIGN_CENTER);
        
        composite.setLayoutData(data);
        composite.setFont(parent.getFont());
        
        // Add the buttons to the button bar.
        createButtonsForButtonBar(composite);
        if(composite.getChildren().length==0)
        {
            data.heightHint = 0;
            data.widthHint = 0;
        }
        return composite;
}

    @Override
    protected void buttonPressed(final int buttonId)
    {
        _selectedButtonId = buttonId;
        super.buttonPressed(buttonId);
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
            button.setText(label==null?"":label);
            
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
