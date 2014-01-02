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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public abstract class EJRWTAbstractDialog extends Dialog implements Serializable
{
    private Shell _parent;
    private int   _selectedButtonId = -1;

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

    @Override
    protected void buttonPressed(final int buttonId)
    {
        _selectedButtonId = buttonId;
        super.buttonPressed(buttonId);
    }

    public void setButtonEnable(final int buttonId, boolean enabled)
    {
        getButton(buttonId).setEnabled(enabled);
    }

    public void validate()
    {

    }

    public void canceled()
    {

    }

    @Override
    public void create()
    {
        super.create();
        getShell().addShellListener(new ShellAdapter()
        {
            @Override
            public void shellClosed(ShellEvent event)
            {
                if (_selectedButtonId == -1)
                {
                    canceled();
                }
            }
        });
    }
}
