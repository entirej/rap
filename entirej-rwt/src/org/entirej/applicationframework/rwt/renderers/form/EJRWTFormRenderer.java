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
package org.entirej.applicationframework.rwt.renderers.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTAbstractDialog;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJStackedPane;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppBlockRenderer;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppFormRenderer;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.common.utils.EJParameterChecker;
import org.entirej.framework.core.data.controllers.EJCanvasController;
import org.entirej.framework.core.data.controllers.EJEmbeddedFormController;
import org.entirej.framework.core.enumerations.EJCanvasSplitOrientation;
import org.entirej.framework.core.enumerations.EJCanvasType;
import org.entirej.framework.core.enumerations.EJPopupButton;
import org.entirej.framework.core.internal.EJInternalBlock;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.containers.interfaces.EJCanvasPropertiesContainer;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJCanvasProperties;
import org.entirej.framework.core.properties.interfaces.EJFormProperties;
import org.entirej.framework.core.properties.interfaces.EJStackedPageProperties;
import org.entirej.framework.core.properties.interfaces.EJTabPageProperties;

public class EJRWTFormRenderer implements EJRWTAppFormRenderer
{

    private EJInternalForm                       _form;
    private EJRWTEntireJGridPane                 _mainPane;
    private LinkedList<String>                   _canvasesIds  = new LinkedList<String>();
    private Map<String, CanvasHandler>           _canvases     = new HashMap<String, CanvasHandler>();
    private Map<String, EJInternalBlock>         _blocks       = new HashMap<String, EJInternalBlock>();
    private Map<String, EJTabFolder>             _tabFolders   = new HashMap<String, EJTabFolder>();
    private Map<String, EJRWTEntireJStackedPane> _stackedPanes = new HashMap<String, EJRWTEntireJStackedPane>();
    private Map<String, Composite>               _formPanes    = new HashMap<String, Composite>();

    @Override
    public void formCleared()
    {

    }

    @Override
    public void formClosed()
    {

    }

    @Override
    public void gainInitialFocus()
    {
        setFocus();

    }

    @Override
    public EJInternalForm getForm()
    {

        return _form;
    }

    @Override
    public void initialiseForm(EJInternalForm form)
    {
        EJParameterChecker.checkNotNull(form, "initialiseForm", "formController");
        _form = form;

    }

    @Override
    public void refreshFormRendererProperty(String arg0)
    {
    }

    @Override
    public void savePerformed()
    {
    }

    @Override
    public void showPopupCanvas(String canvasName)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler handler = (PopupCanvasHandler) canvasHandler;
            handler.open(true);
        }
    }

    @Override
    public void closePopupCanvas(String canvasName)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler handler = (PopupCanvasHandler) canvasHandler;
            handler.close();
        }

    }

    @Override
    public void showStackedPage(String canvasName, String pageName)
    {
        if (canvasName != null && pageName != null)
        {
            EJRWTEntireJStackedPane cardPane = _stackedPanes.get(canvasName);
            if (cardPane != null && !cardPane.isDisposed())
            {
                cardPane.showPane(pageName);
            }
        }
    }

    @Override
    public void openEmbeddedForm(EJEmbeddedFormController formController)
    {
        if (formController == null)
        {
            throw new EJApplicationException("No embedded form controller has been passed to openEmbeddedForm");
        }

        if (formController.getCanvasName() != null)
        {
            Composite composite = _formPanes.get(formController.getCanvasName());
            if (composite != null)
            {
                Control[] children = composite.getChildren();
                for (Control control : children)
                {
                    if (!control.isDisposed())
                    {
                        control.dispose();
                    }
                }

                EJRWTFormRenderer renderer = (EJRWTFormRenderer) formController.getEmbeddedForm().getRenderer();
                final ScrolledComposite scrollComposite = new ScrolledComposite(composite, SWT.V_SCROLL | SWT.H_SCROLL);
                renderer.createControl(scrollComposite);
                scrollComposite.setContent(renderer.getGuiComponent());
                scrollComposite.setExpandHorizontal(true);
                scrollComposite.setExpandVertical(true);
                scrollComposite.setMinSize(formController.getEmbeddedForm().getProperties().getFormWidth(), formController.getEmbeddedForm().getProperties()
                        .getFormHeight());
                composite.layout(true);
                composite.redraw();
            }
            else
            {
                throw new IllegalAccessError("An embedded form can only be opened on EJCanvasType.FORM");
            }
        }
    }

    @Override
    public void closeEmbeddedForm(EJEmbeddedFormController formController)
    {
        if (formController == null)
        {
            throw new EJApplicationException("No embedded form controller has been passed to closeEmbeddedForm");
        }

        if (formController.getCanvasName() != null)
        {
            Composite composite = _formPanes.get(formController.getCanvasName());
            if (composite != null)
            {
                Control[] children = composite.getChildren();
                for (Control control : children)
                {
                    if (!control.isDisposed())
                    {
                        control.dispose();
                    }
                }
                composite.layout(true);
            }
        }
    }

    @Override
    public void showTabPage(String canvasName, String pageName)
    {
        if (canvasName != null && pageName != null)
        {
            EJTabFolder tabPane = _tabFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.showPage(pageName);
            }
        }
    }

    @Override
    public void setTabPageVisible(String canvasName, String pageName, boolean visible)
    {
        if (canvasName != null && pageName != null)
        {
            EJTabFolder tabPane = _tabFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.setTabPageVisible(pageName, visible);
            }
        }

    }

    @Override
    public void createControl(final Composite parent)
    {
        setupGui(parent);
        setFocus();
        _form.getFormController().formInitialised();
    }

    @Override
    public EJRWTEntireJGridPane getGuiComponent()
    {
        if (_mainPane == null)
        {
            throw new IllegalAccessError("Call createControl(Composite parent) before access getGuiComponent()");
        }

        return _mainPane;
    }

    private void setFocus()
    {
        for (String canvasName : _canvasesIds)
        {
            EJCanvasProperties canvasProperties = _form.getProperties().getCanvasProperties(canvasName);

            if (canvasProperties != null && setFocus(canvasProperties))
            {
                return;
            }
        }
    }

    private boolean setFocus(EJCanvasProperties canvasProperties)
    {
        if (canvasProperties.getType() == EJCanvasType.BLOCK)
        {
            if (canvasProperties.getBlockProperties() != null)
            {
                EJInternalEditableBlock block = _form.getBlock(canvasProperties.getBlockProperties().getName());

                if (block.getRendererController() != null)
                {
                    block.getManagedRenderer().gainFocus();
                    return true;
                }
            }

        }
        else if (canvasProperties.getType() == EJCanvasType.GROUP)
        {
            for (EJCanvasProperties groupCanvas : canvasProperties.getGroupCanvasContainer().getAllCanvasProperties())
            {
                if (setFocus(groupCanvas))
                {
                    return true;
                }
            }
        }
        else if (canvasProperties.getType() == EJCanvasType.SPLIT)
        {
            for (EJCanvasProperties groupCanvas : canvasProperties.getSplitCanvasContainer().getAllCanvasProperties())
            {
                if (setFocus(groupCanvas))
                {
                    return true;
                }
            }
        }
        else if (canvasProperties.getType() == EJCanvasType.STACKED)
        {
            for (EJStackedPageProperties pageProps : canvasProperties.getStackedPageContainer().getAllStackedPageProperties())
            {
                if (pageProps.getName().equals(canvasProperties.getInitialStackedPageName() == null ? "" : canvasProperties.getInitialStackedPageName()))
                {
                    for (EJCanvasProperties stackedCanvas : pageProps.getContainedCanvases().getAllCanvasProperties())
                    {
                        if (setFocus(stackedCanvas))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        else if (canvasProperties.getType() == EJCanvasType.TAB)
        {
            for (EJTabPageProperties tabPage : canvasProperties.getTabPageContainer().getAllTabPageProperties())
            {
                if (tabPage.isVisible())
                {
                    _form.getCanvasController().tabPageChanged(canvasProperties.getName(), tabPage.getName());
                    return true;
                }
            }
        }

        return false;
    }

    private void setupGui(final Composite parent)
    {
        EJFormProperties formProperties = _form.getProperties();
        EJCanvasController canvasController = _form.getCanvasController();

        // Now loop through all the forms blocks and create controllers for them
        for (EJInternalBlock block : _form.getAllBlocks())
        {
            String canvasName = block.getProperties().getCanvasName();
            // If the block has not had a canvas defined for it, it cannot be
            // displayed.
            if (canvasName == null || canvasName.trim().length() == 0)
            {
                continue;
            }

            _blocks.put(canvasName, block);
        }
        _mainPane = new EJRWTEntireJGridPane(parent, formProperties.getNumCols());
        _mainPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
        for (EJCanvasProperties canvasProperties : formProperties.getCanvasContainer().getAllCanvasProperties())
        {
            createCanvas(_mainPane, canvasProperties, canvasController);
        }

    }

    private void createCanvas(Composite parent, EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        switch (canvasProperties.getType())
        {
            case BLOCK:
            case GROUP:
                createGroupCanvas(parent, canvasProperties, canvasController);
                break;
            case SPLIT:
                createSplitCanvas(parent, canvasProperties, canvasController);
                break;
            case FORM:
                createFormCanvas(parent, canvasProperties, canvasController);
                break;
            case STACKED:
                createStackedCanvas(parent, canvasProperties, canvasController);
                break;
            case TAB:
                createTabCanvas(parent, canvasProperties, canvasController);
                break;
            case POPUP:
                buildPopupCanvas(canvasProperties, canvasController);
        }
    }

    private void createStackedCanvas(final Composite parent, EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        final String name = canvasProperties.getName();
        EJRWTEntireJStackedPane stackedPane = new EJRWTEntireJStackedPane(parent);
        stackedPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
        stackedPane.setLayoutData(createCanvasGridData(canvasProperties));
        _stackedPanes.put(name, stackedPane);

        for (EJStackedPageProperties page : canvasProperties.getStackedPageContainer().getAllStackedPageProperties())
        {
            
            final ScrolledComposite  scrollComposite =new ScrolledComposite(stackedPane, SWT.V_SCROLL | SWT.H_SCROLL);
            final EJRWTEntireJGridPane pagePane = new EJRWTEntireJGridPane(scrollComposite, page.getNumCols());
            pagePane.cleanLayout();
            stackedPane.add(page.getName(), scrollComposite);
            int width = 0;
                    int height = 0;
            for (EJCanvasProperties properties : page.getContainedCanvases().getAllCanvasProperties())
            {
                createCanvas(pagePane, properties, canvasController);
                
                
                
                int width2 = properties.getWidth();
                
                
                int height2 = properties.getHeight();
                
                if(properties.getBlockProperties()!=null)
                {
                    width2 = properties.getBlockProperties().getMainScreenProperties().getWidth();
                    height2 = properties.getBlockProperties().getMainScreenProperties().getHeight();
                }
                
                if(width<width2)
                {
                    width = width2;
                }
                if(height<height2)
                {
                    height = height2;
                }
            }
            scrollComposite.setContent(pagePane);
            scrollComposite.setMinSize(width, height);
           
            scrollComposite.setExpandHorizontal(true);
            scrollComposite.setExpandVertical(true);
            scrollComposite.setContent(pagePane);
        }

        if (canvasProperties.getInitialStackedPageName() != null)
        {
            stackedPane.showPane(canvasProperties.getInitialStackedPageName());
        }

        _canvasesIds.add(name);
    }

    private void createFormCanvas(Composite parent, EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        final String name = canvasProperties.getName();
        Composite stackedPane = new Composite(parent, SWT.NONE);
        stackedPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
        stackedPane.setLayout(new FillLayout());
        stackedPane.setLayoutData(createCanvasGridData(canvasProperties));
        _formPanes.put(name, stackedPane);

        _canvasesIds.add(name);
        
        if(canvasProperties.getReferredFormId()!=null && canvasProperties.getReferredFormId().length()>0)
        {
            _form.openEmbeddedForm(canvasProperties.getReferredFormId(), name, null);
        }
    }

    private void createTabCanvas(Composite parent, EJCanvasProperties canvasProperties, final EJCanvasController canvasController)
    {
        int style = SWT.FLAT;

        EJFrameworkExtensionProperties rendererProp = EJCoreProperties.getInstance().getApplicationDefinedProperties();
        if (rendererProp != null)
        {
            boolean displayBorder = rendererProp.getBooleanProperty("DISPLAY_TAB_BORDER", false);
            if (displayBorder)
            {
                style = SWT.FLAT | SWT.BORDER;
            }
        }

        switch (canvasProperties.getTabPosition())
        {
            case BOTTOM:
                style = style | SWT.BOTTOM;
                break;
            case LEFT:
                style = style | SWT.LEFT;
                break;
            case RIGHT:
                style = style | SWT.RIGHT;
                break;
            default:
                style = style | SWT.TOP;
                break;
        }
        final String name = canvasProperties.getName();
        final CTabFolder folder = new CTabFolder(parent, style);
        folder.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
        EJTabFolder tabFolder = new EJTabFolder(folder, canvasController);
        folder.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                canvasController.tabPageChanged(name, (String) folder.getSelection().getData("TAB_KEY"));
            }
        });
        _tabFolders.put(name, tabFolder);
        folder.setLayoutData(createCanvasGridData(canvasProperties));

        Collection<EJTabPageProperties> allTabPageProperties = canvasProperties.getTabPageContainer().getAllTabPageProperties();
        int index = 0;
        for (EJTabPageProperties page : allTabPageProperties)
        {
            

                EJTabFolder.Tab tab = tabFolder.newTab(page);
                if (page.isVisible())
                {
                    tab.create();
                }
                tab.index = index;
                index++;

                tabFolder.put(page.getName(), tab);
            
        }

        _canvasesIds.add(name);
    }

    private void buildPopupCanvas(EJCanvasProperties canvasProperties, final EJCanvasController canvasController)
    {

        String name = canvasProperties.getName();

        _canvases.put(name, new PopupCanvasHandler(canvasProperties, canvasController));
    }

    private GridData createCanvasGridData(EJCanvasProperties canvasProperties)
    {
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = canvasProperties.getWidth();
        gridData.heightHint = canvasProperties.getHeight();

        gridData.horizontalSpan = canvasProperties.getHorizontalSpan();
        gridData.verticalSpan = canvasProperties.getVerticalSpan();
        gridData.grabExcessHorizontalSpace = canvasProperties.canExpandHorizontally();
        gridData.grabExcessVerticalSpace = canvasProperties.canExpandVertically();

        if (gridData.grabExcessHorizontalSpace)
        {
            gridData.minimumWidth = canvasProperties.getHeight();
        }
        if (gridData.grabExcessVerticalSpace)
        {
            gridData.minimumHeight = canvasProperties.getWidth();
        }

        return gridData;
    }

    private void createGroupCanvas(Composite parent, EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        String frameTitle = canvasProperties.getGroupFrameTitle();
        if (canvasProperties.getDisplayGroupFrame()&& frameTitle != null && frameTitle.length() > 0)
        {
            Group group = new Group(parent, SWT.NONE);
            group.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
            group.setLayout(new FillLayout());
            group.setLayoutData(createCanvasGridData(canvasProperties));
            
           
                group.setText(frameTitle);
            
            parent = group;
        }
        final EJRWTEntireJGridPane groupPane = new EJRWTEntireJGridPane(parent, canvasProperties.getNumCols(),canvasProperties.getDisplayGroupFrame()?SWT.BORDER:SWT.NONE);
        groupPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
        if (canvasProperties.getDisplayGroupFrame())
        {
            groupPane.cleanLayoutTop();
        }
        else
        {
            groupPane.cleanLayout();
        }

        groupPane.setPaneName(canvasProperties.getName());
        if (!canvasProperties.getDisplayGroupFrame())
        {
            groupPane.setLayoutData(createCanvasGridData(canvasProperties));
        }

        CanvasHandler canvasHandler = new CanvasHandler()
        {

            @Override
            public void add(EJInternalBlock block)
            {

                EJRWTAppBlockRenderer blockRenderer = (EJRWTAppBlockRenderer) block.getRendererController().getRenderer();
                if (blockRenderer == null)
                {
                    throw new EJApplicationException(new EJMessage("Block " + block.getProperties().getName()
                            + " has a canvas defined but no renderer. A block cannot be rendererd if no canvas has been defined."));
                }
                blockRenderer.buildGuiComponent(groupPane);
            }

            @Override
            public EJCanvasType getType()
            {
                return EJCanvasType.BLOCK;
            }
        };
        _canvases.put(groupPane.getPaneName(), canvasHandler);
        _canvasesIds.add(groupPane.getPaneName());
        EJInternalBlock block = _blocks.get(groupPane.getPaneName());
        if (block != null)
        {
            canvasHandler.add(block);
        }
        if (canvasProperties.getType() == EJCanvasType.GROUP)
        {
            for (EJCanvasProperties containedCanvas : canvasProperties.getGroupCanvasContainer().getAllCanvasProperties())
            {
                switch (containedCanvas.getType())
                {
                    case BLOCK:
                    case GROUP:
                        createGroupCanvas(groupPane, containedCanvas, canvasController);
                        break;
                    case FORM:
                        createFormCanvas(groupPane, containedCanvas, canvasController);
                        break;
                    case SPLIT:
                        createSplitCanvas(groupPane, containedCanvas, canvasController);
                        break;
                    case STACKED:
                        createStackedCanvas(groupPane, containedCanvas, canvasController);
                        break;
                    case TAB:
                        createTabCanvas(groupPane, containedCanvas, canvasController);
                        break;
                    case POPUP:
                        throw new AssertionError();
                }
            }
        }
    }

    private void createSplitCanvas(Composite parent, EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        SashForm layoutBody = new SashForm(parent, canvasProperties.getSplitOrientation() == EJCanvasSplitOrientation.HORIZONTAL ? SWT.HORIZONTAL
                : SWT.VERTICAL);
        layoutBody.setLayoutData(createCanvasGridData(canvasProperties));

        layoutBody.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
        if (canvasProperties.getType() == EJCanvasType.SPLIT)
        {
            List<EJCanvasProperties> items = new ArrayList<EJCanvasProperties>(canvasProperties.getSplitCanvasContainer().getAllCanvasProperties());
            int[] weights = new int[items.size()];

            for (EJCanvasProperties containedCanvas : items)
            {
                if (containedCanvas.getType() == EJCanvasType.BLOCK && containedCanvas.getBlockProperties() != null
                        && containedCanvas.getBlockProperties().getMainScreenProperties() != null)
                {
                    weights[items.indexOf(containedCanvas)] = canvasProperties.getSplitOrientation()==EJCanvasSplitOrientation.HORIZONTAL ?containedCanvas.getBlockProperties().getMainScreenProperties().getWidth()+1:containedCanvas.getBlockProperties().getMainScreenProperties().getHeight() + 1;
                }
                else
                {
                    weights[items.indexOf(containedCanvas)] = canvasProperties.getSplitOrientation()==EJCanvasSplitOrientation.HORIZONTAL ?containedCanvas.getWidth()+1:containedCanvas.getHeight() + 1;
                }

                switch (containedCanvas.getType())
                {
                    case BLOCK:
                    case GROUP:
                        createGroupCanvas(layoutBody, containedCanvas, canvasController);
                        break;
                    case FORM:
                        createFormCanvas(layoutBody, containedCanvas, canvasController);
                        break;
                    case SPLIT:
                        createSplitCanvas(layoutBody, containedCanvas, canvasController);
                        break;
                    case STACKED:
                        createStackedCanvas(layoutBody, containedCanvas, canvasController);
                        break;
                    case TAB:
                        createTabCanvas(layoutBody, containedCanvas, canvasController);
                        break;
                    case POPUP:
                        throw new AssertionError();

                }
            }
            layoutBody.setWeights(weights);
        }
    }

    EJRWTApplicationManager getRWTManager()
    {
        return (EJRWTApplicationManager) _form.getFormController().getFrameworkManager().getApplicationManager();
    }

    private final class PopupCanvasHandler implements CanvasHandler
    {
        EJRWTAbstractDialog      _popupDialog;

        final EJCanvasProperties canvasProperties;
        final EJCanvasController canvasController;

        public PopupCanvasHandler(EJCanvasProperties canvasProperties, EJCanvasController canvasController)
        {
            this.canvasController = canvasController;
            this.canvasProperties = canvasProperties;
            open(false);
        }

        @Override
        public void add(EJInternalBlock block)
        {
            // ignore
        }

        void open(boolean show)
        {
            final String name = canvasProperties.getName();
            final String pageTitle = canvasProperties.getPopupPageTitle();
            final int width = canvasProperties.getWidth();
            final int height = canvasProperties.getHeight();
            final int numCols = canvasProperties.getNumCols();

            final String button1Label = canvasProperties.getButtonOneText();
            final String button2Label = canvasProperties.getButtonTwoText();
            final String button3Label = canvasProperties.getButtonThreeText();

            final int ID_BUTTON_1 = 1;
            final int ID_BUTTON_2 = 2;
            final int ID_BUTTON_3 = 3;

            if (_popupDialog == null || _popupDialog.getShell() == null || _popupDialog.getShell().isDisposed())
            {

                _popupDialog = new EJRWTAbstractDialog(getRWTManager().getShell())
                {
                    private static final long serialVersionUID = -4685316941898120169L;

                    @Override
                    public void createBody(Composite parent)
                    {
                        parent.setLayout(new FillLayout());
                        final ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);

                        EJRWTEntireJGridPane _mainPane = new EJRWTEntireJGridPane(scrollComposite, numCols);
                        _mainPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
                        _mainPane.cleanLayout();
                        EJCanvasPropertiesContainer popupCanvasContainer = canvasProperties.getPopupCanvasContainer();
                        Collection<EJCanvasProperties> allCanvasProperties = popupCanvasContainer.getAllCanvasProperties();
                        for (EJCanvasProperties canvasProperties : allCanvasProperties)
                        {
                            createCanvas(_mainPane, canvasProperties, canvasController);
                        }
                        scrollComposite.setContent(_mainPane);
                        scrollComposite.setExpandHorizontal(true);
                        scrollComposite.setExpandVertical(true);
                        scrollComposite.setMinSize(_mainPane.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
                    }

                    @Override
                    public int open()
                    {
                        return super.open();
                    }

                    @Override
                    protected void createButtonsForButtonBar(Composite parent)
                    {
                        // Add the buttons in reverse order, as they will be
                        // added
                        // from left to right
                        addExtraButton(parent, button3Label, ID_BUTTON_3);
                        addExtraButton(parent, button2Label, ID_BUTTON_2);
                        addExtraButton(parent, button1Label, ID_BUTTON_1);
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

                    @Override
                    protected void buttonPressed(int buttonId)
                    {
                        switch (buttonId)
                        {

                            case ID_BUTTON_1:
                            {
                                canvasController.closePopupCanvas(name, EJPopupButton.ONE);
                                break;
                            }
                            case ID_BUTTON_2:
                            {
                                canvasController.closePopupCanvas(name, EJPopupButton.TWO);
                                break;
                            }
                            case ID_BUTTON_3:
                            {
                                canvasController.closePopupCanvas(name, EJPopupButton.THREE);
                                close();
                                break;
                            }

                            default:
                                super.buttonPressed(buttonId);
                                break;
                        }

                    }
                };
                _popupDialog.create();
            }

            if (show)
            {
                _popupDialog.getShell().setData("POPUP - " + name);
                _popupDialog.getShell().setText(pageTitle != null ? pageTitle : "");
                // add dialog border offsets
                _popupDialog.getShell().setSize(width + 80, height + 100);
                _popupDialog.centreLocation();
                _popupDialog.open();
            }
        }

        void close()
        {
            if (_popupDialog != null &&  _popupDialog.getShell() != null && _popupDialog.getShell().isVisible())
            {
                _popupDialog.close();
            }
        }

        @Override
        public EJCanvasType getType()
        {
            return EJCanvasType.POPUP;
        }
    }

    class EJTabFolder
    {
        final CTabFolder         folder;
        final EJCanvasController canvasController;
        final Map<String, Tab>   tabPages = new HashMap<String, Tab>();

        EJTabFolder(CTabFolder folder, EJCanvasController canvasController)
        {
            super();
            this.folder = folder;
            this.canvasController = canvasController;
        }

        public void showPage(String pageName)
        {
            Tab cTabItem = tabPages.get(pageName);
            if (cTabItem != null && cTabItem.item != null)
            {
                folder.setSelection(cTabItem.item);
            }

        }

        public void setTabPageVisible(String pageName, boolean visible)
        {
            final Tab cTabItem = tabPages.get(pageName);
            if (cTabItem != null)
            {
                if (visible)
                {
                    if (cTabItem.item == null)
                    {
                        Display.getDefault().asyncExec(new Runnable()
                        {

                            @Override
                            public void run()
                            {
                                cTabItem.create();

                            }
                        });
                    }
                }
                else
                {
                    if (cTabItem.item != null)
                    {
                        CTabItem[] items = folder.getItems();
                        int index = 0;
                        for (CTabItem cTabItem2 : items)
                        {
                            if (cTabItem2 == cTabItem.item)
                            {
                                cTabItem.index = index;
                                break;
                            }
                            index++;
                        }
                        Display.getDefault().asyncExec(new Runnable()
                        {

                            @Override
                            public void run()
                            {
                                cTabItem.remove();
                            }
                        });
                    }
                }
            }

        }

        Tab newTab(EJTabPageProperties page)
        {
            return new EJTabFolder.Tab(page);
        }

        void clear()
        {
            tabPages.clear();
        }

        boolean containsKey(String key)
        {
            return tabPages.containsKey(key);
        }

        CTabItem get(String key)
        {
            return tabPages.get(key).item;
        }

        void put(String key, Tab value)
        {
            tabPages.put(key, value);
        }

        void remove(String key)
        {
            tabPages.remove(key);
        }

        public String getActiveKey()
        {
            CTabItem selection = folder.getSelection();
            if (selection != null)
            {
                return (String) selection.getData("TAB_KEY");
            }
            return null;
        }

        class Tab
        {
            CTabItem                  item;
            int                       index = -1;

            final EJTabPageProperties page;

            public Tab(EJTabPageProperties page)
            {
                this.page = page;
            }

            void remove()
            {
                if (item != null && !item.isDisposed())
                {
                    item.dispose();
                }
                item = null;
            }

            void create()
            {
                
                    CTabItem tabItem = (index == -1 || folder.getItemCount() < index) ? new CTabItem(folder, SWT.NONE) : new CTabItem(folder, SWT.NONE, index);
                    tabItem.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
                    tabItem.setData("TAB_KEY", page.getName());
                    EJRWTEntireJGridPane pageCanvas = new EJRWTEntireJGridPane(folder, page.getNumCols());
                    pageCanvas.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
                    tabItem.setText(page.getPageTitle() != null && page.getPageTitle().length() > 0 ? page.getPageTitle() : page.getName());
                    tabItem.setControl(pageCanvas);
                    EJCanvasPropertiesContainer containedCanvases = page.getContainedCanvases();
                    for (EJCanvasProperties pageProperties : containedCanvases.getAllCanvasProperties())
                    {
                        createCanvas(pageCanvas, pageProperties, canvasController);
                    }
                    if (folder.getSelection() == null)
                    {
                        folder.setSelection(tabItem);
                    }

                    item = tabItem;
                    tabItem.getControl().setEnabled(page.isEnabled());
                
            }
        }

    }

    private interface CanvasHandler
    {
        EJCanvasType getType();

        void add(EJInternalBlock block);
    }

    @Override
    public String getDisplayedStackedPage(String key)
    {
        EJRWTEntireJStackedPane stackedPane = _stackedPanes.get(key);
        if (stackedPane != null)
        {
            return stackedPane.getActiveControlKey();
        }

        return null;
    }

    @Override
    public String getDisplayedTabPage(String key)
    {
        EJTabFolder tabFolder = _tabFolders.get(key);
        if (tabFolder != null)
        {
            return tabFolder.getActiveKey();
        }
        return null;
    }

    @Override
    public void clearCanvasMessages(String canvasName)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setCanvasMessages(String canvasName, Collection<EJMessage> messages)
    {
        // TODO Auto-generated method stub
        
    }
    
    
    
}
