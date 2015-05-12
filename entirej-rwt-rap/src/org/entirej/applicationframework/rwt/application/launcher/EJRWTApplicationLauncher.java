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
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
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
import org.entirej.applicationframework.rwt.file.EJRWTFileDownload;
import org.entirej.applicationframework.rwt.renderers.html.EJRWTHtmlTableBlockRenderer.VACSSServiceHandler;
import org.entirej.framework.core.EJFrameworkHelper;
import org.entirej.framework.core.EJFrameworkInitialiser;
import org.entirej.framework.core.interfaces.EJMessenger;
import org.entirej.framework.core.properties.EJCoreLayoutContainer;
import org.entirej.framework.core.properties.EJCoreProperties;

public abstract class EJRWTApplicationLauncher implements ApplicationConfiguration
{

    private static final String   THEME_DEFAULT_CSS = "theme/default.css";
    private static final String   ICONS_FAVICON_ICO = "icons/favicon.ico";
    protected static final String THEME_DEFAULT     = "org.entirej.applicationframework.rwt.Default";

    public void configure(Application configuration)
    {
        createEntryPoint(configuration);
    }

    protected String getFavicon()
    {
        return ICONS_FAVICON_ICO;
    }

    protected String getLoadingImage()
    {
        return "icons/ej-default_loading.gif";
    }

    protected String getLoadingMessage()
    {
        return "EJ Loading...";
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

        configuration.setOperationMode(OperationMode.SWT_COMPATIBILITY);
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
                return new EntryPoint()
                {

                    public int createUI()
                    {

                        EJRWTContext.initContext();
                        EJRWTImageRetriever.setGraphicsProvider(new EJRWTGraphicsProvider()
                        {

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
                        preApplicationBuild(applicationManager);
                        applicationManager.buildApplication(appContainer, shell);
                        postApplicationBuild(applicationManager);
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

                        return openShell(display, shell);
                    }
                };
            }
        }, properties);
    }

    @SuppressWarnings("deprecation")
    public static int openShell(Display display, Shell shell)
    {
        shell.open();
        
            while (!shell.isDisposed())
            {
                if (!display.readAndDispatch())
                {
                    display.sleep();
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
        JavaScriptLoader loader = RWT.getClient().getService(JavaScriptLoader.class);
        loader.require("rwt-resources/" + new org.eclipse.ui.forms.internal.widgets.togglehyperlinkkit.ToggleHyperlinkResource().getLocation());
        loader.require("rwt-resources/" + new org.eclipse.ui.forms.internal.widgets.togglehyperlinkkit.ToggleHyperlinkAdapterResource().getLocation());
        loader.require("rwt-resources/" + new org.eclipse.ui.forms.internal.widgets.hyperlinkkit.HyperlinkAdapterResource().getLocation());
        loader.require("rwt-resources/" + new org.eclipse.ui.forms.internal.widgets.formtextkit.FormTextResource().getLocation());
        loader.require("rwt-resources/" + new org.eclipse.ui.forms.internal.widgets.formtextkit.FormTextAdapterResource().getLocation());
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
