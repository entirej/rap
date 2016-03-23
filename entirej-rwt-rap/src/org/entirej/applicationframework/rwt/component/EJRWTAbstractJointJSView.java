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

public abstract class EJRWTAbstractJointJSView extends Composite
{

    private static final String   RESOURCES_PATH = "resources/jointjs/";
    private static final String   REGISTER_PATH  = "jointjs/";

    private static final String[] RESOURCE_FILES = { "jquery.min.js", "lodash.min.js", "backbone-min.js", "joint.min.js", "joint.min.css" };
    private static final String   REMOTE_TYPE    = "eclipsesource.JointJS";

    private final RemoteObject    remoteObject;

    protected OperationHandler getOperationHandler()
    {
        return new AbstractOperationHandler()
        {
            
        };
    }

    public EJRWTAbstractJointJSView(Composite parent, int style)
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

                ClassLoader classLoader = EJRWTAbstractJointJSView.class.getClassLoader();
                InputStream inputStream = classLoader.getResourceAsStream(RESOURCES_PATH + getHandlerJS());
                if (inputStream == null)
                {
                    throw new FileNotFoundException(RESOURCES_PATH + getHandlerJS());
                }
                try
                {
                    resourceManager.register(REGISTER_PATH + getHandlerJS(), inputStream);
                }
                finally
                {
                    inputStream.close();
                }

            }
            catch (IOException ioe)
            {
                throw new IllegalArgumentException("Failed to load resources", ioe);
            }
        }
    }

    public abstract String getHandlerJS();

    private void loadJavaScript()
    {
        JavaScriptLoader jsLoader = RWT.getClient().getService(JavaScriptLoader.class);
        ResourceManager resourceManager = RWT.getResourceManager();

        jsLoader.require(resourceManager.getLocation(REGISTER_PATH + getHandlerJS()));

        jsLoader.require(resourceManager.getLocation(REGISTER_PATH + "jquery.min.js"));
        jsLoader.require(resourceManager.getLocation(REGISTER_PATH + "lodash.min.js"));
        jsLoader.require(resourceManager.getLocation(REGISTER_PATH + "backbone-min.js"));
        jsLoader.require(resourceManager.getLocation(REGISTER_PATH + "joint.min.js"));
        // jsLoader.require(resourceManager.getLocation(REGISTER_PATH +
        // "config.js"));

    }

    private void register(ResourceManager resourceManager, String fileName) throws IOException
    {
        ClassLoader classLoader = EJRWTAbstractJointJSView.class.getClassLoader();
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
        throw new UnsupportedOperationException("Cannot change internal layout of JointJS");
    }

    @Override
    public void dispose()
    {
        remoteObject.destroy();
        super.dispose();
    }

}
