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
package org.entirej.applicationframework.rwt.renderers.html;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rwt.EJRWTAsync;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.launcher.RWTUtils;
import org.entirej.applicationframework.rwt.component.EJRWTHtmlView;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;
import org.entirej.applicationframework.rwt.layout.EJRWTScrolledComposite;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppBlockRenderer;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.blocks.definition.interfaces.EJRWTMultiRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.blocks.definition.interfaces.EJRWTTreeBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.html.EJRWTAbstractFilteredHtml.FilteredContentProvider;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTInsertScreenRenderer;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTQueryScreenRenderer;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTUpdateScreenRenderer;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.table.HtmlBaseColumnLabelProvider;
import org.entirej.applicationframework.rwt.utils.EJRWTKeysUtil;
import org.entirej.applicationframework.rwt.utils.EJRWTKeysUtil.KeyInfo;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJForm;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.data.controllers.EJQuestion;
import org.entirej.framework.core.enumerations.EJManagedBlockProperty;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJQuestionButton;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.extensions.properties.EJCoreFrameworkExtensionPropertyList;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreBlockProperties;
import org.entirej.framework.core.properties.EJCoreMainScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeContainer;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;
import org.entirej.framework.core.properties.interfaces.EJBlockProperties;
import org.entirej.framework.core.properties.interfaces.EJItemGroupProperties;
import org.entirej.framework.core.properties.interfaces.EJMainScreenProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.interfaces.EJInsertScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJUpdateScreenRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJRWTHtmlTableBlockRenderer implements EJRWTAppBlockRenderer, KeyListener, EJRWTAppBlockRenderer.CustomBlockSizeSupport
{
    private Logger                                            LOGGER                     = LoggerFactory.getLogger(this.getClass());

    private static final String                               PROPERTY_ALIGNMENT         = "ALIGNMENT";
    private static final String                               PROPERTY_ALIGNMENT_CHAR    = "CHAR";
    private static final String                               PROPERTY_ALIGNMENT_CENTER  = "CENTER";
    private static final String                               PROPERTY_ALIGNMENT_RIGHT   = "RIGHT";
    private static final String                               PROPERTY_ALIGNMENT_LEFT    = "LEFT";

    public static final String                                CELL_ACTION_COMMAND        = "ACTION_COMMAND";
    public static final String                                ALLOW_ROW_SORTING          = "ALLOW_ROW_SORTING";
    private static final String                               PROPERTY_ALIGNMENT_JUSTIFY = "JUSTIFY";
    private static final String                               PROPERTY_CASE              = "CASE";
    private static final String                               PROPERTY_CASE_CAPITALIZE   = "CAPITALIZE";
    private static final String                               PROPERTY_CASE_UPPER        = "UPPER";
    private static final String                               PROPERTY_CASE_LOWER        = "LOWER";

    public static final String                                CELL_SPACING_PROPERTY      = "CELL_SPACING";
    public static final String                                CELL_PADDING_PROPERTY      = "CELL_PADDING";

    public static final String                                ROW_SELECTION              = "ROW_SELECTION";
    public static final String                                ROW_SELECTION_VA           = "ROW_SELECTION_VA";

    public static final String                                DISPLAY_WIDTH_PROPERTY     = "DISPLAY_WIDTH";
    public static final String                                ACTIONS                    = "ACTIONS";
    public static final String                                ACTION_ID                  = "ACTION_ID";
    public static final String                                ACTION_KEY                 = "ACTION_KEY";

    public static final String                                HEADER_VA                  = "HEADER_VA";
    public static final String                                ROW_ODD_VA                 = "ROW_ODD_VA";
    public static final String                                ROW_EVEN_VA                = "ROW_EVEN_VA";

    public static final String                                ENABLE_MARKUP              = "ENABLE_MARKUP";

    public static final String                                TEXT_SELECTION             = "TEXT_SELECTION";

    private EJEditableBlockController                         _block;
    private boolean                                           _isFocused                 = false;
    private ScrolledComposite                                 scrollComposite;
    private EJRWTHtmlView                                     _browser;
    private List<EJCoreMainScreenItemProperties>              _items                     = new ArrayList<EJCoreMainScreenItemProperties>();
    private Map<String, ColumnLabelProvider>                  _itemLabelProviders        = new HashMap<String, ColumnLabelProvider>();

    private Map<String, EJRWTAbstractTableSorter>             _itemSortProviders         = new HashMap<String, EJRWTAbstractTableSorter>();

    private Map<String, SortInfo>                             _sortContext               = new HashMap<String, SortInfo>();
    private String                                            _headerTag                 = null;
    private EJDataRecord                                      currentRec;

    private EJRWTQueryScreenRenderer                          _queryScreenRenderer;
    private EJRWTInsertScreenRenderer                         _insertScreenRenderer;
    private EJRWTUpdateScreenRenderer                         _updateScreenRenderer;

    private List<String>                                      _actionkeys                = new ArrayList<String>();
    private Map<KeyInfo, String>                              _actionInfoMap             = new HashMap<EJRWTKeysUtil.KeyInfo, String>();

    private SortInfo                                          activeSortColumn;

    private EJRWTAbstractFilteredHtml.FilteredContentProvider _filteredContentProvider;

    private List<EJDataRecord>                                _tableBaseRecords          = new ArrayList<EJDataRecord>();

    private boolean                                           textSelection;

    private boolean                                           filterKeepOnRefresh;

    private EJRWTAbstractFilteredHtml                         filterHtml;

    private String                                            defaultMessage;

    private Display                                           dispaly                    = Display.getDefault();

    private String                                            serviceHandlerUrl;

    private boolean                                           addHeader;

    private int                                               cellPadding;

    private int                                               cellSpacing;

    private boolean                                           rowSelection;

    private String                                            valueVA;

    private String                                            filter;

    private String                                            doubleClickActionCommand;

    private AtomicBoolean                                     refreshHtml                = new AtomicBoolean(false);

    protected void clearFilter()
    {
        if (_filteredContentProvider != null)
        {
            _filteredContentProvider.setFilter(null);
        }
    }

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
    public void blockCleared()
    {
        EJRWTAsync.runUISafe(dispaly, () -> {

            if (!filterKeepOnRefresh)
                clearFilter();
            createHTML();
        });

    }

    public void executingQuery()
    {
    }

    @Override
    public void detailBlocksCleared()
    {
        // no impl

    }

    public void enterInsert(EJDataRecord record)
    {
        if (_block.getInsertScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define an Insert Screen Renderer for this form before an insert operation can be performed.");
            _block.getForm().getMessenger().handleMessage(message);
        }
        else
        {
            _block.getInsertScreenRenderer().open(record);
        }
    }

    public void enterQuery(EJDataRecord queryRecord)
    {
        if (_block.getQueryScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define a Query Screen Renderer for this form before a query operation can be performed.");
            _block.getForm().getMessenger().handleMessage(message);
        }
        else
        {
            _block.getQueryScreenRenderer().open(queryRecord);
        }
    }

    public void enterUpdate(EJDataRecord recordToUpdate)
    {
        if (_block.getUpdateScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define an Update Screen Renderer for this form before an update operation can be performed.");
            _block.getForm().getMessenger().handleMessage(message);
        }
        else
        {
            _block.getUpdateScreenRenderer().open(recordToUpdate);
        }
    }

    @Override
    public void gainFocus()
    {
        setHasFocus(true);

    }

    @Override
    public boolean hasFocus()
    {
        return _isFocused;

    }

    @Override
    public EJQueryScreenRenderer getQueryScreenRenderer()
    {
        return _queryScreenRenderer;
    }

    @Override
    public EJInsertScreenRenderer getInsertScreenRenderer()
    {
        return _insertScreenRenderer;
    }

    @Override
    public EJUpdateScreenRenderer getUpdateScreenRenderer()
    {
        return _updateScreenRenderer;
    }

    @Override
    public void initialiseRenderer(EJEditableBlockController block)
    {
        _block = block;
        _queryScreenRenderer = new EJRWTQueryScreenRenderer();
        _insertScreenRenderer = new EJRWTInsertScreenRenderer();
        _updateScreenRenderer = new EJRWTUpdateScreenRenderer();
        serviceHandlerUrl = RWT.getServiceManager().getServiceHandlerUrl(VACSSServiceHandler.SERVICE_HANDLER);
    }

    @Override
    public boolean isCurrentRecordDirty()
    {

        return false;
    }

    @Override
    public void queryExecuted()
    {

        EJRWTAsync.runUISafe(dispaly, () -> {
            currentRec = null;

            activeSortColumn = null;
            String filter = null;
            if (filterKeepOnRefresh)
            {
                if (filterHtml != null)
                {
                    filter = filterHtml.getFilterString();

                }
            }

            clearFilter();

            if (_filteredContentProvider != null && filterKeepOnRefresh)
            {

                if (filterHtml != null)
                {
                    filterHtml.setInitialText(filter);

                }
                _filteredContentProvider.setFilter(filter);
            }
            createHTML();

        });

    }

    @Override
    public void recordDeleted(int arg0)
    {
        int pos = 0;
        if (_browser != null && !_browser.isDisposed())
        {
            pos = _browser.getScroll();
        }
        if (_filteredContentProvider != null)
        {
            String filter = _filteredContentProvider.getFilter();

            clearFilter();
            _filteredContentProvider.setFilter(filter);
        }
        createHTML();
        if (pos > 0)
        {
            _browser.setScroll(pos);
        }

    }

    @Override
    public void recordInserted(EJDataRecord arg0)
    {
        int pos = 0;
        if (_browser != null && !_browser.isDisposed())
        {
            pos = _browser.getScroll();
        }
        if (_filteredContentProvider != null)
        {
            String filter = _filteredContentProvider.getFilter();
            clearFilter();
            _filteredContentProvider.setFilter(filter);
        }
        createHTML();
        if (pos > 0)
        {
            _browser.setScroll(pos);
        }

    }

    @Override
    public void refreshBlockProperty(EJManagedBlockProperty arg0)
    {
        // no impl
    }

    @Override
    public void refreshBlockRendererProperty(String arg0)
    {
        // no impl
    }

    @Override
    public void setFocusToItem(EJScreenItemController arg0)
    {
        setHasFocus(true);
    }

    @Override
    public void setHasFocus(boolean focus)
    {
        _isFocused = focus;

        if (_isFocused)
        {
            _block.focusGained();
            // showFocusedBorder(true);
        }
        else
        {
            _block.focusLost();
            // showFocusedBorder(false);
        }

    }

    @Override
    public int getDisplayedRecordCount()
    {
        // Indicates the number of records that are available within the View.
        // the number depends on the filters set on the table!
        return _tableBaseRecords.size();
    }

    @Override
    public int getDisplayedRecordNumber(EJDataRecord record)
    {
        if (record == null)
        {
            return -1;
        }

        return _tableBaseRecords.indexOf(record);
    }

    @Override
    public EJDataRecord getRecordAt(int displayedRecordNumber)
    {
        if (displayedRecordNumber > -1 && displayedRecordNumber < getDisplayedRecordCount())
        {
            return _tableBaseRecords.get(displayedRecordNumber);
        }
        return null;
    }

    @Override
    public EJDataRecord getRecordAfter(EJDataRecord record)
    {
        return _block.getDataBlock().getRecordAfter(record);
    }

    @Override
    public EJDataRecord getRecordBefore(EJDataRecord record)
    {
        return _block.getDataBlock().getRecordBefore(record);
    }

    @Override
    public EJDataRecord getFirstRecord()
    {
        return _block.getDataBlock().getRecord(0);
    }

    @Override
    public EJDataRecord getLastRecord()
    {
        return _block.getDataBlock().getRecord(_block.getBlockRecordCount() - 1);
    }

    @Override
    public void recordSelected(EJDataRecord arg0)
    {

        currentRec = arg0;

        EJRWTAsync.runUISafe(dispaly, () -> {

            if (_browser != null && !_browser.isDisposed() && currentRec != null)
            {
                _browser.setSelection(String.valueOf(getDisplayedRecordNumber(currentRec)));
            }
        });
    }

    @Override
    public EJDataRecord getFocusedRecord()
    {
        return currentRec != null ? currentRec : getFirstRecord();
    }

    @Override
    public void refreshAfterChange(EJDataRecord arg0)
    {
        int pos = 0;
        if (_browser != null && !_browser.isDisposed())
        {
            pos = _browser.getScroll();
        }
        createHTML();
        if (pos > 0)
        {
            _browser.setScroll(pos);
        }

    }

    @Override
    public void refreshItemProperty(String itemName, EJManagedScreenProperty managedItemPropertyType, EJDataRecord record)
    {
        if (EJManagedScreenProperty.ITEM_INSTANCE_VISUAL_ATTRIBUTE.equals(managedItemPropertyType))
        {
            EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemName);
            if (item != null)
            {
                createHTML();
            }
        }
        else if (EJManagedScreenProperty.SCREEN_ITEM_VISUAL_ATTRIBUTE.equals(managedItemPropertyType))
        {
            EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemName);
            if (item != null)
            {
                item.getManagedItemRenderer().setVisualAttribute(item.getProperties().getVisualAttributeProperties());

                createHTML();
            }
        }
        else if (EJManagedScreenProperty.LABEL.equals(managedItemPropertyType))
        {
            createHeader();
            createHTML();

        }
        else if (EJManagedScreenProperty.HINT.equals(managedItemPropertyType))
        {

            createHeader();
            createHTML();

        }
        else if (EJManagedScreenProperty.VISIBLE.equals(managedItemPropertyType))
        {

            createHeader();
            createHTML();

        }

    }

    @Override
    public void refreshItemRendererProperty(String arg0, String arg1)
    {
        // no impl

    }

    @Override
    public void synchronize()
    {
        // no impl

    }

    @Override
    public Object getGuiComponent()
    {
        return scrollComposite;
    }

    @Override
    public void buildGuiComponent(EJRWTEntireJGridPane blockCanvas)
    {
        if (_browser != null && !_browser.isDisposed())
        {
            _browser.dispose();
        }

        EJBlockProperties blockProperties = _block.getProperties();
        EJMainScreenProperties mainScreenProperties = blockProperties.getMainScreenProperties();

        EJFrameworkExtensionProperties blockRendererProperties = blockProperties.getBlockRendererProperties();
        addHeader = true;
        doubleClickActionCommand = blockRendererProperties.getStringProperty(EJRWTMultiRecordBlockDefinitionProperties.DOUBLE_CLICK_ACTION_COMMAND);

        if (blockRendererProperties != null)
        {
            addHeader = blockRendererProperties.getBooleanProperty(EJRWTMultiRecordBlockDefinitionProperties.SHOW_HEADING_PROPERTY, true);
            EJCoreFrameworkExtensionPropertyList propertyList = blockRendererProperties.getPropertyList(ACTIONS);

            if (propertyList != null)
            {
                List<EJFrameworkExtensionPropertyListEntry> allListEntries = propertyList.getAllListEntries();
                for (EJFrameworkExtensionPropertyListEntry entry : allListEntries)
                {
                    String actionID = entry.getProperty(ACTION_ID);
                    String actionkey = entry.getProperty(ACTION_KEY);
                    if (actionID != null && actionkey != null && actionID.trim().length() > 0 && actionkey.trim().length() > 0)
                    {
                        addActionKeyinfo(actionkey, actionID);
                    }
                }
            }

        }

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = mainScreenProperties.getWidth();
        gridData.heightHint = mainScreenProperties.getHeight();

        gridData.horizontalSpan = mainScreenProperties.getHorizontalSpan();
        gridData.verticalSpan = mainScreenProperties.getVerticalSpan();
        gridData.grabExcessHorizontalSpace = mainScreenProperties.canExpandHorizontally();
        gridData.grabExcessVerticalSpace = mainScreenProperties.canExpandVertically();

        // if (gridData.grabExcessHorizontalSpace)
        // gridData.minimumHeight = mainScreenProperties.getHeight();
        // if (gridData.grabExcessVerticalSpace)
        // gridData.minimumWidth = mainScreenProperties.getHeight();
        blockCanvas.setLayoutData(gridData);
        scrollComposite = new EJRWTScrolledComposite(blockCanvas, SWT.V_SCROLL | SWT.H_SCROLL);
        scrollComposite.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
        scrollComposite.setExpandHorizontal(true);
        scrollComposite.setExpandVertical(true);
        scrollComposite.setMinSize(mainScreenProperties.getWidth(), mainScreenProperties.getHeight());
        boolean filtered = blockRendererProperties.getBooleanProperty(EJRWTTreeBlockDefinitionProperties.FILTER, false);
        this.filterKeepOnRefresh = blockRendererProperties.getBooleanProperty(EJRWTTreeBlockDefinitionProperties.FILTER_KEEP_ON_REFRESH, false);
        this.textSelection = blockRendererProperties.getBooleanProperty(TEXT_SELECTION, false);

        if (mainScreenProperties.getDisplayFrame())
        {
            String frameTitle = mainScreenProperties.getFrameTitle();
            if (frameTitle != null && frameTitle.length() > 0)
            {
                Group group = new Group(scrollComposite, SWT.NONE);
                group.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
                hookKeyListener(group);
                scrollComposite.setContent(group);
                group.setLayout(new GridLayout());
                scrollComposite.setLayoutData(gridData);

                if (frameTitle != null && frameTitle.length() > 0)
                {
                    group.setText(frameTitle);
                }

                if (filtered)
                {
                    filterHtml = new EJRWTAbstractFilteredHtml(group, SWT.NONE, textSelection)
                    {

                        @Override
                        public void filter(String filter)
                        {
                            _filteredContentProvider.setFilter(filter);
                            createHTML();
                        }

                        @Override
                        protected EJRWTHtmlView doCreateTableViewer(Composite parent, int style, boolean textSelection)
                        {

                            return new EJRWTHtmlView(parent, SWT.NONE, textSelection)
                            {
                                private static final long serialVersionUID = 1L;

                                @Override
                                public void action(String method, JsonObject parameters)
                                {
                                    if ("eselect".equals(method))
                                    {
                                        final Object arg1 = parameters.get("0").asString();
                                        if (arg1 instanceof String)
                                        {
                                            EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg1));
                                            // if (currentRec != recordAt)
                                            {
                                                currentRec = recordAt;
                                                if (currentRec != null)
                                                    _block.newRecordInstance(currentRec);
                                            }

                                        }
                                    }
                                    else if ("edblclick".equals(method))
                                    {
                                        final Object arg1 = parameters.get("0").asString();
                                        if (arg1 instanceof String)
                                        {
                                            EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg1));
                                            if (currentRec != recordAt)
                                            {
                                                currentRec = recordAt;
                                                if (currentRec != null)
                                                    _block.newRecordInstance(currentRec);
                                            }

                                            if (doubleClickActionCommand != null)
                                            {
                                                _block.executeActionCommand(doubleClickActionCommand, EJScreenType.MAIN);
                                            }

                                        }
                                    }

                                    else if ("eaction".equals(method))
                                    {
                                        final Object arg1 = parameters.get("0").asString();
                                        Object arg2 = parameters.get("1").asString();
                                        if (arg1 instanceof String)
                                        {
                                            if (arg2 instanceof String)
                                            {
                                                EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg2));
                                                if (currentRec != recordAt)
                                                {
                                                    currentRec = recordAt;
                                                    if (currentRec != null)
                                                        _block.newRecordInstance(currentRec);
                                                }
                                            }
                                            Display.getDefault().asyncExec(new Runnable()
                                            {

                                                @Override
                                                public void run()
                                                {
                                                    try
                                                    {
                                                        setHasFocus(true);
                                                        _block.executeActionCommand((String) arg1, EJScreenType.MAIN);
                                                    }
                                                    catch (EJApplicationException exception)
                                                    {
                                                        _block.getFrameworkManager().handleException(exception);
                                                    }
                                                }
                                            });

                                        }
                                    }
                                    else if ("esort".equals(method))
                                    {
                                        handleSort(parameters);
                                    }

                                }
                            };
                        }
                    };
                    _browser = filterHtml.getViewer();

                    defaultMessage = blockRendererProperties.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_MESSAGE);
                    if (defaultMessage != null)
                        filterHtml.getFilterControl().setMessage(defaultMessage);
                }
                else
                {
                    _browser = new EJRWTHtmlView(group, SWT.NONE, textSelection)
                    {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void action(String method, JsonObject parameters)
                        {
                            if ("eselect".equals(method))
                            {
                                final Object arg1 = parameters.get("0").asString();
                                if (arg1 instanceof String)
                                {
                                    EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg1));
                                    // if (currentRec != recordAt)
                                    {
                                        currentRec = recordAt;
                                        if (currentRec != null)
                                            _block.newRecordInstance(currentRec);
                                    }
                                }
                            }
                            else if ("edblclick".equals(method))
                            {
                                final Object arg1 = parameters.get("0").asString();
                                if (arg1 instanceof String)
                                {
                                    EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg1));
                                    if (currentRec != recordAt)
                                    {
                                        currentRec = recordAt;
                                        if (currentRec != null)
                                            _block.newRecordInstance(currentRec);
                                    }

                                    if (doubleClickActionCommand != null)
                                    {
                                        _block.executeActionCommand(doubleClickActionCommand, EJScreenType.MAIN);
                                    }

                                }
                            }

                            else if ("eaction".equals(method))
                            {
                                final Object arg1 = parameters.get("0").asString();
                                Object arg2 = parameters.get("1").asString();
                                if (arg1 instanceof String)
                                {
                                    if (arg2 instanceof String)
                                    {
                                        EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg2));
                                        if (currentRec != recordAt)
                                        {
                                            currentRec = recordAt;
                                            if (currentRec != null)
                                                _block.newRecordInstance(currentRec);
                                        }
                                    }
                                    Display.getDefault().asyncExec(new Runnable()
                                    {

                                        @Override
                                        public void run()
                                        {
                                            try
                                            {
                                                setHasFocus(true);
                                                _block.executeActionCommand((String) arg1, EJScreenType.MAIN);
                                            }
                                            catch (EJApplicationException exception)
                                            {
                                                _block.getFrameworkManager().handleException(exception);
                                            }
                                        }
                                    });

                                }
                            }
                            else if ("esort".equals(method))
                            {
                                handleSort(parameters);
                            }

                        }
                    };
                }

            }
            else
            {
                if (filtered)
                {
                    filterHtml = new EJRWTAbstractFilteredHtml(scrollComposite, SWT.NONE, textSelection)
                    {

                        @Override
                        public void filter(String filter)
                        {
                            _filteredContentProvider.setFilter(filter);
                            createHTML();
                        }

                        @Override
                        protected EJRWTHtmlView doCreateTableViewer(Composite parent, int style, boolean textSelection)
                        {

                            return new EJRWTHtmlView(parent, SWT.NONE, textSelection)
                            {
                                private static final long serialVersionUID = 1L;

                                @Override
                                public void action(String method, JsonObject parameters)
                                {
                                    if ("eselect".equals(method))
                                    {
                                        final Object arg1 = parameters.get("0").asString();
                                        if (arg1 instanceof String)
                                        {
                                            EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg1));
                                            // if (currentRec != recordAt)
                                            {
                                                currentRec = recordAt;
                                                if (currentRec != null)
                                                    _block.newRecordInstance(currentRec);
                                            }
                                        }
                                    }
                                    else if ("edblclick".equals(method))
                                    {
                                        final Object arg1 = parameters.get("0").asString();
                                        if (arg1 instanceof String)
                                        {
                                            EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg1));
                                            if (currentRec != recordAt)
                                            {
                                                currentRec = recordAt;
                                                if (currentRec != null)
                                                    _block.newRecordInstance(currentRec);
                                            }

                                            if (doubleClickActionCommand != null)
                                            {
                                                _block.executeActionCommand(doubleClickActionCommand, EJScreenType.MAIN);
                                            }

                                        }
                                    }

                                    else if ("eaction".equals(method))
                                    {
                                        final Object arg1 = parameters.get("0").asString();
                                        Object arg2 = parameters.get("1").asString();
                                        if (arg1 instanceof String)
                                        {
                                            if (arg2 instanceof String)
                                            {
                                                EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg2));
                                                if (currentRec != recordAt)
                                                {
                                                    currentRec = recordAt;
                                                    if (currentRec != null)
                                                        _block.newRecordInstance(currentRec);
                                                }
                                            }
                                            Display.getDefault().asyncExec(new Runnable()
                                            {

                                                @Override
                                                public void run()
                                                {
                                                    try
                                                    {
                                                        _block.executeActionCommand((String) arg1, EJScreenType.MAIN);
                                                    }
                                                    catch (EJApplicationException exception)
                                                    {
                                                        _block.getFrameworkManager().handleException(exception);
                                                    }
                                                }
                                            });

                                        }
                                    }
                                    else if ("esort".equals(method))
                                    {
                                        handleSort(parameters);
                                    }

                                }
                            };
                        }
                    };
                    _browser = filterHtml.getViewer();

                    defaultMessage = blockRendererProperties.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_MESSAGE);
                    if (defaultMessage != null)
                        filterHtml.getFilterControl().setMessage(defaultMessage);
                    scrollComposite.setContent(filterHtml);
                    scrollComposite.setLayoutData(gridData);
                }
                else
                {

                    filterHtml = null;
                    _browser = new EJRWTHtmlView(scrollComposite, SWT.BORDER, textSelection)
                    {

                        private static final long serialVersionUID = 1L;

                        @Override
                        public void action(String method, JsonObject parameters)
                        {
                            if ("eselect".equals(method))
                            {
                                final Object arg1 = parameters.get("0").asString();
                                if (arg1 instanceof String)
                                {
                                    EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg1));
                                    // if (currentRec != recordAt)
                                    {
                                        currentRec = recordAt;
                                        if (currentRec != null)
                                            _block.newRecordInstance(currentRec);
                                    }
                                }
                            }
                            else if ("edblclick".equals(method))
                            {
                                final Object arg1 = parameters.get("0").asString();
                                if (arg1 instanceof String)
                                {
                                    EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg1));
                                    if (currentRec != recordAt)
                                    {
                                        currentRec = recordAt;
                                        if (currentRec != null)
                                            _block.newRecordInstance(currentRec);
                                    }

                                    if (doubleClickActionCommand != null)
                                    {
                                        _block.executeActionCommand(doubleClickActionCommand, EJScreenType.MAIN);
                                    }

                                }
                            }

                            else if ("eaction".equals(method))
                            {
                                final Object arg1 = parameters.get("0").asString();
                                Object arg2 = parameters.get("1").asString();
                                if (arg1 instanceof String)
                                {
                                    if (arg2 instanceof String)
                                    {
                                        EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg2));
                                        if (currentRec != recordAt)
                                        {
                                            currentRec = recordAt;
                                            if (currentRec != null)
                                                _block.newRecordInstance(currentRec);
                                        }
                                    }
                                    Display.getDefault().asyncExec(new Runnable()
                                    {

                                        @Override
                                        public void run()
                                        {
                                            try
                                            {
                                                _block.executeActionCommand((String) arg1, EJScreenType.MAIN);
                                            }
                                            catch (EJApplicationException exception)
                                            {
                                                _block.getFrameworkManager().handleException(exception);
                                            }
                                        }
                                    });

                                }
                            }
                            else if ("esort".equals(method))
                            {
                                handleSort(parameters);
                            }

                        }
                    };
                    scrollComposite.setContent(_browser);
                    scrollComposite.setLayoutData(gridData);

                }
            }

        }
        else
        {
            if (filtered)
            {
                filterHtml = new EJRWTAbstractFilteredHtml(scrollComposite, SWT.NONE, textSelection)
                {

                    @Override
                    public void filter(String filter)
                    {
                        _filteredContentProvider.setFilter(filter);
                        createHTML();
                    }

                    @Override
                    protected EJRWTHtmlView doCreateTableViewer(Composite parent, int style, boolean textSelection)
                    {

                        return new EJRWTHtmlView(parent, SWT.NONE, textSelection)
                        {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public void action(String method, JsonObject parameters)
                            {
                                if ("eselect".equals(method))
                                {
                                    final Object arg1 = parameters.get("0").asString();
                                    if (arg1 instanceof String)
                                    {
                                        EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg1));
                                        // if (currentRec != recordAt)
                                        {
                                            currentRec = recordAt;
                                            if (currentRec != null)
                                                _block.newRecordInstance(currentRec);
                                        }
                                    }
                                }
                                else if ("edblclick".equals(method))
                                {
                                    final Object arg1 = parameters.get("0").asString();
                                    if (arg1 instanceof String)
                                    {
                                        EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg1));
                                        if (currentRec != recordAt)
                                        {
                                            currentRec = recordAt;
                                            if (currentRec != null)
                                                _block.newRecordInstance(currentRec);
                                        }

                                        if (doubleClickActionCommand != null)
                                        {
                                            _block.executeActionCommand(doubleClickActionCommand, EJScreenType.MAIN);
                                        }

                                    }
                                }

                                else if ("eaction".equals(method))
                                {
                                    final Object arg1 = parameters.get("0").asString();
                                    Object arg2 = parameters.get("1").asString();
                                    if (arg1 instanceof String)
                                    {
                                        if (arg2 instanceof String)
                                        {
                                            EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg2));
                                            if (currentRec != recordAt)
                                            {
                                                currentRec = recordAt;
                                                if (currentRec != null)
                                                    _block.newRecordInstance(currentRec);
                                            }
                                        }
                                        Display.getDefault().asyncExec(new Runnable()
                                        {

                                            @Override
                                            public void run()
                                            {
                                                try
                                                {
                                                    _block.executeActionCommand((String) arg1, EJScreenType.MAIN);
                                                }
                                                catch (EJApplicationException exception)
                                                {
                                                    _block.getFrameworkManager().handleException(exception);
                                                }
                                            }
                                        });

                                    }
                                }
                                else if ("esort".equals(method))
                                {
                                    handleSort(parameters);
                                }

                            }
                        };
                    }
                };
                _browser = filterHtml.getViewer();
                scrollComposite.setContent(filterHtml);
                scrollComposite.setLayoutData(gridData);

                defaultMessage = blockRendererProperties.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_MESSAGE);
                if (defaultMessage != null)
                    filterHtml.getFilterControl().setMessage(defaultMessage);
            }
            else
            {
                filterHtml = null;
                _browser = new EJRWTHtmlView(scrollComposite, SWT.NONE, textSelection)
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void action(String method, JsonObject parameters)
                    {

                        if ("eselect".equals(method))
                        {
                            final Object arg1 = parameters.get("0").asString();
                            if (arg1 instanceof String)
                            {
                                EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg1));
                                // if (currentRec != recordAt)
                                {
                                    currentRec = recordAt;
                                    if (currentRec != null)
                                        _block.newRecordInstance(currentRec);
                                }
                            }
                        }
                        else if ("edblclick".equals(method))
                        {
                            final Object arg1 = parameters.get("0").asString();
                            if (arg1 instanceof String)
                            {
                                EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg1));
                                if (currentRec != recordAt)
                                {
                                    currentRec = recordAt;
                                    if (currentRec != null)
                                        _block.newRecordInstance(currentRec);
                                }

                                if (doubleClickActionCommand != null)
                                {
                                    _block.executeActionCommand(doubleClickActionCommand, EJScreenType.MAIN);
                                }

                            }
                        }

                        else if ("eaction".equals(method))
                        {
                            final Object arg1 = parameters.get("0").asString();
                            Object arg2 = parameters.get("1").asString();
                            if (arg1 instanceof String)
                            {
                                if (arg2 instanceof String)
                                {
                                    EJDataRecord recordAt = getRecordAt(Integer.valueOf((String) arg2));
                                    if (currentRec != recordAt)
                                    {
                                        currentRec = recordAt;
                                        if (currentRec != null)
                                            _block.newRecordInstance(currentRec);
                                    }
                                }
                                Display.getDefault().asyncExec(new Runnable()
                                {

                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            _block.executeActionCommand((String) arg1, EJScreenType.MAIN);
                                        }
                                        catch (EJApplicationException exception)
                                        {
                                            _block.getFrameworkManager().handleException(exception);
                                        }
                                    }
                                });

                            }
                        }
                        else if ("esort".equals(method))
                        {
                            handleSort(parameters);
                        }

                    }
                };
                scrollComposite.setContent(_browser);
                scrollComposite.setLayoutData(gridData);
                hookKeyListener(scrollComposite);
            }
        }

        _browser.addFocusListener(new FocusListener()
        {

            private static final long serialVersionUID = 1L;

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
        _browser.addMouseListener(new MouseAdapter()
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void mouseDown(MouseEvent arg0)
            {
                setHasFocus(true);

            }

        });
        createHeader();
        hookKeyListener(_browser);

        final EJRWTAbstractFilteredHtml _filterHtml = filterHtml;
        _filteredContentProvider = new FilteredContentProvider()
        {

            boolean matchItem(EJDataRecord rec)
            {
                if (filter != null && filter.trim().length() > 0)
                {
                    for (ColumnLabelProvider filterTextProvider : _itemLabelProviders.values())
                    {

                        String text = filterTextProvider.getText(rec);
                        if (text != null && text.toLowerCase().contains(filter.toLowerCase()))
                        {
                            return true;
                        }
                    }
                    // if no match try to match Numeric value
                    try
                    {
                        double parseDouble = Double.parseDouble(filter);
                        for (String item : _itemLabelProviders.keySet())
                        {
                            Object value = rec.getValue(item);
                            if (value instanceof Number)
                            {
                                if (((Number) value).doubleValue() == parseDouble)
                                {
                                    return true;
                                }
                            }
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        // ignore
                    }
                }
                return false;
            }

            @Override
            public void setFilter(String filter)
            {
                if (getFilter() != null && getFilter().equals(filter))
                    return;
                super.setFilter(filter);
                _tableBaseRecords.clear();

                if (filter == null || filter.trim().length() == 0)
                {
                    if (_filterHtml != null)
                    {
                        _filterHtml.clearText();
                    }
                    _tableBaseRecords.addAll(_block.getBlock().getRecords());
                }
                else
                {

                    for (EJDataRecord record : _block.getBlock().getRecords())
                    {
                        if (matchItem(record))
                        {
                            _tableBaseRecords.add(record);
                        }
                    }
                }

            }
        };

        if (filterHtml != null && filter != null)
        {
            filterHtml.setInitialText(filter);

        }
        _filteredContentProvider.setFilter(filter);

        createHTML();

    }

    public void handleSort(JsonObject parameters)
    {
        final Object sortid = parameters.get("0").asString();
        if (sortid != null)
        {
            SortInfo sortInfo = _sortContext.get(sortid);
            if (sortInfo != null)
            {
                activeSortColumn = sortInfo;
                switch (sortInfo.direction)
                {
                    case ASC:
                        sortInfo.direction = SortInfo.DIRECTION.DESC;
                        break;
                    case DESC:
                        sortInfo.direction = SortInfo.DIRECTION.NONE;
                        break;
                    case NONE:
                        sortInfo.direction = SortInfo.DIRECTION.ASC;
                        break;
                }
            }
            createHTML();
        }
    }

    private Collection<EJDataRecord> sortedRecords(Collection<EJDataRecord> records)
    {
        if (activeSortColumn == null || _sortContext.isEmpty() || !_itemSortProviders.containsKey(activeSortColumn.columnName))
        {
            return records;
        }
        final EJRWTAbstractTableSorter tableSorter = _itemSortProviders.get(activeSortColumn.columnName);

        List<EJDataRecord> sorted = new ArrayList<EJDataRecord>(records);

        switch (activeSortColumn.direction)
        {
            case ASC:
            {
                Comparator<EJDataRecord> comparator = new Comparator<EJDataRecord>()
                {

                    @Override
                    public int compare(EJDataRecord o1, EJDataRecord o2)
                    {
                        return tableSorter.compare(null, o1, o2);
                    }
                };
                Collections.sort(sorted, comparator);
            }
                break;
            case DESC:
            {
                Comparator<EJDataRecord> comparator = new Comparator<EJDataRecord>()
                {

                    @Override
                    public int compare(EJDataRecord o1, EJDataRecord o2)
                    {
                        return -1 * tableSorter.compare(null, o1, o2);
                    }
                };
                Collections.sort(sorted, comparator);
            }
                break;
        }

        return sorted;
    }

    private static String getStyleDef()
    {

        StringBuilder builder = new StringBuilder();
        // builder.append("<style type=\"text/css\">");
        {
            // builder.append("*{");
            //
            // builder.append("}");
            builder.append("td{");
            builder.append("font: 11px Verdana, \"Lucida Sans\", Arial, Helvetica, sans-serif;");
            builder.append(" -webkit-touch-callout: text;" + "    -webkit-user-select: text;" + "     -khtml-user-select: text;" + "       -moz-user-select: text;" + "        -ms-user-select: text;" + "            user-select: text;");
            builder.append("}");
            builder.append("u.default {");
            builder.append("font: 11px Verdana, \"Lucida Sans\", Arial, Helvetica, sans-serif;");
            builder.append("padding: 1px 2px 1px 0px;");
            builder.append("}");

            builder.append("p{");
            builder.append("font: 11px Verdana, \"Lucida Sans\", Arial, Helvetica, sans-serif;");
            builder.append("padding: 0px 0px 0px 0px;margin-top: 0px; margin-bottom: 0px;");
            builder.append("}");

            builder.append("u.default_link {");
            builder.append("font: 11px Verdana, \"Lucida Sans\", Arial, Helvetica, sans-serif;");
            builder.append("padding: 1px 2px 1px 0px;");
            builder.append("text-shadow: none;");
            builder.append("}");

            builder.append("u.default_link:hover {");
            builder.append("font: 11px Verdana, \"Lucida Sans\", Arial, Helvetica, sans-serif;");
            builder.append("cursor: pointer; cursor: hand;");
            builder.append("}");

            builder.append("u.default_link_fg {");
            builder.append("font: 11px Verdana, \"Lucida Sans\", Arial, Helvetica, sans-serif;");
            builder.append("padding: 1px 2px 1px 0px;");
            builder.append("color: #416693;text-shadow: none;");
            builder.append("}");

            builder.append("u.default_link_fg:hover {");
            builder.append("font: 11px Verdana, \"Lucida Sans\", Arial, Helvetica, sans-serif;");
            builder.append("cursor: pointer; cursor: hand;");
            builder.append("}");

            builder.append(".default_all {");
            builder.append("font: 11px Verdana, \"Lucida Sans\", Arial, Helvetica, sans-serif;");
            builder.append("padding: 0px 0px 0px 0px;");
            Font font = Display.getDefault().getSystemFont();

            builder.append("}");

            EJCoreVisualAttributeContainer visualAttributesContainer = EJCoreProperties.getInstance().getVisualAttributesContainer();
            for (EJCoreVisualAttributeProperties va : visualAttributesContainer.getVisualAttributes())
            {
                builder.append(" \n");
                builder.append(".");
                builder.append(va.getName());
                builder.append("{");
                builder.append("padding: 0px 0px 0px 0px;");

                Font vaFont = EJRWTVisualAttributeUtils.INSTANCE.getFont(va, font);
                if (vaFont != null && vaFont.getFontData().length > 0)
                {
                    FontData fontData = vaFont.getFontData()[0];
                    builder.append("font:");
                    if ((fontData.getStyle() & SWT.BOLD) != 0)
                    {
                        builder.append("bold ");
                    }
                    if ((fontData.getStyle() & SWT.ITALIC) != 0)
                    {
                        builder.append("italic ");
                    }

                    builder.append(fontData.getHeight());
                    builder.append("px ");
                    builder.append(fontData.getName());

                    builder.append(";");

                }
                else
                {
                    builder.append("font: 11px Verdana, \"Lucida Sans\", Arial, Helvetica, sans-serif;");
                }

                Color backgroundColor = va.getBackgroundColor();
                if (backgroundColor != null)
                {
                    String hexString = toHex(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
                    builder.append("background-color: ");
                    builder.append(hexString);
                    builder.append(";");
                }
                Color foregroundColor = va.getForegroundColor();
                if (foregroundColor != null)
                {
                    String hexString = toHex(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue());
                    builder.append("color: ");
                    builder.append(hexString);
                    builder.append(";");
                }
                builder.append("}");
            }

        }
        // builder.append("</style>");

        return builder.toString();

    }

    public static String toHex(int r, int g, int b)
    {
        return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
    }

    private static String toBrowserHexValue(int number)
    {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2)
        {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }

    private void createHeader()
    {
        _items.clear();
        _sortContext.clear();
        _itemLabelProviders.clear();
        _itemSortProviders.clear();
        if (_items.isEmpty())
        {
            Collection<EJItemGroupProperties> allItemGroupProperties = _block.getProperties().getScreenItemGroupContainer(EJScreenType.MAIN).getAllItemGroupProperties();

            cellSpacing = _block.getProperties().getBlockRendererProperties().getIntProperty(CELL_SPACING_PROPERTY, 0);
            cellPadding = _block.getProperties().getBlockRendererProperties().getIntProperty(CELL_PADDING_PROPERTY, 0);
            String paddingStyle = "";
            if (cellPadding > 0)
            {
                String str = String.valueOf(cellPadding);
                paddingStyle += String.format("padding: %spx %spx %spx %spx; ", str, str, str, str);
            }

            StringBuilder header = new StringBuilder("<thead><tr>");

            rowSelection = _block.getProperties().getBlockRendererProperties().getBooleanProperty(ROW_SELECTION, false);

            String selectionTD = "<th width=1 ></td>";
            if (rowSelection)
            {
                header.append(selectionTD);
            }
            for (EJItemGroupProperties groupProperties : allItemGroupProperties)
            {
                Collection<EJScreenItemProperties> itemProperties = groupProperties.getAllItemProperties();
                for (EJScreenItemProperties screenItemProperties : itemProperties)
                {
                    EJCoreMainScreenItemProperties itemProps = (EJCoreMainScreenItemProperties) screenItemProperties;

                    EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemProps.getReferencedItemName());
                    if (!item.isVisible())
                        continue;

                    EJManagedItemRendererWrapper renderer = item.getManagedItemRenderer();
                    if (renderer != null)
                    {
                        EJRWTAppItemRenderer itemRenderer = (EJRWTAppItemRenderer) renderer.getUnmanagedRenderer();

                        ColumnLabelProvider labelProvider = itemRenderer.createColumnLabelProvider(itemProps, item);
                        _items.add(itemProps);
                        _itemLabelProviders.put(itemProps.getReferencedItemName(), labelProvider);

                        if (addHeader)
                        {

                            String styleClass = "default_all";
                            EJFrameworkExtensionProperties rendererProperties = item.getReferencedItemProperties().getItemRendererProperties();
                            EJFrameworkExtensionProperties extentionProperties = itemProps.getBlockRendererRequiredProperties();
                            int width = -1;
                            if (width == -1)
                            {
                                width = extentionProperties.getIntProperty(DISPLAY_WIDTH_PROPERTY, 0);
                            }

                            header.append("<th ");

                            if (width > 0)
                            {
                                Font font = labelProvider.getFont(new Object());

                                if (font == null)
                                    font = _browser.getFont();
                                if (font != null)
                                {
                                    float avgCharWidth = RWTUtils.getAvgCharWidth(font);
                                    if (avgCharWidth > 0)
                                    {
                                        if (width != 1)
                                        {
                                            // add +1 padding
                                            width = ((int) (((width + 1) * avgCharWidth)));
                                        }
                                    }
                                }

                                header.append(String.format(" width=%s ", width));
                            }
                            header.append(" ");

                            String alignment = null;

                            String alignmentProperty = rendererProperties.getStringProperty(PROPERTY_ALIGNMENT);
                            if (alignmentProperty == null)
                            {
                                alignmentProperty = rendererProperties.getStringProperty("ALLIGNMENT");
                            }
                            alignment = getComponentAlignment(alignmentProperty);

                            SortInfo sortInfo = null;
                            if (extentionProperties.getBooleanProperty(ALLOW_ROW_SORTING, true))
                            {
                                EJRWTAbstractTableSorter columnSorter = itemRenderer.getColumnSorter(itemProps, item);
                                if (columnSorter != null)
                                {
                                    _itemSortProviders.put(itemProps.getReferencedItemName(), columnSorter);
                                    sortInfo = new SortInfo();
                                    sortInfo.columnName = itemProps.getReferencedItemName();
                                    _sortContext.put(sortInfo.id, sortInfo);
                                }
                            }

                            String functionDef = null;
                            if (sortInfo != null)
                            {
                                functionDef = String.format("em='esort' earg='%s' ", sortInfo.id);
                            }

                            valueVA = _block.getProperties().getBlockRendererProperties().getStringProperty(HEADER_VA);
                            if (valueVA != null && valueVA.length() > 0)
                            {
                                styleClass = valueVA;
                                valueVA = rendererProperties.getStringProperty(HEADER_VA);
                                if (valueVA != null && valueVA.length() > 0)
                                    styleClass = valueVA;
                            }
                            header.append(String.format(" class=\"%s\" ", styleClass));
                            if (alignment != null)
                            {
                                header.append(String.format(" align=\'%s\'", alignment));
                            }

                            if (paddingStyle != null)
                            {
                                header.append(String.format(" style=\'%s\'", paddingStyle));
                            }
                            if (itemProps.getHint() != null && !itemProps.getHint().isEmpty())
                            {
                                header.append(String.format(" title=\'%s\'", itemProps.getHint()));
                            }
                            header.append("> ");

                            if (itemProps.getLabel() != null)
                            {
                                if (functionDef != null)
                                {
                                    header.append(String.format("<ejl><u %s class=\"%s, %s\"  ", "style=\"line-height: 100%\"", ("default_all".equals(styleClass) ? "default_link_fg" : "default_link"), styleClass));
                                    header.append(functionDef).append(">");
                                }
                                header.append(!(extentionProperties.getBooleanProperty(ENABLE_MARKUP, false) || rendererProperties.getBooleanProperty("HTML_FORMAT", false)) ? ignoreHtml(itemProps.getLabel()) : itemProps.getLabel());
                                if (sortInfo != null)
                                    header.append(String.format("<esh %s/>", sortInfo.id));
                            }
                            header.append("</th>");
                        }
                    }
                }
            }

            if (addHeader)
            {
                header.append("</tr></thead>");
                _headerTag = header.toString();
            }
        }

    }

    private void createHTML()
    {
        if (!refreshHtml.get())
        {
            refreshHtml.set(true);
            Display.getDefault().asyncExec(() -> {
                try
                {
                    _createHTML();
                }
                finally
                {
                    refreshHtml.set(false);
                }
            });
        }
    }

    private void _createHTML()
    {

        if (_browser == null || _browser.isDisposed())
        {
            return;
        }

        StringBuilder builder = new StringBuilder();
        {
            builder.append("<div id=\"table\" style=\"float:left;width:100%;height:100%; overflow:auto\">");
            {
                EJCoreBlockProperties blockProperties = _block.getProperties();
                int cellSpacing = blockProperties.getBlockRendererProperties().getIntProperty(CELL_SPACING_PROPERTY, 0);
                int cellPadding = blockProperties.getBlockRendererProperties().getIntProperty(CELL_PADDING_PROPERTY, 0);

                boolean rowSelection = blockProperties.getBlockRendererProperties().getBooleanProperty(ROW_SELECTION, false);
                String rowSelectionVA = blockProperties.getBlockRendererProperties().getStringProperty(ROW_SELECTION_VA);

                String selectionTD = "<td width=1 class=\"rowindi\" style=\" border-left: 2px solid #4D90F0;\n" + "    background-color: #fafafa; visibility:hidden \"></td>";

                if (rowSelectionVA != null && !rowSelectionVA.isEmpty())
                {
                    EJCoreVisualAttributeProperties selectVa = EJCoreProperties.getInstance().getVisualAttributesContainer().getVisualAttributeProperties(rowSelectionVA);

                    Color backgroundColor = selectVa.getBackgroundColor();
                    if (selectVa != null)
                    {
                        String hexString = toHex(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
                        selectionTD = "<td width=1 class=\"rowindi\" style=\" border-left: 2px solid " + hexString + ";\n" + "    background-color: #fafafa; visibility:hidden \"></td>";

                    }
                }

                String tdStyle = null;
                if (cellPadding > 0)
                {
                    String str = String.valueOf(cellPadding);
                    tdStyle = String.format("padding: %spx %spx %spx %spx; ", str, str, str, str);
                }

                builder.append("<table border=0 cellspacing=").append(cellSpacing).append(" width=\"100%\" >");
                {
                    builder.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
                    builder.append(serviceHandlerUrl);
                    builder.append("\">");
                    int charHeight = EJRWTImageRetriever.getGraphicsProvider().getCharHeight(Display.getDefault().getSystemFont());

                    if (_headerTag != null)
                    {

                        String sortHeader = _headerTag;
                        if (activeSortColumn != null)
                        {
                            StringBuilder header = new StringBuilder();
                            if (activeSortColumn.direction != SortInfo.DIRECTION.NONE)
                            {
                                header.append("&nbsp <img name=\"open\" ");
                                header.append("src=\"");
                                header.append(createImageUrl(activeSortColumn.direction == SortInfo.DIRECTION.ASC ? "resource/widget/rap/column/sort-indicator-up.png" : "resource/widget/rap/column/sort-indicator-down.png"));
                                header.append("\"");
                                header.append(" >");
                            }

                            sortHeader = _headerTag.replace(String.format("<esh %s/>", activeSortColumn.id), header.toString());
                        }
                        builder.append(sortHeader);
                    }

                    Collection<EJDataRecord> records = _tableBaseRecords;

                    builder.append("<tbody>");
                    if (records.size() > 0)
                    {

                        records = sortedRecords(records);
                        int lastRowSpan = 0;

                        String oddVA = "default_all";
                        String valueVA = blockProperties.getBlockRendererProperties().getStringProperty(ROW_ODD_VA);
                        if (valueVA != null && valueVA.length() > 0)
                        {
                            oddVA = valueVA;
                        }
                        String evenVA = "default_all";
                        valueVA = blockProperties.getBlockRendererProperties().getStringProperty(ROW_EVEN_VA);
                        if (valueVA != null && valueVA.length() > 0)
                        {
                            evenVA = valueVA;
                        }
                        int rowid = 0;
                        for (EJDataRecord record : records)
                        {
                            String trDef = String.format("<tr style=\"height: %spx\" recid=\"%s\" >", String.valueOf(charHeight), String.valueOf(getDisplayedRecordNumber(record)));
                            rowid++;
                            if (lastRowSpan > 1)
                            {
                                for (int i = 1; i < lastRowSpan; i++)
                                {
                                    builder.append(trDef).append("</tr>");

                                }
                                lastRowSpan = 0;
                            }
                            builder.append(trDef);

                            if (rowSelection)
                            {
                                builder.append(selectionTD);
                            }
                            for (EJCoreMainScreenItemProperties item : _items)
                            {
                                if (!item.isVisible())
                                    continue;
                                String styleClass = (rowid % 2) != 0 ? oddVA : evenVA;

                                String actionDef = null;
                                String alignment = null;
                                float width = -1;

                                ColumnLabelProvider columnLabelProvider = _itemLabelProviders.get(item.getReferencedItemName());

                                EJScreenItemController screenItem = _block.getScreenItem(EJScreenType.MAIN, item.getReferencedItemName());
                                EJCoreVisualAttributeProperties iva = screenItem.getManagedItemRenderer().getVisualAttributeProperties();
                                if (iva != null)
                                {
                                    styleClass = iva.getName();
                                }

                                EJFrameworkExtensionProperties rendererProperties = item.getReferencedItemProperties().getItemRendererProperties();
                                EJCoreVisualAttributeProperties diva = record.getItem(item.getReferencedItemName()).getVisualAttribute();
                                if (diva != null)
                                {
                                    styleClass = diva.getName();
                                }
                                builder.append(String.format("<td class=\"%s\" ", styleClass));
                                if (tdStyle != null)
                                {
                                    builder.append(String.format(" style=\'%s\'", tdStyle));
                                }

                                EJFrameworkExtensionProperties extentionProperties = item.getBlockRendererRequiredProperties();
                                if (width == -1)
                                {
                                    width = extentionProperties.getIntProperty(DISPLAY_WIDTH_PROPERTY, 0);
                                }

                                String action = extentionProperties.getStringProperty(CELL_ACTION_COMMAND);
                                if (action == null || action.length() == 0)
                                {
                                    action = item.getActionCommand();
                                }
                                if (action != null && action.length() > 0)
                                {
                                    actionDef = String.format("em='eaction' earg='%s , %s' ", action, String.valueOf(getDisplayedRecordNumber(record)));

                                }

                                if (width > 0)
                                {
                                    Font font = columnLabelProvider.getFont(new Object());

                                    if (font == null)
                                        font = _browser.getFont();
                                    if (font != null)
                                    {
                                        float avgCharWidth = RWTUtils.getAvgCharWidth(font);
                                        if (avgCharWidth > 0)
                                        {
                                            if (width != 1)
                                            {
                                                // add +1 padding
                                                width = ((int) (((width + 1) * avgCharWidth)));
                                            }
                                        }
                                    }

                                    builder.append(String.format(" width=%s ", width));
                                }
                                if (alignment == null)
                                {
                                    String alignmentProperty = rendererProperties.getStringProperty(PROPERTY_ALIGNMENT);
                                    if (alignmentProperty == null)
                                    {
                                        alignmentProperty = rendererProperties.getStringProperty("ALLIGNMENT");
                                    }
                                    alignment = getComponentAlignment(alignmentProperty);

                                }
                                if (alignment != null)
                                {
                                    builder.append(String.format(" align=\'%s\'", alignment));
                                }
                                final String caseProperty = getComponentCase(rendererProperties.getStringProperty(PROPERTY_CASE));

                                builder.append(String.format(" font style=\'%s\'", caseProperty));
                                if (item.getHint() != null && !item.getHint().isEmpty())
                                {
                                    builder.append(String.format(" title=\'%s\'", item.getHint()));
                                }
                                builder.append(">");

                                String text = columnLabelProvider instanceof HtmlBaseColumnLabelProvider ? ((HtmlBaseColumnLabelProvider) columnLabelProvider).getSrcText(record) : columnLabelProvider.getText(record);

                                boolean htmlformatting = extentionProperties.getBooleanProperty(ENABLE_MARKUP, false) || rendererProperties.getBooleanProperty("HTML_FORMAT", false);
                                if (!htmlformatting)
                                    text = ignoreHtml(text);

                                if (actionDef != null && text != null && text.length() > 0)
                                {
                                    builder.append(String.format("<ejl><u %s class=\"%s, %s\"  ", "style=\"line-height: 100%;cursor: pointer; cursor: hand;\"", ("default_all".equals(styleClass) ? "default_link_fg" : "default_link"), styleClass));
                                    builder.append(actionDef).append(">");
                                }

                                Image image = columnLabelProvider instanceof HtmlBaseColumnLabelProvider ? ((HtmlBaseColumnLabelProvider) columnLabelProvider).getSrcImage(record) : columnLabelProvider.getImage(record);
                                if (image != null)
                                {
                                    if (actionDef == null)
                                    {
                                        builder.append("<img src=\"");

                                        builder.append(ImageFactory.getImagePath(image));

                                        builder.append("\"");
                                        builder.append(String.format(" class=\"default %s\"  >", styleClass));
                                    }
                                    else

                                    {
                                        builder.append("<ejl><img src=\"");
                                        builder.append(ImageFactory.getImagePath(image));
                                        builder.append("\"");
                                        builder.append(String.format("style=\"cursor: hand;\" class=\"%s \" %s  > </ejl>", styleClass, actionDef));
                                    }
                                }
                                // builder.append(String.format("<p
                                // class=\"default %s\">",
                                // styleClass));

                                // if(textSelection && actionDef == null &&
                                // !htmlformatting)
                                // {
                                // builder.append("<input value=\"");
                                // builder.append(text);
                                // builder.append("\" ");
                                // builder.append("style=\"border:none;\"");
                                // builder.append(" readonly />");
                                // }
                                // else
                                builder.append(text);
                                builder.append("</td>");
                            }
                            builder.append("</tr>");
                        }

                    }
                    else
                    {
                        String trDef = String.format("<tr style=\"height: %spx\"  >", String.valueOf(charHeight));
                        builder.append(trDef);
                        for (EJCoreMainScreenItemProperties item : _items)
                        {
                            String padding = tdStyle;
                            float width = -1;

                            ColumnLabelProvider columnLabelProvider = _itemLabelProviders.get(item.getReferencedItemName());

                            builder.append(String.format("<td class=\"%s\" ", "default_all"));
                            if (padding != null)
                            {
                                builder.append(String.format(" style=\'%s\'", padding));
                            }

                            EJFrameworkExtensionProperties extentionProperties = item.getBlockRendererRequiredProperties();
                            if (width == -1)
                            {
                                width = extentionProperties.getIntProperty(DISPLAY_WIDTH_PROPERTY, 0);
                            }

                            if (width > 0)
                            {
                                Font font = columnLabelProvider.getFont(new Object());

                                if (font == null)
                                    font = _browser.getFont();
                                if (font != null)
                                {
                                    float avgCharWidth = RWTUtils.getAvgCharWidth(font);
                                    if (avgCharWidth > 0)
                                    {
                                        if (width != 1)
                                        {
                                            // add +1 padding
                                            width = ((int) (((width + 1) * avgCharWidth)));
                                        }
                                    }
                                }
                                builder.append(String.format(" width=%s ", width));
                            }

                            builder.append(">");
                            builder.append("</td>");

                        }
                        builder.append("</tr>");
                    }
                }
                builder.append("</tbody></table>");
            }
            builder.append("</div>");
        }
        String html = builder.toString();
        if (_browser.getText() == null || (!html.equals(_browser.getText())))
        {
            _browser.setText(html);
            if (currentRec != null)
            {
                _browser.setSelection(String.valueOf(getDisplayedRecordNumber(currentRec)));
            }
            LOGGER.debug(html);
        }

    }

    private String getComponentAlignment(final String alignmentProperty)
    {
        String align = "left";
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(PROPERTY_ALIGNMENT_JUSTIFY))
            {
                align = "justify";
            }
            else if (alignmentProperty.equals(PROPERTY_ALIGNMENT_RIGHT))
            {
                align = "right";
            }
            else if (alignmentProperty.equals(PROPERTY_ALIGNMENT_CENTER))
            {
                align = "center";
            }
            else if (alignmentProperty.equals(PROPERTY_ALIGNMENT_CHAR))
            {
                align = "char";
            }
        }
        return align;
    }

    static String ignoreHtml(String text)
    {
        if (text == null || text.isEmpty())
            return text;

        return replaceEach(text, new String[] { "&", "<", ">", "\"", "'", "/" }, new String[] { "&amp;", "&lt;", "&gt;", "&quot;", "&#x27;", "&#x2F;" }, false, 0);
    }

    private static String replaceEach(String text, String[] searchList, String[] replacementList, boolean repeat, int timeToLive)
    {

        if (text == null || text.length() == 0 || searchList == null || searchList.length == 0 || replacementList == null || replacementList.length == 0)
        {
            return text;
        }

        // if recursing, this shouldnt be less than 0
        if (timeToLive < 0)
        {
            throw new IllegalStateException("TimeToLive of " + timeToLive + " is less than 0: " + text);
        }

        int searchLength = searchList.length;
        int replacementLength = replacementList.length;

        // make sure lengths are ok, these need to be equal
        if (searchLength != replacementLength)
        {
            throw new IllegalArgumentException("Search and Replace array lengths don't match: " + searchLength + " vs " + replacementLength);
        }

        // keep track of which still have matches
        boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];

        // index on index that the match was found
        int textIndex = -1;
        int replaceIndex = -1;
        int tempIndex = -1;

        // index of replace array that will replace the search string found
        // NOTE: logic duplicated below START
        for (int i = 0; i < searchLength; i++)
        {
            if (noMoreMatchesForReplIndex[i] || searchList[i] == null || searchList[i].length() == 0 || replacementList[i] == null)
            {
                continue;
            }
            tempIndex = text.indexOf(searchList[i]);

            // see if we need to keep searching for this
            if (tempIndex == -1)
            {
                noMoreMatchesForReplIndex[i] = true;
            }
            else
            {
                if (textIndex == -1 || tempIndex < textIndex)
                {
                    textIndex = tempIndex;
                    replaceIndex = i;
                }
            }
        }
        // NOTE: logic mostly below END

        // no search strings found, we are done
        if (textIndex == -1)
        {
            return text;
        }

        int start = 0;

        // get a good guess on the size of the result buffer so it doesnt have
        // to double if it goes over a bit
        int increase = 0;

        // count the replacement text elements that are larger than their
        // corresponding text being replaced
        for (int i = 0; i < searchList.length; i++)
        {
            int greater = replacementList[i].length() - searchList[i].length();
            if (greater > 0)
            {
                increase += 3 * greater; // assume 3 matches
            }
        }
        // have upper-bound at 20% increase, then let Java take over
        increase = Math.min(increase, text.length() / 5);

        StringBuffer buf = new StringBuffer(text.length() + increase);

        while (textIndex != -1)
        {

            for (int i = start; i < textIndex; i++)
            {
                buf.append(text.charAt(i));
            }
            buf.append(replacementList[replaceIndex]);

            start = textIndex + searchList[replaceIndex].length();

            textIndex = -1;
            replaceIndex = -1;
            tempIndex = -1;
            // find the next earliest match
            // NOTE: logic mostly duplicated above START
            for (int i = 0; i < searchLength; i++)
            {
                if (noMoreMatchesForReplIndex[i] || searchList[i] == null || searchList[i].length() == 0 || replacementList[i] == null)
                {
                    continue;
                }
                tempIndex = text.indexOf(searchList[i], start);

                // see if we need to keep searching for this
                if (tempIndex == -1)
                {
                    noMoreMatchesForReplIndex[i] = true;
                }
                else
                {
                    if (textIndex == -1 || tempIndex < textIndex)
                    {
                        textIndex = tempIndex;
                        replaceIndex = i;
                    }
                }
            }
            // NOTE: logic duplicated above END

        }
        int textLength = text.length();
        for (int i = start; i < textLength; i++)
        {
            buf.append(text.charAt(i));
        }
        String result = buf.toString();
        if (!repeat)
        {
            return result;
        }

        return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
    }

    private String getComponentCase(final String caseProperty)
    {
        String caze = "text-transform: none;";
        if (caseProperty != null && caseProperty.trim().length() > 0)
        {

            if (caseProperty.equals(PROPERTY_CASE_LOWER))
            {
                caze = "text-transform: lowercase;";
            }
            else if (caseProperty.equals(PROPERTY_CASE_UPPER))
            {
                caze = "text-transform: uppercase;";
            }
            else if (caseProperty.equals(PROPERTY_CASE_CAPITALIZE))
            {
                caze = "text-transform: capitalize;";
            }

        }

        return caze;
    }

    public static class VACSSServiceHandler implements ServiceHandler
    {
        public static final String       STYLE_DEF       = getStyleDef();
        public final static String       SERVICE_HANDLER = "TMVACSSServiceHandler";
        final Map<String, BufferedImage> map             = new HashMap<String, BufferedImage>();

        @Override
        public void service(HttpServletRequest arg0, HttpServletResponse arg1) throws IOException, ServletException
        {

            HttpServletResponse response = arg1;
            response.setContentType("text/css");
            ServletOutputStream out = response.getOutputStream();
            out.write(STYLE_DEF.getBytes(Charset.forName("UTF-8")));

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
            LOGGER.debug(actionID);
            _block.executeActionCommand(actionID, EJScreenType.MAIN);
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

    private String createImageUrl(String string)
    {
        return ImageFactory.getImagePath(EJRWTImageRetriever.get(string));
    }

    private static class SortInfo
    {
        String id         = UUID.randomUUID().toString();
        String columnName = null;

        enum DIRECTION
        {
            NONE, ASC, DESC
        }

        DIRECTION direction = DIRECTION.NONE;
    }

    public static void main(String[] args)
    {
        System.out.println(ignoreHtml("<html/>"));
    }

    @Override
    public void setFilter(String filter)
    {
        this.filter = filter;
        if (_filteredContentProvider != null)
        {
            _filteredContentProvider.setFilter(filter);
            createHTML();
        }

    }

    @Override
    public String getFilter()
    {

        return _filteredContentProvider != null ? _filteredContentProvider.getFilter() : null;

    }
    
    @Override
    public int getPreferredCanvasHeight()
    {
        return _browser.getScrollHeight();
    }
    
    @Override
    public int getPreferredCanvasWidth()
    {
         return 0;//use rwt default
    }

}
