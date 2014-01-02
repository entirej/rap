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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
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
            handler.open();
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
            if (cardPane != null)
            {
                cardPane.showPane(pageName);
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

    private void createStackedCanvas(Composite parent, EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        final String name = canvasProperties.getName();
        EJRWTEntireJStackedPane stackedPane = new EJRWTEntireJStackedPane(parent);
        stackedPane.setLayoutData(createCanvasGridData(canvasProperties));
        _stackedPanes.put(name, stackedPane);

        for (EJStackedPageProperties page : canvasProperties.getStackedPageContainer().getAllStackedPageProperties())
        {
            EJRWTEntireJGridPane pagePane = new EJRWTEntireJGridPane(stackedPane, page.getNumCols());
            pagePane.cleanLayout();
            stackedPane.add(page.getName(), pagePane);
            for (EJCanvasProperties properties : page.getContainedCanvases().getAllCanvasProperties())
            {
                createCanvas(pagePane, properties, canvasController);
            }
        }

        if (canvasProperties.getInitialStackedPageName() != null)
        {
            stackedPane.showPane(canvasProperties.getInitialStackedPageName());
        }

        _canvasesIds.add(name);
    }

    private void createTabCanvas(Composite parent, EJCanvasProperties canvasProperties, final EJCanvasController canvasController)
    {
        int style = SWT.FLAT;

        EJFrameworkExtensionProperties rendererProp = EJCoreProperties.getInstance().getApplicationDefinedProperties();
        if (rendererProp != null)
        {
            boolean displayBorder = rendererProp.getBooleanProperty("DISPLAY_TAB_BORDER", true);
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
        EJTabFolder tabFolder = new EJTabFolder(folder);
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
        for (EJTabPageProperties page : allTabPageProperties)
        {
            if (page.isVisible())
            {
                CTabItem tabItem = new CTabItem(folder, SWT.NONE);
                tabItem.setData("TAB_KEY", page.getName());
                EJRWTEntireJGridPane pageCanvas = new EJRWTEntireJGridPane(folder, page.getNumCols());
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

                tabFolder.put(page.getName(), tabItem);
                tabItem.getControl().setEnabled(page.isEnabled());
            }
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
        if (canvasProperties.getDisplayGroupFrame())
        {
            Group group = new Group(parent, SWT.NONE);
            group.setLayout(new FillLayout());
            group.setLayoutData(createCanvasGridData(canvasProperties));
            String frameTitle = canvasProperties.getGroupFrameTitle();
            if (frameTitle != null && frameTitle.length() > 0)
            {
                group.setText(frameTitle);
            }
            parent = group;
        }
        final EJRWTEntireJGridPane groupPane = new EJRWTEntireJGridPane(parent, canvasProperties.getNumCols());
        if (canvasProperties.getDisplayGroupFrame())
        {
            groupPane.cleanLayoutVertical();
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

        if (canvasProperties.getType() == EJCanvasType.SPLIT)
        {
            List<EJCanvasProperties> items = new ArrayList<EJCanvasProperties>(canvasProperties.getSplitCanvasContainer().getAllCanvasProperties());
            int[] weights = new int[items.size()];

            for (EJCanvasProperties containedCanvas : items)
            {
                if (containedCanvas.getType() == EJCanvasType.BLOCK && containedCanvas.getBlockProperties() != null
                        && containedCanvas.getBlockProperties().getMainScreenProperties() != null)
                {
                    weights[items.indexOf(containedCanvas)] = containedCanvas.getBlockProperties().getMainScreenProperties().getWidth() + 1;
                }
                else
                {
                    weights[items.indexOf(containedCanvas)] = containedCanvas.getWidth() + 1;
                }

                switch (containedCanvas.getType())
                {
                    case BLOCK:
                    case GROUP:
                        createGroupCanvas(layoutBody, containedCanvas, canvasController);
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
        }

        @Override
        public void add(EJInternalBlock block)
        {
            // ignore
        }

        void open()
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

            _popupDialog = new EJRWTAbstractDialog(getRWTManager().getShell())
            {
                private static final long serialVersionUID = -4685316941898120169L;

                @Override
                public void createBody(Composite parent)
                {
                    parent.setLayout(new FillLayout());
                    final ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);

                    EJRWTEntireJGridPane _mainPane = new EJRWTEntireJGridPane(scrollComposite, numCols);
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
                    // Add the buttons in reverse order, as they will be added
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

            _popupDialog.getShell().setData("POPUP - " + name);
            _popupDialog.getShell().setText(pageTitle != null ? pageTitle : "");
            // add dialog border offsets
            _popupDialog.getShell().setSize(width + 80, height + 100);
            _popupDialog.centreLocation();
            _popupDialog.open();
        }

        void close()
        {
            if (_popupDialog != null)
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
        final CTabFolder            folder;
        final Map<String, CTabItem> tabPages = new HashMap<String, CTabItem>();

        EJTabFolder(CTabFolder folder)
        {
            super();
            this.folder = folder;
        }

        public void showPage(String pageName)
        {
            CTabItem cTabItem = tabPages.get(pageName);
            if (cTabItem != null)
            {
                folder.setSelection(cTabItem);
            }

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
            return tabPages.get(key);
        }

        CTabItem put(String key, CTabItem value)
        {
            return tabPages.put(key, value);
        }

        CTabItem remove(String key)
        {
            return tabPages.remove(key);
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
}
