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
package org.entirej.applicationframework.rwt.application.components.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.components.interfaces.EJRWTFormContainerToolbar;

public class EJRWTExitAction extends EJRWTAction
{
    private EJRWTFormContainerToolbar _toolbar;

    public EJRWTExitAction(EJRWTFormContainerToolbar toolbar)
    {
        _toolbar = toolbar;
        setText("Exit");
        setDescription("Exit the application");
        setToolTipText("Exit");
        setAccelerator(SWT.CTRL + 'E');
    }

    @Override
    public void run()
    {
        super.run();
    }

    @Override
    public Image getImage()
    {
        return EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_DELETE);
    }
}
