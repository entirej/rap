/*******************************************************************************
 * Copyright 2013 Mojave Innovations GmbH
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
 *     Mojave Innovations GmbH - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package org.entirej.applicationframework.rwt.application.components.menu;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTAppComponentRenderer;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTApplicationComponent;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormChosenListener;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;

public class EJRWTMenuTreeComponent implements EJRWTApplicationComponent, EJRWTAppComponentRenderer
{
    private EJRWTApplicationManager _appManager;
    private EJRWTDefaultMenuBuilder _menuBuilder;
    private Control                 menuControl;
    private String                  menuId     = null;
    public static final String      MENU_GROUP = "MENU_GROUP";

    @Override
    public void fireFormOpened(EJInternalForm openedForm)
    {
        // no impl
    }

    @Override
    public void fireFormClosed(EJInternalForm closedForm)
    {
        // no impl
    }

    @Override
    public void fireFormSelected(EJInternalForm selectedForm)
    {
        // no impl
    }

    @Override
    public void addFormChosenListener(EJRWTFormChosenListener formChosenListener)
    {
        // no impl
    }

    @Override
    public void removeFormChosenListener(EJRWTFormChosenListener formChosenListener)
    {
        // no impl
    }

    @Override
    public Control createComponent(Composite parent)
    {
        _menuBuilder = new EJRWTDefaultMenuBuilder(this._appManager, parent);
        menuControl = _menuBuilder.createTreeComponent(menuId);
        return menuControl;
    }

    @Override
    public Control getGuiComponent()
    {
        return menuControl;
    }

    @Override
    public void createContainer(EJRWTApplicationManager manager, Composite parent, EJFrameworkExtensionProperties rendererprop)
    {
        _appManager = manager;
        if (rendererprop != null)
        {
            menuId = rendererprop.getStringProperty(MENU_GROUP);
        }
        createComponent(parent);
    }
}
