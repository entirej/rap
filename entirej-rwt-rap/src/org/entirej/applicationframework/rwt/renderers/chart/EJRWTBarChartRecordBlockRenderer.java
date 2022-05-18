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
package org.entirej.applicationframework.rwt.renderers.chart;

import java.awt.Color;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.rap.chartjs.Axis;
import org.eclipse.rap.chartjs.ChartStyle;
import org.eclipse.rap.chartjs.Ticks;
import org.eclipse.rap.chartjs.bar.BarChart;
import org.eclipse.rap.chartjs.bar.BarChartOptions;
import org.eclipse.rap.chartjs.bar.BarChartRowData;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rwt.EJRWTAsync;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTDeleteAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTInsertAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTQueryAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTUpdateAction;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppBlockRenderer;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.blocks.definition.interfaces.EJRWTSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.blocks.definition.interfaces.EJRWTTreeTableBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.utils.EJRWTKeysUtil;
import org.entirej.applicationframework.rwt.utils.EJRWTKeysUtil.KeyInfo;
import org.entirej.framework.core.EJForm;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.data.controllers.EJItemController;
import org.entirej.framework.core.data.controllers.EJItemLovController;
import org.entirej.framework.core.data.controllers.EJQuestion;
import org.entirej.framework.core.enumerations.EJManagedBlockProperty;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJQuestionButton;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalBlock;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.EJCoreBlockProperties;
import org.entirej.framework.core.properties.EJCoreMainScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.containers.interfaces.EJItemGroupPropertiesContainer;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyList;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;
import org.entirej.framework.core.properties.interfaces.EJBlockProperties;
import org.entirej.framework.core.properties.interfaces.EJItemGroupProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJMainScreenProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.eventhandlers.EJItemFocusListener;
import org.entirej.framework.core.renderers.eventhandlers.EJScreenItemValueChangedListener;
import org.entirej.framework.core.renderers.interfaces.EJInsertScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJItemRenderer;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJUpdateScreenRenderer;
import org.entirej.framework.core.renderers.registry.EJBlockItemRendererRegister;
import org.entirej.framework.core.renderers.registry.EJRendererFactory;

public class EJRWTBarChartRecordBlockRenderer implements EJRWTAppBlockRenderer, KeyListener
{
    final FormToolkit                      toolkit                   = new FormToolkit(Display.getDefault());
    private static final long              serialVersionUID          = -1300484097701416526L;

    private boolean                        _isFocused                = false;
    private EJEditableBlockController      _block;
    private EJRWTEntireJGridPane           _mainPane;
    private BarChart                       _chartView;

    private EJFrameworkExtensionProperties _rendererProp;
    List<String>                           _actionkeys               = new ArrayList<String>();
    private Map<KeyInfo, String>           _actionInfoMap            = new HashMap<EJRWTKeysUtil.KeyInfo, String>();

    private List<EJDataRecord>             _treeBaseRecords          = new ArrayList<EJDataRecord>();

    private final BarChartOptions          options                   = new BarChartOptions();

    public final String                    ANIMATION                 = "animation";
    public final String                    SHOW_TOOLTIPS             = "showToolTips";
    public final String                    SHOW_LEGEND               = "showLegend";
    public final String                    LEGEND_POSITION           = "legendPosition";
    public final String                    X_AXIS_COLUMN             = "xAxisColumn";

    public final String                    POINT_STYLE               = "pointStyle";
    public final String                    LINE_TENSION              = "lineTension";
    public final String                    STEPPED_LINE              = "steppedLine";
    public final String                    SHOW_FILL                 = "showFill";
    public final String                    SHOW_LINE                 = "showLine";
    public final String                    POINT_DOT_RADIUS          = "pointDotRadius";
    public final String                    LINE_WIDTH                = "lineWidth";

    public final String                    BEGIN_AT_ZERO             = "beginAtZero";
    public final String                    MIN                       = "min";
    public final String                    MAX                       = "max";
    public final String                    STEP_SIZE                 = "stepSize";
    public final String                    SUGGESTED_MAX             = "suggestedMax";
    public final String                    SUGGESTED_MIN             = "suggestedMin";
    public final String                    MAX_TICKS_LIMIT           = "maxTicksLimit";

    private String                         xAxisColumn;
    private EJRWTAppItemRenderer           appItemRenderer;
    private boolean                        horizontalBar;
    private EJDataRecord                   currentRec;
    public static final String             VISUAL_ATTRIBUTE_PROPERTY = "VISUAL_ATTRIBUTE";

    public static final String             PROPERTY_FORMAT           = "FORMAT";
    private Display                        dispaly                   = Display.getDefault();

    @Override
    public void setFilter(String filter)
    {

    }

    @Override
    public String getFilter()
    {
        return null;

    }

    @Override
    public void refreshBlockProperty(EJManagedBlockProperty managedBlockPropertyType)
    {
        refresh();
    }

    @Override
    public void refreshBlockRendererProperty(String propertyName)
    {
        refresh();
    }

    @Override
    public void executingQuery()
    {
        // no impl
    }

    @Override
    public void refreshItemProperty(String itemName, EJManagedScreenProperty managedItemPropertyType, EJDataRecord record)
    {
        if (EJManagedScreenProperty.ITEM_INSTANCE_VISUAL_ATTRIBUTE.equals(managedItemPropertyType))
        {
            EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemName);
            if (item != null)
            {
                if (record == null || _chartView == null)
                {
                    return;
                }
                if (_chartView != null && !_chartView.isDisposed())
                {
                    refresh();
                }
            }
        }
        else if (EJManagedScreenProperty.ITEM_INSTANCE_HINT_TEXT.equals(managedItemPropertyType))
        {
            EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemName);
            if (item != null)
            {
                if (record == null)
                {
                    return;
                }
            }
        }
        else if (EJManagedScreenProperty.SCREEN_ITEM_VISUAL_ATTRIBUTE.equals(managedItemPropertyType))
        {
            EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemName);
            if (item != null)
            {
                item.getManagedItemRenderer().getUnmanagedRenderer().setVisualAttribute(item.getProperties().getVisualAttributeProperties());
                refresh();
            }
        }
        else if (EJManagedScreenProperty.VISIBLE.equals(managedItemPropertyType))
        {
            refresh();
        }
    }

    @Override
    public void refreshItemRendererProperty(String itemName, String propertyName)
    {
        refresh();
    }

    @Override
    public Composite getGuiComponent()
    {
        return _mainPane;
    }

    @Override
    public EJQueryScreenRenderer getQueryScreenRenderer()
    {
        return null;
    }

    @Override
    public EJInsertScreenRenderer getInsertScreenRenderer()
    {
        return null;
    }

    @Override
    public EJUpdateScreenRenderer getUpdateScreenRenderer()
    {
        return null;
    }

    @Override
    public void initialiseRenderer(EJEditableBlockController block)
    {
        _block = block;
        EJCoreBlockProperties blockProperties = _block.getProperties();
        options.setAnimation(blockProperties.getBlockRendererProperties().getBooleanProperty(ANIMATION, options.getAnimation()));
        options.setShowToolTips(blockProperties.getBlockRendererProperties().getBooleanProperty(SHOW_TOOLTIPS, options.getShowToolTips()));

        options.getLegend().setEnabled(blockProperties.getBlockRendererProperties().getBooleanProperty(SHOW_LEGEND, options.getLegend().isEnabled()));
        options.getLegend().setPosition(blockProperties.getBlockRendererProperties().getStringProperty(LEGEND_POSITION));
        options.getGridLines().setDisplay(blockProperties.getBlockRendererProperties().getBooleanProperty("gridLines", options.getGridLines().isDisplay()));

        options.setBarPercentage(blockProperties.getBlockRendererProperties().getFloatProperty("barPercentage", options.getBarPercentage()));
        options.setCategoryPercentage(blockProperties.getBlockRendererProperties().getFloatProperty("categoryPercentage", options.getCategoryPercentage()));
        int barThickness = blockProperties.getBlockRendererProperties().getIntProperty("barThickness", 0);
        if (barThickness > 0)
            options.setBarThickness(barThickness);
        int maxBarThickness = blockProperties.getBlockRendererProperties().getIntProperty("maxBarThickness", 0);
        if (maxBarThickness > 0)
            options.setMaxBarThickness(maxBarThickness);

        horizontalBar = blockProperties.getBlockRendererProperties().getBooleanProperty("horizontalBar", false);
        xAxisColumn = blockProperties.getBlockRendererProperties().getStringProperty(X_AXIS_COLUMN);

        EJItemGroupPropertiesContainer container = blockProperties.getScreenItemGroupContainer(EJScreenType.MAIN);
        Collection<EJItemGroupProperties> itemGroupProperties = container.getAllItemGroupProperties();
        options.getYAxes().clear();
        options.getxAxes().clear();
        for (EJItemGroupProperties g : itemGroupProperties)
        {
            Axis axis = new Axis();

            if (horizontalBar)
                options.getxAxes().add(axis);
            else
                options.getYAxes().add(axis);
            if (g.getRendererProperties() == null)
            {
                continue;
            }

            Ticks ticks = axis.getTicks();
            if (!g.getRendererProperties().isPropertyValueNull(BEGIN_AT_ZERO))
                ticks.setBeginAtZero(g.getRendererProperties().getBooleanProperty(BEGIN_AT_ZERO, false));
            if (!g.getRendererProperties().isPropertyValueNull(MIN))
                ticks.setMin(Double.valueOf(g.getRendererProperties().getFloatProperty(MIN, 0f)));
            if (!g.getRendererProperties().isPropertyValueNull(MAX))
                ticks.setMax(Double.valueOf(g.getRendererProperties().getFloatProperty(MAX, 0f)));
            if (!g.getRendererProperties().isPropertyValueNull(SUGGESTED_MAX))
                ticks.setSuggestedMax(Double.valueOf(g.getRendererProperties().getFloatProperty(SUGGESTED_MAX, 0f)));
            if (!g.getRendererProperties().isPropertyValueNull(SUGGESTED_MIN))
                ticks.setSuggestedMin(Double.valueOf(g.getRendererProperties().getFloatProperty(SUGGESTED_MIN, 0f)));

            if (!g.getRendererProperties().isPropertyValueNull(STEP_SIZE))
                ticks.setStepSize(Double.valueOf(g.getRendererProperties().getFloatProperty(STEP_SIZE, 0f)));

            ticks.setMaxTicksLimit((g.getRendererProperties().getIntProperty(MAX_TICKS_LIMIT, ticks.getMaxTicksLimit())));

        }

    }

    @Override
    public void blockCleared()
    {
        EJRWTAsync.runUISafe(dispaly,() -> {

            if (_chartView != null && !_chartView.isDisposed())
            {
                _chartView.clear();
            }
        });
    }

    @Override
    public void synchronize()
    {
    }

    @Override
    public void detailBlocksCleared()
    {
    }

    @Override
    public boolean hasFocus()
    {
        return _isFocused;
    }

    public boolean isInsertMode()
    {
        return false;
    }

    public boolean isUpdateMode()
    {
        return false;
    }

    @Override
    public boolean isCurrentRecordDirty()
    {
        return false;
    }

    @Override
    public void askToDeleteRecord(final EJDataRecord recordToDelete, String msg)
    {
        if (msg == null)
        {
            msg = "Are you sure you want to delete the current record?";
        }
        EJMessage message = new EJMessage(msg);
        EJQuestion question = new EJQuestion(new EJForm(_block.getForm()), "DELETE_RECORD", "Delete", message, "Yes", "No")
        {

            @Override
            public void setAnswer(EJQuestionButton answerButton)
            {

                super.setAnswer(answerButton);

                if (EJQuestionButton.ONE == answerButton)
                {
                    _block.getBlock().deleteRecord(recordToDelete);
                }
                _block.setRendererFocus(true);
            }

        };
        _block.getForm().getMessenger().askQuestion(question);

    }

    @Override
    public void enterInsert(EJDataRecord record)
    {
        if (_block.getInsertScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define an Insert Screen Renderer for this form before an insert operation can be performed.");
            _block.getForm().getMessenger().handleMessage(message);
        }
        else
        {
            EJDataRecord focusedRecord = getFocusedRecord();
            if (focusedRecord != null)
            {
                final String pid = _rendererProp.getStringProperty(EJRWTTreeTableBlockDefinitionProperties.PARENT_ITEM);
                final String rid = _rendererProp.getStringProperty(EJRWTTreeTableBlockDefinitionProperties.RELATION_ITEM);
                record.setValue(rid, focusedRecord.getValue(pid));
            }
            _block.getInsertScreenRenderer().open(record);
        }
    }

    @Override
    public void enterQuery(EJDataRecord queryRecord)
    {

    }

    @Override
    public void enterUpdate(EJDataRecord recordToUpdate)
    {

    }

    @Override
    public void queryExecuted()
    {

        refresh();
    }

    public void pageRetrieved()
    {
        refresh();
    }

    @Override
    public void recordDeleted(int dataBlockRecordNumber)
    {
       refresh();
    }

    public void refresh()
    {
        EJRWTAsync.runUISafe(dispaly,() -> {

            refresh(new Object());
        });

    }

    List<EJScreenItemController> getScreenItems()
    {
        Collection<EJItemGroupProperties> allItemGroupProperties = _block.getProperties().getScreenItemGroupContainer(EJScreenType.MAIN).getAllItemGroupProperties();

        List<EJScreenItemController> list = new ArrayList<EJScreenItemController>();
        for (EJItemGroupProperties groupProperties : allItemGroupProperties)
        {
            Collection<EJScreenItemProperties> itemProperties = groupProperties.getAllItemProperties();
            for (EJScreenItemProperties screenItemProperties : itemProperties)
            {
                EJCoreMainScreenItemProperties mainScreenItemProperties = (EJCoreMainScreenItemProperties) screenItemProperties;

                EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, mainScreenItemProperties.getReferencedItemName());

                list.add(item);
            }
        }

        return list;
    }
    
    private String getToolTipValue(Object object)
    {
        String xvalue;
        if (object instanceof String)
        {
            xvalue = ((String) object);
        }
        else if (object instanceof Number)
        {
            
            xvalue = (createDecimalFormat(object, null).format(object));
        }
        else if (object instanceof Date)
        {
            
            xvalue = (DateFormat.getDateInstance(DateFormat.SHORT, _block.getForm().getFrameworkManager().getCurrentLocale()).format((Date) object));
        }
        else
        {
            xvalue = (object.toString());
        }
        return xvalue;
    }

    public void refresh(Object input)
    {
        if (_chartView != null && !_chartView.isDisposed())
        {
            if (xAxisColumn == null && xAxisColumn.isEmpty())
            {

                return;
            }

            Map<Object, Map<String, Float>> dataset = new HashMap<Object, Map<String, Float>>();
            Map<Object, Map<String, EJCoreVisualAttributeProperties>> datasetVa = new HashMap<Object, Map<String, EJCoreVisualAttributeProperties>>();

            Collection<EJDataRecord> records = _block.getRecords();

            Collection<EJScreenItemController> screenItems = getScreenItems();

            List<Object> labelsIndex = new ArrayList<Object>();
            Map<String, Number> lastVal = new HashMap<String, Number>();
            for (EJDataRecord ejDataRecord : records)
            {
                Object xobject = ejDataRecord.getValue(xAxisColumn);
                if (xobject != null)
                {

                    Map<String, Float> set = dataset.get(xobject);
                    Map<String, EJCoreVisualAttributeProperties> setVa = datasetVa.get(xobject);
                    if (set == null)
                    {
                        set = new HashMap<String, Float>();
                        setVa = new HashMap<String, EJCoreVisualAttributeProperties>();
                        dataset.put(xobject, set);
                        datasetVa.put(xobject, setVa);
                        labelsIndex.add(xobject);
                    }
                    for (EJScreenItemController sItem : screenItems)
                    {
                        if (!sItem.isSpacerItem())
                        {
                            Object yvalue = ejDataRecord.getValue(sItem.getName());

                            Float val = null;
                            if (yvalue instanceof String)
                            {
                                yvalue = new BigDecimal((String)yvalue);

                            }
                            if (yvalue instanceof Number)
                            {
                                lastVal.put(sItem.getName(), (Number) yvalue);
                                val = ((Number) yvalue).floatValue();

                            }
                            else
                            {
                                Number last = lastVal.get(sItem.getName());
                                val = last != null ? last.floatValue() : 0f;

                            }
                            set.put(sItem.getName(), val);
                            EJCoreVisualAttributeProperties visualAttribute = ejDataRecord.getItem(sItem.getName()).getVisualAttribute();
                            if (visualAttribute == null)
                                visualAttribute = sItem.getItemRenderer().getVisualAttributeProperties();
                            setVa.put(sItem.getName(), visualAttribute);
                        }
                    }

                }
            }

            List<String> xlabel = new ArrayList<String>(labelsIndex.size());
            for (Object object : labelsIndex)
            {
                String xvalue;
                xvalue = getStrValue(object);
                xlabel.add(xvalue);

            }

            BarChartRowData chartRowData = new BarChartRowData(xlabel.toArray(new String[0]));

            for (EJScreenItemController sItem : screenItems)
            {
                if (sItem.isSpacerItem())
                    continue;
                List<Float> row = new ArrayList<Float>();
                List<EJCoreVisualAttributeProperties> rowVa = new ArrayList<EJCoreVisualAttributeProperties>();

                for (Object object : labelsIndex)
                {
                    Map<String, Float> map = dataset.get(object);
                    Map<String, EJCoreVisualAttributeProperties> mapVa = datasetVa.get(object);
                    if (map == null || mapVa == null)
                        continue;

                    row.add(map.get(sItem.getName()));
                    rowVa.add(mapVa.get(sItem.getName()));

                }

                float[] floatArray = new float[row.size()];
                String[] floatArrayTips = new String[row.size()];
                ChartStyle[] styleArray = new ChartStyle[row.size()];
                int i = 0;

                for (Float f : row)
                {
                    floatArray[i] = (f != null ? f : 0);
                    floatArrayTips[i] = getToolTipValue(f != null ? f : 0);

                    ChartStyle colors = new ChartStyle(220, 220, 220, 0.8f);

                    EJCoreVisualAttributeProperties attributeProperties = rowVa.get(i);
                    if (attributeProperties != null)
                    {
                        if (attributeProperties.getForegroundColor() != null)
                        {
                            Color color = attributeProperties.getForegroundColor();
                            colors = new ChartStyle(color.getRed(), color.getGreen(), color.getBlue(), 0.8f);
                        }
                        if (attributeProperties.getBackgroundColor() != null)
                        {
                            Color color = attributeProperties.getBackgroundColor();
                            colors.setFillColor(new RGB(color.getRed(), color.getGreen(), color.getBlue()));

                        }
                    }
                    styleArray[i] = colors;
                    i++;

                }
                /*
                 * String pointStyle = "circle";
                 * 
                 * ChartStyle chartStyle;
                 */
                BarChartRowData.RowInfo info = new BarChartRowData.RowInfo();
                String label = sItem.getProperties().getLabel();
                if (label.trim().isEmpty())
                {
                    label = sItem.getProperties().getReferencedItemName();
                }
                info.setLabel(label);

                EJCoreMainScreenItemProperties mainScreenItemProperties = (EJCoreMainScreenItemProperties) sItem.getProperties();
                String action = mainScreenItemProperties.getBlockRendererRequiredProperties().getStringProperty("action");
                if (action == null || action.trim().isEmpty())
                {
                    action = "select";
                }

                ChartStyle colors = new ChartStyle(220, 220, 220, 0.8f);

                EJCoreVisualAttributeProperties attributeProperties = sItem.getItemRenderer().getVisualAttributeProperties();

                if (attributeProperties != null)
                {
                    if (attributeProperties.getForegroundColor() != null)
                    {
                        Color color = attributeProperties.getForegroundColor();
                        colors = new ChartStyle(color.getRed(), color.getGreen(), color.getBlue(), 0.8f);
                    }
                    if (attributeProperties.getBackgroundColor() != null)
                    {
                        Color color = attributeProperties.getBackgroundColor();
                        colors.setFillColor(new RGB(color.getRed(), color.getGreen(), color.getBlue()));

                    }
                }

                info.setAction(action);
                info.setChartStyle(colors);
                info.setHidden(!sItem.isVisible());
                chartRowData.addRow(info, floatArray,floatArrayTips, styleArray);
                // chartRowData.addRow(floatArray, colors);
            }

            if (horizontalBar)
                _chartView.loadHorizontal(chartRowData, options);
            else
                _chartView.load(chartRowData, options);

        }
    }

    private String getStrValue(Object object)
    {
        String xvalue;
        if (appItemRenderer != null)
        {
            String formatValue = appItemRenderer.formatValue(object);
            xvalue = (formatValue != null ? formatValue : object.toString());
        }
        else if (object instanceof String)
        {
            xvalue = ((String) object);
        }
        else if (object instanceof Number)
        {

            xvalue = (createDecimalFormat(object, null).format(object));
        }
        else if (object instanceof Date)
        {

            xvalue = (DateFormat.getDateInstance(DateFormat.SHORT, _block.getForm().getFrameworkManager().getCurrentLocale()).format((Date) object));
        }
        else
        {
            xvalue = (object.toString());
        }
        return xvalue;
    }

    DecimalFormat createDecimalFormat(Object obj, String format)
    {
        DecimalFormat _decimalFormatter = null;

        Locale defaultLocale = _block.getForm().getFrameworkManager().getCurrentLocale();
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
            if ((obj.getClass().equals(Integer.class)) || (obj.getClass().equals(Long.class)))

            {
                _decimalFormatter.setGroupingUsed(true);
                _decimalFormatter.setParseIntegerOnly(true);
                _decimalFormatter.setParseBigDecimal(false);
            }

            else
            {

                char seperator = dfs.getDecimalSeparator();
                if (format.indexOf(seperator) != -1)
                {
                    _decimalFormatter.setGroupingUsed(true);
                }
                _decimalFormatter.setParseIntegerOnly(false);
                _decimalFormatter.setParseBigDecimal(true);
            }

        }
        return _decimalFormatter;
    }

    @Override
    public void recordInserted(EJDataRecord record)
    {
        refresh();
    }

    @Override
    public void refreshAfterChange(EJDataRecord record)
    {

        refresh();

    }

    @Override
    public void recordSelected(EJDataRecord record)
    {
        currentRec = record;
    }

    @Override
    public void setHasFocus(boolean focus)
    {
        _isFocused = focus;
        if (_isFocused)
        {
            showFocusedBorder(true);
            _block.focusGained();
        }
        else
        {
            showFocusedBorder(false);
            _block.focusLost();
        }
    }

    /**
     * Enables a blue border around this controller. This will indicate that the
     * container held by this controller has cursor focus.
     * 
     * @param pFocused
     *            If <code>true</code> is passed then the border will be
     *            displayed, if <code>false</code> is passed then no border will
     *            be shown.
     */
    protected void showFocusedBorder(boolean focused)
    {
    }

    @Override
    public void setFocusToItem(EJScreenItemController item)
    {
        if (_chartView != null && !_chartView.isDisposed())
        {
            _chartView.forceFocus();
        }
    }

    @Override
    public void gainFocus()
    {
        if (_chartView != null && !_chartView.isDisposed())
        {
            _chartView.forceFocus();
        }
        setHasFocus(true);

    }

    @Override
    public EJDataRecord getFocusedRecord()
    {

        return currentRec != null ? currentRec : getFirstRecord();
    }

    @Override
    public int getDisplayedRecordNumber(EJDataRecord record)
    {
        return _treeBaseRecords.indexOf(record);
    }

    @Override
    public int getDisplayedRecordCount()
    {
        // Indicates the number of records that are available within the View.
        // the number depends on the filters set on the table!
        return _treeBaseRecords.size();
    }

    @Override
    public EJDataRecord getFirstRecord()
    {
        return getRecordAt(0);
    }

    @Override
    public EJDataRecord getLastRecord()
    {
        return getRecordAt(getDisplayedRecordCount() - 1);
    }

    @Override
    public EJDataRecord getRecordAt(int displayedRecordNumber)
    {
        if (displayedRecordNumber > -1 && displayedRecordNumber < getDisplayedRecordCount())
        {
            return _treeBaseRecords.get(displayedRecordNumber);
        }

        return null;
    }

    @Override
    public EJDataRecord getRecordAfter(EJDataRecord record)
    {
        int viewIndex = getDisplayedRecordNumber(record);
        if (-1 < viewIndex)
        {
            return getRecordAt(viewIndex + 1);
        }
        return null;
    }

    @Override
    public EJDataRecord getRecordBefore(EJDataRecord record)
    {
        int viewIndex = getDisplayedRecordNumber(record);
        if (-1 < viewIndex)
        {
            return getRecordAt(viewIndex - 1);
        }
        return null;
    }

    public final EJInternalEditableBlock getBlock()
    {
        return _block.getBlock();
    }

    @Override
    public void buildGuiComponent(EJRWTEntireJGridPane blockCanvas)
    {
        EJFrameworkExtensionProperties appProp = EJCoreProperties.getInstance().getApplicationDefinedProperties();
        if (appProp != null)
        {
            EJFrameworkExtensionProperties propertyGroup = appProp.getPropertyGroup(EJRWTSingleRecordBlockDefinitionProperties.ACTION_GROUP);
            if (propertyGroup != null)
            {

                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_QUERY_KEY), EJRWTSingleRecordBlockDefinitionProperties.ACTION_QUERY_KEY);
                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_INSERT_KEY), EJRWTSingleRecordBlockDefinitionProperties.ACTION_INSERT_KEY);
                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_UPDATE_KEY), EJRWTSingleRecordBlockDefinitionProperties.ACTION_UPDATE_KEY);
                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_DELETE_KEY), EJRWTSingleRecordBlockDefinitionProperties.ACTION_DELETE_KEY);
                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_REFRESH_KEY), EJRWTSingleRecordBlockDefinitionProperties.ACTION_REFRESH_KEY);

            }
        }
        EJBlockProperties blockProperties = _block.getProperties();
        EJMainScreenProperties mainScreenProperties = blockProperties.getMainScreenProperties();

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = mainScreenProperties.getWidth();
        gridData.heightHint = mainScreenProperties.getHeight();

        gridData.horizontalSpan = mainScreenProperties.getHorizontalSpan();
        gridData.verticalSpan = mainScreenProperties.getVerticalSpan();
        gridData.grabExcessHorizontalSpace = mainScreenProperties.canExpandHorizontally();
        gridData.grabExcessVerticalSpace = mainScreenProperties.canExpandVertically();

        if (gridData.grabExcessHorizontalSpace)
        {
            gridData.minimumWidth = mainScreenProperties.getWidth();
        }
        if (gridData.grabExcessVerticalSpace)
        {
            gridData.minimumHeight = mainScreenProperties.getHeight();
        }
        _rendererProp = blockProperties.getBlockRendererProperties();
        blockCanvas.setLayoutData(gridData);

        EJFrameworkExtensionProperties sectionProperties = null;
        if (_rendererProp != null)
        {
            sectionProperties = _rendererProp.getPropertyGroup(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR);
        }
        if (sectionProperties != null && sectionProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE) != null
                && !EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_GROUP.equals(sectionProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE)))
        {
            int style = ExpandableComposite.TITLE_BAR;

            String mode = sectionProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE);
            if (EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_TWISTIE.equals(mode))
            {
                style = style | ExpandableComposite.TWISTIE;
            }
            else if (EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_TREE_NODE.equals(mode))
            {
                style = style | ExpandableComposite.TREE_NODE;
            }
            if (sectionProperties.getBooleanProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDED, true))
            {
                style = style | ExpandableComposite.EXPANDED;
            }
            Section section = toolkit.createSection(blockCanvas, style);
            section.setLayoutData(gridData);
            String title = sectionProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_TITLE);
            if (title != null)
            {
                section.setText(title);
            }
            EJRWTImageRetriever.getGraphicsProvider().rendererSection(section);
            String frameTitle = mainScreenProperties.getFrameTitle();
            if (mainScreenProperties.getDisplayFrame() && frameTitle != null && frameTitle.length() > 0)
            {
                Group group = new Group(section, SWT.NONE);
                group.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
                group.setLayout(new FillLayout());
                group.setLayoutData(gridData);
                hookKeyListener(group);

                group.setText(frameTitle);

                _mainPane = new EJRWTEntireJGridPane(group, 1, mainScreenProperties.getDisplayFrame() ? SWT.BORDER : SWT.NONE);
                _mainPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
                section.setClient(group);

            }
            else
            {
                _mainPane = new EJRWTEntireJGridPane(section, 1);
                _mainPane.setLayoutData(gridData);
                _mainPane.cleanLayoutHorizontal();
                _mainPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
                section.setClient(_mainPane);
            }

            final EJFrameworkExtensionPropertyList propertyList = sectionProperties.getPropertyList(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTIONS);

            if (propertyList != null && propertyList.getAllListEntries().size() > 0)
            {
                ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
                final ToolBar toolbar = toolBarManager.createControl(section);
                final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
                toolbar.setCursor(handCursor);
                // Cursor needs to be explicitly disposed
                toolbar.addDisposeListener(new DisposeListener()
                {
                    @Override
                    public void widgetDisposed(DisposeEvent e)
                    {
                        if (handCursor != null && handCursor.isDisposed() == false)
                        {
                            handCursor.dispose();
                        }
                    }
                });
                List<EJFrameworkExtensionPropertyListEntry> allListEntries = propertyList.getAllListEntries();
                for (EJFrameworkExtensionPropertyListEntry entry : allListEntries)
                {
                    final String actionID = entry.getProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTION_ID);
                    String actionImage = entry.getProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTION_IMAGE);
                    String actionName = entry.getProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTION_NAME);
                    String actionTooltip = entry.getProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTION_TOOLTIP);

                    if (actionID != null)
                    {
                        Action action = new Action(actionID, IAction.AS_PUSH_BUTTON)
                        {
                            @Override
                            public void runWithEvent(Event event)
                            {
                                _block.executeActionCommand(actionID, EJScreenType.MAIN);
                            }
                        };
                        if (actionName != null)
                        {
                            action.setText(actionName);
                        }
                        if (actionTooltip != null)
                        {
                            action.setDescription(actionTooltip);
                        }
                        if (actionImage != null && actionImage.length() > 0)
                        {
                            action.setImageDescriptor((EJRWTImageRetriever.createDescriptor(actionImage)));
                        }
                        toolBarManager.add(action);
                    }
                }

                toolBarManager.update(true);
                section.setTextClient(toolbar);
            }
        }
        else
        {
            String frameTitle = mainScreenProperties.getFrameTitle();
            if (mainScreenProperties.getDisplayFrame() && frameTitle != null && frameTitle.length() > 0)
            {
                Group group = new Group(blockCanvas, SWT.NONE);
                group.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
                group.setLayout(new FillLayout());
                group.setLayoutData(gridData);
                hookKeyListener(group);

                group.setText(frameTitle);

                _mainPane = new EJRWTEntireJGridPane(group, 1, mainScreenProperties.getDisplayFrame() ? SWT.BORDER : SWT.NONE);
                _mainPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
            }
            else
            {
                _mainPane = new EJRWTEntireJGridPane(blockCanvas, 1);
                _mainPane.setLayoutData(gridData);
                _mainPane.cleanLayout();
                _mainPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
            }
        }

        hookKeyListener(_mainPane);
        int style = SWT.NONE;

        Collection<EJItemGroupProperties> allItemGroupProperties = _block.getProperties().getScreenItemGroupContainer(EJScreenType.MAIN).getAllItemGroupProperties();

        {
            _chartView = null;
            if (allItemGroupProperties.size() > 0)
            {
                EJItemGroupProperties displayProperties = allItemGroupProperties.iterator().next();
                if (displayProperties.dispayGroupFrame() && displayProperties.getFrameTitle() != null && displayProperties.getFrameTitle().length() > 0)
                {
                    Group group = new Group(_mainPane, SWT.NONE);
                    group.setLayout(new FillLayout());
                    group.setText(displayProperties.getFrameTitle());

                    group.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                    _chartView = new BarChart(group, style)
                    {

                        protected void action(String method, org.eclipse.rap.json.JsonObject parameters)
                        {

                            processAction(method, parameters);
                        }
                    };
                }
                else
                {
                    _chartView = new BarChart(_mainPane, displayProperties.dispayGroupFrame() ? style | SWT.BORDER : style)
                    {

                        protected void action(String method, org.eclipse.rap.json.JsonObject parameters)
                        {

                            processAction(method, parameters);
                        }
                    };

                    _chartView.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                }
            }
            else
            {
                _chartView = new BarChart(_mainPane, style)
                {

                    protected void action(String method, org.eclipse.rap.json.JsonObject parameters)
                    {

                        processAction(method, parameters);
                    }
                };

                _chartView.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
            }
        }

        _chartView.addFocusListener(new FocusListener()
        {
            @Override
            public void focusLost(FocusEvent arg0)
            {
                setHasFocus(false);
            }

            @Override
            public void focusGained(FocusEvent arg0)
            {
                setHasFocus(true);
            }
        });

        _mainPane.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                if (!_chartView.isFocusControl())
                {
                    setHasFocus(true);
                }
            }
        });
        _chartView.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                if (!_chartView.isFocusControl())
                {
                    setHasFocus(true);
                }
            }

        });

        for (EJItemGroupProperties groupProperties : allItemGroupProperties)
        {
            Collection<EJScreenItemProperties> itemProperties = groupProperties.getAllItemProperties();
            for (EJScreenItemProperties screenItemProperties : itemProperties)
            {
                EJCoreMainScreenItemProperties mainScreenItemProperties = (EJCoreMainScreenItemProperties) screenItemProperties;

                String visualAttribute = mainScreenItemProperties.getBlockRendererRequiredProperties().getStringProperty(VISUAL_ATTRIBUTE_PROPERTY);
                EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, mainScreenItemProperties.getReferencedItemName());

                EJRWTAppItemRenderer itemRenderer = (EJRWTAppItemRenderer) item.getManagedItemRenderer().getUnmanagedRenderer();
                if (visualAttribute != null)
                {
                    EJCoreVisualAttributeProperties va = EJCoreProperties.getInstance().getVisualAttributesContainer().getVisualAttributeProperties(visualAttribute);
                    if (va != null)
                    {
                        itemRenderer.setInitialVisualAttribute(va);
                    }
                }

                if (item.getProperties().getVisualAttributeProperties() != null)
                {
                    itemRenderer.setVisualAttribute(item.getProperties().getVisualAttributeProperties());
                }

            }
        }
        appItemRenderer = null;
        final EJItemController blockItemController = _block.getBlockItemController(xAxisColumn);

        String itemRendererName = blockItemController.getProperties().getItemRendererName();
        if (itemRendererName != null)
        {
            EJScreenItemController itemController = new EJScreenItemController()
            {

                @Override
                public boolean validateFromLov()
                {
                    // TODO Auto-generated method stub
                    return false;
                }

                @Override
                public void setItemLovMapping(String lovMapping)
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void removeItemValueChangedListener(EJScreenItemValueChangedListener listener)
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void removeItemFocusListener(EJItemFocusListener listener)
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void itemValueChaged(Object newValue)
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void itemFocusLost()
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void itemFocusGained()
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public boolean isVisible()
                {
                    // TODO Auto-generated method stub
                    return false;
                }

                @Override
                public boolean isSpacerItem()
                {
                    // TODO Auto-generated method stub
                    return false;
                }

                @Override
                public void initialiseRenderer()
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void initialise(EJBlockItemRendererRegister blockItemRegister)
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public EJScreenType getScreenType()
                {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public EJItemProperties getReferencedItemProperties()
                {
                    return blockItemController.getProperties();
                }

                @Override
                public EJScreenItemProperties getProperties()
                {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public String getName()
                {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public EJManagedItemRendererWrapper getManagedItemRenderer()
                {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public EJBlockItemRendererRegister getItemRendererRegister()
                {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public EJItemRenderer getItemRenderer()
                {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public EJItemLovController getItemLovController()
                {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public EJInternalForm getForm()
                {
                    // TODO Auto-generated method stub
                    return _block.getForm();
                }

                @Override
                public EJInternalBlock getBlock()
                {
                    return _block.getBlock();
                }

                @Override
                public void gainFocus()
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void executeActionCommand()
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void addItemValueChangedListener(EJScreenItemValueChangedListener listener)
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void addItemFocusListener(EJItemFocusListener listener)
                {
                    // TODO Auto-generated method stub

                }
            };
            EJManagedItemRendererWrapper renderer = EJRendererFactory.getInstance().getItemRenderer(itemController, new EJCoreMainScreenItemProperties(_block.getProperties(), false));

            if (renderer != null && renderer.getUnmanagedRenderer() instanceof EJRWTAppItemRenderer)
            {
                appItemRenderer = (EJRWTAppItemRenderer) renderer.getUnmanagedRenderer();
            }
        }

        refresh();
    }

    protected void processAction(String method, JsonObject parameters)
    {
        if (parameters.names().contains("data_label") && parameters.names().contains("value"))
        {
            currentRec = null;
            String dataLbl = parameters.get("data_label").asString();
            double pointValue = parameters.get("value").asDouble();
            String lbl = parameters.get("label").asString();
            currentRec = getRecord(dataLbl, pointValue, lbl);

        }
        if (currentRec != null)
        {
            _block.newRecordInstance(currentRec);
        }
        if (!method.equals("select"))
        {
            _block.executeActionCommand(method, EJScreenType.MAIN);
        }

    }

    private EJDataRecord getRecord(String dataLbl, double pointValue, String lbl)
    {
        EJScreenItemController dataItem = null;
        List<EJScreenItemController> screenItems = getScreenItems();
        for (EJScreenItemController sItem : screenItems)
        {
            String label = sItem.getProperties().getLabel();
            if (label.trim().isEmpty())
            {
                label = sItem.getProperties().getReferencedItemName();
            }

            if (label.equals(dataLbl))
            {
                dataItem = sItem;
                break;
            }
        }
        if (dataItem != null)
        {
            Collection<EJDataRecord> records = _block.getRecords();
            Object lastVal = null;
            for (EJDataRecord record : records)
            {
                Object value = record.getValue(dataItem.getName());
                if (value == null)
                    value = lastVal;

                if (value instanceof BigDecimal)
                {
                    value = ((BigDecimal) value).doubleValue();
                }

                if (record.getValue(xAxisColumn) != null && getStrValue(record.getValue(xAxisColumn)).equals(lbl) && value != null && value.equals(pointValue))
                {
                    return record;
                }
                lastVal = value;

            }
        }

        return null;
    }

    private void addActionKeyinfo(String actionKey, String actionId)
    {
        if (actionKey != null && actionKey.trim().length() > 0)
        {
            try
            {
                KeyInfo keyInfo = EJRWTKeysUtil.toKeyInfo(actionKey);
                _actionInfoMap.put(keyInfo, actionId);
                _actionkeys.add(actionKey);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent arg0)
    {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent arg0)
    {
        int keyCode = arg0.keyCode;
        KeyInfo keyInfo = EJRWTKeysUtil.toKeyInfo(keyCode, (arg0.stateMask & SWT.SHIFT) != 0, (arg0.stateMask & SWT.CTRL) != 0, (arg0.stateMask & SWT.ALT) != 0);

        String actionID = _actionInfoMap.get(keyInfo);
        if (actionID != null)
        {
            if (EJRWTSingleRecordBlockDefinitionProperties.ACTION_QUERY_KEY.equals(actionID))
            {
                if (EJRWTQueryAction.canExecute(_block))
                {
                    _block.enterQuery();
                    gainFocus();
                }

            }
            else if (EJRWTSingleRecordBlockDefinitionProperties.ACTION_INSERT_KEY.equals(actionID))
            {
                if (EJRWTInsertAction.canExecute(_block))
                {
                    _block.enterInsert(false);
                    gainFocus();
                }
            }
            else if (EJRWTSingleRecordBlockDefinitionProperties.ACTION_UPDATE_KEY.equals(actionID))
            {
                if (EJRWTUpdateAction.canExecute(_block))
                {
                    _block.enterUpdate();
                    gainFocus();
                }
            }
            else if (EJRWTSingleRecordBlockDefinitionProperties.ACTION_DELETE_KEY.equals(actionID))
            {
                if (EJRWTDeleteAction.canExecute(_block))
                {
                    _block.askToDeleteCurrentRecord(null);
                    gainFocus();
                }
            }
            else if (EJRWTSingleRecordBlockDefinitionProperties.ACTION_REFRESH_KEY.equals(actionID))
            {
                _block.getBlock().refreshAfterChange(getFocusedRecord());
                gainFocus();

            }
        }
    }

    private void hookKeyListener(Control control)
    {
        List<String> subActions = new ArrayList<String>(_actionkeys);
        Object data = control.getData(EJ_RWT.ACTIVE_KEYS);

        if (data != null)
        {
            String[] current = (String[]) data;
            for (String action : current)
            {
                if (subActions.contains(action))
                {
                    continue;
                }
                subActions.add(action);
            }
        }
        control.setData(EJ_RWT.ACTIVE_KEYS, subActions.toArray(new String[0]));
        control.addKeyListener(this);
    }

}
