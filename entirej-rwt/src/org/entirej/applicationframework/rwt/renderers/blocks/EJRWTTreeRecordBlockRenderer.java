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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
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
import org.entirej.applicationframework.rwt.renderers.blocks.definition.interfaces.EJRWTTreeBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTInsertScreenRenderer;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTQueryScreenRenderer;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTUpdateScreenRenderer;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractFilteredTree;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractFilteredTree.FilteredContentProvider;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
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

public class EJRWTTreeRecordBlockRenderer implements EJRWTAppBlockRenderer, KeyListener
{
    final FormToolkit                      toolkit         = new FormToolkit(Display.getDefault());

    private boolean                        _isFocused      = false;
    private EJEditableBlockController      _block;
    private EJRWTEntireJGridPane           _mainPane;
    private TreeViewer                     _tableViewer;
    private EJRWTQueryScreenRenderer       _queryScreenRenderer;
    private EJRWTInsertScreenRenderer      _insertScreenRenderer;
    private EJRWTUpdateScreenRenderer      _updateScreenRenderer;
    private EJFrameworkExtensionProperties rendererProp;
    List<String>                           actionkeys      = new ArrayList<String>();
    private Map<KeyInfo, String>           actionInfoMap   = new HashMap<EJRWTKeysUtil.KeyInfo, String>();

    private List<EJDataRecord>             treeBaseRecords = new ArrayList<EJDataRecord>();
    private FilteredContentProvider        filteredContentProvider;

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
    public void askToDeleteRecord(EJDataRecord recordToDelete, String msg)
    {
        if (msg == null)
        {
            msg = "Are you sure you want to delete the current record?";
        }
        EJMessage message = new EJMessage(msg);
        EJQuestion question = new EJQuestion(new EJForm(_block.getForm()), "DELETE_RECORD", "Delete", message, "Yes", "No");
        _block.getForm().getMessenger().askQuestion(question);
        if (EJQuestionButton.ONE == question.getAnswer())
        {
            _block.getBlock().deleteRecord(recordToDelete);
        }
        _block.setRendererFocus(true);

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
                final String pid = rendererProp.getStringProperty(EJRWTTreeBlockDefinitionProperties.PARENT_ITEM);
                final String rid = rendererProp.getStringProperty(EJRWTTreeBlockDefinitionProperties.RELATION_ITEM);
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
        return treeBaseRecords.indexOf(record);
    }

    @Override
    public int getDisplayedRecordCount()
    {
        // Indicates the number of records that are available within the View.
        // the number depends on the filters set on the table!
        return treeBaseRecords.size();
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
            return treeBaseRecords.get(displayedRecordNumber);
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
        if (_tableViewer != null && !_tableViewer.getTree().isDisposed() && _tableViewer.getTree().getTopItem()!=null)
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
        rendererProp = blockProperties.getBlockRendererProperties();
        blockCanvas.setLayoutData(gridData);

        EJFrameworkExtensionProperties sectionProperties = null;
        if (rendererProp != null)
        {
            sectionProperties = rendererProp.getPropertyGroup(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR);
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
            if (mainScreenProperties.getDisplayFrame())
            {
                Group group = new Group(section, SWT.NONE);
                group.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
                group.setLayout(new FillLayout());
                group.setLayoutData(gridData);
                hookKeyListener(group);
                
                
                    group.setText(frameTitle);
                
                _mainPane = new EJRWTEntireJGridPane(group, 1);
                _mainPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
                section.setClient(group);
            }
            else
            {
                _mainPane = new EJRWTEntireJGridPane(section, 1,mainScreenProperties.getDisplayFrame()?SWT.BORDER:SWT.NONE);
                _mainPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
                _mainPane.setLayoutData(gridData);
                _mainPane.cleanLayoutHorizontal();
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
                            action.setImageDescriptor(ImageDescriptor.createFromImage(EJRWTImageRetriever.get(actionImage)));
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
               
                _mainPane = new EJRWTEntireJGridPane(group, 1);
                _mainPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
            }
            else
            {
                _mainPane = new EJRWTEntireJGridPane(blockCanvas, 1, mainScreenProperties.getDisplayFrame()?SWT.BORDER:SWT.NONE);
                _mainPane.setLayoutData(gridData);
                _mainPane.cleanLayout();
                _mainPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
            }
        }

        hookKeyListener(_mainPane);
        int style = SWT.VIRTUAL;

        if (!rendererProp.getBooleanProperty(EJRWTTreeBlockDefinitionProperties.HIDE_TREE_BORDER, false))
        {
            style = style | SWT.BORDER;
        }
        if (!rendererProp.getBooleanProperty(EJRWTTreeBlockDefinitionProperties.HIDE_SELECTION, false))
        {
            style = style | SWT.FULL_SELECTION;
        }

        Collection<EJItemGroupProperties> allItemGroupProperties = _block.getProperties().getScreenItemGroupContainer(EJScreenType.MAIN)
                .getAllItemGroupProperties();
        final Tree table;
        final EJRWTAbstractFilteredTree filterTree;
        if (rendererProp.getBooleanProperty(EJRWTTreeBlockDefinitionProperties.FILTER, true))
        {
            if (allItemGroupProperties.size() > 0)
            {
                EJItemGroupProperties displayProperties = allItemGroupProperties.iterator().next();
                
                if (displayProperties.dispayGroupFrame() && displayProperties.getFrameTitle() != null && displayProperties.getFrameTitle().length() > 0)
                {
                    Group group = new Group(_mainPane, SWT.NONE);
                    group.setLayout(new FillLayout());
                  
                        group.setText(displayProperties.getFrameTitle());
                    
                    group.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

                    filterTree = new EJRWTAbstractFilteredTree(group, style)
                    {
                        @Override
                        public void filter(String filter)
                        {
                            if (filteredContentProvider != null
                                    && (filter == null && filteredContentProvider.getFilter() != null || !filter.equals(filteredContentProvider.getFilter())))
                            {
                                filteredContentProvider.setFilter(filter);
                                refresh(filter);
                            }
                        }
                    };
                }
                else
                {
                    filterTree = new EJRWTAbstractFilteredTree(_mainPane, style)
                    {
                        @Override
                        public void filter(String filter)
                        {
                            if (filteredContentProvider != null
                                    && (filter == null && filteredContentProvider.getFilter() != null || !filter.equals(filteredContentProvider.getFilter())))
                            {
                                filteredContentProvider.setFilter(filter);
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
                        if (filteredContentProvider != null
                                && (filter == null && filteredContentProvider.getFilter() != null || !filter.equals(filteredContentProvider.getFilter())))
                        {
                            filteredContentProvider.setFilter(filter);
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
                    table = new Tree(_mainPane, style|(displayProperties.dispayGroupFrame()?SWT.BORDER:SWT.NONE));

                    table.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                }
            }
            else
            {
                table = new Tree(_mainPane, style);

                table.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
            }
            _tableViewer = new TreeViewer(table);
        }

        Control[] children = table.getChildren();
        for (Control control : children)
        {
            hookKeyListener(control);
        }
        hookKeyListener(table);

        ColumnViewerToolTipSupport.enableFor(_tableViewer);

        final List<TreeNodeTextProvider> nodeTextProviders = new ArrayList<EJRWTTreeRecordBlockRenderer.TreeNodeTextProvider>();
        for (EJItemGroupProperties groupProperties : allItemGroupProperties)
        {
            Collection<EJScreenItemProperties> itemProperties = groupProperties.getAllItemProperties();
            for (EJScreenItemProperties screenItemProperties : itemProperties)
            {
                EJCoreMainScreenItemProperties mainScreenItemProperties = (EJCoreMainScreenItemProperties) screenItemProperties;
                TreeNodeTextProvider screenItem = createScreenItem(mainScreenItemProperties);
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
        final String pid = rendererProp.getStringProperty(EJRWTTreeBlockDefinitionProperties.PARENT_ITEM);
        final String rid = rendererProp.getStringProperty(EJRWTTreeBlockDefinitionProperties.RELATION_ITEM);
        final String imageid = rendererProp.getStringProperty(EJRWTTreeBlockDefinitionProperties.NODE_IMAGE_ITEM);

        final EJCoreVisualAttributeProperties baseVA;
        String visualAttribute = rendererProp.getStringProperty(EJRWTTreeBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY);

        if (visualAttribute != null)
        {
            baseVA = EJCoreProperties.getInstance().getVisualAttributesContainer().getVisualAttributeProperties(visualAttribute);
        }
        else
        {
            baseVA = null;
        }

        final Map<Object, Image> imageMap = new HashMap<Object, Image>();
        _tableViewer.setLabelProvider(new ColumnLabelProvider()
        {
            @Override
            public Color getBackground(Object element)
            {
                EJCoreVisualAttributeProperties properties = getAttributes(element);
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
                EJCoreVisualAttributeProperties properties = getAttributes(element);
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

            private EJCoreVisualAttributeProperties getAttributes(Object element)
            {
                EJCoreVisualAttributeProperties properties = null;
                if (pid != null && element instanceof EJDataRecord)
                {
                    EJDataRecord record = (EJDataRecord) element;
                    properties = record.getItem(pid).getVisualAttribute();
                }
                if (properties == null)
                {
                    properties = baseVA;
                }
                return properties;
            }

            @Override
            public Font getFont(Object element)
            {
                EJCoreVisualAttributeProperties properties = getAttributes(element);
                if (properties != null)
                {
                    Font font = super.getFont(element);
                    return EJRWTVisualAttributeUtils.INSTANCE.getFont(properties, font != null ? font : _tableViewer.getTree().getFont());
                }
                return super.getFont(element);
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

            @Override
            public String getText(Object element)
            {
                if (nodeTextProviders.size() == 1)
                {
                    return nodeTextProviders.get(0).getText(element);
                }
                StringBuilder builder = new StringBuilder();

                for (TreeNodeTextProvider textProvider : nodeTextProviders)
                {
                    builder.append(textProvider.getText(element));
                }
                return builder.toString();
            }

        });

        int intProperty = rendererProp.getIntProperty(EJRWTTreeBlockDefinitionProperties.NODE_EXPAND_LEVEL, 1);
        intProperty++;//workaround
        if (intProperty<1 && intProperty < 2)
        {
            intProperty = 2;
        }
        if (intProperty>1)
            _tableViewer.setAutoExpandLevel(intProperty);

        _tableViewer.setContentProvider(filteredContentProvider = new FilteredContentProvider()
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
                    for (TreeNodeTextProvider filterTextProvider : nodeTextProviders)
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
                treeBaseRecords.clear();
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
                        treeBaseRecords.add(record);
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
                    for (Image img : imageMap.values())
                    {
                        img.dispose();
                    }
                    imageMap.clear();
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
                        treeBaseRecords.add(record);
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
                            treeBaseRecords.add(record);
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
                for (Image img : imageMap.values())
                {
                    img.dispose();
                }
                imageMap.clear();
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
        final String doubleClickActionCommand = rendererProp.getStringProperty(EJRWTTreeBlockDefinitionProperties.DOUBLE_CLICK_ACTION_COMMAND);
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
        // add double click action
        final String clickActionCommand = rendererProp.getStringProperty(EJRWTTreeBlockDefinitionProperties.CLICK_ACTION_COMMAND);

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

                if (clickActionCommand != null)
                {
                    _block.executeActionCommand(clickActionCommand, EJScreenType.MAIN);
                }

                if (rendererProp.getBooleanProperty(EJRWTTreeBlockDefinitionProperties.HIDE_SELECTION, false))
                {
                    _tableViewer.removeSelectionChangedListener(this);
                    _tableViewer.getTree().deselectAll();
                    _tableViewer.addSelectionChangedListener(this);
                }
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

    static class TreeNodeTextProvider
    {
        private final String              prefix;
        private final String              suffix;
        private final ColumnLabelProvider provider;

        public TreeNodeTextProvider(String prefix, String suffix, ColumnLabelProvider provider)
        {
            this.prefix = prefix;
            this.suffix = suffix;
            this.provider = provider;
        }

        public String getText(Object object)
        {
            String text = provider.getText(object);
            if (text != null && text.length() > 0 && (prefix != null || suffix != null))
            {
                StringBuilder builder = new StringBuilder();
                if (prefix != null)
                {
                    builder.append(prefix);
                }
                builder.append(text);
                if (suffix != null)
                {
                    builder.append(suffix);
                }
                return builder.toString();
            }

            return text;
        }
    }

    public TreeNodeTextProvider createScreenItem(EJCoreMainScreenItemProperties itemProps)
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
                return new TreeNodeTextProvider(blockProperties.getStringProperty(EJRWTTreeBlockDefinitionProperties.ITEM_PREFIX),
                        blockProperties.getStringProperty(EJRWTTreeBlockDefinitionProperties.ITEM_SUFFIX), labelProvider);
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
