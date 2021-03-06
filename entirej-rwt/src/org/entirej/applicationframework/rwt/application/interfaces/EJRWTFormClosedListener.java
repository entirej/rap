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

import org.entirej.framework.core.internal.EJInternalForm;

/**
 * Listens for closed form events
 * <p>
 * All registered listeners will be notified when a form is closed
 */
public interface EJRWTFormClosedListener
{
    /**
     * called when a new form is opened
     * 
     * @param closedForm
     *            The form that was opened
     */
    public void fireFormClosed(EJInternalForm closedForm);
}
