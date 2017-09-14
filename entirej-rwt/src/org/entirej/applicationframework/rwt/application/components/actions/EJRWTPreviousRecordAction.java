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
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.internal.EJInternalForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJRWTPreviousRecordAction extends EJRWTAction
{
    private static final Logger       logger = LoggerFactory.getLogger(EJRWTPreviousRecordAction.class);

    private EJRWTFormContainerToolbar _toolbar;
    private EJBlockController         _currentBlock;

    public EJRWTPreviousRecordAction(EJRWTFormContainerToolbar toolbar)
    {
        _toolbar = toolbar;
        setText("Previous Record");
        setDescription("Navigate to the previous record");
        setToolTipText("Previous Record");
        setAccelerator(SWT.CTRL + 'P');
    }

    public void synchronize(EJBlockController currentBlock)
    {
        logger.trace("START synchronize");

        _currentBlock = currentBlock;
        if (currentBlock == null || currentBlock.preventMasterlessOperations())
        {
            setEnabled(false);
            return;
        }

        if (currentBlock.getDisplayedRecordCount() > 1)
        {
            setEnabled(true);

            if (currentBlock.isFirstDisplayedRecord())
            {
                setEnabled(false);
            }
        }
        else
        {
            setEnabled(false);
        }

        logger.trace("END synchronize");
    }

    @Override
    public void run()
    {
        logger.trace("START run");

        if (_currentBlock == null)
        {
            EJInternalForm form = _toolbar.getForm();
            if (form != null && form.getFocusedBlock() != null)
            {
                try
                {
                    form.getFocusedBlock().previousRecord();
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
                _currentBlock.previousRecord();
            }
            catch (Exception e)
            {
                _currentBlock.getFrameworkManager().getApplicationManager().handleException(e);
            }
            _toolbar.synchronize(_currentBlock);
        }
        logger.trace("END run");

    }

    @Override
    public Image getImage()
    {
        return EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_PREV_RECORD);
    }
}
