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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTDateItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJMessageFactory;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.enumerations.EJFrameworkMessage;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJRWTDateItemRenderer extends EJRWTTextItemRenderer
{
    private MultiDateFormater _dateFormat;

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
                        _dateFormat.parse(_textField.getText());
                    }
                    catch (ParseException e)
                    {
                        _rendererProps.getStringProperty(EJRWTDateItemRendererDefinitionProperties.PROPERTY_FORMAT);

                        String format = _rendererProps.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_FORMAT);
                        if(format==null || format.length()==0)
                        {
                            format = "eg: "+_dateFormat.format(new Date());
                        }
                        _errorDecoration.setDescriptionText(String.format("Invalid Date format. Should be %s ",
                                format));
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
                        _textField.setText(_dateFormat.format(value));
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

        if (_rendererProps != null && _rendererProps.getBooleanProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_SELECT_ON_FOCUS, false))
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
            if (value != null && !Date.class.isAssignableFrom(value.getClass()))
            {
                EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM, _item.getName(),
                        Date.class.getName(), value.getClass().getName());
                throw new IllegalArgumentException(message.getMessage());
            }
            _baseValue = value;
            if (_displayValueAsLabel)
            {
                if (controlState(_valueLabel))
                {
                    _valueLabel.setText(value != null ? _dateFormat.format(value) : "");
                }
            }
            else
            {
                if (controlState(_textField))
                {
                    _textField.setText(value != null ? _dateFormat.format(value) : "");
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
        _item = item;
        _itemProperties = _item.getReferencedItemProperties();
        _screenItemProperties = screenItemProperties;
        _rendererProps = _itemProperties.getItemRendererProperties();
        _displayValueAsLabel = _rendererProps.getBooleanProperty(EJRWTDateItemRendererDefinitionProperties.PROPERTY_DISPLAY_VAUE_AS_LABEL, false);

        _dateFormat = createDateFormat(_item);
        if (_actionControl != null)
        {
            _actionControl.setCustomActionVisible(isEditAllowed());
        }
    }

    protected static MultiDateFormater createDateFormat(EJScreenItemController item)
    {
        MultiDateFormater dateFormat;
        Locale defaultLocale = item.getForm().getFrameworkManager().getCurrentLocale();
        if (defaultLocale == null)
        {
            defaultLocale = Locale.getDefault();
        }

        EJFrameworkExtensionProperties rendererProps = item.getReferencedItemProperties().getItemRendererProperties();
        String format = rendererProps.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_FORMAT);
        if (format == null || format.length() == 0)
        {
            
            format = rendererProps.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_LOCALE_FORMAT);
            if (format == null || format.length() == 0)
            {
               
                dateFormat = new MultiDateFormater(DateFormat.getDateInstance(DateFormat.SHORT,defaultLocale));
            }
            else
            {
            
                DateFormats formats = DateFormats.valueOf(format);
                switch (formats)
                {
                    case DATE_FULL:
                        dateFormat = new MultiDateFormater(DateFormat.getDateInstance(DateFormat.FULL,defaultLocale));
                        break;
                    case DATE_LONG:
                        dateFormat = new MultiDateFormater(DateFormat.getDateInstance(DateFormat.LONG,defaultLocale));
                        break;
                    case DATE_MEDIUM:
                        dateFormat = new MultiDateFormater(DateFormat.getDateInstance(DateFormat.MEDIUM,defaultLocale));
                        break;
                    case DATE_SHORT:
                        dateFormat = new MultiDateFormater(DateFormat.getDateInstance(DateFormat.SHORT,defaultLocale));
                        break;
                    case DATE_TIME_FULL:
                        dateFormat = new MultiDateFormater(DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL,defaultLocale));
                        break;    
                    case DATE_TIME_LONG:
                        dateFormat = new MultiDateFormater(DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG,defaultLocale));
                        break;    
                    case DATE_TIME_MEDIUM:
                        dateFormat = new MultiDateFormater(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM,defaultLocale));
                        break;    
                    case DATE_TIME_SHORT:
                        dateFormat = new MultiDateFormater(DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT,defaultLocale));
                        break; 
                        
                    case TIME_FULL:
                        dateFormat = new MultiDateFormater(DateFormat.getTimeInstance(DateFormat.FULL,defaultLocale));
                        break;
                    case TIME_LONG:
                        dateFormat = new MultiDateFormater(DateFormat.getTimeInstance(DateFormat.LONG,defaultLocale));
                        break;
                    case TIME_MEDIUM:
                        dateFormat = new MultiDateFormater(DateFormat.getTimeInstance(DateFormat.MEDIUM,defaultLocale));
                        break;
                    case TIME_SHORT:
                        dateFormat = new MultiDateFormater(DateFormat.getTimeInstance(DateFormat.SHORT,defaultLocale));
                        break;
    
                    default:
                        dateFormat = new MultiDateFormater(DateFormat.getDateInstance());
                        break;
                }
            }
            
            
        }
        else
        {
            String[] split = format.split("\\|");
            SimpleDateFormat[] formats = new SimpleDateFormat[split.length];
            for (int i = 0; i < split.length; i++)
            {
                formats[i] = new SimpleDateFormat(split[i], defaultLocale);
            }
            dateFormat = new MultiDateFormater(formats);
        }

        return dateFormat;
    }

    @Override
    public Date getValue()
    {
        if (_displayValueAsLabel || !controlState(_textField))
        {
            return (Date) _baseValue;
        }

        Date value = null;
        try
        {
            if (_textField.getText() != null)
            {
                value = _dateFormat.parse(_textField.getText());
            }

            // convert to correct type if need
            if (value != null && !_itemProperties.getDataTypeClassName().equals(Date.class.getName()))
            {
                String dataTypeClass = _itemProperties.getDataTypeClassName();
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
        }
        catch (ParseException e)
        {
            // ignore error
        }

        _baseValue = value;

        return value;
    }

    @Override
    public ColumnLabelProvider createColumnLabelProvider(final EJScreenItemProperties item, EJScreenItemController controller)
    {
        final MultiDateFormater format = createDateFormat(controller);
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

    @Override
    public void setEditAllowed(boolean editAllowed)
    {
        super.setEditAllowed(editAllowed);
        if (controlState(_actionControl))
        {
            _actionControl.setCustomActionVisible(editAllowed);
        }
    }

    @Override
    public Control createCustomButtonControl(final Composite parent)
    {
        final Label label = new Label(parent, SWT.NONE);
        label.setImage(EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_DATE_SELECTION));
        label.addMouseListener(new MouseListener()
        {
            private final DateFormat  format           = new SimpleDateFormat("yyyy/MM/dd");

            private void selectDate(final Shell abstractDialog, final DateTime calendar)
            {
                try
                {
                    setValue(format.parse(String.format("%d/%d/%d", calendar.getYear(), calendar.getMonth() + 1, calendar.getDay())));
                    _item.itemValueChaged();
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
                final Shell abstractDialog = new Shell(shell, SWT.ON_TOP | SWT.APPLICATION_MODAL | SWT.TITLE | SWT.CLOSE);
                abstractDialog.setLayout(new GridLayout(2, false));
                final DateTime calendar = new DateTime(abstractDialog, SWT.CALENDAR | SWT.BORDER);

                Date currentDate = getValue();
                if (currentDate != null)
                {
                    String dateText = format.format(currentDate);
                    String[] split = dateText.split("/");
                    if (split.length == 3)
                    {
                        calendar.setYear(Integer.parseInt(split[0]));
                        calendar.setMonth(Integer.parseInt(split[1])-1);//month index from 0 
                        calendar.setDay(Integer.parseInt(split[2]));
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
                GridData gridData = new GridData();
                gridData.horizontalSpan = 2;
                calendar.setLayoutData(gridData);

                Button today = new Button(abstractDialog, SWT.PUSH);
                today.setText("Today");
                today.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        try
                        {
                            setValue(format.parse(format.format(new Date())));
                            _item.itemValueChaged();
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
                Button clear = new Button(abstractDialog, SWT.PUSH);
                clear.setText("Clear");
                clear.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {

                        setValue(null);
                        _item.itemValueChaged();

                        if (!abstractDialog.isDisposed())
                        {
                            abstractDialog.close();
                            abstractDialog.dispose();
                        }
                        _item.gainFocus();
                    }
                });

                abstractDialog.pack();
                Rectangle shellBounds = shell.getBounds();
                Point dialogSize = abstractDialog.getSize();
                abstractDialog.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x) / 2, shellBounds.y + (shellBounds.height - dialogSize.y) / 2);
                abstractDialog.setText("Date Selection");
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

    private static class MultiDateFormater
    {

        
        private final DateFormat dateFormat;
        private final SimpleDateFormat[] formats;

        public MultiDateFormater(SimpleDateFormat... formats)
        {
            this.formats = formats;
            dateFormat=null;
        }
        public MultiDateFormater(DateFormat dateFormat)
        {
            this.formats = new  SimpleDateFormat[0];
            this.dateFormat=dateFormat;
        }
        

        public Date parse(String text) throws ParseException
        {

            if(dateFormat!=null)
            {
                return dateFormat.parse(text);
            }
            for (SimpleDateFormat format : formats)
            {
                try
                {
                    Date parse = format.parse(text);
                    String pattern = format.toPattern();
                    boolean fixday = !pattern.contains("d");
                    boolean fixmMonth = !pattern.contains("M");
                    boolean fixmYear = !pattern.contains("y");

                    if (fixday || fixmMonth || fixmYear)
                    {
                        Date currentDate = new Date();
                        Calendar calendar = format.getCalendar();

                        if (fixmYear)
                        {
                            calendar.setTime(currentDate);
                            int year = calendar.get(Calendar.YEAR);

                            calendar.setTime(parse);
                            calendar.set(Calendar.YEAR, year);
                            parse = calendar.getTime();
                        }
                        if (fixmMonth)
                        {
                            calendar.setTime(currentDate);
                            int month = calendar.get(Calendar.MONTH);

                            calendar.setTime(parse);
                            calendar.set(Calendar.MONTH, month);
                            parse = calendar.getTime();
                        }
                        if (fixday)
                        {
                            calendar.setTime(currentDate);
                            int day = calendar.get(Calendar.DATE);

                            calendar.setTime(parse);
                            calendar.set(Calendar.DATE, day);
                            parse = calendar.getTime();
                        }
                    }
                    return parse;
                }
                catch (ParseException e)
                {
                    // ignore try next
                }
            }
            throw new ParseException(text, 0);

        }

        public String format(Object value)
        {
            if(dateFormat!=null)
            {
                return dateFormat.format(value);
            }
            
            for (SimpleDateFormat format : formats)
            {
                return format.format(value);
            }
            return "";
        }

    }
    
    enum DateFormats
    {
        DATE_LONG, DATE_MEDIUM, DATE_SHORT, DATE_FULL,

        DATE_TIME_LONG, DATE_TIME_MEDIUM, DATE_TIME_SHORT, DATE_TIME_FULL,

        TIME_LONG, TIME_MEDIUM, TIME_SHORT, TIME_FULL,

    }
}
