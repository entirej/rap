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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTButtonItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.utils.EJRWTItemRendererVisualContext;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJRWTButtonItemRenderer implements EJRWTAppItemRenderer, FocusListener, Serializable
{
    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJItemProperties                _itemProperties;
    protected EJScreenItemProperties          _screenItemProperties;
    private String                            _registeredItemName;
    protected boolean                         _activeEvent = true;
    protected Button                          _button;
    private boolean                           _isValid     = true;
    protected EJRWTItemRendererVisualContext  _visualContext;

    private EJCoreVisualAttributeProperties   _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties _initialVAProperties;
    protected ControlDecoration               _errorDecoration;
    protected EJMessage message;
    private boolean visible;
    
    
   
    public String getDisplayValue()
    {
       
        return null;
    }
    
    @Override
    public String formatValue(Object obj)
    {
        return null;
    }
    
    @Override
    public void refreshItemRendererProperty(String propertyName)
    {
        
        if(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY.equals(propertyName))
        {

            
            if(controlState(_button) && _rendererProps!=null)
            {
                String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

                if (customCSSKey != null && customCSSKey.trim().length() > 0)
                {
                    _button.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                }
                else
                {
                    _button.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_BUTTON);
                }
            }
           
        }
    }

    @Override
    public void validationErrorOccurred(boolean error)
    {

    }

    @Override
    public void refreshItemRenderer()
    {

    }

    @Override
    public boolean useFontDimensions()
    {
        return false;
    }

    protected boolean controlState(Control control)
    {
        return control != null && !control.isDisposed();

    }

    @Override
    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {
        _item = item;
        visible = item.isVisible();
        _itemProperties = _item.getReferencedItemProperties();
        _screenItemProperties = item.getProperties();
        _rendererProps = _itemProperties.getItemRendererProperties();
    }

    @Override
    public void setLabel(String label)
    {
        if (controlState(_button))
        {
            _button.setText(label == null ? "" : label);
        }
    }

    @Override
    public void setHint(String hint)
    {
        if (controlState(_button))
        {
            _button.setToolTipText(hint == null ? "" : hint);
        }
    }

    @Override
    public EJScreenItemController getItem()
    {
        return _item;
    }

    public EJItemProperties getItemProperties()
    {
        return _itemProperties;
    }

    @Override
    public Control getGuiComponent()
    {
        return _button;
    }

    @Override
    public Label getGuiComponentLabel()
    {
        return null;
    }

    @Override
    public void clearValue()
    {
    }

    @Override
    public String getRegisteredItemName()
    {
        return _registeredItemName;
    }

    @Override
    public Object getValue()
    {

        return null;

    }

    @Override
    public boolean isEditAllowed()
    {
        if (controlState(_button))
        {
            return _button.isEnabled();
        }

        return false;
    }

    @Override
    public boolean isVisible()
    {

//        if (controlState(_button))
//        {
//            return _button.isVisible();
//        }

        return visible;
    }

    @Override
    public boolean isMandatory()
    {
        return false;
    }

    @Override
    public boolean isValid()
    {
        if (_isValid)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void gainFocus()
    {
        if (controlState(_button))
        {
            _button.forceFocus();
        }
    }

    @Override
    public void setEditAllowed(boolean editAllowed)
    {
        if (controlState(_button))
        {
            _button.setEnabled(editAllowed);
        }
    }

    @Override
    public void setInitialValue(Object value)
    {
    }

    @Override
    public void setRegisteredItemName(String name)
    {
        _registeredItemName = name;
    }

    @Override
    public void setValue(Object value)
    {

    }

    @Override
    public void setVisible(boolean visible)
    {
        if (controlState(_button))
        {
            _button.setVisible(visible);
        }
        this.visible = visible;
    }

    @Override
    public void setMandatory(boolean mandatory)
    {
    }

    @Override
    public void enableLovActivation(boolean activate)
    {
    }

    @Override
    public boolean valueEqualsTo(Object value)
    {
        return false;
    }

    @Override
    public void setVisualAttribute(EJCoreVisualAttributeProperties visualAttributeProperties)
    {
        _visualAttributeProperties = visualAttributeProperties != null ? visualAttributeProperties : _initialVAProperties;
        if (!controlState(_button))
        {
            return;
        }
        refreshBackground();
        refreshForeground();
        refreshFont();
    }

    @Override
    public EJCoreVisualAttributeProperties getVisualAttributeProperties()
    {
        return _visualAttributeProperties;
    }

    private void refreshBackground()
    {
        Color background = EJRWTVisualAttributeUtils.INSTANCE.getBackground(_visualAttributeProperties);
        if (controlState(_button))
        {
            _button.setBackground(background != null ? background : _visualContext.getBackgroundColor());
        }
    }

    private void refreshForeground()
    {
        Color foreground = EJRWTVisualAttributeUtils.INSTANCE.getForeground(_visualAttributeProperties);
        if (controlState(_button))
        {
            _button.setForeground(foreground != null ? foreground : _visualContext.getForegroundColor());
        }
    }

    private void refreshFont()
    {
        if (controlState(_button))
        {
            _button.setFont(EJRWTVisualAttributeUtils.INSTANCE.getFont(_visualAttributeProperties, _visualContext.getItemFont()));
        }
    }

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ButtonItem:\n");
        buffer.append("  RegisteredItemName: ");
        buffer.append(_registeredItemName);
        buffer.append("\n");
        buffer.append("  Button: ");
        buffer.append(_button);
        buffer.append("  Label: ");
        buffer.append("null");

        return buffer.toString();
    }

    @Override
    public void focusGained(FocusEvent event)
    {
        _item.itemFocusGained();
        if (controlState(_button))
        {
            _button.forceFocus();
        }
    }

    @Override
    public void focusLost(FocusEvent event)
    {
        _item.itemFocusLost();
    }

    @Override
    public void createComponent(Composite composite)
    {

        String pictureName = _itemProperties.getItemRendererProperties().getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_PICTURE);
        String alignmentProperty = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_ALIGNMENT);
        if (alignmentProperty == null)
        {
            alignmentProperty = _rendererProps.getStringProperty("ALLIGNMENT");
        }

        String hint = _screenItemProperties.getHint();
        String label = _screenItemProperties.getLabel();
        int style = SWT.PUSH;
        style = getComponentStyle(alignmentProperty, style);

        _button = newButton(composite, style);

        setHint(hint);
        setLabel(label);

        if (pictureName != null && pictureName.trim().length() > 0)
        {
            _button.setImage(EJRWTImageRetriever.get(pictureName));
        }
       
        _button.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_BUTTON);
        String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

        if (customCSSKey != null && customCSSKey.trim().length() > 0)
        {
            _button.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
        }

        _button.setData(_item.getReferencedItemProperties().getName());
        _button.addFocusListener(this);
        _button.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (_activeEvent)
                {
                    Display.getDefault().asyncExec(new Runnable()
                    {
                        
                        @Override
                        public void run()
                        {
                            _item.executeActionCommand();
                            
                        }
                    });
                }
            }
        });

        if (_rendererProps.getBooleanProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_DEFAULT_BUTTON, false))
        {
            Shell shell = composite.getShell();
            if (shell != null)
            {
                shell.setDefaultButton(_button);
            }
        }

        _errorDecoration = new ControlDecoration(_button, SWT.TOP | SWT.LEFT);
        setMessage(message);
        
        _visualContext = new EJRWTItemRendererVisualContext(_button.getBackground(), _button.getForeground(), _button.getFont());
    }

    protected int getComponentStyle(String alignmentProperty, int style)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_LEFT))
            {
                style = style | SWT.LEFT;
            }
            else if (alignmentProperty.equals(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_RIGHT))
            {
                style = style | SWT.RIGHT;
            }
            else if (alignmentProperty.equals(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_CENTER))
            {
                style = style | SWT.CENTER;
            }
        }
        return style;
    }

    @Override
    public void createLable(Composite composite)
    {

    }

    protected Button newButton(Composite composite, int style)
    {
        boolean hideBorder = _itemProperties.getItemRendererProperties().getBooleanProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_HIDE_BORDER,
                false);
        if (hideBorder)
        {
            style = style | SWT.FLAT;
        }

        return new Button(composite, style);
    }

    @Override
    public ColumnLabelProvider createColumnLabelProvider(final EJScreenItemProperties item, EJScreenItemController controller)
    {
        
        final Image image ;
        String pictureName = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_PICTURE);

        if (pictureName != null && pictureName.length() > 0)
        {
            image = EJRWTImageRetriever.get(pictureName);
        }
        else
        {
            image = null;
        }
        ColumnLabelProvider provider = new ColumnLabelProvider()
        {
            
            @Override
            public Image getImage(Object element)
            {
                return image;
            }
            
            @Override
            public Color getBackground(Object element)
            {

                EJCoreVisualAttributeProperties properties = getAttributes(item, element);
                if (properties != null)
                {
                    Color background = EJRWTVisualAttributeUtils.INSTANCE.getBackground(properties);
                    if (background != null)
                    {
                        return background;
                    }
                }
                return super.getBackground(element);
            }

            @Override
            public Color getForeground(Object element)
            {
                EJCoreVisualAttributeProperties properties = getAttributes(item, element);
                if (properties != null)
                {
                    Color foreground = EJRWTVisualAttributeUtils.INSTANCE.getForeground(properties);
                    if (foreground != null)
                    {
                        return foreground;
                    }
                }
                return super.getForeground(element);
            }

            private EJCoreVisualAttributeProperties getAttributes(final EJScreenItemProperties item, Object element)
            {
                EJCoreVisualAttributeProperties properties = null;
                if (element instanceof EJDataRecord)
                {
                    EJDataRecord record = (EJDataRecord) element;
                    properties = record.getItem(item.getReferencedItemName()).getVisualAttribute();
                }
                if (properties == null)
                {
                    properties = _visualAttributeProperties;
                }
                return properties;
            }

            @Override
            public Font getFont(Object element)
            {
                EJCoreVisualAttributeProperties properties = getAttributes(item, element);
                if (properties != null)
                {
                    return EJRWTVisualAttributeUtils.INSTANCE.getFont(properties, super.getFont(element));

                }
                return super.getFont(element);
            }

            @Override
            public String getText(Object element)
            { 
               
                String label = item.getLabel();
                return label != null ? label : "";
            }

        };
        return provider;
    }

    @Override
    public EJRWTAbstractTableSorter getColumnSorter(EJScreenItemProperties item, EJScreenItemController controller)
    {
        return null;
    }

    @Override
    public void setInitialVisualAttribute(EJCoreVisualAttributeProperties va)
    {
        this._initialVAProperties = va;
        setVisualAttribute(va);
    }

    @Override
    public boolean isReadOnly()
    {
        return true;
    }

    @Override
    public void setMessage(EJMessage message)
    {
        this.message = message;
        if (_errorDecoration != null  && controlState(_button) && !_errorDecoration.getControl().isDisposed())
        {
            ControlDecorationSupport.handleMessage(_errorDecoration, message);
        }
        
    }

    @Override
    public void clearMessage()
    {
        this.message = null;
        if (_errorDecoration != null  && controlState(_button) && !_errorDecoration.getControl().isDisposed())
        {
            _errorDecoration.setDescriptionText("");
            {
                _errorDecoration.hide();
            }
        }
        
    }
    @Override
    public List<Object> getValidValues()
    {
        return Collections.emptyList();
    }
}
