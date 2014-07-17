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
/**
 * 
 */
package org.entirej.applicationframework.rwt.renderers.item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.components.EJRWTAbstractActionList;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.blocks.definition.interfaces.EJRWTSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTListBoxRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTAbstractScreenRenderer;
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
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreItemProperties;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyList;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJLovDefinitionProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.registry.EJBlockItemRendererRegister;

public class EJRWTListItemRenderer implements EJRWTAppItemRenderer, FocusListener, Serializable, EJRWTItemTextChangeNotifier
{
    private List<ChangeListener>              _changeListeners  = new ArrayList<EJRWTItemTextChangeNotifier.ChangeListener>(1);
    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJScreenItemProperties          _screenItemProperties;
    protected EJItemProperties                _itemProperties;
    protected String                          _registeredItemName;
    protected EJRWTAbstractActionList        _actionControl;
    protected org.eclipse.swt.widgets.List                           _listField;
    protected boolean                         _activeEvent      = true;
    protected Label                           _label;
    protected boolean                         _isValid         = true;
    protected boolean                         _mandatory;
    protected int                             _maxLength;
    protected boolean                         _valueChanged;

    private Map<Object, ListBoxValue>        _listValues     = new HashMap<Object, ListBoxValue>();
    private List<Object>                      _listKays       = new ArrayList<Object>();

    private EJRWTItemRendererVisualContext    _visualContext;

    protected EJCoreVisualAttributeProperties _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties _initialVAProperties;
    protected ControlDecoration               _errorDecoration;
    protected ControlDecoration               _mandatoryDecoration;

    protected Object                          _baseValue;

    protected boolean                         _lovActivated;
    protected boolean                         _lovInitialied;

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
    public void clearValue()
    {
        _baseValue = null;
        try
        {
            _activeEvent = false;
            if (controlState(_listField))
            {
                _listField.setSelection(new String[]{});
            }

        }
        finally
        {
            _activeEvent = true;
        }

    }

    @Override
    public void refreshItemRenderer()
    {
        loadListBoxValues();
        refreshList();
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
    public void gainFocus()
    {
        if (controlState(_listField))
        {
            _listField.forceFocus();
        }
    }

    @Override
    public EJScreenItemController getItem()
    {
        return _item;
    }

    @Override
    public String getRegisteredItemName()
    {
        return _registeredItemName;
    }

    private ListBoxValue getListBoxValue()
    {
        if (controlState(_listField))
        {

            int selectionIndex = _listField.getSelectionIndex();
            if (selectionIndex <= -1)
            {
                return null;
            }

            String value = _listField.getItem(_listField.getSelectionIndex());
            if (value != null && _listValues != null && !_listValues.isEmpty())
            {
                ListBoxValue listBoxValue = _listValues.get(value);

                if (listBoxValue != null)
                {
                    return listBoxValue;
                }
            }
        }

        return null;
    }

    @Override
    public Object getValue()
    {

        ListBoxValue value = getListBoxValue();
        if (value != null)
        {
            return _baseValue = value.getItemValue();
        }

        return null;
    }

    @Override
    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {
        _item = item;
        _itemProperties = _item.getReferencedItemProperties();
        _screenItemProperties = screenItemProperties;
        _rendererProps = _itemProperties.getItemRendererProperties();
        if (_rendererProps.getBooleanProperty(EJRWTListBoxRendererDefinitionProperties.INITIALIES_LOV, true))
        {
            loadListBoxValues();
        }
    }

    private void verifyLOVState()
    {
        if (!_lovInitialied)
        {
            loadListBoxValues();
            refreshList();
        }
    }

    private void loadListBoxValues()
    {
        // Initialise both the field and the values.
        _lovInitialied = true;
        _listValues.clear();
        _listKays.clear();
        String lovDefName = _rendererProps.getStringProperty(EJRWTListBoxRendererDefinitionProperties.LOV_DEFINITION_NAME);

        if (lovDefName == null || lovDefName.trim().length() == 0)
        {
            return;
        }

        String defName = lovDefName;
        String defItemName = "";
        if (lovDefName.indexOf('.') != -1)
        {
            defName = lovDefName.substring(0, lovDefName.indexOf('.'));
            defItemName = lovDefName.substring(lovDefName.indexOf('.') + 1);
        }
        else
        {
            EJMessage message = new EJMessage("No LovDefinition item has been chosen for the ListBox renderer properties on item: "
                    + _itemProperties.getName());
            _item.getForm().getFrameworkManager().getApplicationManager().getApplicationMessenger().handleMessage(message);
            return;
        }
        if (_item.getBlock().getProperties().isReferenceBlock())
        {
            defName = String.format("%s.%s", _item.getBlock().getProperties().getReferencedBlockName(), defName);
        }
        EJLovDefinitionProperties lovDef = _item.getForm().getProperties().getLovDefinitionProperties(defName);

        if (lovDef == null)
        {
            return;
        }

        EJLovController lovController = _item.getForm().getLovController(defName);
        if (lovController == null)
        {
            return;
        }
        try
        {
            lovController.executeQuery(new EJItemLovController(_item.getBlock().getBlockController().getFormController(),
                    _item, ((EJCoreItemProperties)_itemProperties).getLovMappingPropertiesOnUpdate()));



            Collection<EJDataRecord> records = lovController.getRecords();
            for (EJDataRecord ejDataRecord : records)
            {
                if (!ejDataRecord.containsItem(defItemName))
                {
                    EJMessage message = new EJMessage("The item name '" + defItemName
                            + "', does not exist within the lov definitions underlying block. Lov Definition: " + defName);
                    _item.getForm().getFrameworkManager().getApplicationManager().getApplicationMessenger().handleMessage(message);
                    return;
                }

                ListBoxValue listValue = new ListBoxValue(ejDataRecord, defItemName);
                String itemValueAsString = listValue.getItemValueAsString();

                _listValues.put(itemValueAsString, listValue);
                _listKays.add(itemValueAsString);
            }
        }
        catch (Exception e)
        {
            _item.getForm().getFrameworkManager().getApplicationManager().getApplicationMessenger().handleException(e, true);
        }
    }

    @Override
    public boolean isEditAllowed()
    {
        if (controlState(_listField))
        {
            return _listField.isEnabled();
        }
        return false;
    }

    @Override
    public boolean isMandatory()
    {
        return _mandatory;
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
    public boolean isVisible()
    {
        if (controlState(_listField))
        {
            return _listField.isVisible();
        }

        return false;
    }

    @Override
    public void refreshItemRendererProperty(String propertyName)
    {

    }

    @Override
    public void setEditAllowed(boolean editAllowed)
    {
        if (controlState(_listField))
        {
            _listField.setEnabled(editAllowed);
        }
        setMandatoryBorder(editAllowed && _mandatory);

        if (_actionControl != null && !_actionControl.isDisposed())
        {
            _actionControl.setActionVisible(isLovActivated() && editAllowed);
        }
    }

    public boolean isLovActivated()
    {
        return _lovActivated;
    }

    @Override
    public void setHint(String hint)
    {
        if (controlState(_listField))
        {
            _listField.setToolTipText(hint == null ? "" : hint);
        }

    }

    @Override
    public void setInitialValue(Object value)
    {
        setValue(value);
    }

    @Override
    public void setLabel(String label)
    {
        if (controlState(_label))
        {
            _label.setText(label == null ? "" : label);
        }

    }

    @Override
    public void setMandatory(boolean mandatory)
    {
        _mandatory = mandatory;
        setMandatoryBorder(mandatory);
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
        if (!_lovInitialied && value != null)
        {
            verifyLOVState();
            return;
        }
        if (controlState(_listField))
        {
            try
            {
                _activeEvent = false;
                if (value != null)
                {
                    if (_listValues != null && !_listValues.isEmpty())
                    {
                        ListBoxValue boxValue = null;

                        for (ListBoxValue val : _listValues.values())
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
                                EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM,
                                        _item.getName(), val.getItemValue().getClass().getName(), value.getClass().getName());
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
                            if (value.equals(boxValue.getItemValue()))
                            {
                                _listField.setSelection(new String[]{boxValue.getItemValueAsString()});

                            }
                            else if (boxValue.getItemValue() == null)
                            {
                                _listField.setSelection(new String[]{});
                            }
                        }
                    }

                }
                else
                {
                    _listField.setSelection(new String[]{});
                }
            }
            finally
            {
                _activeEvent = true;
            }

            setMandatoryBorder(_mandatory);
        }
    }

    @Override
    public void setVisible(boolean visible)
    {
        if (controlState(_listField))
        {
            _listField.setVisible(visible);
        }

        if (controlState(_label))
        {
            _label.setVisible(visible);
        }
    }

    @Override
    public void setVisualAttribute(EJCoreVisualAttributeProperties visualAttributeProperties)
    {
        _visualAttributeProperties = visualAttributeProperties != null ? visualAttributeProperties : _initialVAProperties;

        if (!controlState(_listField))
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
        if (controlState(_listField))
        {
            _listField.setBackground(background != null ? background : _visualContext.getBackgroundColor());
        }
    }

    private void refreshForeground()
    {
        Color foreground = EJRWTVisualAttributeUtils.INSTANCE.getForeground(_visualAttributeProperties);
        if (controlState(_listField))
        {
            _listField.setForeground(foreground != null ? foreground : _visualContext.getForegroundColor());
        }
    }

    private void refreshFont()
    {
        if (controlState(_listField))
        {
            _listField.setFont(EJRWTVisualAttributeUtils.INSTANCE.getFont(_visualAttributeProperties, _visualContext.getItemFont()));
        }
    }

    @Override
    public void validationErrorOccurred(boolean error)
    {
        if (_errorDecoration != null && !_errorDecoration.getControl().isDisposed())
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
    public boolean valueEqualsTo(Object value)
    {
        return value.equals(this.getValue());
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
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ListItem:\n");
        buffer.append("  RegisteredItemName: ");
        buffer.append(_registeredItemName);
        buffer.append("\n");
        buffer.append("  List: ");
        buffer.append(_listField);
        buffer.append("  Label: ");
        buffer.append(_label);
        buffer.append("  GUI Component: ");
        buffer.append(_listField);

        return buffer.toString();
    }

    @Override
    public Control getGuiComponent()
    {
        return _actionControl;
    }

    @Override
    public Label getGuiComponentLabel()
    {
        return _label;
    }

    @Override
    public void createComponent(Composite composite)
    {
        _actionControl = new EJRWTAbstractActionList(composite)
        {
            private static final long serialVersionUID = 2592484612013403481L;

            @Override
            public org.eclipse.swt.widgets.List createList(Composite parent)
            {
                String alignmentProperty = _rendererProps.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT);
                if (alignmentProperty == null)
                {
                    alignmentProperty = _rendererProps.getStringProperty("ALLIGNMENT");
                }
                String hint = _screenItemProperties.getHint();
                int style = SWT.READ_ONLY|SWT.SINGLE|SWT.BORDER;
                style = getComponentStyle(alignmentProperty, style);
                _listField = new org.eclipse.swt.widgets.List(parent, style);

                if (hint != null && hint.trim().length() > 0)
                {
                    _listField.setToolTipText(hint);
                }
                _listField.setData(_item.getReferencedItemProperties().getName());

                _listField.addFocusListener(EJRWTListItemRenderer.this);
                _listField.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        if (isValid())
                        {
                            ListBoxValue value = getListBoxValue();
                            if (value != null && _activeEvent)
                            {
                                value.populateReturnItems(_item.getBlock().getBlockController(), _item.getScreenType());
                            }
                            if (_activeEvent)
                            {
                                _item.itemValueChaged();
                            }
                            setMandatoryBorder(_mandatory);
                        }
                        else
                        {
                            _isValid = true;
                        }

                        if (_activeEvent)
                        {
                            _item.executeActionCommand();
                        }
                    }
                });
                return _listField;
            }

            @Override
            public Control createActionLabel(Composite parent)
            {
                Label label = new Label(parent, SWT.NONE);
                label.setImage(EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_FIND_LOV));
                label.addFocusListener(EJRWTListItemRenderer.this);
                label.addMouseListener(new MouseListener()
                {
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

                label.setData(EJ_RWT.ACTIVE_KEYS, new String[] { lovKey });
                getListControl().setData(EJ_RWT.ACTIVE_KEYS, new String[] { lovKey });
                addKeyListener(new KeyListener()
                {
                    @Override
                    public void keyReleased(KeyEvent arg0)
                    {
                        if ((arg0.stateMask & SWT.SHIFT) != 0 && arg0.keyCode == SWT.ARROW_DOWN && isLovActivated())
                        {
                            _item.getItemLovController().displayLov(EJLovDisplayReason.LOV);
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

        _visualContext = new EJRWTItemRendererVisualContext(_listField.getBackground(), _listField.getForeground(), _listField.getFont());

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

        refreshList();
        setInitialValue(_baseValue);
    }

    private void refreshList()
    {
        if (controlState(_listField))
        {
            try
            {
                _activeEvent = false;
                _listField.removeAll();

                for (Object item : _listKays)
                {
                    _listField.add(item.toString());
                }
                setValue(_baseValue);
            }
            finally
            {
                _activeEvent = true;
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
        _label.setText(_screenItemProperties.getLabel() == null ? "" : _screenItemProperties.getLabel());
    }

    protected void setMandatoryBorder(boolean req)
    {
        if (_mandatoryDecoration == null)
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

    class ListBoxValue
    {
        private String                  _valueLabel;
        private Object                  _itemValue;

        private HashMap<String, Object> _returnItemValues = new HashMap<String, Object>();

        public ListBoxValue(EJDataRecord record, String lovItemName)
        {
            constructStringValue(record, lovItemName);
        }

        private void constructStringValue(EJDataRecord record, String lovItemName)
        {
            if (record == null)
            {
                _itemValue = null;
            }
            else
            {
                _itemValue = record.getValue(lovItemName);
            }

            final EJFrameworkExtensionPropertyList propertyList = _rendererProps.getPropertyList(EJRWTListBoxRendererDefinitionProperties.DISPLAY_COLUMNS);

            if (propertyList == null)
            {
                return;
            }

            StringBuffer buffer = new StringBuffer();
            boolean multi = false;
            for (EJFrameworkExtensionPropertyListEntry entry : propertyList.getAllListEntries())
            {
                String format = entry.getProperty(EJRWTListBoxRendererDefinitionProperties.COLUMN_FORMAT);
                boolean display = Boolean.valueOf(entry.getProperty(EJRWTListBoxRendererDefinitionProperties.COLUMN_DISPLAYED));
                String returnItem = entry.getProperty(EJRWTListBoxRendererDefinitionProperties.COLUMN_RETURN_ITEM);

                // If I have a null record, then I need to initialize all my
                // return items to null when this value is chosen
                if (record == null)
                {
                    _returnItemValues.put(returnItem, null);
                    _valueLabel = "";
                    return;
                }

                Object val = record.getValue(entry.getProperty(EJRWTListBoxRendererDefinitionProperties.COLUMN_NAME));
                if (val == null)
                {
                    continue;
                }

                _returnItemValues.put(returnItem, val);

                if (display)
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
                            return;
                        }

                        if (record.containsItem(itemName))
                        {
                            record.setValue(itemName, _returnItemValues.get(itemName));
                        }
                        return;
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

        if (value instanceof BigDecimal)
        {
            DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(defaultLocale);

            if (format == null || format.trim().length() == 0)
            {
                format = "##################.00";
            }
            DecimalFormat decimalFormat = new DecimalFormat("", formatSymbols);
            return decimalFormat.format(value);
        }
        else if (value instanceof Date)
        {
            if (format == null || format.trim().length() == 0)
            {
                format = "dd.MM.yyyy HH:mm:ss";
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(format, defaultLocale);

            return dateFormat.format(value);
        }
        else
        {
            return value.toString();
        }
    }

    public void valuedChanged()
    {
        if (!_listField.isFocusControl())
        {
            _item.itemValueChaged();
        }
        else
        {
            _valueChanged = true;
        }
        setMandatoryBorder(_mandatory);
    }

    @Override
    public ColumnLabelProvider createColumnLabelProvider(final EJScreenItemProperties item, EJScreenItemController controller)
    {
        if (!_lovInitialied)
        {
            loadListBoxValues();
        }
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

                    if (value != null && _listValues != null && !_listValues.isEmpty())
                    {
                        ListBoxValue boxValue = null;

                        for (ListBoxValue val : _listValues.values())
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

                            if (val.getItemValue().equals(value))
                            {
                                boxValue = val;
                                break;
                            }
                        }

                        if (boxValue != null)
                        {
                            return boxValue.toString();
                        }
                    }
                }
                return "";
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
