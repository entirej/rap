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
package org.entirej.applicationframework.rwt.application.launcher;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getContext;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IWidgetGraphicsAdapter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationContainer;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.EJRWTGraphicsProvider;
import org.entirej.applicationframework.rwt.application.components.menu.EJRWTDefaultMenuBuilder;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTStackedPaneFormContainer;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormContainer;
import org.entirej.applicationframework.rwt.file.EJRWTFileDownload;
import org.entirej.applicationframework.rwt.file.EJRWTFileUpload;
import org.entirej.applicationframework.rwt.renderers.html.EJRWTHtmlTableBlockRenderer.VACSSServiceHandler;
import org.entirej.framework.core.EJFrameworkInitialiser;
import org.entirej.framework.core.interfaces.EJMessenger;
import org.entirej.framework.core.properties.EJCoreLayoutContainer;
import org.entirej.framework.core.properties.EJCoreProperties;

public abstract class EJRWTMobileApplicationLauncher extends EJRWTApplicationLauncher
{

    protected UI ui;
    protected static final String THEME_DEFAULT = "org.entirej.applicationframework.rwt.mobile.Default";

    @Override
    protected String getBaseThemeCSSLocation()
    {
        return "theme/default-mobile.css";
    }

    public void createEntryPoint(final Application configuration)
    {

        configuration.setOperationMode(getOperationMode());
        Map<String, String> properties = new HashMap<String, String>();
        if (this.getClass().getClassLoader().getResource("application.ejprop") != null)
        {
            EJFrameworkInitialiser.initialiseFramework("application.ejprop");
        }
        else if (this.getClass().getClassLoader().getResource("EntireJApplication.properties") != null)
        {

            EJFrameworkInitialiser.initialiseFramework("EntireJApplication.properties");
        }
        else
        {
            throw new RuntimeException("application.ejprop not found");
        }
        EJCoreLayoutContainer layoutContainer = EJCoreProperties.getInstance().getLayoutContainer();
        properties.put(WebClient.PAGE_TITLE, layoutContainer.getTitle());
        properties.put(WebClient.FAVICON, getFavicon());
        properties.put(WebClient.BODY_HTML, getBodyHtml());
        properties.put(WebClient.THEME_ID, THEME_DEFAULT);
        addOtherResources(configuration);
        configuration.addResource(getFavicon(), new FileResource());
        configuration.addResource(getLoadingImage(), new FileResource());

        configuration.addStyleSheet(THEME_DEFAULT, "resource/theme/default.css");
        configuration.addStyleSheet(THEME_DEFAULT, getBaseThemeCSSLocation());
        configuration.addResource(getBaseThemeCSSLocation(), new FileResource());

        configuration.addStyleSheet(THEME_DEFAULT, "org/entirej/rwt/mobile/mobile.css");
        configuration.addResource("org/entirej/rwt/mobile/mobile.css", new FileResource());

        if (getThemeCSSLocation() != null)
        {

            configuration.addStyleSheet(THEME_DEFAULT, getThemeCSSLocation());
            configuration.addResource(getThemeCSSLocation(), new FileResource());
        }
        configuration.addEntryPoint(String.format("/%s", getWebPathContext()), new EntryPointFactory()
        {

            public EntryPoint create()
            {
                try
                {

                    RWT.getServiceManager().registerServiceHandler(VACSSServiceHandler.SERVICE_HANDLER, new VACSSServiceHandler());
                    RWT.getServiceManager().registerServiceHandler(EJRWTFileDownload.SERVICE_HANDLER, EJRWTFileDownload.newServiceHandler());
                    registerServiceHandlers();
                }
                catch (java.lang.IllegalArgumentException e)
                {
                    // ignore if already registered
                }
                registerWidgetHandlers();
                return new EntryPoint()
                {

                    public int createUI()
                    {
                        EJRWTImageRetriever.setGraphicsProvider(new EJRWTGraphicsProvider()
                        {

                            @Override
                            public String promptFileUpload(String title)
                            {
                                return EJRWTFileUpload.promptFileUpload(title);
                            }
                            @Override
                            public List<String> promptMultipleFileUpload(String title)
                            
                            {
                                String[] promptMultipleFileUpload = EJRWTFileUpload.promptMultipleFileUpload(title);
                                return (List<String>) (promptMultipleFileUpload!=null ? Arrays.asList(promptMultipleFileUpload):Collections.emptyList());
                            }
                            
                            public Image getImage(String name, ClassLoader loader)
                            {
                                return RWTUtils.getImage(name, loader);
                            }

                            public float getAvgCharWidth(Font font)
                            {
                                return RWTUtils.getAvgCharWidth(font);
                            }

                            public int getCharHeight(Font font)
                            {
                                return RWTUtils.getCharHeight(font);
                            }

                            public void rendererSection(final Section section)
                            {
                                section.removeListener(SWT.Dispose, section.getListeners(SWT.Dispose)[0]);
                                section.removeListener(SWT.Resize, section.getListeners(SWT.Resize)[0]);
                                section.setFont(section.getParent().getFont());
                                section.setForeground(section.getParent().getForeground());
                                Object adapter = section.getAdapter(IWidgetGraphicsAdapter.class);
                                IWidgetGraphicsAdapter gfxAdapter = (IWidgetGraphicsAdapter) adapter;
                                gfxAdapter.setRoundedBorder(1, section.getTitleBarBackground(), 2, 2, 0, 0);

                                Listener listener = new Listener()
                                {
                                    public void handleEvent(Event e)
                                    {

                                        Object adapter = section.getAdapter(IWidgetGraphicsAdapter.class);
                                        IWidgetGraphicsAdapter gfxAdapter = (IWidgetGraphicsAdapter) adapter;
                                        Color[] gradientColors = new Color[] { section.getTitleBarBorderColor(), section.getBackground(),
                                                section.getTitleBarBackground(), section.getBackground(), section.getBackground() };
                                        int gradientPercent = 0;
                                        Rectangle bounds = section.getClientArea();

                                        if (bounds.height != 0)
                                        {
                                            gradientPercent = 30 * 100 / bounds.height;
                                            if (gradientPercent > 100)
                                            {
                                                gradientPercent = 100;
                                            }
                                        }
                                        int[] percents = new int[] { 0, 1, 2, gradientPercent, 100 };
                                        gfxAdapter.setBackgroundGradient(gradientColors, percents, true);
                                        gfxAdapter.setRoundedBorder(1, section.getBackground(), 4, 4, 0, 0);
                                    }
                                };
                                section.addListener(SWT.Dispose, listener);
                                section.addListener(SWT.Resize, listener);

                            }

                            @Override
                            public void open(final String output,String outputName)
                            {
                                EJRWTFileDownload.download(output, outputName);
                                RWT.getUISession().addUISessionListener(new UISessionListener()
                                {

                                  
                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public void beforeDestroy(UISessionEvent arg0)
                                    {
                                        File f = new File(output);
                                        if (f.exists())
                                        {
                                               
                                                f.delete();
                                        }

                                    }
                                });
                                
                            }
                        });
                        EJRWTApplicationManager applicationManager = null;

                        if (this.getClass().getClassLoader().getResource("application.ejprop") != null)
                        {
                            applicationManager = (EJRWTApplicationManager) EJFrameworkInitialiser.initialiseFramework("application.ejprop");
                        }
                        else if (this.getClass().getClassLoader().getResource("EntireJApplication.properties") != null)
                        {

                            applicationManager = (EJRWTApplicationManager) EJFrameworkInitialiser.initialiseFramework("EntireJApplication.properties");
                        }
                        else
                        {
                            throw new RuntimeException("application.ejprop not found");
                        }
                        EJRWTContext.getPageContext().setManager(applicationManager);
                        EJCoreLayoutContainer layoutContainer = EJCoreProperties.getInstance().getLayoutContainer();
                        // Now build the application container
                        EJRWTApplicationContainer appContainer = new EJRWTApplicationContainer(layoutContainer)
                        {
                            @Override
                            protected void buildApplicationContainer()
                            {
                                _mainPane.setLayout(new FillLayout());
                                _mainPane.setData(RWT.CUSTOM_VARIANT, null);
                                _formContainer = createFormContainer(_applicationManager, _mainPane);
                            }

                        };

                        // Add the application menu and status bar to the app
                        // container
                        EJMessenger messenger = applicationManager.getApplicationMessenger();
                        if (messenger == null)
                        {
                            throw new NullPointerException("The ApplicationComponentProvider must provide an Messenger via method: getApplicationMessenger()");
                        }
                        Display display = Display.getDefault();
                        if (display.isDisposed())
                            display = new Display();
                        final Shell shell = new Shell(display, SWT.NO_TRIM);
                        GridLayout gridLayout = new GridLayout(2, false);
                        gridLayout.marginHeight = 0;
                        gridLayout.marginWidth = 0;
                        gridLayout.horizontalSpacing = 1;
                        gridLayout.verticalSpacing = 0;
                        shell.setLayout(gridLayout);

                        final Composite nav = new Composite(shell, SWT.NONE);
                        nav.setLayout(new FillLayout());

                        final GridData navData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, true);
                        navData.widthHint = getSidebarWidth();
                        navData.heightHint = SWT.MAX;
                        // nav.setLayoutData(navData);
                        final GridData navEmptyData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
                        navEmptyData.minimumWidth = 0;
                        navEmptyData.widthHint = 0;
                        nav.setLayoutData(navEmptyData);
                        nav.setVisible(false);

                        Composite page = new Composite(shell, SWT.NONE);
                        GridLayout gridLayoutPage = new GridLayout(1, false);
                        gridLayoutPage.marginHeight = 0;
                        gridLayoutPage.marginWidth = 0;
                        gridLayoutPage.verticalSpacing = 1;
                        page.setLayout(gridLayoutPage);
                        GridData pageData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
                        page.setLayoutData(pageData);

                        Composite pageHeader = new Composite(page, SWT.NONE);
                        GridData pageHData = new GridData(GridData.FILL_HORIZONTAL);
                        pageHData.heightHint = 40;
                        pageHData.minimumHeight = 40;
                        pageHeader.setLayoutData(pageHData);
                        GridLayout gridLayoutHeader = new GridLayout(8, false);
                        pageHeader.setLayout(gridLayoutHeader);
                        gridLayoutHeader.marginHeight = 1;
                        gridLayoutHeader.marginWidth = 1;

                        Button pageB = new Button(pageHeader, SWT.PUSH);
                        pageB.setImage(EJRWTImageRetriever.get("icons/menu-32.png"));

                        pageB.addSelectionListener(new SelectionAdapter()
                        {
                            @Override
                            public void widgetSelected(SelectionEvent e)
                            {
                                nav.setVisible(!nav.isVisible());
                                nav.setLayoutData(nav.isVisible() ? navData : navEmptyData);
                                shell.layout();
                            }
                        });
                        
                        ui = new UI()
                        {
                            
                            @Override
                            public void showMenu(boolean show)
                            {
                                nav.setVisible(show);
                                nav.setLayoutData(show ? navData : navEmptyData);
                                shell.layout();
                            }
                        };
                        final GridData actioBData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, true);
                        actioBData.widthHint = 40;
                        actioBData.heightHint = 40;
                        pageB.setLayoutData(actioBData);

                        Text headerText = new Text(pageHeader, SWT.SINGLE | SWT.READ_ONLY | SWT.CENTER);

                        final GridData headerTextData = new GridData(SWT.FILL, SWT.FILL, true, true);
                        headerTextData.heightHint = 40;
                        headerTextData.grabExcessHorizontalSpace = true;
                        headerText.setLayoutData(headerTextData);
                        headerText.setText(layoutContainer.getTitle());
                        createToolBar(applicationManager, pageHeader);

                        Composite pageBody = new Composite(page, SWT.NONE);
                        GridData pageBData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
                        pageBody.setLayoutData(pageBData);
                        pageBody.setLayout(new FillLayout());

                        nav.setData(RWT.CUSTOM_VARIANT, "sidebar");
                        pageHeader.setData(RWT.CUSTOM_VARIANT, "drawerHeader");
                        // pageBody.setData(RWT.CUSTOM_VARIANT, "applayout");
                        pageB.setData(RWT.CUSTOM_VARIANT, "drawerAction");
                        headerText.setData(RWT.CUSTOM_VARIANT, "drawerTitle");
                        EJRWTContext.getPageContext().setManager(applicationManager);
                        getContext().getUISession().setAttribute("ej.MobileMode", true);
                        try
                        {
                            preApplicationBuild(applicationManager);
                        }
                        finally
                        {
                            applicationManager.getConnection().close();
                        }

                        createNavigator(applicationManager, nav);

                        createDrawerBody(applicationManager, appContainer, pageBody);
                        try
                        {
                            postApplicationBuild(applicationManager);
                        }
                        finally
                        {
                            applicationManager.getConnection().close();
                        }
                        shell.layout();
                        shell.setMaximized(true);

                        return openShell(display, shell);
                    }

                };
            }
        }, properties);
    }

    protected int getSidebarWidth()
    {
        return 250;
    }

    protected void createToolBar(EJRWTApplicationManager applicationManager, Composite parent)
    {

    }

    protected void createNavigator(EJRWTApplicationManager applicationManager, Composite parent)
    {
        // create menu component by default
        EJRWTDefaultMenuBuilder menuBuilder = new EJRWTDefaultMenuBuilder(applicationManager, parent);
        menuBuilder.createTreeComponent(getMenuID(), true).setData(RWT.CUSTOM_VARIANT, "sidebar");
    }

    private void createDrawerBody(EJRWTApplicationManager applicationManager, EJRWTApplicationContainer appContainer, Composite parent)
    {
        // create application layout by default
        applicationManager.buildApplication(appContainer, parent);
    }

    protected EJRWTFormContainer createFormContainer(EJRWTApplicationManager applicationManager, Composite parent)
    {

        EJRWTStackedPaneFormContainer formContainer = new EJRWTStackedPaneFormContainer()
        {
            @Override
            protected int getStyle()
            {
                return SWT.NONE;
            }

        };

        formContainer.createContainer(applicationManager, parent, null);
        return formContainer;
    }

    protected String getMenuID()
    {

        return "Default";
    }

    
    protected static interface UI{
        
        void showMenu(boolean show);
        
    }
}
