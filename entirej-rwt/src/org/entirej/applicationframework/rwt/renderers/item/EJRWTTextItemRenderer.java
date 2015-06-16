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

import java.io.Serializable;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.components.EJRWTAbstractActionText;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.blocks.definition.interfaces.EJRWTSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTButtonItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.utils.EJRWTItemRendererVisualContext;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.enumerations.EJLovDisplayReason;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJRWTTextItemRenderer implements EJRWTAppItemRenderer, FocusListener, Serializable, EJRWTItemTextChangeNotifier
{
    private List<ChangeListener>              _changeListeners = new ArrayList<EJRWTItemTextChangeNotifier.ChangeListener>(1);
    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJScreenItemProperties          _screenItemProperties;
    protected EJItemProperties                _itemProperties;
    protected String                          _registeredItemName;
    protected Text                            _textField;
    protected EJRWTAbstractActionText         _actionControl;
    protected Label                           _valueLabel;
    protected Label                           _label;
    protected boolean                         _isValid         = true;
    protected boolean                         _mandatory;
    protected int                             _maxLength;
    protected boolean                         _displayValueAsLabel;
    protected boolean                         _displayValueAsProtected;
    protected boolean                         _valueChanged;
    protected final TextModifyListener        _modifyListener  = new TextModifyListener();
    protected VALUE_CASE                      _valueCase       = VALUE_CASE.DEFAULT;

    protected boolean                         _lovActivated;

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
        _maxLength = _rendererProps.getIntProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_MAXLENGTH, 0);
        _displayValueAsLabel = _rendererProps.getBooleanProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_DISPLAY_VAUE_AS_LABEL, false);
        _displayValueAsProtected = _rendererProps.getBooleanProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_DISPLAY_VAUE_AS_PROTECTED, false);
        final String caseProperty = _rendererProps.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_CASE);
        if (caseProperty != null && caseProperty.trim().length() > 0)
        {
            if (caseProperty.equals(EJRWTTextItemRendererDefinitionProperties.PROPERTY_CASE_LOWER))
            {
                _valueCase = VALUE_CASE.LOWER;
            }
            else if (caseProperty.equals(EJRWTTextItemRendererDefinitionProperties.PROPERTY_CASE_UPPER))
            {
                _valueCase = VALUE_CASE.UPPER;
            }
        }

        if (controlState(_actionControl))
        {
            _actionControl.setActionVisible(isLovActivated() && isEditAllowed());
        }
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
        if (controlState(_valueLabel))
        {
            _valueLabel.setToolTipText(hint == null ? "" : hint);
        }
    }

    @Override
    public void enableLovActivation(boolean activate)
    {
        _lovActivated = activate;
        if (_displayValueAsLabel)
        {
            return;
        }
        if (controlState(_actionControl))
        {
            _actionControl.setActionVisible(activate && isEditAllowed());
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
        if (_displayValueAsLabel)
        {
            return _valueLabel;
        }
        return _actionControl;
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
        if (_displayValueAsLabel)
        {
            if (controlState(_valueLabel))
            {
                _valueLabel.setText("");
            }
        }

        try
        {
            _modifyListener.enable = false;
            if (controlState(_textField))
            {
                _textField.setText("");
            }
        }
        finally
        {
            _modifyListener.enable = true;
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
        if (_displayValueAsLabel)
        {
            return _baseValue;
        }

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
        return _lovActivated;
    }

    @Override
    public boolean isVisible()
    {
        if (_displayValueAsLabel)
        {
            if (controlState(_valueLabel))
            {
                return _valueLabel.isVisible();
            }
        }
        else
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
        if (_displayValueAsLabel)
        {
            return true;
        }

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
        if (_displayValueAsLabel)
        {
            if (_valueLabel != null)
            {
                _valueLabel.forceFocus();
            }
        }
        else
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
        if (_displayValueAsLabel)
        {
            return;
        }
        if (controlState(_textField))
        {
            _textField.setEditable(editAllowed);
        }
        setMandatoryBorder(editAllowed && _mandatory);

        if (controlState(_actionControl))
        {
            _actionControl.setActionVisible(isLovActivated() && editAllowed);
        }
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

    private String toCaseValue(String string)
    {
        switch (_valueCase)
        {
            case LOWER:
                string = string.toLowerCase();
                break;
            case UPPER:
                string = string.toUpperCase();
                break;
        }

        return string;
    }

    @Override
    public void setValue(Object value)
    {
        _baseValue = value;
        _valueChanged = false;
        try
        {
            _modifyListener.enable = false;
            if (_displayValueAsLabel)
            {
                if (controlState(_valueLabel))
                {
                    if (value == null)
                    {
                        _valueLabel.setText("");
                        _valueLabel.setToolTipText("");
                    }
                    else
                    {
                        _valueLabel.setText(toCaseValue(value.toString()));
                        _valueLabel.setToolTipText(value.toString());
                    }
                }
            }
            else
            {
                if (controlState(_textField))
                {
                    if (value != null)
                    {
                        if (_maxLength > 0 && value.toString().length() > _maxLength)
                        {
                            EJMessage message = new EJMessage("The value for item, " + _item.getReferencedItemProperties().getBlockName() + "."
                                    + _item.getReferencedItemProperties().getName() + " is too long for its field definition.");
                            throw new EJApplicationException(message);
                        }
                    }

                    _textField.setText(value == null ? "" : toCaseValue(value.toString()));
                    setMandatoryBorder(_mandatory);
                }
            }
        }
        finally
        {
            _modifyListener.enable = true;
        }
    }

    @Override
    public void setVisible(boolean visible)
    {
        if (_displayValueAsLabel)
        {
            if (controlState(_valueLabel))
            {
                _valueLabel.setVisible(visible);
            }
        }
        else
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
        if (_displayValueAsLabel)
        {
            return;
        }
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

        fireTextChange();
    }

    public void valueChanged()
    {
        Object base = _baseValue;
        Object value = getValue();

        if (!_textField.isFocusControl())
        {

            if (_valueChanged || ((base == null && value != null) || (base != null && value == null) || (value != null && !value.equals(base))))
                _item.itemValueChaged();
            _valueChanged = false;
        }
        else
        {
            _valueChanged = _valueChanged || ((base == null && value != null) || (base != null && value == null) || (value != null && !value.equals(base)));
        }
        setMandatoryBorder(_mandatory);
        fireTextChange();
    }

    protected void setMandatoryBorder(boolean req)
    {
        if (_displayValueAsLabel || _mandatoryDecoration == null || !controlState(_textField))
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
                if (_valueChanged)
                {
                    _valueChanged = false;
                    _item.itemValueChaged();
                    setMandatoryBorder(_mandatory);

                }
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
        if (_displayValueAsLabel)
        {
            if (controlState(_valueLabel))
            {
                _valueLabel.setBackground(background != null ? background : _visualContext.getBackgroundColor());
            }
        }
        else
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
        if (_displayValueAsLabel)
        {

            if (controlState(_valueLabel))
            {
                _valueLabel.setForeground(foreground != null ? foreground : _visualContext.getForegroundColor());
            }
        }
        else
        {
            if (controlState(_textField))
            {
                _textField.setForeground(foreground != null ? foreground : _visualContext.getForegroundColor());
            }
        }
    }

    private void refreshFont()
    {
        if (_displayValueAsLabel)
        {
            if (controlState(_valueLabel))
            {
                _valueLabel.setFont(EJRWTVisualAttributeUtils.INSTANCE.getFont(_visualAttributeProperties, _visualContext.getItemFont()));
            }

        }
        else
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

    protected Text newTextField(Composite composite, int style)
    {
        final Text text = new Text(composite, style);
        if (_rendererProps != null && _rendererProps.getBooleanProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_SELECT_ON_FOCUS, false))
        {
            text.addFocusListener(new FocusListener()
            {
                @Override
                public void focusLost(FocusEvent arg0)
                {
                    // ignore
                }

                @Override
                public void focusGained(FocusEvent arg0)
                {
                    text.selectAll();
                }
            });
        }
        return text;
    }

    public Control createCustomButtonControl(Composite parent)
    {
        return null;
    }

    @Override
    public void createComponent(Composite composite)
    {

        String alignmentProperty = _rendererProps.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT);
        if (alignmentProperty == null)
        {
            alignmentProperty = _rendererProps.getStringProperty("ALLIGNMENT");
        }
        String hint = _screenItemProperties.getHint();

        if (_displayValueAsLabel)
        {
            _valueLabel = newVlaueLabel(composite);
            _valueLabel.setData(_itemProperties.getName());
            _valueLabel.setData(EJ_RWT.CUSTOM_VARIANT, getCSSKey());
            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

            if (customCSSKey != null && customCSSKey.trim().length() > 0)
            {
                _valueLabel.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            }
            if (hint != null && hint.trim().length() > 0)
            {
                _valueLabel.setToolTipText(hint);
            }
            setValueLabelAlign(alignmentProperty);
            _visualContext = new EJRWTItemRendererVisualContext(_valueLabel.getBackground(), _valueLabel.getForeground(), _valueLabel.getFont());
            setInitialValue(_baseValue);
        }
        else
        {
            final String alignmentProp = alignmentProperty;
            _actionControl = new EJRWTAbstractActionText(composite)
            {
                @Override
                public Text createText(Composite parent)
                {
                    int style = SWT.BORDER;
                    if (_displayValueAsProtected)
                    {
                        style = style | SWT.PASSWORD;
                    }
                    _textField = newTextField(parent, getComponentStyle(alignmentProp, style));
                    _textField.setData(EJ_RWT.CUSTOM_VARIANT, getCSSKey());
                    String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

                    if (customCSSKey != null && customCSSKey.trim().length() > 0)
                    {
                        _textField.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                    }
                    return _textField;
                }

                @Override
                public Control createCustomActionLabel(Composite parent)
                {
                    return createCustomButtonControl(parent);
                }

                @Override
                public Control createActionLabel(Composite parent)
                {
                    Label label = new Label(parent, SWT.NONE);
                    label.setImage(EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_FIND_LOV));
                    label.addFocusListener(EJRWTTextItemRenderer.this);
                    label.addMouseListener(new MouseListener()
                    {
                        private static final long serialVersionUID = 529634857284996692L;

                        @Override
                        public void mouseUp(MouseEvent arg0)
                        {
                            _item.getItemLovController().displayLov(EJLovDisplayReason.LOV);
                        }

                        @Override
                        public void mouseDown(MouseEvent arg0)
                        {

                        }

                        @Override
                        public void mouseDoubleClick(MouseEvent arg0)
                        {

                        }
                    });

                    final EJFrameworkExtensionProperties rendererProp = EJCoreProperties.getInstance().getApplicationDefinedProperties();
                    final EJFrameworkExtensionProperties propertyGroup = rendererProp.getPropertyGroup(EJRWTSingleRecordBlockDefinitionProperties.ACTION_GROUP);

                    String lovKey = "SHIFT+ARROW_DOWN";
                    if (propertyGroup != null)
                    {
                        lovKey = propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_LOV_KEY);
                    }

                    if (lovKey == null)
                    {
                        lovKey = "SHIFT+ARROW_DOWN";
                    }

                    String[] keys = new String[] { lovKey, "ENTER", "RETURN", "CR" };
                    label.setData(EJ_RWT.ACTIVE_KEYS, keys);
                    getTextControl().setData(EJ_RWT.ACTIVE_KEYS, keys);
                    addKeyListener(new KeyListener()
                    {
                        @Override
                        public void keyReleased(KeyEvent arg0)
                        {

                            if ((arg0.stateMask & SWT.SHIFT) != 0 && arg0.keyCode == SWT.ARROW_DOWN && isLovActivated())
                            {
                                _item.getItemLovController().displayLov(EJLovDisplayReason.LOV);
                            }

                            if (arg0.keyCode == 13 && (SWT.MULTI != (_textField.getStyle() & SWT.MULTI) || (arg0.stateMask & SWT.CONTROL) != 0))
                            {
                                if (_valueChanged)
                                {
                                    _valueChanged = false;
                                    _item.itemValueChaged();
                                    setMandatoryBorder(_mandatory);
                                }
                            }

                        }

                        @Override
                        public void keyPressed(KeyEvent arg0)
                        {
                        }
                    });
                    return label;
                }
            };

            if (_maxLength > 0)
            {
                _textField.setTextLimit(_maxLength);
            }
            if (hint != null && hint.trim().length() > 0)
            {
                _textField.setToolTipText(hint);
            }

            _visualContext = new EJRWTItemRendererVisualContext(_textField.getBackground(), _textField.getForeground(), _textField.getFont());

            _textField.setData(_item.getReferencedItemProperties().getName());
            _textField.addFocusListener(this);

            _mandatoryDecoration = new ControlDecoration(_actionControl, SWT.TOP | SWT.LEFT);
            _errorDecoration = new ControlDecoration(_actionControl, SWT.TOP | SWT.LEFT);
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
            _textField.addModifyListener(_modifyListener);
            // TODO: Move to client side validation on Rap 2.4
            if (_valueCase != null && _valueCase != VALUE_CASE.DEFAULT)
            {

                _textField.addVerifyListener(new VerifyListener()
                {
                    @Override
                    public void verifyText(VerifyEvent event)
                    {
                        String caseValue = toCaseValue(event.text);
                        if (!event.text.equals(caseValue))
                        {
                            event.text = caseValue;
                        }
                    }
                });
            }
            setInitialValue(_baseValue);
        }
    }

    protected void setValueLabelAlign(String alignmentProperty)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJRWTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_LEFT))
            {
                _valueLabel.setAlignment(SWT.LEFT);
            }
            else if (alignmentProperty.equals(EJRWTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_RIGHT))
            {
                _valueLabel.setAlignment(SWT.RIGHT);
            }
            else if (alignmentProperty.equals(EJRWTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_CENTER))
            {
                _valueLabel.setAlignment(SWT.CENTER);
            }
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

    class TextModifyListener implements ModifyListener
    {
        protected boolean enable = true;

        @Override
        public void modifyText(ModifyEvent event)
        {

            if (enable)
            {

                valueChanged();
            }
        }

    }

    @Override
    public ColumnLabelProvider createColumnLabelProvider(final EJScreenItemProperties item, EJScreenItemController controller)
    {
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
                if (element instanceof EJDataRecord)
                {
                    EJDataRecord record = (EJDataRecord) element;
                    Object value = record.getValue(item.getReferencedItemName());
                    if (value instanceof String)
                    {
                        return value.toString();
                    }
                }
                return "";
            }

        };
        return provider;
    }

    @Override
    public EJRWTAbstractTableSorter getColumnSorter(final EJScreenItemProperties item, EJScreenItemController controller)
    {
        final Collator compareCollator = Collator.getInstance();
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
                        if (value1 == null && value2 == null)
                        {
                            return 0;
                        }
                        if (value1 == null && value2 != null)
                        {
                            return -1;
                        }
                        if (value1 != null && value2 == null)
                        {
                            return 1;
                        }
                        return compareCollator.compare(value1, value2);
                    }
                }
                return 0;
            }

            @Override
            public int compareNumber(Viewer viewer, Object e1, Object e2,String format)
            {
                if (e1 instanceof EJDataRecord && e2 instanceof EJDataRecord)
                {
                    EJDataRecord d1 = (EJDataRecord) e1;
                    EJDataRecord d2 = (EJDataRecord) e2;
                    if (d1 != null && d2 != null)
                    {

                        Object value1 = d1.getValue(item.getReferencedItemName());
                        Object value2 = d2.getValue(item.getReferencedItemName());
                        if (value1 == null && value2 == null)
                        {
                            return 0;
                        }
                        if (value1 == null && value2 != null)
                        {
                            return -1;
                        }
                        if (value1 != null && value2 == null)
                        {
                            return 1;
                        }

                        if (value1 instanceof String && value2 instanceof String)
                        {
                            final    DecimalFormat frm = new DecimalFormat(format);
                            try
                            {
                                
                                Number dv1 = frm.parse((String) value1);
                                Number dv2 = frm.parse((String) value2);

                                return Double.compare(dv1.doubleValue(), dv2.doubleValue());
                            }
                            catch (NumberFormatException f)
                            {
                                return compareCollator.compare(value1, value2);
                            }
                            catch (ParseException e)
                            {
                                return compareCollator.compare(value1, value2);
                            }
                        }
                        return compareCollator.compare(value1, value2);

                    }
                }
                return 0;
            }
            @Override
            public int compareDate(Viewer viewer, Object e1, Object e2,String formt)
            {
                if (e1 instanceof EJDataRecord && e2 instanceof EJDataRecord)
                {
                    EJDataRecord d1 = (EJDataRecord) e1;
                    EJDataRecord d2 = (EJDataRecord) e2;
                    if (d1 != null && d2 != null)
                    {
                        
                        Object value1 = d1.getValue(item.getReferencedItemName());
                        Object value2 = d2.getValue(item.getReferencedItemName());
                        if (value1 == null && value2 == null)
                        {
                            return 0;
                        }
                        if (value1 == null && value2 != null)
                        {
                            return -1;
                        }
                        if (value1 != null && value2 == null)
                        {
                            return 1;
                        }
                        
                        if (value1 instanceof String && value2 instanceof String)
                        {
                            SimpleDateFormat format = new SimpleDateFormat(formt);
                            try
                            {
                                Date dv1 = format.parse((String) value1);
                                Date dv2 = format.parse((String) value1);
                                
                                return dv1.compareTo(dv2);
                            }
                            
                            catch (ParseException e)
                            {
                                //ignore 
                            }
                        }
                        return compareCollator.compare(value1, value2);
                        
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

    @Override
    public void addListener(ChangeListener listener)
    {
        _changeListeners.add(listener);
    }

    @Override
    public void removeListener(ChangeListener listener)
    {
        _changeListeners.remove(listener);
    }

    protected void fireTextChange()
    {
        for (ChangeListener listener : new ArrayList<ChangeListener>(_changeListeners))
        {
            listener.changed();
        }
    }
}
