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
package org.entirej.applicationframework.rwt.renderers.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTAbstractDialog;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTDialogContext;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTDialogTray;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTTrayDialog.TrayLocation;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTTrayPane;
import org.entirej.applicationframework.rwt.application.form.containers.ITrayPane;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJStackedPane;
import org.entirej.applicationframework.rwt.layout.EJRWTScrolledComposite;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppBlockRenderer;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppFormRenderer;
import org.entirej.applicationframework.rwt.renderers.form.EJDrawerFolder.DrawerTab;
import org.entirej.applicationframework.rwt.utils.EJRWTVisualAttributeUtils;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.common.utils.EJParameterChecker;
import org.entirej.framework.core.data.controllers.EJCanvasController;
import org.entirej.framework.core.data.controllers.EJEmbeddedFormController;
import org.entirej.framework.core.enumerations.EJCanvasMessagePosition;
import org.entirej.framework.core.enumerations.EJCanvasSplitOrientation;
import org.entirej.framework.core.enumerations.EJCanvasType;
import org.entirej.framework.core.enumerations.EJPopupButton;
import org.entirej.framework.core.internal.EJInternalBlock;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.EJCoreCanvasProperties;
import org.entirej.framework.core.properties.EJCoreMainScreenProperties;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.containers.interfaces.EJCanvasPropertiesContainer;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJCanvasProperties;
import org.entirej.framework.core.properties.interfaces.EJDrawerPageProperties;
import org.entirej.framework.core.properties.interfaces.EJFormProperties;
import org.entirej.framework.core.properties.interfaces.EJMessagePaneProperties;
import org.entirej.framework.core.properties.interfaces.EJStackedPageProperties;
import org.entirej.framework.core.properties.interfaces.EJTabPageProperties;

public class EJRWTFormRenderer implements EJRWTAppFormRenderer
{

    private interface UICallCache
    {
        void call();
    }

    protected EJInternalForm                        _form;
    protected EJRWTEntireJGridPane                  _mainPane;
    protected LinkedList<String>                    _canvasesIds        = new LinkedList<String>();
    protected Map<String, CanvasHandler>            _canvases           = new HashMap<String, CanvasHandler>();
    protected Map<String, EJInternalBlock>          _blocks             = new HashMap<String, EJInternalBlock>();
    protected Map<String, ITabFolder>               _tabFolders         = new HashMap<String, ITabFolder>();
    protected Map<String, EJDrawerFolder>           _drawerFolders      = new HashMap<String, EJDrawerFolder>();
    protected Map<String, EJRWTEntireJStackedPane>  _stackedPanes       = new HashMap<String, EJRWTEntireJStackedPane>();
    protected Map<String, Composite>                _formPanes          = new HashMap<String, Composite>();
    protected Map<String, String>                   _tabFoldersCache    = new HashMap<String, String>();
    protected Map<String, String>                   _drawerFoldersCache = new HashMap<String, String>();
    protected Map<String, String>                   _stackedPanesCache  = new HashMap<String, String>();
    protected Map<String, EJEmbeddedFormController> _formPanesCache     = new HashMap<String, EJEmbeddedFormController>();

    private Map<String, Collection<EJMessage>>      _messageCache       = new HashMap<>();
    private List<String>                            _showPopupCache     = new LinkedList<>();
    private List<UICallCache>                       _uicallCache        = new LinkedList<>();

    @Override
    public void formCleared()
    {

    }

    @Override
    public void formClosed()
    {
        if (_mainPane != null)
            _mainPane.dispose();
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
        // build all form canvases
        Collection<EJCanvasProperties> allFormCanvases = EJRWTCanvasRetriever.retriveAllFormCanvases(_form.getProperties());
        for (EJCanvasProperties formCanvas : allFormCanvases)
        {
            if (formCanvas.getReferredFormId() != null && formCanvas.getReferredFormId().length() > 0)
            {
                final String name = formCanvas.getName();
                _form.addEmbeddedForm(formCanvas.getReferredFormId(), name, null);
            }
        }

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
        else
            _showPopupCache.add(canvasName);
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
        else
            _showPopupCache.remove(canvasName);

    }

    @Override
    public void showStackedPage(final String canvasName, final String pageName)
    {
        // Display.getDefault().asyncExec(new Runnable()
        // {
        //
        // @Override
        // public void run()
        // {
        if (canvasName != null && pageName != null)
        {
            EJRWTEntireJStackedPane cardPane = _stackedPanes.get(canvasName);
            if (cardPane != null && !cardPane.isDisposed())
            {
                cardPane.showPane(pageName);
            }
            else
            {
                _stackedPanesCache.put(canvasName, pageName);
            }
        }

        // }
        // });

    }

    @Override
    public void openEmbeddedForm(EJEmbeddedFormController formController)
    {
        if (formController == null)
        {
            throw new EJApplicationException("No embedded form controller has been passed to openEmbeddedForm");
        }
        EJRWTFormRenderer renderer = (EJRWTFormRenderer) formController.getEmbeddedForm().getRenderer();
        renderer.init();
        if (formController.getCanvasName() != null)
        {
            Composite composite = _formPanes.get(formController.getCanvasName());
            if (composite != null)
            {
                createEmbededFormUI(formController, renderer, composite);
            }
            else
            {
                _formPanesCache.put(formController.getCanvasName(), formController);
            }
        }
    }

    private void createEmbededFormUI(EJEmbeddedFormController formController, EJRWTFormRenderer renderer, Composite composite)
    {
        Control[] children = composite.getChildren();
        for (Control control : children)
        {
            if (!control.isDisposed())
            {
                control.dispose();
            }
        }

        final ScrolledComposite scrollComposite = new EJRWTScrolledComposite(composite, SWT.V_SCROLL | SWT.H_SCROLL);

        renderer.create(scrollComposite);
        EJRWTEntireJGridPane entireJGridPane = renderer.getGuiComponent();
        entireJGridPane.cleanLayout();
        scrollComposite.setContent(entireJGridPane);
        scrollComposite.setExpandHorizontal(true);
        scrollComposite.setExpandVertical(true);
        scrollComposite.setMinSize(formController.getEmbeddedForm().getProperties().getFormWidth(), formController.getEmbeddedForm().getProperties().getFormHeight());
        composite.layout(true);
        composite.redraw();
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
            else
            {
                _uicallCache.add(() -> closeEmbeddedForm(formController));
            }
        }
    }

    @Override
    public void showTabPage(String canvasName, String pageName)
    {
        if (canvasName != null && pageName != null)
        {
            ITabFolder tabPane = _tabFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.showPage(pageName);
            }
            else
            {
                _tabFoldersCache.put(canvasName, pageName);
            }
        }
    }

    @Override
    public void setPopupStatusBarMessage(String canvasName, String status)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
            popupCanvasHandler.setStatusBarMessage(status);
        }
        else
        {
            _uicallCache.add(() -> setPopupStatusBarMessage(canvasName, status));
        }
    }

    @Override
    public void showDrawerPage(String canvasName, String pageName)
    {
        if (canvasName != null && pageName != null)
        {
            EJDrawerFolder tabPane = _drawerFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.showPage(pageName);
            }
            else
            {
                _drawerFoldersCache.put(canvasName, pageName);
            }
        }

    }

    @Override
    public void closeDrawerPage(String canvasName, String pageName)
    {
        if (canvasName != null && pageName != null)
        {
            EJDrawerFolder tabPane = _drawerFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.closePage(pageName);
            }
            else
            {
                _drawerFoldersCache.remove(canvasName);
            }
        }

    }

    @Override
    public void setTabPageVisible(String canvasName, String pageName, boolean visible)
    {
        if (canvasName != null && pageName != null)
        {
            ITabFolder tabPane = _tabFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.setTabPageVisible(pageName, visible);
            }
            else
                _uicallCache.add(() -> setTabPageVisible(canvasName, pageName, visible));
        }

    }

    @Override
    public void setDrawerPageVisible(String canvasName, String pageName, boolean visible)
    {
        if (canvasName != null && pageName != null)
        {
            EJDrawerFolder tabPane = _drawerFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.setTabPageVisible(pageName, visible);
            }
            else
                _uicallCache.add(() -> setDrawerPageVisible(canvasName, pageName, visible));
        }

    }

    @Override
    public void setDrawerVisible(String canvasName, boolean visible)
    {
        if (canvasName != null)
        {
            EJDrawerFolder tabPane = _drawerFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.setVisible(visible);
            }
            else
                _uicallCache.add(() -> setDrawerVisible(canvasName, visible));
        }

    }

    @Override
    public void init()
    {

        try
        {
            _form.getFormController().formInitialised();
        }
        catch (EJApplicationException e)
        {
            _form.getFrameworkManager().handleException(e);
        }

    }

    @Override
    public void create(final Composite parent)
    {

        setupGui(parent);

        Display.getDefault().asyncExec(new Runnable()
        {

            @Override
            public void run()
            {
                setFocus();

            }
        });

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

    protected void setFocus()
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
        _showPopupCache.forEach(this::showPopupCanvas);

        ArrayList<UICallCache> uiactions = new ArrayList<>(_uicallCache);
        uiactions.forEach(UICallCache::call);

        _messageCache.entrySet().forEach(e -> {

            CanvasHandler canvasHandler = _canvases.get(e.getKey());
            if (canvasHandler != null)
            {
                canvasHandler.setCanvasMessages(e.getValue());
            }
        });

        _uicallCache.removeAll(uiactions);

        _messageCache.clear();
        _showPopupCache.clear();

        _mainPane.addDisposeListener(new DisposeListener()
        {

            @Override
            public void widgetDisposed(DisposeEvent event)
            {
                Collection<CanvasHandler> values = _canvases.values();
                for (CanvasHandler canvasHandler : values)
                {
                    if (canvasHandler instanceof PopupCanvasHandler)
                    {
                        PopupCanvasHandler handler = (PopupCanvasHandler) canvasHandler;
                        if (handler._popupDialog != null && handler._popupDialog.getShell() != null)
                        {
                            handler._popupDialog.getShell().dispose();
                        }
                    }
                }

            }
        });

    }

    protected void createCanvas(Composite parent, EJCanvasProperties canvasProperties, EJCanvasController canvasController)
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
            case DRAWER:
                createDrawerCanvas(parent, canvasProperties, canvasController);
                break;
            case POPUP:
                buildPopupCanvas(canvasProperties, canvasController);
                break;
            case SEPARATOR:
                createSeparatorCanvas(parent, canvasProperties);
                break;
        }

    }

    protected void createSeparatorCanvas(Composite parent, EJCanvasProperties component)
    {

        int style = SWT.SEPARATOR;

        if (component.getSplitOrientation() == EJCanvasSplitOrientation.HORIZONTAL)
        {
            style = style | SWT.HORIZONTAL;
        }
        else
        {
            style = style | SWT.VERTICAL;
        }

        Label layoutBody = new Label(parent, style);
        layoutBody.setLayoutData(createCanvasGridData(component));

        switch (component.getLineStyle())
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

    }

    private void createStackedCanvas(Composite parent, final EJCanvasProperties canvasProperties, final EJCanvasController canvasController)
    {
        final String name = canvasProperties.getName();

        final EJRWTTrayPane trayPane = new EJRWTTrayPane(parent);
        trayPane.setLayoutData(createCanvasGridData(canvasProperties));
        parent = trayPane;
        final EJRWTEntireJStackedPane stackedPane = new EJRWTEntireJStackedPane(parent);
        trayPane.initBase(stackedPane);
        stackedPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
        stackedPane.setLayoutData(createCanvasGridData(canvasProperties));
        _stackedPanes.put(name, stackedPane);
        CanvasHandler canvasHandler = new CanvasHandler()
        {

            private Collection<EJMessage> msgs;

            @Override
            public void clearCanvasMessages()
            {
                this.msgs = null;
                if (trayPane != null && !trayPane.isDisposed())
                {
                    trayPane.closeTray();
                }

            }

            @Override
            public void setTrayContent(String id)
            {
                if (id == null && trayPane.getTray() != null)
                {
                    trayPane.closeTray();
                    return;
                }

                if (id != null)
                {
                    createFormTray(_form, canvasProperties, trayPane, id);

                }

            }

            @Override
            public void setCanvasMessages(Collection<EJMessage> messages)
            {
                this.msgs = messages;
                if (trayPane != null && !trayPane.isDisposed())
                {

                    if (trayPane.getTray() != null)
                    {
                        trayPane.closeTray();
                    }

                    {
                        MessageTray messageTray = new MessageTray(canvasProperties.getMessagePaneProperties())
                        {

                            @Override
                            void close()
                            {
                                if (trayPane != null && !trayPane.isDisposed())
                                {
                                    trayPane.closeTray();
                                }

                            }

                        };
                        messageTray.setMessages(msgs);

                        TrayLocation location = TrayLocation.RIGHT;

                        switch (canvasProperties.getMessagePaneProperties().getPosition())
                        {
                            case BOTTOM:
                                location = TrayLocation.BOTTOM;
                                break;
                            case LEFT:
                                location = TrayLocation.LEFT;
                                break;
                            case RIGHT:
                                location = TrayLocation.RIGHT;
                                break;
                            case TOP:
                                location = TrayLocation.TOP;
                                break;

                            default:
                                break;
                        }

                        trayPane.openTray(location, messageTray, canvasProperties.getMessagePaneProperties().getSize());
                    }

                }

            }

            @Override
            public void add(EJInternalBlock block)
            {

            }

            @Override
            public EJCanvasType getType()
            {
                return EJCanvasType.STACKED;
            }
        };
        _canvases.put(canvasProperties.getName(), canvasHandler);
        // if (!canvasProperties.getMessagePaneProperties().getCloseable())
        // {
        // canvasHandler.setCanvasMessages(Collections.<EJMessage> emptyList());
        // }
        for (final EJStackedPageProperties page : canvasProperties.getStackedPageContainer().getAllStackedPageProperties())
        {

            stackedPane.add(page.getName(), new EJRWTEntireJStackedPane.StackedPage()
            {
                Control control;

                @Override
                public String getKey()
                {
                    return page.getName();
                }

                @Override
                public Control getControl()
                {

                    if (control != null && !control.isDisposed())
                    {
                        return control;
                    }

                    final ScrolledComposite scrollComposite = new EJRWTScrolledComposite(stackedPane, SWT.V_SCROLL | SWT.H_SCROLL);
                    control = scrollComposite;
                    final EJRWTEntireJGridPane pagePane = new EJRWTEntireJGridPane(scrollComposite, page.getNumCols());
                    pagePane.cleanLayout();
                    int width = 0;
                    int height = 0;
                    for (EJCanvasProperties properties : page.getContainedCanvases().getAllCanvasProperties())
                    {
                        createCanvas(pagePane, properties, canvasController);

                        int width2 = properties.getWidth();

                        int height2 = properties.getHeight();

                        if (properties.getBlockProperties() != null)
                        {
                            width2 = properties.getBlockProperties().getMainScreenProperties().getWidth();
                            height2 = properties.getBlockProperties().getMainScreenProperties().getHeight();
                        }

                        if (width < width2)
                        {
                            width = width2;
                        }
                        if (height < height2)
                        {
                            height = height2;
                        }
                    }
                    scrollComposite.setContent(pagePane);
                    scrollComposite.setMinSize(width, height);

                    scrollComposite.setExpandHorizontal(true);
                    scrollComposite.setExpandVertical(true);
                    scrollComposite.setContent(pagePane);
                    stackedPane.layout();
                    return control;
                }
            });

        }

        if (canvasProperties.getInitialStackedPageName() != null)
        {
            stackedPane.showPane(canvasProperties.getInitialStackedPageName());
        }
        if (_stackedPanesCache.containsKey(name))
        {
            stackedPane.showPane(_stackedPanesCache.get(name));
            _stackedPanesCache.remove(name);
        }
        _canvasesIds.add(name);
    }

    private void createFormCanvas(Composite parent, final EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {

        final EJRWTTrayPane trayPane = new EJRWTTrayPane(parent);
        trayPane.setLayoutData(createCanvasGridData(canvasProperties));
        parent = trayPane;
        final String name = canvasProperties.getName();
        Composite stackedPane = new Composite(parent, SWT.NONE);
        stackedPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
        stackedPane.setLayout(new FillLayout());
        stackedPane.setLayoutData(createCanvasGridData(canvasProperties));

        EJ_RWT.setTestId(trayPane, _form.getProperties().getName() + "." + canvasProperties.getName());
        _formPanes.put(name, stackedPane);
        trayPane.initBase(stackedPane);
        _canvasesIds.add(name);
        CanvasHandler canvasHandler = new CanvasHandler()
        {

            private Collection<EJMessage> msgs;

            @Override
            public void clearCanvasMessages()
            {
                this.msgs = null;
                if (trayPane != null && !trayPane.isDisposed())
                {
                    trayPane.closeTray();
                }

            }

            @Override
            public void setTrayContent(String id)
            {
                if (id == null && trayPane.getTray() != null)
                {
                    trayPane.closeTray();
                    return;
                }

                if (id != null)
                {
                    createFormTray(_form, canvasProperties, trayPane, id);

                }

            }

            @Override
            public void setCanvasMessages(Collection<EJMessage> messages)
            {
                this.msgs = messages;
                if (trayPane != null && !trayPane.isDisposed())
                {

                    if (trayPane.getTray() != null)
                    {
                        trayPane.closeTray();
                    }

                    {
                        MessageTray messageTray = new MessageTray(canvasProperties.getMessagePaneProperties())
                        {

                            @Override
                            void close()
                            {
                                if (trayPane != null && !trayPane.isDisposed())
                                {
                                    trayPane.closeTray();
                                }

                            }

                        };
                        messageTray.setMessages(msgs);

                        TrayLocation location = TrayLocation.RIGHT;

                        switch (canvasProperties.getMessagePaneProperties().getPosition())
                        {
                            case BOTTOM:
                                location = TrayLocation.BOTTOM;
                                break;
                            case LEFT:
                                location = TrayLocation.LEFT;
                                break;
                            case RIGHT:
                                location = TrayLocation.RIGHT;
                                break;
                            case TOP:
                                location = TrayLocation.TOP;
                                break;

                            default:
                                break;
                        }

                        trayPane.openTray(location, messageTray, canvasProperties.getMessagePaneProperties().getSize());
                    }

                }

            }

            @Override
            public void add(EJInternalBlock block)
            {

            }

            @Override
            public EJCanvasType getType()
            {
                return EJCanvasType.FORM;
            }
        };
        _canvases.put(canvasProperties.getName(), canvasHandler);
        // if (!canvasProperties.getMessagePaneProperties().getCloseable())
        // {
        // canvasHandler.setCanvasMessages(Collections.<EJMessage> emptyList());
        // }

        if (_formPanesCache.containsKey(name))
        {
            EJEmbeddedFormController formController = _formPanesCache.get(name);
            Composite composite = _formPanes.get(formController.getCanvasName());
            if (composite != null)
            {
                EJRWTFormRenderer renderer = (EJRWTFormRenderer) formController.getEmbeddedForm().getRenderer();
                createEmbededFormUI(formController, renderer, composite);
                _formPanesCache.remove(name);
            }

        }
        else
        {
            if (canvasProperties.getReferredFormId() != null && canvasProperties.getReferredFormId().length() > 0)
            {
                _form.openEmbeddedForm(canvasProperties.getReferredFormId(), name, null);
            }
        }
    }

    private void createTabCanvas(Composite parent, final EJCanvasProperties canvasProperties, final EJCanvasController canvasController)
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

        final EJRWTTrayPane trayPane = new EJRWTTrayPane(parent);

        EJ_RWT.setTestId(trayPane, _form.getProperties().getName() + "." + canvasProperties.getName());
        trayPane.setLayoutData(createCanvasGridData(canvasProperties));
        parent = trayPane;

        ITabFolder folder = null;

        CTabFolder cfolder = new CTabFolder(parent, style);

        cfolder.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
        final EJTabFolder tabFolder = new EJTabFolder(this, cfolder, canvasController);
        trayPane.initBase(tabFolder.getFolder());
        cfolder.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (tabFolder.canFireEvent())
                    canvasController.tabPageChanged(name, tabFolder.getActiveKey());

                EJ_RWT.setAttribute(cfolder, "ej-item-selection", tabFolder.getActiveKey());
            }
        });
        folder = tabFolder;

        CanvasHandler canvasHandler = new CanvasHandler()
        {

            private Collection<EJMessage> msgs;

            @Override
            public void clearCanvasMessages()
            {
                this.msgs = null;
                if (trayPane != null && !trayPane.isDisposed())
                {
                    trayPane.closeTray();
                }

            }

            @Override
            public void setTrayContent(String id)
            {
                if (id == null && trayPane.getTray() != null)
                {
                    trayPane.closeTray();
                    return;
                }

                if (id != null)
                {
                    createFormTray(_form, canvasProperties, trayPane, id);

                }

            }

            @Override
            public void setCanvasMessages(Collection<EJMessage> messages)
            {
                this.msgs = messages;
                if (trayPane != null && !trayPane.isDisposed())
                {

                    if (trayPane.getTray() != null)
                    {
                        trayPane.closeTray();
                    }

                    {
                        MessageTray messageTray = new MessageTray(canvasProperties.getMessagePaneProperties())
                        {

                            @Override
                            void close()
                            {
                                if (trayPane != null && !trayPane.isDisposed())
                                {
                                    trayPane.closeTray();
                                }

                            }

                        };
                        messageTray.setMessages(msgs);

                        TrayLocation location = TrayLocation.RIGHT;

                        switch (canvasProperties.getMessagePaneProperties().getPosition())
                        {
                            case BOTTOM:
                                location = TrayLocation.BOTTOM;
                                break;
                            case LEFT:
                                location = TrayLocation.LEFT;
                                break;
                            case RIGHT:
                                location = TrayLocation.RIGHT;
                                break;
                            case TOP:
                                location = TrayLocation.TOP;
                                break;

                            default:
                                break;
                        }

                        trayPane.openTray(location, messageTray, canvasProperties.getMessagePaneProperties().getSize());
                    }

                }

            }

            @Override
            public void add(EJInternalBlock block)
            {

            }

            @Override
            public EJCanvasType getType()
            {
                return EJCanvasType.TAB;
            }
        };
        _canvases.put(canvasProperties.getName(), canvasHandler);
        // if (!canvasProperties.getMessagePaneProperties().getCloseable())
        // {
        // canvasHandler.setCanvasMessages(Collections.<EJMessage> emptyList());
        // }

        _tabFolders.put(name, folder);
        folder.getFolder().setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

        Collection<EJTabPageProperties> allTabPageProperties = canvasProperties.getTabPageContainer().getAllTabPageProperties();
        int index = 0;
        List<ITabFolder.ITab> tabs = new ArrayList<>();
        for (EJTabPageProperties page : allTabPageProperties)
        {

            ITabFolder.ITab tab = folder.newTab(page);
            if (page.isVisible())
                tabs.add(tab);
            tab.setIndex(index);
            index++;

            folder.put(page.getName(), tab);

        }
        boolean create = true;
        for (ITabFolder.ITab tab : tabs)
        {

            tab.create(create);
            create = false;

        }

        EJ_RWT.setAttribute(cfolder, "ej-item-selection", tabFolder.getActiveKey());

        if (_tabFoldersCache.containsKey(name))
        {
            folder.showPage(_tabFoldersCache.get(name));
            _tabFoldersCache.remove(name);
        }

        _canvasesIds.add(name);
    }

    private void createDrawerCanvas(Composite parent, final EJCanvasProperties canvasProperties, final EJCanvasController canvasController)
    {
        int style = SWT.NONE;

        final String name = canvasProperties.getName();

        final EJRWTTrayPane trayPane = new EJRWTTrayPane(parent);
        trayPane.setLayoutData(createCanvasGridData(canvasProperties));
        EJ_RWT.setTestId(trayPane, _form.getProperties().getName() + "." + canvasProperties.getName());
        parent = trayPane;

        EJDrawerFolder folder = null;

        final EJDrawerFolder tabFolder = new EJDrawerFolder(this, canvasController, parent, style)
        {

            @Override
            protected void selection(String page)
            {
                if (canFireEvent())
                    canvasController.drawerPageChanged(name, page);

                EJ_RWT.setAttribute(getFolder(), "ej-item-selection", page);
            }
        };
        // tabFolder.setDefaultWidth(canvasProperties.getWidth());
        tabFolder.setPosition(canvasProperties.getDrawerPosition());
        tabFolder.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
        trayPane.initBase(tabFolder.getFolder());

        folder = tabFolder;

        CanvasHandler canvasHandler = new CanvasHandler()
        {

            private Collection<EJMessage> msgs;

            @Override
            public void clearCanvasMessages()
            {
                this.msgs = null;
                if (trayPane != null && !trayPane.isDisposed())
                {
                    trayPane.closeTray();
                }

            }

            @Override
            public void setTrayContent(String id)
            {
                if (id == null && trayPane.getTray() != null)
                {
                    trayPane.closeTray();
                    return;
                }

                if (id != null)
                {
                    createFormTray(_form, canvasProperties, trayPane, id);

                }

            }

            @Override
            public void setCanvasMessages(Collection<EJMessage> messages)
            {
                this.msgs = messages;
                if (trayPane != null && !trayPane.isDisposed())
                {

                    if (trayPane.getTray() != null)
                    {
                        trayPane.closeTray();
                    }

                    {
                        MessageTray messageTray = new MessageTray(canvasProperties.getMessagePaneProperties())
                        {

                            @Override
                            void close()
                            {
                                if (trayPane != null && !trayPane.isDisposed())
                                {
                                    trayPane.closeTray();
                                }

                            }

                        };
                        messageTray.setMessages(msgs);

                        TrayLocation location = TrayLocation.RIGHT;

                        switch (canvasProperties.getMessagePaneProperties().getPosition())
                        {
                            case BOTTOM:
                                location = TrayLocation.BOTTOM;
                                break;
                            case LEFT:
                                location = TrayLocation.LEFT;
                                break;
                            case RIGHT:
                                location = TrayLocation.RIGHT;
                                break;
                            case TOP:
                                location = TrayLocation.TOP;
                                break;

                            default:
                                break;
                        }

                        trayPane.openTray(location, messageTray, canvasProperties.getMessagePaneProperties().getSize());
                    }

                }

            }

            @Override
            public void add(EJInternalBlock block)
            {

            }

            @Override
            public EJCanvasType getType()
            {
                return EJCanvasType.TAB;
            }
        };
        _canvases.put(canvasProperties.getName(), canvasHandler);
        // if (!canvasProperties.getMessagePaneProperties().getCloseable())
        // {
        // canvasHandler.setCanvasMessages(Collections.<EJMessage> emptyList());
        // }

        _drawerFolders.put(name, folder);
        folder.getFolder().setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

        Collection<EJDrawerPageProperties> allTabPageProperties = canvasProperties.getDrawerPageContainer().getAllDrawerPageProperties();
        int index = 0;
        for (EJDrawerPageProperties page : allTabPageProperties)
        {

            DrawerTab tab = folder.newTab(page);
            if (page.isVisible())
            {

                tab.create(index == 0);
            }
            tab.setIndex(index);
            index++;

            folder.put(page.getName(), tab);

        }
        EJ_RWT.setAttribute(folder, "ej-item-selection", folder.getActiveKey());
        if (_drawerFoldersCache.containsKey(name))
        {
            folder.showPage(_drawerFoldersCache.get(name));
            _drawerFoldersCache.remove(name);
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

    private void createGroupCanvas(Composite parent, final EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {

        final EJRWTTrayPane trayPane = new EJRWTTrayPane(parent);
        trayPane.setLayoutData(createCanvasGridData(canvasProperties));

        EJ_RWT.setTestId(trayPane, _form.getProperties().getName() + "." + canvasProperties.getName());
        parent = trayPane;

        String frameTitle = canvasProperties.getGroupFrameTitle();
        if (canvasProperties.getDisplayGroupFrame() && frameTitle != null && frameTitle.length() > 0)
        {
            Group group = new Group(parent, SWT.NONE);
            group.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
            group.setLayout(new FillLayout());
            group.setLayoutData(createCanvasGridData(canvasProperties));

            group.setText(frameTitle);
            trayPane.initBase(group);
            parent = group;
        }
        final EJRWTEntireJGridPane groupPane = new EJRWTEntireJGridPane(parent, canvasProperties.getNumCols(), canvasProperties.getDisplayGroupFrame() && (frameTitle == null || frameTitle.length() == 0) ? SWT.BORDER : SWT.NONE);
        groupPane.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
        if (canvasProperties.getDisplayGroupFrame())
        {
            // .cleanLayoutTop();
        }
        else
        {
            // groupPane.cleanLayout();
        }

        groupPane.setPaneName(canvasProperties.getName());
        if (!(canvasProperties.getDisplayGroupFrame() && frameTitle != null && frameTitle.length() > 0))
        {
            groupPane.setLayoutData(createCanvasGridData(canvasProperties));
            trayPane.initBase(groupPane);
        }

        CanvasHandler canvasHandler = new CanvasHandler()
        {

            private Collection<EJMessage> msgs;

            @Override
            public void clearCanvasMessages()
            {
                this.msgs = null;
                if (trayPane != null && !trayPane.isDisposed())
                {
                    trayPane.closeTray();
                }

            }

            @Override
            public void setTrayContent(String id)
            {
                if (id == null && trayPane.getTray() != null)
                {
                    trayPane.closeTray();
                    return;
                }

                if (id != null)
                {
                    createFormTray(_form, canvasProperties, trayPane, id);

                }

            }

            @Override
            public void setCanvasMessages(Collection<EJMessage> messages)
            {
                this.msgs = messages;
                if (trayPane != null && !trayPane.isDisposed())
                {

                    if (trayPane.getTray() != null)
                    {
                        trayPane.closeTray();
                    }

                    {
                        MessageTray messageTray = new MessageTray(canvasProperties.getMessagePaneProperties())
                        {

                            @Override
                            void close()
                            {
                                if (trayPane != null && !trayPane.isDisposed())
                                {
                                    trayPane.closeTray();
                                }

                            }

                        };
                        messageTray.setMessages(msgs);

                        TrayLocation location = TrayLocation.RIGHT;

                        switch (canvasProperties.getMessagePaneProperties().getPosition())
                        {
                            case BOTTOM:
                                location = TrayLocation.BOTTOM;
                                break;
                            case LEFT:
                                location = TrayLocation.LEFT;
                                break;
                            case RIGHT:
                                location = TrayLocation.RIGHT;
                                break;
                            case TOP:
                                location = TrayLocation.TOP;
                                break;

                            default:
                                break;
                        }

                        trayPane.openTray(location, messageTray, canvasProperties.getMessagePaneProperties().getSize());
                    }

                }

            }

            @Override
            public void add(EJInternalBlock block)
            {

                EJRWTAppBlockRenderer blockRenderer = (EJRWTAppBlockRenderer) block.getRendererController().getRenderer();
                if (blockRenderer == null)
                {
                    throw new EJApplicationException(new EJMessage("Block " + block.getProperties().getName() + " has a canvas defined but no renderer. A block cannot be rendererd if no canvas has been defined."));
                }
                blockRenderer.buildGuiComponent(groupPane);
                trayPane.setLayoutData(groupPane.getLayoutData());
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
        // if (!canvasProperties.getMessagePaneProperties().getCloseable())
        // {
        // canvasHandler.setCanvasMessages(Collections.<EJMessage> emptyList());
        // }
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
                    case SEPARATOR:
                        createSeparatorCanvas(groupPane, containedCanvas);
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
                    case DRAWER:
                        createDrawerCanvas(groupPane, containedCanvas, canvasController);
                        break;
                    case POPUP:
                        throw new AssertionError();
                }
            }
        }
    }

    private void createSplitCanvas(Composite parent, final EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {

        final EJRWTTrayPane trayPane = new EJRWTTrayPane(parent);

        EJ_RWT.setTestId(trayPane, _form.getProperties().getName() + "." + canvasProperties.getName());
        trayPane.setLayoutData(createCanvasGridData(canvasProperties));
        parent = trayPane;
        SashForm layoutBody = new SashForm(parent, canvasProperties.getSplitOrientation() == EJCanvasSplitOrientation.HORIZONTAL ? SWT.HORIZONTAL : SWT.VERTICAL);
        trayPane.initBase(layoutBody);
        layoutBody.setLayoutData(createCanvasGridData(canvasProperties));

        CanvasHandler canvasHandler = new CanvasHandler()
        {

            private Collection<EJMessage> msgs;

            @Override
            public void clearCanvasMessages()
            {
                this.msgs = null;
                if (trayPane != null && !trayPane.isDisposed())
                {
                    trayPane.closeTray();
                }

            }

            @Override
            public void setTrayContent(String id)
            {
                if (id == null && trayPane.getTray() != null)
                {
                    trayPane.closeTray();
                    return;
                }

                if (id != null)
                {
                    createFormTray(_form, canvasProperties, trayPane, id);

                }

            }

            @Override
            public void setCanvasMessages(Collection<EJMessage> messages)
            {
                this.msgs = messages;
                if (trayPane != null && !trayPane.isDisposed())
                {

                    if (trayPane.getTray() != null)
                    {
                        trayPane.closeTray();
                    }

                    {
                        MessageTray messageTray = new MessageTray(canvasProperties.getMessagePaneProperties())
                        {

                            @Override
                            void close()
                            {
                                if (trayPane != null && !trayPane.isDisposed())
                                {
                                    trayPane.closeTray();
                                }

                            }

                        };
                        messageTray.setMessages(msgs);

                        TrayLocation location = TrayLocation.RIGHT;

                        switch (canvasProperties.getMessagePaneProperties().getPosition())
                        {
                            case BOTTOM:
                                location = TrayLocation.BOTTOM;
                                break;
                            case LEFT:
                                location = TrayLocation.LEFT;
                                break;
                            case RIGHT:
                                location = TrayLocation.RIGHT;
                                break;
                            case TOP:
                                location = TrayLocation.TOP;
                                break;

                            default:
                                break;
                        }

                        trayPane.openTray(location, messageTray, canvasProperties.getMessagePaneProperties().getSize());
                    }

                }

            }

            @Override
            public void add(EJInternalBlock block)
            {

            }

            @Override
            public EJCanvasType getType()
            {
                return EJCanvasType.SPLIT;
            }
        };
        _canvases.put(canvasProperties.getName(), canvasHandler);
        // if (!canvasProperties.getMessagePaneProperties().getCloseable())
        // {
        // canvasHandler.setCanvasMessages(Collections.<EJMessage> emptyList());
        // }
        layoutBody.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
        if (canvasProperties.getType() == EJCanvasType.SPLIT)
        {
            List<EJCanvasProperties> items = new ArrayList<EJCanvasProperties>(canvasProperties.getSplitCanvasContainer().getAllCanvasProperties());
            int[] weights = new int[items.size()];

            for (EJCanvasProperties containedCanvas : items)
            {
                if (containedCanvas.getType() == EJCanvasType.BLOCK && containedCanvas.getBlockProperties() != null && containedCanvas.getBlockProperties().getMainScreenProperties() != null)
                {
                    weights[items.indexOf(containedCanvas)] = canvasProperties.getSplitOrientation() == EJCanvasSplitOrientation.HORIZONTAL ? containedCanvas.getBlockProperties().getMainScreenProperties().getWidth() + 1 : containedCanvas.getBlockProperties().getMainScreenProperties().getHeight() + 1;
                    EJCoreMainScreenProperties mainScreenProperties = (EJCoreMainScreenProperties) containedCanvas.getBlockProperties().getMainScreenProperties();
                    mainScreenProperties.setExpandHorizontally(true);
                    mainScreenProperties.setExpandVertically(true);
                }
                else
                {
                    weights[items.indexOf(containedCanvas)] = canvasProperties.getSplitOrientation() == EJCanvasSplitOrientation.HORIZONTAL ? containedCanvas.getWidth() + 1 : containedCanvas.getHeight() + 1;

                    EJCoreCanvasProperties coreCanvasProperties = (EJCoreCanvasProperties) containedCanvas;
                    coreCanvasProperties.setExpandHorizontally(true);
                    coreCanvasProperties.setExpandVertically(true);
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

                    case SEPARATOR:
                        createSeparatorCanvas(layoutBody, containedCanvas);
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
                    case DRAWER:
                        createDrawerCanvas(layoutBody, containedCanvas, canvasController);
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

    public final class PopupCanvasHandler implements CanvasHandler
    {
        public EJRWTAbstractDialog _popupDialog;
        final int                  ID_BUTTON_1         = 1;
        final int                  ID_BUTTON_2         = 2;
        final int                  ID_BUTTON_3         = 3;
        final EJCanvasProperties   canvasProperties;
        final EJCanvasController   canvasController;

        boolean                    popupButton1        = true;
        boolean                    popupButton2        = true;
        boolean                    popupButton3        = true;
        boolean                    popupButtonVisible1 = true;
        boolean                    popupButtonVisible2 = true;
        boolean                    popupButtonVisible3 = true;
        String                     button1Label;
        String                     button2Label;
        String                     button3Label;
        String                     status;

        Collection<EJMessage>      msgs;
        private int                customWidth         = -1;
        private int                customHeight        = -1;

        public PopupCanvasHandler(EJCanvasProperties canvasProperties, EJCanvasController canvasController)
        {
            this.canvasController = canvasController;
            this.canvasProperties = canvasProperties;
            button1Label = canvasProperties.getButtonOneText();
            button2Label = canvasProperties.getButtonTwoText();
            button3Label = canvasProperties.getButtonThreeText();
            open(false);
        }

        public void setStatusBarMessage(String status)
        {
            this.status = status;
            if (_popupDialog != null)
                _popupDialog.setStatus(status);

        }

        @Override
        public void add(EJInternalBlock block)
        {
            // ignore
        }

        @Override
        public void setTrayContent(String id)
        {
            if (id == null && _popupDialog != null && _popupDialog.getTray() != null)
            {
                _popupDialog.closeTray();
                return;
            }

            if (id != null)
            {
                createFormTray(_form, canvasProperties, _popupDialog, id);

            }

        }

        @Override
        public void setCanvasMessages(Collection<EJMessage> messages)
        {
            this.msgs = messages;
            if (_popupDialog != null && !_popupDialog.getShell().isDisposed() && _popupDialog.getShell().isVisible())
            {

                if (_popupDialog.getTray() != null)
                {
                    _popupDialog.closeTray();
                }

                {
                    MessageTray messageTray = new MessageTray(canvasProperties.getMessagePaneProperties())
                    {

                        @Override
                        void close()
                        {
                            if (_popupDialog != null && !_popupDialog.getShell().isDisposed())
                            {
                                _popupDialog.closeTray();
                            }

                        }

                    };
                    messageTray.setMessages(msgs);

                    TrayLocation location = TrayLocation.RIGHT;

                    switch (canvasProperties.getMessagePaneProperties().getPosition())
                    {
                        case BOTTOM:
                            location = TrayLocation.BOTTOM;
                            break;
                        case LEFT:
                            location = TrayLocation.LEFT;
                            break;
                        case RIGHT:
                            location = TrayLocation.RIGHT;
                            break;
                        case TOP:
                            location = TrayLocation.TOP;
                            break;

                        default:
                            break;
                    }

                    _popupDialog.openTray(location, messageTray, canvasProperties.getMessagePaneProperties().getSize());
                }

            }

        }

        @Override
        public void clearCanvasMessages()
        {
            this.msgs = null;
            if (_popupDialog != null && !_popupDialog.getShell().isDisposed())
            {
                _popupDialog.closeTray();
            }
        }

        void open(boolean show)
        {
            final String name = canvasProperties.getName();
            final String pageTitle = canvasProperties.getPopupPageTitle();
            final int width = customWidth != -1 ? customWidth : canvasProperties.getWidth();
            final int height = customHeight != -1 ? customHeight : canvasProperties.getHeight();
            final int numCols = canvasProperties.getNumCols();
            final EJRWTApplicationManager applicationManager = (EJRWTApplicationManager) _form.getFrameworkManager().getApplicationManager();

            if (_popupDialog == null || _popupDialog.getShell() == null || _popupDialog.getShell().isDisposed())
            {

                _popupDialog = new EJRWTAbstractDialog(getRWTManager().getShell())
                {
                    private static final long serialVersionUID = -4685316941898120169L;

                    protected boolean isHelpActive()
                    {
                        return applicationManager.isHelpActive();
                    }

                    @Override
                    public boolean isHelpAvailable()
                    {
                        return applicationManager.isHelpSupported() && hasPopupButtons();// show
                                                                                         // only
                                                                                         // if
                                                                                         // other
                                                                                         // buttons
                                                                                         // added
                    }

                    @Override
                    protected void helpPressed(boolean active)
                    {
                        applicationManager.setHelpActive(active);
                    }

                    @Override
                    public void createBody(Composite parent)
                    {
                        parent.setLayout(new FillLayout());
                        final ScrolledComposite scrollComposite = new EJRWTScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);

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
                        ArrayList<UICallCache> uiactions = new ArrayList<>(_uicallCache);
                        uiactions.forEach(UICallCache::call);

                        _messageCache.entrySet().forEach(e -> {

                            CanvasHandler canvasHandler = _canvases.get(e.getKey());
                            if (canvasHandler != null)
                            {
                                canvasHandler.setCanvasMessages(e.getValue());
                            }
                        });

                        _uicallCache.removeAll(uiactions);

                    }

                    @Override
                    public int open()
                    {
                        return super.open();
                    }

                    @Override
                    public void canceled()
                    {
                        canvasController.closePopupCanvas(name, EJPopupButton.UNDEFINED);

                    }

                    @Override
                    protected void createButtonsForButtonBar(Composite parent)
                    {
                        // Add the buttons in reverse order, as they will be
                        // added
                        // from left to right
                        addExtraButton(parent, button3Label, ID_BUTTON_3, canvasProperties.getDefaultPopupButton() == EJPopupButton.THREE);
                        addExtraButton(parent, button2Label, ID_BUTTON_2, canvasProperties.getDefaultPopupButton() == EJPopupButton.TWO);
                        addExtraButton(parent, button1Label, ID_BUTTON_1, canvasProperties.getDefaultPopupButton() == EJPopupButton.ONE);

                        setButtonEnable(ID_BUTTON_1, popupButton1);
                        setButtonEnable(ID_BUTTON_2, popupButton2);
                        setButtonEnable(ID_BUTTON_3, popupButton3);
                        setButtonVisible(ID_BUTTON_1, popupButtonVisible1);
                        setButtonVisible(ID_BUTTON_2, popupButtonVisible2);
                        setButtonVisible(ID_BUTTON_3, popupButtonVisible3);

                    }

                    boolean hasPopupButtons()
                    {
                        return canAddButton(button1Label) || canAddButton(button2Label) || canAddButton(button3Label);
                    }

                    boolean canAddButton(String label)
                    {
                        if (label == null || label.length() == 0)
                        {
                            return false;
                        }

                        return true;
                    }

                    private void addExtraButton(Composite parent, String label, int id, boolean deafultButton)
                    {
                        if (!canAddButton(label))
                        {
                            return;
                        }
                        Button button = createButton(parent, id, label, deafultButton, true);
                        EJ_RWT.setTestId(button, "btn-" + id);

                    }

                    @Override
                    public boolean close()
                    {
                        msgs = null;
                        if (getTray() != null)
                        {
                            closeTray();
                        }
                        EJRWTDialogContext.get().close(this);
                        getShell().setVisible(false);
                        return true;
                    }

                    @Override
                    protected boolean isStatusMessageSupported()
                    {
                        return true;
                    }

                    @Override
                    protected void buttonPressed(int buttonId)
                    {
                        switch (buttonId)
                        {

                            case ID_BUTTON_1:
                            {
                                msgs = null;
                                Display.getDefault().asyncExec(() -> canvasController.closePopupCanvas(name, EJPopupButton.ONE));

                                break;
                            }
                            case ID_BUTTON_2:
                            {
                                msgs = null;
                                Display.getDefault().asyncExec(() -> canvasController.closePopupCanvas(name, EJPopupButton.TWO));
                                break;
                            }
                            case ID_BUTTON_3:
                            {
                                msgs = null;
                                Display.getDefault().asyncExec(() -> canvasController.closePopupCanvas(name, EJPopupButton.THREE));

                                break;
                            }

                            default:
                                super.buttonPressed(buttonId);
                                break;
                        }

                    }
                };
                _popupDialog.create();
                if (status != null)
                    _popupDialog.setStatus(status);
            }

            if (show)
            {
                _popupDialog.getShell().setData("POPUP - " + name);
                _popupDialog.getShell().setText(pageTitle != null ? pageTitle : "");
                // add dialog border offsets
                _popupDialog.getShell().setSize(width + 80, height + 100);
                _popupDialog.centreLocation();
                _popupDialog.open();
                _popupDialog.activateDialog();
                // if
                // (!canvasProperties.getMessagePaneProperties().getCloseable()
                // && msgs == null)
                // {
                // // msgs = Collections.emptyList();
                // }
                if ((msgs != null && msgs.size() > 0) || !canvasProperties.getMessagePaneProperties().getCloseable())
                {
                    if (_popupDialog.getTray() == null)
                    {
                        MessageTray messageTray = new MessageTray(canvasProperties.getMessagePaneProperties())
                        {

                            @Override
                            void close()
                            {
                                if (_popupDialog != null && !_popupDialog.getShell().isDisposed())
                                {
                                    _popupDialog.closeTray();
                                }

                            }

                        };
                        messageTray.setMessages(msgs);
                        TrayLocation location = TrayLocation.RIGHT;

                        switch (canvasProperties.getMessagePaneProperties().getPosition())
                        {
                            case BOTTOM:
                                location = TrayLocation.BOTTOM;
                                break;
                            case LEFT:
                                location = TrayLocation.LEFT;
                                break;
                            case RIGHT:
                                location = TrayLocation.RIGHT;
                                break;
                            case TOP:
                                location = TrayLocation.TOP;
                                break;

                            default:
                                break;
                        }

                        _popupDialog.openTray(location, messageTray, canvasProperties.getMessagePaneProperties().getSize());
                    }
                    else
                    {
                        MessageTray tray = (MessageTray) _popupDialog.getTray();
                        tray.setMessages(msgs);
                    }
                }
            }
        }

        void close()
        {
            msgs = null;
            if (_popupDialog != null && _popupDialog.getShell() != null && _popupDialog.getShell().isVisible())
            {
                _popupDialog.close();
                _popupDialog.getShell().dispose();
                _popupDialog = null;
            }
        }

        @Override
        public EJCanvasType getType()
        {
            return EJCanvasType.POPUP;
        }

        public void enableButton(EJPopupButton button, boolean state)
        {
            switch (button)
            {
                case ONE:
                    popupButton1 = state;
                    if (_popupDialog != null)
                        _popupDialog.setButtonEnable(ID_BUTTON_1, popupButton1);

                    break;
                case TWO:
                    popupButton2 = state;
                    if (_popupDialog != null)
                        _popupDialog.setButtonEnable(ID_BUTTON_2, popupButton2);
                    break;
                case THREE:
                    popupButton3 = state;
                    if (_popupDialog != null)
                        _popupDialog.setButtonEnable(ID_BUTTON_3, popupButton3);
                    break;

                default:
                    break;
            }

        }

        public void setButtonVisible(EJPopupButton button, boolean state)
        {
            switch (button)
            {
                case ONE:
                    popupButtonVisible1 = state;
                    if (_popupDialog != null)
                        _popupDialog.setButtonVisible(ID_BUTTON_1, popupButtonVisible1);

                    break;
                case TWO:
                    popupButtonVisible2 = state;
                    if (_popupDialog != null)
                        _popupDialog.setButtonVisible(ID_BUTTON_2, popupButtonVisible2);
                    break;
                case THREE:
                    popupButtonVisible3 = state;
                    if (_popupDialog != null)
                        _popupDialog.setButtonVisible(ID_BUTTON_3, popupButtonVisible3);
                    break;

                default:
                    break;
            }

        }

        public void setButtonLabel(EJPopupButton button, String label)
        {
            switch (button)
            {
                case ONE:
                    button1Label = label;
                    if (_popupDialog != null)
                        _popupDialog.setButtonLabel(ID_BUTTON_1, button1Label);

                    break;
                case TWO:
                    button2Label = label;
                    if (_popupDialog != null)
                        _popupDialog.setButtonLabel(ID_BUTTON_2, button2Label);
                    break;
                case THREE:
                    button3Label = label;
                    if (_popupDialog != null)
                        _popupDialog.setButtonLabel(ID_BUTTON_3, button3Label);
                    break;

                default:
                    break;
            }

        }

        public boolean isButtonEnabled(EJPopupButton button)
        {
            switch (button)
            {
                case ONE:
                    return popupButton1;
                case TWO:
                    return popupButton2;
                case THREE:
                    return popupButton3;

                default:
                    break;
            }
            return false;
        }

        public String getButtonLabel(EJPopupButton button)
        {
            switch (button)
            {
                case ONE:
                    return button1Label;
                case TWO:
                    return button2Label;
                case THREE:
                    return button3Label;

                default:
                    break;
            }
            return null;
        }

        public boolean isButtonVisible(EJPopupButton button)
        {
            switch (button)
            {
                case ONE:
                    return popupButtonVisible1;
                case TWO:
                    return popupButtonVisible2;
                case THREE:
                    return popupButtonVisible3;

                default:
                    break;
            }
            return false;
        }

        public void setCanvasSize(int width, int height)
        {
            customWidth = width;
            customHeight = height;
            if (_popupDialog != null && _popupDialog.getShell() != null && !_popupDialog.getShell().isDisposed())
            {
                _popupDialog.getShell().setSize(width + 80, height + 100);
                _popupDialog.centreLocation();
            }

        }
    }

    public interface CanvasHandler
    {
        EJCanvasType getType();

        void setCanvasMessages(Collection<EJMessage> messages);

        void setTrayContent(String id);

        void clearCanvasMessages();

        void add(EJInternalBlock block);
    }

    @Override
    public String getDisplayedStackedPage(String key)
    {
        EJRWTEntireJStackedPane stackedPane = _stackedPanes.get(key);
        if (stackedPane != null && !stackedPane.isDisposed())
        {
            return stackedPane.getActiveControlKey();
        }
        else
        {
            if (_stackedPanesCache.containsKey(key))
            {
                return _stackedPanesCache.get(key);
            }
            // use the prop
            EJCanvasProperties properties = EJRWTCanvasRetriever.getCanvas(_form.getProperties(), key);
            if (properties != null && properties.getType() == EJCanvasType.STACKED)
            {
                if (properties.getInitialStackedPageName() != null && !properties.getInitialStackedPageName().isEmpty())
                {
                    return properties.getInitialStackedPageName();
                }
                Collection<EJStackedPageProperties> allTabPageProperties = properties.getStackedPageContainer().getAllStackedPageProperties();
                for (EJStackedPageProperties ejTabPageProperties : allTabPageProperties)
                {
                    return ejTabPageProperties.getName();
                }
            }
        }

        return null;
    }

    @Override
    public String getDisplayedTabPage(String key)
    {
        ITabFolder tabFolder = _tabFolders.get(key);
        if (tabFolder != null && tabFolder.getFolder()!=null && !tabFolder.getFolder().isDisposed())
        {
            return tabFolder.getActiveKey();
        }
        else
        {
            if (_tabFoldersCache.containsKey(key))
            {
                return _tabFoldersCache.get(key);
            }
            // use the prop
            EJCanvasProperties properties = EJRWTCanvasRetriever.getCanvas(_form.getProperties(), key);
            if (properties != null && properties.getType() == EJCanvasType.TAB)
            {
                Collection<EJTabPageProperties> allTabPageProperties = properties.getTabPageContainer().getAllTabPageProperties();
                for (EJTabPageProperties ejTabPageProperties : allTabPageProperties)
                {
                    return ejTabPageProperties.getName();
                }
            }
        }
        return null;
    }

    @Override
    public String getDisplayedDrawerPage(String key)
    {
        EJDrawerFolder tabFolder = _drawerFolders.get(key);
        if (tabFolder != null && tabFolder.getFolder()!=null && !tabFolder.getFolder().isDisposed())
        {
            return tabFolder.getActiveKey();
        }
        else
        {
            if (_drawerFoldersCache.containsKey(key))
            {
                return _drawerFoldersCache.get(key);
            }
            // use the prop
            EJCanvasProperties properties = EJRWTCanvasRetriever.getCanvas(_form.getProperties(), key);
            if (properties != null && properties.getType() == EJCanvasType.DRAWER)
            {
                Collection<EJTabPageProperties> allTabPageProperties = properties.getTabPageContainer().getAllTabPageProperties();
                for (EJTabPageProperties ejTabPageProperties : allTabPageProperties)
                {
                    return ejTabPageProperties.getName();
                }
            }
        }
        return null;
    }

    @Override
    public void clearCanvasMessages(String canvasName)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler != null)
        {
            canvasHandler.clearCanvasMessages();
        }
        else
        {
            _messageCache.remove(canvasName);
        }

    }

    @Override
    public void setCanvasMessages(String canvasName, Collection<EJMessage> messages)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler != null)
        {
            canvasHandler.setCanvasMessages(messages);
        }
        else
            _messageCache.put(canvasName, messages);

    }

    @Override
    public void setTrayContent(String canvasName, String id)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler != null)
        {
            canvasHandler.setTrayContent(id);
        }
        else
            _uicallCache.add(() -> setTrayContent(canvasName, id));
    }

    @Override
    public void setButtonEnabled(String canvasName, EJPopupButton button, boolean state)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
            popupCanvasHandler.enableButton(button, state);
        }
        else
            _uicallCache.add(() -> setButtonEnabled(canvasName, button, state));

    }

    @Override
    public boolean isButtonEnabled(String canvasName, EJPopupButton button)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
            return popupCanvasHandler.isButtonEnabled(button);
        }
        return false;
    }

    @Override
    public void setButtonVisible(String canvasName, EJPopupButton button, boolean state)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
            popupCanvasHandler.setButtonVisible(button, state);
        }
        else
            _uicallCache.add(() -> setButtonVisible(canvasName, button, state));

    }

    @Override
    public boolean isButtonVisible(String canvasName, EJPopupButton button)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
            return popupCanvasHandler.isButtonVisible(button);
        }
        return false;
    }

    @Override
    public void setButtonLabel(String canvasName, EJPopupButton button, String label)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
            popupCanvasHandler.setButtonLabel(button, label);
        }
        else
        {
            _uicallCache.add(() -> setButtonLabel(canvasName, button, label));
        }
    }

    public void setCanvasSize(String canvasName, int width, int height)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
            popupCanvasHandler.setCanvasSize(width, height);
        }
        else
        {
            _uicallCache.add(() -> setCanvasSize(canvasName, width, height));
        }
    }

    @Override
    public String getButtonLabel(String canvasName, EJPopupButton button)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
            return popupCanvasHandler.getButtonLabel(button);
        }

        return null;
    }

    public static class FormCanvasTray extends EJRWTDialogTray
    {

        EJInternalForm               parentForm;
        private final String         formId;
        private EJRWTEntireJGridPane composite;

        public FormCanvasTray(EJInternalForm parentForm, String formId)
        {
            this.parentForm = parentForm;
            this.formId = formId;
        }

        @Override
        protected Control createContents(Composite parent)
        {
            if (composite == null || composite.isDisposed())
            {

                composite = new EJRWTEntireJGridPane(parent, 1);
                composite.cleanLayout();

                // load given form

                EJEmbeddedFormController formController = new EJEmbeddedFormController(parentForm.getFrameworkManager(), parentForm.getFormController(), formId, null, null);
                EJRWTFormRenderer renderer = (EJRWTFormRenderer) formController.getEmbeddedForm().getRenderer();
                final ScrolledComposite scrollComposite = new EJRWTScrolledComposite(composite, SWT.V_SCROLL | SWT.H_SCROLL);
                renderer.init();
                renderer.create(scrollComposite);
                EJRWTEntireJGridPane entireJGridPane = renderer.getGuiComponent();
                entireJGridPane.cleanLayout();
                scrollComposite.setContent(entireJGridPane);
                scrollComposite.setExpandHorizontal(true);
                scrollComposite.setExpandVertical(true);
                scrollComposite.setMinSize(formController.getEmbeddedForm().getProperties().getFormWidth(), formController.getEmbeddedForm().getProperties().getFormHeight());
                scrollComposite.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL));
                composite.layout(true);
                composite.redraw();
                composite.addDisposeListener(e -> formController.getEmbeddedForm().close());
            }

            return composite;
        }

    }

    public abstract class MessageTray extends EJRWTDialogTray
    {

        private Composite       parent;
        EJRWTEntireJGridPane    composite;
        ScrolledComposite       scrollComposite;
        Collection<EJMessage>   msgs;

        EJRWTEntireJGridPane    shell;
        EJMessagePaneProperties properties;

        public MessageTray(EJMessagePaneProperties properties)
        {
            this.properties = properties;
        }

        @Override
        protected Control createContents(Composite parent)
        {
            this.parent = parent;

            parent.addControlListener(new ControlListener()
            {

                @Override
                public void controlResized(ControlEvent e)
                {
                    calculateSize();
                }

                @Override
                public void controlMoved(ControlEvent e)
                {
                    // TODO Auto-generated method stub

                }
            });

            setMessages(msgs);
            return composite;
        }

        abstract void close();

        void setMessages(Collection<EJMessage> msgs)

        {
            this.msgs = msgs;
            if (shell != null && !shell.isDisposed())
            {

                // shell.setParent(null);
                shell.dispose();
            }

            if (parent != null && !parent.isDisposed())
            {

                if (composite == null || composite.isDisposed())
                {

                    composite = new EJRWTEntireJGridPane(parent, 1);

                    scrollComposite = new EJRWTScrolledComposite(composite, SWT.V_SCROLL);

                    GridData layoutData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL);
                    scrollComposite.setLayoutData(layoutData);
                    composite.addControlListener(new ControlListener()
                    {

                        @Override
                        public void controlResized(ControlEvent e)
                        {
                            calculateSize();
                        }

                        @Override
                        public void controlMoved(ControlEvent e)
                        {
                            // TODO Auto-generated method stub

                        }
                    });
                    composite.cleanLayout();
                }

                shell = new EJRWTEntireJGridPane(scrollComposite, 2);
                if (properties.getVa() != null)
                {
                    EJCoreVisualAttributeProperties visualAttribute = _form.getVisualAttribute(properties.getVa());
                    if (visualAttribute != null)
                    {
                        Color background = EJRWTVisualAttributeUtils.INSTANCE.getBackground(visualAttribute);
                        if (background != null)
                        {
                            shell.setBackground(background);
                            composite.setBackground(background);
                        }
                    }
                }

                shell.cleanLayoutVertical();

                // add close button
                if (properties.getCloseable())
                {

                    Label close = new Label(shell, SWT.None);
                    close.setImage(EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_CLOSE));
                    GridData data = new GridData(GridData.FILL_HORIZONTAL);
                    data.widthHint = 16;
                    data.heightHint = 16;
                    data.horizontalSpan = 2;
                    data.horizontalAlignment = SWT.RIGHT;
                    close.setLayoutData(data);
                    close.addMouseListener(new MouseAdapter()
                    {

                        @Override
                        public void mouseUp(MouseEvent e)
                        {
                            close();
                        }
                    });

                }

                if (msgs != null)
                {
                    for (EJMessage msg : msgs)
                    {
                        {// img

                            Label img = new Label(shell, SWT.None);

                            GridData data = new GridData();
                            data.widthHint = 16;
                            data.heightHint = 16;
                            data.verticalAlignment = SWT.TOP;
                            img.setLayoutData(data);

                            switch (msg.getLevel())
                            {
                                case HINT:
                                case MESSAGE:
                                    img.setImage(EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_INFO_16));
                                    break;
                                case ERROR:
                                    img.setImage(EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_ERROR_16));
                                    break;
                                case WARNING:
                                case DEBUG:
                                    img.setImage(EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_WARNING_16));
                                    break;

                                default:
                                    break;
                            }
                        }
                        {// text

                            Label text = new Label(shell, SWT.WRAP);
                            GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
                            text.setData(EJ_RWT.MARKUP_ENABLED, properties.getCustomFormatting());
                            text.setText(properties.getCustomFormatting() ? EJ_RWT.escapeHtmlWithXhtml(msg.getMessage()) : msg.getMessage());
                            text.setLayoutData(data);
                        }
                    }
                    composite.layout(true);
                }

                calculateSize();
                scrollComposite.setContent(shell);
            }
        }

        private void calculateSize()
        {
            if (shell != null && !shell.isDisposed() && !parent.isDisposed())
            {
                Point computeSize = shell.computeSize(composite.getBounds().width, SWT.DEFAULT);
                computeSize.x = computeSize.x - 5;
                if (properties.getPosition() == EJCanvasMessagePosition.LEFT || properties.getPosition() == EJCanvasMessagePosition.RIGHT)
                    computeSize.y = Math.max(computeSize.y - 20, Math.max(computeSize.y, parent.getBounds().height - 100));
                shell.setSize(computeSize);
            }
        }

        void clear()
        {
            msgs = null;

            if (shell != null && !shell.isDisposed())
            {

                // shell.setParent(null);
                shell.dispose();
            }
            close();
        }

        boolean hasMessages()
        {
            return msgs != null && msgs.size() > 0;
        }

    }

    @Override
    public void setTabPageBadge(String canvasName, String tabPageName, String badge)
    {
        if (canvasName != null && tabPageName != null)
        {
            ITabFolder tabPane = _tabFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.setTabPageBadge(tabPageName, badge);
            }
            else
                _uicallCache.add(() -> setTabPageBadge(canvasName, tabPageName, badge));
        }

    }

    @Override
    public void setDrawerPageBadge(String canvasName, String tabPageName, String badge)
    {
        if (canvasName != null && tabPageName != null)
        {
            EJDrawerFolder tabPane = _drawerFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.setDrawerPageBadge(tabPageName, badge);
            }
            else
                _uicallCache.add(() -> setDrawerPageBadge(canvasName, tabPageName, badge));
        }

    }

    @Override
    public void setDrawerPageBadgeVisualAttribute(String canvasName, String tabPageName, String visualAttributeName)
    {
        if (canvasName != null && tabPageName != null)
        {
            EJDrawerFolder tabPane = _drawerFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.setDrawerPageBadgeVa(tabPageName, visualAttributeName);
            }
            else
                _uicallCache.add(() -> setDrawerPageBadgeVisualAttribute(canvasName, tabPageName, visualAttributeName));

        }

    }

    public void setTabPageVisualAttribute(String canvasName, String tabPageName, String visualAttributeName)
    {
        if (canvasName != null && tabPageName != null)
        {
            ITabFolder tabPane = _tabFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.setTabPageVa(tabPageName, visualAttributeName);
            }
            else
                _uicallCache.add(() -> setTabPageVisualAttribute(canvasName, tabPageName, visualAttributeName));

        }
    }

    private static void createFormTray(EJInternalForm parentForm, EJCanvasProperties canvasProperties, final ITrayPane trayPane, String id)
    {
        FormCanvasTray formCanvasTray = new FormCanvasTray(parentForm, id);

        EJRWTDialogTray tray = trayPane.getTray();
        if (tray != null)
        {
            trayPane.closeTray();
        }
        trayPane.openTray(TrayLocation.RIGHT, formCanvasTray, 400);// TODO: add
                                                                   // size for
                                                                   // try and
                                                                   // location

    }

    public void closesDrawerPages()
    {

        _drawerFolders.values().forEach(d -> d.closeActivePage());
    }

}
