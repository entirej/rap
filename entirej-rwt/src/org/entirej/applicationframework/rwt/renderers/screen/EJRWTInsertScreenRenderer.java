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
package org.entirej.applicationframework.rwt.renderers.screen;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTAbstractDialog;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;
import org.entirej.applicationframework.rwt.renderers.item.EJRWTComboItemRenderer;
import org.entirej.applicationframework.rwt.renderers.item.EJRWTItemTextChangeNotifier;
import org.entirej.applicationframework.rwt.renderers.item.EJRWTItemTextChangeNotifier.ChangeListener;
import org.entirej.applicationframework.rwt.renderers.screen.definition.interfaces.EJRWTScreenRendererDefinitionProperties;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJFrameworkManager;
import org.entirej.framework.core.EJScreenItem;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.properties.EJCoreInsertScreenItemProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJBlockProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.eventhandlers.EJScreenItemValueChangedListener;
import org.entirej.framework.core.renderers.interfaces.EJInsertScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJItemRenderer;
import org.entirej.framework.core.renderers.registry.EJBlockItemRendererRegister;
import org.entirej.framework.core.renderers.registry.EJInsertScreenItemRendererRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJRWTInsertScreenRenderer extends EJRWTAbstractScreenRenderer implements EJInsertScreenRenderer, EJScreenItemValueChangedListener
{
    private final int                          INSERT_OK_ACTION_COMMAND     = 0;
    private final int                          INSERT_CANCEL_ACTION_COMMAND = -1;

    private EJEditableBlockController          _block;
    private EJRWTAbstractDialog                _insertDialog;
    private EJInsertScreenItemRendererRegister _itemRegister;
    private EJFrameworkManager                 _frameworkManager;
    private boolean                            _maximize;
    final Logger                               _logger                       = LoggerFactory.getLogger(EJRWTInsertScreenRenderer.class);

    @Override
    public void refreshInsertScreenRendererProperty(String propertyName)
    {
    }

  
    public EJInsertScreenItemRendererRegister getItemRegister()
    {
        return _itemRegister;
    }

    @Override
    public EJScreenItemController getItem(String itemName)
    {
        return _block.getScreenItem(EJScreenType.INSERT, itemName);
    }

    @Override
    public void refreshItemProperty(EJCoreInsertScreenItemProperties itemProperties, EJManagedScreenProperty managedItemProperty)
    {
        EJManagedItemRendererWrapper rendererForItem = _itemRegister.getManagedItemRendererForItem(itemProperties.getReferencedItemName());
        if (rendererForItem == null)
        {
            return;
        }
        switch (managedItemProperty)
        {
            case VISIBLE:
                rendererForItem.setVisible(itemProperties.isVisible());
                break;
            case EDIT_ALLOWED:
                rendererForItem.setEditAllowed(itemProperties.isEditAllowed());
                break;
            case MANDATORY:
                rendererForItem.setMandatory(itemProperties.isMandatory());
                break;
            case LABEL:
                rendererForItem.setLabel(itemProperties.getLabel());
                break;
            case HINT:
                rendererForItem.setHint(itemProperties.getHint());
                break;
            default:
                // do nothing
        }
    }

    @Override
    public Object getGuiComponent()
    {
        return _insertDialog;
    }

    @Override
    public void initialiseRenderer(EJEditableBlockController block)
    {
        _block = block;
        _itemRegister = new EJInsertScreenItemRendererRegister(block);
        _frameworkManager = block.getFrameworkManager();
    }

    @Override
    public void open(EJDataRecord record)
    {
        _itemRegister.resetRegister();
        setupInsertScreen();

        _itemRegister.register(record);
        Collection<EJManagedItemRendererWrapper> registeredRenderers = _itemRegister.getRegisteredRenderers();
        for (EJManagedItemRendererWrapper wrapper : registeredRenderers)
        {
            if(wrapper.getUnmanagedRenderer() instanceof EJRWTComboItemRenderer)
            {
               if( ((EJRWTComboItemRenderer)wrapper.getUnmanagedRenderer()).isLovInitialiedOnValueSet())
                   wrapper.getUnmanagedRenderer().refreshItemRenderer();
            }
        }
        _insertDialog.centreLocation();
        if (_maximize)
        {
            _insertDialog.getShell().setMaximized(_maximize);
        }
        _insertDialog.open();
        _insertDialog.getShell().forceFocus();
    }

    @Override
    public void close()
    {
        _insertDialog.close();
        _insertDialog = null;
    }

    @Override
    public EJDataRecord getInsertRecord()
    {
        return _itemRegister.getRegisteredRecord();
    }

    @Override
    public void synchronize()
    {
    }

    @Override
    public void refreshAfterChange(EJDataRecord record)
    {
        _itemRegister.refreshAfterChange(record);
    }

    EJRWTApplicationManager getRWTManager()
    {
        return (EJRWTApplicationManager) _frameworkManager.getApplicationManager();
    }

    private void setupInsertScreen()
    {
        // Setup pane for Insert window
        EJFrameworkExtensionProperties rendererProperties = _block.getProperties().getInsertScreenRendererProperties();

        String title = rendererProperties.getStringProperty(EJRWTScreenRendererDefinitionProperties.TITLE);
        final int width = rendererProperties.getIntProperty(EJRWTScreenRendererDefinitionProperties.WIDTH, 300);
        _maximize = rendererProperties.getBooleanProperty(EJRWTScreenRendererDefinitionProperties.MAXIMIZE, false);
        final int height = rendererProperties.getIntProperty(EJRWTScreenRendererDefinitionProperties.HEIGHT, 500);
        final int numCols = rendererProperties.getIntProperty(EJRWTScreenRendererDefinitionProperties.NUM_COLS, 1);
        final String insertButtonLabel = rendererProperties.getStringProperty(EJRWTScreenRendererDefinitionProperties.EXECUTE_BUTTON_TEXT);
        final String cancelButtonLabel = rendererProperties.getStringProperty(EJRWTScreenRendererDefinitionProperties.CANCEL_BUTTON_TEXT);

        EJFrameworkExtensionProperties extraButtonsGroup = rendererProperties.getPropertyGroup(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTONS_GROUP);

        final String button1Label = extraButtonsGroup.getStringProperty(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_ONE_LABEL);
        final String button1Command = extraButtonsGroup.getStringProperty(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_ONE_COMMAND);
        final String button2Label = extraButtonsGroup.getStringProperty(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_TWO_LABEL);
        final String button2Command = extraButtonsGroup.getStringProperty(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_TWO_COMMAND);
        final String button3Label = extraButtonsGroup.getStringProperty(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_THREE_LABEL);
        final String button3Command = extraButtonsGroup.getStringProperty(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_THREE_COMMAND);
        final String button4Label = extraButtonsGroup.getStringProperty(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_FOUR_LABEL);
        final String button4Command = extraButtonsGroup.getStringProperty(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_FOUR_COMMAND);
        final String button5Label = extraButtonsGroup.getStringProperty(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_FIVE_LABEL);
        final String button5Command = extraButtonsGroup.getStringProperty(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_FIVE_COMMAND);

        final int ID_BUTTON_1 = 1;
        final int ID_BUTTON_2 = 2;
        final int ID_BUTTON_3 = 3;
        final int ID_BUTTON_4 = 4;
        final int ID_BUTTON_5 = 5;

        _insertDialog = new EJRWTAbstractDialog(getRWTManager().getShell())
        {
            @Override
            public void createBody(Composite parent)
            {
                parent.setLayout(new FillLayout());
                final ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);

                EJRWTEntireJGridPane _mainPane = new EJRWTEntireJGridPane(scrollComposite, numCols);
                _mainPane.cleanLayout();
                EJBlockProperties blockProperties = _block.getProperties();
                addAllItemGroups(blockProperties.getScreenItemGroupContainer(EJScreenType.INSERT), _mainPane, EJScreenType.INSERT);

                scrollComposite.setContent(_mainPane);
                scrollComposite.setExpandHorizontal(true);
                scrollComposite.setExpandVertical(true);
                // remove the offset
                scrollComposite.setMinSize(width, height - 10);
                _block.addItemValueChangedListener(EJRWTInsertScreenRenderer.this);

                EJRWTItemTextChangeNotifier.ChangeListener changeListener = new ChangeListener()
                {
                    @Override
                    public void changed()
                    {
                        validate();
                    }
                };
                Collection<EJManagedItemRendererWrapper> registeredRenderers = _itemRegister.getRegisteredRenderers();
                for (EJManagedItemRendererWrapper ejManagedItemRendererWrapper : registeredRenderers)
                {
                    if (ejManagedItemRendererWrapper.getUnmanagedRenderer() instanceof EJRWTItemTextChangeNotifier)
                    {
                        ((EJRWTItemTextChangeNotifier) ejManagedItemRendererWrapper.getUnmanagedRenderer()).addListener(changeListener);
                    }
                }
            }

            @Override
            public void validate()
            {
                Button button = getButton(INSERT_OK_ACTION_COMMAND);
                if (button == null)
                {
                    return;
                }
                Collection<EJScreenItemController> allScreenItems = _block.getAllScreenItems(EJScreenType.INSERT);
                for (EJScreenItemController ejScreenItemController : allScreenItems)
                {
                    if (!ejScreenItemController.getManagedItemRenderer().isValid())
                    {
                        button.setEnabled(false);
                        return;
                    }
                }
                button.setEnabled(true);
            }

            @Override
            public int open()
            {
                validate();
                setFoucsItemRenderer();
                return super.open();
            }

            @Override
            protected void createButtonsForButtonBar(Composite parent)
            {
                // Add the buttons in reverse order, as they will be added from left to right
                addExtraButton(parent, button5Label, ID_BUTTON_5);
                addExtraButton(parent, button4Label, ID_BUTTON_4);
                addExtraButton(parent, button3Label, ID_BUTTON_3);
                addExtraButton(parent, button2Label, ID_BUTTON_2);
                addExtraButton(parent, button1Label, ID_BUTTON_1);
                createButton(parent, INSERT_OK_ACTION_COMMAND, insertButtonLabel == null ? "Insert" : insertButtonLabel, true);
                createButton(parent, INSERT_CANCEL_ACTION_COMMAND, cancelButtonLabel == null ? "Cancel" : cancelButtonLabel, false);
            }

            private void addExtraButton(Composite parent, String label, int id)
            {
                if (label == null)
                {
                    return;
                }
                createButton(parent, id, label, false);
            }

            @Override
            public boolean close()
            {
                _block.removeItemValueChangedListener(EJRWTInsertScreenRenderer.this);
                _block.setRendererFocus(true);
                return super.close();
            }

            @Override
            public boolean canceled()
            {
                _block.insertCancelled();
                
                return  true;
            }

            @Override
            protected void buttonPressed(int buttonId)
            {
                try
                {
                    switch (buttonId)
                    {
                        case INSERT_OK_ACTION_COMMAND:
                        {
                            EJDataRecord newRecord = getInsertRecord();
                            try
                            {
                                _block.getBlock().insertRecord(newRecord);
                                if (_block.getInsertScreenDisplayProperties().getBooleanProperty(
                                        EJRWTScreenRendererDefinitionProperties.SAVE_FORM_AFTER_EXECUTE, false))
                                {
                                    _block.getBlock().getForm().saveChanges();
                                }

                            }
                            catch (EJApplicationException e)
                            {

                                setButtonEnable(buttonId, false);
                                throw e;
                            }
                            close();
                            break;
                        }
                        case INSERT_CANCEL_ACTION_COMMAND:
                        {
                            _block.updateCancelled();
                            close();
                            break;
                        }
                        case ID_BUTTON_1:
                        {
                            _block.executeActionCommand(button1Command, EJScreenType.UPDATE);
                            break;
                        }
                        case ID_BUTTON_2:
                        {
                            _block.executeActionCommand(button2Command, EJScreenType.UPDATE);
                            break;
                        }
                        case ID_BUTTON_3:
                        {
                            _block.executeActionCommand(button3Command, EJScreenType.UPDATE);
                            break;
                        }
                        case ID_BUTTON_4:
                        {
                            _block.executeActionCommand(button4Command, EJScreenType.UPDATE);
                            break;
                        }
                        case ID_BUTTON_5:
                        {
                            _block.executeActionCommand(button5Command, EJScreenType.UPDATE);
                            break;
                        }

                        default:
                            _block.updateCancelled();
                            break;
                    }
                }
                catch (EJApplicationException e)
                {
                    _logger.trace(e.getMessage());
                    _frameworkManager.handleException(e);
                    return;
                }
            }
        };
        _insertDialog.create();
        _insertDialog.getShell().setData("INSERT - " + _block.getProperties().getName());
        _insertDialog.getShell().setText(title != null ? title : "");
        // add dialog border offsets
        _insertDialog.getShell().setSize(width + 80, height + 100);
    }

    @Override
    protected EJInternalEditableBlock getBlock()
    {
        return _block.getBlock();
    }

    @Override
    protected void registerRendererForItem(EJItemRenderer renderer, EJScreenItemController item)
    {
        _itemRegister.registerRendererForItem(renderer, item);
    }

    @Override
    protected EJFrameworkExtensionProperties getItemRendererPropertiesForItem(EJScreenItemProperties item)
    {
        return ((EJCoreInsertScreenItemProperties) item).getInsertScreenRendererProperties();
    }

    public void setFocusToItem(EJScreenItem item)
    {
        EJManagedItemRendererWrapper renderer = _itemRegister.getManagedItemRendererForItem(item.getName());
        if (renderer != null)
        {
            renderer.gainFocus();
        }
    }

   
    @Override
    public void screenItemValueChanged(EJScreenItemController item, EJItemRenderer changedRenderer, Object oldValue, Object newValue)
    {
        if (_insertDialog != null)
        {
            _insertDialog.validate();
        }
        
    }
    
}
