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
package org.entirej.applicationframework.rwt.renderers.application;

import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevAppComponentRendererDefinition;

public class EJRWTMenuTreeRendererDefinition implements EJDevAppComponentRendererDefinition
{

    public static final String MENU_GROUP = "MENU_GROUP";

    @Override
    public EJPropertyDefinitionGroup getComponentPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("MENUCONFIG");
        mainGroup.setLabel("Menu Configuration");

        EJDevPropertyDefinition menuId = new EJDevPropertyDefinition(MENU_GROUP, EJPropertyDefinitionType.MENU_GROUP);
        menuId.setLabel("Application Menu");
        menuId.setDescription("The menu that will be displayed within this component");
        mainGroup.addPropertyDefinition(menuId);
        return mainGroup;
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.rwt.application.components.menu.EJRWTMenuTreeComponent";
    }

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties arg0, EJPropertyDefinition arg1)
    {

    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener arg0, EJFrameworkExtensionProperties arg1, String arg2)
    {

    }

}
