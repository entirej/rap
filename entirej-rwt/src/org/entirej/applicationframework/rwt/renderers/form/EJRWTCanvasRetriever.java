/*******************************************************************************
 * Copyright 2013 Mojave Innovations GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Contributors: Mojave Innovations GmbH - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.renderers.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.entirej.framework.core.enumerations.EJCanvasType;
import org.entirej.framework.core.properties.EJCoreFormProperties;
import org.entirej.framework.core.properties.containers.interfaces.EJCanvasPropertiesContainer;
import org.entirej.framework.core.properties.interfaces.EJCanvasProperties;
import org.entirej.framework.core.properties.interfaces.EJDrawerPageProperties;
import org.entirej.framework.core.properties.interfaces.EJStackedPageProperties;
import org.entirej.framework.core.properties.interfaces.EJTabPageProperties;

public class EJRWTCanvasRetriever
{

    public static Collection<EJCanvasProperties> retriveAllFormCanvases(EJCoreFormProperties formProperties)
    {
        ArrayList<EJCanvasProperties> formCanvasProperties = new ArrayList<EJCanvasProperties>();

        Collection<EJCanvasProperties> retriveAllCanvases = retriveAllCanvases(formProperties);
        for (EJCanvasProperties canvasProperties : retriveAllCanvases)
        {
            if (canvasProperties.getType() == EJCanvasType.FORM)
            {
                formCanvasProperties.add(canvasProperties);
            }
        }
        return formCanvasProperties;
    }

    public static Collection<EJCanvasProperties> retriveAllCanvases(EJCoreFormProperties formProperties)
    {
        ArrayList<EJCanvasProperties> canvasList = new ArrayList<EJCanvasProperties>();

        addCanvasesFromContainer(formProperties, formProperties.getCanvasContainer(), canvasList);

        return canvasList;
    }

    /**
     * Checks to see if there is already a canvas existing with the given name
     * 
     * @param formProperties
     *            The form to check
     * @param name
     *            The name of the canvas to check for
     * @return <code>true</code> if the canvas exists, otherwise
     *         <code>false</code>
     */
    public static boolean canvasExists(EJCoreFormProperties formProperties, String name)
    {
        ArrayList<EJCanvasProperties> canvasList = new ArrayList<EJCanvasProperties>();
        addCanvasesFromContainer(formProperties, formProperties.getCanvasContainer(), canvasList);

        for (EJCanvasProperties canvas : canvasList)
        {
            if (canvas.getName().equals(name))
            {
                return true;
            }
        }
        return false;
    }
    public static EJCanvasProperties getCanvas(EJCoreFormProperties formProperties, String name)
    {
        ArrayList<EJCanvasProperties> canvasList = new ArrayList<EJCanvasProperties>();
        addCanvasesFromContainer(formProperties, formProperties.getCanvasContainer(), canvasList);
        
        for (EJCanvasProperties canvas : canvasList)
        {
            if (canvas.getName().equals(name))
            {
                return canvas;
            }
        }
        return null;
    }

    private static void addCanvasesFromContainer(EJCoreFormProperties formProperties, EJCanvasPropertiesContainer container, ArrayList<EJCanvasProperties> canvasList)
    {
        Iterator<EJCanvasProperties> allCanvases = container.getAllCanvasProperties().iterator();
        while (allCanvases.hasNext())
        {
            EJCanvasProperties canvas = allCanvases.next();
            canvasList.add(canvas);
            if (canvas.getType() == EJCanvasType.POPUP)
            {
                addCanvasesFromContainer(formProperties, canvas.getPopupCanvasContainer(), canvasList);
            }
            else if (canvas.getType() == EJCanvasType.TAB)
            {
                Iterator<EJTabPageProperties> allTabPages = canvas.getTabPageContainer().getAllTabPageProperties().iterator();
                while (allTabPages.hasNext())
                {
                    addCanvasesFromContainer(formProperties, allTabPages.next().getContainedCanvases(), canvasList);
                }
            }
            else if (canvas.getType() == EJCanvasType.DRAWER)
            {
                Iterator<EJDrawerPageProperties> allTabPages = canvas.getDrawerPageContainer().getAllDrawerPageProperties().iterator();
                while (allTabPages.hasNext())
                {
                    addCanvasesFromContainer(formProperties, allTabPages.next().getContainedCanvases(), canvasList);
                }
            }
            else if (canvas.getType() == EJCanvasType.STACKED)
            {
                Iterator<EJStackedPageProperties> allStackedPages = canvas.getStackedPageContainer().getAllStackedPageProperties().iterator();
                while (allStackedPages.hasNext())
                {
                    addCanvasesFromContainer(formProperties, allStackedPages.next().getContainedCanvases(), canvasList);
                }
            }
            else if (canvas.getType() == EJCanvasType.GROUP)
            {
                addCanvasesFromContainer(formProperties, canvas.getGroupCanvasContainer(), canvasList);
            }

            else if (canvas.getType() == EJCanvasType.SPLIT)
            {
                addCanvasesFromContainer(formProperties, canvas.getSplitCanvasContainer(), canvasList);
            }
        }
    }

}
