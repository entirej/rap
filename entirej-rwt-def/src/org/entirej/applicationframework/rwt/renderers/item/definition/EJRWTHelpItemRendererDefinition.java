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
package org.entirej.applicationframework.rwt.renderers.item.definition;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevItemRendererDefinition;

public class EJRWTHelpItemRendererDefinition implements EJDevItemRendererDefinition
{
   

    public EJRWTHelpItemRendererDefinition()
    {
    }

    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.rwt.renderers.item.EJRWTHelpItemRenderer";
    }

    public boolean canExecuteActionCommand()
    {
        return true;
    }

    public void loadValidValuesForProperty(EJFrameworkExtensionProperties frameworkExtensionProperties, EJPropertyDefinition propertyDefinition)
    {
        // no impl
    }

    public void propertyChanged(EJPropertyDefinitionListener propertyDefListener, EJFrameworkExtensionProperties properties, String propertyName)
    {
        // no impl
    }

    public EJPropertyDefinitionGroup getItemPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("help Item Renderer");

        
        return mainGroup;
    }

    @Override
    public EJDevItemRendererDefinitionControl getItemControl(EJDevScreenItemDisplayProperties itemDisplayProperties, Composite parent, FormToolkit toolkit)
    {
        Button button = new Button(parent, SWT.TOGGLE);
        button.setImage(JFaceResources.getImage("dialog_help_image"));
        EJDevItemRendererDefinitionControl control = new EJDevItemRendererDefinitionControl(itemDisplayProperties, button, false);

        return control;
    }

    @Override
    public Control getLabelControl(EJDevScreenItemDisplayProperties itemDisplayProperties, Composite parent, FormToolkit toolkit)
    {
        // A button implements no label
        return null;
    }

    public boolean isReadOnly()
    {
        return true;
    }
}
