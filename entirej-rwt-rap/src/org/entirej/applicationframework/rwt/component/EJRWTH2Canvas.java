/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: EclipseSource - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientFileLoader;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.widgets.Display;

public class EJRWTH2Canvas
{

    /**
     * 
     */
    private static final long      serialVersionUID = 1L;
    private static final String    RESOURCES_PATH   = "resources/h2canvas/";
    private static final String    REGISTER_PATH    = "html2canvas/";

    private static final String[]  RESOURCE_FILES   = { "html2canvas.min.js", "Html2canvasHandler.js" };

    private static final String    REMOTE_TYPE      = "entirej.H2Canvas";
    private RemoteObject           remoteObject;
    private final OperationHandler operationHandler = new AbstractOperationHandler()
                                                    {
                                                        private static final long serialVersionUID = 1L;

                                                        public void handleSet(JsonObject properties)
                                                        {

                                                            if (callback != null)
                                                                callback.accept(properties.get("data").asString());
                                                        }

                                                        @Override
                                                        public void handleCall(final String method, final JsonObject parameters)
                                                        {
                                                            Display.getCurrent().asyncExec(new Runnable()
                                                                                                            {

                                                                                                                @Override
                                                                                                                public void run()
                                                                                                                {
                                                                                                                    action(method, parameters);

                                                                                                                }

                                                                                                            });
                                                        }
                                                    };
    private Consumer<String>       callback;

    protected void action(String method, JsonObject parameters)
    {
        if (callback != null)
            callback.accept(parameters.get("0").asString());

    }

    public EJRWTH2Canvas()
    {
        registerResources();
        loadJavaScript();
        Connection connection = RWT.getUISession().getConnection();
        remoteObject = connection.createRemoteObject(REMOTE_TYPE);
        remoteObject.setHandler(operationHandler);
    }

    public static void initResources()
    {
        registerResources();
        loadJavaScript();
    }

    protected void textValueChanged()
    {
        // ignore

    }

    private static void registerResources()
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

            }
            catch (IOException ioe)
            {
                throw new IllegalArgumentException("Failed to load resources", ioe);
            }
        }
    }

    private static void loadJavaScript()
    {
        ClientFileLoader jsLoader = RWT.getClient().getService(ClientFileLoader.class);
        ResourceManager resourceManager = RWT.getResourceManager();

        jsLoader.requireJs(resourceManager.getLocation(REGISTER_PATH + "Html2canvasHandler.js"));
        jsLoader.requireJs(resourceManager.getLocation(REGISTER_PATH + "html2canvas.min.js"));

    }

    private static void register(ResourceManager resourceManager, String fileName) throws IOException
    {
        ClassLoader classLoader = EJRWTH2Canvas.class.getClassLoader();
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

    public void screenshot(Consumer<String> callback)
    {
        this.callback = callback;
        remoteObject.set("data", "snap");
       
    }

}
