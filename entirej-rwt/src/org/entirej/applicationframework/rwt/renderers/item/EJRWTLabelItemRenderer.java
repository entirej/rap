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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTLabelItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.utils.EJRWTItemRendererVisualContext;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJRWTLabelItemRenderer implements EJRWTAppItemRenderer, FocusListener, Serializable
{
    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJScreenItemProperties          _screenItemProperties;
    protected EJItemProperties                _itemProperties;
    protected String                          _registeredItemName;
    protected SWTComponentAdapter             _labelField;

    protected EJCoreVisualAttributeProperties _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties _initialVAProperties;
    protected ControlDecoration               _errorDecoration;
    protected ControlDecoration               _mandatoryDecoration;
    protected VALUE_CASE                      _valueCase         = VALUE_CASE.DEFAULT;
    private EJRWTItemRendererVisualContext    _visualContext;
    private boolean                           _displayAsHyperlink = false;

    protected Object                          _baseValue;

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

        final String caseProperty = _rendererProps.getStringProperty(EJRWTLabelItemRendererDefinitionProperties.PROPERTY_CASE);
        if (caseProperty != null && caseProperty.trim().length() > 0)
        {
            if (caseProperty.equals(EJRWTLabelItemRendererDefinitionProperties.PROPERTY_CASE_LOWER))
            {
                _valueCase = VALUE_CASE.LOWER;
            }
            else if (caseProperty.equals(EJRWTLabelItemRendererDefinitionProperties.PROPERTY_CASE_UPPER))
            {
                _valueCase = VALUE_CASE.UPPER;
            }
        }
    }

    @Override
    public void setLabel(String label)
    {
        if (_labelField != null && controlState(_labelField.getControl()))
        {
            _labelField.setText(label == null ? "" : label);
        }
    }

    @Override
    public void setHint(String hint)
    {
        if (_labelField != null && controlState(_labelField.getControl()))
        {
            _labelField.getControl().setToolTipText(hint == null ? "" : hint);
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

        return _labelField.getControl();
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
        if (_labelField != null && controlState(_labelField.getControl()))
        {

            _labelField.setText(_item.getForm().translateText(_screenItemProperties.getLabel()));
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
        if (_labelField != null && controlState(_labelField.getControl()))
        {
            return _labelField.getControl().isEnabled();
        }
        return false;
    }

    @Override
    public boolean isVisible()
    {
        if (_labelField != null && controlState(_labelField.getControl()))
        {
            return _labelField.getControl().isVisible();
        }

        return false;
    }

    @Override
    public boolean isValid()
    {
        return true;
    }

    @Override
    public void gainFocus()
    {
        if (_labelField != null && controlState(_labelField.getControl()))
        {
            _labelField.getControl().forceFocus();
        }
    }

    @Override
    public void setEditAllowed(boolean editAllowed)
    {
        if (_labelField != null && controlState(_labelField.getControl()))
        {
            _labelField.getControl().setEnabled(editAllowed);
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
        if (_labelField != null && controlState(_labelField.getControl()))
        {
            if (value == null)
            {
                value = _item.getForm().translateText(_screenItemProperties.getLabel());
            }

            _labelField.setText(value == null ? "" : toCaseValue(value.toString()));
        }
    }

    @Override
    public void setVisible(boolean visible)
    {
        if (_labelField != null)
        {
            _labelField.getControl().setVisible(visible);
        }
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
        if (_labelField == null || _labelField.getControl() == null || _labelField.getControl().isDisposed())
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

        _labelField.getControl().setBackground(background != null ? background : _visualContext.getBackgroundColor());
    }

    private void refreshForeground()
    {
        Color foreground = EJRWTVisualAttributeUtils.INSTANCE.getForeground(_visualAttributeProperties);

        _labelField.getControl().setForeground(foreground != null ? foreground : _visualContext.getForegroundColor());
    }

    private void refreshFont()
    {
        _labelField.getControl().setFont(EJRWTVisualAttributeUtils.INSTANCE.getFont(_visualAttributeProperties, _visualContext.getItemFont()));
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
        String alignmentProperty = _rendererProps.getStringProperty(EJRWTLabelItemRendererDefinitionProperties.PROPERTY_ALIGNMENT);
        if (alignmentProperty == null)
        {
            alignmentProperty = _rendererProps.getStringProperty("ALLIGNMENT");
        }
        boolean textWrapProperty = _rendererProps.getBooleanProperty(EJRWTLabelItemRendererDefinitionProperties.PROPERTY_TEXT_WRAP, false);
        String hint = _screenItemProperties.getHint();

        int style = SWT.NONE;
        style = getComponentStyle(alignmentProperty, style);

        if (textWrapProperty)
        {
            style = style + SWT.WRAP;
        }

        _displayAsHyperlink = _rendererProps.getBooleanProperty(EJRWTLabelItemRendererDefinitionProperties.PROPERTY_DISPLAY_AS_HYPERLINK, false);
        final String label = _screenItemProperties.getLabel();
        if (!_displayAsHyperlink)
        {
            final Label labelField = new Label(composite, style);

            String pictureName = _rendererProps.getStringProperty(EJRWTLabelItemRendererDefinitionProperties.PROPERTY_PICTURE);

            if (pictureName != null && pictureName.length() > 0)
            {
                if (pictureName != null && pictureName.trim().length() > 0)
                {
                    labelField.setImage(EJRWTImageRetriever.get(pictureName));
                }
            }

            _labelField = new SWTComponentAdapter()
            {
                @Override
                public void setText(String text)
                {
                    if (controlState(labelField))
                    {
                        labelField.setText(text);
                    }
                }

                @Override
                public String getText()
                {
                    return labelField.getText();
                }

                @Override
                public Control getControl()
                {
                    return labelField;
                }
            };
        }
        else
        {
            final Link linkField ;
            final Control control ;
            //use workaround to make sure link also provide alignment
            if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
            {
                if (alignmentProperty.equals(EJRWTLabelItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_LEFT))
                {
                    control = linkField = new Link(composite, style);
                }
                else if (alignmentProperty.equals(EJRWTLabelItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_RIGHT))
                {
                    EJRWTEntireJGridPane sub = new EJRWTEntireJGridPane(composite, 3);
                    control = sub;
                    sub.cleanLayout();
                    new Label(sub, SWT.NONE).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
                   
                    new Label(sub, SWT.NONE).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
                    linkField = new Link(sub, style);
                }
                else if (alignmentProperty.equals(EJRWTLabelItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_CENTER))
                {
                    EJRWTEntireJGridPane sub = new EJRWTEntireJGridPane(composite, 3);
                    control = sub;
                    sub.cleanLayout();
                    new Label(sub, SWT.NONE).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
                    linkField = new Link(sub, style);
                    new Label(sub, SWT.NONE).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
                   
                }
                else
                {
                    control =linkField = new Link(composite, style);
                }
            }else
            {
                control = linkField = new Link(composite, style);
            }
            _labelField = new SWTComponentAdapter()
            {
                String value;

                @Override
                public void setText(String text)
                {
                    if (controlState(linkField))
                    {
                        linkField.setText(String.format("<a>%s</a>", value = text));
                    }
                }

                @Override
                public String getText()
                {
                    return value;
                }

                @Override
                public Control getControl()
                {
                    return control;
                }

            };

            linkField.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    _item.executeActionCommand();
                }
            });
        }

        _labelField.setText(label != null ? label : "");
        _labelField.getControl().setToolTipText(hint != null ? hint : "");
        _labelField.getControl().setData(_item.getReferencedItemProperties().getName());
        _labelField.getControl().addFocusListener(this);
        _visualContext = new EJRWTItemRendererVisualContext(_labelField.getControl().getBackground(), _labelField.getControl().getForeground(), _labelField
                .getControl().getFont());

        _mandatoryDecoration = new ControlDecoration(_labelField.getControl(), SWT.TOP | SWT.LEFT);
        _errorDecoration = new ControlDecoration(_labelField.getControl(), SWT.TOP | SWT.LEFT);
        _errorDecoration.setImage(getDecorationImage(FieldDecorationRegistry.DEC_ERROR));
        _mandatoryDecoration.setImage(getDecorationImage(FieldDecorationRegistry.DEC_REQUIRED));
        _mandatoryDecoration.setShowHover(true);
        _mandatoryDecoration.setDescriptionText(_screenItemProperties.getLabel() == null || _screenItemProperties.getLabel().isEmpty() ? "Required Item"
                : String.format("%s is required", _screenItemProperties.getLabel()));
        _errorDecoration.hide();
        _mandatoryDecoration.hide();
    }

    protected int getComponentStyle(String alignmentProperty, int style)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJRWTLabelItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_LEFT))
            {
                style = style | SWT.LEFT;
            }
            else if (alignmentProperty.equals(EJRWTLabelItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_RIGHT))
            {
                style = style | SWT.RIGHT;
            }
            else if (alignmentProperty.equals(EJRWTLabelItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_CENTER))
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
                        return value.toString();
                    }
                }
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

    private static interface SWTComponentAdapter
    {
        Control getControl();

        String getText();

        void setText(String string);
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
}
