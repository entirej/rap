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

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTButtonItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTImageItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.table.HtmlBaseColumnLabelProvider;
import org.entirej.applicationframework.rwt.utils.EJRWTItemRendererVisualContext;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJMessageFactory;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.enumerations.EJFrameworkMessage;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJRWTImageItemRenderer implements EJRWTAppItemRenderer, FocusListener, Serializable
{
    @Override
    public String formatValue(Object obj)
    {
       
        return null;
    }
    public final class HtmlBaseImageColumn extends ColumnLabelProvider implements HtmlBaseColumnLabelProvider
    {
        private final EJScreenItemProperties item;

        private HtmlBaseImageColumn(EJScreenItemProperties item)
        {
            this.item = item;
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
        public String getText(Object element)
        {
            Image srcImage = getSrcImage(element);
            if(srcImage==null)return "";
            String src = ImageFactory.getImagePath(srcImage);
            Rectangle bounds = srcImage.getBounds();
            return "<img width='"+bounds.width+"' height='"+bounds.height+"' src='" + src  + "'/>";
        }

        public String getSrcText(Object element)
        {
            return "";
        }
        
        public Image getSrcImage(Object element)
        {
            if (element instanceof EJDataRecord)
            {
                EJDataRecord record = (EJDataRecord) element;
                Object value = record.getValue(item.getReferencedItemName());

                if (value != null)
                {
                    if (value instanceof String)
                    {
                        URL resource = EJRWTImageRetriever.class.getClassLoader().getResource((String) value);
                        if(resource!=null)
                        {
                            return ImageDescriptor.createFromURL(resource).createImage();
                        }
                        else
                        {
                            try
                            {
                               return ImageDescriptor.createFromURL((new URL((String)value))).createImage();
                            }
                            catch (MalformedURLException e)
                            {
                                return null;
                            }
                        }
                    }
                    else if (value instanceof URL)
                    {
                        return ImageDescriptor.createFromURL((URL) value).createImage();
                    }
                    else if (value instanceof byte[])
                    {
                        return new Image(Display.getDefault(), new ByteArrayInputStream((byte[]) value));
                    }
                }
            }
            return _defaultImage;
        }
    }

    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJScreenItemProperties          _screenItemProperties;
    protected EJItemProperties                _itemProperties;
    protected String                          _registeredItemName;
    protected Label                           _labelField;

    protected EJCoreVisualAttributeProperties _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties _initialVAProperties;
    protected ControlDecoration               _errorDecoration;
    protected ControlDecoration               _mandatoryDecoration;
    protected VALUE_CASE                      _valueCase       = VALUE_CASE.DEFAULT;
    private EJRWTItemRendererVisualContext    _visualContext;
    private transient Image                   _defaultImage;
    private transient Image                   _currentImage;
    protected Object                          _baseValue;
    private EJMessage message;
    private boolean visible;

    protected boolean controlState(Control control)
    {
        return control != null && !control.isDisposed();
    }

    @Override
    public boolean useFontDimensions()
    {
        return false;
    }
    
    public String getDisplayValue()
    {
        
        return null;
    }

    @Override
    public void refreshItemRendererProperty(String propertyName)
    {
        
        if(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY.equals(propertyName))
        {

            
           
           
            if(controlState(_labelField) && _rendererProps!=null)
            {
                String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);
                
                if (customCSSKey != null && customCSSKey.trim().length() > 0)
                {
                    _labelField.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                }
                else
                {
                    _labelField.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_IMAGE);
                }
            }
            
        }
    }

    @Override
    public void refreshItemRenderer()
    {
    }

    @Override
    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {
        _item = item;
        visible = item.isVisible();
        _itemProperties = _item.getReferencedItemProperties();
        _screenItemProperties = screenItemProperties;
        _rendererProps = _itemProperties.getItemRendererProperties();
        String pictureName = _rendererProps.getStringProperty(EJRWTImageItemRendererDefinitionProperties.PROPERTY_IMAGE);

        if (pictureName != null && pictureName.length() > 0)
        {
            if (pictureName != null && pictureName.trim().length() > 0)
            {
                _defaultImage = EJRWTImageRetriever.get(pictureName);
            }
        }
    }

    @Override
    public void setLabel(String label)
    {
        if (controlState(_labelField))
        {
            _labelField.setText(label == null ? "" : label);
        }
    }

    @Override
    public void setHint(String hint)
    {
        if (controlState(_labelField))
        {
            _labelField.setToolTipText(hint == null ? "" : hint);
        }
    }

    @Override
    public void enableLovActivation(boolean activate)
    {

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
        return _labelField;
    }

    @Override
    public Label getGuiComponentLabel()
    {
        return null;
    }

    @Override
    public void clearValue()
    {
        _baseValue = null;
        if (controlState(_labelField))
        {
            _labelField.setImage(_defaultImage);
        }
    }

    @Override
    public String getRegisteredItemName()
    {
        return _registeredItemName;
    }

    @Override
    public Object getValue()
    {
        return _baseValue;
    }

    @Override
    public boolean isEditAllowed()
    {
        if (controlState(_labelField))
        {
            return _labelField.isEnabled();
        }
        return false;
    }

    @Override
    public boolean isVisible()
    {
//        if (controlState(_labelField))
//        {
//            return _labelField.isVisible();
//        }

        return visible;
    }

    @Override
    public boolean isValid()
    {
        return true;
    }

    @Override
    public void gainFocus()
    {
        if (_labelField != null && controlState(_labelField))
        {
            _labelField.forceFocus();
        }
    }

    @Override
    public void setEditAllowed(boolean editAllowed)
    {
       
    }

    @Override
    public void setInitialValue(Object value)
    {
        setValue(value);
    }

    @Override
    public void setRegisteredItemName(String name)
    {
        _registeredItemName = name;
    }

    @Override
    public void setValue(Object value)
    {
        _baseValue = value;
        if (_currentImage != null && !_currentImage.isDisposed())
        {
            _currentImage.dispose();
            _currentImage = null;
        }
        if (_labelField != null && controlState(_labelField))
        {
            if (value == null)
            {
                _labelField.setImage(_defaultImage);
            }
            else
            {
                if (value instanceof String)
                {
                    URL resource = EJRWTImageRetriever.class.getClassLoader().getResource((String) value);
                    if(resource!=null)
                    {
                        _currentImage = ImageDescriptor.createFromURL(resource).createImage();
                    }
                    else
                    {
                        try
                        {
                            _currentImage = ImageDescriptor.createFromURL((new URL((String)value))).createImage();
                        }
                        catch (MalformedURLException e)
                        {
                            EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM, _item.getName(),
                                    "String should follow URL Spec or Need to be In Classpath", (String)value);
                            throw new IllegalArgumentException(message.getMessage());
                        }
                    }
                }
                else if (value instanceof URL)
                {
                    _currentImage = ImageDescriptor.createFromURL((URL) value).createImage();
                }
                else if (value instanceof byte[])
                {
                    _currentImage = new Image(Display.getDefault(), new ByteArrayInputStream((byte[]) value));
                }
                else
                {
                    EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM, _item.getName(),
                            "URL or byte[] ", value.getClass().getName());
                    throw new IllegalArgumentException(message.getMessage());
                }
                _labelField.setImage(_currentImage);
            }
        }
    }

    @Override
    public void setVisible(boolean visible)
    {
        if (_labelField != null)
        {
            _labelField.setVisible(visible);
        }
        this.visible = visible;
    }

    @Override
    public void setMandatory(boolean mandatory)
    {

    }

    @Override
    public boolean isMandatory()
    {
        return false;
    }

    @Override
    public boolean valueEqualsTo(Object value)
    {
        return value.equals(this.getValue());
    }

    @Override
    public void validationErrorOccurred(boolean error)
    {
        if (_errorDecoration == null ||  !controlState(_labelField) || _errorDecoration.getControl().isDisposed())
        {
            return;
        }
        _errorDecoration.setDescriptionText("");
        if (error)
        {
            _errorDecoration.show();
        }
        else
        {
            _errorDecoration.hide();
        }
    }
    
    @Override
    public void setMessage(EJMessage message)
    {
        this.message = message;
        if (_errorDecoration != null && controlState(_labelField)  &&  !_errorDecoration.getControl().isDisposed())
        {
            ControlDecorationSupport.handleMessage(_errorDecoration, message);
        }
        
    }

    @Override
    public void clearMessage()
    {
        this.message = null;
        if (_errorDecoration != null  && controlState(_labelField) && !_errorDecoration.getControl().isDisposed())
        {
            _errorDecoration.setDescriptionText("");
            {
                _errorDecoration.hide();
            }
        }
        
    }

    @Override
    public void focusGained(FocusEvent event)
    {
        _item.itemFocusGained();
    }

    @Override
    public void focusLost(FocusEvent event)
    {
        _item.itemFocusLost();
    }

    @Override
    public void setVisualAttribute(EJCoreVisualAttributeProperties visualAttributeProperties)
    {
        _visualAttributeProperties = visualAttributeProperties != null ? visualAttributeProperties : _initialVAProperties;
        if (_labelField == null || _labelField.isDisposed())
        {
            return;
        }
        refreshBackground();
    }

    @Override
    public EJCoreVisualAttributeProperties getVisualAttributeProperties()
    {
        return _visualAttributeProperties;
    }

    private void refreshBackground()
    {
        Color background = EJRWTVisualAttributeUtils.INSTANCE.getBackground(_visualAttributeProperties);

        _labelField.setBackground(background != null ? background : _visualContext.getBackgroundColor());
    }

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("LabelItem:\n");
        buffer.append("  RegisteredItemName: ");
        buffer.append(_registeredItemName);
        buffer.append("\n");
        buffer.append("  Label: ");
        buffer.append(_labelField);
        buffer.append("  GUI Component: ");
        buffer.append(_labelField);

        return buffer.toString();
    }

    @Override
    public void createComponent(Composite composite)
    {
        String alignmentProperty = _rendererProps.getStringProperty(EJRWTImageItemRendererDefinitionProperties.PROPERTY_ALIGNMENT);
        String hint = _screenItemProperties.getHint();

        int style = SWT.NONE;
        style = getComponentStyle(alignmentProperty, style);

        final String label = _screenItemProperties.getLabel();
        final Label labelField = new Label(composite, style);

        

        _labelField = labelField;
        _labelField.setData(EJ_RWT.CUSTOM_VARIANT,EJ_RWT.CSS_CV_ITEM_IMAGE);
        String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

        if (customCSSKey != null && customCSSKey.trim().length() > 0)
        {
            _labelField.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
        }
        _labelField.setText(label != null ? label : "");
        _labelField.setToolTipText(hint != null ? hint : "");
        _labelField.setData(_item.getReferencedItemProperties().getName());
        _labelField.addFocusListener(this);
        _visualContext = new EJRWTItemRendererVisualContext(_labelField.getBackground(), _labelField.getForeground(), _labelField.getFont());

        _mandatoryDecoration = new ControlDecoration(_labelField, SWT.TOP | SWT.LEFT);
        _errorDecoration = new ControlDecoration(_labelField, SWT.TOP | SWT.LEFT);
        _errorDecoration.setImage(getDecorationImage(FieldDecorationRegistry.DEC_ERROR));
        _mandatoryDecoration.setImage(getDecorationImage(FieldDecorationRegistry.DEC_REQUIRED));
        _mandatoryDecoration.setShowHover(true);
        _mandatoryDecoration.setDescriptionText(_screenItemProperties.getLabel() == null || _screenItemProperties.getLabel().isEmpty() ? "Required Item"
                : String.format("%s is required", _screenItemProperties.getLabel()));
        _errorDecoration.hide();
        _labelField.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent arg0)
            {
                if (_currentImage != null && !_currentImage.isDisposed())
                {
                    _currentImage.dispose();
                    _currentImage = null;
                }
            }
        });
        String actionCommand = _screenItemProperties.getActionCommand();
        if (actionCommand != null && actionCommand.trim().length() != 0)
        {
            _labelField.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseUp(MouseEvent e)
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
            });
        }
       
        _labelField.setImage(_defaultImage );
        _mandatoryDecoration.hide();
        setMessage(message);
    }

    protected int getComponentStyle(String alignmentProperty, int style)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJRWTImageItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_LEFT))
            {
                style = style | SWT.LEFT;
            }
            else if (alignmentProperty.equals(EJRWTImageItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_RIGHT))
            {
                style = style | SWT.RIGHT;
            }
            else if (alignmentProperty.equals(EJRWTImageItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_CENTER))
            {
                style = style | SWT.CENTER;
            }
        }
        return style;
    }

    private Image getDecorationImage(String image)
    {
        FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
        return registry.getFieldDecoration(image).getImage();
    }

    @Override
    public void createLable(Composite composite)
    {

    }

    @Override
    public ColumnLabelProvider createColumnLabelProvider(final EJScreenItemProperties item, EJScreenItemController controller)
    {
        ColumnLabelProvider provider = new HtmlBaseImageColumn(item);
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

    public boolean canExecuteActionCommand()
    {
        return false;
    }
    
    @Override
    public List<Object> getValidValues()
    {
        return Collections.emptyList();
    }
}
