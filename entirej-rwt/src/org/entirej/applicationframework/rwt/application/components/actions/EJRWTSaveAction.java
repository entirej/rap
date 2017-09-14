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
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.internal.EJInternalForm;

public class EJRWTSaveAction extends EJRWTAction
{
    private EJRWTFormContainerToolbar _toolbar;
    private EJEditableBlockController _currentBlock;

    public EJRWTSaveAction(EJRWTFormContainerToolbar toolbar)
    {
        _toolbar = toolbar;
        setText("Save changes");
        setDescription("Saves all open changes");
        setToolTipText("Save changes");
        setAccelerator(SWT.CTRL + 'S');
    }

    public void synchronize(EJEditableBlockController currentBlock)
    {
        _currentBlock = currentBlock;
        if (_currentBlock != null && _currentBlock.getForm().isDirty() || _toolbar.getForm() != null && _toolbar.getForm().isDirty())
        {
            setEnabled(true);
        }
        else
        {
            setEnabled(false);
        }
    }

    @Override
    public void run()
    {
        EJInternalForm form;
        if (_currentBlock == null)
        {
            form = _toolbar.getForm();
        }
        else
        {
            form = _currentBlock.getForm();
        }

        if (form != null)
        {
            try
            {
                form.saveChanges();
            }
            catch (Exception e)
            {
                form.getFrameworkManager().getApplicationManager().handleException(e);
            }
            _toolbar.synchronize(_currentBlock != null ? _currentBlock : form.getFocusedBlock().getBlockController());
        }
    }

    @Override
    public Image getImage()
    {
        return EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_SAVE);
    }
}
