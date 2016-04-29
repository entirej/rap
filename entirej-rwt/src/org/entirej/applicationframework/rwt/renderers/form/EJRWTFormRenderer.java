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

import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTAbstractDialog;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTDialogTray;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTTrayDialog.TrayLocation;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTTrayPane;
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
import org.entirej.framework.core.properties.EJCoreCanvasProperties;
import org.entirej.framework.core.properties.EJCoreMainScreenProperties;
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
    private Map<String, String>                 _tabFoldersCache   = new HashMap<String, String>();
    private Map<String, String>                 _stackedPanesCache = new HashMap<String, String>();
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
    public void showStackedPage(final String canvasName,final  String pageName)
    {
        Display.getDefault().asyncExec(new Runnable()
        {
            
            @Override
            public void run()
            {
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
                
            }
        });
        
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
            else
            {
                _tabFoldersCache.put(canvasName, pageName);
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
        _mainPane.addDisposeListener(new DisposeListener()
        {
            
            @Override
            public void widgetDisposed(DisposeEvent event)
            {
                Collection<CanvasHandler> values = _canvases.values();
                for (CanvasHandler canvasHandler : values)
                {
                    if(canvasHandler instanceof PopupCanvasHandler)
                    {
                        PopupCanvasHandler handler = (PopupCanvasHandler) canvasHandler;
                        if(handler._popupDialog!=null && handler._popupDialog.getShell()!=null)
                        {
                            handler._popupDialog.getShell().dispose();
                        }
                    }
                }
                
            }
        });

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

    private void createStackedCanvas(Composite parent, final EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        final String name = canvasProperties.getName();

        final EJRWTTrayPane trayPane = new EJRWTTrayPane(parent);
        trayPane.setLayoutData(createCanvasGridData(canvasProperties));
        parent = trayPane;
        EJRWTEntireJStackedPane stackedPane = new EJRWTEntireJStackedPane(parent);
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
                        MessageTray messageTray = new MessageTray(canvasProperties.getCloseableMessagePane())
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

                        switch (canvasProperties.getMessagePosition())
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

                        trayPane.openTray(location, messageTray, canvasProperties.getMessagePaneSize());
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
        for (EJStackedPageProperties page : canvasProperties.getStackedPageContainer().getAllStackedPageProperties())
        {

            final ScrolledComposite scrollComposite = new ScrolledComposite(stackedPane, SWT.V_SCROLL | SWT.H_SCROLL);
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
        }

        if (canvasProperties.getInitialStackedPageName() != null)
        {
            stackedPane.showPane(canvasProperties.getInitialStackedPageName());
        }
        if(_stackedPanesCache.containsKey(name))
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
                        MessageTray messageTray = new MessageTray(canvasProperties.getCloseableMessagePane())
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

                        switch (canvasProperties.getMessagePosition())
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

                        trayPane.openTray(location, messageTray, canvasProperties.getMessagePaneSize());
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

        if (canvasProperties.getReferredFormId() != null && canvasProperties.getReferredFormId().length() > 0)
        {
            _form.openEmbeddedForm(canvasProperties.getReferredFormId(), name, null);
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
        trayPane.setLayoutData(createCanvasGridData(canvasProperties));
        parent = trayPane;
        final CTabFolder folder = new CTabFolder(parent, style);

        folder.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);
        EJTabFolder tabFolder = new EJTabFolder(folder, canvasController);
        trayPane.initBase(tabFolder.getFolder());
        folder.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                canvasController.tabPageChanged(name, (String) folder.getSelection().getData("TAB_KEY"));
            }
        });

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
                        MessageTray messageTray = new MessageTray(canvasProperties.getCloseableMessagePane())
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

                        switch (canvasProperties.getMessagePosition())
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

                        trayPane.openTray(location, messageTray, canvasProperties.getMessagePaneSize());
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

        _tabFolders.put(name, tabFolder);
        folder.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

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
        
        if(_tabFoldersCache.containsKey(name))
        {
            tabFolder.showPage(_tabFoldersCache.get(name));
            _tabFoldersCache.remove(name);
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
        final EJRWTEntireJGridPane groupPane = new EJRWTEntireJGridPane(parent, canvasProperties.getNumCols(),
                canvasProperties.getDisplayGroupFrame() ? SWT.BORDER : SWT.NONE);
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
                        MessageTray messageTray = new MessageTray(canvasProperties.getCloseableMessagePane())
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

                        switch (canvasProperties.getMessagePosition())
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

                        trayPane.openTray(location, messageTray, canvasProperties.getMessagePaneSize());
                    }

                }

            }

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
                        createSeparatorCanvas(parent, canvasProperties);
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

    private void createSplitCanvas(Composite parent, final EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {

        final EJRWTTrayPane trayPane = new EJRWTTrayPane(parent);
        trayPane.setLayoutData(createCanvasGridData(canvasProperties));
        parent = trayPane;
        SashForm layoutBody = new SashForm(parent, canvasProperties.getSplitOrientation() == EJCanvasSplitOrientation.HORIZONTAL ? SWT.HORIZONTAL
                : SWT.VERTICAL);
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
                        MessageTray messageTray = new MessageTray(canvasProperties.getCloseableMessagePane())
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

                        switch (canvasProperties.getMessagePosition())
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

                        trayPane.openTray(location, messageTray, canvasProperties.getMessagePaneSize());
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
                    weights[items.indexOf(containedCanvas)] = canvasProperties.getSplitOrientation() == EJCanvasSplitOrientation.HORIZONTAL ? containedCanvas
                            .getBlockProperties().getMainScreenProperties().getWidth() + 1 : containedCanvas.getBlockProperties().getMainScreenProperties()
                            .getHeight() + 1;
                    EJCoreMainScreenProperties mainScreenProperties = (EJCoreMainScreenProperties) containedCanvas.getBlockProperties()
                            .getMainScreenProperties();
                    mainScreenProperties.setExpandHorizontally(true);
                    mainScreenProperties.setExpandVertically(true);
                }
                else
                {
                    weights[items.indexOf(containedCanvas)] = canvasProperties.getSplitOrientation() == EJCanvasSplitOrientation.HORIZONTAL ? containedCanvas
                            .getWidth() + 1 : containedCanvas.getHeight() + 1;

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
                        createSeparatorCanvas(parent, canvasProperties);
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
        final int                ID_BUTTON_1         = 1;
        final int                ID_BUTTON_2         = 2;
        final int                ID_BUTTON_3         = 3;
        final EJCanvasProperties canvasProperties;
        final EJCanvasController canvasController;

        boolean                  popupButton1        = true;
        boolean                  popupButton2        = true;
        boolean                  popupButton3        = true;
        boolean                  popupButtonVisible1 = true;
        boolean                  popupButtonVisible2 = true;
        boolean                  popupButtonVisible3 = true;
        String                   button1Label;
        String                   button2Label;
        String                   button3Label;
        Collection<EJMessage>    msgs;

        public PopupCanvasHandler(EJCanvasProperties canvasProperties, EJCanvasController canvasController)
        {
            this.canvasController = canvasController;
            this.canvasProperties = canvasProperties;
            button1Label = canvasProperties.getButtonOneText();
            button2Label = canvasProperties.getButtonTwoText();
            button3Label = canvasProperties.getButtonThreeText();
            open(false);
        }

        @Override
        public void add(EJInternalBlock block)
        {
            // ignore
        }

        @Override
        public void setCanvasMessages(Collection<EJMessage> messages)
        {
            this.msgs = messages;
            if (_popupDialog != null && !_popupDialog.getShell().isDisposed())
            {

                if (_popupDialog.getTray() != null)
                {
                    _popupDialog.closeTray();
                }

                {
                    MessageTray messageTray = new MessageTray(canvasProperties.getCloseableMessagePane())
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

                    switch (canvasProperties.getMessagePosition())
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

                    _popupDialog.openTray(location, messageTray, canvasProperties.getMessagePaneSize());
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
            final int width = canvasProperties.getWidth();
            final int height = canvasProperties.getHeight();
            final int numCols = canvasProperties.getNumCols();

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

                    private void addExtraButton(Composite parent, String label, int id, boolean deafultButton)
                    {
                        if (label == null || label.length() == 0)
                        {
                            return;
                        }
                        createButton(parent, id, label, deafultButton);

                    }

                    @Override
                    public boolean close()
                    {
                        msgs = null;
                        if (getTray() != null)
                        {
                            closeTray();
                        }
                        getShell().setVisible(false);
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
                                canvasController.closePopupCanvas(name, EJPopupButton.ONE);
                                break;
                            }
                            case ID_BUTTON_2:
                            {
                                msgs = null;
                                canvasController.closePopupCanvas(name, EJPopupButton.TWO);
                                break;
                            }
                            case ID_BUTTON_3:
                            {
                                msgs = null;
                                canvasController.closePopupCanvas(name, EJPopupButton.THREE);

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
                _popupDialog.activateDialog();
                if (msgs != null && msgs.size() > 0)
                {
                    if (_popupDialog.getTray() == null)
                    {
                        MessageTray messageTray = new MessageTray(canvasProperties.getCloseableMessagePane())
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

                        switch (canvasProperties.getMessagePosition())
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

                        _popupDialog.openTray(location, messageTray, canvasProperties.getMessagePaneSize());
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

        public CTabFolder getFolder()
        {
            return folder;
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

        void setCanvasMessages(Collection<EJMessage> messages);

        void clearCanvasMessages();

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
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler != null)
        {
            canvasHandler.clearCanvasMessages();
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
    public void setButtonLabel(String canvasName, EJPopupButton button,String label)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
             popupCanvasHandler.setButtonLabel(button, label);
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
    
    

    public static abstract class MessageTray extends EJRWTDialogTray
    {

        private Composite     parent;
        EJRWTEntireJGridPane  composite;
        Collection<EJMessage> msgs;

        EJRWTEntireJGridPane  shell;
        boolean               canClose;

        public MessageTray(boolean canClose)
        {
            this.canClose = canClose;
        }

        @Override
        protected Control createContents(Composite parent)
        {
            this.parent = parent;

            setMessages(msgs);
            return composite;
        }

        abstract void close();

        void setMessages(Collection<EJMessage> msgs)

        {
            this.msgs = msgs;
            if (shell != null && !shell.isDisposed())
            {

                shell.setParent(null);
                shell.dispose();
            }

            if (parent != null && !parent.isDisposed())
            {

                if (composite == null || composite.isDisposed())
                {

                    composite = new EJRWTEntireJGridPane(parent, 1);
                    composite.cleanLayout();
                }

                final ScrolledComposite scrollComposite = new ScrolledComposite(composite, SWT.V_SCROLL);

                shell = new EJRWTEntireJGridPane(scrollComposite, 2);

                GridData layoutData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL);
                scrollComposite.setLayoutData(layoutData);
                shell.cleanLayoutVertical();

                // add close button
                if (canClose)
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
                            text.setData(EJ_RWT.MARKUP_ENABLED, true);
                            text.setText(msg.getMessage());
                            text.setLayoutData(data);
                        }
                    }
                    composite.layout(true);
                }

                composite.addControlListener(new ControlListener()
                {

                    @Override
                    public void controlResized(ControlEvent e)
                    {
                        Point computeSize = shell.computeSize(composite.getBounds().width, SWT.DEFAULT);
                        computeSize.x = computeSize.x - 5;
                        shell.setSize(computeSize);
                    }

                    @Override
                    public void controlMoved(ControlEvent e)
                    {
                        // TODO Auto-generated method stub

                    }
                });
                scrollComposite.setContent(shell);
            }
        }

        void clear()
        {
            msgs = null;

            if (shell != null && !shell.isDisposed())
            {

                shell.setParent(null);
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
    public String promptFileUpload(String title)
    {
        return EJRWTImageRetriever.getGraphicsProvider().promptFileUpload(title);
    }

    @Override
    public List<String> promptMultipleFileUpload(String title)
    {
        return EJRWTImageRetriever.getGraphicsProvider().promptMultipleFileUpload(title);
    }
    
    

}
