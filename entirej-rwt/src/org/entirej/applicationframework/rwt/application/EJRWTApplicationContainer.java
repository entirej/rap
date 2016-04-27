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
package org.entirej.applicationframework.rwt.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTAbstractDialog;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTFormPopUp;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTSingleFormContainer;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTAppComponentRenderer;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTApplicationComponent;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTApplicationStatusbar;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormChosenEvent;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormChosenListener;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormClosedListener;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormContainer;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormOpenedListener;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormSelectedListener;
import org.entirej.applicationframework.rwt.renderers.form.EJRWTFormRenderer;
import org.entirej.framework.core.data.controllers.EJPopupFormController;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.EJCoreFormProperties;
import org.entirej.framework.core.properties.EJCoreLayoutContainer;
import org.entirej.framework.core.properties.EJCoreLayoutItem;
import org.entirej.framework.core.properties.EJCoreLayoutItem.LayoutComponent;
import org.entirej.framework.core.properties.EJCoreLayoutItem.LayoutGroup;
import org.entirej.framework.core.properties.EJCoreLayoutItem.LayoutSpace;
import org.entirej.framework.core.properties.EJCoreLayoutItem.SplitGroup;
import org.entirej.framework.core.properties.EJCoreLayoutItem.SplitGroup.ORIENTATION;
import org.entirej.framework.core.properties.EJCoreLayoutItem.TabGroup;
import org.entirej.framework.core.renderers.interfaces.EJApplicationComponentRenderer;
import org.entirej.framework.core.renderers.registry.EJRendererFactory;

public class EJRWTApplicationContainer implements Serializable, EJRWTFormOpenedListener, EJRWTFormClosedListener, EJRWTFormSelectedListener,
        EJRWTFormChosenListener
{

    private static final long                 serialVersionUID      = 1L;

    protected List<EJRWTApplicationComponent> _addedComponents;

    protected Composite                       _mainPane;
    protected EJRWTFormContainer              _formContainer;
    protected EJRWTApplicationStatusbar       _statusbar;
    protected List<EJRWTSingleFormContainer>  _singleFormContainers = new ArrayList<EJRWTSingleFormContainer>();
    protected EJRWTApplicationManager         _applicationManager;
    protected final EJCoreLayoutContainer     _layoutContainer;

    public EJRWTApplicationContainer(EJCoreLayoutContainer layoutContainer)
    {
        _layoutContainer = layoutContainer;
        _addedComponents = new ArrayList<EJRWTApplicationComponent>();
    }

    /**
     * Returns the {@link EJSwingFormContainer} used within this application
     * 
     * @return This applications {@link EJSwingFormContainer}
     */
    public EJRWTFormContainer getFormContainer()
    {
        return _formContainer;
    }

    /**
     * Returns the {@link EJRWTApplicationStatusbar} used within this
     * application
     * 
     * @return This applications {@link EJRWTApplicationStatusbar}
     */
    public EJRWTApplicationStatusbar getStatusbar()
    {
        return _statusbar;
    }

    /**
     * Returns the main application window of this application
     * <p>
     * The window is passed from the Application when the application is build.
     * via this layout manager. There will only be a root window if the
     * application is started as a stand alone application or an Applet. If the
     * application is started as a portlet then there will be no root window
     * 
     * @return The root window of this application or null if the application
     *         was started as a portlet application
     */
    public Composite getMainPane()
    {
        return _mainPane;
    }

    void buildApplication(EJRWTApplicationManager applicationManager, Composite mainWindow)
    {
        _applicationManager = applicationManager;

        _mainPane = new Composite(mainWindow, SWT.NO_FOCUS);
        _mainPane.setData(EJ_RWT.CUSTOM_VARIANT, "applayout");

        mainWindow.setLayout(new FillLayout());

        buildApplicationContainer();

        if (_formContainer == null)
        {
            _formContainer = new EJRWTFormContainer()
            {
                EJRWTAbstractDialog _popupDialog;
                EJRWTFormPopUp      _formPopup;

                @Override
                public EJInternalForm switchToForm(String key)
                {
                    // ignore
                    return null;
                }
                
                
                @Override
                public EJInternalForm switchToForm(EJInternalForm form)
                {
                    // TODO Auto-generated method stub
                    return null;
                }
                
                
                

                @Override
                public void removeFormSelectedListener(EJRWTFormSelectedListener selectionListener)
                {
                    // ignore
                }

                @Override
                public void popupFormClosed()
                {
                    if (_formPopup != null)
                    {
                        _formPopup.close();
                        _formPopup = null;
                    }
                }

                @Override
                public void openPopupForm(EJPopupFormController popupController)
                {
                    _formPopup = new EJRWTFormPopUp(_applicationManager.getShell(), popupController);
                    _formPopup.showForm();

                }

                @Override
                public Collection<EJInternalForm> getAllForms()
                {
                    // ignore
                    return Collections.emptyList();
                }

                @Override
                public EJInternalForm getActiveForm()
                {
                    // ignore
                    return null;
                }

                @Override
                public boolean containsForm(String formName)
                {
                    // ignore
                    return false;
                }

                @Override
                public void closeForm(EJInternalForm form)
                {
                    if(_formPopup!=null && _formPopup.getPopupController().getPopupForm().equals(form))
                    {
                        
                        _formPopup.close();
                        _formPopup = null;
                        return;
                    }
                    
                    if (_popupDialog != null)
                    {
                        _popupDialog.close();
                        _popupDialog = null;
                    }

                }

                @Override
                public void addFormSelectedListener(EJRWTFormSelectedListener selectionListener)
                {
                    // ignore

                }

                @Override
                public EJInternalForm addForm(final EJInternalForm form)
                {
                    final int height = form.getProperties().getFormHeight();
                    final int width = form.getProperties().getFormWidth();

                    final EJRWTFormRenderer formRenderer = (EJRWTFormRenderer) form.getRenderer();
                    _popupDialog = new EJRWTAbstractDialog(_applicationManager.getShell())
                    {
                        private static final long serialVersionUID = -4685316941898120169L;

                        @Override
                        public void createBody(Composite parent)
                        {
                            parent.setLayout(new FillLayout());
                            final ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
                            formRenderer.createControl(scrollComposite);
                            scrollComposite.setContent(formRenderer.getGuiComponent());
                            scrollComposite.setExpandHorizontal(true);
                            scrollComposite.setExpandVertical(true);
                            scrollComposite.setMinSize(form.getProperties().getFormWidth(), form.getProperties().getFormHeight());

                            formRenderer.gainInitialFocus();
                        }

                        @Override
                        public int open()
                        {
                            return super.open();
                        }

                        private void addExtraButton(Composite parent, String label, int id)
                        {
                            if (label == null || label.length() == 0)
                            {
                                return;
                            }
                            createButton(parent, id, label, false);

                        }

                        @Override
                        public boolean close()
                        {
                            return super.close();
                        }

                    };
                    _popupDialog.create();
                    final EJCoreFormProperties coreFormProperties = form.getProperties();
                    _popupDialog.getShell().setData("POPUP - " + coreFormProperties.getName());
                    _popupDialog.getShell().setText(coreFormProperties.getTitle() == null ? coreFormProperties.getName() : coreFormProperties.getTitle());
                    // add dialog border offsets
                    _popupDialog.getShell().setSize(width + 80, height + 100);
                    _popupDialog.centreLocation();
                    _popupDialog.open();
                    _popupDialog.getShell().forceFocus();
                    return form;
                }



                @Override
                public void updateFormTitle(EJInternalForm form)
                {
                    // TODO Auto-generated method stub
                    
                }
            };
        }

    }

    /**
     * Returns the currently active form
     * 
     * @return The currently active form or <code>null</code> if there is
     *         currently no active form
     */
    public EJInternalForm getActiveForm()
    {
        return _formContainer != null ? _formContainer.getActiveForm() : null;
    }

    /**
     * Returns the amount of forms currently opened and stored within the form
     * container
     * 
     * @return The amount of forms currently opened
     */
    public int getOpenFormCount()
    {
        return _formContainer != null ? _formContainer.getAllForms().size() : 0;
    }

    /**
     * Instructs the form container to close the given form
     * 
     * @param form
     *            The form to close
     */
    public void remove(EJInternalForm form)
    {
        if (_formContainer != null)
        {
            _formContainer.closeForm(form);
        }

        // Inform the listeners that the form has been closed
        fireFormClosed(form);
    }

    /**
     * Opens a new form and adds it to the FormContainer
     * <p>
     * If the form passed is <code>null</code> or not
     * {@link EJSwingFormContainer} has been implemented then this method will
     * do nothing
     * 
     * @param form
     *            The form to be opened and added to the
     *            {@link EJSwingFormContainer}
     */
    public void add(EJInternalForm form)
    {
        if (form == null)
        {
            return;
        }

        if (_formContainer != null)
        {
            EJInternalForm addForm = _formContainer.addForm(form);
            // Inform the listeners that the form was opened
            fireFormOpened(addForm);
        }
    }

    public boolean isFormOpened(String formName)
    {

        return getForm(formName) != null;
    }
    public boolean isFormOpened(EJInternalForm form)
    {
        
        return getForm(form) != null;
    }

    protected void buildApplicationContainer()
    {
        GridLayout gridLayout = new GridLayout(_layoutContainer.getColumns(), true);
        _mainPane.setLayout(gridLayout);

        List<EJCoreLayoutItem> items = _layoutContainer.getItems();
        for (EJCoreLayoutItem item : items)
        {
            switch (item.getType())
            {
                case GROUP:
                    createGroupLayout(_mainPane, (LayoutGroup) item);
                    break;
                case SPACE:
                    createSpace(_mainPane, (LayoutSpace) item);
                    break;
                case COMPONENT:
                    createComponent(_mainPane, (LayoutComponent) item);
                    break;
                case SPLIT:
                    createSplitLayout(_mainPane, (SplitGroup) item);
                    break;
                case TAB:
                    createTabLayout(_mainPane, (TabGroup) item);
                    break;
            }
        }
        _mainPane.layout();
        if (_formContainer != null)
        {
            for (EJRWTApplicationComponent applicationComponent : _addedComponents)
            {
                if (applicationComponent instanceof EJRWTFormSelectedListener)
                {
                    _formContainer.addFormSelectedListener(applicationComponent);
                }
            }
        }
        for (EJRWTSingleFormContainer singleFormContainer : _singleFormContainers)
        {
            if (singleFormContainer.getForm() != null)
            {
                fireFormOpened(singleFormContainer.getForm());
            }
        }
    }

    private GridData createGridData(EJCoreLayoutItem layoutItem)
    {
        GridData gd = new GridData();
        gd.minimumHeight = layoutItem.getMinHeight();
        gd.minimumWidth = layoutItem.getMinWidth();
        gd.heightHint = layoutItem.getHintHeight();
        gd.widthHint = layoutItem.getHintWidth();
        gd.verticalSpan = layoutItem.getVerticalSpan();
        gd.horizontalSpan = layoutItem.getHorizontalSpan();

        switch (layoutItem.getGrab())
        {
            case BOTH:
                gd.grabExcessHorizontalSpace = true;
                gd.grabExcessVerticalSpace = true;
                break;
            case HORIZONTAL:
                gd.grabExcessHorizontalSpace = true;
                break;
            case VERTICAL:
                gd.grabExcessVerticalSpace = true;
                break;
            case NONE:
                break;
        }

        switch (layoutItem.getFill())
        {
            case BOTH:
                gd.verticalAlignment = SWT.FILL;
                gd.horizontalAlignment = SWT.FILL;
                break;
            case VERTICAL:
                gd.verticalAlignment = SWT.FILL;
                break;
            case HORIZONTAL:
                gd.horizontalAlignment = SWT.FILL;
                break;
            case NONE:
                break;
        }

        if(gd.grabExcessHorizontalSpace && gd.widthHint==0)
        {
            gd.horizontalAlignment = SWT.FILL;
        }
        
        if(gd.grabExcessVerticalSpace && gd.heightHint==0)
        {
            gd.verticalAlignment = SWT.FILL;
        }
        return gd;
    }

    private void createSpace(Composite parent, EJCoreLayoutItem.LayoutSpace space)
    {
        Label spaceLabel = new Label(parent, SWT.NONE);
        spaceLabel.setLayoutData(createGridData(space));
    }

    private void createComponent(Composite parent, EJCoreLayoutItem.LayoutComponent component)
    {
        try
        {
            EJApplicationComponentRenderer applicationComponentRenderer = EJRendererFactory.getInstance().getApplicationComponentRenderer(
                    component.getRenderer());
            if (applicationComponentRenderer instanceof EJRWTFormContainer)
            {
                if (_formContainer != null)
                {
                    throw new IllegalStateException("Multiple EJRWTFormContainer setup in layout");
                }
                _formContainer = (EJRWTFormContainer) applicationComponentRenderer;
            }
            if (applicationComponentRenderer instanceof EJRWTApplicationStatusbar)
            {
                if (_statusbar != null)
                {
                    throw new IllegalStateException("Multiple EJRWTApplicationStatusbar setup in layout");
                }
                _statusbar = (EJRWTApplicationStatusbar) applicationComponentRenderer;
            }
            if (applicationComponentRenderer instanceof EJRWTSingleFormContainer)
            {

                _singleFormContainers.add((EJRWTSingleFormContainer) applicationComponentRenderer);
            }
            if (applicationComponentRenderer instanceof EJRWTApplicationComponent)
            {
                _addedComponents.add((EJRWTApplicationComponent) applicationComponentRenderer);
            }

            EJRWTAppComponentRenderer renderer = (EJRWTAppComponentRenderer) applicationComponentRenderer;
            renderer.createContainer(_applicationManager, parent, component.getRendereProperties());
            renderer.getGuiComponent().setLayoutData(createGridData(component));
            return;
        }
        catch (Exception e)
        {
            _applicationManager.getApplicationMessenger().handleException(e, true);
        }

        // fail over
        Composite layoutBody = new Composite(parent, SWT.NO_FOCUS | SWT.BORDER);
        layoutBody.setLayoutData(createGridData(component));
        layoutBody.setLayout(new GridLayout());
        Label spaceLabel = new Label(layoutBody, SWT.NONE);
        spaceLabel.setText(String.format("<%s>",
                component.getRenderer() == null || component.getRenderer().length() == 0 ? "<component>" : component.getRenderer()));
        spaceLabel.setLayoutData(createGridData(component));
        spaceLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    private void createGroupLayout(Composite parent, EJCoreLayoutItem.LayoutGroup group)
    {
        Composite layoutBody = new Composite(parent, SWT.NO_FOCUS | (group.isBorder() ? SWT.BORDER : SWT.NONE));
        layoutBody.setLayoutData(createGridData(group));
        layoutBody.setData(EJ_RWT.CUSTOM_VARIANT, "applayout");
        List<EJCoreLayoutItem> items = group.getItems();
        if (items.size() > 0)
        {
            GridLayout gridLayout = new GridLayout(group.getColumns(), false);
            if (group.isHideMargin())
            {
                gridLayout.marginHeight = 0;
                gridLayout.marginWidth = 0;
            }

            layoutBody.setLayout(gridLayout);
            for (EJCoreLayoutItem item : items)
            {
                switch (item.getType())
                {
                    case GROUP:
                        createGroupLayout(layoutBody, (LayoutGroup) item);
                        break;
                    case SPACE:
                        createSpace(layoutBody, (LayoutSpace) item);
                        break;
                    case COMPONENT:
                        createComponent(layoutBody, (LayoutComponent) item);
                        break;
                    case SPLIT:
                        createSplitLayout(layoutBody, (SplitGroup) item);
                        break;
                    case TAB:
                        createTabLayout(layoutBody, (TabGroup) item);
                        break;
                }
            }
        }
        else
        {
            layoutBody.setLayout(new GridLayout());
            Label compLabel = new Label(layoutBody, SWT.NONE);
            compLabel.setLayoutData(new GridData(GridData.FILL_BOTH));

        }
    }

    private void createSplitLayout(Composite parent, EJCoreLayoutItem.SplitGroup group)
    {
        SashForm layoutBody = new SashForm(parent, group.getOrientation() == ORIENTATION.HORIZONTAL ? SWT.HORIZONTAL : SWT.VERTICAL);
        layoutBody.setLayoutData(createGridData(group));
        List<EJCoreLayoutItem> items = group.getItems();
        if (items.size() > 0)
        {
            int[] weights = new int[items.size()];

            for (EJCoreLayoutItem item : items)
            {
                weights[items.indexOf(item)] = item.getHintWidth() + 1;
                switch (item.getType())
                {
                    case GROUP:
                        createGroupLayout(layoutBody, (LayoutGroup) item);
                        break;
                    case SPACE:
                        createSpace(layoutBody, (LayoutSpace) item);
                        break;
                    case COMPONENT:
                        createComponent(layoutBody, (LayoutComponent) item);
                        break;
                    case SPLIT:
                        createSplitLayout(layoutBody, (SplitGroup) item);
                        break;
                    case TAB:
                        createTabLayout(layoutBody, (TabGroup) item);
                        break;

                }
            }

            layoutBody.setWeights(weights);
        }
        else
        {
            layoutBody.setLayout(new GridLayout());
            Label compLabel = new Label(layoutBody, SWT.NONE);
            compLabel.setLayoutData(new GridData(GridData.FILL_BOTH));

        }

        layoutBody.setBackground(parent.getBackground());
    }

    private void createTabLayout(Composite parent, EJCoreLayoutItem.TabGroup group)
    {
        CTabFolder layoutBody = new CTabFolder(parent, SWT.BORDER | (group.getOrientation() == TabGroup.ORIENTATION.TOP ? SWT.TOP : SWT.BOTTOM));

        layoutBody.setLayoutData(createGridData(group));
        List<EJCoreLayoutItem> items = group.getItems();

        for (EJCoreLayoutItem item : items)
        {
            CTabItem tabItem = new CTabItem(layoutBody, SWT.NONE);
            Composite composite = new Composite(layoutBody, SWT.NO_FOCUS);
            composite.setData(EJ_RWT.CUSTOM_VARIANT, "applayout");
            composite.setData("TAB_ITEM", tabItem);
            composite.setLayout(new GridLayout());
            tabItem.setControl(composite);
            tabItem.setText(item.getName() != null ? item.getName() : "");
            switch (item.getType())
            {
                case GROUP:
                    createGroupLayout(composite, (LayoutGroup) item);
                    break;
                case SPACE:
                    createSpace(composite, (LayoutSpace) item);
                    break;
                case COMPONENT:
                    createComponent(composite, (LayoutComponent) item);
                    break;
                case SPLIT:
                    createSplitLayout(composite, (SplitGroup) item);
                    break;
                case TAB:
                    createTabLayout(composite, (TabGroup) item);
                    break;

            }
        }
        if (items.size() > 0)
        {
            layoutBody.setSelection(0);
        }

    }

    @Override
    public void formChosen(EJRWTFormChosenEvent event)
    {
        EJInternalForm form = _applicationManager.getFrameworkManager().createInternalForm(event.getChosenFormName(), null);
        if (form != null)
        {
            add(form);
        }
    }

    @Override
    public void fireFormClosed(EJInternalForm closedForm)
    {
        for (EJRWTApplicationComponent component : _addedComponents)
        {
            component.fireFormClosed(closedForm);
        }
    }

    @Override
    public void fireFormOpened(EJInternalForm openedForm)
    {
        for (EJRWTApplicationComponent component : _addedComponents)
        {
            component.fireFormOpened(openedForm);
        }
    }

    @Override
    public void fireFormSelected(EJInternalForm selectedForm)
    {
        for (EJRWTApplicationComponent component : _addedComponents)
        {
            component.fireFormSelected(selectedForm);
        }
    }

    public EJInternalForm getForm(String formName)
    {

        for (EJRWTSingleFormContainer singleFormContainer : _singleFormContainers)
        {
            if (singleFormContainer.getForm() != null && formName.equals(singleFormContainer.getForm().getProperties().getName()))
            {
                return singleFormContainer.getForm();
            }
        }

        for (EJInternalForm form : getFormContainer().getAllForms())
        {
            if (formName.equals(form.getProperties().getName()))
            {
                return form;
            }
        }

        return null;
    }
    public EJInternalForm getForm(EJInternalForm form)
    {
        
        for (EJRWTSingleFormContainer singleFormContainer : _singleFormContainers)
        {
            if (singleFormContainer.getForm() != null && form.equals(singleFormContainer.getForm()))
            {
                return singleFormContainer.getForm();
            }
        }
        
        for (EJInternalForm aform : getFormContainer().getAllForms())
        {
            if (form.equals(aform))
            {
                return form;
            }
        }
        
        return null;
    }

    public EJInternalForm switchToForm(String key)
    {
        EJRWTFormContainer formContainer = getFormContainer();
        if (formContainer != null)
        {
            EJInternalForm switchToForm = formContainer.switchToForm(key);
            if (switchToForm != null)
            {
                if (formContainer instanceof EJApplicationComponentRenderer)
                {
                    switchTabs((Control) ((EJApplicationComponentRenderer) formContainer).getGuiComponent());
                }

                return switchToForm;
            }
        }
        for (EJRWTSingleFormContainer container : _singleFormContainers)
        {
            if (container.getForm() != null && key.equalsIgnoreCase(container.getForm().getProperties().getName()))
            {
                if (container instanceof EJApplicationComponentRenderer)
                {
                    switchTabs((Control) ((EJApplicationComponentRenderer) container).getGuiComponent());
                }
                return container.getForm();
            }
        }
        return null;
    }
    public EJInternalForm switchToForm(EJInternalForm form)
    {
        EJRWTFormContainer formContainer = getFormContainer();
        if (formContainer != null)
        {
            EJInternalForm switchToForm = formContainer.switchToForm(form);
            if (switchToForm != null)
            {
                if (formContainer instanceof EJApplicationComponentRenderer)
                {
                    switchTabs((Control) ((EJApplicationComponentRenderer) formContainer).getGuiComponent());
                }
                
                return switchToForm;
            }
        }
        for (EJRWTSingleFormContainer container : _singleFormContainers)
        {
            if (container.getForm() != null && form.equals(container.getForm()))
            {
                if (container instanceof EJApplicationComponentRenderer)
                {
                    switchTabs((Control) ((EJApplicationComponentRenderer) container).getGuiComponent());
                }
                return container.getForm();
            }
        }
        return null;
    }

    private void switchTabs(Control control)
    {
        if (control == null || control.isDisposed())
        {
            return;
        }

        Control parent = control.getParent();
        while (parent != null && !parent.isDisposed())
        {
            if (parent.getData("TAB_ITEM") != null)
            {
                CTabItem data = (CTabItem) parent.getData("TAB_ITEM");
                data.getParent().setSelection(data);
                parent = null;
                switchTabs(data.getParent());
            }
            else
            {
                parent = parent.getParent();
            }
        }
    }

    public void updateFormTitle(EJInternalForm form)
    {
        if (_formContainer != null)
        {
            _formContainer.updateFormTitle(form);
        }
        
    }

    public Collection<EJInternalForm> getOpenForms()
    {
        
        return new ArrayList<EJInternalForm>(_formContainer.getAllForms());
    }
}
