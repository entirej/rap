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
package org.entirej.applicationframework.rwt.renderers.item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.components.EJRWTAbstractPanelAction;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJStackedPane;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJStackedPane.StackedPage;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.blocks.definition.interfaces.EJRWTSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.EJRWTDateItemRenderer.MultiDateFormater;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTButtonItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTAbstractScreenRenderer;
import org.entirej.applicationframework.rwt.renderers.stack.EJRWTStackedItemRendererConfig;
import org.entirej.applicationframework.rwt.renderers.stack.EJRWTStackedItemRendererConfig.CheckBox;
import org.entirej.applicationframework.rwt.renderers.stack.EJRWTStackedItemRendererConfig.Combo.Column;
import org.entirej.applicationframework.rwt.renderers.stack.EJRWTStackedItemRendererConfig.LOVSupportConfig;
import org.entirej.applicationframework.rwt.renderers.stack.EJRWTStackedItemRendererConfig.TextArea;
import org.entirej.applicationframework.rwt.renderers.stack.EJRWTStackedItemRendererType;
import org.entirej.applicationframework.rwt.renderers.stack.EJRWTStackedItemRendererValue;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.utils.EJRWTItemRendererVisualContext;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJMessageFactory;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.data.controllers.EJItemLovController;
import org.entirej.framework.core.data.controllers.EJLovController;
import org.entirej.framework.core.enumerations.EJFrameworkMessage;
import org.entirej.framework.core.enumerations.EJLovDisplayReason;
import org.entirej.framework.core.enumerations.EJMessageLevel;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreInsertScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreItemProperties;
import org.entirej.framework.core.properties.EJCoreMainScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreQueryScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreUpdateScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJLovDefinitionProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.registry.EJBlockItemRendererRegister;

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
    protected Object                          _oldvalue;
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

    private NUMBER_TYPE                     _numberType = NUMBER_TYPE.NUMBER;

    protected EJRWTStackedItemRendererValue _baseValue;
    protected EJRWTStackedItemRendererValue _intbaseValue;
    private EJMessage                       message;
    private String                          defaultMessage;

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

                EJRWTStackedItemRendererType[] values = EJRWTStackedItemRendererType.values();
                for (EJRWTStackedItemRendererType type : values)
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

        if (_baseValue != null && controlState(stackedPane))
        {
            switch (_baseValue.getConfig().getType())
            {
                case COMBO:
                {
                    Combo control = (Combo) stackedPane.getControl(_baseValue.getConfig().getType().name());
                    EJRWTStackedItemRendererConfig.Combo config = (EJRWTStackedItemRendererConfig.Combo) control.getData("CONFIG");
                    if (config != null)
                    {
                        ComboViewer _vViewer = (ComboViewer) control.getData("VIEW");
                        List<ComboBoxValue> loadComboBoxValues = loadComboBoxValues(config);

                        _vViewer.setInput(loadComboBoxValues);

                    }
                }
                    break;

            }
        }
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
        defaultMessage = _rendererProps.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_MESSAGE);

    }

    @Override
    public void setLabel(String label)
    {
        if (_label != null && !_label.isDisposed())
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
            EJRWTStackedItemRendererType[] values = EJRWTStackedItemRendererType.values();
            for (EJRWTStackedItemRendererType type : values)
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
                    else if (control instanceof Button)
                    {
                        ((Button) control).setSelection(false);
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

    Object getRealValue()

    {
        Object value = getValue();

        if (value instanceof EJRWTStackedItemRendererValue)
        {
            return ((EJRWTStackedItemRendererValue) value).getValue();
        }
        return value;
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
            switch (_baseValue.getConfig().getType())
            {

                case TEXT:
                case TEXT_AREA:
                {
                    Text control = (Text) stackedPane.getControl(_baseValue.getConfig().getType().name());
                    String value = control.getText();

                    if (value == null || value.length() == 0)
                    {
                        value = null;
                    }
                    _baseValue.setValue(value);
                    break;
                }
                case CHECKBOX:
                {
                    Button control = (Button) stackedPane.getControl(_baseValue.getConfig().getType().name());

                    EJRWTStackedItemRendererConfig.CheckBox config = (CheckBox) _baseValue.getConfig();

                    _baseValue.setValue(control.getSelection() ? config.getCheckBoxCheckedValue() : config.getCheckBoxUnCheckedValue());
                    break;
                }
                case COMBO:
                {
                    Combo control = (Combo) stackedPane.getControl(_baseValue.getConfig().getType().name());

                    ComboViewer _vViewer = (ComboViewer) control.getData("VIEW");

                    IStructuredSelection selection = (IStructuredSelection) _vViewer.getSelection();
                    if (selection.getFirstElement() instanceof ComboBoxValue)
                    {
                        ComboBoxValue value = (ComboBoxValue) selection.getFirstElement();
                        _baseValue.setValue(value.getItemValue());
                    }
                    else
                    {
                        _baseValue.setValue(null);
                    }

                    break;
                }
                case NUMBER:
                {

                    Text control = (Text) stackedPane.getControl(_baseValue.getConfig().getType().name());
                    Number value = null;
                    if (control.getText() != null && control.getText().isEmpty())
                    {
                        value = null;
                    }
                    else
                    {
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

                    Text control = (Text) stackedPane.getControl(_baseValue.getConfig().getType().name());
                    EJRWTStackedItemRendererConfig.Date config = (EJRWTStackedItemRendererConfig.Date) _baseValue.getConfig();
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

                            switch (config.getReturnType())
                            {
                                case SQL_DATE:
                                    value = new java.sql.Date(value.getTime());
                                    break;
                                case SQL_TIME:
                                    value = new java.sql.Time(value.getTime());
                                    break;
                                case SQL_TIMESTAMP:
                                    value = new java.sql.Timestamp(value.getTime());
                                    break;

                                default:
                                    break;
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

    private static NUMBER_TYPE getNumberType(EJRWTStackedItemRendererValue object)
    {
        if (object == null || object.getConfig().getType() != EJRWTStackedItemRendererType.NUMBER)
            return NUMBER_TYPE.NUMBER;

        EJRWTStackedItemRendererConfig.Number config = (EJRWTStackedItemRendererConfig.Number) object.getConfig();

        // final String datatypeClassName =
        // object.getValue().getClass().getName();
        // NUMBER_TYPE numberType;
        // if (datatypeClassName.equals(Integer.class.getName()))
        // {
        // numberType = NUMBER_TYPE.INTEGER;
        // }
        // else if (datatypeClassName.equals(Float.class.getName()))
        // {
        // numberType = NUMBER_TYPE.FLOAT;
        // }
        // else if (datatypeClassName.endsWith(Long.class.getName()))
        // {
        // numberType = NUMBER_TYPE.LONG;
        // }
        // else if (datatypeClassName.endsWith(Double.class.getName()))
        // {
        // numberType = NUMBER_TYPE.DOUBLE;
        // }
        // else
        // {
        //
        // numberType = NUMBER_TYPE.BIG_DECIMAL;
        // }
        return NUMBER_TYPE.valueOf(config.getDataType().name());
    }

    @Override
    public boolean isEditAllowed()
    {
        EJRWTStackedItemRendererType[] values = EJRWTStackedItemRendererType.values();
        for (EJRWTStackedItemRendererType type : values)
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
            if (_mandatory && (getValue() == null || _baseValue.getValue() == null))
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
        EJRWTStackedItemRendererType[] values = EJRWTStackedItemRendererType.values();
        for (EJRWTStackedItemRendererType type : values)
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
        if (_baseValue == null)
        {
            setLabel("");
        }
        if (controlState(stackedPane))
        {
            _errorDecoration.hide();
        }
        _valueChanged = false;
        _oldvalue = null;
        try
        {
            _modifyListener.enable = false;

            if (!(value instanceof EJRWTStackedItemRendererValue))
            {
                if (_baseValue != null)
                {
                    _baseValue.setValue(value);

                    setStackValue();
                    boolean lovNotificationEnabled = _item.getProperties().isLovNotificationEnabled();
                    try
                    {
                        _item.getProperties().enableLovNotification(false);
                        _item.itemValueChaged(_baseValue);
                    }
                    finally
                    {

                        _item.getProperties().enableLovNotification(lovNotificationEnabled);
                        ;
                    }
                    return;
                }
            }
            else
            {
                _baseValue = (EJRWTStackedItemRendererValue) value;
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

        setLabel("");

        if (!controlState(stackedPane))
            return;

        try
        {
            enableLovActivation(false);
            _item.setItemLovMapping(null);

            _actionControl.setCustomActionVisible(_baseValue != null && _baseValue.getConfig().getType() == EJRWTStackedItemRendererType.DATE);
            if (_baseValue != null)
            {

//                if (_baseValue.getConfig().getTextAliment() != null && controlState(_label))
//                {
//                    _label.setData("data.TextAliment", _label.getAlignment());
//                    labletextAliment(_label, _baseValue.getConfig().getTextAliment());
//                }
//                else if (controlState(_label))
//                {
//                    if (_label.getData("data.TextAliment") instanceof Integer)
//                        _label.setAlignment((Integer) _label.getData("data.TextAliment"));
//                }

                if (controlState(_label) && !(_baseValue.getConfig().getType() == EJRWTStackedItemRendererType.SPACER || _baseValue.getConfig().getType() == EJRWTStackedItemRendererType.CHECKBOX || _baseValue.getConfig().getType() == EJRWTStackedItemRendererType.BUTTON))
                {
                    if (_baseValue.getConfig().getLabel() != null)
                    {
                        setLabel(_baseValue.getConfig().getLabel());
                    }
                    else
                    {
                        setLabel(_screenItemProperties.getLabel() == null ? "" : _screenItemProperties.getLabel());
                    }

                }

                if (_baseValue.getConfig().getType() == EJRWTStackedItemRendererType.COMBO)
                {
                    EJRWTStackedItemRendererConfig.Combo config = (EJRWTStackedItemRendererConfig.Combo) _baseValue.getConfig();
                    Combo control = (Combo) stackedPane.getControl(_baseValue.getConfig().getType().name());
                    if (control.getData("CONFIG") != config)
                    {
                        List<ComboBoxValue> loadComboBoxValues = loadComboBoxValues(config);

                        ((ComboViewer) control.getData("VIEW")).setInput(loadComboBoxValues);
                        control.setData("CONFIG", config);
                        if (config.getVisibleItemCount() > 5)
                        {
                            control.setVisibleItemCount(config.getVisibleItemCount());
                        }
                    }

                }
                if (_baseValue.getConfig().getType() == EJRWTStackedItemRendererType.LINKS)
                {
                    EJRWTStackedItemRendererConfig.Links config = (EJRWTStackedItemRendererConfig.Links) _baseValue.getConfig();
                    EJRWTEntireJGridPane control = (EJRWTEntireJGridPane) stackedPane.getControl(_baseValue.getConfig().getType().name());
                    if (control.getData("CONFIG") != config)
                    {
                        Arrays.stream(control.getChildren()).filter(c -> !c.isDisposed()).forEach(Control::dispose);

                        control.setData("CONFIG", config);

                        config.getActions().forEach(a -> {

                            if (a.isSeparator())
                            {

                                new Label(control, SWT.SEPARATOR|SWT.VERTICAL).setLayoutData(new GridData(GridData.FILL_VERTICAL|GridData.GRAB_VERTICAL));;
                            }
                            else if (a.isSpace())
                            {
                                new Label(control, SWT.NONE).setLayoutData(new GridData(GridData.FILL_BOTH|GridData.GRAB_HORIZONTAL));
                            }
                            else if (a.getActionCommand() ==null)
                            {
                                Link label = new Link(control, SWT.NONE);
                                //label.setLayoutData(new GridData(GridData.FILL_VERTICAL));
                                label.setText(a.getName());
                                label.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_LABEL);
                            }
                            else
                            {
                                Link linkField = new Link(control, SWT.NONE);
                                linkField.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_LABEL);
                                linkField.setText(String.format("<a>%s</a>", a.getName()));
                                linkField.setData(EJ_RWT.MARKUP_ENABLED, true);
                                linkField.addSelectionListener(new SelectionAdapter()
                                {

                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public void widgetSelected(SelectionEvent e)
                                    {
                                        setCustomActionCommand(a.getActionCommand());
                                        Display.getDefault().asyncExec(() -> {

                                            _item.executeActionCommand();

                                        });
                                    }
                                });
                            }

                        });

                        control.layout(true);
                    }

                }

                if (_baseValue.getConfig().getTooltip() != null)
                {
                    setHint(_baseValue.getConfig().getTooltip());
                }
                else
                {
                    setHint(_screenItemProperties.getHint() == null ? "" : _screenItemProperties.getHint());
                }

                stackedPane.showPane(_baseValue.getConfig().getType().name());

                if (_baseValue.getConfig().getExpandHorizontally() != null)
                {
                    Object layoutData = _actionControl.getLayoutData();
                    if (layoutData instanceof GridData)
                    {
                        GridData data = (GridData) layoutData;

                        // add padding

                        if (_actionControl.getData("data.ExpandH") == null)
                            _actionControl.setData("data.ExpandH", data.grabExcessHorizontalSpace);
                        if (_actionControl.getData("data.HA") == null)
                            _actionControl.setData("data.HA", data.horizontalAlignment);

                        data.grabExcessHorizontalSpace = _baseValue.getConfig().getExpandHorizontally();
                        data.horizontalAlignment = data.grabExcessHorizontalSpace ? SWT.FILL : SWT.BEGINNING;

                    }
                }
                else
                {
                    Object layoutData = _actionControl.getLayoutData();
                    if (layoutData instanceof GridData)
                    {
                        GridData data = (GridData) layoutData;

                        // add padding
                        if (_actionControl.getData("data.ExpandH") != null)
                        {

                            data.grabExcessHorizontalSpace = (Boolean) _actionControl.getData("data.ExpandH");
                            data.horizontalAlignment = (Integer) _actionControl.getData("data.HA");

                            _actionControl.setData("data.ExpandH", null);
                            _actionControl.setData("data.HA", null);
                        }

                    }
                }

                if (_baseValue.getConfig().getExpandVertically() != null)
                {
                    Object layoutData = _actionControl.getLayoutData();
                    if (layoutData instanceof GridData)
                    {
                        GridData data = (GridData) layoutData;

                        // add padding

                        if (_actionControl.getData("data.ExpandV") == null)
                            _actionControl.setData("data.ExpandV", data.grabExcessVerticalSpace);
                        if (_actionControl.getData("data.VA") == null)
                            _actionControl.setData("data.VA", data.verticalAlignment);
                        data.grabExcessVerticalSpace = _baseValue.getConfig().getExpandVertically();

                        data.verticalAlignment = data.grabExcessVerticalSpace ? SWT.FILL : SWT.TOP;

                    }

                    if (_label != null)
                    {
                        layoutData = _label.getLayoutData();
                        if (layoutData instanceof GridData)
                        {
                            GridData data = (GridData) layoutData;

                            if (_label.getData("data.VA") == null)
                                _label.setData("data.VA", data.verticalAlignment);
                            if (_label.getData("data.VI") == null)
                                _label.setData("data.VI", data.verticalIndent);

                            data.verticalIndent = 2;
                            data.verticalAlignment = SWT.TOP;
                        }
                    }
                }
                else
                {
                    Object layoutData = _actionControl.getLayoutData();
                    if (layoutData instanceof GridData)
                    {
                        GridData data = (GridData) layoutData;

                        // add padding
                        if (_actionControl.getData("data.ExpandV") != null)
                        {

                            data.grabExcessVerticalSpace = (Boolean) _actionControl.getData("data.ExpandV");
                            data.verticalAlignment = (Integer) _actionControl.getData("data.VA");
                            _actionControl.setData("data.ExpandV", null);
                            _actionControl.setData("data.VA", null);
                        }

                    }

                    if (_label != null)
                    {
                        layoutData = _label.getLayoutData();
                        if (layoutData instanceof GridData)
                        {
                            GridData data = (GridData) layoutData;

                            if (_label.getData("data.VA") != null)
                                data.verticalAlignment = (Integer) _label.getData("data.VA");
                            if (_label.getData("data.VI") != null)
                                data.verticalIndent = (Integer) _label.getData("data.VI");

                        }
                    }
                }

                if (_baseValue.getConfig().getXSpan() != EJRWTStackedItemRendererConfig.DEFUALT)
                {
                    Object layoutData = _actionControl.getLayoutData();
                    if (layoutData instanceof GridData)
                    {
                        GridData data = (GridData) layoutData;

                        // add padding

                        if (_actionControl.getData("data.xspan") == null)
                            _actionControl.setData("data.xspan", data.horizontalSpan);

                        data.horizontalSpan = _baseValue.getConfig().getXSpan();

                    }
                }
                else
                {
                    Object layoutData = _actionControl.getLayoutData();
                    if (layoutData instanceof GridData)
                    {
                        GridData data = (GridData) layoutData;

                        // add padding
                        if (_actionControl.getData("data.xspan") != null)
                        {

                            data.horizontalSpan = (Integer) _actionControl.getData("data.xspan");

                            _actionControl.setData("data.xspan", null);
                        }

                    }
                }

                if (_baseValue.getConfig().getYSpan() != EJRWTStackedItemRendererConfig.DEFUALT)
                {
                    Object layoutData = _actionControl.getLayoutData();
                    if (layoutData instanceof GridData)
                    {
                        GridData data = (GridData) layoutData;

                        // add padding

                        if (_actionControl.getData("data.yspan") == null)
                            _actionControl.setData("data.yspan", data.verticalSpan);

                        data.verticalSpan = _baseValue.getConfig().getYSpan();

                    }
                }
                else
                {
                    Object layoutData = _actionControl.getLayoutData();
                    if (layoutData instanceof GridData)
                    {
                        GridData data = (GridData) layoutData;

                        // add padding
                        if (_actionControl.getData("data.yspan") != null)
                        {

                            data.verticalSpan = (Integer) _actionControl.getData("data.yspan");

                            _actionControl.setData("data.yspan", null);
                        }

                    }
                }

                if (_baseValue.getConfig().getWidth() != EJRWTStackedItemRendererConfig.DEFUALT)
                {
                    Object layoutData = _actionControl.getLayoutData();
                    if (layoutData instanceof GridData)
                    {
                        GridData data = (GridData) layoutData;

                        // add padding

                        Control control = stackedPane.getControl(_baseValue.getConfig().getType().name());

                        float avgCharHeight = EJRWTImageRetriever.getGraphicsProvider().getAvgCharWidth(control.getFont());

                        if (_actionControl.getData("data.widthHint") == null)
                            _actionControl.setData("data.widthHint", data.widthHint);

                        data.widthHint = (int) (avgCharHeight * (_baseValue.getConfig().getWidth() + 1));

                    }
                }
                else
                {
                    Object layoutData = _actionControl.getLayoutData();
                    if (layoutData instanceof GridData)
                    {
                        GridData data = (GridData) layoutData;

                        // add padding
                        if (_actionControl.getData("data.widthHint") != null)
                        {

                            data.widthHint = (Integer) _actionControl.getData("data.widthHint");

                            _actionControl.setData("data.widthHint", null);
                        }

                    }
                }

                if (_baseValue.getConfig().getType() == EJRWTStackedItemRendererType.TEXT_AREA)
                {
                    Object layoutData = _actionControl.getLayoutData();
                    if (layoutData instanceof GridData)
                    {
                        GridData data = (GridData) layoutData;
                        EJRWTStackedItemRendererConfig.TextArea config = (TextArea) _baseValue.getConfig();
                        Control control = stackedPane.getControl(_baseValue.getConfig().getType().name());

                        float avgCharHeight = EJRWTImageRetriever.getGraphicsProvider().getCharHeight(control.getFont());

                        // add padding
                        if (config.getLines() > 0)
                        {
                            if (_actionControl.getData("data.heightHint") == null)
                                _actionControl.setData("data.heightHint", data.heightHint);

                            data.heightHint = (int) ((config.getLines() + 1) * avgCharHeight);

                        }

                    }

                    if (_label != null)
                    {
                        layoutData = _label.getLayoutData();
                        if (layoutData instanceof GridData)
                        {
                            GridData data = (GridData) layoutData;

                            if (_label.getData("data.VA") == null)
                                _label.setData("data.VA", data.verticalAlignment);
                            if (_label.getData("data.VI") == null)
                                _label.setData("data.VI", data.verticalIndent);

                            data.verticalIndent = 2;
                            data.verticalAlignment = SWT.TOP;
                        }
                    }
                }
                else if (_baseValue.getConfig().getHeight() != EJRWTStackedItemRendererConfig.DEFUALT)
                {
                    Object layoutData = _actionControl.getLayoutData();
                    if (layoutData instanceof GridData)
                    {
                        GridData data = (GridData) layoutData;

                        // add padding

                        Control control = stackedPane.getControl(_baseValue.getConfig().getType().name());

                        float avgCharHeight = EJRWTImageRetriever.getGraphicsProvider().getCharHeight(control.getFont());

                        if (_actionControl.getData("data.heightHint") == null)
                            _actionControl.setData("data.heightHint", data.heightHint);

                        data.heightHint = (int) (avgCharHeight * (_baseValue.getConfig().getHeight() + 1));

                    }

                    if (_label != null)
                    {
                        layoutData = _label.getLayoutData();
                        if (layoutData instanceof GridData)
                        {
                            GridData data = (GridData) layoutData;

                            if (_label.getData("data.VA") == null)
                                _label.setData("data.VA", data.verticalAlignment);
                            if (_label.getData("data.VI") == null)
                                _label.setData("data.VI", data.verticalIndent);

                            data.verticalIndent = 2;
                            data.verticalAlignment = SWT.TOP;
                        }
                    }
                }
                else
                {
                    Object layoutData = _actionControl.getLayoutData();
                    if (layoutData instanceof GridData)
                    {
                        GridData data = (GridData) layoutData;

                        // add padding
                        if (_actionControl.getData("data.heightHint") != null)
                        {

                            data.heightHint = (Integer) _actionControl.getData("data.heightHint");

                            _actionControl.setData("data.heightHint", null);
                        }

                    }

                    if (_label != null)
                    {
                        layoutData = _label.getLayoutData();
                        if (layoutData instanceof GridData)
                        {
                            GridData data = (GridData) layoutData;

                            if (_label.getData("data.VA") != null)
                                data.verticalAlignment = (Integer) _label.getData("data.VA");
                            if (_label.getData("data.VI") != null)
                                data.verticalIndent = (Integer) _label.getData("data.VI");

                        }
                    }
                }

                // setLOV mapping
                if (_baseValue.getConfig() instanceof EJRWTStackedItemRendererConfig.ActionSupportConfig)
                {
                    String actionCommand = ((EJRWTStackedItemRendererConfig.ActionSupportConfig) _baseValue.getConfig()).getActionCommand();
                    setCustomActionCommand(actionCommand);

                }
                if (_baseValue.getConfig() instanceof EJRWTStackedItemRendererConfig.LOVSupportConfig)
                {
                    EJRWTStackedItemRendererConfig.LOVSupportConfig lovMapping = (LOVSupportConfig) _baseValue.getConfig();

                    _item.getProperties().enableLovNotification(lovMapping.isLovEnabled());
                    _item.setItemLovMapping(lovMapping.getLovMapping());
                    _item.getProperties().enableLovValidation(lovMapping.isValidateLov());
                    enableLovActivation(lovMapping.isLovEnabled());
                }

                setStackValue();
            }
            else
            {
                stackedPane.showPane(EJRWTStackedItemRendererType.SPACER.name());// switch
                Object layoutData = _actionControl.getLayoutData();
                if (layoutData instanceof GridData)
                {
                    GridData data = (GridData) layoutData;

                    if (_actionControl.getData("data.heightHint") != null)
                    {

                        data.heightHint = (Integer) _actionControl.getData("data.heightHint");

                        _actionControl.setData("data.heightHint", null);
                    }

                    if (_actionControl.getData("data.xspan") != null)
                    {

                        data.horizontalSpan = (Integer) _actionControl.getData("data.xspan");

                        _actionControl.setData("data.xspan", null);
                    }

                    if (_actionControl.getData("data.yspan") != null)
                    {

                        data.verticalSpan = (Integer) _actionControl.getData("data.yspan");

                        _actionControl.setData("data.yspan", null);
                    }

                    if (_actionControl.getData("data.ExpandV") != null)
                    {

                        data.grabExcessVerticalSpace = (Boolean) _actionControl.getData("data.ExpandV");
                        data.verticalAlignment = (Integer) _actionControl.getData("data.VA");
                        _actionControl.setData("data.ExpandV", null);
                        _actionControl.setData("data.VA", null);
                    }

                    if (_actionControl.getData("data.ExpandH") != null)
                    {

                        data.grabExcessHorizontalSpace = (Boolean) _actionControl.getData("data.ExpandH");
                        data.horizontalAlignment = (Integer) _actionControl.getData("data.HA");

                        _actionControl.setData("data.ExpandH", null);
                        _actionControl.setData("data.HA", null);
                    }

                    if (_label != null)
                    {
                        layoutData = _label.getLayoutData();
                        if (layoutData instanceof GridData)
                        {
                            GridData ldata = (GridData) layoutData;

                            if (_label.getData("data.VA") != null)
                                ldata.verticalAlignment = (Integer) _label.getData("data.VA");
                            if (_label.getData("data.VI") != null)
                                ldata.verticalIndent = (Integer) _label.getData("data.VI");

                        }
                    }

                } // to
                  // empty
            }
        }
        finally
        {
            _actionControl.getParent().layout();
        }

    }

    private void setCustomActionCommand(String actionCommand)
    {
        if (_item.getProperties() instanceof EJCoreInsertScreenItemProperties)
        {
            ((EJCoreInsertScreenItemProperties) _item.getProperties()).setActionCommand(actionCommand);
        }
        else if (_item.getProperties() instanceof EJCoreQueryScreenItemProperties)
        {
            ((EJCoreQueryScreenItemProperties) _item.getProperties()).setActionCommand(actionCommand);
        }
        else if (_item.getProperties() instanceof EJCoreUpdateScreenItemProperties)
        {
            ((EJCoreUpdateScreenItemProperties) _item.getProperties()).setActionCommand(actionCommand);
        }
        else if (_item.getProperties() instanceof EJCoreMainScreenItemProperties)
        {

            ((EJCoreMainScreenItemProperties) _item.getProperties()).setActionCommand(actionCommand);
        }
    }

    private void setStackValue()
    {
        Object value = _baseValue.getValue();
        try
        {
            _modifyListener.enable = false;
            EJRWTStackedItemRendererType[] values = EJRWTStackedItemRendererType.values();
            for (EJRWTStackedItemRendererType type : values)
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
                    else if (control instanceof Button)
                    {

                        EJRWTStackedItemRendererConfig config = _baseValue.getConfig();
                        ((Button) control).setText(config.getLabel() == null ? (_item.getProperties().getLabel() == null ? "" : _item.getProperties().getLabel()) : config.getLabel());
                        ((Button) control).setToolTipText(config.getTooltip() == null ? (_item.getProperties().getHint() == null ? "" : _item.getProperties().getHint()) : config.getTooltip());

                        if (config instanceof EJRWTStackedItemRendererConfig.Button)
                        {
                            EJRWTStackedItemRendererConfig.Button button = (EJRWTStackedItemRendererConfig.Button) config;
                            if (button.getImage() != null && button.getImage().trim().length() > 0)
                            {
                                ((Button) control).setImage(EJRWTImageRetriever.get(button.getImage()));
                            }
                        }

                    }
                    // check_box/combo
                }
            }

            if (value != null)
            {
                switch (_baseValue.getConfig().getType())
                {
                    case LABEL:
                    {
                        Label control = (Label) stackedPane.getControl(_baseValue.getConfig().getType().name());
                        control.setText(value.toString());
                        break;
                    }
                    case VALUE_LABEL:
                    {
                        setLabel("");
                        Label control = (Label) stackedPane.getControl(_baseValue.getConfig().getType().name());
                        control.setText(value.toString());
                        break;
                    }

                    case TEXT:
                    case TEXT_AREA:
                    {
                        Text control = (Text) stackedPane.getControl(_baseValue.getConfig().getType().name());
                        control.setText(value.toString());
                        break;
                    }
                    case COMBO:
                    {

                        Combo control = (Combo) stackedPane.getControl(_baseValue.getConfig().getType().name());
                        List<ComboBoxValue> loadComboBoxValues = (List<ComboBoxValue>) ((ComboViewer) control.getData("VIEW")).getInput();
                        ComboBoxValue boxValue = null;

                        for (ComboBoxValue val : loadComboBoxValues)
                        {
                            if (val.getItemValue() == null && value == null)
                            {
                                boxValue = val;
                                break;
                            }

                            if (val.getItemValue() == null)
                            {
                                continue;
                            }

                            if (!val.getItemValue().getClass().isAssignableFrom(value.getClass()))
                            {
                                EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM, _item.getName(), val.getItemValue().getClass().getName(), value.getClass().getName());
                                throw new IllegalArgumentException(message.getMessage());
                            }

                            if (val.getItemValue().equals(value))
                            {
                                boxValue = val;
                                break;
                            }
                        }

                        if (boxValue != null)
                        {
                            ((ComboViewer) control.getData("VIEW")).setSelection(new StructuredSelection(boxValue));
                        }

                        break;
                    }
                    case NUMBER:
                    {
                        EJRWTStackedItemRendererConfig.Number config = (EJRWTStackedItemRendererConfig.Number) _baseValue.getConfig();

                        _numberType = getNumberType(_baseValue);
                        _decimalFormatter = createFormatter(_numberType, config.getFormat());
                        if (value != null && !Number.class.isAssignableFrom(value.getClass()))
                        {
                            EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM, _item.getName(), Number.class.getName(), value.getClass().getName());
                            throw new IllegalArgumentException(message.getMessage());
                        }
                        Text control = (Text) stackedPane.getControl(_baseValue.getConfig().getType().name());
                        control.setText(_decimalFormatter.format(value));
                        break;
                    }
                    case DATE:
                    {
                        createDateFormat();
                        if (value != null && !Date.class.isAssignableFrom(value.getClass()))
                        {
                            EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM, _item.getName(), Date.class.getName(), value.getClass().getName());
                            throw new IllegalArgumentException(message.getMessage());
                        }
                        Text control = (Text) stackedPane.getControl(_baseValue.getConfig().getType().name());
                        control.setText(_dateFormat.format(value));
                        control.setMessage(_dateFormat.toFormatString());
                        break;
                    }

                    case SPACER:
                    {
                        setLabel("");
                        break;
                    }
                    case CHECKBOX:
                    {
                        setLabel("");
                        Control control = stackedPane.getControl(_baseValue.getConfig().getType().name());
                        EJRWTStackedItemRendererConfig.CheckBox config = ((EJRWTStackedItemRendererConfig.CheckBox) _baseValue.getConfig());

                        ((Button) control).setSelection(config.getCheckBoxCheckedValue() == value || (value != null && value.equals(config.getCheckBoxCheckedValue())));
                        break;
                    }
                    case BUTTON:
                    case LINKS:
                    {
                        setLabel("");

                        break;
                    }

                    default:
                        break;
                }
            }
            else
            {
                switch (_baseValue.getConfig().getType())
                {
                    case DATE:
                        createDateFormat();
                        break;
                    case NUMBER:
                        _numberType = getNumberType(_baseValue);
                        _decimalFormatter = createFormatter(_numberType, ((EJRWTStackedItemRendererConfig.Number) _baseValue.getConfig()).getFormat());
                        break;
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

        {
            if (controlState(stackedPane))
            {
                stackedPane.setVisible(visible);
            }

            if (controlState(_label))
            {
                _label.setVisible(visible);
            }

            if (controlState(_actionControl))
            {
                _actionControl.setVisible(visible);
            }
        }

    }

    @Override
    public void setMandatory(boolean mandatory)
    {
        _mandatory = mandatory;
        setMandatoryBorder(mandatory);
    }

    public String getDisplayValue()
    {
        if (_baseValue != null && controlState(stackedPane))
        {

            Control control = stackedPane.getControl(_baseValue.getConfig().getType().name());
            if (control != null && controlState(control))
            {
                if (control instanceof Label)
                {
                    return ((Label) control).getText();
                }
                else if (control instanceof Text)
                {
                    return ((Text) control).getText();
                }
                else if (control instanceof Combo)
                {
                    return ((Combo) control).getText();
                }

                // check_box/combo
            }

        }
        return null;
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

    @Override
    public void setMessage(EJMessage message)
    {
        this.message = message;
        if (_errorDecoration != null && controlState(stackedPane) && !_errorDecoration.getControl().isDisposed())
        {
            if (message != null && message.getLevel() == EJMessageLevel.HINT)
                setStackHintMessage();
            else
                ControlDecorationSupport.handleMessage(_errorDecoration, message);
        }
    }

    private void setStackHintMessage()
    {
        String msg = defaultMessage != null ? defaultMessage : "";
        if (message != null || message.getLevel() == EJMessageLevel.HINT)
            msg = message.getMessage();
        boolean setOnField = false;
        if (_baseValue != null)
        {
            Control control = stackedPane.getControl(_baseValue.getConfig().getType().name());
            if (control != null && controlState(control))
            {

                if (control instanceof Text)
                {
                    ((Text) control).setMessage(msg);
                    setOnField = true;
                }

            }

        }

        if (!setOnField)
            ControlDecorationSupport.handleMessage(_errorDecoration, message);

    }

    @Override
    public void clearMessage()
    {
        this.message = null;
        setStackHintMessage();

        if (_errorDecoration != null && controlState(stackedPane) && !_errorDecoration.getControl().isDisposed())
        {
            _errorDecoration.setDescriptionText("");
            {
                _errorDecoration.hide();
            }
        }

    }

    public void valueChanged()
    {
        Object base = _intbaseValue != null ? _intbaseValue.getValue() : null;
        Object value = getRealValue();

        Control activeControl = stackedPane.getActiveControl();
        if (!(activeControl instanceof Text && activeControl.isFocusControl()))
        {

            if (_valueChanged || ((base == null && value != null) || (base != null && value == null) || (value != null && !value.equals(base))))
                _item.itemValueChaged(value);
            _valueChanged = false;
            _oldvalue = null;
        }
        else
        {
            if (_oldvalue == null)
            {
                _oldvalue = base;
            }
            _valueChanged = _valueChanged || ((base == null && value != null) || (base != null && value == null) || (value != null && !value.equals(base)));
        }
        setMandatoryBorder(_mandatory);
        fireTextChange();
    }

    protected void setMandatoryBorder(boolean req)
    {

        if (controlState(_mandatoryDecoration.getControl()))
        {
            if (req && (getValue() == null || _baseValue.getValue() == null))
            {
                _mandatoryDecoration.show();
            }
            else
            {
                _mandatoryDecoration.hide();
            }
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

                    _item.itemValueChaged(getRealValue());
                    _oldvalue = null;
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

        EJRWTStackedItemRendererType[] values = EJRWTStackedItemRendererType.values();
        for (EJRWTStackedItemRendererType type : values)
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

        EJRWTStackedItemRendererType[] values = EJRWTStackedItemRendererType.values();
        for (EJRWTStackedItemRendererType type : values)
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

        EJRWTStackedItemRendererType[] values = EJRWTStackedItemRendererType.values();
        for (EJRWTStackedItemRendererType type : values)
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
        final Label label = new Label(parent, SWT.NONE);
        label.setImage(EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_DATE_SELECTION));
        label.addMouseListener(new MouseListener()
        {
            private final DateFormat format = new SimpleDateFormat("yyyy/MM/dd");

            private void selectDate(final Shell abstractDialog, final DateTime calendar)
            {
                try
                {
                    Object old = _baseValue.getValue();
                    Date newVal = format.parse(String.format("%d/%d/%d", calendar.getYear(), calendar.getMonth() + 1, calendar.getDay()));
                    if (newVal != null && !_itemProperties.getDataTypeClassName().equals(Date.class.getName()))
                    {
                        String dataTypeClass = _itemProperties.getDataTypeClassName();
                        if (dataTypeClass.equals("java.sql.Date"))
                        {
                            newVal = new java.sql.Date(newVal.getTime());
                        }
                        else if (dataTypeClass.equals("java.sql.Time"))
                        {
                            newVal = new java.sql.Time(newVal.getTime());
                        }
                        else if (dataTypeClass.equals("java.sql.Timestamp"))
                        {
                            newVal = new java.sql.Timestamp(newVal.getTime());
                        }
                    }
                    setValue(newVal);
                    _item.itemValueChaged(getRealValue());
                }
                catch (ParseException e1)
                {
                    // ignore
                }
                abstractDialog.close();
                abstractDialog.dispose();
                _item.gainFocus();
            }

            @Override
            public void mouseUp(MouseEvent arg0)
            {
                Shell shell = ((EJRWTApplicationManager) _item.getForm().getFrameworkManager().getApplicationManager()).getShell();
                final Shell abstractDialog = new Shell(shell, SWT.ON_TOP | SWT.APPLICATION_MODAL | SWT.BORDER);
                abstractDialog.setLayout(new GridLayout(3, false));

                GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
                new Label(abstractDialog, SWT.NONE).setLayoutData(gridData);
                Link today = new Link(abstractDialog, SWT.PUSH);
                today.setText("<A>Today</A>");
                today.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        try
                        {
                            Object old = _baseValue.getValue();
                            Date newVal = format.parse(format.format(new Date()));
                            newVal = convertDate(newVal);
                            setValue(newVal);
                            _item.itemValueChaged(getRealValue());
                        }
                        catch (ParseException e1)
                        {
                            // ignore
                        }
                        if (!abstractDialog.isDisposed())
                        {
                            abstractDialog.close();
                            abstractDialog.dispose();
                        }
                        _item.gainFocus();
                    }

                });
                Link clear = new Link(abstractDialog, SWT.PUSH);
                clear.setText("<A>Clear</A>");
                clear.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        Object old = _baseValue.getValue();
                        setValue(null);
                        _item.itemValueChaged(null);

                        if (!abstractDialog.isDisposed())
                        {
                            abstractDialog.close();
                            abstractDialog.dispose();
                        }
                        _item.gainFocus();
                    }
                });

                final DateTime calendar = new DateTime(abstractDialog, SWT.CALENDAR | SWT.BORDER);

                if (_baseValue != null && _baseValue.getValue() instanceof Date)
                {
                    Date currentDate = (Date) _baseValue.getValue();
                    if (currentDate != null)
                    {
                        String dateText = format.format(currentDate);
                        String[] split = dateText.split("/");
                        if (split.length == 3)
                        {
                            calendar.setYear(Integer.parseInt(split[0]));
                            calendar.setMonth(Integer.parseInt(split[1]) - 1);// month
                                                                              // index
                                                                              // from
                                                                              // 0
                            calendar.setDay(Integer.parseInt(split[2]));
                        }
                    }

                }

                calendar.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mouseDoubleClick(MouseEvent e)
                    {
                        if (e.y >= 40)
                        {
                            selectDate(abstractDialog, calendar);
                        }
                    }
                });

                String[] keys = new String[] { "ENTER", "RETURN", "CR" };
                calendar.setData(EJ_RWT.ACTIVE_KEYS, keys);
                calendar.addKeyListener(new KeyAdapter()
                {
                    @Override
                    public void keyReleased(KeyEvent e)
                    {

                    }
                });
                gridData = new GridData();
                gridData.horizontalSpan = 3;
                calendar.setLayoutData(gridData);

                gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
                new Label(abstractDialog, SWT.NONE).setLayoutData(gridData);
                Button ok = new Button(abstractDialog, SWT.PUSH);

                ok.setText("OK");
                ok.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        try
                        {
                            Object old = _baseValue.getValue();
                            Date newVal = format.parse(String.format("%d/%d/%d", calendar.getYear(), calendar.getMonth() + 1, calendar.getDay()));
                            setValue(newVal);
                            _item.itemValueChaged(getRealValue());
                        }
                        catch (ParseException e1)
                        {
                            // ignore
                        }
                        if (!abstractDialog.isDisposed())
                        {
                            abstractDialog.close();
                            abstractDialog.dispose();
                        }
                        _item.gainFocus();
                    }
                });
                Button close = new Button(abstractDialog, SWT.PUSH);
                close.setText("Cancel");
                close.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {

                        if (!abstractDialog.isDisposed())
                        {
                            abstractDialog.close();
                            abstractDialog.dispose();
                        }
                        _item.gainFocus();
                    }
                });

                abstractDialog.pack();
                Point display = _actionControl.toDisplay(0, 0);
                Rectangle bounds = _actionControl.getBounds();
                abstractDialog.setLocation(display.x, display.y + bounds.height);

                abstractDialog.open();
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

        return label;
    }

    @Override
    public void createComponent(Composite composite)
    {

        String alignmentProperty = _rendererProps.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT);
        if (alignmentProperty == null)
        {
            alignmentProperty = _rendererProps.getStringProperty("ALLIGNMENT");
        }

        _numberType = getNumberType(_baseValue);
        _decimalFormatter = createFormatter(_numberType, null);
        _dateFormat = new MultiDateFormater(DateFormat.getDateInstance(DateFormat.SHORT, _item.getForm().getFrameworkManager().getCurrentLocale()));

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

    private void updateStackHint(String hint)
    {
        EJRWTStackedItemRendererType[] values = EJRWTStackedItemRendererType.values();
        for (EJRWTStackedItemRendererType type : values)
        {
            Control control = stackedPane.getControl(type.name());
            if (control != null && controlState(control))
            {
                control.setToolTipText(hint == null ? "" : hint);
            }
        }

    }

    void connectLOVAction(final Text text, EJRWTStackedItemRendererType type)
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

        List<String> actions = new ArrayList<>();

        actions.add(lovKey);
        actions.add("ENTER");
        actions.add("RETURN");
        actions.add("CR");

        if (type == EJRWTStackedItemRendererType.DATE)
        {
            actions.addAll(getDateActionKeys(propertyGroup));
        }

        text.setData(EJ_RWT.ACTIVE_KEYS, actions.toArray(new String[0]));
        text.addKeyListener(new KeyListener()
        {
            @Override
            public void keyReleased(KeyEvent arg0)
            {

                if ((arg0.stateMask & SWT.SHIFT) != 0 && arg0.keyCode == SWT.ARROW_DOWN && isLovActivated())
                {
                    _item.getItemLovController().displayLov(EJLovDisplayReason.LOV);
                }

                else if (arg0.keyCode == 13 && (SWT.MULTI != (text.getStyle() & SWT.MULTI) || (arg0.stateMask & SWT.CONTROL) != 0))
                {
                    if (_valueChanged)
                    {
                        _valueChanged = false;
                        _item.itemValueChaged(getRealValue());
                        _oldvalue = null;
                        setMandatoryBorder(_mandatory);
                    }
                }
                else
                {
                    if (type == EJRWTStackedItemRendererType.DATE)
                    {
                        dateAction(arg0);
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
            final Label label = new Label(stackedPane, SWT.NONE);
            label.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_LABEL);
            label.setData(EJ_RWT.CUSTOM_VARIANT + "_DEF", EJ_RWT.CSS_CV_ITEM_LABEL);
            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

            if (customCSSKey != null && customCSSKey.trim().length() > 0)
            {
                label.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            }
            stackedPane.add(EJRWTStackedItemRendererType.LABEL.name(), new EJRWTEntireJStackedPane.StackedPage()
            {

                @Override
                public String getKey()
                {
                    return EJRWTStackedItemRendererType.LABEL.name();
                }

                @Override
                public Control getControl()
                {
                    return label;
                }
            });
            label.setData(_item.getReferencedItemProperties().getName());
            label.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(label.getBackground(), label.getForeground(), label.getFont()));

        }
        {
            int style = SWT.NONE;
            final Button _button = new Button(stackedPane, style = style | SWT.CHECK);

            _button.addListener(SWT.Selection, new Listener()
            {
                @Override
                public void handleEvent(Event e)
                {
                    Display.getDefault().asyncExec(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            Object old = null;
                            Object newVal = null;
                            EJRWTStackedItemRendererConfig.CheckBox config = (CheckBox) _baseValue.getConfig();
                            if (_button.getSelection())
                            {
                                old = config.getCheckBoxUnCheckedValue();
                                newVal = config.getCheckBoxCheckedValue();
                            }
                            else
                            {
                                old = config.getCheckBoxCheckedValue();
                                newVal = config.getCheckBoxUnCheckedValue();
                            }
                            _item.executeActionCommand();
                            _item.itemValueChaged(getRealValue());
                        }
                    });

                }

            });

            _button.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_CHECKBOX);
            _button.setData(EJ_RWT.CUSTOM_VARIANT + "_DEF", EJ_RWT.CSS_CV_ITEM_CHECKBOX);
            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

            if (customCSSKey != null && customCSSKey.trim().length() > 0)
            {
                _button.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            }
            stackedPane.add(EJRWTStackedItemRendererType.CHECKBOX.name(), new StackedPage()
            {

                @Override
                public String getKey()
                {
                    return EJRWTStackedItemRendererType.CHECKBOX.name();
                }

                @Override
                public Control getControl()
                {
                    return _button;
                }
            });
            _button.setData(_item.getReferencedItemProperties().getName());
            _button.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(_button.getBackground(), _button.getForeground(), _button.getFont()));

        }
        {
            int style = SWT.NONE;
            final Button _button = new Button(stackedPane, style);

            _button.addListener(SWT.Selection, new Listener()
            {
                @Override
                public void handleEvent(Event e)
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

            _button.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_BUTTON);
            _button.setData(EJ_RWT.CUSTOM_VARIANT + "_DEF", EJ_RWT.CSS_CV_ITEM_BUTTON);
            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

            if (customCSSKey != null && customCSSKey.trim().length() > 0)
            {
                _button.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            }
            stackedPane.add(EJRWTStackedItemRendererType.BUTTON.name(), new StackedPage()
            {

                @Override
                public String getKey()
                {
                    return EJRWTStackedItemRendererType.BUTTON.name();
                }

                @Override
                public Control getControl()
                {
                    return _button;
                }
            });
            _button.setData(_item.getReferencedItemProperties().getName());
            _button.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(_button.getBackground(), _button.getForeground(), _button.getFont()));

        }
        {

            final EJRWTEntireJGridPane _grid = new EJRWTEntireJGridPane(stackedPane, 20);

            _grid.cleanLayout();

            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

            if (customCSSKey != null && customCSSKey.trim().length() > 0)
            {
                _grid.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            }
            stackedPane.add(EJRWTStackedItemRendererType.LINKS.name(), new StackedPage()
            {

                @Override
                public String getKey()
                {
                    return EJRWTStackedItemRendererType.LINKS.name();
                }

                @Override
                public Control getControl()
                {
                    return _grid;
                }
            });
            _grid.setData(_item.getReferencedItemProperties().getName());
            _grid.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(_grid.getBackground(), _grid.getForeground(), _grid.getFont()));

        }
        {

            final Label _valueLbl = new Label(stackedPane, SWT.WRAP);

            _valueLbl.setData(EJ_RWT.MARKUP_ENABLED, true);
            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

            if (customCSSKey != null && customCSSKey.trim().length() > 0)
            {
                _valueLbl.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            }
            stackedPane.add(EJRWTStackedItemRendererType.VALUE_LABEL.name(), new StackedPage()
            {

                @Override
                public String getKey()
                {
                    return EJRWTStackedItemRendererType.VALUE_LABEL.name();
                }

                @Override
                public Control getControl()
                {
                    return _valueLbl;
                }
            });
            _valueLbl.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_TEXT);
            _valueLbl.setData(_item.getReferencedItemProperties().getName());
            _valueLbl.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(_valueLbl.getBackground(), _valueLbl.getForeground(), _valueLbl.getFont()));

        }

        {
            int style = SWT.READ_ONLY;
            final Combo _comboField = new Combo(stackedPane, style);

            final ComboViewer _comboViewer = new ComboViewer(_comboField);

            _comboField.setData(_item.getReferencedItemProperties().getName());

            _comboField.addFocusListener(EJRWTStackedItemRenderer.this);
            _comboViewer.setLabelProvider(new ColumnLabelProvider()
            {

                @Override
                public String getText(Object element)
                {
                    if (element instanceof ComboBoxValue)
                    {
                        ComboBoxValue value = (ComboBoxValue) element;

                        return value.getItemValueAsString();
                    }
                    return "";
                }

            });
            _comboViewer.setContentProvider(new ArrayContentProvider());
            _comboViewer.addSelectionChangedListener(new ISelectionChangedListener()
            {

                private ComboBoxValue getComboBoxValue()
                {
                    if (controlState(_comboField))
                    {
                        IStructuredSelection selection = (IStructuredSelection) _comboViewer.getSelection();
                        return (ComboBoxValue) selection.getFirstElement();
                    }

                    return null;
                }

                @Override
                public void selectionChanged(SelectionChangedEvent event)
                {

                    if (!_modifyListener.enable)
                    {
                        return;
                    }

                    if (isValid())
                    {
                        Object old = _baseValue.getValue();
                        ComboBoxValue value = getComboBoxValue();
                        if (value != null)
                        {
                            value.populateReturnItems(_item.getBlock().getBlockController(), _item.getScreenType());
                        }

                        _item.itemValueChaged(getRealValue());

                        setMandatoryBorder(_mandatory);
                    }
                    else
                    {
                        _isValid = true;
                    }

                    _item.executeActionCommand();

                }
            });

            _comboField.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_COMBOBOX);
            _comboField.setData(EJ_RWT.CUSTOM_VARIANT + "_DEF", EJ_RWT.CSS_CV_ITEM_COMBOBOX);
            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

            if (customCSSKey != null && customCSSKey.trim().length() > 0)
            {
                _comboField.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            }
            stackedPane.add(EJRWTStackedItemRendererType.COMBO.name(), new StackedPage()
            {

                @Override
                public String getKey()
                {
                    return EJRWTStackedItemRendererType.COMBO.name();
                }

                @Override
                public Control getControl()
                {
                    return _comboField;
                }
            });
            _comboField.setData(_item.getReferencedItemProperties().getName());
            _comboField.setData("VIEW", _comboViewer);
            _comboField.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(_comboField.getBackground(), _comboField.getForeground(), _comboField.getFont()));
        }

        // EJStackedItemRendererType.SPACER;
        {
            final Label label = new Label(stackedPane, SWT.NONE);
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
            stackedPane.add(EJRWTStackedItemRendererType.SPACER.name(), new StackedPage()
            {

                @Override
                public String getKey()
                {
                    return EJRWTStackedItemRendererType.SPACER.name();
                }

                @Override
                public Control getControl()
                {
                    return label;
                }
            });
            label.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(label.getBackground(), label.getForeground(), label.getFont()));
        }

        // EJStackedItemRendererType.TEXT;
        {

            final Text textField = new Text(stackedPane, SWT.BORDER);
            connectLOVAction(textField, EJRWTStackedItemRendererType.TEXT);
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
            stackedPane.add(EJRWTStackedItemRendererType.TEXT.name(), new StackedPage()
            {

                @Override
                public String getKey()
                {
                    return EJRWTStackedItemRendererType.TEXT.name();
                }

                @Override
                public Control getControl()
                {
                    return textField;
                }
            });
            textField.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(textField.getBackground(), textField.getForeground(), textField.getFont()));

        }
        // EJStackedItemRendererType.TEXT;
        {

            final Text textField = new Text(stackedPane, SWT.BORDER | SWT.MULTI | SWT.WRAP);
            connectLOVAction(textField, EJRWTStackedItemRendererType.TEXT);
            textField.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_TEXTAREA);
            textField.setData(EJ_RWT.CUSTOM_VARIANT + "_DEF", EJ_RWT.CSS_CV_ITEM_TEXTAREA);
            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);
            textField.setData(_item.getReferencedItemProperties().getName());
            textField.addFocusListener(this);
            if (customCSSKey != null && customCSSKey.trim().length() > 0)
            {
                textField.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            }
            textField.addModifyListener(_modifyListener);
            stackedPane.add(EJRWTStackedItemRendererType.TEXT_AREA.name(), new StackedPage()
            {

                @Override
                public String getKey()
                {
                    return EJRWTStackedItemRendererType.TEXT_AREA.name();
                }

                @Override
                public Control getControl()
                {
                    return textField;
                }
            });
            textField.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(textField.getBackground(), textField.getForeground(), textField.getFont()));

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

                            _errorDecoration.setDescriptionText(String.format("Invalid Number format. Should be %s ", _rendererProps.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_FORMAT)));
                            _errorDecoration.show();
                        }
                    }
                }
            });
            connectLOVAction(textField, EJRWTStackedItemRendererType.NUMBER);
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
                        if (value != null && _baseValue.getValue() != null)
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

            stackedPane.add(EJRWTStackedItemRendererType.NUMBER.name(), new StackedPage()
            {

                @Override
                public String getKey()
                {
                    return EJRWTStackedItemRendererType.NUMBER.name();
                }

                @Override
                public Control getControl()
                {
                    return textField;
                }
            });
            textField.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(textField.getBackground(), textField.getForeground(), textField.getFont()));

        }
        // EJStackedItemRendererType.DATE;
        {

            final Text textField = new Text(stackedPane, SWT.BORDER | SWT.RIGHT);
            textField.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_DATE);
            textField.setData(EJ_RWT.CUSTOM_VARIANT + "_DEF", EJ_RWT.CSS_CV_ITEM_DATE);
            textField.setData(_item.getReferencedItemProperties().getName());
            textField.addFocusListener(this);
            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);
            connectLOVAction(textField, EJRWTStackedItemRendererType.DATE);
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
                        if (value != null && _baseValue.getValue() != null)
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

            stackedPane.add(EJRWTStackedItemRendererType.DATE.name(), new StackedPage()
            {

                @Override
                public String getKey()
                {
                    return EJRWTStackedItemRendererType.DATE.name();
                }

                @Override
                public Control getControl()
                {
                    return textField;
                }
            });
            textField.setData("EJRWTItemRendererVisualContext", new EJRWTItemRendererVisualContext(textField.getBackground(), textField.getForeground(), textField.getFont()));

        }

        {

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

    @Override
    public String formatValue(Object obj)
    {
        // TODO Auto-generated method stub
        return null;
    }

    protected void fireTextChange()
    {
        for (ChangeListener listener : new ArrayList<ChangeListener>(_changeListeners))
        {
            listener.changed();
        }
    }

    DecimalFormat createFormatter(NUMBER_TYPE numberType, String format)
    {
        DecimalFormat _decimalFormatter = null;
        Locale defaultLocale = _item.getForm().getFrameworkManager().getCurrentLocale();
        if (defaultLocale == null)
        {
            defaultLocale = Locale.getDefault();
        }

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

        String format = _baseValue != null ? ((EJRWTStackedItemRendererConfig.Date) _baseValue.getConfig()).getFormat() : null;
        if (format != null)
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

    class ComboBoxValue
    {
        private String                  _valueLabel;
        private Object                  _itemValue;

        private HashMap<String, Object> _returnItemValues = new HashMap<String, Object>();

        public ComboBoxValue(EJDataRecord record, String lovItemName, EJRWTStackedItemRendererConfig.Combo comboConfig)
        {
            constructStringValue(record, lovItemName, comboConfig);
        }

        private void constructStringValue(EJDataRecord record, String lovItemName, EJRWTStackedItemRendererConfig.Combo comboConfig)
        {
            if (record == null)
            {
                _itemValue = null;
            }
            else
            {
                _itemValue = record.getValue(lovItemName);
            }

            StringBuffer buffer = new StringBuffer();
            boolean multi = false;

            List<Column> columns = comboConfig.getColumns();

            for (Column column : columns)
            {
                String format = column.getDatatypeFormat();
                boolean display = column.isDisplayed();
                String returnItem = column.getReturnItem();

                // If I have a null record, then I need to initialize all my
                // return items to null when this value is chosen
                if (record == null)
                {
                    _returnItemValues.put(returnItem, null);
                    _valueLabel = "";
                    return;
                }

                Object val = record.getValue(column.getItem());

                if (returnItem != null && !returnItem.isEmpty())
                    _returnItemValues.put(returnItem, val);

                if (display && val != null)
                {
                    if (multi)
                    {
                        buffer.append(" - ");
                    }

                    buffer.append(getFormattedString(val, format));
                    multi = true;
                }
            }

            _valueLabel = buffer.toString();
        }

        @Override
        public String toString()
        {
            return getItemValueAsString();
        }

        public void populateReturnItems(EJBlockController controller, EJScreenType screenType)
        {
            for (String itemName : _returnItemValues.keySet())
            {

                if (itemName == null || itemName.length() == 0)
                {
                    continue;
                }

                EJScreenItemController itemController = controller.getScreenItem(screenType, itemName);
                if (itemController != null)
                {
                    itemController.getItemRenderer().setValue(_returnItemValues.get(itemName));

                    // Was a screen item, so no need to go to the record...
                    continue;
                }

                EJRWTAbstractScreenRenderer abstractScreenRenderer = null;
                switch (screenType)
                {
                    case MAIN:
                        EJDataRecord record = controller.getFocusedRecord();
                        if (record == null)
                        {
                            break;
                        }

                        if (record.containsItem(itemName))
                        {
                            record.setValue(itemName, _returnItemValues.get(itemName));
                        }
                        break;
                    case INSERT:
                        abstractScreenRenderer = (EJRWTAbstractScreenRenderer) controller.getManagedInsertScreenRenderer().getUnmanagedRenderer();
                        break;
                    case QUERY:
                        abstractScreenRenderer = (EJRWTAbstractScreenRenderer) controller.getManagedQueryScreenRenderer().getUnmanagedRenderer();
                        break;
                    case UPDATE:
                        abstractScreenRenderer = (EJRWTAbstractScreenRenderer) controller.getManagedUpdateScreenRenderer().getUnmanagedRenderer();
                        break;
                }
                if (abstractScreenRenderer != null)
                {
                    EJBlockItemRendererRegister itemRegister = abstractScreenRenderer.getItemRegister();
                    itemRegister.setItemValueNoValidate(screenType, itemName, _returnItemValues.get(itemName));
                }

            }
        }

        public String getItemValueAsString()
        {
            return _valueLabel;
        }

        public Object getItemValue()
        {
            return _itemValue;
        }
    }

    private String getFormattedString(Object value, String format)
    {
        Locale defaultLocale = _item.getForm().getFrameworkManager().getCurrentLocale();
        if (defaultLocale == null)
        {
            defaultLocale = Locale.UK;
        }

        if (value instanceof Number)
        {

            NUMBER_TYPE numberType = NUMBER_TYPE.NUMBER;
            if (value != null)
            {

                final String datatypeClassName = value.getClass().getName();

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

            }
            DecimalFormat decimalFormat = createFormatter(numberType, format);

            return decimalFormat.format(value);
        }
        else if (value instanceof Date)
        {
            SimpleDateFormat dateFormat;
            if (format == null || format.trim().length() == 0)
            {
                dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, defaultLocale);
            }
            else
                dateFormat = new SimpleDateFormat(format, defaultLocale);

            return dateFormat.format(value);
        }
        else
        {
            return value.toString();
        }
    }

    private List<ComboBoxValue> loadComboBoxValues(EJRWTStackedItemRendererConfig.Combo config)
    {
        // Initialise both the field and the values.
        List<ComboBoxValue> _comboValues = new ArrayList<ComboBoxValue>();
        String lovDefName = config.getLovDefinition();

        if (lovDefName == null || lovDefName.trim().length() == 0)
        {
            return _comboValues;
        }

        String defName = lovDefName;
        String defItemName = config.getItemName();
        if (defItemName == null || defItemName.trim().length() == 0)

        {
            EJMessage message = new EJMessage("No LovDefinition item has been chosen for the ComboBox renderer properties on item: " + _itemProperties.getName());
            _item.getForm().getFrameworkManager().getApplicationManager().getApplicationMessenger().handleMessage(message);
            return _comboValues;
        }
        if (_item.getBlock().getProperties().isReferenceBlock())
        {
            defName = String.format("%s.%s", _item.getBlock().getProperties().getName(), defName);
        }
        EJLovDefinitionProperties lovDef = _item.getForm().getProperties().getLovDefinitionProperties(defName);

        if (lovDef == null)
        {
            return _comboValues;
        }

        EJLovController lovController = _item.getForm().getLovController(defName);
        if (lovController == null)
        {
            return _comboValues;
        }
        try
        {
            lovController.executeQuery(new EJItemLovController(_item.getBlock().getBlockController().getFormController(), _item, ((EJCoreItemProperties) _itemProperties).getLovMappingPropertiesOnUpdate()));

            if (!_item.getProperties().isMandatory())
            {
                ComboBoxValue emptyValue = new ComboBoxValue(null, defItemName, config);
                _comboValues.add(emptyValue);
            }

            Collection<EJDataRecord> records = lovController.getRecords();
            for (EJDataRecord ejDataRecord : records)
            {
                if (!ejDataRecord.containsItem(defItemName))
                {
                    EJMessage message = new EJMessage("The item name '" + defItemName + "', does not exist within the lov definitions underlying block. Lov Definition: " + defName);
                    _item.getForm().getFrameworkManager().getApplicationManager().getApplicationMessenger().handleMessage(message);
                    return new ArrayList<ComboBoxValue>();
                }

                ComboBoxValue comboValue = new ComboBoxValue(ejDataRecord, defItemName, config);

                _comboValues.add(comboValue);
            }
        }
        catch (Exception e)
        {
            _item.getForm().getFrameworkManager().getApplicationManager().getApplicationMessenger().handleException(e, true);
        }

        return _comboValues;
    }

    @Override
    public List<Object> getValidValues()
    {
        // TODO
        return Collections.emptyList();
    }

    protected List<String> getDateActionKeys(EJFrameworkExtensionProperties propertyGroup)
    {
        List<String> actionKeys = new ArrayList<>();
        actionKeys.add("SHIFT+ARROW_UP");
        actionKeys.add("SHIFT+ARROW_DOWN");
        actionKeys.add("ALT+ARROW_UP");
        actionKeys.add("ALT+ARROW_DOWN");
        actionKeys.add("ARROW_DOWN");
        actionKeys.add("ARROW_UP");
        return actionKeys;
    }

    Date valueToDate(Object value)
    {
        if (value instanceof EJRWTStackedItemRendererValue)
        {
            EJRWTStackedItemRendererValue rendererValue = (EJRWTStackedItemRendererValue) value;
            return (Date) rendererValue.getValue();
        }

        return null;
    }

    protected void dateAction(KeyEvent event)
    {

        if ((event.stateMask & SWT.SHIFT) != 0 && event.keyCode == SWT.ARROW_DOWN)
        {

            addMonth(valueToDate(getValue()), -1);

        }
        else if ((event.stateMask & SWT.ALT) != 0 && event.keyCode == SWT.ARROW_DOWN)
        {

            addYear(valueToDate(getValue()), -1);

        }
        else if (event.keyCode == SWT.ARROW_DOWN)
        {

            addDay(valueToDate(getValue()), -1);

        }
        else if ((event.stateMask & SWT.SHIFT) != 0 && event.keyCode == SWT.ARROW_UP)
        {

            addMonth(valueToDate(getValue()), 1);

        }
        else if ((event.stateMask & SWT.ALT) != 0 && event.keyCode == SWT.ARROW_UP)
        {

            addYear(valueToDate(getValue()), 1);

        }
        else if (event.keyCode == SWT.ARROW_UP)
        {

            Date value = valueToDate(getValue());

            addDay(value, 1);

        }

    }

    private void addMonth(Date value, int i)
    {

        value = value == null ? new Date() : new Date(value.getTime());
        LocalDate date = value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (i > 0)
            date = date.plusMonths(i);
        else
            date = date.minusMonths(-1 * i);

        value = java.sql.Date.valueOf(date);
        Date newValue = convertDate(value);
        setValue(newValue);
        _item.itemValueChaged(newValue);
    }

    private void addDay(Date value, int i)
    {

        value = value == null ? new Date() : new Date(value.getTime());
        LocalDate date = value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (i > 0)
            date = date.plusDays(i);
        else
            date = date.minusDays(-1 * i);

        value = java.sql.Date.valueOf(date);
        Date newValue = convertDate(value);
        setValue(newValue);
        _item.itemValueChaged(newValue);
    }

    private void addYear(Date value, int i)
    {
        value = value == null ? new Date() : new Date(value.getTime());
        LocalDate date = value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (i > 0)
            date = date.plusYears(i);
        else
            date = date.minusYears(-1 * i);

        value = java.sql.Date.valueOf(date);
        Date newValue = convertDate(value);
        setValue(newValue);
        _item.itemValueChaged(newValue);
    }

    private Date convertDate(Date newVal)
    {
        if (newVal != null && !_itemProperties.getDataTypeClassName().equals(Date.class.getName()))
        {
            String dataTypeClass = _itemProperties.getDataTypeClassName();
            if (dataTypeClass.equals("java.sql.Date"))
            {
                newVal = new java.sql.Date(newVal.getTime());
            }
            else if (dataTypeClass.equals("java.sql.Time"))
            {
                newVal = new java.sql.Time(newVal.getTime());
            }
            else if (dataTypeClass.equals("java.sql.Timestamp"))
            {
                newVal = new java.sql.Timestamp(newVal.getTime());
            }
        }
        return newVal;
    }

    private void labletextAliment(Label label, String labelOrientation)
    {
        if (label == null)
        {
            return;
        }
        if (EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_LEFT_PROPERTY.equals(labelOrientation))
        {
            label.setAlignment(SWT.LEFT);
        }
        else if (EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY.equals(labelOrientation))
        {
            label.setAlignment(SWT.RIGHT);
        }
        else if (EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_CENTER_PROPERTY.equals(labelOrientation))
        {
            label.setAlignment(SWT.CENTER);
        }
    }

}
