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
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public class EJRWTCKEditor extends Composite
{

    private static final String    RESOURCES_PATH   = "resources/ckeditor/";
    private static final String    REGISTER_PATH    = "ckeditor/";

    private static final String[]  RESOURCE_FILES   = { "ckeditor.js", "styles.js", "config.js", "handler.js", "contents.css" };
    private static final String    REMOTE_TYPE      = "eclipsesource.CKEditor";

    private String                 text             = "";
    private final RemoteObject     remoteObject;

    private final OperationHandler operationHandler = new AbstractOperationHandler()
                                                    {
                                                        @Override
                                                        public void handleSet(JsonObject properties)
                                                        {
                                                            JsonValue textValue = properties.get("text");
                                                            if (textValue != null)
                                                            {
                                                                text = textValue.asString();
                                                            }
                                                        }
                                                    };

    public EJRWTCKEditor(Composite parent, int style,boolean inline)
    {
        super(parent, style);
       
    
        registerResources();
        loadJavaScript();
        Connection connection = RWT.getUISession().getConnection();
        remoteObject = connection.createRemoteObject(REMOTE_TYPE);
        remoteObject.setHandler(operationHandler);
        remoteObject.set("parent", WidgetUtil.getId(this));
        remoteObject.set("inline", inline);
        
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

                CodeSource src = EJRWTCKEditor.class.getProtectionDomain().getCodeSource();
                if (src != null)
                {
                    URL jar = src.getLocation();
                    ZipInputStream zip = new ZipInputStream(jar.openStream());
                    while (true)
                    {
                        ZipEntry e = zip.getNextEntry();
                        if (e == null)
                            break;
                        String name = e.getName();
                        if (name.startsWith("resources/ckeditor/lang/") || name.startsWith("resources/ckeditor/core/") || name.startsWith("resources/ckeditor/plugins/")
                                || name.startsWith("resources/ckeditor/skins/"))
                        {
                            if (!e.isDirectory())
                            {
                                register(resourceManager, name.replaceFirst("resources/ckeditor/", ""));
                            }
                        }
                    }
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
        jsLoader.require(resourceManager.getLocation(REGISTER_PATH + "handler.js"));

        jsLoader.require(resourceManager.getLocation(REGISTER_PATH + "ckeditor.js"));
        jsLoader.require(resourceManager.getLocation(REGISTER_PATH + "styles.js"));
        jsLoader.require(resourceManager.getLocation(REGISTER_PATH + "config.js"));

    }

    private void register(ResourceManager resourceManager, String fileName) throws IOException
    {
        ClassLoader classLoader = EJRWTCKEditor.class.getClassLoader();
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
        throw new UnsupportedOperationException("Cannot change internal layout of CkEditor");
    }

    @Override
    public void setFont(Font font)
    {
        super.setFont(font);
        remoteObject.set("font", getCssFont());
    }

    @Override
    public void dispose()
    {
        remoteObject.destroy();
        super.dispose();
    }

    
    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        remoteObject.set("enable", enabled);
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

    private String getCssFont()
    {
        StringBuilder result = new StringBuilder();
        if (getFont() != null)
        {
            FontData data = getFont().getFontData()[0];
            result.append(data.getHeight());
            result.append("px ");
            result.append(data.getName());
        }
        return result.toString();
    }

    public boolean getEditable()
    {
        return true;
    }

    public void setEditable(boolean editAllowed)
    {
        setEnabled(editAllowed);

    }

}
