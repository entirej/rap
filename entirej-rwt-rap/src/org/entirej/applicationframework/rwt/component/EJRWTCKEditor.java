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

public class EJRWTCKEditor extends Composite
{

    private static final String    RESOURCES_PATH     = "resources/ckeditor/";
    private static final String    REGISTER_PATH      = "ckeditor/";

    private static final String[]  RESOURCE_FILES     = { "ckeditor.js", "styles.js", "handler.js", "contents.css" };
    private static final String[]  RESOURCE_FILES_SUB = { "lang/en.js", "plugins/a11yhelp/dialogs/lang/_translationstatus.txt", "plugins/a11yhelp/dialogs/lang/af.js", "plugins/a11yhelp/dialogs/lang/ar.js", "plugins/a11yhelp/dialogs/lang/bg.js", "plugins/a11yhelp/dialogs/lang/ca.js",
            "plugins/a11yhelp/dialogs/lang/cs.js", "plugins/a11yhelp/dialogs/lang/cy.js", "plugins/a11yhelp/dialogs/lang/da.js", "plugins/a11yhelp/dialogs/lang/de.js", "plugins/a11yhelp/dialogs/lang/el.js", "plugins/a11yhelp/dialogs/lang/en-gb.js", "plugins/a11yhelp/dialogs/lang/en.js",
            "plugins/a11yhelp/dialogs/lang/eo.js", "plugins/a11yhelp/dialogs/lang/es.js", "plugins/a11yhelp/dialogs/lang/et.js", "plugins/a11yhelp/dialogs/lang/fa.js", "plugins/a11yhelp/dialogs/lang/fi.js", "plugins/a11yhelp/dialogs/lang/fo.js", "plugins/a11yhelp/dialogs/lang/fr-ca.js",
            "plugins/a11yhelp/dialogs/lang/fr.js", "plugins/a11yhelp/dialogs/lang/gl.js", "plugins/a11yhelp/dialogs/lang/gu.js", "plugins/a11yhelp/dialogs/lang/he.js", "plugins/a11yhelp/dialogs/lang/hi.js", "plugins/a11yhelp/dialogs/lang/hr.js", "plugins/a11yhelp/dialogs/lang/hu.js",
            "plugins/a11yhelp/dialogs/lang/id.js", "plugins/a11yhelp/dialogs/lang/it.js", "plugins/a11yhelp/dialogs/lang/ja.js", "plugins/a11yhelp/dialogs/lang/km.js", "plugins/a11yhelp/dialogs/lang/ko.js", "plugins/a11yhelp/dialogs/lang/ku.js", "plugins/a11yhelp/dialogs/lang/lt.js",
            "plugins/a11yhelp/dialogs/lang/lv.js", "plugins/a11yhelp/dialogs/lang/mk.js", "plugins/a11yhelp/dialogs/lang/mn.js", "plugins/a11yhelp/dialogs/lang/nb.js", "plugins/a11yhelp/dialogs/lang/nl.js", "plugins/a11yhelp/dialogs/lang/no.js", "plugins/a11yhelp/dialogs/lang/pl.js",
            "plugins/a11yhelp/dialogs/lang/pt-br.js", "plugins/a11yhelp/dialogs/lang/pt.js", "plugins/a11yhelp/dialogs/lang/ro.js", "plugins/a11yhelp/dialogs/lang/ru.js", "plugins/a11yhelp/dialogs/lang/si.js", "plugins/a11yhelp/dialogs/lang/sk.js", "plugins/a11yhelp/dialogs/lang/sl.js",
            "plugins/a11yhelp/dialogs/lang/sq.js", "plugins/a11yhelp/dialogs/lang/sr-latn.js", "plugins/a11yhelp/dialogs/lang/sr.js", "plugins/a11yhelp/dialogs/lang/sv.js", "plugins/a11yhelp/dialogs/lang/th.js", "plugins/a11yhelp/dialogs/lang/tr.js", "plugins/a11yhelp/dialogs/lang/tt.js",
            "plugins/a11yhelp/dialogs/lang/ug.js", "plugins/a11yhelp/dialogs/lang/uk.js", "plugins/a11yhelp/dialogs/lang/vi.js", "plugins/a11yhelp/dialogs/lang/zh-cn.js", "plugins/a11yhelp/dialogs/lang/zh.js", "plugins/a11yhelp/dialogs/a11yhelp.js", "plugins/about/dialogs/hidpi/logo_ckeditor.png",
            "plugins/about/dialogs/about.js", "plugins/about/dialogs/logo_ckeditor.png", "plugins/base64image/dialogs/base64image.js", "plugins/clipboard/dialogs/paste.js", "plugins/colordialog/dialogs/colordialog.js", "plugins/dialog/dialogDefinition.js", "plugins/div/dialogs/div.js", "plugins/find/dialogs/find.js",
            "plugins/flash/dialogs/flash.js", "plugins/flash/images/placeholder.png", "plugins/floating-tools/plugin.js", "plugins/forms/dialogs/button.js", "plugins/forms/dialogs/checkbox.js", "plugins/forms/dialogs/form.js", "plugins/forms/dialogs/hiddenfield.js", "plugins/forms/dialogs/radio.js",
            "plugins/forms/dialogs/select.js", "plugins/forms/dialogs/textarea.js", "plugins/forms/dialogs/textfield.js", "plugins/forms/images/hiddenfield.gif", "plugins/iframe/dialogs/iframe.js", "plugins/iframe/images/placeholder.png", "plugins/image/dialogs/image.js", "plugins/image/images/noimage.png",
            "plugins/link/dialogs/anchor.js", "plugins/link/dialogs/link.js", "plugins/link/images/hidpi/anchor.png", "plugins/link/images/anchor.png", "plugins/liststyle/dialogs/liststyle.js", "plugins/magicline/images/hidpi/icon-rtl.png", "plugins/magicline/images/hidpi/icon.png",
            "plugins/magicline/images/icon-rtl.png", "plugins/magicline/images/icon.png", "plugins/onchange/docs/install.html", "plugins/onchange/docs/styles.css", "plugins/pagebreak/images/pagebreak.gif", "plugins/pastefromword/filter/default.js", "plugins/preview/preview.html", "plugins/scayt/dialogs/options.js",
            "plugins/scayt/dialogs/toolbar.css", "plugins/showblocks/images/block_address.png", "plugins/showblocks/images/block_blockquote.png", "plugins/showblocks/images/block_div.png", "plugins/showblocks/images/block_h1.png", "plugins/showblocks/images/block_h2.png", "plugins/showblocks/images/block_h3.png",
            "plugins/showblocks/images/block_h4.png", "plugins/showblocks/images/block_h5.png", "plugins/showblocks/images/block_h6.png", "plugins/showblocks/images/block_p.png", "plugins/showblocks/images/block_pre.png", "plugins/smiley/dialogs/smiley.js", "plugins/smiley/images/angel_smile.gif",
            "plugins/smiley/images/angel_smile.png", "plugins/smiley/images/angry_smile.gif", "plugins/smiley/images/angry_smile.png", "plugins/smiley/images/broken_heart.gif", "plugins/smiley/images/broken_heart.png", "plugins/smiley/images/confused_smile.gif", "plugins/smiley/images/confused_smile.png",
            "plugins/smiley/images/cry_smile.gif", "plugins/smiley/images/cry_smile.png", "plugins/smiley/images/devil_smile.gif", "plugins/smiley/images/devil_smile.png", "plugins/smiley/images/embaressed_smile.gif", "plugins/smiley/images/embarrassed_smile.gif", "plugins/smiley/images/embarrassed_smile.png",
            "plugins/smiley/images/envelope.gif", "plugins/smiley/images/envelope.png", "plugins/smiley/images/heart.gif", "plugins/smiley/images/heart.png", "plugins/smiley/images/kiss.gif", "plugins/smiley/images/kiss.png", "plugins/smiley/images/lightbulb.gif", "plugins/smiley/images/lightbulb.png",
            "plugins/smiley/images/omg_smile.gif", "plugins/smiley/images/omg_smile.png", "plugins/smiley/images/regular_smile.gif", "plugins/smiley/images/regular_smile.png", "plugins/smiley/images/sad_smile.gif", "plugins/smiley/images/sad_smile.png", "plugins/smiley/images/shades_smile.gif",
            "plugins/smiley/images/shades_smile.png", "plugins/smiley/images/teeth_smile.gif", "plugins/smiley/images/teeth_smile.png", "plugins/smiley/images/thumbs_down.gif", "plugins/smiley/images/thumbs_down.png", "plugins/smiley/images/thumbs_up.gif", "plugins/smiley/images/thumbs_up.png",
            "plugins/smiley/images/tongue_smile.gif", "plugins/smiley/images/tongue_smile.png", "plugins/smiley/images/tounge_smile.gif", "plugins/smiley/images/whatchutalkingabout_smile.gif", "plugins/smiley/images/whatchutalkingabout_smile.png", "plugins/smiley/images/wink_smile.gif",
            "plugins/smiley/images/wink_smile.png", "plugins/specialchar/dialogs/lang/_translationstatus.txt", "plugins/specialchar/dialogs/lang/af.js", "plugins/specialchar/dialogs/lang/ar.js", "plugins/specialchar/dialogs/lang/bg.js", "plugins/specialchar/dialogs/lang/ca.js",
            "plugins/specialchar/dialogs/lang/cs.js", "plugins/specialchar/dialogs/lang/cy.js", "plugins/specialchar/dialogs/lang/da.js", "plugins/specialchar/dialogs/lang/de.js", "plugins/specialchar/dialogs/lang/el.js", "plugins/specialchar/dialogs/lang/en-gb.js", "plugins/specialchar/dialogs/lang/en.js",
            "plugins/specialchar/dialogs/lang/eo.js", "plugins/specialchar/dialogs/lang/es.js", "plugins/specialchar/dialogs/lang/et.js", "plugins/specialchar/dialogs/lang/fa.js", "plugins/specialchar/dialogs/lang/fi.js", "plugins/specialchar/dialogs/lang/fr-ca.js", "plugins/specialchar/dialogs/lang/fr.js",
            "plugins/specialchar/dialogs/lang/gl.js", "plugins/specialchar/dialogs/lang/he.js", "plugins/specialchar/dialogs/lang/hr.js", "plugins/specialchar/dialogs/lang/hu.js", "plugins/specialchar/dialogs/lang/id.js", "plugins/specialchar/dialogs/lang/it.js", "plugins/specialchar/dialogs/lang/ja.js",
            "plugins/specialchar/dialogs/lang/km.js", "plugins/specialchar/dialogs/lang/ko.js", "plugins/specialchar/dialogs/lang/ku.js", "plugins/specialchar/dialogs/lang/lt.js", "plugins/specialchar/dialogs/lang/lv.js", "plugins/specialchar/dialogs/lang/nb.js", "plugins/specialchar/dialogs/lang/nl.js",
            "plugins/specialchar/dialogs/lang/no.js", "plugins/specialchar/dialogs/lang/pl.js", "plugins/specialchar/dialogs/lang/pt-br.js", "plugins/specialchar/dialogs/lang/pt.js", "plugins/specialchar/dialogs/lang/ru.js", "plugins/specialchar/dialogs/lang/si.js", "plugins/specialchar/dialogs/lang/sk.js",
            "plugins/specialchar/dialogs/lang/sl.js", "plugins/specialchar/dialogs/lang/sq.js", "plugins/specialchar/dialogs/lang/sv.js", "plugins/specialchar/dialogs/lang/th.js", "plugins/specialchar/dialogs/lang/tr.js", "plugins/specialchar/dialogs/lang/tt.js", "plugins/specialchar/dialogs/lang/ug.js",
            "plugins/specialchar/dialogs/lang/uk.js", "plugins/specialchar/dialogs/lang/vi.js", "plugins/specialchar/dialogs/lang/zh-cn.js", "plugins/specialchar/dialogs/lang/zh.js", "plugins/specialchar/dialogs/specialchar.js", "plugins/table/dialogs/table.js", "plugins/tabletools/dialogs/tableCell.js",
            "plugins/templates/dialogs/templates.css", "plugins/templates/dialogs/templates.js", "plugins/templates/templates/images/template1.gif", "plugins/templates/templates/images/template2.gif", "plugins/templates/templates/images/template3.gif", "plugins/templates/templates/default.js",
            "plugins/wsc/dialogs/ciframe.html", "plugins/wsc/dialogs/tmpFrameset.html", "plugins/wsc/dialogs/wsc.css", "plugins/wsc/dialogs/wsc.js", "plugins/wsc/dialogs/wsc_ie.js", "plugins/icons.png", "plugins/icons_hidpi.png", "skins/moono/images/hidpi/close.png", "skins/moono/images/hidpi/lock-open.png",
            "skins/moono/images/hidpi/lock.png", "skins/moono/images/hidpi/refresh.png", "skins/moono/images/arrow.png", "skins/moono/images/close.png", "skins/moono/images/lock-open.png", "skins/moono/images/lock.png", "skins/moono/images/refresh.png", "skins/moono/images/spinner.gif", "skins/moono/dialog.css",
            "skins/moono/dialog_ie.css", "skins/moono/dialog_ie7.css", "skins/moono/dialog_ie8.css", "skins/moono/dialog_iequirks.css", "skins/moono/editor.css", "skins/moono/editor_gecko.css", "skins/moono/editor_ie.css", "skins/moono/editor_ie7.css", "skins/moono/editor_ie8.css", "skins/moono/editor_iequirks.css",
            "skins/moono/icons.png", "skins/moono/icons_hidpi.png", };
    private static final String    REMOTE_TYPE        = "eclipsesource.CKEditor";

    private String                 text               = "";
    private final RemoteObject     remoteObject;

    private final OperationHandler operationHandler   = new AbstractOperationHandler()
                                                      {
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
                                                      };

    public EJRWTCKEditor(Composite parent, int style, boolean inline, String profile, boolean removeToolbar)
    {
        super(parent, style);

        Connection connection = RWT.getUISession().getConnection();
        remoteObject = connection.createRemoteObject(REMOTE_TYPE);
        remoteObject.setHandler(operationHandler);
        remoteObject.set("parent", WidgetUtil.getId(this));
        remoteObject.set("inline", inline);
        remoteObject.set("removeToolbar", removeToolbar);
        remoteObject.set("profile", profile == null ? "Standard" : profile);

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
                for (String fileName : RESOURCE_FILES_SUB)
                {
                    register(resourceManager, fileName);
                }

                // String build = "";
                //
                // CodeSource src =
                // EJRWTCKEditor.class.getProtectionDomain().getCodeSource();
                // if (src != null)
                // {
                // URL jar = src.getLocation();
                //
                // ZipInputStream zip = new ZipInputStream(jar.openStream());
                // while (true)
                // {
                // ZipEntry e = zip.getNextEntry();
                // if (e == null)
                // break;
                // String name = e.getName();
                // if (name.startsWith("resources/ckeditor/lang/") ||
                // name.startsWith("resources/ckeditor/core/")
                // || name.startsWith("resources/ckeditor/plugins/") ||
                // name.startsWith("resources/ckeditor/skins/"))
                // {
                // if (!e.isDirectory())
                // {
                // String first = name.replaceFirst("resources/ckeditor/", "");
                // register(resourceManager, first);
                // if (!name.endsWith(".md"))
                // build += "\"" + first + "\" , ";
                //
                // }
                // }
                // }
                // System.err.println(build);
                // }
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
        jsLoader.requireJs(resourceManager.getLocation(REGISTER_PATH + "handler.js"));

        jsLoader.requireJs(resourceManager.getLocation(REGISTER_PATH + "ckeditor.js"));
        jsLoader.requireJs(resourceManager.getLocation(REGISTER_PATH + "styles.js"));
        // jsLoader.require(resourceManager.getLocation(REGISTER_PATH +
        // "config.js"));

    }

    private static void register(ResourceManager resourceManager, String fileName) throws IOException
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
        return text != null ? text.trim() : null;
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
