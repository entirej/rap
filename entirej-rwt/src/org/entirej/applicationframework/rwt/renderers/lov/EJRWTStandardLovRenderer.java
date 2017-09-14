/*******************************************************************************
 * Copyright 2013 CRESOFT AG
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
 *     CRESOFT AG - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.renderers.lov;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTAbstractDialog;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.blocks.definition.interfaces.EJRWTMultiRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractFilteredTable;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractFilteredTable.FilteredContentProvider;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.applicationframework.rwt.table.EJRWTTableSortSelectionListener;
import org.entirej.applicationframework.rwt.table.EJRWTTableViewerColumnFactory;
import org.entirej.framework.core.EJFrameworkManager;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.data.controllers.EJItemLovController;
import org.entirej.framework.core.data.controllers.EJLovController;
import org.entirej.framework.core.enumerations.EJLovDisplayReason;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalBlock;
import org.entirej.framework.core.properties.EJCoreMainScreenItemProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemGroupProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.interfaces.EJLovRenderer;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;

public class EJRWTStandardLovRenderer implements EJLovRenderer
{
    final int                       OK_ACTION_COMMAND     = 1;
    final int                       CANCEL_ACTION_COMMAND = 2;

    private EJItemLovController     _itemToValidate;
    private EJLovDisplayReason      _displayReason;
    private EJLovController         _lovController;
    private TableViewer             _tableViewer;
    private boolean                 _validate             = true;

    private EJFrameworkManager      _frameworkManager;
    protected EJRWTAbstractDialog     _dialog;

    private EJInternalBlock         _block;
    private FilteredContentProvider _filteredContentProvider;
    private List<EJDataRecord>      _tableBaseRecords      = new ArrayList<EJDataRecord>();

    @Override
    public Object getGuiComponent()
    {
        return _dialog;
    }

    protected EJLovController getLovController()
    {
        return _lovController;
    }

    @Override
    public EJQueryScreenRenderer getQueryScreenRenderer()
    {
        return null;
    }

    @Override
    public void refreshLovRendererProperty(String propertyName)
    {
    }

    @Override
    public void refreshItemProperty(String itemName, EJManagedScreenProperty managedItemPropertyType, EJDataRecord record)
    {
    }

    @Override
    public void refreshItemRendererProperty(String itemName, String propertyName)
    {
    }

    @Override
    public void synchronize()
    {
    }

    @Override
    public void initialiseRenderer(EJLovController lovController)
    {
        this._lovController = lovController;
        _frameworkManager = _lovController.getFrameworkManager();
        _block = _lovController.getBlock();

    }

    protected Control createToolbar(Composite parent)
    {
        return null;
    }

    protected void buildGui()
    {
        int width = _lovController.getDefinitionProperties().getWidth();
        int height = _lovController.getDefinitionProperties().getHeight();

        _dialog = new EJRWTAbstractDialog(getRWTManager().getShell())
        {
            private static final long serialVersionUID = -4685316941898120169L;

            @Override
            public boolean close()
            {
                _tableViewer = null;
                _dialog = null;
                return super.close();
            }
            
           

            @Override
            public void createBody(Composite parent)
            {
                GridLayout layout = new GridLayout();
                layout.marginWidth = 0;
                // layout.horizontalSpacing = 0;
                layout.marginLeft = 0;
                layout.marginRight = 0;
                layout.marginHeight = 0;
                // layout.verticalSpacing = 0;
                layout.marginBottom = 0;
                layout.marginTop = 0;
                parent.setLayout(layout);
                int style = SWT.VIRTUAL;
                EJFrameworkExtensionProperties rendererProp = _lovController.getDefinitionProperties().getLovRendererProperties();

                if (!rendererProp.getBooleanProperty(EJRWTMultiRecordBlockDefinitionProperties.HIDE_TABLE_BORDER, false))
                {
                    style = style | SWT.BORDER;
                }

               
                final EJRWTAbstractFilteredTable filterTree;
                Table table;

                filterTree = new EJRWTAbstractFilteredTable(parent, style)
                {
                    @Override
                    public void filter(String filter)
                    {
                        if (_filteredContentProvider != null
                                && (filter == null && _filteredContentProvider.getFilter() != null || !filter.equals(_filteredContentProvider.getFilter())))
                        {
                            _filteredContentProvider.setFilter(filter);
                            getViewer().setInput(filter);
                        }
                    }

                    @Override
                    protected boolean doCreateCustomComponents(Composite parent)
                    {
                        return createToolbar(parent) != null;
                    }

                    @Override
                    protected TableViewer doCreateTableViewer(Composite parent, int style)
                    {

                        _tableViewer = new TableViewer(new Table(parent, style));
                        return _tableViewer;
                    }
                };
                table = (_tableViewer = filterTree.getViewer()).getTable();

                table.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
                table.setLinesVisible(rendererProp.getBooleanProperty(EJRWTMultiRecordBlockDefinitionProperties.SHOW_VERTICAL_LINES, true));
                table.setHeaderVisible(rendererProp.getBooleanProperty(EJRWTMultiRecordBlockDefinitionProperties.SHOW_HEADING_PROPERTY, true));

                EJRWTTableViewerColumnFactory factory = new EJRWTTableViewerColumnFactory(_tableViewer);
                ColumnViewerToolTipSupport.enableFor(_tableViewer);
                Collection<EJItemGroupProperties> allItemGroupProperties = _block.getProperties().getScreenItemGroupContainer(EJScreenType.MAIN)
                        .getAllItemGroupProperties();
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

                final EJBlockController blockController = _block.getBlockController();
                // _mainPane.addControlListener(new
                // TableAutoResizeAdapter(table));
                /*
                 * _tableViewer.setContentProvider(new ILazyContentProvider() {
                 * 
                 * @Override public void inputChanged(Viewer arg0, Object arg1,
                 * Object arg2) {
                 * _tableViewer.setItemCount(blockController.getDataBlock().
                 * getBlockRecordCount());
                 * 
                 * }
                 * 
                 * @Override public void dispose() { // TODO Auto-generated
                 * method stub
                 * 
                 * }
                 * 
                 * @Override public void updateElement(int index) { EJDataRecord
                 * record = null; if (index <=
                 * blockController.getDataBlock().getBlockRecordCount()) {
                 * record = blockController.getDataBlock().getRecord(index);
                 * 
                 * } _tableViewer.replace(record, index);
                 * 
                 * } });
                 */
                _tableViewer.setContentProvider(_filteredContentProvider = new FilteredContentProvider()
                {

                    private static final long serialVersionUID = 7262009393527533868L;

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
                        _tableBaseRecords.clear();

                        if (arg2 != null && arg2.equals(filter) && filter.trim().length() > 0)
                        {
                            // filter

                            for (EJDataRecord record : blockController.getBlock().getRecords())
                            {
                                if (matchItem(record))
                                {
                                    _tableBaseRecords.add(record);
                                }
                            }
                        }
                        else
                        {
                            filter = null;
                            if (filterTree != null)
                            {
                                filterTree.clearText();
                            }
                            _tableBaseRecords.addAll(blockController.getBlock().getRecords());
                        }
                    }

                    @Override
                    public void dispose()
                    {
                    }

                    @Override
                    public Object[] getElements(Object arg0)
                    {
                        return _tableBaseRecords.toArray();
                    }
                });
                _tableViewer.addDoubleClickListener(new IDoubleClickListener()
                {

                    @Override
                    public void doubleClick(DoubleClickEvent arg0)
                    {
                        buttonPressed(OK_ACTION_COMMAND);
                    }
                });
                _tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
                {

                    @Override
                    public void selectionChanged(SelectionChangedEvent arg0)
                    {
                        if (!_validate)
                        {
                            return;
                        }

                        _validate = false;

                        try
                        {
                            EJDataRecord record = getFocusedRecord();
                            if (_lovController.getFocusedRecord() == null || _lovController.getFocusedRecord() != record)
                            {
                                _lovController.newRecordInstance(record);
                            }
                        }
                        finally
                        {
                            _validate = true;
                        }

                    }
                });

            }

            @Override
            protected void createButtonsForButtonBar(Composite parent)
            {

                createButton(parent, OK_ACTION_COMMAND, "OK", true);
                createButton(parent, CANCEL_ACTION_COMMAND, "Cancel", false);
            }

            @Override
            public void canceled()
            {
                _lovController.lovCompleted(_itemToValidate, null);
               close();
            }

            @Override
            protected void buttonPressed(int buttonId)
            {
                switch (buttonId)
                {
                    case OK_ACTION_COMMAND:
                    {
                        _lovController.lovCompleted(_itemToValidate, _lovController.getFocusedRecord());
                        if (_dialog != null)
                        {
                            _dialog.close();
                        }
                        break;
                    }
                    case CANCEL_ACTION_COMMAND:
                    {
                        _lovController.lovCompleted(_itemToValidate, null);
                        if (_dialog != null)
                        {
                            _dialog.close();
                        }
                        break;
                    }

                    default:
                        _lovController.lovCompleted(_itemToValidate, null);

                        break;
                }
                super.buttonPressed(buttonId);

            }
        };
        _dialog.create();
        _tableViewer.setInput(new Object());
        selectRow(0);
        _dialog.getShell().setSize(width + 80, height + 100);// add dialog
                                                             // border
                                                             // offsets
    }

    @Override
    public EJLovDisplayReason getDisplayReason()
    {
        return _displayReason;
    }

    @Override
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

    @Override
    public void enterQuery(EJDataRecord record)
    {
        // No user query is permitted on this standard lov
    }

    @Override
    public void blockCleared()
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            _tableViewer.setInput(new Object());
        }

    }

    EJRWTApplicationManager getRWTManager()
    {
        return (EJRWTApplicationManager) _frameworkManager.getApplicationManager();
    }

    @Override
    public void displayLov(EJItemLovController itemToValidate, EJLovDisplayReason displayReason)
    {
        _itemToValidate = itemToValidate;
        _displayReason = displayReason;
        buildGui();
        String title = null;
        if (_itemToValidate.getLovMappingProperties().getLovDisplayName() != null)
        {
            title = _itemToValidate.getLovMappingProperties().getLovDisplayName();
        }
        _dialog.getShell().setText(title == null ? "" : title);

        _dialog.setButtonEnable(OK_ACTION_COMMAND, _itemToValidate.getManagedLovItemRenderer().isEditAllowed());
        selectRow(0);
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            _tableViewer.getTable().forceFocus();
        }

        _dialog.centreLocation();
        _dialog.open();
        //_dialog.activateDialog();
    }

    @Override
    public void refreshAfterChange(EJDataRecord record)
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            _tableViewer.refresh(record);
        }
    }

    @Override
    public void recordSelected(EJDataRecord record)
    {
        if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
        {
            _tableViewer.setSelection(record != null ? new StructuredSelection(record) : new StructuredSelection(), true);
        }
    }

    @Override
    public int getDisplayedRecordCount()
    {
        return _tableBaseRecords.size();
    }

    @Override
    public int getDisplayedRecordNumber(EJDataRecord record)
    {
        return _tableBaseRecords.indexOf(record);
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

            return _tableBaseRecords.get(displayedRecordNumber);
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

    public void selectRow(int selectedRow)
    {

        if (_tableViewer != null && !_tableViewer.getTable().isDisposed() && getDisplayedRecordCount() > selectedRow)
        {
            _tableViewer.setSelection(new StructuredSelection(getRecordAt(selectedRow)), true);
        }
    }

    @Override
    public void executingQuery()
    {
        // TODO Auto-generated method stub
    }

    protected void clearFilter()
    {
        if (_filteredContentProvider != null)
        {
            _filteredContentProvider.setFilter(null);
        }
    }

    @Override
    public void queryExecuted()
    {
        try
        {
            _validate = false;
            if (_tableViewer != null && !_tableViewer.getTable().isDisposed())
            {
                clearFilter();
                _tableViewer.setInput(new Object());

            }
            selectRow(0);

        }
        finally
        {
            _validate = true;
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
            EJFrameworkExtensionProperties blockProperties = itemProps.getLovRendererRequiredProperties();

            EJRWTAppItemRenderer itemRenderer = (EJRWTAppItemRenderer) renderer.getUnmanagedRenderer();
            ColumnLabelProvider labelProvider = itemRenderer.createColumnLabelProvider(itemProps, item);
            if (labelProvider != null)
            {

                int _widthHint = blockProperties.getIntProperty(EJRWTMultiRecordBlockDefinitionProperties.DISPLAY_WIDTH_PROPERTY, 0);
                // if no width define in block properties use item renderer pref
                // width
                if (_widthHint == 0)
                {
                    if (itemProps.getLabel() != null)
                    {
                        _widthHint = itemProps.getLabel().length() + 2;// offset
                    }
                    else
                    {
                        _widthHint = 5;
                    }
                }

                String alignmentProperty = blockProperties.getStringProperty(EJRWTMultiRecordBlockDefinitionProperties.COLUMN_ALIGNMENT);

                TableViewerColumn viewerColumn = factory.createColumn(itemProps.getLabel(), _widthHint, labelProvider, getComponentStyle(alignmentProperty));
                TableColumn column = viewerColumn.getColumn();
                column.setToolTipText(itemProps.getHint());

                column.setMoveable(blockProperties.getBooleanProperty(EJRWTMultiRecordBlockDefinitionProperties.ALLOW_COLUMN_REORDER, true));
                column.setResizable(blockProperties.getBooleanProperty(EJRWTMultiRecordBlockDefinitionProperties.ALLOW_COLUMN_RESIZE, true));
                if (blockProperties.getBooleanProperty(EJRWTMultiRecordBlockDefinitionProperties.ALLOW_ROW_SORTING, true))
                {
                    EJRWTAbstractTableSorter columnSorter = itemRenderer.getColumnSorter(itemProps, item);
                    if (columnSorter != null)
                    {
                        new EJRWTTableSortSelectionListener(_tableViewer, column, columnSorter, SWT.UP, false);
                    }
                }

                // ensure that the width property of the table column is in
                // Characters
                Font font = labelProvider.getFont(new Object());
                if (font == null)
                {
                    font = _tableViewer.getTable().getFont();
                }
                if (font != null)
                {
                    float avgCharWidth = EJRWTImageRetriever.getGraphicsProvider().getAvgCharWidth(font);
                    if (avgCharWidth > 0)
                    {
                        column.setWidth((int) ((column.getWidth() + 1) * avgCharWidth));// add
                                                                                        // +1
                                                                                        // padding
                    }
                }
                return labelProvider;

            }
        }
        return null;
    }

    protected int getComponentStyle(String alignmentProperty)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJRWTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT))
            {
                return SWT.RIGHT;
            }
            else if (alignmentProperty.equals(EJRWTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_CENTER))
            {
                return SWT.CENTER;
            }
        }
        return SWT.LEFT;
    }

}
