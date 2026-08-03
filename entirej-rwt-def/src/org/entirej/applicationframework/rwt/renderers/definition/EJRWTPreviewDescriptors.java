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
package org.entirej.applicationframework.rwt.renderers.definition;

import org.entirej.applicationframework.rwt.renderers.screen.definition.interfaces.EJRWTScreenRendererDefinitionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJMainScreenProperties;
import org.entirej.framework.dev.renderer.definition.EJDevPreviewDescriptor;
import org.entirej.framework.dev.renderer.definition.EJDevPreviewKind;

public final class EJRWTPreviewDescriptors
{
    private EJRWTPreviewDescriptors()
    {
    }

    public static EJDevPreviewDescriptor descriptor(EJDevPreviewKind kind)
    {
        return EJDevPreviewDescriptor.create(kind, null, null, null, 0, 0);
    }

    public static EJDevPreviewDescriptor block(EJDevPreviewKind kind, EJMainScreenProperties mainScreenProperties)
    {
        int width = mainScreenProperties == null ? 0 : mainScreenProperties.getWidth();
        int height = mainScreenProperties == null ? 0 : mainScreenProperties.getHeight();
        return EJDevPreviewDescriptor.create(kind, null, null, null, width, height);
    }

    public static EJDevPreviewDescriptor screen(EJDevPreviewKind kind, String fallbackLabel, EJFrameworkExtensionProperties rendererProperties)
    {
        int width = rendererProperties == null ? 0 : rendererProperties.getIntProperty(EJRWTScreenRendererDefinitionProperties.WIDTH, 300);
        int height = rendererProperties == null ? 0 : rendererProperties.getIntProperty(EJRWTScreenRendererDefinitionProperties.HEIGHT, 300);
        String title = rendererProperties == null ? null : rendererProperties.getStringProperty(EJRWTScreenRendererDefinitionProperties.TITLE);
        return EJDevPreviewDescriptor.create(kind, null, value(title, fallbackLabel), null, width, height);
    }

    private static String value(String... values)
    {
        for (String value : values)
        {
            if (value != null && value.trim().length() > 0)
            {
                return value;
            }
        }
        return null;
    }
}
