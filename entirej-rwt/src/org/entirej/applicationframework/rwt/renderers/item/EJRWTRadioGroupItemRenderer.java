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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTRadioButtonItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.utils.EJRWTItemRendererVisualContext;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyList;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJRWTRadioGroupItemRenderer implements EJRWTAppItemRenderer, FocusListener, Serializable
{
    private Map<String, RadioButtonValue>     _radioButtons;
    private Composite                         _radioGroup;
    private boolean                           _displayFrame         = true;
    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJScreenItemProperties          _screenItemProperties;
    protected EJItemProperties                _itemProperties;
    protected String                          _registeredItemName;
    protected boolean                         _mandatory;
    private boolean                           _isValid              = true;
    private RadioButtonSelection              _radioButtonSelection = new RadioButtonSelection();

    protected EJCoreVisualAttributeProperties _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties _initialVAProperties;
    protected ControlDecoration               _errorDecoration;
    protected ControlDecoration               _mandatoryDecoration;
    private EJRWTItemRendererVisualContext    _visualContext;
    protected Object                          _baseValue;
    protected String                          _defaultButtonId;

    @Override
    public boolean useFontDimensions()
    {
        return false;
    }

    @Override
    public void refreshItemRenderer()
    {

    }

    protected boolean controlState(Control control)
    {
        return control != null && !control.isDisposed();
    }

    @Override
    public void clearValue()
    {
        _baseValue = null;
        setInitialValue(_baseValue);

    }

    private void addListeners()
    {
        _radioButtonSelection.active.set(true);
    }

    private void removeListeners()
    {
        _radioButtonSelection.active.set(false);
    }

    @Override
    public void enableLovActivation(boolean arg0)
    {

    }

    @Override
    public void gainFocus()
    {
        if (controlState(_radioGroup))
        {
            for (RadioButtonValue buttonValue : _radioButtons.values())
            {
                Button button = buttonValue.getButton();
                if (button.getSelection())
                {
                    button.forceFocus();
                    break;
                }
            }
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

    @Override
    public Object getValue()
    {
        if (!controlState(_radioGroup))
        {
            return _baseValue;
        }

        for (RadioButtonValue buttonValue : _radioButtons.values())
        {
            Button button = buttonValue.getButton();

            if (button.getSelection())
            {
                return _baseValue = buttonValue.getValue();
            }
        }

        return _baseValue;
    }

    @Override
    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {
        _item = item;
        _itemProperties = _item.getReferencedItemProperties();
        _screenItemProperties = screenItemProperties;
        _rendererProps = _itemProperties.getItemRendererProperties();

        _radioButtons = new HashMap<String, RadioButtonValue>();

        _displayFrame = _rendererProps.getBooleanProperty(EJRWTRadioButtonItemRendererDefinitionProperties.PROPERTY_SHOW_BORDER, true);

    }

    @Override
    public boolean isEditAllowed()
    {
        if (controlState(_radioGroup))
        {
            _radioGroup.isEnabled();
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
        if (controlState(_radioGroup))
        {
            return _radioGroup.isVisible();
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
        if (_radioGroup != null && !_radioGroup.isDisposed())
        {
            _radioGroup.setEnabled(editAllowed);
        }
    }

    @Override
    public void setHint(String hint)
    {
        if (controlState(_radioGroup))
        {
            _radioGroup.setToolTipText(hint == null ? "" : hint);
        }
    }

    @Override
    public void setInitialValue(Object value)
    {
        if(controlState(_radioGroup))
        {
            try
            {
                removeListeners();
                for (RadioButtonValue buttonValue : _radioButtons.values())
                {
                    if (buttonValue.getValue().equals(value))
                    {
                        buttonValue.getButton().setSelection(true);
                    }
                    else
                    {
                        buttonValue.getButton().setSelection(false);
                    }
                }
                if (value == null)
                {
                    if (_defaultButtonId != null)
                    {
                        for (RadioButtonValue buttonValue : _radioButtons.values())
                        {
                            if (buttonValue.ID.equals(_defaultButtonId))
                            {
                                buttonValue.getButton().setSelection(true);
                                break;
                            }

                        }
                    }
                }
            }
            finally
            {
                addListeners();
                setMandatoryBorder(_mandatory);
            }
        }
        
    }

    @Override
    public void setLabel(String label)
    {
        if (controlState(_radioGroup) && _radioGroup instanceof Group)
        {
            ((Group) _radioGroup).setText(label == null ? "" : label);
        }
    }

    protected void setMandatoryBorder(boolean req)
    {
        if (_mandatoryDecoration == null|| _radioGroup.isDisposed())
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
        if (controlState(_radioGroup))
        {
            for (RadioButtonValue buttonValue : _radioButtons.values())
            {
                if (buttonValue.getValue().equals(value))
                {
                    buttonValue.getButton().setSelection(true);
                }
                else
                {
                    buttonValue.getButton().setSelection(false);
                }
            }
            if (value == null)
            {
                if (_defaultButtonId != null)
                {
                    for (RadioButtonValue buttonValue : _radioButtons.values())
                    {
                        if (buttonValue.ID.equals(_defaultButtonId))
                        {
                            buttonValue.getButton().setSelection(true);
                            valueChanged();
                            break;
                        }

                    }
                }
            }
            setMandatoryBorder(_mandatory);
        }
    }

    @Override
    public void setVisible(boolean visible)
    {
        if (controlState(_radioGroup))
        {
            _radioGroup.setVisible(visible);
            Control[] children = _radioGroup.getChildren();
            for (int i = 0; i < children.length; i++)
            {
                children[i].setVisible(visible);
            }
        }
    }

    @Override
    public void setVisualAttribute(EJCoreVisualAttributeProperties visualAttributeProperties)
    {
        _visualAttributeProperties = visualAttributeProperties != null ? visualAttributeProperties : _initialVAProperties;

        for (RadioButtonValue buttonValue : _radioButtons.values())
        {
            Button button = buttonValue.getButton();
            if (button == null || button.isDisposed())
            {
                continue;
            }
            refreshBackground(button);
            refreshForeground(button);
            refreshFont(button);
        }
    }

    @Override
    public EJCoreVisualAttributeProperties getVisualAttributeProperties()
    {
        return _visualAttributeProperties;
    }

    private void refreshBackground(Button radioButton)
    {
        Color background = EJRWTVisualAttributeUtils.INSTANCE.getBackground(_visualAttributeProperties);
        if (radioButton != null)
        {
            radioButton.setBackground(background != null ? background : _visualContext.getBackgroundColor());
        }
    }

    private void refreshForeground(Button radioButton)
    {
        Color foreground = EJRWTVisualAttributeUtils.INSTANCE.getForeground(_visualAttributeProperties);
        if (radioButton != null)
        {
            radioButton.setForeground(foreground != null ? foreground : _visualContext.getForegroundColor());
        }
    }

    private void refreshFont(Button radioButton)
    {
        if (radioButton != null)
        {
            radioButton.setFont(EJRWTVisualAttributeUtils.INSTANCE.getFont(_visualAttributeProperties, _visualContext.getItemFont()));
        }
    }

    @Override
    public void validationErrorOccurred(boolean error)
    {
        if (_errorDecoration == null)
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
    public Control getGuiComponent()
    {
        return _radioGroup;
    }

    @Override
    public Label getGuiComponentLabel()
    {
        return null;
    }

    @Override
    public void createComponent(Composite composite)
    {
        final String hint = _screenItemProperties.getHint();
        int style = SWT.NO_FOCUS | (_displayFrame ? SWT.SHADOW_ETCHED_IN : SWT.NONE);

        if (_displayFrame)
        {
            _radioGroup = new Group(composite, style);
            if (_screenItemProperties.getLabel() != null)
            {
                ((Group) _radioGroup).setText(_screenItemProperties.getLabel());
            }

            composite = _radioGroup;
        }
        else
        {
            _radioGroup = new Composite(composite, SWT.NO_FOCUS);
        }
        if (hint != null && hint.trim().length() > 0)
        {
            _radioGroup.setToolTipText(hint);
        }
        _radioGroup.setData(_item.getReferencedItemProperties().getName());

        int type = SWT.HORIZONTAL;

        if (EJRWTRadioButtonItemRendererDefinitionProperties.PROPERTY_ORIENTATION_VERTICAL.equals(_rendererProps
                .getStringProperty(EJRWTRadioButtonItemRendererDefinitionProperties.PROPERTY_ORIENTATION)))
        {
            type = SWT.VERTICAL;
        }

        RowLayout rowLayout = new RowLayout(type);
        _radioGroup.setLayout(rowLayout);
        rowLayout.marginTop = 0;
        rowLayout.marginBottom = 0;
        rowLayout.marginRight = 0;
        rowLayout.marginLeft = 0;
        rowLayout.marginHeight = 0;
        rowLayout.marginWidth = 0;
        _radioGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        EJFrameworkExtensionPropertyList radioButtons = _rendererProps.getPropertyList(EJRWTRadioButtonItemRendererDefinitionProperties.PROPERTY_RADIO_BUTTONS);
        for (EJFrameworkExtensionPropertyListEntry listEntry : radioButtons.getAllListEntries())
        {
            Object value = getValueAsObject(_item.getReferencedItemProperties().getDataTypeClass(),
                    listEntry.getProperty(EJRWTRadioButtonItemRendererDefinitionProperties.PROPERTY_VALUE));
            Button button = new Button(_radioGroup, SWT.RADIO);

            // Store the button and the button values for future reference
            String id = listEntry.getProperty(EJRWTRadioButtonItemRendererDefinitionProperties.PROPERTY_NAME);
            _radioButtons.put(id, new RadioButtonValue(id,button, value));

            // Set the button properties
            button.setData(listEntry.getProperty(EJRWTRadioButtonItemRendererDefinitionProperties.PROPERTY_NAME));
            button.setText(_item.getForm().translateText(listEntry.getProperty(EJRWTRadioButtonItemRendererDefinitionProperties.PROPERTY_LABEL)));

            // button.setActionCommand(screenItemProperties.getActionCommand());

            _defaultButtonId = _rendererProps.getStringProperty(EJRWTRadioButtonItemRendererDefinitionProperties.PROPERTY_DEFAULT_BUTTON);
            if (_defaultButtonId != null)
            {
                if (_defaultButtonId.equals(listEntry.getProperty(EJRWTRadioButtonItemRendererDefinitionProperties.PROPERTY_NAME)))
                {
                    button.setSelection(true);
                }
                else
                {
                    button.setSelection(false);
                }
            }

            // Add the listeners. These will be needed to trigger events within
            // EntireJ
            button.addFocusListener(this);
            button.addSelectionListener(_radioButtonSelection);

        }

        _mandatoryDecoration = new ControlDecoration(_radioGroup, SWT.TOP | SWT.LEFT);
        _errorDecoration = new ControlDecoration(_radioGroup, SWT.TOP | SWT.LEFT);
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
        addListeners();
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

    protected class RadioButtonValue
    {

        private Button button;
        private Object value;
        private Object ID;

        /**
         * @param button
         * @param value
         */
        public RadioButtonValue(Object ID, Button button, Object value)
        {
            this.ID = ID;
            this.button = button;
            this.value = value;
        }

        public Button getButton()
        {
            return button;
        }

        public Object getValue()
        {
            return value;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + (button == null ? 0 : button.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            RadioButtonValue other = (RadioButtonValue) obj;
            if (!getOuterType().equals(other.getOuterType()))
            {
                return false;
            }
            if (button == null)
            {
                if (other.button != null)
                {
                    return false;
                }
            }
            else if (!button.equals(other.button))
            {
                return false;
            }
            return true;
        }

        private EJRWTRadioGroupItemRenderer getOuterType()
        {
            return EJRWTRadioGroupItemRenderer.this;
        }
    }

    private Object getValueAsObject(Class<?> dataType, String value)
    {
        try
        {
            Constructor<?> constructor = dataType.getConstructor(String.class);
            Object val = constructor.newInstance(value);
            return val;
        }
        catch (SecurityException e)
        {
            throw new EJApplicationException("Unable to find a constructor with a String parameter for the data type: " + dataType.getName(), e);
        }
        catch (NoSuchMethodException e)
        {
            throw new EJApplicationException("Unable to find a constructor with a String parameter for the data type: " + dataType.getName(), e);
        }
        catch (IllegalArgumentException e)
        {
            throw new EJApplicationException("Unable create a new data type: " + dataType.getName() + ". With a single string parameter of: " + value);
        }
        catch (InstantiationException e)
        {
            throw new EJApplicationException("Unable create a new data type: " + dataType.getName() + ". With a single string parameter of: " + value);
        }
        catch (IllegalAccessException e)
        {
            throw new EJApplicationException("Unable create a new data type: " + dataType.getName() + ". With a single string parameter of: " + value);
        }
        catch (InvocationTargetException e)
        {
            throw new EJApplicationException("Unable create a new data type: " + dataType.getName() + ". With a single string parameter of: " + value);
        }
    }

    private class RadioButtonSelection extends SelectionAdapter
    {
        AtomicBoolean active  = new AtomicBoolean(true);
        @Override
        public void widgetSelected(SelectionEvent e)
        {
            if (active.get() && e.widget instanceof Button && ((Button) e.widget).getSelection())
            {
                valueChanged();
            }
        }
    }

    public void valueChanged()
    {
        _item.executeActionCommand();
        _item.itemValueChaged();
        setMandatoryBorder(_mandatory);
    }

    @Override
    public ColumnLabelProvider createColumnLabelProvider(final EJScreenItemProperties item, EJScreenItemController controller)
    {
        final Map<Object, String> items = new HashMap<Object, String>();
        EJFrameworkExtensionPropertyList radioButtons = _rendererProps.getPropertyList(EJRWTRadioButtonItemRendererDefinitionProperties.PROPERTY_RADIO_BUTTONS);
        for (EJFrameworkExtensionPropertyListEntry listEntry : radioButtons.getAllListEntries())
        {
            Object value = getValueAsObject(_item.getReferencedItemProperties().getDataTypeClass(),
                    listEntry.getProperty(EJRWTRadioButtonItemRendererDefinitionProperties.PROPERTY_VALUE));
            String lable = _item.getForm().translateText(listEntry.getProperty(EJRWTRadioButtonItemRendererDefinitionProperties.PROPERTY_LABEL));
            items.put(value, lable);
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

                    if (value != null)
                    {
                        return items.get(value);
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
}
