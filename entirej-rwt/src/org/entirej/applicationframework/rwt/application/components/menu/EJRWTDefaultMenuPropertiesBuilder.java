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
package org.entirej.applicationframework.rwt.application.components.menu;

import java.util.List;

import org.entirej.framework.core.data.controllers.EJTranslationController;
import org.entirej.framework.core.interfaces.EJApplicationManager;
import org.entirej.framework.core.properties.EJCoreMenuContainer;
import org.entirej.framework.core.properties.EJCoreMenuLeafActionProperties;
import org.entirej.framework.core.properties.EJCoreMenuLeafContainer;
import org.entirej.framework.core.properties.EJCoreMenuLeafFormProperties;
import org.entirej.framework.core.properties.EJCoreMenuLeafProperties;
import org.entirej.framework.core.properties.EJCoreMenuLeafSpacerProperties;
import org.entirej.framework.core.properties.EJCoreMenuProperties;
import org.entirej.framework.core.properties.EJCoreProperties;

/**
 * Build the menu properties by reading the form files defined in the
 * "Form Package" in EntirejApplication.properties file.<br>
 * Each package will be a menu and each file will be a menu item.<br>
 */
public class EJRWTDefaultMenuPropertiesBuilder
{
    private EJRWTDefaultMenuPropertiesBuilder()
    {
    }

    /**
     * Build a menu item properties based of the form packages defined in the
     * EntireJApplication.properties file and the forms contained in those
     * packages
     * 
     * @param applicationManager
     * 
     * @return a MenuItemProperties for opening the forms from the defined
     *         packages
     */
    public static EJRWTMenuTreeRoot buildMenuProperties(EJApplicationManager applicationManager, String menuId)
    {
        EJRWTMenuTreeRoot root = new EJRWTMenuTreeRoot();

        if (menuId != null)
        {
            EJTranslationController translationController = applicationManager.getFrameworkManager().getTranslationController();
            EJCoreMenuContainer menuContainer = EJCoreProperties.getInstance().getMenuContainer();
            EJCoreMenuProperties menuProperties = menuContainer.getMenuProperties(menuId);
            if (menuProperties != null)
            {
                root.setActionProcessorClassName(menuProperties.getActionProcessorClassName());
                translationController.translateMenuProperties(menuProperties);
                List<EJCoreMenuLeafProperties> leaves = menuProperties.getLeaves();
                for (EJCoreMenuLeafProperties leafProperties : leaves)
                {
                    addEJCoreMenuLeafProperties(root, leafProperties);
                }
                return root;
            }

        }

        return root;
    }

    private static void addEJCoreMenuLeafProperties(EJRWTMenuTreeElement parent, EJCoreMenuLeafProperties leafProperties)
    {

        if (leafProperties instanceof EJCoreMenuLeafContainer)
        {
            EJRWTMenuTreeElement subMenu = parent.addSubTreeElement(leafProperties.getDisplayName(), null, leafProperties.getIconName());

            EJCoreMenuLeafContainer container = (EJCoreMenuLeafContainer) leafProperties;
            for (EJCoreMenuLeafProperties subLeafProperties : container.getLeaves())
            {
                addEJCoreMenuLeafProperties(subMenu, subLeafProperties);
            }
            return;
        }
        if (leafProperties instanceof EJCoreMenuLeafSpacerProperties)
        {
            parent.addSeparatorItem();
            return;
        }
        if (leafProperties instanceof EJCoreMenuLeafFormProperties)
        {
            EJCoreMenuLeafFormProperties formLeaf = (EJCoreMenuLeafFormProperties) leafProperties;

            String actionCommand = formLeaf.getFormName();

            parent.addFormMenuItem(formLeaf.getDisplayName(), formLeaf.getHint(), actionCommand, formLeaf.getIconName());
            return;
        }
        if (leafProperties instanceof EJCoreMenuLeafActionProperties)
        {
            EJCoreMenuLeafActionProperties action = (EJCoreMenuLeafActionProperties) leafProperties;

            String actionCommand = action.getMenuAction();

            parent.addActionMenuItem(action.getDisplayName(), action.getHint(), actionCommand, action.getIconName());
            return;
        }
    }
}
