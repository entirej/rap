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
package org.entirej.applicationframework.rwt.application.form.containers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTDeleteAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTInsertAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTNextPageAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTNextRecordAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTPreviousPageAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTPreviousRecordAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTQueryAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTSaveAction;
import org.entirej.applicationframework.rwt.application.components.actions.EJRWTUpdateAction;
import org.entirej.applicationframework.rwt.application.components.interfaces.EJRWTFormContainerToolbar;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTAppComponentRenderer;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTApplicationComponent;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormChosenListener;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.data.controllers.EJFormController;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.renderers.eventhandlers.EJItemFocusedEvent;
import org.entirej.framework.core.renderers.interfaces.EJItemRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJRWTDefaultFormContainerToolbar implements EJRWTFormContainerToolbar, EJRWTAppComponentRenderer, EJRWTApplicationComponent
{
    final Logger                      logger = LoggerFactory.getLogger(EJRWTDefaultFormContainerToolbar.class);

    private EJItemFocusedEvent        _itemFocusedEvent;
    private ToolBar                   _toolBar;

    private EJRWTQueryAction          _toolbarQueryAction;
    private EJRWTPreviousRecordAction _toolbarPreviousRecordAction;
    private EJRWTNextRecordAction     _toolbarNextRecordAction;
    private EJRWTPreviousPageAction   _toolbarPreviousPageAction;
    private EJRWTNextPageAction       _toolbarNextPageAction;
    private EJRWTInsertAction         _toolbarInsertAction;
    private EJRWTUpdateAction         _toolbarUpdateAction;
    private EJRWTDeleteAction         _toolbarDeleteAction;
    private EJRWTSaveAction           _toolbarSaveAction;
    private EJRWTApplicationManager   manager;

    /**
     * Return the {@link CoolBar} component for this toolbar
     * 
     * @return The {@link CoolBar} component
     */
    @Override
    public ToolBar getComponent()
    {
        if (_toolBar == null)
        {
            throw new IllegalAccessError("Call createCoolbar(Composite parent) before access getCoolbarComponent()");
        }

        return _toolBar;
    }

    @Override
    public EJInternalForm getForm()
    {

        return manager != null && manager.getFormContainer() != null ? manager.getFormContainer().getActiveForm() : null;
    }

    @Override
    public void blockFocusLost(EJBlockController focused)
    {
        synchronize(focused);
    }

    @Override
    public void blockFocusGained(EJBlockController focused)
    {
        synchronize(focused);
    }

    @Override
    public EJScreenItemController getFocusedItem()
    {
        if (_itemFocusedEvent != null)
        {
            return _itemFocusedEvent.getItem();
        }
        else
        {
            return null;
        }

    }

    
    @Override
    public void screenItemValueChanged(EJScreenItemController item, EJItemRenderer changedRenderer, Object oldValue, Object newValue)
    {
        synchronize(item.getBlock().getBlockController());
        
    }

    @Override
    public void focusedGained(EJDataRecord focusedRecord)
    {
        if (focusedRecord != null)
        {
            synchronize(focusedRecord.getBlock().getBlockController());
        }
    }

    @Override
    public void synchronize(EJBlockController focusedBlock)
    {
        logger.trace("START synchronize");
        _toolbarQueryAction.synchronize(focusedBlock instanceof EJEditableBlockController ? (EJEditableBlockController) focusedBlock : null);
        _toolbarPreviousRecordAction.synchronize(focusedBlock);
        _toolbarNextRecordAction.synchronize(focusedBlock);
        _toolbarNextPageAction.synchronize(focusedBlock);
        _toolbarPreviousPageAction.synchronize(focusedBlock);
        _toolbarInsertAction.synchronize(focusedBlock instanceof EJEditableBlockController ? (EJEditableBlockController) focusedBlock : null);
        _toolbarUpdateAction.synchronize(focusedBlock instanceof EJEditableBlockController ? (EJEditableBlockController) focusedBlock : null);
        _toolbarDeleteAction.synchronize(focusedBlock instanceof EJEditableBlockController ? (EJEditableBlockController) focusedBlock : null);
        _toolbarSaveAction.synchronize(focusedBlock instanceof EJEditableBlockController ? (EJEditableBlockController) focusedBlock : null);
        logger.trace("END synchronize");
    }

    @Override
    public void focusGained(EJItemFocusedEvent itemFocusedEvent)
    {
        _itemFocusedEvent = itemFocusedEvent;

        if (itemFocusedEvent == null || itemFocusedEvent.getItem().getBlock() == null)
        {
            synchronize(null);
        }
        else
        {
            synchronize(itemFocusedEvent.getItem().getBlock().getBlockController());
        }
    }

    @Override
    public void focusLost(EJItemFocusedEvent itemFocusedEvent)
    {
        _itemFocusedEvent = null;
        if (itemFocusedEvent == null || itemFocusedEvent.getItem().getBlock() == null)
        {
            synchronize(null);
        }
        else
        {
            synchronize(itemFocusedEvent.getItem().getBlock().getBlockController());
        }
    }

    @Override
    public void disable()
    {
        _toolbarQueryAction.setEnabled(false);
        _toolbarPreviousRecordAction.setEnabled(false);
        _toolbarNextRecordAction.setEnabled(false);
        _toolbarNextPageAction.setEnabled(false);
        _toolbarPreviousPageAction.setEnabled(false);
        _toolbarInsertAction.setEnabled(false);
        _toolbarUpdateAction.setEnabled(false);
        _toolbarDeleteAction.setEnabled(false);
        _toolbarSaveAction.setEnabled(false);
    }

    private void initialise()
    {
        _toolbarQueryAction = new EJRWTQueryAction(this);
        _toolbarPreviousRecordAction = new EJRWTPreviousRecordAction(this);
        _toolbarNextRecordAction = new EJRWTNextRecordAction(this);
        _toolbarNextPageAction = new EJRWTNextPageAction(this);
        _toolbarPreviousPageAction = new EJRWTPreviousPageAction(this);
        _toolbarInsertAction = new EJRWTInsertAction(this);
        _toolbarUpdateAction = new EJRWTUpdateAction(this);
        _toolbarDeleteAction = new EJRWTDeleteAction(this);
        _toolbarSaveAction = new EJRWTSaveAction(this);

    }

    protected EJRWTAction[] getActions()
    {
        return new EJRWTAction[] { _toolbarQueryAction, null, _toolbarPreviousRecordAction, _toolbarNextRecordAction, null, _toolbarPreviousPageAction,
                _toolbarNextPageAction, null, _toolbarInsertAction, _toolbarUpdateAction, _toolbarSaveAction, null, _toolbarDeleteAction };

    }

    @Override
    public Control createComponent(Composite parent)
    {
        _toolBar = new ToolBar(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
        addItems();
        return _toolBar;

    }

    protected void addItems()
    {
        initialise();
        EJRWTAction[] actions = getActions();
        for (final EJRWTAction action : actions)
        {
            if (action == null)
            {
                new ToolItem(_toolBar, SWT.SEPARATOR);
                continue;
            }
            ToolItem item = new ToolItem(_toolBar, SWT.PUSH);
            item.setToolTipText(action.getToolTipText());
            item.setImage(action.getImage());
            item.addSelectionListener(new SelectionAdapter()
            {

                private static final long serialVersionUID = -4955607396916437725L;

                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    action.run();
                }
            });

            action.setToolItem(item);
        }

        disable();
    }

    @Override
    public Control getGuiComponent()
    {
        return getComponent();
    }

    @Override
    public void createContainer(EJRWTApplicationManager manager, Composite parent, EJFrameworkExtensionProperties rendererprop)
    {
        this.manager = manager;
        createComponent(parent);
    }

    @Override
    public void fireFormSelected(EJInternalForm selectedForm)
    {
        synchronize(selectedForm != null ? selectedForm.getFocusedBlock().getBlockController() : null);

    }

    @Override
    public void fireFormOpened(EJInternalForm openedForm)
    {
        openedForm.addFormEventListener(this);
        openedForm.addBlockFocusedListener(this);
        openedForm.addItemValueChangedListener(this);
        openedForm.addItemFocusListener(this);
        openedForm.addNewRecordFocusedListener(this);
        synchronize(openedForm != null ? openedForm.getFocusedBlock().getBlockController() : null);

    }

    @Override
    public void fireFormClosed(EJInternalForm closedForm)
    {
        closedForm.removeFormEventListener(this);
        closedForm.removeBlockFocusedListener(this);
        closedForm.removeItemFocusListener(this);
        closedForm.removeItemValueChangedListener(this);
        closedForm.removeNewRecordFocusedListener(this);
        EJInternalForm form = getForm();
        synchronize(form != null ? form.getFocusedBlock().getBlockController() : null);

    }

    @Override
    public void addFormChosenListener(EJRWTFormChosenListener formChosenListener)
    {
        // ignore

    }

    @Override
    public void removeFormChosenListener(EJRWTFormChosenListener formChosenListener)
    {
        // ignore

    }

    @Override
    public void formSaved(EJFormController savedForm)
    {
        synchronize(savedForm.getFocusedBlockController());

    }

    @Override
    public void formCleared(EJFormController clearForm)
    {

        synchronize(clearForm.getFocusedBlockController());

    }
}
