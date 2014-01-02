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
package org.entirej.applicationframework.rwt.application.components.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.components.interfaces.EJRWTFormContainerToolbar;
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.internal.EJInternalForm;

public class EJRWTQueryAction extends EJRWTAction
{
    private EJRWTFormContainerToolbar _toolbar;
    private EJEditableBlockController _currentBlock;

    public EJRWTQueryAction(final EJRWTFormContainerToolbar toolbar)
    {
        _toolbar = toolbar;
        setText("Query");
        setDescription("Enter a query");
        setToolTipText("Enter a query");
        setAccelerator(SWT.CTRL + 'Q');

    }

    @Override
    public Image getImage()
    {
        return EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_QUERY);
    }

    public void synchronize(final EJEditableBlockController currentBlock)
    {
        _currentBlock = currentBlock;
        if (!canExecute(currentBlock))
        {
            setEnabled(false);
            return;
        }

        setEnabled(true);
    }

    public static boolean canExecute(final EJEditableBlockController currentBlock)
    {
        if (currentBlock == null || currentBlock.preventMasterlessOperations())
        {
            return false;
        }

        if (!currentBlock.getProperties().isQueryAllowed())
        {
            return false;
        }

        if (currentBlock.getProperties().getBlockService() == null)
        {
            return false;
        }

        if (currentBlock.getQueryScreenRenderer() == null)
        {
            return false;

        }
        return true;
    }

    @Override
    public void run()
    {

        if (_currentBlock == null)
        {
            EJInternalForm form = _toolbar.getForm();
            EJInternalEditableBlock focusedBlock = form.getFocusedBlock();
            if (form != null && focusedBlock != null)
            {
                try
                {
                    focusedBlock.enterQuery();
                    focusedBlock.getManagedRenderer().gainFocus();
                }
                catch (Exception e)
                {
                    form.getFrameworkManager().getApplicationManager().handleException(e);
                }
                _toolbar.synchronize(form.getFocusedBlock().getBlockController());
            }
        }
        else
        {
            try
            {
                _currentBlock.enterQuery();
                _currentBlock.getBlock().getManagedRenderer().gainFocus();
            }
            catch (Exception e)
            {
                _currentBlock.getFrameworkManager().getApplicationManager().handleException(e);
            }
            _toolbar.synchronize(_currentBlock);
        }
    }
}
