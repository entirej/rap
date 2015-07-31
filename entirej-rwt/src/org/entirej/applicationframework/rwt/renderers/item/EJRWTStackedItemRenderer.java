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
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.components.EJRWTAbstractPanelAction;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJStackedPane;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.blocks.definition.interfaces.EJRWTSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.EJRWTDateItemRenderer.DateFormats;
import org.entirej.applicationframework.rwt.renderers.item.EJRWTDateItemRenderer.MultiDateFormater;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTButtonItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.utils.EJRWTItemRendererVisualContext;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJMessageFactory;
import org.entirej.framework.core.data.EJStackedItemRendererValue;
import org.entirej.framework.core.enumerations.EJFrameworkMessage;
import org.entirej.framework.core.enumerations.EJLovDisplayReason;
import org.entirej.framework.core.enumerations.EJStackedItemRendererType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreInsertScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreMainScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreQueryScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreUpdateScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJRWTStackedItemRenderer implements EJRWTAppItemRenderer, FocusListener, Serializable, EJRWTItemTextChangeNotifier
{
    private List<ChangeListener>              _changeListeners = new ArrayList<EJRWTItemTextChangeNotifier.ChangeListener>(1);
    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJScreenItemProperties          _screenItemProperties;
    protected EJItemProperties                _itemProperties;
    protected String                          _registeredItemName;
    protected EJRWTAbstractPanelAction        _actionControl;

    private EJRWTEntireJStackedPane           stackedPane;
    protected Label                           _label;
    protected boolean                         _isValid         = true;
    protected boolean                         _mandatory;

    protected boolean                         _valueChanged;
    protected final TextModifyListener        _modifyListener  = new TextModifyListener();

    protected boolean                         _lovActivated;

    protected EJCoreVisualAttributeProperties _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties _initialVAProperties;
    protected ControlDecoration               _errorDecoration;
    protected ControlDecoration               _mandatoryDecoration;

    private DecimalFormat                     _decimalFormatter;

    private MultiDateFormater                 _dateFormat;

    public enum NUMBER_TYPE
    {
        NUMBER, INTEGER, FLOAT, BIG_DECIMAL, DOUBLE, LONG
    };

    private NUMBER_TYPE                  _numberType = NUMBER_TYPE.NUMBER;

    protected EJStackedItemRendererValue _baseValue;
    protected EJStackedItemRendererValue _intbaseValue;

    protected boolean controlState(Control control)
    {
        return control != null && !control.isDisposed();

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
            if (controlState(stackedPane) && _rendererProps != null)
            {
                String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

                EJStackedItemRendererType[] values = EJStackedItemRendererType.values();
                for (EJStackedItemRendererType type : values)
                {
                    Control control = stackedPane.getControl(type.name());
                    if (control != null && controlState(control))
                    {
                        if (customCSSKey != null && customCSSKey.trim().length() > 0)
                        {
                            control.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                        }
                        else
                        {
                            control.setData(EJ_RWT.CUSTOM_VARIANT, control.getData(EJ_RWT.CUSTOM_VARIANT + "_DEF"));// reset
                                                                                                                    // default
                        }
                    }
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
        updateStackHint(hint);
    }

    @Override
    public void enableLovActivation(boolean activate)
    {
        _lovActivated = activate;

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

        return _actionControl;
    }

    @Override
    public void clearValue()
    {
        _baseValue = null;

        try
        {
            _modifyListener.enable = false;
            EJStackedItemRendererType[] values = EJStackedItemRendererType.values();
            for (EJStackedItemRendererType type : values)
            {
                Control control = stackedPane.getControl(type.name());
                if (control != null && controlState(control))
                {
                    if (control instanceof Label)
                    {
                        ((Label) control).setText("");
                    }
                    else if (control instanceof Text)
                    {
                        ((Text) control).setText("");
                    }
                    else if (control instanceof Combo)
                    {
                        ((Combo) control).setText("");
                    }
                    // check_box/combo
                }
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

        if (controlState(stackedPane))
        {
            processStackValue();

            return _baseValue;
        }

        return _baseValue;
    }

    private void processStackValue()
    {
        if (_baseValue != null)
        {
            switch (_baseValue.getType())
            {

                case TEXT:
                {
                    Text control = (Text) stackedPane.getControl(_baseValue.getType().name());
                    String value = control.getText();

                    if (value == null || value.length() == 0)
                    {
                        value = null;
                    }
                    _baseValue.setValue(value);
                    break;
                }
                case NUMBER:
                {

                    Text control = (Text) stackedPane.getControl(_baseValue.getType().name());
                    Number value = null;
                    if (control.getText() != null && control.getText().isEmpty())
                    {
                        value = null;
                    }
                    else {
                        try
                        {
                            value = _decimalFormatter.parse(control.getText());
                        }
                        catch (ParseException e)
                        {
                            // ignore error
                        }
                    }

                    if (value == null)
                    {
                        _baseValue.setValue(value);
                        return;
                    }
                    try
                    {
                        switch (_numberType)
                        {
                            case INTEGER:
                                _baseValue.setValue(value.intValue());
                                break;
                            case LONG:
                                _baseValue.setValue(value.longValue());
                                break;
                            case FLOAT:
                                _baseValue.setValue(value.floatValue());
                                break;
                            case DOUBLE:
                                _baseValue.setValue(value.doubleValue());
                                break;
                            case BIG_DECIMAL:
                                _baseValue.setValue(new BigDecimal(value.toString()));
                                break;
                            default:
                                _baseValue.setValue(value);
                        }
                    }
                    catch (NumberFormatException nfe)
                    {
                        nfe.printStackTrace();
                    }
                    break;
                }
                case DATE:
                {

                    Text control = (Text) stackedPane.getControl(_baseValue.getType().name());
                    Date value = null;
                    try
                    {
                        if (control.getText() != null && control.getText().isEmpty())
                        {
                            value = null;
                        }
                        else if (control.getText() != null)
                        {
                            value = _dateFormat.parse(control.getText());
                        }

                        // convert to correct type if need
                        if (value != null)
                        {
                            String dataTypeClass = _baseValue.getValue() != null ? _baseValue.getValue().getClass().getName() : Date.class.getName();
                            if (dataTypeClass.equals("java.sql.Date"))
                            {
                                value = new java.sql.Date(value.getTime());
                            }
                            else if (dataTypeClass.equals("java.sql.Time"))
                            {
                                value = new java.sql.Time(value.getTime());
                            }
                            else if (dataTypeClass.equals("java.sql.Timestamp"))
                            {
                                value = new java.sql.Timestamp(value.getTime());
                            }

                        }
                        _baseValue.setValue(value);
                    }
                    catch (ParseException e)
                    {
                        // ignore error
                    }
                    break;
                }

                default:
                    break;
            }
        }

    }

    private static NUMBER_TYPE getNumberType(Object object)
    {
        if (object == null)
            return NUMBER_TYPE.NUMBER;
        final String datatypeClassName = object.getClass().getName();
        NUMBER_TYPE numberType;
        if (datatypeClassName.equals(Integer.class.getName()))
        {
            numberType = NUMBER_TYPE.INTEGER;
        }
        else if (datatypeClassName.equals(Float.class.getName()))
        {
            numberType = NUMBER_TYPE.FLOAT;
        }
        else if (datatypeClassName.endsWith(Long.class.getName()))
        {
            numberType = NUMBER_TYPE.LONG;
        }
        else if (datatypeClassName.endsWith(Double.class.getName()))
        {
            numberType = NUMBER_TYPE.DOUBLE;
        }
        else
        {

            numberType = NUMBER_TYPE.BIG_DECIMAL;
        }
        return numberType;
    }

    @Override
    public boolean isEditAllowed()
    {
        EJStackedItemRendererType[] values = EJStackedItemRendererType.values();
        for (EJStackedItemRendererType type : values)
        {
            Control control = stackedPane.getControl(type.name());
            if (control != null && controlState(control))
            {
                if (control instanceof Button)
                {
                    return ((Button) control).isEnabled();
                }
                else if (control instanceof Text)
                {
                    return ((Text) control).getEditable();
                }
                else if (control instanceof Combo)
                {
                    return ((Combo) control).getEnabled();
                }
                // check_box/combo
            }
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

        {
            if (controlState(stackedPane))
            {
                return stackedPane.isVisible();
            }
        }

        return false;
    }

    @Override
    public boolean isValid()
    {

        if (_isValid)
        {
            if (_mandatory && (getValue() == null || _baseValue.getValue()==null))
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

            if (controlState(stackedPane))
            {
                stackedPane.forceFocus();
            }
        }
    }

    @Override
    public void setEditAllowed(boolean editAllowed)
    {
        EJStackedItemRendererType[] values = EJStackedItemRendererType.values();
        for (EJStackedItemRendererType type : values)
        {
            Control control = stackedPane.getControl(type.name());
            if (control != null && controlState(control))
            {
                if (control instanceof Button)
                {
                    ((Button) control).setEnabled(editAllowed);
                }
                else if (control instanceof Text)
                {
                    ((Text) control).setEditable(editAllowed);
                }
                else if (control instanceof Combo)
                {
                    ((Combo) control).setEnabled(editAllowed);
                }
            }
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

    @Override
    public void setValue(Object value)
    {
        setLabel("");
        if(controlState(stackedPane))
        {
            _errorDecoration.hide();
        }
        _valueChanged = false;
        try
        {
            _modifyListener.enable = false;

            if (!(value instanceof EJStackedItemRendererValue))
            {
                if (_baseValue != null)
                {
                    _baseValue.setValue(value);
                    setStackValue();
                    return;
                }
            }
            else
            {
                _baseValue = (EJStackedItemRendererValue) value;
                _intbaseValue = _baseValue;
            }

            {
                if (controlState(stackedPane))
                {

                    extractValuetoUi();
                    setMandatoryBorder(_mandatory);
                }
            }
        }
        finally
        {
            _modifyListener.enable = true;
        }
    }

    private void extractValuetoUi()
    {
        if (!controlState(stackedPane))
            return;

        _item.setItemLovController(null);
        enableLovActivation(false);
        
        if (_baseValue != null)
        {
            if (controlState(_label) && _baseValue.getType()!=EJStackedItemRendererType.SPACER)
            {
                if (_baseValue.getLabel() != null)
                {
                    setLabel(_baseValue.getLabel());
                }
                else
                {
                    setLabel(_screenItemProperties.getLabel() == null ? "" : _screenItemProperties.getLabel());
                }

            }

            if (_baseValue.getTooltip() != null)
            {
                setHint(_baseValue.getLabel());
            }
            else
            {
                setHint(_screenItemProperties.getHint() == null ? "" : _screenItemProperties.getHint());
            }

            stackedPane.showPane(_baseValue.getType().name());

            // setLOV mapping
            {
                if (_item.getProperties() instanceof EJCoreInsertScreenItemProperties)
                {
                    ((EJCoreInsertScreenItemProperties) _item.getProperties()).setActionCommand(_baseValue.getActionCommand());
                }
                else if (_item.getProperties() instanceof EJCoreQueryScreenItemProperties)
                {
                    ((EJCoreQueryScreenItemProperties) _item.getProperties()).setActionCommand(_baseValue.getActionCommand());
                }
                else if (_item.getProperties() instanceof EJCoreUpdateScreenItemProperties)
                {
                    ((EJCoreUpdateScreenItemProperties) _item.getProperties()).setActionCommand(_baseValue.getActionCommand());
                }
                else if (_item.getProperties() instanceof EJCoreMainScreenItemProperties)
                {
                   
                    ((EJCoreMainScreenItemProperties) _item.getProperties()).setActionCommand(_baseValue.getActionCommand());
                }
                _item.setItemLovController(_baseValue.getLovMapping());
                enableLovActivation(_item.getItemLovController()!=null);

            }

            setStackValue();
        }
        else
        {
            stackedPane.showPane(EJStackedItemRendererType.SPACER.name());// switch
                                                                          // to
                                                                          // empty
        }

    }

    private void setStackValue()
    {
        Object value = _baseValue.getValue();
        try
        {
            _modifyListener.enable = false;
            EJStackedItemRendererType[] values = EJStackedItemRendererType.values();
            for (EJStackedItemRendererType type : values)
            {
                Control control = stackedPane.getControl(type.name());
                if (control != null && controlState(control))
                {
                    if (control instanceof Label)
                    {
                        ((Label) control).setText("");
                    }
                    else if (control instanceof Text)
                    {
                        ((Text) control).setText("");
                    }
                    else if (control instanceof Combo)
                    {
                        ((Combo) control).setText("");
                    }
                    // check_box/combo
                }
            }
        }
        finally
        {
            _modifyListener.enable = true;
        }
        if (value != null)
        {
            switch (_baseValue.getType())
            {
                case LABEL:
                {
                    Label control = (Label) stackedPane.getControl(_baseValue.getType().name());
                    control.setText(value.toString());
                    break;
                }
                case TEXT:
                {
                    Text control = (Text) stackedPane.getControl(_baseValue.getType().name());
                    control.setText(value.toString());
                    break;
                }
                case NUMBER:
                {
                    _numberType = getNumberType(value);
                    _decimalFormatter = createFormatter(_numberType);
                    if (value != null && !Number.class.isAssignableFrom(value.getClass()))
                    {
                        EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM, _item.getName(),
                                Number.class.getName(), value.getClass().getName());
                        throw new IllegalArgumentException(message.getMessage());
                    }
                    Text control = (Text) stackedPane.getControl(_baseValue.getType().name());
                    control.setText(_decimalFormatter.format(value));
                    break;
                }
                case DATE:
                {
                    createDateFormat();
                    if (value != null && !Date.class.isAssignableFrom(value.getClass()))
                    {
                        EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM, _item.getName(),
                                Date.class.getName(), value.getClass().getName());
                        throw new IllegalArgumentException(message.getMessage());
                    }
                    Text control = (Text) stackedPane.getControl(_baseValue.getType().name());
                    control.setText(_dateFormat.format(value));
                    control.setMessage(_dateFormat.toFormatString());
                    break;
                }
                
                case SPACER:
                {
                    setLabel("");
                    break;
                }

                default:
                    break;
            }
        }
    }

    @Override
    public void setVisible(boolean visible)
    {

        {
            if (controlState(stackedPane))
            {
                stackedPane.setVisible(visible);
            }

            if (controlState(_label))
            {
                _label.setVisible(visible);
            }
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

        if (_errorDecoration != null && controlState(stackedPane))
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
        Object base = _intbaseValue;
        Object value = getValue();

        Control activeControl = stackedPane.getActiveControl();
        if (!(activeControl instanceof Text && activeControl.isFocusControl()))
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

        if (req && (getValue() == null || _baseValue.getValue()==null))
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
        if (controlState(stackedPane))
        {
            stackedPane.getActiveControl().forceFocus();
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

        EJStackedItemRendererType[] values = EJStackedItemRendererType.values();
        for (EJStackedItemRendererType type : values)
        {
            Control control = stackedPane.getControl(type.name());

            if (control != null && controlState(control))
            {
                EJRWTItemRendererVisualContext _visualContext = (EJRWTItemRendererVisualContext) control.getData("EJRWTItemRendererVisualContext");
                control.setBackground(background != null ? background : _visualContext.getBackgroundColor());
            }
        }

    }

    private void refreshForeground()
    {

        Color foreground = EJRWTVisualAttributeUtils.INSTANCE.getForeground(_visualAttributeProperties);

        EJStackedItemRendererType[] values = EJStackedItemRendererType.values();
        for (EJStackedItemRendererType type : values)
        {
            Control control = stackedPane.getControl(type.name());
            if (control != null && controlState(control))
            {
                EJRWTItemRendererVisualContext _visualContext = (EJRWTItemRendererVisualContext) control.getData("EJRWTItemRendererVisualContext");
                control.setForeground(foreground != null ? foreground : _visualContext.getForegroundColor());
            }
        }

    }

    private void refreshFont()
    {

        EJStackedItemRendererType[] values = EJStackedItemRendererType.values();
        for (EJStackedItemRendererType type : values)
        {
            Control control = stackedPane.getControl(type.name());
            if (control != null && controlState(control))
            {
                EJRWTItemRendererVisualContext _visualContext = (EJRWTItemRendererVisualContext) control.getData("EJRWTItemRendererVisualContext");
                control.setFont(EJRWTVisualAttributeUtils.INSTANCE.getFont(_visualAttributeProperties, _visualContext.getItemFont()));
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

        String alignmentProperty = _rendererProps.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT);
        if (alignmentProperty == null)
        {
            alignmentProperty = _rendererProps.getStringProperty("ALLIGNMENT");
        }

        _numberType = getNumberType(_baseValue != null ? _baseValue.getValue() : null);
        _decimalFormatter = createFormatter(_numberType);
        createDateFormat();

        String hint = _screenItemProperties.getHint();

        {
            final String alignmentProp = alignmentProperty;
            _actionControl = new EJRWTAbstractPanelAction(composite)
            {
                @Override
                public Composite createPanel(Composite parent)
                {
                    stackedPane = new EJRWTEntireJStackedPane(parent);

                    // _textField.setData(EJ_RWT.CUSTOM_VARIANT, getCSSKey());
                    // String customCSSKey =
                    // _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);
                    //
                    // if (customCSSKey != null && customCSSKey.trim().length()
                    // > 0)
                    // {
                    // _textField.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                    // }
                    buildStackUI(stackedPane);

                    return stackedPane;
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
                    label.addFocusListener(EJRWTStackedItemRenderer.this);
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
                    return label;
                }
            };

            if (hint != null && hint.trim().length() > 0)
            {
                updateStackHint(hint);
            }

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

            setInitialValue(_baseValue);
        }
    }

    private void updateStackHint(String hint)
    {
        EJStackedItemRendererType[] values = EJStackedItemRendererType.values();
        for (EJStackedItemRendererType type : values)
        {
            Control control = stackedPane.getControl(type.name());
            if (control != null && controlState(control))
            {
                control.setToolTipText(hint == null ? "" : hint);
            }
        }

    }

    void connectLOVAction(final Text text)
    {
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

        text.setData(EJ_RWT.ACTIVE_KEYS, keys);
        text.addKeyListener(new KeyListener()
        {
            @Override
            public void keyReleased(KeyEvent arg0)
            {

                if ((arg0.stateMask & SWT.SHIFT) != 0 && arg0.keyCode == SWT.ARROW_DOWN && isLovActivated())
                {
                    _item.getItemLovController().displayLov(EJLovDisplayReason.LOV);
                }

                if (arg0.keyCode == 13 && (SWT.MULTI != (text.getStyle() & SWT.MULTI) || (arg0.stateMask & SWT.CONTROL) != 0))
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
    }

    protected void buildStackUI(EJRWTEntireJStackedPane stackedPane)
    {
        // create stack UI for eash type

        // EJStackedItemRendererType.LABEL;
        {
            Label label = new Label(stackedPane, SWT.NONE);
            label.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_LABEL);
            label.setData(EJ_RWT.CUSTOM_VARIANT + "_DEF", EJ_RWT.CSS_CV_ITEM_LABEL);
            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

            if (customCSSKey != null && customCSSKey.trim().length() > 0)
            {
                label.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            }
            stackedPane.add(EJStackedItemRendererType.LABEL.name(), label);
            label.setData(_item.getReferencedItemProperties().getName());
            label.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(label.getBackground(), label.getForeground(), label.getFont()));

        }
        // EJStackedItemRendererType.SPACER;
        {
            Label label = new Label(stackedPane, SWT.NONE);
            // label.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_LABEL);
            // label.setData(EJ_RWT.CUSTOM_VARIANT+"_DEF",
            // EJ_RWT.CSS_CV_ITEM_LABEL);
            // String customCSSKey =
            // _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);
            //
            // if (customCSSKey != null && customCSSKey.trim().length() > 0)
            // {
            // label.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            // }
            stackedPane.add(EJStackedItemRendererType.SPACER.name(), label);
            label.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(label.getBackground(), label.getForeground(), label.getFont()));
        }

        // EJStackedItemRendererType.TEXT;
        {

            Text textField = new Text(stackedPane, SWT.BORDER);
            connectLOVAction(textField);
            textField.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_TEXT);
            textField.setData(EJ_RWT.CUSTOM_VARIANT + "_DEF", EJ_RWT.CSS_CV_ITEM_TEXT);
            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);
            textField.setData(_item.getReferencedItemProperties().getName());
            textField.addFocusListener(this);
            if (customCSSKey != null && customCSSKey.trim().length() > 0)
            {
                textField.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            }
            textField.addModifyListener(_modifyListener);
            stackedPane.add(EJStackedItemRendererType.TEXT.name(), textField);
            textField.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(textField.getBackground(), textField.getForeground(),
                    textField.getFont()));

        }
        // EJStackedItemRendererType.NUMBER;
        {

            final Text textField = new Text(stackedPane, SWT.BORDER | SWT.RIGHT);
            textField.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_NUMBER);
            textField.setData(EJ_RWT.CUSTOM_VARIANT + "_DEF", EJ_RWT.CSS_CV_ITEM_NUMBER);
            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);
            textField.setData(_item.getReferencedItemProperties().getName());
            textField.addFocusListener(this);
            if (customCSSKey != null && customCSSKey.trim().length() > 0)
            {
                textField.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            }

            textField.addModifyListener(new ModifyListener()
            {
                @Override
                public void modifyText(ModifyEvent arg0)
                {
                    if (textField.getText().trim().length() > 0)
                    {
                        try
                        {
                            _decimalFormatter.parse(textField.getText());
                            _errorDecoration.hide();
                        }
                        catch (ParseException e)
                        {
                            _rendererProps.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_FORMAT);

                            _errorDecoration.setDescriptionText(String.format("Invalid Number format. Should be %s ",
                                    _rendererProps.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_FORMAT)));
                            _errorDecoration.show();
                        }
                    }
                }
            });
            connectLOVAction(textField);
            textField.addModifyListener(_modifyListener);
            textField.addFocusListener(new FocusListener()
            {
                @Override
                public void focusLost(FocusEvent arg0)
                {
                    try
                    {
                        _modifyListener.enable = false;
                        Object value = getValue();
                        if (value != null && _baseValue.getValue()!=null)
                        {
                            textField.setText(_decimalFormatter.format(_baseValue.getValue()));
                        }
                        else
                        {
                            textField.setText("");
                        }
                    }
                    finally
                    {
                        _modifyListener.enable = true;
                    }
                }

                @Override
                public void focusGained(FocusEvent arg0)
                {

                }
            });

            stackedPane.add(EJStackedItemRendererType.NUMBER.name(), textField);
            textField.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(textField.getBackground(), textField.getForeground(),
                    textField.getFont()));

        }
        // EJStackedItemRendererType.DATE;
        {

            final Text textField = new Text(stackedPane, SWT.BORDER | SWT.RIGHT);
            textField.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_DATE);
            textField.setData(EJ_RWT.CUSTOM_VARIANT + "_DEF", EJ_RWT.CSS_CV_ITEM_DATE);
            textField.setData(_item.getReferencedItemProperties().getName());
            textField.addFocusListener(this);
            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);
            connectLOVAction(textField);
            if (customCSSKey != null && customCSSKey.trim().length() > 0)
            {
                textField.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            }

            textField.addModifyListener(_modifyListener);
            textField.addModifyListener(new ModifyListener()
            {
                @Override
                public void modifyText(ModifyEvent arg0)
                {
                    if (textField.getText().trim().length() > 0)
                    {
                        try
                        {
                            _dateFormat.parse(textField.getText());
                            _errorDecoration.hide();
                        }
                        catch (ParseException e)
                        {

                            String format = _dateFormat.toFormatString();
                            if (format == null || format.length() == 0)
                            {
                                format = "eg: " + _dateFormat.format(new Date());
                            }
                            _errorDecoration.setDescriptionText(String.format("Invalid Date format. Should be %s ", format));
                            _errorDecoration.show();
                        }
                    }
                }
            });
            textField.addFocusListener(new FocusListener()
            {
                @Override
                public void focusLost(FocusEvent arg0)
                {
                    try
                    {
                        _modifyListener.enable = false;
                        Object value = getValue();
                        if (value!=null && _baseValue.getValue()!=null)
                        {
                            textField.setText(_dateFormat.format(_baseValue.getValue()));
                        }
                        else
                        {
                            textField.setText("");
                        }
                    }
                    finally
                    {
                        _modifyListener.enable = true;
                    }
                }

                @Override
                public void focusGained(FocusEvent arg0)
                {

                }
            });

            stackedPane.add(EJStackedItemRendererType.DATE.name(), textField);
            textField.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(textField.getBackground(), textField.getForeground(),
                    textField.getFont()));

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

    public String getCSSKey()
    {
        return EJ_RWT.CSS_CV_ITEM_TEXT;
    }

    @Override
    public Label getGuiComponentLabel()
    {
        return _label;
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

    DecimalFormat createFormatter(NUMBER_TYPE numberType)
    {
        DecimalFormat _decimalFormatter = null;
        Locale defaultLocale = _item.getForm().getFrameworkManager().getCurrentLocale();
        if (defaultLocale == null)
        {
            defaultLocale = Locale.getDefault();
        }

        String format = null;// todo

        if (format == null || format.length() == 0)
        {
            _decimalFormatter = (DecimalFormat) NumberFormat.getNumberInstance(defaultLocale);
        }
        else
        {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols(defaultLocale);
            _decimalFormatter = new DecimalFormat(format, dfs);
            switch (numberType)
            {
                case INTEGER:
                case LONG:

                    _decimalFormatter.setGroupingUsed(true);
                    _decimalFormatter.setParseIntegerOnly(true);
                    _decimalFormatter.setParseBigDecimal(false);
                    break;

                default:

                    char seperator = dfs.getDecimalSeparator();
                    if (format.indexOf(seperator) != -1)
                    {
                        _decimalFormatter.setGroupingUsed(true);
                    }
                    _decimalFormatter.setParseIntegerOnly(false);
                    _decimalFormatter.setParseBigDecimal(true);
                    break;
            }
        }
        return _decimalFormatter;
    }

    protected void createDateFormat()
    {
        MultiDateFormater dateFormat;
        Locale defaultLocale = _item.getForm().getFrameworkManager().getCurrentLocale();
        if (defaultLocale == null)
        {
            defaultLocale = Locale.getDefault();
        }
        

       
        String format = _baseValue!=null ? _baseValue.getFormat():null;
        if(format!=null)
        {
            String[] split = format.split("\\|");
            SimpleDateFormat[] formats = new SimpleDateFormat[split.length];
            for (int i = 0; i < split.length; i++)
            {
                formats[i] = new SimpleDateFormat(split[i], defaultLocale);
            }
            dateFormat = new MultiDateFormater(formats);
        }
        else
        {
            dateFormat = new MultiDateFormater(DateFormat.getDateInstance(DateFormat.SHORT, defaultLocale));
        }

        _dateFormat = dateFormat;
    }

}
