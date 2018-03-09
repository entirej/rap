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
/**
 * 
 */
package org.entirej.applicationframework.rwt.renderers.item;

import java.math.BigDecimal;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTButtonItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTCheckBoxRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.utils.EJRWTItemRendererVisualContext;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJRWTCheckBoxItemRenderer extends EJRWTButtonItemRenderer
{
    private Object                          _checkedValue;
    private Object                          _uncheckedValue;
    private boolean                         _otherValueMappingValue;
    private boolean                         _useTriStateChaeckBox = false;
    protected boolean                       _isValid              = true;
    private boolean                         _mandatory;

    protected Object                        _baseValue;

    private EJCoreVisualAttributeProperties _visualAttributeProperties;

   
    private ControlDecoration               _mandatoryDecoration;
    private String                          _defaultValue;

    
    
    
    @Override
    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {
        super.initialise(item, screenItemProperties);

        final String checkedValue = _rendererProps.getStringProperty(EJRWTCheckBoxRendererDefinitionProperties.CHECKED_VALUE);
        final String uncheckedValue = _rendererProps.getStringProperty(EJRWTCheckBoxRendererDefinitionProperties.UNCHECKED_VALUE);
        final String otherValueMapping = _rendererProps.getStringProperty(EJRWTCheckBoxRendererDefinitionProperties.OTHER_VALUE_MAPPING);

        if (checkedValue == null || uncheckedValue == null)
        {
            throw new EJApplicationException(new EJMessage("A checked and unchecked value must be specified for the Check Box Renderer for item: "
                    + _itemProperties.getBlockName() + "." + _rendererProps.getName()));
        }

        // set 3 state checkBox
        if (_item.getScreenType() == EJScreenType.QUERY)
        {
            _useTriStateChaeckBox = _rendererProps.getBooleanProperty(EJRWTCheckBoxRendererDefinitionProperties.TRI_STATE, false);
        }

        _checkedValue = getValueAsObject(_itemProperties.getDataTypeClass(), checkedValue);
        _uncheckedValue = getValueAsObject(_itemProperties.getDataTypeClass(), uncheckedValue);

        _otherValueMappingValue = !EJRWTCheckBoxRendererDefinitionProperties.UNCHECKED.equals(otherValueMapping);

        _defaultValue = _itemProperties.getItemRendererProperties().getStringProperty(EJRWTCheckBoxRendererDefinitionProperties.DEFAULT_VALUE);
    }

    @Override
    public void clearValue()
    {
        _baseValue = null;
        try
        {
            _activeEvent = false;
            if (controlState(_button))
            {
                if (_useTriStateChaeckBox && (_defaultValue == null || "".equals(_defaultValue)))
                {
                    _button.setGrayed(true);
                    _button.setSelection(true);
                }
                else
                {
                    _button.setGrayed(false);
                    _button.setSelection(EJRWTCheckBoxRendererDefinitionProperties.CHECKED.equals(_defaultValue));
                }
            }
        }
        finally
        {
            _activeEvent = true;
        }
    }

    @Override
    public Object getValue()
    {
        if (controlState(_button))
        {
            if (_useTriStateChaeckBox && _button.getGrayed())
            {
                return null;
            }
            else
            {
                if (_button.getSelection())
                {
                    return _baseValue = _checkedValue;
                }
                else
                {
                    return _baseValue = _uncheckedValue;
                }
            }
        }

        return _baseValue;
    }

    @Override
    public void setValue(Object value)
    {
        _baseValue = value;
        if (controlState(_button))
        {
            try
            {
                _activeEvent = false;
                if (_useTriStateChaeckBox && value == null)
                {
                    _button.setGrayed(true);
                    _button.setSelection(true);
                }
                else
                {
                    _button.setGrayed(false);
                    if (_checkedValue.equals(value))
                    {
                        _button.setSelection(true);
                    }
                    else if (_uncheckedValue.equals(value))
                    {
                        _button.setSelection(false);
                    }
                    else
                    {
                        _button.setSelection(_otherValueMappingValue);
                    }
                }
            }
            finally
            {
                _activeEvent = true;
            }
        }
    }

    @Override
    public void setInitialValue(Object value)
    {
        try
        {
            _activeEvent = false;
            if (controlState(_button))
            {
                if (_useTriStateChaeckBox && value == null)
                {
                    _button.setGrayed(true);
                    _button.setSelection(true);
                }
                else
                {
                    _button.setGrayed(false);
                    if (_checkedValue.equals(value))
                    {
                        _button.setSelection(true);
                    }
                    else if (_uncheckedValue.equals(value))
                    {
                        _button.setSelection(false);
                    }
                    else
                    {
                        if (value != null && !"".equals(value))
                        {
                            _button.setSelection(_otherValueMappingValue);
                        }
                        else
                        {
                            if (_useTriStateChaeckBox && (_defaultValue == null || "".equals(_defaultValue)))
                            {
                                _button.setGrayed(true);
                                _button.setSelection(true);
                            }
                            else
                            {
                                _button.setSelection(EJRWTCheckBoxRendererDefinitionProperties.CHECKED.equals(_defaultValue));
                            }
                        }
                    }
                }
            }
        }
        finally
        {
            _activeEvent = true;
        }
    }

    @Override
    protected Button newButton(Composite parent, int style)
    {
        return new Button(parent, style);
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
                    _button.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_COMBOBOX);
                }
            }
           
        }
    }
    
    @Override
    public void createComponent(Composite composite)
    {
        final String label = _screenItemProperties.getLabel();
        final String hint = _screenItemProperties.getHint();
        int style = SWT.NONE;
        _button = newButton(composite, style = style | SWT.CHECK);

        _button.addListener(SWT.Selection, new Listener()
        {
            @Override
            public void handleEvent(Event e)
            {
                if (_activeEvent)
                {
                    if (_useTriStateChaeckBox)
                    {
                        if (!_button.getSelection())
                        {
                            _button.setGrayed(!_button.getGrayed());
                            if (_button.getGrayed())
                            {
                                _button.setSelection(true);
                            }
                        }
                        else
                        {
                            _button.setGrayed(false);
                        }
                    }
                    Object old = null;
                    Object newVal = null;
                    if (_button.getSelection())
                    {
                        old = _uncheckedValue;
                        newVal =  _checkedValue;
                    }
                    else
                    {
                        old = _checkedValue;
                        newVal =  _uncheckedValue;
                    }
                    EJ_RWT.setAttribute(_button, "ej-item-selection", String.valueOf(newVal));
                    _item.itemValueChaged(newVal);
                    _item.executeActionCommand();
                   
                }
            }
        });

        if (label != null && label.trim().length() > 0)
        {
            _button.setText(label);
        }
        if (hint != null && hint.trim().length() > 0)
        {
            _button.setToolTipText(hint);
        }
        _button.setData(EJ_RWT.CUSTOM_VARIANT,EJ_RWT.CSS_CV_ITEM_CHECKBOX);
        String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

        if (customCSSKey != null && customCSSKey.trim().length() > 0)
        {
            _button.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
        }
        _button.setData(_item.getReferencedItemProperties().getName());
        _button.addFocusListener(this);
        saveInitialVisualAttributePropeties();

        _mandatoryDecoration = new ControlDecoration(_button, SWT.TOP | SWT.LEFT);
        _errorDecoration = new ControlDecoration(_button, SWT.TOP | SWT.LEFT);
        _errorDecoration.setImage(getDecorationImage(FieldDecorationRegistry.DEC_ERROR));
        _mandatoryDecoration.setImage(getDecorationImage(FieldDecorationRegistry.DEC_REQUIRED));
        _mandatoryDecoration.setShowHover(true);
        _mandatoryDecoration.setDescriptionText(_screenItemProperties.getLabel() == null || _screenItemProperties.getLabel().isEmpty() ? "Required Item"
                : String.format("%s is required", _screenItemProperties.getLabel()));
        if (_isValid)
        {
            _errorDecoration.hide();
        }
        if(message!=null)
        {
            setMessage(message);
        }
        _mandatoryDecoration.hide();
        setInitialValue(_baseValue);
        _visualContext = new EJRWTItemRendererVisualContext(_button.getBackground(), _button.getForeground(), _button.getFont());
        EJ_RWT.setAttribute(_button, "ej-item-selection", String.valueOf(getValue()));
    }

    private Object getValueAsObject(Class<?> datatypeClassName, String value)
    {
        if (datatypeClassName.getName().equals(Integer.class.getName()))
        {
            return Integer.parseInt(value);
        }
        else if (datatypeClassName.getName().equals(String.class.getName()))
        {
            return value;
        }
        else if (datatypeClassName.getName().equals(Float.class.getName()))
        {
            return Float.parseFloat(value);
        }
        else if (datatypeClassName.getName().equals(Long.class.getName()))
        {
            return Long.parseLong(value);
        }
        else if (datatypeClassName.getName().equals(Double.class.getName()))
        {
            return Double.parseDouble(value);
        }
        else if (datatypeClassName.getName().equals(Boolean.class.getName()))
        {
            return Boolean.parseBoolean(value);
        }
        else if (datatypeClassName.getName().equals(BigDecimal.class.getName()))
        {
            return new BigDecimal(value);
        }
        else if (datatypeClassName.getName().equals(Number.class.getName()))
        {
            return Double.parseDouble(value);
        }

        return value;

    }

    private void saveInitialVisualAttributePropeties()
    {
    }

    private Image getDecorationImage(String image)
    {
        FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
        return registry.getFieldDecoration(image).getImage();
    }

    @Override
    public boolean isMandatory()
    {
        return _mandatory;
    }

    @Override
    public void setMandatory(boolean mandatory)
    {
        _mandatory = mandatory;
    }

    @Override
    public boolean valueEqualsTo(Object value)
    {
        return value.equals(this.getValue());
    }

    @Override
    public boolean isValid()
    {
        if (_isValid)
        {
            if (_mandatory && getValue() == null)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public ColumnLabelProvider createColumnLabelProvider(final EJScreenItemProperties item, final EJScreenItemController controller)
    {
        EJItemProperties itemProperties = controller.getReferencedItemProperties();
        EJFrameworkExtensionProperties itemRendererProperties = itemProperties.getItemRendererProperties();
        final Object checkedValue = getValueAsObject(itemProperties.getDataTypeClass(),
                itemRendererProperties.getStringProperty(EJRWTCheckBoxRendererDefinitionProperties.CHECKED_VALUE));
        final Object uncheckedValue = getValueAsObject(itemProperties.getDataTypeClass(),
                itemRendererProperties.getStringProperty(EJRWTCheckBoxRendererDefinitionProperties.UNCHECKED_VALUE));
        final boolean otherValueMappingValue = EJRWTCheckBoxRendererDefinitionProperties.CHECKED.equals(itemRendererProperties
                .getStringProperty(EJRWTCheckBoxRendererDefinitionProperties.OTHER_VALUE_MAPPING));
        final boolean defaultState = EJRWTCheckBoxRendererDefinitionProperties.CHECKED.equals(otherValueMappingValue);

        ColumnLabelProvider provider = new ColumnLabelProvider()
        {
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

                return "";
            }

            @Override
            public Image getImage(Object element)
            {
                if (element instanceof EJDataRecord)
                {
                    EJDataRecord record = (EJDataRecord) element;
                    Object value = record.getValue(item.getReferencedItemName());
                    if (value != null)
                    {
                        if (value.equals(checkedValue))
                        {
                            return EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_CHECK_SELECTED);
                        }
                        else if (value.equals(uncheckedValue))
                        {
                            return EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_CHECK_UNSELECTED);
                        }
                        else
                        {
                            if (otherValueMappingValue)
                            {
                                return EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_CHECK_SELECTED);
                            }
                            else
                            {
                                return EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_CHECK_UNSELECTED);
                            }
                        }
                    }
                    else
                    {
                        if (defaultState)
                        {
                            return EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_CHECK_SELECTED);
                        }
                        else
                        {
                            return EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_CHECK_UNSELECTED);
                        }
                    }
                }
                return EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_CHECK_UNSELECTED);
            }
        };
        return provider;
    }
    
    
    
    
    @Override
    public EJRWTAbstractTableSorter getColumnSorter(final EJScreenItemProperties item, EJScreenItemController controller)
    {
        EJItemProperties itemProperties = controller.getReferencedItemProperties();
        EJFrameworkExtensionProperties itemRendererProperties = itemProperties.getItemRendererProperties();
        
        final Object checkedValue = getValueAsObject(itemProperties.getDataTypeClass(),
                itemRendererProperties.getStringProperty(EJRWTCheckBoxRendererDefinitionProperties.CHECKED_VALUE));
        return new EJRWTAbstractTableSorter()
        {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2)
            {
                if (e1 instanceof EJDataRecord && e2 instanceof EJDataRecord)
                {
                    EJDataRecord d1 = (EJDataRecord) e1;
                    EJDataRecord d2 = (EJDataRecord) e2;
                    if (d1 != null && d2 != null)
                    {
                        Object value1 = d1.getValue(item.getReferencedItemName());
                        Object value2 = d2.getValue(item.getReferencedItemName());
                      
                       if(checkedValue.equals(value1)&& !checkedValue.equals(value2))
                       {
                           return 1;
                       }
                       if(!checkedValue.equals(value1) &&checkedValue.equals(value2))
                       {
                           return -1;
                       }
                    }
                }
                return 0;
            }
            
          
            
            
        };
    }
    
    

    @Override
    public boolean isReadOnly()
    {
        return false;
    }
}
