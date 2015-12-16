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
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.properties.EJCoreUpdateScreenItemProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJBlockProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.eventhandlers.EJScreenItemValueChangedListener;
import org.entirej.framework.core.renderers.interfaces.EJItemRenderer;
import org.entirej.framework.core.renderers.interfaces.EJUpdateScreenRenderer;
import org.entirej.framework.core.renderers.registry.EJBlockItemRendererRegister;
import org.entirej.framework.core.renderers.registry.EJUpdateScreenItemRendererRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJRWTUpdateScreenRenderer extends EJRWTAbstractScreenRenderer implements EJUpdateScreenRenderer, EJScreenItemValueChangedListener
{
    private final int                          UPDATE_OK_ACTION_COMMAND     = 0;
    private final int                          UPDATE_CANCEL_ACTION_COMMAND = -1;

    private EJEditableBlockController          _block;
    private EJRWTAbstractDialog                _updateDialog;
    private EJUpdateScreenItemRendererRegister _itemRegister;
    private EJFrameworkManager                 _frameworkManager;
    private boolean                            _maximize;

    final Logger                               _logger                      = LoggerFactory.getLogger(EJRWTUpdateScreenRenderer.class);

    @Override
    public void refreshUpdateScreenRendererProperty(String propertyName)
    {
    }

    @Override
    public EJUpdateScreenItemRendererRegister getItemRegister()
    {
        return _itemRegister;
    }

    EJRWTApplicationManager getRWTManager()
    {
        return (EJRWTApplicationManager) _frameworkManager.getApplicationManager();
    }

    @Override
    public EJScreenItemController getItem(String itemName)
    {
        return _block.getScreenItem(EJScreenType.UPDATE, itemName);
    }

    @Override
    public void refreshItemProperty(EJCoreUpdateScreenItemProperties itemProperties, EJManagedScreenProperty managedItemProperty)
    {
        EJManagedItemRendererWrapper rendererWrapper = _itemRegister.getManagedItemRendererForItem(itemProperties.getReferencedItemName());
        if (rendererWrapper == null)
        {
            return;
        }
        switch (managedItemProperty)
        {
            case VISIBLE:
                rendererWrapper.setVisible(itemProperties.isVisible());
                break;
            case EDIT_ALLOWED:
                rendererWrapper.setEditAllowed(itemProperties.isEditAllowed());
                break;
            case MANDATORY:
                rendererWrapper.setMandatory(itemProperties.isMandatory());
                break;
            case LABEL:
                rendererWrapper.setLabel(itemProperties.getLabel());
                break;
            case HINT:
                rendererWrapper.setHint(itemProperties.getHint());
                break;
        }
    }

    @Override
    public Object getGuiComponent()
    {
        return _updateDialog;
    }

    @Override
    public void initialiseRenderer(EJEditableBlockController block)
    {
        _block = block;
        _itemRegister = new EJUpdateScreenItemRendererRegister(block);
        _frameworkManager = block.getFrameworkManager();
    }

    @Override
    public void open(EJDataRecord recordToUpdate)
    {
        _itemRegister.resetRegister();
        setupUpdateScreen();
        _itemRegister.register(recordToUpdate);
        
        Collection<EJManagedItemRendererWrapper> registeredRenderers = _itemRegister.getRegisteredRenderers();
        for (EJManagedItemRendererWrapper wrapper : registeredRenderers)
        {
            if(wrapper.getUnmanagedRenderer() instanceof EJRWTComboItemRenderer)
            {
               if( ((EJRWTComboItemRenderer)wrapper.getUnmanagedRenderer()).isLovInitialiedOnValueSet())
                   wrapper.getUnmanagedRenderer().refreshItemRenderer();
            }
        }
        _updateDialog.centreLocation();
        if (_maximize)
        {
            _updateDialog.getShell().setMaximized(_maximize);
        }
        _updateDialog.open();
    }

    @Override
    public void close()
    {
        _updateDialog.close();
        _updateDialog = null;
    }

    @Override
    public EJDataRecord getUpdateRecord()
    {
        return _itemRegister.getRegisteredRecord();
    }

    @Override
    public void refreshAfterChange(EJDataRecord record)
    {
        _itemRegister.refreshAfterChange(record);
    }

    @Override
    public void synchronize()
    {
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
        return ((EJCoreUpdateScreenItemProperties) item).getUpdateScreenRendererProperties();
    }

    private void setupUpdateScreen()
    {
        // Setup pane for query window
        EJFrameworkExtensionProperties rendererProperties = _block.getProperties().getUpdateScreenRendererProperties();

        String title = rendererProperties.getStringProperty(EJRWTScreenRendererDefinitionProperties.TITLE);
        final int width = rendererProperties.getIntProperty(EJRWTScreenRendererDefinitionProperties.WIDTH, 300);
        final int height = rendererProperties.getIntProperty(EJRWTScreenRendererDefinitionProperties.HEIGHT, 500);
        _maximize = rendererProperties.getBooleanProperty(EJRWTScreenRendererDefinitionProperties.MAXIMIZE, false);
        final int numCols = rendererProperties.getIntProperty(EJRWTScreenRendererDefinitionProperties.NUM_COLS, 1);
        final String updateButtonLabel = rendererProperties.getStringProperty(EJRWTScreenRendererDefinitionProperties.EXECUTE_BUTTON_TEXT);
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

        _updateDialog = new EJRWTAbstractDialog(getRWTManager().getShell())
        {
            @Override
            public void createBody(Composite parent)
            {
                parent.setLayout(new FillLayout());
                final ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);

                EJRWTEntireJGridPane _mainPane = new EJRWTEntireJGridPane(scrollComposite, numCols);
                _mainPane.cleanLayout();
                EJBlockProperties blockProperties = _block.getProperties();
                addAllItemGroups(blockProperties.getScreenItemGroupContainer(EJScreenType.UPDATE), _mainPane, EJScreenType.UPDATE);

                scrollComposite.setContent(_mainPane);
                scrollComposite.setExpandHorizontal(true);
                scrollComposite.setExpandVertical(true);
                // remove the offset
                scrollComposite.setMinSize(width, height - 10);

                _block.addItemValueChangedListener(EJRWTUpdateScreenRenderer.this);
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
            public int open()
            {
                validate();
                setFoucsItemRenderer();
                return super.open();
            }

            @Override
            protected void createButtonsForButtonBar(Composite parent)
            {
                // Add the buttons in reverse order, as they will be added from
                // left to right
                addExtraButton(parent, button5Label, ID_BUTTON_5);
                addExtraButton(parent, button4Label, ID_BUTTON_4);
                addExtraButton(parent, button3Label, ID_BUTTON_3);
                addExtraButton(parent, button2Label, ID_BUTTON_2);
                addExtraButton(parent, button1Label, ID_BUTTON_1);
                createButton(parent, UPDATE_OK_ACTION_COMMAND, updateButtonLabel == null ? "Update" : updateButtonLabel, true);
                createButton(parent, UPDATE_CANCEL_ACTION_COMMAND, cancelButtonLabel == null ? "Cancel" : cancelButtonLabel, false);
            }

            @Override
            public void validate()
            {
                Button button = getButton(UPDATE_OK_ACTION_COMMAND);
                if (button == null)
                {
                    return;
                }
                Collection<EJScreenItemController> allScreenItems = _block.getAllScreenItems(EJScreenType.UPDATE);
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
                _block.removeItemValueChangedListener(EJRWTUpdateScreenRenderer.this);
                _block.setRendererFocus(true);
                return super.close();
            }

            @Override
            public boolean canceled()
            {
                _block.updateCancelled();
                return  true;
            }

            @Override
            protected void buttonPressed(int buttonId)
            {
                try
                {
                    switch (buttonId)
                    {
                        case UPDATE_OK_ACTION_COMMAND:
                        {
                            try
                            {
                                _block.getBlock().updateRecord(_itemRegister.getRegisteredRecord());
                                if (_block.getUpdateScreenDisplayProperties().getBooleanProperty(
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
                        case UPDATE_CANCEL_ACTION_COMMAND:
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
        _updateDialog.create();
        _updateDialog.getShell().setData("UPDATE - " + _block.getProperties().getName());
        _updateDialog.getShell().setText(title != null ? title : "");
        // add dialog border offsets
        _updateDialog.getShell().setSize(width + 80, height + 100);
    }

    @Override
    public void screenItemValueChanged(EJScreenItemController arg0, EJItemRenderer arg1)
    {
        if (_updateDialog != null)
        {
            _updateDialog.validate();
        }
    }
}
