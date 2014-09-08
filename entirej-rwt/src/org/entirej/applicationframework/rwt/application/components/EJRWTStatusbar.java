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



import java.util.List;

import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTAppComponentRenderer;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.data.controllers.EJApplicationLevelParameter;
import org.entirej.framework.core.data.controllers.EJApplicationLevelParameter.ParameterChangedListener;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyList;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;

public class EJRWTStatusbar implements  EJRWTAppComponentRenderer
{
    
    public static final String SECTIONS                  = "SECTIONS";
    public static final String EXPAND_X                  = "EXPAND_X";
    public static final String PARAMETER                 = "PARAMETER";
    public static final String WIDTH                     = "WIDTH";
    public static final String VISUAL_ATTRIBUTE_PROPERTY = "VISUAL_ATTRIBUTE";

    public static final String PROPERTY_ALIGNMENT        = "ALIGNMENT";
    public static final String PROPERTY_ALIGNMENT_LEFT   = "LEFT";
    public static final String PROPERTY_ALIGNMENT_RIGHT  = "RIGHT";
    public static final String PROPERTY_ALIGNMENT_CENTER = "CENTER";


    private Composite panel;

    

    @Override
    public Control getGuiComponent()
    {
        return panel;
    }

    @Override
    public void createContainer(EJRWTApplicationManager manager, Composite parent, EJFrameworkExtensionProperties rendererprop)
    {
        int style = SWT.NONE;
        

        panel = new Composite(parent, style);
        
        panel.setData(EJ_RWT.CUSTOM_VARIANT, "applayout");
        
        
        final EJFrameworkExtensionPropertyList propertyList = rendererprop.getPropertyList(SECTIONS);

        if (propertyList == null)
        {
            return ;
        }
        
       
        
        List<EJFrameworkExtensionPropertyListEntry> allListEntries = propertyList.getAllListEntries();
        GridLayout layout = new GridLayout(allListEntries.size(), false);
        panel.setLayout(layout);
        for (EJFrameworkExtensionPropertyListEntry entry : allListEntries)
        {
            final Label section = new Label(panel,getComponentStyle(entry.getProperty(PROPERTY_ALIGNMENT),SWT.NONE));
            section.setData(EJ_RWT.MARKUP_ENABLED,Boolean.TRUE);
            
            String paramName = entry.getProperty(PARAMETER);
            if(paramName!=null && paramName.length()>0)
            {
                
                final EJApplicationLevelParameter applicationLevelParameter = manager.getApplicationLevelParameter(paramName);
                if(applicationLevelParameter!=null)
                {
                    Object value = applicationLevelParameter.getValue();
                    section.setText(value==null?"":value.toString());
                    applicationLevelParameter.addParameterChangedListener(new ParameterChangedListener()
                    {
                        
                        @Override
                        public void parameterChanged(String parameterName, Object oldValue, Object newValue)
                        {
                            section.setText(newValue==null?"":newValue.toString());
                            
                        }
                    });
                }
                
            }
            GridData gridData = new GridData();
            gridData.verticalAlignment = SWT.CENTER;
            gridData.grabExcessVerticalSpace = true;
            gridData.horizontalAlignment = SWT.FILL;
            section.setLayoutData(gridData);
            
            boolean expand = Boolean.valueOf(entry.getProperty(EXPAND_X));
            if(expand)
            {
                gridData.grabExcessHorizontalSpace = true;
            }
            
            String width = entry.getProperty(WIDTH);
          
            
            if(width!=null && width.length()>0)
            {
                try
                {
                    gridData.widthHint = (Integer.parseInt(width));
                }
                catch(Exception ex)
                {
                    //ignore
                }
                
            }
           
            
            //set VA 
            
            String visualAttribute = entry.getProperty(VISUAL_ATTRIBUTE_PROPERTY);
            if(visualAttribute!=null && visualAttribute.length()>0)
            {
                
                EJCoreVisualAttributeProperties va = EJCoreProperties.getInstance().getVisualAttributesContainer()
                        .getVisualAttributeProperties(visualAttribute);
                if (va != null)
                {
                    
                        Color background = EJRWTVisualAttributeUtils.INSTANCE.getBackground(va);
                        if(background != null)
                        {
                            section.setBackground(background);
                        }
                    

                    
                        Color foreground = EJRWTVisualAttributeUtils.INSTANCE.getForeground(va);
                        if(foreground != null)
                        {
                            section.setForeground(foreground);
                        }
                    

                    
                        section.setFont(EJRWTVisualAttributeUtils.INSTANCE.getFont(va, section.getFont()));
                    
                }
            }
            //
            
        }
        
        
        

    }
    
    
    protected int getComponentStyle(String alignmentProperty, int style)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(PROPERTY_ALIGNMENT_LEFT))
            {
                style = style | SWT.LEFT;
            }
            else if (alignmentProperty.equals(PROPERTY_ALIGNMENT_RIGHT))
            {
                style = style | SWT.RIGHT;
            }
            else if (alignmentProperty.equals(PROPERTY_ALIGNMENT_CENTER))
            {
                style = style | SWT.CENTER;
            }
        }
        return style;
    }
}
