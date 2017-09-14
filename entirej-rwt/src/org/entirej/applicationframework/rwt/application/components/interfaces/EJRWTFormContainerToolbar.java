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
package org.entirej.applicationframework.rwt.application.components.interfaces;

import java.io.Serializable;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.renderers.eventhandlers.EJBlockFocusedListener;
import org.entirej.framework.core.renderers.eventhandlers.EJFormEventListener;
import org.entirej.framework.core.renderers.eventhandlers.EJItemFocusListener;
import org.entirej.framework.core.renderers.eventhandlers.EJNewRecordFocusedListener;
import org.entirej.framework.core.renderers.eventhandlers.EJScreenItemValueChangedListener;

public interface EJRWTFormContainerToolbar extends EJBlockFocusedListener, EJScreenItemValueChangedListener, EJItemFocusListener, EJNewRecordFocusedListener,
        EJFormEventListener, Serializable
{
    /**
     * Returns the form to which this toolbar belongs
     * 
     * @return The form to which this toolbar belongs
     */
    public EJInternalForm getForm();

    /**
     * Return the {@link Control} component for this toolbar
     * 
     * @return The {@link Control} component
     */
    public Control getComponent();

    public Control createComponent(Composite parent);

    /**
     * Disables this toolbar
     */
    public void disable();

    /**
     * Returns the current focused item
     * 
     * @return The currently focused item or <code>null</code> if no item is
     *         currently focused
     */
    public EJScreenItemController getFocusedItem();

    /**
     * Instructs the toolbar to synchronize itself according to the focused
     * block
     * 
     * @param focusedBlock
     *            The block to synchronize with
     */
    public void synchronize(EJBlockController focusedBlock);
}
