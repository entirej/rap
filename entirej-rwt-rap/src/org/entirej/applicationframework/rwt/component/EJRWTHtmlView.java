/*******************************************************************************
 * Copyright 2013 CRESOFT AG
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
 *     CRESOFT AG - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.component;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;

public class EJRWTHtmlView extends Composite
{

    /**
     * 
     */
    private static final long      serialVersionUID = 1L;
    private static final String    RESOURCES_PATH   = "resources/";
    private static final String    REGISTER_PATH    = "ejhtmlview/";

    private static final String[]  RESOURCE_FILES   = { "HtmlViewHandler.js", };
    private static final String    REMOTE_TYPE      = "entirej.HtmlView";

    private String                 text             = "";
    private final RemoteObject     remoteObject;
    private int                    scrollPos;

    private final OperationHandler operationHandler = new AbstractOperationHandler()
                                                    {
                                                        private static final long serialVersionUID = 1L;

                                                        public void handleSet(JsonObject properties)
                                                        {
                                                            JsonValue textValue = properties.get("scroll");
                                                            if (textValue != null)
                                                            {
                                                                scrollPos = textValue.asObject().get("vpos").asInt();
                                                            }
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

    public void action(String method, JsonObject parameters)
    {

    }

    public EJRWTHtmlView(Composite parent, int style,boolean textSelection)
    {
        super(parent, style);
        registerResources();
        loadJavaScript();
        Connection connection = RWT.getUISession().getConnection();
        remoteObject = connection.createRemoteObject(REMOTE_TYPE);
        remoteObject.setHandler(operationHandler);
        remoteObject.set("parent", WidgetUtil.getId(this));
        remoteObject.set("textSelect", textSelection);
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
            }
            catch (IOException ioe)
            {
                throw new IllegalArgumentException("Failed to load resources", ioe);
            }
        }
    }

    private void loadJavaScript()
    {
        JavaScriptLoader jsLoader = RWT.getClient().getService(JavaScriptLoader.class);
        ResourceManager resourceManager = RWT.getResourceManager();
        jsLoader.require(resourceManager.getLocation(REGISTER_PATH + "HtmlViewHandler.js"));
    }

    private void register(ResourceManager resourceManager, String fileName) throws IOException
    {
        ClassLoader classLoader = EJRWTHtmlView.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(RESOURCES_PATH + fileName);
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
        throw new UnsupportedOperationException("Cannot change internal layout of HtmlView");
    }

    @Override
    public void dispose()
    {
        remoteObject.destroy();
        super.dispose();
    }

    // ////
    // API

    public void setText(String text)
    {
        checkWidget();
        if (text == null)
        {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        this.text = text;
        remoteObject.set("text", text);
    }

    public String getText()
    {
        checkWidget();
        return text;
    }

    public void setScroll(int pos)
    {
        checkWidget();

        remoteObject.set("scroll", pos);
    }
    
    
    public void setSelection(String id)
    {
        checkWidget();

        remoteObject.set("selection",  id);
    }

    public int getScroll()
    {
        return scrollPos;
    }

}