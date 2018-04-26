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
package org.entirej.applicationframework.rwt.application.launcher;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.rap.chartjs.AbstractChart;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.eclipse.rap.rwt.client.service.BrowserNavigationEvent;
import org.eclipse.rap.rwt.client.service.BrowserNavigationListener;
import org.eclipse.rap.rwt.client.service.ClientFileLoader;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IWidgetGraphicsAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.ToggleHyperlink;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationContainer;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.EJRWTGraphicsProvider;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.component.EJRWTTinymceEditor;
import org.entirej.applicationframework.rwt.file.EJRWTFileDownload;
import org.entirej.applicationframework.rwt.file.EJRWTFileUpload;
import org.entirej.applicationframework.rwt.file.EJRWTFileUpload.FileSelectionCallBack;
import org.entirej.applicationframework.rwt.renderers.html.EJRWTHtmlTableBlockRenderer.VACSSServiceHandler;
import org.entirej.framework.core.EJActionProcessorException;
import org.entirej.framework.core.EJFrameworkHelper;
import org.entirej.framework.core.EJFrameworkInitialiser;
import org.entirej.framework.core.data.controllers.EJFileUpload;
import org.entirej.framework.core.extensions.properties.EJCoreFrameworkExtensionPropertyList;
import org.entirej.framework.core.interfaces.EJMessenger;
import org.entirej.framework.core.properties.EJCoreLayoutContainer;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;

public abstract class EJRWTApplicationLauncher implements ApplicationConfiguration
{

    private static final String   THEME_DEFAULT_CSS = "theme/default.css";
    private static final String   ICONS_FAVICON_ICO = "icons/favicon.ico";
    protected static final String THEME_DEFAULT     = "org.entirej.applicationframework.rwt.Default";
    private String                _baseURL;

    public void configure(Application configuration)
    {

        createEntryPoint(configuration);
    }

    protected String getFavicon()
    {
        return ICONS_FAVICON_ICO;
    }
    
    protected boolean canLoadServices(){
        
       return true;
    }

    protected String getLoadingImage()
    {
        return "icons/ej-default_loading.gif";
    }

    protected String getLoadingMessage()
    {
        return "EJ Loading...";
    }

    /**
     * Changes the runtim mode of RAP @link
     * http://download.eclipse.org/rt/rap/doc
     * /2.1/guide/reference/api/org/eclipse
     * /rap/rwt/application/Application.OperationMode.html
     * 
     * OperationMode
     * 
     * @return OperationMode
     */
    protected OperationMode getOperationMode()
    {
        return OperationMode.SWT_COMPATIBILITY;
    }

    protected String getBaseThemeCSSLocation()
    {
        return THEME_DEFAULT_CSS;
    }

    protected String getThemeCSSLocation()
    {
        return null;
    }

    // disable due to RWT bug
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=410895
    // protected String getDefaultTabCloseMessage()
    // {
    // return "__DEFAULT__";
    // }

    protected String getWebPathContext()
    {
        return "ej";
    }

    protected String getTimeoutUrl()
    {

        return null;
    }

    protected String getBodyHtml()
    {
        StringBuilder b = new StringBuilder();
        b.append("<div id=\"splash\" style=\"width:100%;  position: absolute;  top: 50%;   text-align: center;\">");
        b.append("<img src=\"./rwt-resources/");
        b.append(getLoadingImage());
        b.append("\"  style=\"margin: 10px 15px 0\" />");
        b.append("<div style=\"margin: 5px 15px 10px;  font: 12px Verdana, 'Lucida Sans', sans-serif\">");
        b.append(getLoadingMessage());
        b.append("</div></div>");

        return b.toString();
    }

    protected void addOtherResources(final Application configuration)
    {
        // Add FORMS UI configs
        configuration.addThemeableWidget(Hyperlink.class);
        configuration.addThemeableWidget(FormText.class);
        configuration.addThemeableWidget(ToggleHyperlink.class);
        addResource(configuration, new org.eclipse.ui.forms.internal.widgets.togglehyperlinkkit.ToggleHyperlinkResource());
        addResource(configuration, new org.eclipse.ui.forms.internal.widgets.togglehyperlinkkit.ToggleHyperlinkAdapterResource());
        addResource(configuration, new org.eclipse.ui.forms.internal.widgets.hyperlinkkit.HyperlinkResource());
        addResource(configuration, new org.eclipse.ui.forms.internal.widgets.hyperlinkkit.HyperlinkAdapterResource());
        addResource(configuration, new org.eclipse.ui.forms.internal.widgets.formtextkit.FormTextResource());
        addResource(configuration, new org.eclipse.ui.forms.internal.widgets.formtextkit.FormTextAdapterResource());
    }

    void addResource(final Application configuration, final org.eclipse.rap.ui.resources.IResource resource)
    {

        configuration.addResource(resource.getLocation(), new ResourceLoader()
        {

            public InputStream getResourceAsStream(String arg0) throws IOException
            {
                return resource.getLoader().getResourceAsStream(arg0);
            }
        });

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
        final EJCoreProperties coreProperties = EJCoreProperties.getInstance();
        EJCoreLayoutContainer layoutContainer = coreProperties.getLayoutContainer();
        properties.put(WebClient.PAGE_TITLE, layoutContainer.getTitle());
        String favicon = getFavicon();
        if (favicon == null)
        {
            favicon = ICONS_FAVICON_ICO;
        }

        properties.put(WebClient.FAVICON, favicon);
        properties.put(WebClient.BODY_HTML, getBodyHtml());
        properties.put(WebClient.THEME_ID, THEME_DEFAULT);
        addOtherResources(configuration);
        configuration.addResource(favicon, new FileResource());
        configuration.addResource(getLoadingImage(), new FileResource());
        configuration.addStyleSheet(THEME_DEFAULT, "resource/theme/default.css");
        String baseThemeCSSLocation = getBaseThemeCSSLocation();
        if (baseThemeCSSLocation == null)
        {
            baseThemeCSSLocation = THEME_DEFAULT_CSS;
        }
        configuration.addStyleSheet(THEME_DEFAULT, baseThemeCSSLocation);
        properties.put(WebClient.PAGE_OVERFLOW, "scrollY");
        configuration.addResource(baseThemeCSSLocation, new FileResource());
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
                final EntryPoint wrapped = newEntryPoint();
                return new EntryPoint()
                {
                    public int createUI()
                    {
                        BrowserNavigation service = RWT.getClient().getService(BrowserNavigation.class);
                        BrowserNavigationListener listener = new BrowserNavigationListener()
                        {
                            @Override
                            public void navigated(BrowserNavigationEvent event)
                            {
                                EJRWTContext.getPageContext().setState(event.getState());
                            }
                        };

                        service.addBrowserNavigationListener(listener);

                        int createUI = wrapped.createUI();
                        return createUI;
                    }

                };
            }

           

            private EntryPoint newEntryPoint()
            {
                return new EntryPoint()
                {

                    public int createUI()
                    {

                        {// connect BaseURL
                            StringBuffer url = new StringBuffer();
                            url.append(RWT.getRequest().getContextPath());
                            url.append(RWT.getRequest().getServletPath());
                            String encodeURL = RWT.getResponse().encodeURL(url.toString());
                            if (encodeURL.contains("jsessionid"))
                            {
                                encodeURL = encodeURL.substring(0, encodeURL.indexOf("jsessionid"));
                            }
                            int patchIndex = encodeURL.lastIndexOf(getWebPathContext());
                            if (patchIndex > -1)
                            {
                                encodeURL = encodeURL.substring(0, patchIndex);
                            }
                            _baseURL = encodeURL;

                        }

                        RWTUtils.patchClient(getWebPathContext(), getTimeoutUrl());

                        EJRWTImageRetriever.setGraphicsProvider(new EJRWTGraphicsProvider()
                        {

                            @Override
                            public void promptFileUpload(final EJFileUpload fileUpload,final Callable<Object> callable)
                            {
                                if(fileUpload.isMultiSelection())
                                {
                                    EJRWTFileUpload.promptMultipleFileUpload(fileUpload.getTitle(),fileUpload.getUploadSizeLimit(),fileUpload.getUploadTimeLimit(),fileUpload.getFileExtensions().toArray(new String[0]), new FileSelectionCallBack()
                                    {
                                        
                                        @Override
                                        public void select(String[] files)
                                        {
                                            try
                                            {
                                                fileUpload.setFilePaths(files!=null ? Arrays.asList(files) :null);
                                                callable.call();
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }
                                            
                                        }
                                    });
                                }
                                else
                                {
                                    
                                    EJRWTFileUpload.promptFileUpload(fileUpload.getTitle(),fileUpload.getUploadSizeLimit(),fileUpload.getUploadTimeLimit(),fileUpload.getFileExtensions().toArray(new String[0]), new FileSelectionCallBack()
                                    {
                                        
                                        @Override
                                        public void select(String[] files)
                                        {
                                            try
                                            {
                                                fileUpload.setFilePaths(files!=null ? Arrays.asList(files) :null);
                                                callable.call();
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }
                                            
                                        }
                                    });
                                    
                                    
                                }
                                
                            }

                            public Image getImage(String name, ClassLoader loader)
                            {
                                return RWTUtils.getImage(name, loader);
                            }

                            @Override
                            public void open(final String output, String outputName)
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
                                        Color[] gradientColors = new Color[] { section.getTitleBarBorderColor(), section.getBackground(), section.getTitleBarBackground(), section.getBackground(), section.getBackground() };
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
                        });
                        final EJRWTApplicationManager applicationManager;

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

                        getContext().getUISession().setAttribute("ej.applicationManager", applicationManager);

                        EJCoreLayoutContainer layoutContainer = EJCoreProperties.getInstance().getLayoutContainer();
                        // Now build the application container
                        EJRWTApplicationContainer appContainer = new EJRWTApplicationContainer(layoutContainer);

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
                        Shell shell = new Shell(display, SWT.NO_TRIM);
                        
                        EJFrameworkExtensionProperties definedProperties = coreProperties.getApplicationDefinedProperties();
                        String keyMnemonic = definedProperties.getStringProperty("MNEMONIC");
                        if(keyMnemonic==null || keyMnemonic.isEmpty())
                            keyMnemonic = "ALT";
                        display.setData( RWT.MNEMONIC_ACTIVATOR, keyMnemonic);
                        //check test mode
                        
                        StartupParameters service = RWT.getClient().getService(StartupParameters.class);
                        if(service!= null && Boolean.valueOf(service.getParameter("TEST_MODE")))
                        {
                            EJ_RWT.setTestMode(true);
                        }
                        
                        try
                        {
                            preApplicationBuild(applicationManager);
                        }
                        finally
                        {
                            applicationManager.getConnection().close();
                        }
                        applicationManager.buildApplication(appContainer, shell);

                        final EJRWTApplicationManager appman = applicationManager;

                        Display.getCurrent().asyncExec(new Runnable()
                        {

                            @Override
                            public void run()
                            {
                                try
                                {

                                    postApplicationBuild(appman);
                                }
                                finally
                                {
                                    appman.getConnection().close();
                                }

                            }
                        });
                        shell.layout();
                        shell.setMaximized(true);
                        // disable due to RWT bug
                        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=410895
                        // ExitConfirmation confirmation =
                        // RWT.getClient().getService(ExitConfirmation.class);
                        // String message = getDefaultTabCloseMessage();
                        // if ("__DEFAULT__".equals(message))
                        // {
                        // confirmation.setMessage(String.format("Do you want to close %s ?",
                        // EJCoreProperties.getInstance().getLayoutContainer().getTitle()));
                        // }
                        // else if (message != null)
                        // {
                        // confirmation.setMessage(message);
                        // }

                        final ServerPushSession pushSession = new ServerPushSession();

                        RWT.getUISession().addUISessionListener(new UISessionListener()
                        {
                            public void beforeDestroy(UISessionEvent event)
                            {
                                if (applicationManager.getApplicationActionProcessor() != null)
                                    try
                                    {
                                        applicationManager.getApplicationActionProcessor().whenApplicationEnd(applicationManager);
                                    }
                                    catch (EJActionProcessorException e)
                                    {
                                        e.printStackTrace();
                                    }
                                pushSession.stop();
                            }
                        });

                        if (applicationManager.getApplicationActionProcessor() != null)
                            try
                            {
                                applicationManager.getApplicationActionProcessor().whenApplicationStart(applicationManager);
                            }
                            catch (EJActionProcessorException e)
                            {
                                e.printStackTrace();
                            }

                        
                        if(definedProperties!=null && definedProperties.getBooleanProperty("LIVE_CONNECTION", false))
                            pushSession.start();

                        return openShell(display, shell);
                    }
                };
            }
        }, properties);
        
        
        // services

        {
            final String SERVICE = "SERVICE";
            final String SERVICE_LIST = "SERVICE_LIST";
            final String SERVICE_PATH = "SERVICE_PATH";
            final String SERVICE_FORM = "SERVICE_FORM";

           final String SERVICE_NAME = "SERVICE_NAME";
            EJFrameworkExtensionProperties definedProperties = coreProperties.getApplicationDefinedProperties();
            if (canLoadServices()&& definedProperties != null)
            {
                EJFrameworkExtensionProperties group = definedProperties.getPropertyGroup(SERVICE);
                if (group != null && group.getPropertyList(SERVICE_LIST)!=null)
                {
                    EJCoreFrameworkExtensionPropertyList list = group.getPropertyList(SERVICE_LIST);
                    List<EJFrameworkExtensionPropertyListEntry> allListEntries = list.getAllListEntries();
                    for (EJFrameworkExtensionPropertyListEntry entry : allListEntries)
                    {

                        
                        final String formId = entry.getProperty(SERVICE_FORM);
                        HashMap<String, String>  srvproperties = new HashMap<String, String>(properties);
                         srvproperties.put(WebClient.PAGE_TITLE, entry.getProperty(SERVICE_NAME));
                        if(entry.getProperty(SERVICE_PATH)!=null && formId!=null && formId!=null)
                        {
                            configuration.addEntryPoint(String.format("/%s", entry.getProperty(SERVICE_PATH)), new EntryPointFactory()
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
                                    final EntryPoint wrapped = newServiceEntryPoint(formId);
                                    return new EntryPoint()
                                    {
                                        public int createUI()
                                        {
                                            BrowserNavigation service = RWT.getClient().getService(BrowserNavigation.class);
                                            BrowserNavigationListener listener = new BrowserNavigationListener()
                                            {
                                                @Override
                                                public void navigated(BrowserNavigationEvent event)
                                                {
                                                    EJRWTContext.getPageContext().setState(event.getState());
                                                }
                                            };

                                            service.addBrowserNavigationListener(listener);

                                            int createUI = wrapped.createUI();
                                            return createUI;
                                        }

                                    };
                                }

                               

                                private EntryPoint newServiceEntryPoint(String serviceFormID)
                                {
                                    return new EntryPoint()
                                    {

                                        public int createUI()
                                        {

                                            

                                            RWTUtils.patchClient(getWebPathContext(), null);

                                            EJRWTImageRetriever.setGraphicsProvider(new EJRWTGraphicsProvider()
                                            {

                                               

                                                @Override
                                                public void promptFileUpload(final EJFileUpload fileUpload,final Callable<Object> callable)
                                                {
                                                    if(fileUpload.isMultiSelection())
                                                    {
                                                        EJRWTFileUpload.promptMultipleFileUpload(fileUpload.getTitle(),fileUpload.getUploadSizeLimit(),fileUpload.getUploadTimeLimit(),fileUpload.getFileExtensions().toArray(new String[0]), new FileSelectionCallBack()
                                                        {
                                                            
                                                            @Override
                                                            public void select(String[] files)
                                                            {
                                                                try
                                                                {
                                                                    fileUpload.setFilePaths(files!=null ? Arrays.asList(files) :null);
                                                                    callable.call();
                                                                }
                                                                catch (Exception e)
                                                                {
                                                                    e.printStackTrace();
                                                                }
                                                                
                                                            }
                                                        });
                                                    }
                                                    else
                                                    {
                                                        
                                                        EJRWTFileUpload.promptFileUpload(fileUpload.getTitle(),fileUpload.getUploadSizeLimit(),fileUpload.getUploadTimeLimit(),fileUpload.getFileExtensions().toArray(new String[0]), new FileSelectionCallBack()
                                                        {
                                                            
                                                            @Override
                                                            public void select(String[] files)
                                                            {
                                                                try
                                                                {
                                                                    fileUpload.setFilePaths(files!=null ? Arrays.asList(files) :null);
                                                                    callable.call();
                                                                }
                                                                catch (Exception e)
                                                                {
                                                                    e.printStackTrace();
                                                                }
                                                                
                                                            }
                                                        });
                                                        
                                                        
                                                    }
                                                    
                                                }
                                                
                                                public Image getImage(String name, ClassLoader loader)
                                                {
                                                    return RWTUtils.getImage(name, loader);
                                                }

                                                @Override
                                                public void open(final String output, String outputName)
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
                                                            Color[] gradientColors = new Color[] { section.getTitleBarBorderColor(), section.getBackground(), section.getTitleBarBackground(), section.getBackground(), section.getBackground() };
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
                                            });
                                            final EJRWTApplicationManager applicationManager;

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

                                            getContext().getUISession().setAttribute("ej.applicationManager", applicationManager);

                                            EJCoreLayoutContainer layoutContainer = EJCoreProperties.getInstance().getLayoutContainer();
                                            // Now build the application container
                                            EJRWTApplicationContainer appContainer = new EJRWTApplicationContainer(layoutContainer);

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
                                            Shell shell = new Shell(display, SWT.NO_TRIM);
                                           
                                            applicationManager.buildServiceApplication(appContainer, shell,formId);

                                            final EJRWTApplicationManager appman = applicationManager;

                                            Display.getCurrent().asyncExec(new Runnable()
                                            {

                                                @Override
                                                public void run()
                                                {
                                                    try
                                                    {

                                                        postApplicationBuild(appman);
                                                    }
                                                    finally
                                                    {
                                                        appman.getConnection().close();
                                                    }

                                                }
                                            });
                                            shell.layout();
                                            shell.setMaximized(true);
                                           

                                            final ServerPushSession pushSession = new ServerPushSession();

                                            RWT.getUISession().addUISessionListener(new UISessionListener()
                                            {
                                                public void beforeDestroy(UISessionEvent event)
                                                {
                                                    
                                                    pushSession.stop();
                                                }
                                            });

                                            

                                            pushSession.start();

                                            return openShell(display, shell);
                                        }
                                    };
                                }
                            }, srvproperties);
                        }
                    }
                }

            }

        }
        
    }

    @SuppressWarnings("deprecation")
    public int openShell(Display display, Shell shell)
    {
        shell.open();
        if (getOperationMode() == OperationMode.SWT_COMPATIBILITY)
        {
            while (!shell.isDisposed())
            {
                if (!display.readAndDispatch())
                {
                    display.sleep();
                }
            }
        }
        return 0;
    }

    public void preApplicationBuild(EJFrameworkHelper frameworkHelper)
    {
    };

    public void postApplicationBuild(EJFrameworkHelper frameworkHelper)
    {
    };

    public void registerServiceHandlers()
    {

    }

    public void registerWidgetHandlers()
    {
        // TODO remove after fixed
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=401126
        ClientFileLoader loader = RWT.getClient().getService(ClientFileLoader.class);
        loader.requireJs("rwt-resources/" + new org.eclipse.ui.forms.internal.widgets.togglehyperlinkkit.ToggleHyperlinkResource().getLocation());
        loader.requireJs("rwt-resources/" + new org.eclipse.ui.forms.internal.widgets.togglehyperlinkkit.ToggleHyperlinkAdapterResource().getLocation());
        loader.requireJs("rwt-resources/" + new org.eclipse.ui.forms.internal.widgets.hyperlinkkit.HyperlinkAdapterResource().getLocation());
        loader.requireJs("rwt-resources/" + new org.eclipse.ui.forms.internal.widgets.formtextkit.FormTextResource().getLocation());
        loader.requireJs("rwt-resources/" + new org.eclipse.ui.forms.internal.widgets.formtextkit.FormTextAdapterResource().getLocation());
        EJRWTTinymceEditor.initResources();
        AbstractChart.registerJS();
        AbstractChart.requireJS();
    }

    public static class FileResource implements ResourceLoader
    {

        public InputStream getResourceAsStream(String arg0) throws IOException
        {
            return getLoader().getResourceAsStream(arg0);
        }

        public ClassLoader getLoader()
        {
            return EJRWTApplicationLauncher.class.getClassLoader();
        }

    }

    public String getBaseURL()
    {
        return _baseURL;
    }

    public static void reloadApplication(EJFrameworkHelper frameworkHelper)
    {
        // disable due to RWT bug
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=410895
        // ExitConfirmation confirmation =
        // RWT.getClient().getService(ExitConfirmation.class);
        // confirmation.setMessage(null);
        StringBuffer url = new StringBuffer();
        url.append(RWT.getRequest().getContextPath());
        url.append(RWT.getRequest().getServletPath());
        String encodeURL = RWT.getResponse().encodeURL(url.toString());
        if (encodeURL.contains("jsessionid"))
        {
            encodeURL = encodeURL.substring(0, encodeURL.indexOf("jsessionid"));
        }
        String browserText = MessageFormat.format("parent.window.location.href = \"{0}\";", encodeURL);
        JavaScriptExecutor executor = RWT.getClient().getService(JavaScriptExecutor.class);
        if (executor != null)
        {
            executor.execute(browserText);
        }
    }
}
