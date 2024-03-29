/*******************************************************************************
 * Copyright 2013 CRESOFT AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Contributors: CRESOFT AG - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.renderers.html;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.html.HtmlProxyServiceHandler.HtmlGet;
import org.entirej.applicationframework.rwt.renderers.item.ControlDecorationSupport;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTButtonItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.utils.EJRWTItemRendererVisualContext;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJRWTExtHtmlViewItemRenderer implements EJRWTAppItemRenderer, FocusListener, Serializable
{

    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJScreenItemProperties          _screenItemProperties;
    protected EJItemProperties                _itemProperties;
    protected String                          _registeredItemName;
    protected Browser                         _textField;
    protected Label                           _label;
    protected boolean                         _isValid = true;
    protected boolean                         _mandatory;

    protected EJCoreVisualAttributeProperties _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties _initialVAProperties;
    protected ControlDecoration               _errorDecoration;
    protected ControlDecoration               _mandatoryDecoration;
    private EJRWTItemRendererVisualContext    _visualContext;

    protected Object                          _baseValue;
    protected Object                          _baseValueWithURl;
    protected String                          _baseValueKey;
    private EJMessage                         message;

    private boolean                           visible;

    protected boolean controlState(Control control)
    {
        return control != null && !control.isDisposed();

    }

    @Override
    public String formatValue(Object obj)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDisplayValue()
    {
        return (String) getValue();
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
        visible = _item.isVisible();
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
        _baseValueWithURl = null;

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

        return _baseValue;
    }

    @Override
    public boolean isEditAllowed()
    {
        if (controlState(_textField))
        {
            return _textField.isEnabled();
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

        return visible;
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
            _textField.setEnabled(editAllowed);
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
        if (value == null)
        {
            value = "";
        }
        _baseValue = _baseValueWithURl = value;
        try
        {
            if (_baseValueKey != null)
            {
                HtmlProxyServiceHandler.HtmlProxy.INSTANCE.unregister(_baseValueKey);
            }
            if (value != null)
            {
                _baseValueKey = HtmlProxyServiceHandler.HtmlProxy.INSTANCE.register(new HtmlGet()
                {

                    public String html()
                    {
                        return (String) _baseValueWithURl;
                    }
                    
                    public void action(String action)
                    {
                        try
                        {
                            EJBlockController blockController = _item.getBlock().getBlockController();
                            blockController.executeActionCommand(action, _item.getScreenType());

                        }
                        catch (Exception e)
                        {
                            _item.getBlock().getBlockController().getFormController().getFrameworkManager().handleException(e);
                        }
                    }
                });
            }
            {
                if (controlState(_textField))
                {
                    if (_baseValueKey != null)
                    {
                        StringBuffer url = new StringBuffer();
                        url.append(RWT.getServiceManager().getServiceHandlerUrl(HtmlProxyServiceHandler.SERVICE_HANDLER));
                        url.append("&req_id=");
                        url.append(_baseValueKey);
                        String encodedURL = RWT.getResponse().encodeURL(url.toString());
                        _textField.setUrl(encodedURL);
                        _baseValueWithURl = _baseValue;
                        if(_baseValueWithURl!=null)
                            _baseValueWithURl = ((String)_baseValueWithURl).replaceAll("%RAP_URL_PATH%", encodedURL);
                    }

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
        this.visible = visible;

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

    @Override
    public void setMessage(EJMessage message)
    {
        this.message = message;
        if (_errorDecoration != null && controlState(_textField) && !_errorDecoration.getControl().isDisposed())
        {
            ControlDecorationSupport.handleMessage(_errorDecoration, message);
        }

    }

    @Override
    public void clearMessage()
    {
        this.message = null;
        if (_errorDecoration != null && controlState(_textField) && !_errorDecoration.getControl().isDisposed())
        {
            _errorDecoration.setDescriptionText("");
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
            _item.itemValueChaged(value);
        setMandatoryBorder(_mandatory);
    }

    protected void setMandatoryBorder(boolean req)
    {

        if (_textField.isDisposed())
        {
            return;
        }

        if (req && getValue() == null)
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
                Object base = _baseValue;
                Object value = getValue();
                _item.itemValueChaged(value);
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
            _textField = new Browser(composite, SWT.NONE);

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
            _mandatoryDecoration.setDescriptionText(_screenItemProperties.getLabel() == null || _screenItemProperties.getLabel().isEmpty() ? "Required Item" : String.format("%s is required", _screenItemProperties.getLabel()));
            if (_isValid)
            {
                _errorDecoration.hide();
            }
            if (message != null)
            {
                setMessage(message);
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
        return true;
    }

    @Override
    public List<Object> getValidValues()
    {
        return Collections.emptyList();
    }

}
