/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public abstract class EJRWTAbstractDraw2DView extends Composite
{

    private static final String   RESOURCES_PATH = "resources/draw2d/";
    private static final String   REGISTER_PATH  = "draw2d/";

    private static final String[] RESOURCE_FILES = { "raphael.js", "rgbcolor.js", "shifty.js" , "canvg.js", "Class.js", "jquery-1.10.2.min.js", "jquery-touch_punch.js",
            "jquery.autoresize.js", "jquery.contextmenu.js", "json2.js", "pathfinding-browser.min.js", "draw2d.js" };
    private static final String   REMOTE_TYPE    = "ej.draw2d";

    protected final RemoteObject  remoteObject;

    protected OperationHandler getOperationHandler()
    {
        return new AbstractOperationHandler()
        {

        };
    }

    public EJRWTAbstractDraw2DView(Composite parent, int style)
    {
        super(parent, style);

        registerResources();
        loadJavaScript();
        Connection connection = RWT.getUISession().getConnection();
        remoteObject = connection.createRemoteObject(REMOTE_TYPE);
        remoteObject.setHandler(getOperationHandler());
        remoteObject.set("parent", WidgetUtil.getId(this));

    }

    private void registerResources()
    {
        ResourceManager resourceManager = RWT.getResourceManager();
        boolean isRegistered = resourceManager.isRegistered(REGISTER_PATH + RESOURCE_FILES[0]);
        if (!isRegistered)
        {
            try
            {
                for (String fileName : RESOURCE_FILES)
                {
                    register(resourceManager, fileName);
                }

                ClassLoader classLoader = EJRWTAbstractDraw2DView.class.getClassLoader();
                String[] handlerJS = getHandlerJS();
                for (String handler : handlerJS)
                {
                    InputStream inputStream = classLoader.getResourceAsStream(RESOURCES_PATH + handler);
                    if (inputStream == null)
                    {
                        throw new FileNotFoundException(RESOURCES_PATH + handler);
                    }
                    try
                    {
                        resourceManager.register(REGISTER_PATH + handler, inputStream);
                    }
                    finally
                    {
                        inputStream.close();
                    }
                }

            }
            catch (IOException ioe)
            {
                throw new IllegalArgumentException("Failed to load resources", ioe);
            }
        }
    }

    public abstract String[] getHandlerJS();

    private void loadJavaScript()
    {
        JavaScriptLoader jsLoader = RWT.getClient().getService(JavaScriptLoader.class);
        ResourceManager resourceManager = RWT.getResourceManager();

        for (String file : RESOURCE_FILES)
        {
            jsLoader.require(resourceManager.getLocation(REGISTER_PATH + file));
        }
        for (String file : getHandlerJS())
        {
            jsLoader.require(resourceManager.getLocation(REGISTER_PATH + file));
        }

    }

    private void register(ResourceManager resourceManager, String fileName) throws IOException
    {
        ClassLoader classLoader = EJRWTAbstractDraw2DView.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(RESOURCES_PATH + fileName);
        if (inputStream == null)
        {
            throw new FileNotFoundException(RESOURCES_PATH + fileName);
        }
        try
        {
            resourceManager.register(REGISTER_PATH + fileName, inputStream);
        }
        finally
        {
            inputStream.close();
        }

    }

    // //////////////////
    // overwrite methods

    @Override
    public void setLayout(Layout layout)
    {
        throw new UnsupportedOperationException("Cannot change internal layout of draw2d");
    }

    @Override
    public void dispose()
    {
        remoteObject.destroy();
        super.dispose();
    }

}
