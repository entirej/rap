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
import org.eclipse.rap.chartjs.ChartStyle;
import org.eclipse.rap.chartjs.radar.RadarChart;
import org.eclipse.rap.chartjs.radar.RadarChartOptions;
import org.eclipse.rap.chartjs.radar.RadarChartRowData;
import org.eclipse.rap.chartjs.radar.RadarChartRowData.RowInfo;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
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
import org.entirej.applicationframework.rwt.renderers.html.EJRWTHtmlTableBlockRenderer;
import org.entirej.applicationframework.rwt.utils.EJRWTKeysUtil;
import org.entirej.applicationframework.rwt.utils.EJRWTKeysUtil.KeyInfo;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.EJForm;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.data.controllers.EJQuestion;
import org.entirej.framework.core.enumerations.EJManagedBlockProperty;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJQuestionButton;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.properties.EJCoreBlockProperties;
import org.entirej.framework.core.properties.EJCoreMainScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyList;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;
import org.entirej.framework.core.properties.interfaces.EJBlockProperties;
import org.entirej.framework.core.properties.interfaces.EJItemGroupProperties;
import org.entirej.framework.core.properties.interfaces.EJMainScreenProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.interfaces.EJInsertScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJUpdateScreenRenderer;

public class EJRWTRadarChartRecordBlockRenderer implements EJRWTAppBlockRenderer, KeyListener
{
    final FormToolkit                      toolkit                   = new FormToolkit(Display.getDefault());
    private static final long              serialVersionUID          = -1300484097701416526L;

    private boolean                        _isFocused                = false;
    private EJEditableBlockController      _block;
    private EJRWTEntireJGridPane           _mainPane;
    private RadarChart                     _chartView;

    private EJFrameworkExtensionProperties _rendererProp;
    List<String>                           _actionkeys               = new ArrayList<String>();
    private Map<KeyInfo, String>           _actionInfoMap            = new HashMap<EJRWTKeysUtil.KeyInfo, String>();

    private List<EJDataRecord>             _treeBaseRecords          = new ArrayList<EJDataRecord>();

    private final RadarChartOptions        options                   = new RadarChartOptions();

    public final String                    ANIMATION                 = "animation";
    public final String                    SHOW_TOOLTIPS             = "showToolTips";
    public final String                    SHOW_LEGEND               = "showLegend";
    public final String                    LEGEND_POSITION           = "legendPosition";
    public final String                    SCALE_MIN                  = "min";
    public final String                    SCALE_MAX                  = "max";
    public final String                    SCALE_STEP                 = "stepSize";

    public final String                    LBL_VIEW_TYPE             = "lblViewType";
    public final String                    LBL_VIEW_POS              = "lblViewPos";
    public final String                    LBL_VIEW_ARC              = "lblViewArc";
    public final String                    LBL_VIEW_VA               = "lblViewVA";

    public final String                    LABLE_COLUMN              = "lblColumn";

    public final String                    LINE_WIDTH                = "lineWidth";
    public final String                    FILLBG                    = "fillbg";

    private String                         labelColumn;
    private EJDataRecord                   currentRecord;
    public static final String             VISUAL_ATTRIBUTE_PROPERTY = "VISUAL_ATTRIBUTE";

    public static final String             PROPERTY_FORMAT           = "FORMAT";
    private Display                        dispaly                   = Display.getDefault();
    private boolean                        fillBG;

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
        
        {

            EJFrameworkExtensionProperties propertyGroup = blockProperties.getBlockRendererProperties().getPropertyGroup("SCALE");
            
            if(propertyGroup!=null) {
            float scaleMin = propertyGroup.getFloatProperty(SCALE_MIN, -1f);
            float scaleMax =  propertyGroup.getFloatProperty(SCALE_MAX, -1f);
            float scaleStep =  propertyGroup.getFloatProperty(SCALE_STEP, -1f);
            if(scaleMin>-1) {
                options.setScaleMin(scaleMin); 
            }
            if(scaleMax>-1) {
                options.setScaleMax(scaleMax); 
            }
            if(scaleStep>-1) {
                options.setScaleStep(scaleStep); 
            }
            }
        }

        fillBG = blockProperties.getBlockRendererProperties().getBooleanProperty(FILLBG, true);

        options.getLegend().setEnabled(blockProperties.getBlockRendererProperties().getBooleanProperty(SHOW_LEGEND, options.getLegend().isEnabled()));
        options.getLegend().setPosition(blockProperties.getBlockRendererProperties().getStringProperty(LEGEND_POSITION));

        EJFrameworkExtensionProperties propertyGroup = blockProperties.getBlockRendererProperties().getPropertyGroup("LBL_CONFIG");

        options.getPlugins().getLabels().setArc(propertyGroup.getBooleanProperty(LBL_VIEW_ARC, options.getPlugins().getLabels().isArc()));
        options.getPlugins().getLabels().setRender(propertyGroup.getStringProperty(LBL_VIEW_TYPE));
        options.getPlugins().getLabels().setPosition(propertyGroup.getStringProperty(LBL_VIEW_POS));
        options.getPlugins().getLabels().setShowZero(false);
        String va = propertyGroup.getStringProperty(LBL_VIEW_VA);

        if (va != null && !va.isEmpty())
        {
            EJCoreVisualAttributeProperties visualAttributeProperties = _block.getForm().getVisualAttribute(va);
            if (visualAttributeProperties != null)
            {
                if (visualAttributeProperties.getForegroundColor() != null)
                {
                    Color color = visualAttributeProperties.getForegroundColor();
                    options.getPlugins().getLabels().setFontColor(EJRWTHtmlTableBlockRenderer.toHex(color.getRed(), color.getGreen(), color.getBlue()));
                }

                Font vaFont = EJRWTVisualAttributeUtils.INSTANCE.getFont(visualAttributeProperties, null);
                if (vaFont != null && vaFont.getFontData().length > 0)
                {
                    FontData fontData = vaFont.getFontData()[0];

                    if ((fontData.getStyle() & SWT.BOLD) != 0)
                    {
                        options.getPlugins().getLabels().setFontStyle("bold");
                    }
                    if ((fontData.getStyle() & SWT.ITALIC) != 0)
                    {
                        options.getPlugins().getLabels().setFontStyle("italic");
                    }

                    options.getPlugins().getLabels().setFontSize(fontData.getHeight());
                    options.getPlugins().getLabels().setFontFamily(fontData.getName());

                }
            }

        }

        // TODO:VA settings

        labelColumn = blockProperties.getBlockRendererProperties().getStringProperty(LABLE_COLUMN);

    }

    @Override
    public void blockCleared()
    {

        EJRWTAsync.runUISafe(dispaly, () -> {

            currentRecord = null;
            _treeBaseRecords.clear();

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

        _treeBaseRecords.addAll(_block.getRecords());
        recordSelected(getFirstRecord());

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
        EJRWTAsync.runUISafe(dispaly, () -> {

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

            Collection<EJDataRecord> records = _block.getRecords();

            Collection<EJScreenItemController> screenItems = getScreenItems();

            List<String> xlabel = new ArrayList<String>(screenItems.size());
            for (EJScreenItemController sItem : screenItems)
            {
                if (sItem.isSpacerItem())
                    continue;

                xlabel.add(sItem.getProperties().getLabel());

            }

            RadarChartRowData chartRowData = new RadarChartRowData(xlabel.toArray(new String[0]));

            for (EJDataRecord ejDataRecord : records)
            {
                if (ejDataRecord == null)
                    continue;
                Object lbl = labelColumn != null ? ejDataRecord.getValue(labelColumn) : "";
                ChartStyle colors = new ChartStyle(220, 220, 220, 0.6f);

                EJCoreVisualAttributeProperties attributeProperties = labelColumn != null ? ejDataRecord.getItem(labelColumn).getVisualAttribute() : null;

                RadarChartRowData.RowInfo rowInfo = new RowInfo();
                rowInfo.setLabel(lbl != null ? lbl.toString() : "");

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
                        //

                        colors.setStrokeColor(new RGB(color.getRed(), color.getGreen(), color.getBlue()));

                    }

                }
                colors.setFill(fillBG);
                rowInfo.setChartStyle(colors);
                double[] data = new double[screenItems.size()];
                String[] dataToolTips = new String[screenItems.size()];
                int index = 0;
                for (EJScreenItemController sItem : screenItems)
                {

                    Object yvalue = ejDataRecord.getValue(sItem.getName());

                    double val = 0;

                    if (yvalue instanceof String)
                    {
                        yvalue = new BigDecimal((String) yvalue);

                    }
                    if (yvalue instanceof Number)
                    {

                        val = ((Number) yvalue).doubleValue();

                    }

                    data[index] = (val);
                    dataToolTips[index] = getToolTipValue(val);
                    index++;

                }

                rowInfo.setAction("_radar_select");
                chartRowData.addRow(rowInfo, data, dataToolTips);

            }

            _chartView.load(chartRowData, options);

        }
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
        currentRecord = record;
        refresh();
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

        return currentRecord != null ? currentRecord : getFirstRecord();
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
                    _chartView = new RadarChart(group, style)
                    {

                        protected void action(String method, org.eclipse.rap.json.JsonObject parameters)
                        {

                            processAction(method, parameters);
                        }
                    };
                }
                else
                {
                    _chartView = new RadarChart(_mainPane, displayProperties.dispayGroupFrame() ? style | SWT.BORDER : style)
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
                _chartView = new RadarChart(_mainPane, style)
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

        refresh();
    }

    protected void processAction(String method, JsonObject parameters)
    {
        if ("legend_action".equals(method))
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

                if (label.equals(parameters.get("label").asString()))
                {
                    dataItem = sItem;
                    break;
                }
            }
            if (dataItem != null)
            {

                dataItem.getManagedItemRenderer().setVisible(!dataItem.getManagedItemRenderer().isVisible());
            }
            return;
        }
        if (method.equals("_pie_select"))
        {
            if (parameters.names().contains("label") && parameters.names().contains("value"))
            {
                currentRecord = null;
                EJScreenItemController dataItem = null;
                List<EJScreenItemController> screenItems = getScreenItems();
                for (EJScreenItemController sItem : screenItems)
                {
                    String label = sItem.getProperties().getLabel();
                    if (label.trim().isEmpty())
                    {
                        label = sItem.getProperties().getReferencedItemName();
                    }

                    if (label.equals(parameters.get("label").asString()))
                    {
                        dataItem = sItem;
                        break;
                    }
                }
                if (dataItem != null)
                {
                    // Collection<EJDataRecord> records = _block.getRecords();
                    // Object lastVal=null;
                    // for (EJDataRecord record : records)
                    // {
                    // Object value = record.getValue(dataItem.getName());
                    // if(value==null)
                    // value = lastVal;
                    //
                    // if (
                    // value != null &&
                    // value.equals(parameters.get("value").asDouble()))
                    // {
                    // currentRecord = record;
                    // break;
                    // }
                    // lastVal = value;
                    //
                    // }

                    EJCoreMainScreenItemProperties mainScreenItemProperties = (EJCoreMainScreenItemProperties) dataItem.getProperties();
                    String action = mainScreenItemProperties.getBlockRendererRequiredProperties().getStringProperty("action");
                    if (action != null && !action.trim().isEmpty())
                    {
                        _block.executeActionCommand(action, EJScreenType.MAIN);
                    }

                }
            }
        }

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
