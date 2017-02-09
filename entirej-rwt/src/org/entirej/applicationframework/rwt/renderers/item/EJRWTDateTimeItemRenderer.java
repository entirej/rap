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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.components.EJRWTAbstractActionDateTime;
import org.entirej.applicationframework.rwt.application.components.EJRWTAbstractLabel;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTButtonItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTDateTimeItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.utils.EJRWTItemRendererVisualContext;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJMessageFactory;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.enumerations.EJFrameworkMessage;
import org.entirej.framework.core.enumerations.EJLovDisplayReason;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJRWTDateTimeItemRenderer implements EJRWTAppItemRenderer, FocusListener, Serializable, EJRWTItemTextChangeNotifier
{
    private List<ChangeListener>              _changeListeners  = new ArrayList<EJRWTItemTextChangeNotifier.ChangeListener>(1);
    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJScreenItemProperties          _screenItemProperties;
    protected EJItemProperties                _itemProperties;
    protected String                          _registeredItemName;
    protected DateTime                        _textField;
    protected EJRWTAbstractActionDateTime     _actionControl;
    protected Label                           _valueLabel;
    protected Label                           _label;
    protected boolean                         _isValid         = true;
    protected boolean                         _mandatory;

    protected boolean                         _displayValueAsLabel;

    protected boolean                         _lovActivated;

    protected EJCoreVisualAttributeProperties _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties _initialVAProperties;
    protected ControlDecoration               _errorDecoration;
    protected ControlDecoration               _mandatoryDecoration;
    private EJRWTItemRendererVisualContext    _visualContext;

    protected Object                          _baseValue;
    private EJMessage message;
    private EJRWTAbstractLabel labelField;

    protected boolean controlState(Control control)
    {
        return control != null && !control.isDisposed();
    }
    
    
    
    public String getDisplayValue()
    {
       
        return null;
    }

    private Date today()
    {
        Calendar date = _dateFormat.getCalendar();
        date.setTime(new Date());
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTime();
    }

    @Override
    public boolean useFontDimensions()
    {
        return true;
    }

    @Override
    public void refreshItemRendererProperty(String propertyName)
    {
        
        if(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY.equals(propertyName))
        {

            
            if(controlState(_label) && _rendererProps!=null)
            {
                String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

                if (customCSSKey != null && customCSSKey.trim().length() > 0)
                {
                    _label.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                }
                else
                {
                    _label.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_DATETIME);
                }
            }
           
            if(controlState(_textField) && _rendererProps!=null)
            {
                String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);
                
                if (customCSSKey != null && customCSSKey.trim().length() > 0)
                {
                    _textField.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                }
                else
                {
                    _textField.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_DATETIME);
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
            return labelField;
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
        _baseValue = today();
        if (_displayValueAsLabel)
        {
            if (controlState(_valueLabel))
            {
                _valueLabel.setText("");
            }
        }
        setDateTime(_textField, _baseValue);
    }

    private void setDateTime(DateTime dateTime, Object d)
    {
        if (controlState(_textField))
        {
            if (!(d instanceof Date))
            {
                d = today();
            }
            Calendar date = _dateFormat.getCalendar();
            date.setTime((Date) d);
            dateTime.setDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
            dateTime.setTime(date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), date.get(Calendar.SECOND));
           
        }
    }

    private Date getDateTime()
    {
        if (controlState(_textField))
        {
            Calendar date = _dateFormat.getCalendar();
            date.set(Calendar.YEAR, _textField.getYear());
            date.set(Calendar.MONTH, _textField.getMonth());
            date.set(Calendar.DAY_OF_MONTH, _textField.getDay());
            date.set(Calendar.HOUR_OF_DAY, _textField.getHours());
            date.set(Calendar.MINUTE, _textField.getMinutes());
            date.set(Calendar.SECOND, _textField.getSeconds());
            date.set(Calendar.MILLISECOND, 0);
            return date.getTime();
        }
        return today();
    }

    @Override
    public String getRegisteredItemName()
    {
        return _registeredItemName;
    }

    @Override
    public boolean isEditAllowed()
    {
        if (controlState(_textField))
        {
            return _textField.getEnabled();
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
        if (_errorDecoration != null && controlState(_actionControl) && !_errorDecoration.getControl().isDisposed())
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
        if (_errorDecoration != null  && controlState(_actionControl) && !_errorDecoration.getControl().isDisposed())
        {
            ControlDecorationSupport.handleMessage(_errorDecoration, message);
        }
        
    }

    @Override
    public void clearMessage()
    {
        this.message = null;
        if (_errorDecoration != null  && controlState(_actionControl) && !_errorDecoration.getControl().isDisposed())
        {
            _errorDecoration.setDescriptionText("");
            {
                _errorDecoration.hide();
            }
        }
        
    }

    public void valueChanged()
    {
        Object old =_baseValue;
        _item.itemValueChaged(getValue());
        setMandatoryBorder(_mandatory);
    }

    protected void setMandatoryBorder(boolean req)
    {
        if (_displayValueAsLabel || _mandatoryDecoration == null)
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
        _item.itemFocusLost();
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

    public Control createCustomButtonControl(Composite parent)
    {
        return null;
    }

    protected Label newVlaueLabel(Composite composite)
    {
        return new Label(composite, SWT.NONE);
    }

    @Override
    public void createComponent(Composite composite)
    {

        String hint = _screenItemProperties.getHint();

        if (_displayValueAsLabel)
        {
            labelField = new EJRWTAbstractLabel(composite)
            {
                
                @Override
                public Label createLabel(Composite parent)
                {
                    return _valueLabel = newVlaueLabel(parent);
                }
                
                @Override
                public Control createCustomActionLabel(Composite parent)
                {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                @Override
                public Control createActionLabel(Composite parent)
                {
                    // TODO Auto-generated method stub
                    return null;
                }
            };
            _valueLabel.setData(EJ_RWT.CUSTOM_VARIANT,EJ_RWT.CSS_CV_ITEM_DATETIME);
            String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

            if (customCSSKey != null && customCSSKey.trim().length() > 0)
            {
                _valueLabel.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
            }
            _valueLabel.setData(_itemProperties.getName());
            if (hint != null && hint.trim().length() > 0)
            {
                _valueLabel.setToolTipText(hint);
            }
            _visualContext = new EJRWTItemRendererVisualContext(_valueLabel.getBackground(), _valueLabel.getForeground(), _valueLabel.getFont());
            setInitialValue(_baseValue);
        }
        else
        {
            _actionControl = new EJRWTAbstractActionDateTime(composite)
            {
                private static final long serialVersionUID = 2592484612013403481L;

                @Override
                public DateTime createText(Composite parent)
                {
                    int style = SWT.BORDER;
                    String details = _rendererProps.getStringProperty(EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_DETAILS);
                    if (EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_DETAILS_LONG.equals(details))
                    {
                        style = style | SWT.LONG;
                    }
                    else if (EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_DETAILS_MEDIUM.equals(details))
                    {
                        style = style | SWT.MEDIUM;
                    }
                    else if (EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_DETAILS_SHORT.equals(details))
                    {
                        style = style | SWT.SHORT;
                    }
                    String type = _rendererProps.getStringProperty(EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_TYPE);
                    if (EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_TYPE_CALENDAR.equals(type))
                    {
                        style = style | SWT.CALENDAR;
                    }
                    else if (EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_TYPE_DATE.equals(type))
                    {
                        style = style | SWT.DATE;
                    }
                    else if (EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_TYPE_TIME.equals(type))
                    {
                        style = style | SWT.TIME;
                    }

                    if (_rendererProps.getBooleanProperty(EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_DROP_DOWN, false))
                    {
                        style = style | SWT.DROP_DOWN;
                    }

                    _textField = newTextField(parent, style);
                    _textField.setData(EJ_RWT.CUSTOM_VARIANT,EJ_RWT.CSS_CV_ITEM_DATETIME);
                    String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

                    if (customCSSKey != null && customCSSKey.trim().length() > 0)
                    {
                        _textField.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                    }
                    
                    _textField.addSelectionListener(new SelectionAdapter()
                    {
                        @Override
                        public void widgetSelected(SelectionEvent e)
                        {
                            valueChanged();
                        }
                    });
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
                    label.addFocusListener(EJRWTDateTimeItemRenderer.this);
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

                    return label;
                }
            };

            if (hint != null && hint.trim().length() > 0)
            {
                _textField.setToolTipText(hint);
            }

            _visualContext = new EJRWTItemRendererVisualContext(_textField.getBackground(), _textField.getForeground(), _textField.getFont());

            _textField.setData(_item.getReferencedItemProperties().getName());
            _textField.addFocusListener(this);
            String[] keys = new String[] { "BACKSPACE" };
            _textField.setData(EJ_RWT.ACTIVE_KEYS, keys);
            if((_textField.getStyle() & SWT.TIME)!=0)
                _textField.addKeyListener(new KeyListener()
                {
                    
                    @Override
                    public void keyReleased(KeyEvent e)
                    {
                        if (e.keyCode == SWT.BS)
                        {
                            Date value = getValue();
                            if(value!=null)
                            {
                            
                                _textField.setTime(0, 0, 0);
                                valueChanged();
                            }
                        }
                    }
                    
                    @Override
                    public void keyPressed(KeyEvent e)
                    {
                        // TODO Auto-generated method stub
                        
                    }
                });
            
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
            if (message!=null)
            {
                setMessage(message);
            }
            _mandatoryDecoration.hide();
            setInitialValue(_baseValue);
        }
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
        _label.setData(EJ_RWT.CUSTOM_VARIANT,EJ_RWT.CSS_CV_ITEM_DATETIME);
        String customCSSKey = _rendererProps.getStringProperty(EJRWTButtonItemRendererDefinitionProperties.PROPERTY_CSS_KEY);

        if (customCSSKey != null && customCSSKey.trim().length() > 0)
        {
            _label.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
        }
        _label.setText(_screenItemProperties.getLabel() == null ? "" : _screenItemProperties.getLabel());
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

    private DateFormat _dateFormat;

    protected DateTime newTextField(Composite composite, int style)
    {
        _textField = new DateTime(composite, style);

        _textField.addFocusListener(new FocusListener()
        {
            @Override
            public void focusLost(FocusEvent arg0)
            {
                Object value = getValue();
                setDateTime(_textField, value);
            }

            @Override
            public void focusGained(FocusEvent arg0)
            {

            }
        });
        return _textField;
    }

    @Override
    public void setValue(Object value)
    {
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
                setDateTime(_textField, value);
                setMandatoryBorder(_mandatory);
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
        _displayValueAsLabel = _rendererProps.getBooleanProperty(EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_DISPLAY_VAUE_AS_LABEL, false);

        _dateFormat = createDateFormat(_item);
        if (_actionControl != null)
        {
            _actionControl.setCustomActionVisible(isEditAllowed());
        }

    }

    protected static DateFormat createDateFormat(EJScreenItemController item)
    {
        DateFormat dateFormat;
        Locale defaultLocale =  Locale.getDefault();
        

        EJFrameworkExtensionProperties rendererProps = item.getReferencedItemProperties().getItemRendererProperties();
        String format = rendererProps.getStringProperty(EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_DETAILS);
        boolean istime = EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_TYPE_TIME.equals(rendererProps
                .getStringProperty(EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_TYPE));
        if (EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_DETAILS_LONG.equals(format))
        {
            if(istime)
            {
                dateFormat =  new SimpleDateFormat("HH:mm:ss");
            }
            else
            {
                dateFormat = DateFormat.getDateInstance(DateFormat.LONG, defaultLocale);
            }
        }
        else if (EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_DETAILS_MEDIUM.equals(format))
        {
           
            if(istime)
            {
                dateFormat =  new SimpleDateFormat("HH:mm:ss");
            }
            else
            {
                dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, defaultLocale);
            }
        }
        else if (EJRWTDateTimeItemRendererDefinitionProperties.PROPERTY_DETAILS_SHORT.equals(format))
        {
            if(istime)
            {
                dateFormat =  new SimpleDateFormat("HH:mm");
            }
            else
            {
                dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, defaultLocale);
            }
        }
        else
        {
            dateFormat = new SimpleDateFormat();
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

        value = getDateTime();

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

        _baseValue = value;

        return value;
    }

    @Override
    public ColumnLabelProvider createColumnLabelProvider(final EJScreenItemProperties item, EJScreenItemController controller)
    {
        final DateFormat format = createDateFormat(controller);
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
        if (_displayValueAsLabel)
        {
            return;
        }
        if (controlState(_textField))
        {
            _textField.setEnabled(editAllowed);
        }
        setMandatoryBorder(editAllowed && _mandatory);

        if (controlState(_actionControl))
        {
            _actionControl.setActionVisible(isLovActivated() && editAllowed);
        }
    }

}
