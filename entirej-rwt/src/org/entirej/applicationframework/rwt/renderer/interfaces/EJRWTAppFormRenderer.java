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
package org.entirej.applicationframework.rwt.renderer.interfaces;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.entirej.framework.core.renderers.interfaces.EJFormRenderer;

public interface EJRWTAppFormRenderer extends EJFormRenderer
{
    void create(Composite parent);
    void init();
    

    /**
     * Returns the GUI object that is responsible for the display of this form
     * <p>
     * The object will need to be casted to the correct component type as
     * defined by the application renderer style. I.e. if the java Swing style
     * is being used then a JComponent will be returned, if Swing style is being
     * used, then an Component will be returned.
     * <p>
     * See the application style documentation for more details
     * 
     * @return The object representing this forms GUI object
     */
    @Override
    public Control getGuiComponent();

}
 