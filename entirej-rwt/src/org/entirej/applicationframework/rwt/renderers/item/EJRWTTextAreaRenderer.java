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
package org.entirej.applicationframework.rwt.renderers.item;

import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTButtonItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTTextItemRendererDefinitionProperties;

public class EJRWTTextAreaRenderer extends EJRWTTextItemRenderer
{
    @Override
    protected Text newTextField(Composite composite, int style)
    {
        if (_rendererProps.getBooleanProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_WRAP, false))
        {
            style = style | SWT.MULTI | SWT.WRAP;
        }
        else
        {
            style = style | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
        }
        return super.newTextField(composite, style);
    }
    
    @Override
    public void createComponent(Composite composite)
    {
        super.createComponent(composite);
        if (controlState(_actionControl))
        {
            String[] keys = new String[] { };
            _actionControl.getTextControl().setData(EJ_RWT.ACTIVE_KEYS, keys);
        }
    }
    
    @Override
    public String getCSSKey()
    {
        return EJ_RWT.CSS_CV_ITEM_TEXTAREA;
    }
    
    
    @Override
    public void refreshItemRendererProperty(String propertyName)
    {
        
        if(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY.equals(propertyName))
        {

            
            if(controlState(_label) && _rendererProps!=null)
            {
                String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

                if (customCSSKey != null && customCSSKey.trim().length() > 0)
                {
                    _label.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                }
                else
                {
                    _label.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_TEXTAREA);
                }
            }
           
            if(controlState(_textField) && _rendererProps!=null)
            {
                String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);
                
                if (customCSSKey != null && customCSSKey.trim().length() > 0)
                {
                    _textField.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                }
                else
                {
                    _textField.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_TEXTAREA);
                }
            }
            
        }
    }
}
