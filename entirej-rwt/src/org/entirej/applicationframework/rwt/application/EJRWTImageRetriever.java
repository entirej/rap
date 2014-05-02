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
package org.entirej.applicationframework.rwt.application;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

public class EJRWTImageRetriever
{
    public static transient Map<String, Image> _imageBucket         = new HashMap<String, Image>();
    private static String                      ICONS                = "icons/";
    public static final String                 IMG_QUERY            = ICONS + "search.png";
    public static final String                 IMG_FIND_LOV         = ICONS + "find_lov.png";
    public static final String                 IMG_DATE_SELECTION   = ICONS + "date_selection.png";
    public static final String                 IMG_CLOSE_FORM       = ICONS + "closeForm.png";
    public static final String                 IMG_DELETE           = ICONS + "remove.png";
    public static final String                 IMG_INSERT           = ICONS + "add.png";
    public static final String                 IMG_LOV              = ICONS + "reload.png";
    public static final String                 IMG_NEXT_PAGE        = ICONS + "nextPage.png";
    public static final String                 IMG_PREV_PAGE        = ICONS + "prevPage.png";
    public static final String                 IMG_NEXT_RECORD      = ICONS + "down.png";
    public static final String                 IMG_PREV_RECORD      = ICONS + "up.png";
    public static final String                 IMG_SAVE             = ICONS + "save.png";
    public static final String                 IMG_EDIT             = ICONS + "edit.png";
    public static final String                 IMG_CHECK_SELECTED   = ICONS + "check-selected.png";
    public static final String                 IMG_CHECK_UNSELECTED = ICONS + "check-unselected.png";
    public static final String                 IMG_INFO             = ICONS + "info.png";
    public static final String                 IMG_WARNING          = ICONS + "warning.png";
    public static final String                 IMG_ERROR            = ICONS + "error.png";
    public static final String                 IMG_CLOSE            = ICONS + "close.png";
    private static EJRWTGraphicsProvider       graphicsProvider;

    private EJRWTImageRetriever()
    {

    }

    public static Image get(String name)
    {
        if (name == null || name.trim().length() == 0)
        {
            return null;
        }

        Image image = _imageBucket.get(name);
        if (image == null)
        {
            _imageBucket.put(name, image = create(name));
        }
        return image;
    }

    public static void setGraphicsProvider(EJRWTGraphicsProvider graphicsProvider)
    {
        EJRWTImageRetriever.graphicsProvider = graphicsProvider;
    }

    private static Image create(String name)
    {
        ClassLoader loader = EJRWTImageRetriever.class.getClassLoader();
        return graphicsProvider.getImage(name, loader);
    }

    public static EJRWTGraphicsProvider getGraphicsProvider()
    {
        if (graphicsProvider == null)
        {
            throw new IllegalStateException("EJRWTImageRetriever not initialized");
        }
        return graphicsProvider;
    }

}
