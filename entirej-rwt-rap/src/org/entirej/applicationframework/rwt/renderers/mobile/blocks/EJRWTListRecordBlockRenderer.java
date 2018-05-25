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
package org.entirej.applicationframework.rwt.renderers.mobile.blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTDeleteAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTInsertAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTQueryAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTUpdateAction;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppBlockRenderer;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.blocks.definition.interfaces.EJRWTSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.blocks.definition.interfaces.EJRWTTreeBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.definition.interfaces.EJRWTTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTInsertScreenRenderer;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTQueryScreenRenderer;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTUpdateScreenRenderer;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractFilteredTable;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractFilteredTable.FilteredContentProvider;
import org.entirej.applicationframework.rwt.table.EJRWTTableViewerColumnFactory;
import org.entirej.applicationframework.rwt.utils.EJRWTKeysUtil;
import org.entirej.applicationframework.rwt.utils.EJRWTKeysUtil.KeyInfo;
import org.entirej.framework.core.EJForm;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.data.controllers.EJQuestion;
import org.entirej.framework.core.enumerations.EJManagedBlockProperty;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJQuestionButton;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
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
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.interfaces.EJInsertScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJUpdateScreenRenderer;

public class EJRWTListRecordBlockRenderer implements EJRWTAppBlockRenderer, KeyListener
{
    final FormToolkit                  toolkit          = new FormToolkit(Display.getDefault());
    private static final long          serialVersionUID = -1300484097701416526L;

    private boolean                    _isFocused       = false;
    private EJEditableBlockController  _block;
    private EJRWTEntireJGridPane       _mainPane;
    private TableViewer                _tableViewer;
    private EJRWTQueryScreenRenderer   _queryScreenRenderer;
    private EJRWTInsertScreenRenderer  _insertScreenRenderer;
    private EJRWTUpdateScreenRenderer  _updateScreenRenderer;

    List<String>                       actionkeys       = new ArrayList<String>();
    private Map<KeyInfo, String>       actionInfoMap    = new HashMap<EJRWTKeysUtil.KeyInfo, String>();

    private FilteredContentProvider    filteredContentProvider;

    private List<EJDataRecord>         tableBaseRecords = new ArrayList<EJDataRecord>();

    private String                     filterText;
    private EJRWTAbstractFilteredTable filterTree;
    private String                     defaultMessage;

    private Display                    dispaly          = Display.getDefault();

    protected void clearFilter()
    {
        if (filteredContentProvider != null)
        {
            filteredContentProvider.setFilter(null);
        }
    }

    protected void applyFileter()
    {
        if (filterText != null && !filterText.isEmpty())
        {
            filterTree.setFilterText(filterText);
            filterTree.filter(filterText);
        }
    }

    public void refreshBlockProperty(EJManagedBlockProperty managedBlockPropertyType)
    {
    }

    public void refreshBlockRendererProperty(String propertyName)
    {
    }

    public void executingQuery()
    {
        // no impl
    }

    public void refreshItemProperty(String itemName, EJManagedScreenProperty managedItemPropertyType, EJDataRecord record)
    {
        if (EJManagedScreenProperty.ITEM_INSTANCE_VISUAL_ATTRIBUTE.equals(managedItemPropertyType))
        {
            EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemName);
            if (item != null)
            {
                if (record == null || _tableViewer == null)
                    return;
                if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
                    _tableViewer.refresh(record);
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

                // int recNum = _dataView.getRecordNumber(record);
                // int colNum = _dataView.findColumn(itemName);
                // _dataView.fireTableCellUpdated(recNum - 1, colNum);
            }
        }
        else if (EJManagedScreenProperty.SCREEN_ITEM_VISUAL_ATTRIBUTE.equals(managedItemPropertyType))
        {
            EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemName);
            if (item != null)
            {
                item.getManagedItemRenderer().getUnmanagedRenderer().setVisualAttribute(item.getProperties().getVisualAttributeProperties());
                if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
                    _tableViewer.setInput(new Object());
            }
        }
    }

    public void refreshItemRendererProperty(String itemName, String propertyName)
    {
    }

    public Composite getGuiComponent()
    {
        return _mainPane;
    }

    public EJQueryScreenRenderer getQueryScreenRenderer()
    {
        return _queryScreenRenderer;
    }

    public EJInsertScreenRenderer getInsertScreenRenderer()
    {
        return _insertScreenRenderer;
    }

    public EJUpdateScreenRenderer getUpdateScreenRenderer()
    {
        return _updateScreenRenderer;
    }

    public void initialiseRenderer(EJEditableBlockController block)
    {
        _block = block;
        _queryScreenRenderer = new EJRWTQueryScreenRenderer();
        _insertScreenRenderer = new EJRWTInsertScreenRenderer();
        _updateScreenRenderer = new EJRWTUpdateScreenRenderer();
    }

    public void blockCleared()
    {
        dispaly.asyncExec(() -> {
            if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
            {
                clearFilter();
                _tableViewer.setInput(new Object());
                applyFileter();
            }
            notifyStatus();

        });

    }

    public void synchronize()
    {
    }

    protected void notifyStatus()
    {
        if (hasFocus())
        {
            EJRWTApplicationManager mng = (EJRWTApplicationManager) _block.getFrameworkManager().getApplicationManager();
            int displayedRecordCount = getDisplayedRecordCount();
            if (mng.getStatusbar() != null)
            {
                EJDataRecord focusedRecord = getFocusedRecord();
                int displayedRecordNumber = getDisplayedRecordNumber(focusedRecord);
                if (displayedRecordCount > 0 && displayedRecordNumber == -1)
                {
                    mng.getStatusbar().setStatus2("");
                }
                else
                {
                    mng.getStatusbar().setStatus2(String.format("%s of %s", String.valueOf(displayedRecordNumber + 1), String.valueOf(displayedRecordCount)));
                }
            }

        }
        else
        {
            EJRWTApplicationManager mng = (EJRWTApplicationManager) _block.getFrameworkManager().getApplicationManager();
            if (mng.getStatusbar() != null)
            {
                mng.getStatusbar().setStatus2("");

            }
        }
    }

    public void detailBlocksCleared()
    {
        // TODO Auto-generated method stub
    }

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

    public boolean isCurrentRecordDirty()
    {
        return false;
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

    public void queryExecuted()
    {

        dispaly.asyncExec(() -> {
            if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
            {
                clearFilter();
                _tableViewer.setInput(new Object());
                applyFileter();
            }
            selectRow(0);

        });

    }

    public void pageRetrieved()
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            clearFilter();
            _tableViewer.setInput(new Object());
            applyFileter();
        }
        selectRow(0);
    }

    public void recordDeleted(int dataBlockRecordNumber)
    {
        EJDataRecord recordAt = getRecordAt(dataBlockRecordNumber > 1 ? dataBlockRecordNumber - 2 : 0);

        if (recordAt == null)

        {
            recordAt = getLastRecord();
        }
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            clearFilter();
            _tableViewer.setInput(new Object());
            applyFileter();

        }
        if (recordAt != null)
            recordSelected(recordAt);

    }

    public void recordInserted(EJDataRecord record)
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            clearFilter();
            _tableViewer.setInput(new Object());
            applyFileter();
        }
        recordSelected(record);
    }

    public void refreshAfterChange(EJDataRecord record)
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            _tableViewer.refresh(record);
        }
    }

    public void recordSelected(EJDataRecord record)
    {
        dispaly.asyncExec(() -> {
            if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
            {
                _tableViewer.setSelection(record != null ? new StructuredSelection(record) : new StructuredSelection(), true);
            }
            notifyStatus();

        });

    }

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
        notifyStatus();
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
        /**
         * FIXME: This needs to be implemented
         */

        /*
         * if (_showFocusedBorder) { if (focused) {
         * _mainPane.setBorder(BorderFactory.createLineBorder(Color.blue)); }
         * else if (_hideTableBorder) {
         * _mainPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1)); }
         * else { _mainPane.setBorder(_standardBorder); } }
         */
    }

    public void setFocusToItem(EJScreenItemController item)
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            _tableViewer.getTable().forceFocus();
        }
    }

    public void gainFocus()
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            _tableViewer.getTable().forceFocus();
        }
        setHasFocus(true);

    }

    public EJDataRecord getFocusedRecord()
    {
        EJDataRecord _focusedRecord = null;

        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            ISelection selection = _tableViewer.getSelection();
            if (selection instanceof IStructuredSelection)
            {
                IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                Object firstElement = structuredSelection.getFirstElement();
                if (firstElement instanceof EJDataRecord)
                {
                    _focusedRecord = (EJDataRecord) firstElement;
                }
            }
        }
        return _focusedRecord;
    }

    public int getDisplayedRecordNumber(EJDataRecord record)
    {
        if (record == null)
        {
            return -1;
        }

        return tableBaseRecords.indexOf(record);
    }

    public int getDisplayedRecordCount()
    {

        // Indicates the number of records that are available within the View.
        // the number depends on the filters set on the table!
        return tableBaseRecords.size();
    }

    public EJDataRecord getFirstRecord()
    {
        return getRecordAt(0);
    }

    public EJDataRecord getLastRecord()
    {
        return getRecordAt(getDisplayedRecordCount() - 1);
    }

    public EJDataRecord getRecordAt(int displayedRecordNumber)
    {

        if (displayedRecordNumber > -1 && displayedRecordNumber < getDisplayedRecordCount())
        {

            return tableBaseRecords.get(displayedRecordNumber);
        }

        return null;
    }

    public EJDataRecord getRecordAfter(EJDataRecord record)
    {
        int viewIndex = getDisplayedRecordNumber(record);
        if (-1 < viewIndex)
        {
            return getRecordAt(viewIndex + 1);
        }
        return null;
    }

    public EJDataRecord getRecordBefore(EJDataRecord record)
    {
        int viewIndex = getDisplayedRecordNumber(record);
        if (-1 < viewIndex)
        {
            return getRecordAt(viewIndex - 1);
        }
        return null;
    }

    public void selectRow(int selectedRow)
    {

        if (_tableViewer != null && !_tableViewer.getTable().isDisposed() && getDisplayedRecordCount() > selectedRow)
        {
            _tableViewer.setSelection(new StructuredSelection(getRecordAt(selectedRow)), true);
        }
        notifyStatus();
    }

    public final EJInternalEditableBlock getBlock()
    {
        return _block.getBlock();
    }

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
        // blockCanvas.cleanLayoutVertical();
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
            gridData.minimumWidth = mainScreenProperties.getWidth();
        if (gridData.grabExcessVerticalSpace)
            gridData.minimumHeight = mainScreenProperties.getHeight();
        EJFrameworkExtensionProperties rendererProp = blockProperties.getBlockRendererProperties();
        blockCanvas.setLayoutData(gridData);

        EJFrameworkExtensionProperties sectionProperties = null;
        if (rendererProp != null)
        {
            sectionProperties = rendererProp.getPropertyGroup(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR);
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
                section.setText(title);
            EJRWTImageRetriever.getGraphicsProvider().rendererSection(section);
            if (mainScreenProperties.getDisplayFrame())
            {

                Group group = new Group(section, SWT.NONE);
                group.setLayout(new FillLayout());
                group.setLayoutData(gridData);
                hookKeyListener(group);
                String frameTitle = mainScreenProperties.getFrameTitle();
                if (frameTitle != null && frameTitle.length() > 0)
                {
                    group.setText(frameTitle);
                }
                _mainPane = new EJRWTEntireJGridPane(group, 1);
                section.setClient(group);

            }
            else
            {
                _mainPane = new EJRWTEntireJGridPane(section, 1);
                _mainPane.setLayoutData(gridData);
                _mainPane.cleanLayoutHorizontal();
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
                    public void widgetDisposed(DisposeEvent e)
                    {
                        if ((handCursor != null) && (handCursor.isDisposed() == false))
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
                            action.setText(actionName);
                        if (actionTooltip != null)
                            action.setDescription(actionTooltip);
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
            if (mainScreenProperties.getDisplayFrame())
            {

                Group group = new Group(blockCanvas, SWT.NONE);
                group.setLayout(new FillLayout());
                group.setLayoutData(gridData);
                hookKeyListener(group);
                String frameTitle = mainScreenProperties.getFrameTitle();
                if (frameTitle != null && frameTitle.length() > 0)
                {
                    group.setText(frameTitle);
                }
                _mainPane = new EJRWTEntireJGridPane(group, 1);

            }
            else
            {
                _mainPane = new EJRWTEntireJGridPane(blockCanvas, 1);
                _mainPane.setLayoutData(gridData);
                _mainPane.cleanLayoutHorizontal();
            }
        }

        hookKeyListener(_mainPane);
        int style = SWT.VIRTUAL | SWT.V_SCROLL;

        if (!rendererProp.getBooleanProperty(EJRWTListRecordBlockDefinitionProperties.HIDE_TABLE_BORDER, false))
            style = style | SWT.BORDER;

        if (rendererProp.getBooleanProperty(EJRWTListRecordBlockDefinitionProperties.ROW_SELECTION_PROPERTY, true))
            style = style | SWT.FULL_SELECTION;
        else
            style = style | SWT.HIDE_SELECTION;
        Collection<EJItemGroupProperties> allItemGroupProperties = _block.getProperties().getScreenItemGroupContainer(EJScreenType.MAIN).getAllItemGroupProperties();
        final Table table;
        final boolean hideSelection = (style & SWT.HIDE_SELECTION) != 0;
        // final EJRWTAbstractFilteredTable filterTree;
        if (rendererProp.getBooleanProperty(EJRWTTreeBlockDefinitionProperties.FILTER, false))
        {
            if (allItemGroupProperties.size() > 0)
            {
                EJItemGroupProperties displayProperties = allItemGroupProperties.iterator().next();
                if (displayProperties.dispayGroupFrame())
                {
                    Group group = new Group(_mainPane, SWT.NONE);
                    group.setLayout(new FillLayout());
                    if (displayProperties.getFrameTitle() != null && displayProperties.getFrameTitle().length() > 0)
                        group.setText(displayProperties.getFrameTitle());
                    group.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

                    filterTree = new EJRWTAbstractFilteredTable(group, style)
                    {
                        @Override
                        public void filter(String filter)
                        {
                            if (filteredContentProvider != null && ((filter == null && filteredContentProvider.getFilter() != null) || !filter.equals(filteredContentProvider.getFilter())))
                            {
                                filteredContentProvider.setFilter(filter);
                                filterText = filter;
                                getViewer().setInput(filter);
                                notifyStatus();
                            }
                        }

                        @Override
                        protected TableViewer doCreateTableViewer(Composite parent, int style)
                        {
                            return _tableViewer = new TableViewer(parent)
                            {

                                @Override
                                public void setSelection(ISelection selection)
                                {
                                    if (hideSelection)
                                        selection = new StructuredSelection();
                                    super.setSelection(selection);
                                }

                                @Override
                                public void setSelection(ISelection selection, boolean reveal)
                                {
                                    if (hideSelection)
                                        selection = new StructuredSelection();
                                    super.setSelection(selection, reveal);
                                }
                            };
                        }
                    };
                }
                else
                {

                    filterTree = new EJRWTAbstractFilteredTable(_mainPane, style)
                    {
                        @Override
                        public void filter(String filter)
                        {
                            if (filteredContentProvider != null && ((filter == null && filteredContentProvider.getFilter() != null) || !filter.equals(filteredContentProvider.getFilter())))
                            {
                                filteredContentProvider.setFilter(filter);
                                filterText = filter;
                                getViewer().setInput(filter);
                                notifyStatus();
                            }
                        }

                        @Override
                        protected TableViewer doCreateTableViewer(Composite parent, int style)
                        {
                            return _tableViewer = new TableViewer(parent)
                            {

                                @Override
                                public void setSelection(ISelection selection)
                                {
                                    if (hideSelection)
                                        selection = new StructuredSelection();
                                    super.setSelection(selection);
                                }

                                @Override
                                public void setSelection(ISelection selection, boolean reveal)
                                {
                                    if (hideSelection)
                                        selection = new StructuredSelection();
                                    super.setSelection(selection, reveal);
                                }
                            };
                        }

                    };
                    filterTree.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                }
            }
            else
            {
                filterTree = new EJRWTAbstractFilteredTable(_mainPane, style)
                {
                    @Override
                    public void filter(String filter)
                    {
                        if (filteredContentProvider != null && ((filter == null && filteredContentProvider.getFilter() != null) || !filter.equals(filteredContentProvider.getFilter())))
                        {
                            filteredContentProvider.setFilter(filter);
                            filterText = filter;
                            getViewer().setInput(filter);
                            notifyStatus();
                        }
                    }

                    @Override
                    protected TableViewer doCreateTableViewer(Composite parent, int style)
                    {
                        return _tableViewer = new TableViewer(parent)
                        {

                            @Override
                            public void setSelection(ISelection selection)
                            {
                                if (hideSelection)
                                    selection = new StructuredSelection();
                                super.setSelection(selection);
                            }

                            @Override
                            public void setSelection(ISelection selection, boolean reveal)
                            {
                                if (hideSelection)
                                    selection = new StructuredSelection();
                                super.setSelection(selection, reveal);
                            }
                        };
                    }
                };

                filterTree.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
            }
            table = (_tableViewer = filterTree.getViewer()).getTable();

            defaultMessage = rendererProp.getStringProperty(EJRWTTextItemRendererDefinitionProperties.PROPERTY_MESSAGE);
            if (defaultMessage != null)
                filterTree.getFilterControl().setMessage(defaultMessage);
        }
        else
        {
            filterTree = null;
            if (allItemGroupProperties.size() > 0)
            {
                EJItemGroupProperties displayProperties = allItemGroupProperties.iterator().next();
                if (displayProperties.dispayGroupFrame())
                {
                    Group group = new Group(_mainPane, SWT.NONE);
                    group.setLayout(new FillLayout());
                    if (displayProperties.getFrameTitle() != null && displayProperties.getFrameTitle().length() > 0)
                        group.setText(displayProperties.getFrameTitle());
                    group.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                    table = new Table(group, style);
                }
                else
                {
                    table = new Table(_mainPane, style);

                    table.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                }
            }
            else
            {
                table = new Table(_mainPane, style);

                table.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
            }

            _tableViewer = new TableViewer(table)
            {

                @Override
                public void setSelection(ISelection selection)
                {
                    if (hideSelection)
                        selection = new StructuredSelection();
                    super.setSelection(selection);
                }

                @Override
                public void setSelection(ISelection selection, boolean reveal)
                {
                    if (hideSelection)
                        selection = new StructuredSelection();
                    super.setSelection(selection, reveal);
                }
            };
        }

        table.setLinesVisible(false);
        table.setHeaderVisible(false);
        Control[] children = table.getChildren();
        for (Control control : children)
        {
            hookKeyListener(control);
        }
        hookKeyListener(table);

        EJRWTTableViewerColumnFactory factory = new EJRWTTableViewerColumnFactory(_tableViewer);
        ColumnViewerToolTipSupport.enableFor(_tableViewer);

        final List<ColumnLabelProvider> nodeTextProviders = new ArrayList<ColumnLabelProvider>();
        final Map<ColumnLabelProvider, EJScreenItemProperties> nodeTextProvidersMap = new HashMap<ColumnLabelProvider, EJScreenItemProperties>();

        for (EJItemGroupProperties groupProperties : allItemGroupProperties)
        {
            Collection<EJScreenItemProperties> itemProperties = groupProperties.getAllItemProperties();

            for (EJScreenItemProperties screenItemProperties : itemProperties)
            {
                EJCoreMainScreenItemProperties mainScreenItemProperties = (EJCoreMainScreenItemProperties) screenItemProperties;
                ColumnLabelProvider screenItem = createScreenItem(factory, mainScreenItemProperties);
                if (screenItem != null)
                {
                    nodeTextProviders.add(screenItem);
                    nodeTextProvidersMap.put(screenItem, screenItemProperties);
                }
            }
        }

        if (!nodeTextProviders.isEmpty())
        {
            table.setData(EJ_RWT.MARKUP_ENABLED, Boolean.TRUE);

            int height = rendererProp.getIntProperty(EJRWTListRecordBlockDefinitionProperties.ROW_HEIGHT, -1);

            if (height == -1 && nodeTextProviders.size() > 1)
            {
                table.setData(EJ_RWT.CUSTOM_ITEM_HEIGHT, Integer.valueOf(nodeTextProviders.size() * 20));
            }
            else if (height > 0)
            {
                table.setData(EJ_RWT.CUSTOM_ITEM_HEIGHT, Integer.valueOf(height));
            }

            // add double click action
            final String doubleClickActionCommand = rendererProp.getStringProperty(EJRWTListRecordBlockDefinitionProperties.CLICK_ACTION_COMMAND);
            final boolean hasAction = doubleClickActionCommand != null && doubleClickActionCommand.length() != 0;
            if (hasAction)
            {
                _tableViewer.addDoubleClickListener(new IDoubleClickListener()
                {

                    public void doubleClick(DoubleClickEvent arg0)
                    {
                        _block.executeActionCommand(doubleClickActionCommand, EJScreenType.MAIN);
                    }
                });
            }

            final TableViewerColumn dataColumn = factory.createColumn("HTML", 500, new ColumnLabelProvider()
            {

                public String toHex(int r, int g, int b)
                {
                    return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
                }

                private String toBrowserHexValue(int number)
                {
                    StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
                    while (builder.length() < 2)
                    {
                        builder.append("0");
                    }
                    return builder.toString().toUpperCase();
                }

                @Override
                public String getText(Object element)
                {

                    StringBuilder builder = new StringBuilder();
                    boolean addBr = false;
                    for (ColumnLabelProvider labelProvider : nodeTextProviders)
                    {
                        if (addBr)
                        {
                            builder.append("<br/>");
                        }

                        boolean added = false;

                        EJScreenItemProperties item = nodeTextProvidersMap.get(labelProvider);

                        if (item != null && item.getLabel() != null && item.getLabel().trim().length() > 0)
                        {
                            builder.append(item.getLabel());
                            builder.append(" : ");
                            added = true;
                        }

                        Image image = labelProvider.getImage(element);
                        if (image != null)
                        {
                            String imagePath = ImageFactory.getImagePath(image);
                            if (imagePath != null)
                            {
                                builder.append("<img src=\"");
                                builder.append(imagePath);
                                Rectangle bounds = image.getBounds();
                                builder.append("\" style=\"vertical-align:middle;\" ");
                                builder.append(" width=\"");
                                builder.append(bounds.width);
                                builder.append("\" ");
                                builder.append(" height=\"");
                                builder.append(bounds.height);
                                builder.append("\"/>");
                                added = true;
                            }
                        }

                        String text = labelProvider.getText(element);
                        if (text != null && text.length() != 0)
                        {

                            Font vaFont = labelProvider.getFont(element);
                            builder.append("<span style=\"");
                            if (vaFont != null && vaFont.getFontData().length > 0)
                            {
                                FontData fontData = vaFont.getFontData()[0];

                                if ((fontData.getStyle() & SWT.BOLD) != 0)
                                {
                                    builder.append("font-weight:bold;");
                                }
                                if ((fontData.getStyle() & SWT.ITALIC) != 0)
                                {
                                    builder.append("font-style:italic;");
                                }

                                builder.append("font-size:");
                                builder.append(fontData.getHeight());
                                builder.append("px;");

                                builder.append("font-family:");
                                builder.append(fontData.getName().replace('"', ' '));
                                builder.append(";");

                            }
                            Color background = labelProvider.getBackground(element);
                            if (background != null)
                            {
                                builder.append("background-color:");
                                builder.append(toHex(background.getRed(), background.getGreen(), background.getBlue()));
                                builder.append(";");
                            }
                            Color foreground = labelProvider.getForeground(element);
                            if (foreground != null)
                            {
                                builder.append("color:");
                                builder.append(toHex(foreground.getRed(), foreground.getGreen(), foreground.getBlue()));
                                builder.append(";");
                            }

                            builder.append("\">");
                            builder.append(text);
                            builder.append("</span>");
                            added = true;
                        }
                        if (added)
                        {
                            addBr = true;
                        }
                    }
                    return builder.toString();
                }
            });

            if (hasAction)
            {
                final Image arrow = EJRWTImageRetriever.get("icons/left-arrow.png");
                final TableViewerColumn actionColumn = factory.createColumn("HTML-ACTION", 40, new ColumnLabelProvider()
                {

                    @Override
                    public String getText(Object element)
                    {
                        return "";
                    }

                    @Override
                    public Image getImage(Object element)
                    {

                        return arrow;
                    }

                });
                actionColumn.getColumn().setAlignment(SWT.RIGHT);
                actionColumn.setEditingSupport(new EditingSupport(_tableViewer)
                {

                    protected void setValue(Object arg0, Object arg1)
                    {
                        // ignore

                    }

                    protected Object getValue(Object arg0)
                    {
                        // ignore
                        return null;
                    }

                    protected CellEditor getCellEditor(Object arg0)
                    {

                        return new CellEditor()
                        {

                            @Override
                            protected void doSetValue(Object arg0)
                            {
                                // ignore

                            }

                            @Override
                            protected void doSetFocus()
                            {
                                // ignore

                            }

                            @Override
                            protected Object doGetValue()
                            {
                                // ignore
                                return null;
                            }

                            @Override
                            protected Control createControl(Composite parent)
                            {
                                Label action = new Label(parent, SWT.NONE);
                                action.setImage(arrow);

                                return action;
                            }
                        };
                    }

                    protected boolean canEdit(Object arg0)
                    {
                        _block.executeActionCommand(doubleClickActionCommand, EJScreenType.MAIN);
                        return false;
                    }
                });
            }

            table.addControlListener(new ControlAdapter()
            {

                @Override
                public void controlResized(ControlEvent e)
                {
                    int offset = hasAction ? 50 : 10;
                    if (table.getSize().x > offset)
                        dataColumn.getColumn().setWidth(table.getSize().x - offset);
                }
            });
        }

        table.addFocusListener(new FocusListener()
        {

            public void focusLost(FocusEvent arg0)
            {
                setHasFocus(false);

            }

            public void focusGained(FocusEvent arg0)
            {
                setHasFocus(true);

            }
        });
        // setHasFocus(true);
        _mainPane.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseDown(MouseEvent arg0)
            {
                if (!table.isFocusControl())
                    setHasFocus(true);

            }

        });
        table.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseDown(MouseEvent arg0)
            {
                if (!table.isFocusControl())
                    setHasFocus(true);

            }

        });
        final EJBlockController blockController = _block;

        _tableViewer.setContentProvider(filteredContentProvider = new FilteredContentProvider()
        {

            boolean matchItem(EJDataRecord rec)
            {
                if (filter != null && filter.trim().length() > 0)
                {
                    for (ColumnLabelProvider filterTextProvider : nodeTextProviders)
                    {
                        String text = filterTextProvider.getText(rec);
                        if ((text != null) && text.toLowerCase().contains(filter.toLowerCase()))
                        {
                            return true;
                        }
                    }
                }

                return false;
            }

            public void inputChanged(Viewer arg0, Object arg1, Object arg2)
            {
                tableBaseRecords.clear();

                if (arg2 != null && arg2.equals(filter) && filter.trim().length() > 0)
                {
                    // filter

                    for (EJDataRecord record : _block.getBlock().getRecords())
                    {
                        if (matchItem(record))
                            tableBaseRecords.add(record);
                    }
                }
                else
                {
                    filter = null;
                    if (filterTree != null)
                        filterTree.clearText();
                    tableBaseRecords.addAll(_block.getBlock().getRecords());
                }
            }

            public void dispose()
            {
            }

            public Object[] getElements(Object arg0)
            {
                return tableBaseRecords.toArray();
            }
        });
        _tableViewer.setInput(new Object());
        selectRow(0);

        _tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
        {

            public void selectionChanged(SelectionChangedEvent arg0)
            {

                EJDataRecord focusedRecord = getFocusedRecord();
                if (focusedRecord != null)
                    _block.newRecordInstance(focusedRecord);
                notifyStatus();
            }
        });

    }

    private void addActionKeyinfo(String actionKey, String actionId)
    {
        if (actionKey != null && actionKey.trim().length() > 0)
        {
            try
            {

                KeyInfo keyInfo = EJRWTKeysUtil.toKeyInfo(actionKey);
                actionInfoMap.put(keyInfo, actionId);
                actionkeys.add(actionKey);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public ColumnLabelProvider createScreenItem(EJRWTTableViewerColumnFactory factory, EJCoreMainScreenItemProperties itemProps)
    {
        if (itemProps.isSpacerItem())
        {

            return null;
        }
        EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemProps.getReferencedItemName());
        EJManagedItemRendererWrapper renderer = item.getManagedItemRenderer();
        if (renderer != null)
        {
            EJRWTAppItemRenderer itemRenderer = (EJRWTAppItemRenderer) renderer.getUnmanagedRenderer();
            ColumnLabelProvider labelProvider = itemRenderer.createColumnLabelProvider(itemProps, item);

            if (labelProvider != null)
            {
                EJFrameworkExtensionProperties blockProperties = itemProps.getBlockRendererRequiredProperties();
                String visualAttribute = blockProperties.getStringProperty(EJRWTListRecordBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY);

                if (visualAttribute != null)
                {
                    EJCoreVisualAttributeProperties va = EJCoreProperties.getInstance().getVisualAttributesContainer().getVisualAttributeProperties(visualAttribute);
                    if (va != null)
                        itemRenderer.setInitialVisualAttribute(va);
                }
            }
            return labelProvider;

        }
        return null;
    }

    public void keyPressed(KeyEvent arg0)
    {

        // ignore
    }

    public void keyReleased(KeyEvent arg0)
    {
        int keyCode = arg0.keyCode;
        KeyInfo keyInfo = EJRWTKeysUtil.toKeyInfo(keyCode, (arg0.stateMask & SWT.SHIFT) != 0, (arg0.stateMask & SWT.CTRL) != 0, (arg0.stateMask & SWT.ALT) != 0);

        String actionID = actionInfoMap.get(keyInfo);
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
        List<String> subActions = new ArrayList<String>(actionkeys);
        Object data = control.getData(EJ_RWT.ACTIVE_KEYS);

        if (data != null)
        {
            String[] current = (String[]) data;
            for (String action : current)
            {
                if (subActions.contains(action))
                    continue;
                subActions.add(action);
            }
        }
        control.setData(EJ_RWT.ACTIVE_KEYS, subActions.toArray(new String[0]));
        control.addKeyListener(this);
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
    public void setFilter(String filter)
    {
        throw new IllegalStateException("not supported yet");
        // this.filterText = filter;
        // if(filterTree!=null)
        // {
        // filterTree.setFilterText(filter);
        // filterTree.filter(filter);
        // }

    }

    @Override
    public String getFilter()
    {
        throw new IllegalStateException("not supported yet");
        // return filterText;

    }

}
