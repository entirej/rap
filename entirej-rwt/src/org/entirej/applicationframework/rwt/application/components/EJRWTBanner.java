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
package org.entirej.applicationframework.rwt.application.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTAppComponentRenderer;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTTextItemRendererDefinitionProperties;
import org.entirej.framework.core.data.controllers.EJApplicationLevelParameter;
import org.entirej.framework.core.data.controllers.EJApplicationLevelParameter.ParameterChangedListener;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;

public class EJRWTBanner implements EJRWTAppComponentRenderer
{

    public static final String IMAGE_PATH                = "IMAGE_PATH";
    public static final String IMAGE_PARAM                = "IMAGE_PARAM";
    public static final String PROPERTY_ALIGNMENT        = "ALIGNMENT";
    public static final String PROPERTY_ALIGNMENT_LEFT   = "LEFT";
    public static final String PROPERTY_ALIGNMENT_RIGHT  = "RIGHT";
    public static final String PROPERTY_ALIGNMENT_CENTER = "CENTER";
    private Label              canvas;

    @Override
    public Control getGuiComponent()
    {
        return canvas;
    }

    protected int getComponentStyle(String alignmentProperty, int style)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJRWTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_LEFT))
            {
                style = style | SWT.LEFT;
            }
            else if (alignmentProperty.equals(EJRWTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_RIGHT))
            {
                style = style | SWT.RIGHT;
            }
            else if (alignmentProperty.equals(EJRWTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_CENTER))
            {
                style = style | SWT.CENTER;
            }
        }
        return style;
    }

    @Override
    public void createContainer(final EJRWTApplicationManager manager, Composite parent, final EJFrameworkExtensionProperties rendererprop)
    {
        canvas = new Label(parent, getComponentStyle(rendererprop.getStringProperty(PROPERTY_ALIGNMENT), SWT.NONE));
        String imagePath = null;
        if (rendererprop != null)
        {
            
            String paramName = rendererprop.getStringProperty(IMAGE_PARAM);
            if(paramName!=null && paramName.length()>0)
            {
                
                final EJApplicationLevelParameter applicationLevelParameter = manager.getApplicationLevelParameter(paramName);
                if(applicationLevelParameter!=null)
                {
                    Object value = applicationLevelParameter.getValue();
                    imagePath = (String) value;
                    if(imagePath!=null)
                    {
                         updateImage(manager, imagePath);
                    }
                    else
                    {
                        imagePath = rendererprop.getStringProperty(IMAGE_PATH);
                        updateImage(manager, imagePath);
                    }
                   
                    applicationLevelParameter.addParameterChangedListener(new ParameterChangedListener()
                    {
                        
                        @Override
                        public void parameterChanged(String parameterName, Object oldValue, Object newValue)
                        {
                            
                            
                            if(newValue!=null)
                            {
                                 updateImage(manager,(String) newValue);
                            }
                            else
                            {
                               
                                updateImage(manager, rendererprop.getStringProperty(IMAGE_PATH));
                            }
                            
                        }
                    });
                }
                else
                {
                    imagePath = rendererprop.getStringProperty(IMAGE_PATH);
                    updateImage(manager, imagePath);
                }
                
            }
            
            
           
        }

        
    }

    private void updateImage(EJRWTApplicationManager manager, String imagePath)
    {
        if (imagePath != null && !imagePath.isEmpty())
        {
            try
            {
                final Image image = EJRWTImageRetriever.get(imagePath);
                if (image != null && !canvas.isDisposed())
                {
                    canvas.setImage(image);
                }
                else
                {
                    throw new IllegalArgumentException("Image not found at : " + imagePath);
                }
            }
            catch (Exception e)
            {
                manager.getFrameworkManager().handleException(e);
            }
        }
        else
        {
            canvas.setImage(null);
        }
    }
}
