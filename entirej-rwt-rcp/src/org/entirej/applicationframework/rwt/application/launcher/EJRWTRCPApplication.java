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

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Section;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationContainer;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.EJRWTGraphicsProvider;
import org.entirej.framework.core.EJFrameworkInitialiser;
import org.entirej.framework.core.interfaces.EJApplicationManager;
import org.entirej.framework.core.interfaces.EJMessenger;
import org.entirej.framework.core.properties.EJCoreLayoutContainer;
import org.entirej.framework.core.properties.EJCoreProperties;

public abstract class EJRWTRCPApplication
{

    protected String getShellIcon()
    {
        return "icons/favicon.ico";
    }

    public void run()
    {
        final Display display = new Display();
        EJRWTImageRetriever.setGraphicsProvider(new EJRWTGraphicsProvider()
        {

            private final ImageRegistry PLUGIN_REGISTRY = new ImageRegistry();

            public Image getImage(String name, ClassLoader loader)
            {
                Image image = PLUGIN_REGISTRY.get(name);
                if (image == null || image.isDisposed())
                {
                    if (name.startsWith("/") || name.startsWith("\\"))
                    {
                        image = new Image(display, loader.getResourceAsStream(name.substring(1)));
                    }
                    else
                        image = new Image(display, loader.getResourceAsStream(name));
                    PLUGIN_REGISTRY.put(name, image);
                }
                return image;

            }

            public float getAvgCharWidth(Font font)
            {
                GC gc = new GC(display);
                try
                {

                    gc.setFont(font);

                    return gc.getFontMetrics().getAverageCharWidth();
                }
                finally
                {
                    gc.dispose();
                }
            }

            public int getCharHeight(Font font)
            {
                if (font.getFontData().length > 0)
                {
                    return font.getFontData()[0].getHeight();
                }
                return 13;
            }

            public void rendererSection(Section section)
            {
                // IGNOTE

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

        Shell shell = new Shell(display);
        shell.setImage(EJRWTImageRetriever.get(getShellIcon()));
        shell.setText(layoutContainer.getTitle());
        preApplicationBuild(applicationManager);
        applicationManager.buildApplication(appContainer, shell);
        postApplicationBuild(applicationManager);
        shell.layout();
        shell.pack();
        shell.setMaximized(true);
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        display.dispose();
    }

    public void preApplicationBuild(EJApplicationManager appManager)
    {
    };

    public void postApplicationBuild(EJApplicationManager appManager)
    {
    };

}
