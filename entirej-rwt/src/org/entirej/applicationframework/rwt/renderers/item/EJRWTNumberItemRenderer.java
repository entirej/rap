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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.utils.EJRWTItemRendererVisualContext;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJMessageFactory;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.enumerations.EJFrameworkMessage;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJRWTNumberItemRenderer extends EJRWTTextItemRenderer implements Serializable
{
    private DecimalFormat                  _decimalFormatter;
    private NUMBER_TYPE                    _numberType;

    public enum NUMBER_TYPE
    {
        NUMBER, INTEGER, FLOAT, BIG_DECIMAL, DOUBLE, LONG
    };

    @Override
    protected Label newVlaueLabel(Composite composite)
    {
        return new Label(composite, SWT.RIGHT);
    }

    @Override
    protected void setValueLabelAlign(final String alignmentProperty)
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
        else
        {
            _valueLabel.setAlignment(SWT.RIGHT);
        }
    }

    @Override
    protected int getComponentStyle(final String alignmentProperty, int style)
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
        else
        {
            style = style | SWT.RIGHT;
        }
        return style;
    }

    @Override
    protected Text newTextField(Composite composite, int style)
    {
        _textField = super.newTextField(composite, style);
        _textField.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent arg0)
            {
                if (_textField.getText().trim().length() > 0)
                {
                    try
                    {
                        _decimalFormatter.parse(_textField.getText());
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
        _textField.addFocusListener(new FocusListener()
        {
            @Override
            public void focusLost(FocusEvent arg0)
            {
                try
                {
                    _modifyListener.enable = false;
                    Object value = getValue();
                    if (value != null)
                    {
                        _textField.setText(_decimalFormatter.format(value));
                    }
                    else
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
            public void focusGained(FocusEvent arg0)
            {

            }
        });

        if (_rendererProps != null && _rendererProps.getBooleanProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_SELECT_ON_FOCUS, true))
        {
            _textField.addFocusListener(new FocusListener()
            {
                @Override
                public void focusLost(FocusEvent arg0)
                {
                    // ignore
                }

                @Override
                public void focusGained(FocusEvent arg0)
                {
                    _textField.selectAll();
                }
            });
        }

        return _textField;
    }

    @Override
    public void setValue(Object value)
    {
        try
        {
            _modifyListener.enable = false;
            if (value != null && !Number.class.isAssignableFrom(value.getClass()))
            {
                EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM, _item.getName(),
                        Number.class.getName(), value.getClass().getName());
                throw new IllegalArgumentException(message.getMessage());
            }
            _baseValue = value;
            if (_displayValueAsLabel)
            {
                if (controlState(_valueLabel))
                {
                    _valueLabel.setText(value != null ? _decimalFormatter.format(value) : "");
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

                    _textField.setText(value != null ? _decimalFormatter.format(value) : "");
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
    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {
        super.initialise(item, screenItemProperties);

        _numberType = getNumberType(_item);
        _decimalFormatter = createFormatter(_item, _numberType);
    }

    private static NUMBER_TYPE getNumberType(EJScreenItemController _item)
    {
        final String datatypeClassName = _item.getReferencedItemProperties().getDataTypeClassName();
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

    private static DecimalFormat createFormatter(EJScreenItemController item, NUMBER_TYPE numberType)
    {
        DecimalFormat _decimalFormatter = null;
        EJFrameworkExtensionProperties _rendererProps = item.getReferencedItemProperties().getItemRendererProperties();
        Locale defaultLocale = item.getForm().getFrameworkManager().getCurrentLocale();
        if (defaultLocale == null)
        {
            defaultLocale = Locale.getDefault();
        }

        String format = _rendererProps.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_FORMAT);

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

        Number value = null;
        try
        {
            value = _decimalFormatter.parse(_textField.getText());
        }
        catch (ParseException e)
        {
            // ignore error
        }

        if (value == null)
        {
            return _baseValue = value;
        }
        try
        {
            switch (_numberType)
            {
                case INTEGER:
                    return value.intValue();
                case LONG:
                    return value.longValue();
                case FLOAT:
                    return value.floatValue();
                case DOUBLE:
                    return value.doubleValue();
                case BIG_DECIMAL:
                    return new BigDecimal(value.toString());

            }
        }
        catch (NumberFormatException nfe)
        {
            nfe.printStackTrace();
        }

        return _baseValue = value;
    }

    @Override
    public ColumnLabelProvider createColumnLabelProvider(final EJScreenItemProperties item, EJScreenItemController controller)
    {
        NUMBER_TYPE numberType = getNumberType(controller);
        final DecimalFormat format = createFormatter(controller, numberType);
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
                    if (value != null && value instanceof Number)
                    {
                        return format.format(value);
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
                        if (value1 instanceof Comparable)
                        {
                            @SuppressWarnings("unchecked")
                            Comparable<Object> comparable = (Comparable<Object>) value1;
                            return comparable.compareTo(value2);
                        }
                    }
                }
                return 0;
            }
        };
    }
}
