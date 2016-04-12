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
package org.entirej.applicationframework.rwt.renderers.blocks;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
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
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTInsertScreenRenderer;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTQueryScreenRenderer;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTUpdateScreenRenderer;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractFilteredTree;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractFilteredTree.FilteredContentProvider;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.table.EJRWTTreeTableSortSelectionListener;
import org.entirej.applicationframework.rwt.table.EJRWTTreeTableViewerColumnFactory;
import org.entirej.applicationframework.rwt.utils.EJRWTKeysUtil;
import org.entirej.applicationframework.rwt.utils.EJRWTKeysUtil.KeyInfo;
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

public class EJRWTTreeTableRecordBlockRenderer implements EJRWTAppBlockRenderer, KeyListener
{
    final FormToolkit                      toolkit          = new FormToolkit(Display.getDefault());
    private static final long              serialVersionUID = -1300484097701416526L;

    private boolean                        _isFocused       = false;
    private EJEditableBlockController      _block;
    private EJRWTEntireJGridPane           _mainPane;
    private TreeViewer                     _tableViewer;
    private EJRWTQueryScreenRenderer       _queryScreenRenderer;
    private EJRWTInsertScreenRenderer      _insertScreenRenderer;
    private EJRWTUpdateScreenRenderer      _updateScreenRenderer;
    private EJFrameworkExtensionProperties _rendererProp;
    List<String>                           _actionkeys      = new ArrayList<String>();
    private Map<KeyInfo, String>           _actionInfoMap   = new HashMap<EJRWTKeysUtil.KeyInfo, String>();

    private List<EJDataRecord>             _treeBaseRecords = new ArrayList<EJDataRecord>();

    private FilteredContentProvider        _filteredContentProvider;

    @Override
    public void refreshBlockProperty(EJManagedBlockProperty managedBlockPropertyType)
    {
    }

    @Override
    public void refreshBlockRendererProperty(String propertyName)
    {
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
                if (record == null || _tableViewer == null)
                {
                    return;
                }
                if (_tableViewer != null && !_tableViewer.getTree().isDisposed())
                {
                    _tableViewer.refresh(record);
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
                if (_tableViewer != null && !_tableViewer.getTree().isDisposed())
                {
                    _tableViewer.setInput(new Object());
                }
            }
        }
        else if (EJManagedScreenProperty.VISIBLE.equals(managedItemPropertyType))
        {
            if (_tableViewer != null && !_tableViewer.getTree().isDisposed())
            {
                TreeColumn[] columns = _tableViewer.getTree().getColumns();
                for (TreeColumn tableColumn : columns)
                {
                    if (itemName.equals(tableColumn.getData("KEY")))
                    {
                        EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemName);
                        ColumnInfo data = (ColumnInfo) tableColumn.getData("INFO");
                        if (item.isVisible() && data != null)
                        {
                            tableColumn.setWidth(data.width);
                            tableColumn.setResizable(data.resizable);
                        }
                        else
                        {
                            tableColumn.setWidth(0);
                            tableColumn.setResizable(false);
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void refreshItemRendererProperty(String itemName, String propertyName)
    {
    }

    @Override
    public Composite getGuiComponent()
    {
        return _mainPane;
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
    }

    @Override
    public void blockCleared()
    {
        if (_tableViewer != null && !_tableViewer.getTree().isDisposed())
        {
            _tableViewer.setInput(new Object());
        }
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
        EJQuestion question = new EJQuestion(new EJForm(_block.getForm()), "DELETE_RECORD", "Delete", message, "Yes", "No"){
            
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

    @Override
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
    public void queryExecuted()
    {
        if (_tableViewer != null && !_tableViewer.getTree().isDisposed())
        {
            _tableViewer.setInput(new Object());
        }
        selectFirst();
    }

    public void pageRetrieved()
    {
        if (_tableViewer != null && !_tableViewer.getTree().isDisposed())
        {
            _tableViewer.refresh();
        }
        selectFirst();
    }

    @Override
    public void recordDeleted(int dataBlockRecordNumber)
    {
        EJDataRecord recordAt = getRecordAt(dataBlockRecordNumber > 1 ? dataBlockRecordNumber - 2 : 0);

        if (recordAt == null)
        {
            recordAt = getLastRecord();
        }

        if (_tableViewer != null && !_tableViewer.getTree().isDisposed())
        {
            refresh();
        }

        if (recordAt != null)
        {
            recordSelected(recordAt);
        }
    }

    public void refresh()
    {
        refresh(new Object());
    }

    public void refresh(Object input)
    {
        if (_tableViewer != null && !_tableViewer.getTree().isDisposed())
        {
            TreeViewer treeview = _tableViewer;
            if (treeview != null)
            {
                Object[] expanded = treeview.getExpandedElements();

                treeview.getControl().setRedraw(false);
                treeview.setInput(input);
                treeview.setExpandedElements(expanded);
                treeview.getControl().setRedraw(true);
                treeview.refresh();
            }
        }
    }

    @Override
    public void recordInserted(EJDataRecord record)
    {
        if (_tableViewer != null && !_tableViewer.getTree().isDisposed())
        {
            refresh();
        }
        recordSelected(record);
    }

    @Override
    public void refreshAfterChange(EJDataRecord record)
    {
        if (_tableViewer != null && !_tableViewer.getTree().isDisposed())
        {
            refresh();
        }
    }

    @Override
    public void recordSelected(EJDataRecord record)
    {
        if (_tableViewer != null && !_tableViewer.getTree().isDisposed())
        {
            _tableViewer.setSelection(record != null ? new StructuredSelection(record) : new StructuredSelection(), true);
        }
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
        if (_tableViewer != null && !_tableViewer.getTree().isDisposed())
        {
            _tableViewer.getTree().forceFocus();
        }
    }

    @Override
    public void gainFocus()
    {
        if (_tableViewer != null && !_tableViewer.getTree().isDisposed())
        {
            _tableViewer.getTree().forceFocus();
        }
        setHasFocus(true);

    }

    @Override
    public EJDataRecord getFocusedRecord()
    {
        EJDataRecord _focusedRecord = null;

        if (_tableViewer != null && !_tableViewer.getTree().isDisposed())
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

    public void selectFirst()
    {
        if (_tableViewer != null && !_tableViewer.getTree().isDisposed() && _tableViewer.getTree().getTopItem() != null)
        {
            _tableViewer.getTree().select(_tableViewer.getTree().getTopItem());
        }
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

                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_QUERY_KEY),
                        EJRWTSingleRecordBlockDefinitionProperties.ACTION_QUERY_KEY);
                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_INSERT_KEY),
                        EJRWTSingleRecordBlockDefinitionProperties.ACTION_INSERT_KEY);
                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_UPDATE_KEY),
                        EJRWTSingleRecordBlockDefinitionProperties.ACTION_UPDATE_KEY);
                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_DELETE_KEY),
                        EJRWTSingleRecordBlockDefinitionProperties.ACTION_DELETE_KEY);
                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_REFRESH_KEY),
                        EJRWTSingleRecordBlockDefinitionProperties.ACTION_REFRESH_KEY);

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
        if (sectionProperties != null
                && sectionProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE) != null
                && !EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_GROUP.equals(sectionProperties
                        .getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE)))
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

            final EJFrameworkExtensionPropertyList propertyList = sectionProperties
                    .getPropertyList(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTIONS);

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
        int style = SWT.VIRTUAL | SWT.FULL_SELECTION;

        if (!_rendererProp.getBooleanProperty(EJRWTTreeTableBlockDefinitionProperties.HIDE_TREE_BORDER, false))
        {
            style = style | SWT.BORDER;
        }

        Collection<EJItemGroupProperties> allItemGroupProperties = _block.getProperties().getScreenItemGroupContainer(EJScreenType.MAIN)
                .getAllItemGroupProperties();
        final Tree table;
        final EJRWTAbstractFilteredTree filterTree;
        if (_rendererProp.getBooleanProperty(EJRWTTreeTableBlockDefinitionProperties.FILTER, true))
        {
            if (allItemGroupProperties.size() > 0)
            {
                EJItemGroupProperties displayProperties = allItemGroupProperties.iterator().next();
                if (displayProperties.dispayGroupFrame() && displayProperties.getFrameTitle() != null && displayProperties.getFrameTitle().length() > 0)
                {
                    Group group = new Group(_mainPane, SWT.NONE);
                    group.setLayout(new GridLayout());
                    group.setText(displayProperties.getFrameTitle());

                    group.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                    filterTree = new EJRWTAbstractFilteredTree(group, style)
                    {
                        @Override
                        public void filter(String filter)
                        {
                            if (_filteredContentProvider != null
                                    && (filter == null && _filteredContentProvider.getFilter() != null || !filter.equals(_filteredContentProvider.getFilter())))
                            {
                                _filteredContentProvider.setFilter(filter);
                                refresh(filter);
                            }
                        }
                    };
                }
                else
                {
                    filterTree = new EJRWTAbstractFilteredTree(_mainPane, displayProperties.dispayGroupFrame() ? style | SWT.BORDER : style)
                    {
                        @Override
                        public void filter(String filter)
                        {
                            if (_filteredContentProvider != null
                                    && (filter == null && _filteredContentProvider.getFilter() != null || !filter.equals(_filteredContentProvider.getFilter())))
                            {
                                _filteredContentProvider.setFilter(filter);
                                refresh(filter);
                            }
                        }
                    };
                    filterTree.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                }
            }
            else
            {
                filterTree = new EJRWTAbstractFilteredTree(_mainPane, style)
                {
                    @Override
                    public void filter(String filter)
                    {
                        if (_filteredContentProvider != null
                                && (filter == null && _filteredContentProvider.getFilter() != null || !filter.equals(_filteredContentProvider.getFilter())))
                        {
                            _filteredContentProvider.setFilter(filter);
                            refresh(filter);
                        }
                    }
                };

                filterTree.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
            }
            table = (_tableViewer = filterTree.getViewer()).getTree();
        }
        else
        {
            filterTree = null;
            if (allItemGroupProperties.size() > 0)
            {
                EJItemGroupProperties displayProperties = allItemGroupProperties.iterator().next();
                if (displayProperties.dispayGroupFrame() && displayProperties.getFrameTitle() != null && displayProperties.getFrameTitle().length() > 0)
                {
                    Group group = new Group(_mainPane, SWT.NONE);
                    group.setLayout(new FillLayout());
                    group.setText(displayProperties.getFrameTitle());

                    group.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                    table = new Tree(group, style);
                }
                else
                {
                    table = new Tree(_mainPane, displayProperties.dispayGroupFrame() ? style | SWT.BORDER : style);

                    table.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                }
            }
            else
            {
                table = new Tree(_mainPane, style);

                table.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
            }
        }

        table.setLinesVisible(_rendererProp.getBooleanProperty(EJRWTTreeTableBlockDefinitionProperties.SHOW_VERTICAL_LINES, true));
        table.setHeaderVisible(_rendererProp.getBooleanProperty(EJRWTTreeTableBlockDefinitionProperties.SHOW_HEADING_PROPERTY, true));
        Control[] children = table.getChildren();
        for (Control control : children)
        {
            hookKeyListener(control);
        }
        hookKeyListener(table);
        // final boolean hideSelection = (style & SWT.HIDE_SELECTION) != 0;
        _tableViewer = new TreeViewer(table);

        ColumnViewerToolTipSupport.enableFor(_tableViewer);

        EJRWTTreeTableViewerColumnFactory factory = new EJRWTTreeTableViewerColumnFactory(_tableViewer);
        final List<ColumnLabelProvider> nodeTextProviders = new ArrayList<ColumnLabelProvider>();
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
                }
            }
        }

        table.addFocusListener(new FocusListener()
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
                if (!table.isFocusControl())
                {
                    setHasFocus(true);
                }
            }
        });
        table.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                if (!table.isFocusControl())
                {
                    setHasFocus(true);
                }
            }

        });
        final String pid = _rendererProp.getStringProperty(EJRWTTreeTableBlockDefinitionProperties.PARENT_ITEM);
        final String rid = _rendererProp.getStringProperty(EJRWTTreeTableBlockDefinitionProperties.RELATION_ITEM);
        final String imageid = _rendererProp.getStringProperty(EJRWTTreeTableBlockDefinitionProperties.NODE_IMAGE_ITEM);

        if (imageid != null && _tableViewer.getTree().getColumnCount() > 0)
        {
            final ColumnLabelProvider baselabelProvider = (ColumnLabelProvider) _tableViewer.getLabelProvider(0);

            ColumnLabelProvider imgPatchedProvider = new ColumnLabelProvider()
            {
                final transient Map<Object, Image> imageMap = new HashMap<Object, Image>();

                @Override
                public void dispose()
                {
                    for (Image img : imageMap.values())
                    {
                        img.dispose();
                    }
                    imageMap.clear();
                    super.dispose();
                }

                @Override
                public Font getFont(Object element)
                {
                    return baselabelProvider.getFont(element);
                }

                @Override
                public Color getBackground(Object element)
                {
                    return baselabelProvider.getBackground(element);
                }

                @Override
                public Color getForeground(Object element)
                {
                    return baselabelProvider.getForeground(element);
                }

                @Override
                public String getText(Object element)
                {
                    return baselabelProvider.getText(element);
                }

                @Override
                public Color getToolTipBackgroundColor(Object object)
                {
                    return baselabelProvider.getToolTipBackgroundColor(object);
                }

                @Override
                public int getToolTipDisplayDelayTime(Object object)
                {
                    return baselabelProvider.getToolTipDisplayDelayTime(object);
                }

                @Override
                public Font getToolTipFont(Object object)
                {
                    return baselabelProvider.getToolTipFont(object);
                }

                @Override
                public Color getToolTipForegroundColor(Object object)
                {
                    return baselabelProvider.getToolTipForegroundColor(object);
                }

                @Override
                public Image getToolTipImage(Object object)
                {
                    return baselabelProvider.getToolTipImage(object);
                }

                @Override
                public Point getToolTipShift(Object object)
                {
                    return baselabelProvider.getToolTipShift(object);
                }

                @Override
                public int getToolTipStyle(Object object)
                {
                    return baselabelProvider.getToolTipStyle(object);
                }

                @Override
                public String getToolTipText(Object element)
                {
                    return baselabelProvider.getToolTipText(element);
                }

                @Override
                public int getToolTipTimeDisplayed(Object object)
                {
                    return baselabelProvider.getToolTipTimeDisplayed(object);
                }

                @Override
                public Image getImage(Object element)
                {
                    if (imageid != null && element instanceof EJDataRecord)
                    {
                        EJDataRecord record = (EJDataRecord) element;
                        Object iV = record.getValue(imageid);
                        if (iV == null)
                        {
                            return null;
                        }
                        Image image = imageMap.get(iV);
                        if (image != null && !image.isDisposed())
                        {
                            return image;
                        }

                        if (iV instanceof URL)
                        {
                            image = ImageDescriptor.createFromURL((URL) iV).createImage();
                        }
                        else if (iV instanceof byte[])
                        {
                            image = new Image(Display.getDefault(), new ByteArrayInputStream((byte[]) iV));
                        }
                        if (image != null)
                        {
                            imageMap.put(iV, image);
                        }
                        return image;
                    }
                    return super.getImage(element);
                }

            };
            ((TreeViewerColumn) _tableViewer.getTree().getColumn(0).getData("VIEWER")).setLabelProvider(imgPatchedProvider);

        }
        int intProperty = _rendererProp.getIntProperty(EJRWTTreeTableBlockDefinitionProperties.NODE_EXPAND_LEVEL, 1);
        intProperty++;// workaround
        if (intProperty < 1 && intProperty < 2)
        {
            intProperty = 2;
        }
        if (intProperty > 1)
            _tableViewer.setAutoExpandLevel(intProperty);

        _tableViewer.setContentProvider(_filteredContentProvider = new FilteredContentProvider()
        {
            private List<EJDataRecord>              root     = new ArrayList<EJDataRecord>();
            private Map<Object, Object>             indexMap = new HashMap<Object, Object>();
            private Map<Object, List<EJDataRecord>> cmap     = new HashMap<Object, List<EJDataRecord>>();

            private List<EJDataRecord>              froot    = new ArrayList<EJDataRecord>();
            private Map<Object, List<EJDataRecord>> fcmap    = new HashMap<Object, List<EJDataRecord>>();

            boolean matchItem(EJDataRecord rec)
            {
                if (filter != null && filter.trim().length() > 0)
                {
                    for (ColumnLabelProvider filterTextProvider : nodeTextProviders)
                    {
                        String text = filterTextProvider.getText(rec);
                        if (text != null && text.toLowerCase().contains(filter.toLowerCase()))
                        {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public void inputChanged(Viewer arg0, Object arg1, Object arg2)
            {
                _treeBaseRecords.clear();
                if (arg2 != null && arg2.equals(filter) && filter.trim().length() > 0)
                {
                    froot.clear();
                    fcmap.clear();
                    // filter
                    for (Entry<Object, List<EJDataRecord>> entry : cmap.entrySet())
                    {

                        List<EJDataRecord> values = entry.getValue();
                        List<EJDataRecord> fvalues = new ArrayList<EJDataRecord>(values.size());
                        fcmap.put(entry.getKey(), fvalues);
                        for (EJDataRecord record : values)
                        {
                            if (matchItem(record))
                            {
                                fvalues.add(record);
                            }
                        }
                    }
                    // filter root
                    for (EJDataRecord record : root)
                    {
                        if (matchItem(record))
                        {
                            froot.add(record);
                        }
                        else if (hasChildren(record))
                        {
                            froot.add(record);
                        }
                    }
                    for (EJDataRecord record : froot)
                    {
                        _treeBaseRecords.add(record);
                        addSubRecords(record.getValue(pid), fcmap);
                    }
                }
                else
                {
                    filter = null;
                    if (filterTree != null)
                    {
                        filterTree.clearText();
                    }
                    root.clear();
                    indexMap.clear();
                    froot.clear();
                    cmap.clear();
                    fcmap.clear();
                    Collection<EJDataRecord> records = _block.getRecords();
                    for (EJDataRecord record : records)
                    {
                        Object rV = record.getValue(rid);
                        Object pV = record.getValue(pid);
                        if (rV == null)
                        {
                            root.add(record);
                            if (pid != null)
                            {
                                indexMap.put(pV, record);
                            }

                            continue;
                        }
                        List<EJDataRecord> list = cmap.get(rV);
                        if (list == null)
                        {
                            list = new ArrayList<EJDataRecord>();
                            cmap.put(rV, list);
                        }
                        list.add(record);
                    }

                    // child node with no parent need to consider as roots
                    MAIN: for (Object key : new HashSet<Object>(cmap.keySet()))
                    {
                        if (indexMap.containsKey(key))
                        {
                            continue;
                        }

                        for (EJDataRecord rec : records)
                        {
                            if (key.equals(rec.getValue(pid)))
                            {
                                continue MAIN;
                            }
                        }
                        List<EJDataRecord> list = cmap.get(key);
                        cmap.remove(key);
                        for (EJDataRecord record : list)
                        {
                            Object pV = record.getValue(pid);
                            root.add(record);
                            if (pid != null)
                            {
                                indexMap.put(pV, record);
                            }
                        }
                    }

                    for (EJDataRecord record : root)
                    {
                        _treeBaseRecords.add(record);
                        addSubRecords(record.getValue(pid), cmap);
                    }

                }

            }

            private void addSubRecords(Object key, Map<Object, List<EJDataRecord>> cmap)
            {
                if (key != null)
                {
                    List<EJDataRecord> list = cmap.get(key);
                    if (list != null)
                    {
                        for (EJDataRecord record : list)
                        {
                            _treeBaseRecords.add(record);
                            addSubRecords(record.getValue(pid), cmap);
                        }
                    }
                }
            }

            @Override
            public void dispose()
            {
                root.clear();
                indexMap.clear();
                cmap.clear();
                froot.clear();
                fcmap.clear();
            }

            @Override
            public Object[] getElements(Object arg0)
            {
                if (filter != null && filter.trim().length() > 0)
                {
                    return froot.toArray();
                }
                return root.toArray();
            }

            @Override
            public Object[] getChildren(Object arg0)
            {
                Map<Object, List<EJDataRecord>> map = filter != null && filter.trim().length() > 0 ? fcmap : cmap;
                if (arg0 instanceof EJDataRecord)
                {
                    EJDataRecord record = (EJDataRecord) arg0;
                    Object pV = record.getValue(pid);
                    if (pV != null)
                    {
                        List<EJDataRecord> list = map.get(pV);
                        if (list != null)
                        {
                            return list.toArray();
                        }
                    }
                }
                return new Object[0];
            }

            @Override
            public Object getParent(Object arg0)
            {
                if (arg0 instanceof EJDataRecord)
                {
                    EJDataRecord record = (EJDataRecord) arg0;
                    Object rV = record.getValue(rid);
                    if (rV != null)
                    {
                        return indexMap.get(rV);
                    }
                }
                return null;
            }

            @Override
            public boolean hasChildren(Object arg0)
            {
                if (arg0 instanceof EJDataRecord)
                {
                    Map<Object, List<EJDataRecord>> map = filter != null && filter.trim().length() > 0 ? fcmap : cmap;
                    EJDataRecord record = (EJDataRecord) arg0;
                    Object pV = record.getValue(pid);
                    if (pV != null)
                    {
                        List<EJDataRecord> list = map.get(pV);
                        return list != null && list.size() > 0;

                    }
                }
                return false;
            }
        });
        _tableViewer.setInput(new Object());
        selectFirst();

        // add double click action
        final String doubleClickActionCommand = _rendererProp.getStringProperty(EJRWTTreeTableBlockDefinitionProperties.DOUBLE_CLICK_ACTION_COMMAND);
        if (doubleClickActionCommand != null)
        {
            _tableViewer.addDoubleClickListener(new IDoubleClickListener()
            {

                @Override
                public void doubleClick(DoubleClickEvent arg0)
                {
                    _block.executeActionCommand(doubleClickActionCommand, EJScreenType.MAIN);
                }
            });
        }
        _tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
        {

            @Override
            public void selectionChanged(SelectionChangedEvent arg0)
            {
                EJDataRecord focusedRecord = getFocusedRecord();
                if (focusedRecord != null)
                {
                    _block.newRecordInstance(focusedRecord);
                }
            }
        });
        table.addListener(SWT.MouseDown, new Listener()
        {
            @Override
            public void handleEvent(Event event)
            {
                Point pt = new Point(event.x, event.y);
                TreeItem item = table.getItem(pt);
                if (item == null)
                    return;
                for (int i = 0; i < table.getColumnCount(); i++)
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt))
                    {

                        TreeColumn column = table.getColumn(i);
                        if (column != null && column.getData("ITEM") instanceof EJScreenItemController)
                        {
                            ((EJScreenItemController) column.getData("ITEM")).executeActionCommand();
                        }
                    }
                }
            }
        });
    }

    private class ColumnInfo
    {
        boolean resizable = true;
        int     width     = 0;
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

    public ColumnLabelProvider createScreenItem(EJRWTTreeTableViewerColumnFactory factory, EJCoreMainScreenItemProperties itemProps)
    {
        if (itemProps.isSpacerItem())
        {

            return null;
        }
        EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemProps.getReferencedItemName());
        EJManagedItemRendererWrapper renderer = item.getManagedItemRenderer();
        if (renderer != null)
        {
            EJFrameworkExtensionProperties blockProperties = itemProps.getBlockRendererRequiredProperties();
            EJRWTAppItemRenderer itemRenderer = (EJRWTAppItemRenderer) renderer.getUnmanagedRenderer();
            ColumnLabelProvider labelProvider = itemRenderer.createColumnLabelProvider(itemProps, item);
            if (labelProvider != null)
            {

                String labelOrientation = blockProperties.getStringProperty(EJRWTTreeTableBlockDefinitionProperties.COLUMN_ALIGNMENT);

                int displayedWidth = blockProperties.getIntProperty(EJRWTTreeTableBlockDefinitionProperties.DISPLAY_WIDTH_PROPERTY, 0);

                // if no width define in block properties use item renderer pref
                // width
                if (displayedWidth == 0)
                {
                    if (itemProps.getLabel() != null)
                    {
                        displayedWidth = itemProps.getLabel().length() + 2;// add
                                                                           // offset
                    }
                    else
                    {
                        displayedWidth = 5;
                    }
                }

                String visualAttribute = blockProperties.getStringProperty(EJRWTTreeTableBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY);

                if (visualAttribute != null)
                {
                    EJCoreVisualAttributeProperties va = EJCoreProperties.getInstance().getVisualAttributesContainer()
                            .getVisualAttributeProperties(visualAttribute);
                    if (va != null)
                    {
                        itemRenderer.setInitialVisualAttribute(va);
                    }
                }
                
                if(item.getProperties().getVisualAttributeProperties()!=null)
                {
                    renderer.setVisualAttribute(item.getProperties().getVisualAttributeProperties());
                }

                TreeViewerColumn viewerColumn = factory.createColumn(itemProps.getLabel(), displayedWidth, labelProvider, getComponentStyle(labelOrientation));
                TreeColumn column = viewerColumn.getColumn();
                column.setData("KEY", itemProps.getReferencedItemName());
                column.setData("VIEWER", viewerColumn);
                column.setData("ITEM", item);
                column.setToolTipText(itemProps.getHint());

                ColumnInfo info = new ColumnInfo();
                column.setData("INFO", info);

                column.setMoveable(blockProperties.getBooleanProperty(EJRWTTreeTableBlockDefinitionProperties.ALLOW_COLUMN_REORDER, true));
                column.setResizable(info.resizable = blockProperties.getBooleanProperty(EJRWTTreeTableBlockDefinitionProperties.ALLOW_COLUMN_RESIZE, true));
                if (blockProperties.getBooleanProperty(EJRWTTreeTableBlockDefinitionProperties.ALLOW_ROW_SORTING, true))
                {
                    EJRWTAbstractTableSorter columnSorter = itemRenderer.getColumnSorter(itemProps, item);
                    if (columnSorter != null)
                    {
                        new EJRWTTreeTableSortSelectionListener(_tableViewer, column, columnSorter, SWT.UP, false);
                    }
                }
                // ensure that the width property of the table column is in
                // Characters
                Font font = labelProvider.getFont(new Object());
                if (font == null)
                {
                    font = _tableViewer.getTree().getFont();
                }
                if (font != null)
                {
                    float avgCharWidth = EJRWTImageRetriever.getGraphicsProvider().getAvgCharWidth(font);
                    if (avgCharWidth > 0)
                    {
                        column.setWidth(info.width = ((int) ((column.getWidth() + 1) * avgCharWidth)));// add
                        // +1
                        // padding
                    }
                }

                return labelProvider;
            }
        }
        return null;
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
        KeyInfo keyInfo = EJRWTKeysUtil
                .toKeyInfo(keyCode, (arg0.stateMask & SWT.SHIFT) != 0, (arg0.stateMask & SWT.CTRL) != 0, (arg0.stateMask & SWT.ALT) != 0);

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

    protected int getComponentStyle(String alignmentProperty)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJRWTTreeTableBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT))
            {
                return SWT.RIGHT;
            }
            else if (alignmentProperty.equals(EJRWTTreeTableBlockDefinitionProperties.COLUMN_ALLIGN_CENTER))
            {
                return SWT.CENTER;
            }
        }
        return SWT.LEFT;
    }
}
