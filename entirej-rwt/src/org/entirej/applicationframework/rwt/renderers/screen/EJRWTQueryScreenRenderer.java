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
import org.entirej.applicationframework.rwt.renderers.item.EJRWTItemTextChangeNotifier;
import org.entirej.applicationframework.rwt.renderers.item.EJRWTItemTextChangeNotifier.ChangeListener;
import org.entirej.applicationframework.rwt.renderers.screen.definition.interfaces.EJRWTScreenRendererDefinitionProperties;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJBlock;
import org.entirej.framework.core.EJFrameworkManager;
import org.entirej.framework.core.EJLovBlock;
import org.entirej.framework.core.EJQueryBlock;
import org.entirej.framework.core.EJScreenItem;
import org.entirej.framework.core.data.EJDataItem;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.data.controllers.EJLovController;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJRecordType;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalBlock;
import org.entirej.framework.core.properties.EJCoreQueryScreenItemProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJBlockProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.eventhandlers.EJItemValueChangedListener;
import org.entirej.framework.core.renderers.interfaces.EJItemRenderer;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;
import org.entirej.framework.core.renderers.registry.EJBlockItemRendererRegister;
import org.entirej.framework.core.renderers.registry.EJQueryScreenItemRendererRegister;
import org.entirej.framework.core.service.EJQueryCriteria;
import org.entirej.framework.core.service.EJRestrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJRWTQueryScreenRenderer extends EJRWTAbstractScreenRenderer implements EJQueryScreenRenderer, EJItemValueChangedListener
{
    private final int                         QUERY_OK_ACTION_COMMAND     = 0;
    private final int                         QUERY_CANCEL_ACTION_COMMAND = 2;
    private final int                         QUERY_CLEAR_ACTION_COMMAND  = 4;
    
    private EJBlockController                 _block;
    private EJRWTAbstractDialog               _queryDialog;
    private EJQueryScreenItemRendererRegister _itemRegister;
    private EJFrameworkManager                _frameworkManager;
    private boolean                           _maximize;
    
    final Logger                              _logger                      = LoggerFactory.getLogger(EJRWTQueryScreenRenderer.class);

    @Override
    public void refreshQueryScreenRendererProperty(String propertyName)
    {
    }

    @Override
    public EJBlockItemRendererRegister getItemRegister()
    {
        return _itemRegister;
    }

    @Override
    public EJScreenItemController getItem(String itemName)
    {
        return _block.getScreenItem(EJScreenType.QUERY, itemName);
    }

    @Override
    public void refreshItemProperty(EJCoreQueryScreenItemProperties itemProperties, EJManagedScreenProperty managedItemProperty)
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
        }
    }

    @Override
    public Object getGuiComponent()
    {
        return _queryDialog;
    }

    @Override
    public void initialiseRenderer(EJBlockController block)
    {
        _block = block;

        _frameworkManager = block.getFrameworkManager();
        _itemRegister = new EJQueryScreenItemRendererRegister(block);
    }

    @Override
    public void initialiseRenderer(EJLovController controller)
    {
        _block = controller.getBlock().getBlockController();
        _itemRegister = new EJQueryScreenItemRendererRegister(controller);
        _frameworkManager = controller.getFrameworkManager();
        setupQueryScreen();
    }

    @Override
    public void open(EJDataRecord queryRecord)
    {
        _itemRegister.resetRegister();
        setupQueryScreen();
        _itemRegister.register(queryRecord);
        _itemRegister.initialiseRegisteredRenderers();
        _queryDialog.centreLocation();
        if (_maximize)
        {
            _queryDialog.getShell().setMaximized(_maximize);
        }
        _queryDialog.open();
    }

    @Override
    public void close()
    {
        _queryDialog.close();
        _queryDialog = null;
    }

    @Override
    public EJDataRecord getQueryRecord()
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

    EJRWTApplicationManager getRWTManager()
    {
        return (EJRWTApplicationManager) _frameworkManager.getApplicationManager();
    }

    private void setupQueryScreen()
    {
        // Setup pane for query window
        EJFrameworkExtensionProperties rendererProperties = _block.getProperties().getQueryScreenRendererProperties();

        String title = rendererProperties.getStringProperty(EJRWTScreenRendererDefinitionProperties.TITLE);
        final int width = rendererProperties.getIntProperty(EJRWTScreenRendererDefinitionProperties.WIDTH, 300);
        final int height = rendererProperties.getIntProperty(EJRWTScreenRendererDefinitionProperties.HEIGHT, 500);
        _maximize = rendererProperties.getBooleanProperty(EJRWTScreenRendererDefinitionProperties.MAXIMIZE, false);
        final int numCols = rendererProperties.getIntProperty(EJRWTScreenRendererDefinitionProperties.NUM_COLS, 1);
        final String queryButtonLabel = rendererProperties.getStringProperty(EJRWTScreenRendererDefinitionProperties.EXECUTE_BUTTON_TEXT);
        final String cancelButtonLabel = rendererProperties.getStringProperty(EJRWTScreenRendererDefinitionProperties.CANCEL_BUTTON_TEXT);
        final String clearButtonLabel = rendererProperties.getStringProperty(EJRWTScreenRendererDefinitionProperties.CLEAR_BUTTON_TEXT);

        _queryDialog = new EJRWTAbstractDialog(getRWTManager().getShell())
        {
            private static final long serialVersionUID = -4685316941898120169L;

            @Override
            public void createBody(Composite parent)
            {
                parent.setLayout(new FillLayout());
                final ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);

                EJRWTEntireJGridPane _mainPane = new EJRWTEntireJGridPane(scrollComposite, numCols);
                _mainPane.cleanLayout();
                EJBlockProperties blockProperties = _block.getProperties();
                addAllItemGroups(blockProperties.getScreenItemGroupContainer(EJScreenType.QUERY), _mainPane, EJScreenType.QUERY);

                scrollComposite.setContent(_mainPane);
                scrollComposite.setExpandHorizontal(true);
                scrollComposite.setExpandVertical(true);
                // remove the oddset
                scrollComposite.setMinSize(width, height - 10);

                _block.addItemValueChangedListener(EJRWTQueryScreenRenderer.this);
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
                Button button = getButton(QUERY_OK_ACTION_COMMAND);
                if (button == null)
                {
                    return;
                }
                Collection<EJScreenItemController> allScreenItems = _block.getAllScreenItems(EJScreenType.QUERY);
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
                createButton(parent, QUERY_OK_ACTION_COMMAND, queryButtonLabel == null ? "Query" : queryButtonLabel, true);
                createButton(parent, QUERY_CLEAR_ACTION_COMMAND, clearButtonLabel == null ? "Clear" : clearButtonLabel, false);
                createButton(parent, QUERY_CANCEL_ACTION_COMMAND, cancelButtonLabel == null ? "Cancel" : cancelButtonLabel, false);
            }

            @Override
            public boolean close()
            {
                _block.removeItemValueChangedListener(EJRWTQueryScreenRenderer.this);
                _block.setRendererFocus(true);
                return super.close();
            }

            @Override
            protected void buttonPressed(int buttonId)
            {
                try
                {
                    switch (buttonId)
                    {
                        case QUERY_OK_ACTION_COMMAND:
                        {
                            EJQueryBlock b = new EJLovBlock(_block.getBlock());
                            EJQueryCriteria queryCriteria = new EJQueryCriteria(b);

                            EJDataRecord record = getQueryRecord();
                            for (EJDataItem item : record.getAllItems())
                            {
                                if (!item.isBlockServiceItem())
                                {
                                    continue;
                                }
                                if (item.getValue() != null)
                                {
                                    if (item.getProperties().getDataTypeClass().isAssignableFrom(String.class))
                                    {
                                        String value = (String) item.getValue();
                                        if (value.contains("%"))
                                        {
                                            queryCriteria.add(EJRestrictions.like(item.getName(), item.getValue()));
                                        }
                                        else
                                        {
                                            queryCriteria.add(EJRestrictions.equals(item.getName(), item.getValue()));
                                        }
                                    }
                                    else
                                    {
                                        queryCriteria.add(EJRestrictions.equals(item.getName(), item.getValue()));
                                    }
                                }
                            }
                            try
                            {
                                _block.executeQuery(queryCriteria);
                            }
                            catch (EJApplicationException e)
                            {
                                setButtonEnable(buttonId, false);
                                throw e;
                            }
                            close();
                            break;
                        }
                        case QUERY_CLEAR_ACTION_COMMAND:
                        {
                            _itemRegister.clearRegisteredValues();
                            _itemRegister.register(_block.createRecord(EJRecordType.QUERY));
                            break;
                        }
                        case QUERY_CANCEL_ACTION_COMMAND:
                        {
                            close();
                            break;
                        }
                    }
                }
                catch (EJApplicationException e)
                {
                    _logger.trace(e.getMessage());
                    _frameworkManager.handleException(e);
                    return;
                }
                super.buttonPressed(buttonId);
            }
        };
        _queryDialog.create();
        _queryDialog.getShell().setData("QUERY - " + _block.getProperties().getName());
        _queryDialog.getShell().setText(title != null ? title : "");
        // add dialog border offsets
        _queryDialog.getShell().setSize(width + 80, height + 100);
    }

    @Override
    protected EJInternalBlock getBlock()
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
        return ((EJCoreQueryScreenItemProperties) item).getQueryScreenRendererProperties();
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
    public void valueChanged(EJScreenItemController arg0, EJItemRenderer arg1)
    {
        if (_queryDialog != null)
        {
            _queryDialog.validate();
        }
    }
}
