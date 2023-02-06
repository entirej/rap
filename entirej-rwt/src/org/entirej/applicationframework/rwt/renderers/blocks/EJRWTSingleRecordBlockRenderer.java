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
package org.entirej.applicationframework.rwt.renderers.blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.rwt.EJRWTAsync;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.components.EJRWTAbstractLabel;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTDeleteAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTInsertAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTQueryAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTUpdateAction;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;
import org.entirej.applicationframework.rwt.layout.EJRWTScrolledComposite;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppBlockRenderer;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.renderers.blocks.definition.interfaces.EJRWTSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.item.EJRWTComboItemRenderer;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTInsertScreenRenderer;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTQueryScreenRenderer;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTUpdateScreenRenderer;
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
import org.entirej.framework.core.enumerations.EJSeparatorOrientation;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.properties.EJCoreMainScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.containers.interfaces.EJItemGroupPropertiesContainer;
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
import org.entirej.framework.core.renderers.registry.EJMainScreenItemRendererRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJRWTSingleRecordBlockRenderer implements EJRWTAppBlockRenderer, KeyListener
{
    final FormToolkit                        toolkit            = new FormToolkit(Display.getDefault());
    final Logger                             logger             = LoggerFactory.getLogger(EJRWTSingleRecordBlockRenderer.class);

    private boolean                          _showFocusedBorder = false;
    private EJManagedItemRendererWrapper     _firstNavigationalItem;
    private EJEditableBlockController        _block;
    private EJMainScreenItemRendererRegister _mainItemRegister;
    private EJRWTEntireJGridPane             _mainPane;
    private boolean                          _isFocused         = false;

    private EJRWTQueryScreenRenderer         _queryScreenRenderer;
    private EJRWTInsertScreenRenderer        _insertScreenRenderer;
    private EJRWTUpdateScreenRenderer        _updateScreenRenderer;

    private List<String>                     _actionkeys        = new ArrayList<String>();
    private Map<KeyInfo, String>             _actionInfoMap     = new HashMap<EJRWTKeysUtil.KeyInfo, String>();
    private Display                          dispaly            = Display.getDefault();

    protected EJInternalEditableBlock getBlock()
    {
        return _block.getBlock();
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
        EJManagedItemRendererWrapper itemRenderer = _mainItemRegister.getManagedItemRendererForItem(itemName);

        if (itemRenderer == null)
        {
            return;
        }

        switch (managedItemPropertyType)
        {
            case EDIT_ALLOWED:
                itemRenderer.setEditAllowed((itemRenderer.isReadOnly() || _block.getBlock().getProperties().isControlBlock()) && itemRenderer.getItem().getProperties().isEditAllowed());
                break;
            case MANDATORY:
                itemRenderer.setMandatory(itemRenderer.getItem().getProperties().isMandatory());
                break;
            case VISIBLE:
                itemRenderer.setVisible(itemRenderer.getItem().getProperties().isVisible());
                break;
            case HINT:
                itemRenderer.setHint(itemRenderer.getItem().getProperties().getHint());
                break;
            case LABEL:
                itemRenderer.setLabel(itemRenderer.getItem().getProperties().getLabel());
                break;
            case SCREEN_ITEM_VISUAL_ATTRIBUTE:
                itemRenderer.setVisualAttribute(itemRenderer.getItem().getProperties().getVisualAttributeProperties());
                break;
            case ITEM_INSTANCE_VISUAL_ATTRIBUTE:
                if (record == getFocusedRecord())
                {
                    refreshRecordInstanceVA(record);
                }
                break;
            case ITEM_INSTANCE_HINT_TEXT:
                if (record == getFocusedRecord())
                {
                    refreshRecordInstanceHintText(record);
                }
                break;
        }
    }

    @Override
    public void refreshItemRendererProperty(String itemName, String propertyName)
    {
        _mainItemRegister.getManagedItemRendererForItem(itemName).refreshItemRendererProperty(propertyName);
    }

    @Override
    public Object getGuiComponent()
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
        _mainItemRegister = new EJMainScreenItemRendererRegister(_block);
        _queryScreenRenderer = new EJRWTQueryScreenRenderer();
        _insertScreenRenderer = new EJRWTInsertScreenRenderer();
        _updateScreenRenderer = new EJRWTUpdateScreenRenderer();
    }

    @Override
    public boolean isCurrentRecordDirty()
    {
        return _mainItemRegister.changesMade();
    }

    @Override
    public void synchronize()
    {
        // implementing this method caused modified values to be overridden by
        // the screen values
    }

    @Override
    public void blockCleared()
    {

        EJRWTAsync.runUISafe(dispaly,() -> {

            logger.trace("START blockCleared");
            _mainItemRegister.clearRegisteredValues();
            logger.trace("END blockCleared");
            notifyStatus();

        });
    }

    public void savePerformed()
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
        if (getBlock().getInsertScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define an Insert Screen Renderer for this form before an insert operation can be performed.");
            getBlock().getForm().getMessenger().handleMessage(message);
        }
        else
        {
            getBlock().getInsertScreenRenderer().open(record);
        }
    }

    @Override
    public void enterQuery(EJDataRecord record)
    {
        if (getBlock().getQueryScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define a Query Screen Renderer for this form before a query operation can be performed.");
            getBlock().getForm().getMessenger().handleMessage(message);
        }
        else
        {
            getBlock().getQueryScreenRenderer().open(record);
        }
    }

    @Override
    public void enterUpdate(EJDataRecord recordToUpdate)
    {
        if (getBlock().getUpdateScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define an Update Screen Renderer for this form before an update operation can be performed.");
            getBlock().getForm().getMessenger().handleMessage(message);
        }
        else
        {
            getBlock().getUpdateScreenRenderer().open(recordToUpdate);
        }
    }

    @Override
    public int getDisplayedRecordNumber(EJDataRecord record)
    {
        return _block.getDataBlock().getRecordNumber(record);
    }

    @Override
    public int getDisplayedRecordCount()
    {
        return _block.getDataBlock().getBlockRecordCount();
    }

    @Override
    public EJDataRecord getRecordAt(int displayedRecordNumber)
    {
        if (displayedRecordNumber > -1 && displayedRecordNumber < getDisplayedRecordCount())
        {

            return _block.getRecord(displayedRecordNumber);
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
    public EJDataRecord getFocusedRecord()
    {
        return _mainItemRegister.getRegisteredRecord();
    }

    @Override
    public void queryExecuted()
    {

        EJRWTAsync.runUISafe(dispaly,() -> {
            if (getFocusedRecord() == null)
            {
                _mainItemRegister.register(getFirstRecord());
            }
            notifyStatus();

        });
    }

    @Override
    public void recordDeleted(int dataBlockRecordNumber)
    {
        EJDataRecord recordAt = getRecordAt(dataBlockRecordNumber > 1 ? dataBlockRecordNumber - 2 : 0);

        if (recordAt == null)

        {
            recordAt = getLastRecord();
        }
        recordSelected(recordAt);
    }

    @Override
    public void recordInserted(EJDataRecord record)
    {
        if (record != null)
        {
            logger.trace("START recordInserted");
            _mainItemRegister.register(record);
            logger.trace("END recordInserted");
        }
        notifyStatus();
    }

    @Override
    public void refreshAfterChange(EJDataRecord record)
    {
        logger.trace("START refreshAfterChange");
        _mainItemRegister.refreshAfterChange(record);
        logger.trace("END recordUpdated");
    }

    @Override
    public void recordSelected(EJDataRecord record)
    {


            if (record != null)
            {
                logger.trace("START recordSelected");
                synchronize();

                _mainItemRegister.register(record);
                Collection<EJManagedItemRendererWrapper> registeredRenderers = _mainItemRegister.getRegisteredRenderers();
                for (EJManagedItemRendererWrapper wrapper : registeredRenderers)
                {
                    if (wrapper.getUnmanagedRenderer() instanceof EJRWTComboItemRenderer)
                    {
                        if (((EJRWTComboItemRenderer) wrapper.getUnmanagedRenderer()).isLovInitialiedOnValueSet())
                            wrapper.getUnmanagedRenderer().refreshItemRenderer();
                    }
                }
                logger.trace("END recordSelected");
            }
            notifyStatus();
        
    }

    private void refreshRecordInstanceVA(EJDataRecord record)
    {
        for (EJManagedItemRendererWrapper wrapper : _mainItemRegister.getRegisteredRenderers())
        {
            // The screen item visual attribute has priority over the record
            // instance va
            if (record.containsItem(wrapper.getRegisteredItemName()) && wrapper.getItem().getProperties().getVisualAttributeProperties() == null)
            {
                if (record.getItem(wrapper.getRegisteredItemName()).getVisualAttribute() != null)
                {
                    wrapper.setVisualAttribute(record.getItem(wrapper.getRegisteredItemName()).getVisualAttribute());
                }
                else
                {
                    if (wrapper.getVisualAttributeProperties() != null)
                    {
                        wrapper.setVisualAttribute(null);
                    }
                }
            }
        }
    }

    private void refreshRecordInstanceHintText(EJDataRecord record)
    {
        for (EJManagedItemRendererWrapper wrapper : _mainItemRegister.getRegisteredRenderers())
        {
            if (record.containsItem(wrapper.getRegisteredItemName()))
            {
                if (record.getItem(wrapper.getRegisteredItemName()).getHint() != null)
                {
                    wrapper.setHint(record.getItem(wrapper.getRegisteredItemName()).getHint());
                }
                else
                {
                    EJScreenItemController screenItem = record.getBlock().getScreenItem(EJScreenType.MAIN, wrapper.getRegisteredItemName());
                    if (screenItem != null)
                    {
                        wrapper.setHint(screenItem.getProperties().getHint());
                    }
                }
            }
        }
    }

    @Override
    public void gainFocus()
    {
        logger.trace("START gainFocus");
        if (_firstNavigationalItem != null)
        {
            _firstNavigationalItem.gainFocus();
        }
        else
        {
            if(!_mainPane.isDisposed())
                _mainPane.forceFocus();
        }
        setHasFocus(true);
        logger.trace("END gainFocus");

    }

    @Override
    public void setHasFocus(boolean focus)
    {
        logger.trace("START setHasFocus. Focus: {}", focus);
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
        logger.trace("END hasFocus");
        notifyStatus();
    }

    /**
     * Enables a red border around this controller. This will indicate that the
     * container held by this controller has cursor focus.
     * 
     * @param pFocused
     *            If <code>true</code> is passed then the border will be
     *            displayed, if <code>false</code> is passed then no border will
     *            be shown.
     */
    private void showFocusedBorder(boolean focused)
    {
    }

    @Override
    public void setFocusToItem(EJScreenItemController item)
    {
        if (item == null)
        {
            return;
        }

        logger.trace("START setFocusToItem. Item: {}", item.getName());

        EJManagedItemRendererWrapper renderer = _mainItemRegister.getManagedItemRendererForItem(item.getProperties().getReferencedItemName());
        if (renderer != null)
        {
            renderer.gainFocus();
        }
        logger.trace("END setFocusToItem");
    }

    protected void setShowFocusedBorder(boolean show)
    {
        _showFocusedBorder = show;
    }

    protected EJMainScreenItemRendererRegister getMainItemRegister()
    {
        return _mainItemRegister;
    }

    protected void setFirstNavigationalItem(EJManagedItemRendererWrapper firstNavigationalItem)
    {
        _firstNavigationalItem = firstNavigationalItem;
    }

    @Override
    public void buildGuiComponent(EJRWTEntireJGridPane blockCanvas)
    {

        EJBlockProperties blockProperties = _block.getProperties();
        EJMainScreenProperties mainScreenProperties = blockProperties.getMainScreenProperties();

        EJFrameworkExtensionProperties brendererProperties = blockProperties.getBlockRendererProperties();

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
        blockCanvas.setLayoutData(gridData);

        ScrolledComposite scrollComposite = null;

        EJFrameworkExtensionProperties sectionProperties = null;
        if (brendererProperties != null)
        {
            sectionProperties = brendererProperties.getPropertyGroup(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR);
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

                group.setLayout(new FillLayout());
                group.setLayoutData(gridData);

                scrollComposite = new EJRWTScrolledComposite(group, SWT.V_SCROLL | SWT.H_SCROLL);

                _mainPane = new EJRWTEntireJGridPane(scrollComposite, mainScreenProperties.getNumCols());
                group.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
                _mainPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
                group.setText(frameTitle);
                section.setClient(group);

            }
            else
            {
                Composite composite = new Composite(blockCanvas, mainScreenProperties.getDisplayFrame() ? SWT.BORDER : SWT.NONE);
                composite.setLayout(new FillLayout());
                scrollComposite = new EJRWTScrolledComposite(composite, SWT.V_SCROLL | SWT.H_SCROLL);
                _mainPane = new EJRWTEntireJGridPane(scrollComposite, mainScreenProperties.getNumCols());
                _mainPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);

                _mainPane.setLayoutData(gridData);
                section.setClient(composite);
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

                group.setText(frameTitle);

                scrollComposite = new EJRWTScrolledComposite(group, SWT.V_SCROLL | SWT.H_SCROLL);
                _mainPane = new EJRWTEntireJGridPane(scrollComposite, mainScreenProperties.getNumCols());

            }
            else
            {
                Composite composite = new Composite(blockCanvas, mainScreenProperties.getDisplayFrame() ? SWT.BORDER : SWT.NONE);
                composite.setLayout(new FillLayout());
                scrollComposite = new EJRWTScrolledComposite(composite, SWT.V_SCROLL | SWT.H_SCROLL);
                _mainPane = new EJRWTEntireJGridPane(scrollComposite, mainScreenProperties.getNumCols());
                _mainPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);

                composite.setLayoutData(gridData);
            }
        }

        EJFrameworkExtensionProperties rendererProp = EJCoreProperties.getInstance().getApplicationDefinedProperties();
        if (rendererProp != null)
        {
            EJFrameworkExtensionProperties propertyGroup = rendererProp.getPropertyGroup(EJRWTSingleRecordBlockDefinitionProperties.ACTION_GROUP);
            if (propertyGroup != null)
            {
                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_QUERY_KEY), EJRWTSingleRecordBlockDefinitionProperties.ACTION_QUERY_KEY);
                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_INSERT_KEY), EJRWTSingleRecordBlockDefinitionProperties.ACTION_INSERT_KEY);
                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_UPDATE_KEY), EJRWTSingleRecordBlockDefinitionProperties.ACTION_UPDATE_KEY);
                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_DELETE_KEY), EJRWTSingleRecordBlockDefinitionProperties.ACTION_DELETE_KEY);
                addActionKeyinfo(propertyGroup.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ACTION_REFRESH_KEY), EJRWTSingleRecordBlockDefinitionProperties.ACTION_REFRESH_KEY);

            }
        }

        hookKeyListener(_mainPane);
        hookFocusListener(_mainPane);
        _mainPane.cleanLayout();
        EJDataRecord registeredRecord = _mainItemRegister.getRegisteredRecord();
        _mainItemRegister.resetRegister();
        EJItemGroupPropertiesContainer container = blockProperties.getScreenItemGroupContainer(EJScreenType.MAIN);
        Collection<EJItemGroupProperties> itemGroupProperties = container.getAllItemGroupProperties();
        for (EJItemGroupProperties ejItemGroupProperties : itemGroupProperties)
        {
            createItemGroup(_mainPane, ejItemGroupProperties);
        }

        _mainItemRegister.clearRegisteredValues();
        if (registeredRecord == null)
        {
            registeredRecord = getFirstRecord();
        }
        if (registeredRecord != null)
        {

            _mainItemRegister.register(registeredRecord);
        }
        _mainPane.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                setHasFocus(true);
            }
        });

        if (scrollComposite != null)
        {
            scrollComposite.setContent(_mainPane);
            scrollComposite.setLayout(new FillLayout());

            scrollComposite.setExpandHorizontal(true);

            scrollComposite.setExpandVertical(true);

            scrollComposite.setMinSize(_mainPane.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));

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

    private void createItemGroup(Composite parent, EJItemGroupProperties groupProperties)
    {
        EJRWTEntireJGridPane groupPane;

        if (groupProperties.isSeparator())
        {

            int style = SWT.SEPARATOR;

            if (groupProperties.getSeparatorOrientation() == EJSeparatorOrientation.HORIZONTAL)
            {
                style = style | SWT.HORIZONTAL;
            }
            else
            {
                style = style | SWT.VERTICAL;
            }

            Label layoutBody = new Label(parent, style);
            layoutBody.setLayoutData(createItemGroupGridData(groupProperties));

            switch (groupProperties.getSeparatorLineStyle())
            {
                case DASHED:
                    layoutBody.setData(EJ_RWT.CUSTOM_VARIANT, "separator_dashed");
                    break;
                case DOTTED:
                    layoutBody.setData(EJ_RWT.CUSTOM_VARIANT, "separator_dotted");
                    break;
                case DOUBLE:
                    layoutBody.setData(EJ_RWT.CUSTOM_VARIANT, "separator_double");
                    break;

                default:
                    layoutBody.setData(EJ_RWT.CUSTOM_VARIANT, "separator");
                    break;
            }
            return;

        }
        String frameTitle = groupProperties.getFrameTitle();
        EJFrameworkExtensionProperties rendererProperties = groupProperties.getRendererProperties();

        boolean hasGroup = groupProperties.dispayGroupFrame() && frameTitle != null && frameTitle.length() > 0;
        if (hasGroup)
        {

            if (rendererProperties != null && rendererProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE) != null
                    && !EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_GROUP.equals(rendererProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE)))
            {
                int style = ExpandableComposite.TITLE_BAR;

                String mode = rendererProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE);
                if (EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_TWISTIE.equals(mode))
                {
                    style = style | ExpandableComposite.TWISTIE;
                }
                else if (EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_TREE_NODE.equals(mode))
                {
                    style = style | ExpandableComposite.TREE_NODE;
                }
                if (rendererProperties.getBooleanProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDED, true))
                {
                    style = style | ExpandableComposite.EXPANDED;
                }
                Section section = toolkit.createSection(parent, style);
                section.setText(frameTitle);
                EJRWTImageRetriever.getGraphicsProvider().rendererSection(section);
                section.setLayoutData(createItemGroupGridData(groupProperties));
                parent = section;
                hookKeyListener(section);
                section.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mouseDown(MouseEvent arg0)
                    {
                        setHasFocus(true);
                    }
                });
                groupPane = new EJRWTEntireJGridPane(parent, groupProperties.getNumCols());
                groupPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);

                String customCSSKey = rendererProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_CSS_KEY);

                if (customCSSKey != null && customCSSKey.trim().length() > 0)
                {
                    groupPane.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                }

                // groupPane.getLayout().marginRight = 5;
                // groupPane.getLayout().marginLeft = 5;
                section.setClient(groupPane);

                final EJFrameworkExtensionPropertyList propertyList = rendererProperties.getPropertyList(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTIONS);

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

                Composite group;

                if (hasGroup)
                {
                    Group g = new Group(parent, SWT.NONE);
                    g.setText(frameTitle);
                    group = g;
                }
                else
                {
                    group = new Composite(parent, groupProperties.dispayGroupFrame() ? SWT.BORDER : SWT.NONE);
                }
                group.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);
                String customCSSKey = null;
                if (rendererProperties != null)
                {
                    customCSSKey = rendererProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_CSS_KEY);

                    if (customCSSKey != null && customCSSKey.trim().length() > 0)
                    {
                        group.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                    }
                }

                group.setLayout(new FillLayout());
                group.setLayoutData(createItemGroupGridData(groupProperties));

                parent = group;
                hookKeyListener(group);
                group.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mouseDown(MouseEvent arg0)
                    {
                        setHasFocus(true);
                    }
                });
                groupPane = new EJRWTEntireJGridPane(parent, groupProperties.getNumCols());
                groupPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);

                if (customCSSKey != null && customCSSKey.trim().length() > 0)
                {
                    groupPane.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                }
                // groupPane.getLayout().marginRight = 5;
                // groupPane.getLayout().marginLeft = 5;
            }
        }
        else
        {
            groupPane = new EJRWTEntireJGridPane(parent, groupProperties.getNumCols(), groupProperties.dispayGroupFrame() ? SWT.BORDER : SWT.NONE);
            groupPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_ITEM_GROUP);

            if (rendererProperties != null)
            {
                String customCSSKey = rendererProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_CSS_KEY);

                if (customCSSKey != null && customCSSKey.trim().length() > 0)
                {
                    groupPane.setData(EJ_RWT.CUSTOM_VARIANT, customCSSKey);
                }
            }

            if (groupProperties.dispayGroupFrame())
            {
                // groupPane.getLayout().marginRight = 5;
                // groupPane.getLayout().marginLeft = 5;
            }
        }

        groupPane.getLayout().verticalSpacing = 1;
        hookKeyListener(groupPane);
        groupPane.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDown(MouseEvent arg0)
            {
                setHasFocus(true);
            }
        });

        groupPane.setPaneName(groupProperties.getName());
        if (!hasGroup)
        {
            groupPane.setLayoutData(createItemGroupGridData(groupProperties));
        }
        // items adding
        Collection<EJScreenItemProperties> itemProperties = groupProperties.getAllItemProperties();
        for (EJScreenItemProperties screenItemProperties : itemProperties)
        {
            createScreenItem(groupPane, (EJCoreMainScreenItemProperties) screenItemProperties);
        }

        // build sub groups
        EJItemGroupPropertiesContainer groupPropertiesContainer = groupProperties.getChildItemGroupContainer();
        Collection<EJItemGroupProperties> itemGroupProperties = groupPropertiesContainer.getAllItemGroupProperties();
        for (EJItemGroupProperties ejItemGroupProperties : itemGroupProperties)
        {
            createItemGroup(groupPane, ejItemGroupProperties);
        }
    }

    static GridData createItemGroupGridData(EJItemGroupProperties groupProperties)
    {
        GridData gridData = new GridData(GridData.FILL_BOTH);
        if (groupProperties.getWidth() > 0)
        {
            gridData.widthHint = groupProperties.getWidth();
        }
        if (groupProperties.getHeight() > 0)
        {
            gridData.heightHint = groupProperties.getHeight();
        }
        gridData.horizontalSpan = groupProperties.getXspan();
        gridData.verticalSpan = groupProperties.getYspan();
        gridData.grabExcessHorizontalSpace = groupProperties.canExpandHorizontally();
        gridData.grabExcessVerticalSpace = groupProperties.canExpandVertically();

        if (gridData.grabExcessHorizontalSpace)
        {
            gridData.minimumWidth = groupProperties.getWidth();
        }
        if (gridData.grabExcessVerticalSpace)
        {
            gridData.minimumHeight = groupProperties.getHeight();
        }

        if (groupProperties.getHorizontalAlignment() != null)
        {
            switch (groupProperties.getHorizontalAlignment())
            {
                case CENTER:
                    gridData.horizontalAlignment = SWT.CENTER;
                    gridData.grabExcessHorizontalSpace = true;
                    break;
                case BEGINNING:
                    gridData.horizontalAlignment = SWT.BEGINNING;
                    break;
                case END:
                    gridData.horizontalAlignment = SWT.END;
                    gridData.grabExcessHorizontalSpace = true;
                    break;

                default:
                    break;
            }
        }
        if (groupProperties.getVerticalAlignment() != null)
        {
            switch (groupProperties.getVerticalAlignment())
            {
                case CENTER:
                    gridData.verticalAlignment = SWT.CENTER;
                    gridData.grabExcessVerticalSpace = true;
                    break;
                case BEGINNING:
                    gridData.verticalAlignment = SWT.BEGINNING;
                    break;
                case END:
                    gridData.verticalAlignment = SWT.END;
                    gridData.grabExcessVerticalSpace = true;
                    break;

                default:
                    break;
            }
        }
        return gridData;
    }

    private GridData createBlockLableGridData(EJFrameworkExtensionProperties blockRequiredItemProperties)
    {
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.grabExcessHorizontalSpace = false;
        gridData.verticalSpan = blockRequiredItemProperties.getIntProperty(EJRWTSingleRecordBlockDefinitionProperties.MAIN_YSPAN_PROPERTY, 1);

        gridData.verticalAlignment = SWT.CENTER;
        if (gridData.verticalSpan > 1 || blockRequiredItemProperties.getBooleanProperty(EJRWTSingleRecordBlockDefinitionProperties.MAIN_EXPAND_Y_PROPERTY, false))
        {
            gridData.verticalIndent = 2;
            gridData.verticalAlignment = SWT.TOP;
        }
        return gridData;
    }

    private GridData createBlockItemGridData(EJRWTAppItemRenderer itemRenderer, EJFrameworkExtensionProperties blockRequiredItemProperties, Control control)
    {

        boolean grabExcessVerticalSpace = blockRequiredItemProperties.getBooleanProperty(EJRWTSingleRecordBlockDefinitionProperties.MAIN_EXPAND_Y_PROPERTY, false);
        boolean grabExcessHorizontalSpace = blockRequiredItemProperties.getBooleanProperty(EJRWTSingleRecordBlockDefinitionProperties.MAIN_EXPAND_X_PROPERTY, false);
        GridData gridData;
        if (grabExcessVerticalSpace && grabExcessHorizontalSpace)
        {
            gridData = new GridData(GridData.FILL_BOTH);
        }
        else if (!grabExcessVerticalSpace && grabExcessHorizontalSpace)
        {
            gridData = new GridData(GridData.FILL_BOTH);
        }
        else if (grabExcessVerticalSpace && !grabExcessHorizontalSpace)
        {
            gridData = new GridData(GridData.FILL_VERTICAL);
        }
        else
        {
            gridData = new GridData(GridData.FILL_VERTICAL);
        }
        gridData.horizontalSpan = blockRequiredItemProperties.getIntProperty(EJRWTSingleRecordBlockDefinitionProperties.MAIN_XSPAN_PROPERTY, 1);
        gridData.verticalSpan = blockRequiredItemProperties.getIntProperty(EJRWTSingleRecordBlockDefinitionProperties.MAIN_YSPAN_PROPERTY, 1);
        gridData.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
        gridData.grabExcessVerticalSpace = grabExcessVerticalSpace;

        if (!grabExcessVerticalSpace)
        {
            gridData.verticalAlignment = SWT.CENTER;
        }

        int displayedWidth = blockRequiredItemProperties.getIntProperty(EJRWTSingleRecordBlockDefinitionProperties.DISPLAYED_WIDTH_PROPERTY, 0);
        int displayedHeight = blockRequiredItemProperties.getIntProperty(EJRWTSingleRecordBlockDefinitionProperties.DISPLAYED_HEIGHT_PROPERTY, 0);

        if (displayedWidth > 0)
        {

            float avgCharWidth = control == null ? 1 : EJRWTImageRetriever.getGraphicsProvider().getAvgCharWidth(control.getFont());
            if (itemRenderer != null && itemRenderer.useFontDimensions() && avgCharWidth > 0)
            {
                // add padding
                gridData.widthHint = (int) ((displayedWidth + 1) * avgCharWidth);
            }
            else
            {
                gridData.widthHint = displayedWidth;
            }
        }
        if (displayedHeight > 0)
        {
            float avgCharHeight = control == null ? 1 : EJRWTImageRetriever.getGraphicsProvider().getCharHeight(control.getFont());
            if (itemRenderer != null && itemRenderer.useFontDimensions() && avgCharHeight > 0)
            {
                // add padding
                gridData.heightHint = (int) ((displayedHeight + 1) * avgCharHeight);
            }
            else
            {
                gridData.heightHint = displayedHeight;
            }
        }
        else if (control instanceof EJRWTAbstractLabel)
        {
            gridData.heightHint = 20;
        }

        return gridData;
    }

    private void labletextAliment(Label label, String labelOrientation)
    {
        if (label == null)
        {
            return;
        }
        if (EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_LEFT_PROPERTY.equals(labelOrientation))
        {
            label.setAlignment(SWT.LEFT);
        }
        else if (EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY.equals(labelOrientation))
        {
            label.setAlignment(SWT.RIGHT);
        }
        else if (EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_CENTER_PROPERTY.equals(labelOrientation))
        {
            label.setAlignment(SWT.CENTER);
        }
    }

    public void createScreenItem(Composite parent, EJCoreMainScreenItemProperties itemProps)
    {
        if (itemProps.isSpacerItem())
        {
            if (itemProps.isSeparator())
            {
                int style = SWT.SEPARATOR;

                if (itemProps.getSeparatorOrientation() == EJSeparatorOrientation.HORIZONTAL)
                {
                    style = style | SWT.HORIZONTAL;
                }
                else
                {
                    style = style | SWT.VERTICAL;
                }

                Label layoutBody = new Label(parent, style);
                layoutBody.setLayoutData(createBlockItemGridData(null, itemProps.getBlockRendererRequiredProperties(), layoutBody));

                switch (itemProps.getSeparatorLineStyle())
                {
                    case DASHED:
                        layoutBody.setData(EJ_RWT.CUSTOM_VARIANT, "separator_dashed");
                        break;
                    case DOTTED:
                        layoutBody.setData(EJ_RWT.CUSTOM_VARIANT, "separator_dotted");
                        break;
                    case DOUBLE:
                        layoutBody.setData(EJ_RWT.CUSTOM_VARIANT, "separator_double");
                        break;

                    default:
                        layoutBody.setData(EJ_RWT.CUSTOM_VARIANT, "separator");
                        break;
                }
                return;
            }

            Label label = new Label(parent, SWT.NONE);
            label.setLayoutData(createBlockItemGridData(null, itemProps.getBlockRendererRequiredProperties(), label));
            return;
        }
        EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemProps.getReferencedItemName());
        EJManagedItemRendererWrapper renderer = item.getManagedItemRenderer();
        if (renderer != null)
        {
            _mainItemRegister.registerRendererForItem(renderer.getUnmanagedRenderer(), item);
            EJFrameworkExtensionProperties blockRequiredItemProperties = itemProps.getBlockRendererRequiredProperties();

            String labelPosition = blockRequiredItemProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.LABEL_POSITION_PROPERTY);
            String labelOrientation = blockRequiredItemProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_PROPERTY);
            String visualAttribute = blockRequiredItemProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY);

            EJRWTAppItemRenderer itemRenderer = (EJRWTAppItemRenderer) renderer.getUnmanagedRenderer();
            boolean hasLabel = itemProps.getLabel() != null && itemProps.getLabel().trim().length() > 0;

            if (hasLabel && EJRWTSingleRecordBlockDefinitionProperties.LABEL_POSITION_LEFT_PROPERTY.equals(labelPosition))
            {
                itemRenderer.createLable(parent);
                itemRenderer.createComponent(parent);
                labletextAliment(itemRenderer.getGuiComponentLabel(), labelOrientation);
            }
            else if (hasLabel && EJRWTSingleRecordBlockDefinitionProperties.LABEL_POSITION_RIGHT_PROPERTY.equals(labelPosition))
            {
                itemRenderer.createComponent(parent);
                itemRenderer.createLable(parent);
                labletextAliment(itemRenderer.getGuiComponentLabel(), labelOrientation);
            }
            else
            {
                itemRenderer.createComponent(parent);
            }
            itemRenderer.getGuiComponent().setLayoutData(createBlockItemGridData(itemRenderer, blockRequiredItemProperties, itemRenderer.getGuiComponent()));
            if (itemRenderer.getGuiComponentLabel() != null)
            {
                itemRenderer.getGuiComponentLabel().setLayoutData(createBlockLableGridData(blockRequiredItemProperties));
            }

            EJ_RWT.setTestId(itemRenderer.getGuiComponent(), blockRequiredItemProperties.getBlockProperties().getName() + "." + itemRenderer.getRegisteredItemName());

            if (visualAttribute != null)
            {
                EJCoreVisualAttributeProperties va = EJCoreProperties.getInstance().getVisualAttributesContainer().getVisualAttributeProperties(visualAttribute);
                if (va != null)
                {
                    itemRenderer.setInitialVisualAttribute(va);
                }
            }

            hookFocusListener(itemRenderer.getGuiComponent());
            hookKeyListener(itemRenderer.getGuiComponent());

            EJScreenItemProperties itemProperties = item.getProperties();

            renderer.setVisible(itemProperties.isVisible());
            renderer.setEditAllowed((itemRenderer.isReadOnly() || _block.getBlock().getProperties().isControlBlock()) && itemProperties.isEditAllowed());

            // Add the item to the pane according to its display coordinates.
            renderer.setMandatory(itemProperties.isMandatory());
            renderer.enableLovActivation(itemProperties.isLovNotificationEnabled());

            if (item.getProperties().getVisualAttributeProperties() != null)
            {
                renderer.setVisualAttribute(item.getProperties().getVisualAttributeProperties());
            }

            if (_firstNavigationalItem == null)
            {
                if (itemProperties.isVisible() && itemProperties.isEditAllowed())
                {
                    _firstNavigationalItem = renderer;
                }
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
                _block.getBlock().refreshAfterChange(_mainItemRegister.getRegisteredRecord());
                gainFocus();
            }
        }

    }

    private void hookKeyListener(Control control)
    {
        List<String> subActions = new ArrayList<String>(_actionkeys);
        subActions.add("ARROW_UP");
        subActions.add("ARROW_DOWN");
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

    private void hookFocusListener(final Control control)
    {
        control.addFocusListener(new FocusListener()
        {
            @Override
            public void focusLost(FocusEvent arg0)
            {
                logger.trace("START focusLost");
                setHasFocus(false);
                logger.trace("END focusLost");
            }

            @Override
            public void focusGained(FocusEvent arg0)
            {
                logger.trace("START focusGained");
                setHasFocus(true);
                logger.trace("END focusGained");
            }
        });
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
