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
package org.entirej.applicationframework.rwt.application.interfaces;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface EJRWTApplicationComponent extends EJRWTFormOpenedListener, EJRWTFormClosedListener, EJRWTFormSelectedListener
{
    /**
     * Called if this form component is used to choose forms to open
     * <p>
     * The form component can be used to choose forms to be opened by the
     * framework. If this is the case, then the component must call this method
     * to inform all listeners that a form has been chosen. If this component is
     * not used to choose forms then this method need not be implemented
     * 
     * @param formChosenListener
     *            The listener to add
     */
    public void addFormChosenListener(EJRWTFormChosenListener formChosenListener);

    /**
     * Removes a {@link EJRWTFormChosenListener} from this component
     * 
     * @param formChosenListener
     *            The listener to add
     * 
     * @see #addFormChosenListener(EJRWTFormChosenListener)
     */
    public void removeFormChosenListener(EJRWTFormChosenListener formChosenListener);

    /**
     * Returns the actual component that will be added to the application
     * container
     * <p>
     * The component is the actual GUI widget that will be displayed
     * 
     * @return The GUI widget that will be displayed
     */
    public Control createComponent(Composite parent);
}
