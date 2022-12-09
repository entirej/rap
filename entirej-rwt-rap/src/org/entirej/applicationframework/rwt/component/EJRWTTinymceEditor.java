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
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientFileLoader;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public class EJRWTTinymceEditor extends Composite
{

    /**
     * 
     */
    private static final long      serialVersionUID   = 1L;
    private static final String    RESOURCES_PATH     = "resources/tinymce/";
    private static final String    REGISTER_PATH      = "tinymceeditor/";

    private static final String[]  RESOURCE_FILES     = { "tinymce.min.js", "tinymcehandler.js" };
    private static final String[]  RESOURCE_FILES_SUB = { "plugins/fullscreen/plugin.min.js", "plugins/visualblocks/css/visualblocks.css", "skins/lightgray/content.inline.min.css", "skins/lightgray/content.min.css", "skins/lightgray/content.mobile.min.css", "skins/lightgray/skin.min.css",
            "skins/lightgray/skin.mobile.min.css", "skins/lightgray/fonts/tinymce-mobile.woff", "skins/lightgray/fonts/tinymce-small.eot", "skins/lightgray/fonts/tinymce-small.svg", "skins/lightgray/fonts/tinymce-small.ttf", "skins/lightgray/fonts/tinymce-small.woff", "skins/lightgray/fonts/tinymce.eot",
            "skins/lightgray/fonts/tinymce.svg", "skins/lightgray/fonts/tinymce.ttf", "skins/lightgray/fonts/tinymce.woff", "skins/lightgray/img/anchor.gif", "skins/lightgray/img/loader.gif", "skins/lightgray/img/object.gif", "skins/lightgray/img/trans.gif", "themes/inlite/theme.min.js", "themes/mobile/theme.min.js",
            "themes/modern/theme.min.js", };

    private static final String    REMOTE_TYPE        = "eclipsesource.TinymceEditor";

    private String                 text               = "";

    private final RemoteObject     remoteObject;

    private final OperationHandler operationHandler   = new AbstractOperationHandler()
                                                      {
                                                          /**
                                                          * 
                                                          */
                                                          private static final long serialVersionUID = 1L;

                                                          @Override
                                                          public void handleSet(JsonObject properties)
                                                          {
                                                              JsonValue textValue = properties.get("text");
                                                              if (textValue != null)
                                                              {
                                                                  String asString = textValue.asString();
                                                                  boolean changed = text != null && !text.equals(asString);
                                                                  text = asString;
                                                                  if (changed)
                                                                  {
                                                                      textValueChanged();
                                                                  }
                                                              }
                                                          }

                                                          @Override
                                                          public void handleCall(String method, JsonObject parameters)
                                                          {
                                                              action(method);
                                                          }
                                                      };
    private boolean                supportTable;

    public EJRWTTinymceEditor(Composite parent, int style, boolean inline, String profile, boolean removeToolbar, boolean supportTable, String contentCssFile, String configJsonFile)
    {
        super(parent, style);

        this.supportTable = supportTable;
        Connection connection = RWT.getUISession().getConnection();
        remoteObject = connection.createRemoteObject(REMOTE_TYPE);
        remoteObject.setHandler(operationHandler);
        remoteObject.set("parent", WidgetUtil.getId(this));
        remoteObject.set("inline", inline);
        remoteObject.set("removeToolbar", removeToolbar);
        remoteObject.set("supportTable", supportTable);
        remoteObject.set("profile", profile == null ? "Standard" : profile);
        // remoteObject.set("font", getCssFont());
        remoteObject.set("contentCss", read(contentCssFile == null || contentCssFile.isEmpty() ? hasFile("/resources/tinymce/ej/custom.ej.css") ? "/resources/tinymce/ej/custom.ej.css" : "resources/tinymce/ej/content.ej.css" : contentCssFile));
        remoteObject.set("configObj", readAsJson(configJsonFile == null || configJsonFile.isEmpty() ? "resources/tinymce/ej/config.ej.json" : configJsonFile));

    }

    public EJRWTTinymceEditor(Composite parent, int style, boolean inline, String profile, boolean removeToolbar, String contentCssFile, String configJsonFile)
    {
        this(parent, style, inline, profile, removeToolbar, false, contentCssFile, configJsonFile);

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

    protected void action(String method)
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

                for (String fileName : RESOURCE_FILES_SUB)
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
        // jsLoader.requireJs(resourceManager.getLocation(REGISTER_PATH +
        // "jquery-3.3.1.min.js"));
        jsLoader.requireJs(resourceManager.getLocation(REGISTER_PATH + "tinymcehandler.js"));

        jsLoader.requireJs(resourceManager.getLocation(REGISTER_PATH + "tinymce.min.js"));
        // jsLoader.requireJs(resourceManager.getLocation(REGISTER_PATH +
        // "jquery.tinymce.min.js"));

    }

    private static void register(ResourceManager resourceManager, String fileName) throws IOException
    {
        ClassLoader classLoader = EJRWTTinymceEditor.class.getClassLoader();
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

    @SuppressWarnings("resource")
    private static String read(String fileName)
    {
        ClassLoader classLoader = EJRWTTinymceEditor.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null)
        {
            return "";
        }
        try (java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");)
        {

            return s.hasNext() ? s.next().replaceAll("\\R+", " ") : "";
        }
        finally
        {
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static boolean hasFile(String fileName)
    {
        ClassLoader classLoader = EJRWTTinymceEditor.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null)
        {
            return false;
        }
        try
        {

            return true;
        }
        finally
        {
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static JsonObject readAsJson(String fileName)
    {
        ClassLoader classLoader = EJRWTTinymceEditor.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null)
        {
            return new JsonObject();
        }
        try (Reader reader = new InputStreamReader(inputStream);)
        {

            return JsonObject.readFrom(reader);

        }
        catch (IOException e1)
        {
            e1.printStackTrace();
            return new JsonObject();
        }
        finally
        {
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    // //////////////////
    // overwrite methods

    @Override
    public void setLayout(Layout layout)
    {
        throw new UnsupportedOperationException("Cannot change internal layout of Tinymce Editor");
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
        return supportTable ? text : stripHtml(text != null ? text.trim() : null);
    }

    private static String stripHtml(String text)
    {
        //
        if (text != null)
        {
            String tableRegEx = "<[/]?table[^>]*>";
            String tableBodyRegEx = "<[/]?tbody[^>]*>";
            String trRegEx = "<[/]?tr[^>]*>";
            String tdRegEx = "<[/]?td[^>]*>";
            text = text.replaceAll(tableRegEx, "");
            text = text.replaceAll(tableBodyRegEx, "");
            text = text.replaceAll(trRegEx, "<br>");
            text = text.replaceAll(tdRegEx, " ");
        }

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
        return isEnabled();
    }

    public void setEditable(boolean editAllowed)
    {
        setEnabled(editAllowed);

    }

}
