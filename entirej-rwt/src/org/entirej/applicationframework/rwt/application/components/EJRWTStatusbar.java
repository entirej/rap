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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTAppComponentRenderer;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTApplicationStatusbar;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;

public class EJRWTStatusbar implements EJRWTApplicationStatusbar, EJRWTAppComponentRenderer
{
    private Label     status1;
    private Label     status2;
    private Composite panel;

    @Override
    public void setStatus1(String text)
    {
        if (status1 != null && !status1.isDisposed())
        {
            status1.setText(text != null ? text : "");
        }

    }

    @Override
    public void setStatus2(String text)
    {
        if (status2 != null && !status2.isDisposed())
        {
            status2.setText(text != null ? text : "");
        }
    }

    @Override
    public Control getGuiComponent()
    {
        return panel;
    }

    @Override
    public void createContainer(EJRWTApplicationManager manager, Composite parent, EJFrameworkExtensionProperties rendererprop)
    {
        int style = SWT.BORDER;
        GridLayout layout = new GridLayout(3, false);

        panel = new Composite(parent, style);
        panel.setLayout(layout);
        status1 = new Label(panel, SWT.NONE);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
        status1.setLayoutData(gridData);

        gridData = new GridData(GridData.FILL_BOTH);

        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = false;
        gridData.widthHint = 5;
        gridData.verticalIndent = 0;
        new Label(panel, SWT.SEPARATOR).setLayoutData(gridData);

        status2 = new Label(panel, SWT.NONE);
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = true;
        gridData.widthHint = 120;
        gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
        status2.setLayoutData(gridData);
    }
}
