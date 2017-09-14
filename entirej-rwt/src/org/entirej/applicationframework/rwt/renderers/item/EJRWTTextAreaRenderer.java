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
package org.entirej.applicationframework.rwt.renderers.item;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTButtonItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.table.HtmlBaseColumnLabelProvider;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJRWTTextAreaRenderer extends EJRWTTextItemRenderer
{
    
    
    @Override
    public ColumnLabelProvider createColumnLabelProvider(EJScreenItemProperties item, EJScreenItemController controller)
    {
        
       final  ColumnLabelProvider labelProvider = super.createColumnLabelProvider(item, controller);
       
        return new MultiColumnLabelProvider(labelProvider);
    }
    
    
    private class MultiColumnLabelProvider extends ColumnLabelProvider  implements HtmlBaseColumnLabelProvider
    {
        final  ColumnLabelProvider proxy;
        public MultiColumnLabelProvider(ColumnLabelProvider labelProvider)
        {
            this.proxy = labelProvider;
        }
        public void addListener(ILabelProviderListener listener)
        {
            proxy.addListener(listener);
        }
        
        @Override
        public String getSrcText(Object element)
        {
            
            return proxy.getText(element);
        }
        
        @Override
        public Image getSrcImage(Object element)
        {
            return getImage(element);
        }
        
        public void dispose()
        {
            proxy.dispose();
        }
        public boolean isLabelProperty(Object element, String property)
        {
            return proxy.isLabelProperty(element, property);
        }
        public int hashCode()
        {
            return proxy.hashCode();
        }
        public Font getFont(Object element)
        {
            return proxy.getFont(element);
        }
        public Color getBackground(Object element)
        {
            return proxy.getBackground(element);
        }
        public void removeListener(ILabelProviderListener listener)
        {
            proxy.removeListener(listener);
        }
        public Color getForeground(Object element)
        {
            return proxy.getForeground(element);
        }
        public Image getImage(Object element)
        {
            return proxy.getImage(element);
        }
        public String getText(Object element)
        {
            String text = proxy.getText(element);
            if(text!=null)
            {
                text=   text.replaceAll("\n", "<br />");
            }
            return text;
        }
        public Image getToolTipImage(Object object)
        {
            return proxy.getToolTipImage(object);
        }
        public String getToolTipText(Object element)
        {
            return proxy.getToolTipText(element);
        }
        public Color getToolTipBackgroundColor(Object object)
        {
            return proxy.getToolTipBackgroundColor(object);
        }
        public boolean equals(Object obj)
        {
            return proxy.equals(obj);
        }
        public Color getToolTipForegroundColor(Object object)
        {
            return proxy.getToolTipForegroundColor(object);
        }
        public Font getToolTipFont(Object object)
        {
            return proxy.getToolTipFont(object);
        }
        public Point getToolTipShift(Object object)
        {
            return proxy.getToolTipShift(object);
        }
        public boolean useNativeToolTip(Object object)
        {
            return proxy.useNativeToolTip(object);
        }
        public int getToolTipTimeDisplayed(Object object)
        {
            return proxy.getToolTipTimeDisplayed(object);
        }
        public int getToolTipDisplayDelayTime(Object object)
        {
            return proxy.getToolTipDisplayDelayTime(object);
        }
        public int getToolTipStyle(Object object)
        {
            return proxy.getToolTipStyle(object);
        }
        public void dispose(ColumnViewer viewer, ViewerColumn column)
        {
            proxy.dispose(viewer, column);
        }
        public String toString()
        {
            return proxy.toString();
        }
        
        
    }
    
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
