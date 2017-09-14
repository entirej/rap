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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.interfaces.EJItemRenderer;

public interface EJRWTAppItemRenderer extends EJItemRenderer
{
    /**
     * Returns the object that will be displayed upon the
     * <code>BlockRenderer</code>
     * <p>
     * The object is dependent upon the style of renderers used i.e. if standard
     * java Swing is used as the application renderer style then a JComponent
     * will be returned if the application renderer style is Canoos ULC then an
     * ULCComponent will be returned
     * <p>
     * The block renderer will have to cast the object to the correct class if
     * an incorrect type is given then, a <code>ClassCastException</code> will
     * be thrown
     * 
     * @return The object representing the GUI component for the corresponding
     *         application style
     */
    @Override
    public Control getGuiComponent();

    /**
     * Each <code>ItemRenderer</code> must create a label object, whether or not
     * the label object will be displayed and how it will be displayed is up to
     * the <code>BlockRenderer</code> within which it will be displayed
     * <p>
     * If the <code>ItemRenderer</code> is set to <code>displayed=false</code>
     * then the label object must also be hidden.
     * 
     * @return The object representing the GUI components label
     */
    public Label getGuiComponentLabel();

    public void createComponent(Composite composite);

    public void createLable(Composite composite);

    public boolean useFontDimensions();

    public enum VALUE_CASE
    {
        UPPER, LOWER, DEFAULT
    }

    public void setInitialVisualAttribute(EJCoreVisualAttributeProperties va);

    ColumnLabelProvider createColumnLabelProvider(EJScreenItemProperties item, EJScreenItemController controller);

    EJRWTAbstractTableSorter getColumnSorter(EJScreenItemProperties item, EJScreenItemController controller);
    
    String formatValue(Object obj);

}
