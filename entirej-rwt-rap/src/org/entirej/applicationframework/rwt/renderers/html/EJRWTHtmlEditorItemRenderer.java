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
package org.entirej.applicationframework.rwt.renderers.html;

import java.io.Serializable;
import java.text.Collator;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.rwt.component.EJRWTCKEditor;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTButtonItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.utils.EJRWTItemRendererVisualContext;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJRWTHtmlEditorItemRenderer implements EJRWTAppItemRenderer, FocusListener, Serializable
{
    public static final String PROPERTY_INLINE_KEY = "INLINE";
    
    public static final String PROPERTY_PROFILE_KEY      = "PROFILE";
    public static final String PROPERTY_PROFILE_BASIC    = "Basic";
    public static final String PROPERTY_PROFILE_STANDARD = "Standard";
    public static final String PROPERTY_PROFILE_FULL     = "Full";
    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJScreenItemProperties          _screenItemProperties;
    protected EJItemProperties                _itemProperties;
    protected String                          _registeredItemName;
    protected EJRWTCKEditor                   _textField;
    protected Label                           _label;
    protected boolean                         _isValid = true;
    protected boolean                         _mandatory;

    protected EJCoreVisualAttributeProperties _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties _initialVAProperties;
    protected ControlDecoration               _errorDecoration;
    protected ControlDecoration               _mandatoryDecoration;
    private EJRWTItemRendererVisualContext    _visualContext;

    protected Object                          _baseValue;

    protected boolean controlState(Control control)
    {
        return control != null && !control.isDisposed();

    }

    public String getCSSKey()
    {
        return EJ_RWT.CSS_CV_ITEM_TEXT;
    }

    @Override
    public boolean useFontDimensions()
    {
        return true;
    }

    @Override
    public void refreshItemRendererProperty(String propertyName)
    {

        if (EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY.equals(propertyName))
        {

            if (controlState(_label) && _rendererProps != null)
            {
                String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

                if (customCSSKey != null && customCSSKey.trim().length() > 0)
                {
                    _label.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                }
                else
                {
                    _label.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_TEXT);
                }
            }

            if (controlState(_textField) && _rendererProps != null)
            {
                String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

                if (customCSSKey != null && customCSSKey.trim().length() > 0)
                {
                    _textField.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                }
                else
                {
                    _textField.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_TEXT);
                }
            }

        }
    }

    @Override
    public void setInitialVisualAttribute(EJCoreVisualAttributeProperties va)
    {
        this._initialVAProperties = va;
        setVisualAttribute(va);

    }

    @Override
    public void refreshItemRenderer()
    {

    }

    @Override
    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {

        _item = item;
        _itemProperties = _item.getReferencedItemProperties();
        _screenItemProperties = screenItemProperties;
        _rendererProps = _itemProperties.getItemRendererProperties();
        final String caseProperty = _rendererProps.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_CASE);

    }

    @Override
    public void setLabel(String label)
    {
        if (_label != null)
        {
            _label.setText(label == null ? "" : label);
        }
    }

    @Override
    public void setHint(String hint)
    {
        if (controlState(_textField))
        {
            _textField.setToolTipText(hint == null ? "" : hint);
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

        return _textField;
    }

    @Override
    public Label getGuiComponentLabel()
    {
        return _label;
    }

    @Override
    public void clearValue()
    {
        _baseValue = null;

        try
        {

            if (controlState(_textField))
            {
                _textField.setText("");
            }
        }
        finally
        {

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

        if (!controlState(_textField))
        {
            return _baseValue;
        }

        String value = _textField.getText();

        if (value == null || value.length() == 0)
        {
            value = null;
        }

        return _baseValue = value;
    }

    @Override
    public boolean isEditAllowed()
    {
        if (controlState(_textField))
        {
            return _textField.getEditable();
        }

        return false;
    }

    public boolean isLovActivated()
    {
        return false;
    }

    @Override
    public boolean isVisible()
    {

        {
            if (controlState(_textField))
            {
                return _textField.isVisible();
            }
        }

        return false;
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
    public void gainFocus()
    {

        {
            if (controlState(_textField))
            {
                _textField.forceFocus();
            }
        }
    }

    @Override
    public void setEditAllowed(boolean editAllowed)
    {

        if (controlState(_textField))
        {
            _textField.setEditable(editAllowed);
        }
        setMandatoryBorder(editAllowed && _mandatory);

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
        try
        {

            {
                if (controlState(_textField))
                {

                    _textField.setText(value==null ? "" :(value.toString()));
                    setMandatoryBorder(_mandatory);
                }
            }
        }
        finally
        {

        }
    }

    @Override
    public void setVisible(boolean visible)
    {

        {
            if (controlState(_textField))
            {
                _textField.setVisible(visible);
            }
        }

        if (controlState(_label))
        {
            _label.setVisible(visible);
        }
    }

    @Override
    public void setMandatory(boolean mandatory)
    {
        _mandatory = mandatory;
        setMandatoryBorder(mandatory);
    }

    @Override
    public boolean isMandatory()
    {
        return _mandatory;
    }

    @Override
    public boolean valueEqualsTo(Object value)
    {
        return value.equals(this.getValue());
    }

    @Override
    public void validationErrorOccurred(boolean error)
    {

        if (_errorDecoration != null && controlState(_textField))
        {
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

    }

    public void valueChanged()
    {
        Object base = _baseValue;
        Object value = getValue();

        if (((base == null && value != null) || (base != null && value == null) || (value != null && !value.equals(base))))
            _item.itemValueChaged();
        setMandatoryBorder(_mandatory);
    }

    protected void setMandatoryBorder(boolean req)
    {

        if(_textField.isDisposed() )
        {
            return;
        }
        
        if (req && getValue() == null )
        {
            _mandatoryDecoration.show();
        }
        else
        {
            _mandatoryDecoration.hide();
        }
    }

    @Override
    public void focusGained(FocusEvent event)
    {
        _item.itemFocusGained();
        if (controlState(_textField))
        {
            _textField.forceFocus();
        }
    }

    @Override
    public void focusLost(FocusEvent event)
    {
        Display.getCurrent().asyncExec(new Runnable()
        {

            @Override
            public void run()
            {
                _item.itemValueChaged();
                setMandatoryBorder(_mandatory);

                _item.itemFocusLost();

            }
        });
    }

    @Override
    public void setVisualAttribute(EJCoreVisualAttributeProperties visualAttributeProperties)
    {
        _visualAttributeProperties = visualAttributeProperties != null ? visualAttributeProperties : _initialVAProperties;

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

        {
            if (controlState(_textField))
            {
                _textField.setBackground(background != null ? background : _visualContext.getBackgroundColor());
            }
        }
    }

    private void refreshForeground()
    {

        Color foreground = EJRWTVisualAttributeUtils.INSTANCE.getForeground(_visualAttributeProperties);

        {
            if (controlState(_textField))
            {
                _textField.setForeground(foreground != null ? foreground : _visualContext.getForegroundColor());
            }
        }
    }

    private void refreshFont()
    {

        {
            if (controlState(_textField))
            {
                _textField.setFont(EJRWTVisualAttributeUtils.INSTANCE.getFont(_visualAttributeProperties, _visualContext.getItemFont()));
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("TextItem:\n");
        buffer.append("  RegisteredItemName: ");
        buffer.append(_registeredItemName);
        buffer.append("\n");
        buffer.append("  TextField: ");
        buffer.append(_textField);
        buffer.append("  Label: ");
        buffer.append(_label);
        buffer.append("  GUI Component: ");
        buffer.append(_textField);

        return buffer.toString();
    }

    protected Label newVlaueLabel(Composite composite)
    {
        return new Label(composite, SWT.NONE);
    }

    public Control createCustomButtonControl(Composite parent)
    {
        return null;
    }

    @Override
    public void createComponent(Composite composite)
    {

        String hint = _screenItemProperties.getHint();

        {

            composite.setData(EJ_RWT.CUSTOM_VARIANT, "html");
            _textField = new EJRWTCKEditor(composite, SWT.NONE,_rendererProps.getBooleanProperty(PROPERTY_INLINE_KEY, false),
                    
                    _rendererProps.getStringProperty(PROPERTY_PROFILE_KEY));
            _textField.setData(EJ_RWT.CUSTOM_VARIANT, "html");
            _textField.setData(EJ_RWT.CUSTOM_VARIANT, getCSSKey());
            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

            if (customCSSKey != null && customCSSKey.trim().length() > 0)
            {
                _textField.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            }
            if (hint != null && hint.trim().length() > 0)
            {
                _textField.setToolTipText(hint);
            }

            _visualContext = new EJRWTItemRendererVisualContext(_textField.getBackground(), _textField.getForeground(), _textField.getFont());

            _textField.setData(_item.getReferencedItemProperties().getName());
            _textField.addFocusListener(this);

            _mandatoryDecoration = new ControlDecoration(_textField, SWT.TOP | SWT.LEFT);
            _errorDecoration = new ControlDecoration(_textField, SWT.TOP | SWT.LEFT);
            _errorDecoration.setImage(getDecorationImage(FieldDecorationRegistry.DEC_ERROR));
            _mandatoryDecoration.setImage(getDecorationImage(FieldDecorationRegistry.DEC_REQUIRED));
            _mandatoryDecoration.setShowHover(true);
            _mandatoryDecoration.setDescriptionText(_screenItemProperties.getLabel() == null || _screenItemProperties.getLabel().isEmpty() ? "Required Item"
                    : String.format("%s is required", _screenItemProperties.getLabel()));
            if (_isValid)
            {
                _errorDecoration.hide();
            }
            _mandatoryDecoration.hide();

            setInitialValue(_baseValue);
        }
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

    private Image getDecorationImage(String image)
    {
        FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
        return registry.getFieldDecoration(image).getImage();
    }

    @Override
    public void createLable(Composite composite)
    {
        _label = new Label(composite, SWT.NONE);
        _label.setData(EJ_RWT.CUSTOM_VARIANT, getCSSKey());
        String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

        if (customCSSKey != null && customCSSKey.trim().length() > 0)
        {
            _label.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
        }
        _label.setText(_screenItemProperties.getLabel() == null ? "" : _screenItemProperties.getLabel());
    }


    @Override
    public ColumnLabelProvider createColumnLabelProvider(final EJScreenItemProperties item, EJScreenItemController controller)
    {
        
        return null;
    }

    @Override
    public EJRWTAbstractTableSorter getColumnSorter(final EJScreenItemProperties item, EJScreenItemController controller)
    {
       return null;
    }

    @Override
    public boolean isReadOnly()
    {
        return false;
    }

}
