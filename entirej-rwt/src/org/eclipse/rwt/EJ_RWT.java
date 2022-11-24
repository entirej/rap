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
package org.eclipse.rwt;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.entirej.framework.core.internal.EJInternalForm;

import com.google.common.html.HtmlEscapers;

public class EJ_RWT
{

    public static final java.lang.String ACTIVE_KEYS          = "org.eclipse.rap.rwt.activeKeys";

    public static final java.lang.String CANCEL_KEYS          = "org.eclipse.rap.rwt.cancelKeys";

    public static final java.lang.String CUSTOM_ITEM_HEIGHT   = "org.eclipse.rap.rwt.customItemHeight";

    public static final java.lang.String MARKUP_ENABLED       = "org.eclipse.rap.rwt.markupEnabled";

    public static final java.lang.String FIXED_COLUMNS        = "org.eclipse.rap.rwt.fixedColumns";

    public static final java.lang.String DEFAULT_THEME_ID     = "org.eclipse.rap.rwt.theme.Default";

    public static final java.lang.String CUSTOM_VARIANT       = "org.eclipse.rap.rwt.customVariant";

    public static final java.lang.String CSS_CV_ITEM_GROUP    = "itemgroup";
    public static final java.lang.String CSS_CV_FORM          = "form";

    public static final java.lang.String CSS_CV_ITEM_BUTTON   = "itembutton";
    public static final java.lang.String CSS_CV_ITEM_CHECKBOX = "itemcheckbox";
    public static final java.lang.String CSS_CV_ITEM_COMBOBOX = "itemchombobox";
    public static final java.lang.String CSS_CV_ITEM_DATE     = "itemdate";
    public static final java.lang.String CSS_CV_ITEM_DATETIME = "itemdatetime";
    public static final java.lang.String CSS_CV_ITEM_NUMBER   = "itemnumber";
    public static final java.lang.String CSS_CV_ITEM_TEXT     = "itemtext";
    public static final java.lang.String CSS_CV_ITEM_DROPDOWN = "itemdropdown";
    public static final java.lang.String CSS_CV_ITEM_TEXTAREA = "itemtextarea";
    public static final java.lang.String CSS_CV_ITEM_IMAGE    = "itemimage";
    public static final java.lang.String CSS_CV_ITEM_LABEL    = "itemlabel";
    public static final java.lang.String CSS_CV_ITEM_LIST     = "itemlist";
    public static final java.lang.String CSS_CV_ITEM_RADIO    = "itemradio";

    public static final java.lang.String PROPERTY_CSS_KEY     = "CSS_KEY";

    public static class TextContext
    {
        private final AtomicBoolean TESTMODE = new AtomicBoolean(false);

        private TextContext()
        {
            // prevent instantiation from outside
        }

        public static TextContext getInstance()
        {
            return SingletonUtil.getSessionInstance(TextContext.class);
        }

    }

    public static void setTestId(Widget widget, String value)
    {
        if (!TextContext.getInstance().TESTMODE.get() && value != null && widget != null)
            return;

        if (!widget.isDisposed())
        {

            String $el = widget instanceof Text ? "$input" : "$el";
            String id = WidgetUtil.getId(widget);
            exec("rap.getObject( '", id, "' ).", $el, ".attr( 'test-id', '", value + "' );");
        }
    }

    public static void setAttribute(Widget widget, String attid, String value)
    {
        if (!TextContext.getInstance().TESTMODE.get() && value != null && widget != null)
            return;

        if (!widget.isDisposed())
        {

            String $el = widget instanceof Text ? "$input" : "$el";
            String id = WidgetUtil.getId(widget);
            exec("rap.getObject( '", id, "' ).", $el, ".attr( '" + attid + "', '", value + "' );");
        }
    }

    private static void exec(String... strings)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("try{");
        for (String str : strings)
        {
            builder.append(str);
        }
        builder.append("}catch(e){}");
        JavaScriptExecutor executor = RWT.getClient().getService(JavaScriptExecutor.class);
        executor.execute(builder.toString());
    }

    public static void setTestMode(boolean b)
    {
        TextContext.getInstance().TESTMODE.set(b);
    }

    public static String escapeHtmlWithXhtml(String string)
    {
        if (string == null)
            return string;

        String escapeHtml = escapeHtml(string);

        for (String tag : xhtmlSet)
        {
            escapeHtml = escapeHtml.replaceAll(escapeHtml(tag), tag);
        }

        return escapeHtml;
    }

    public static String escapeHtml(String string)
    {
        if (string == null)
            return string;

        return HtmlEscapers.htmlEscaper().escape(string);
    }

    /*
     * <b>text</b> renders its content in bold font style <i>text</i> renders
     * its content in italic font style <br/> inserts a line break <sub> renders
     * its content as subscript <sup> renders its content as superscript <big>
     * renders its content with bigger font size <small> renders its content
     * with smaller font size <del> renders its content as deleted text <ins>
     * renders its content as inserted text <em> renders its content as
     * emphasized text <strong> renders its content as strong emphasized text
     * <dfn> renders its content as instance definition <code> renders its
     * content as computer code fragment <samp> renders its content as sample
     * program output <kbd> renders its content as text to be entered by the
     * user <var> renders its content as instance of a variable or program
     * argument <cite> renders its content as citation <q> renders its content
     * as short inline quotation <abbr> renders its content as abbreviation
     * <span> generic style container <img> renders an image <a> renders a
     * hyperlink
     */

    private static Set<String> xhtmlSet = new HashSet<>();
    static
    {
        xhtmlSet.add("<br/>");
        xhtmlSet.add("<b>");
        xhtmlSet.add("</b>");
        xhtmlSet.add("<i>");
        xhtmlSet.add("</i>");
        xhtmlSet.add("<sub>");
        xhtmlSet.add("</sub>");
        xhtmlSet.add("<big>");
        xhtmlSet.add("</big>");
        xhtmlSet.add("<small>");
        xhtmlSet.add("</small>");
        xhtmlSet.add("<del>");
        xhtmlSet.add("</del>");
        xhtmlSet.add("<ins>");
        xhtmlSet.add("</ins>");
        xhtmlSet.add("<em>");
        xhtmlSet.add("</em>");
        xhtmlSet.add("<strong>");
        xhtmlSet.add("</strong>");
        xhtmlSet.add("<dfn>");
        xhtmlSet.add("</dfn>");
        xhtmlSet.add("<code>");
        xhtmlSet.add("</code>");
        xhtmlSet.add("<samp>");
        xhtmlSet.add("</samp>");
        xhtmlSet.add("<kbd>");
        xhtmlSet.add("</kbd>");
        xhtmlSet.add("<var>");
        xhtmlSet.add("</var>");
        xhtmlSet.add("<cite>");
        xhtmlSet.add("</cite>");
        xhtmlSet.add("<q>");
        xhtmlSet.add("</q>");
        xhtmlSet.add("<abbr>");
        xhtmlSet.add("<abbr/>");
        // NOT support yet to escape as text can go in-between start tag
        // xhtmlSet.add("<span>");
        // xhtmlSet.add("</span>");
        // xhtmlSet.add("<img>");
        // xhtmlSet.add("</img>");
        xhtmlSet.add("<a>");
        xhtmlSet.add("</a>");

    }

    public static void main(String[] args)
    {
        System.out.println(clearHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; blank spaces a > b").trim());
    }

    public static String clearHtml(String html)
    {
        html = html.replaceAll("&nbsp;", " ");
        final StringBuilder sb = new StringBuilder();
        HTMLEditorKit.ParserCallback parserCallback = new HTMLEditorKit.ParserCallback()
        {
            public boolean readyForNewline;

            @Override
            public void handleText(final char[] data, final int pos)
            {
                String s = new String(data);
                sb.append(s.trim());
                readyForNewline = true;
            }

            @Override
            public void handleStartTag(final HTML.Tag t, final MutableAttributeSet a, final int pos)
            {
                if (readyForNewline && (t == HTML.Tag.DIV || t == HTML.Tag.BR || t == HTML.Tag.P || t == HTML.Tag.LI))
                {
                    sb.append("\n");
                    readyForNewline = false;
                }
                
            }

            @Override
            public void handleSimpleTag(final HTML.Tag t, final MutableAttributeSet a, final int pos)
            {
                handleStartTag(t, a, pos);
            }
        };
        try
        {
            new ParserDelegator().parse(new StringReader(html), parserCallback, false);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String toFormID(EJInternalForm prop)
    {

        return mapper.toFormID(prop);
    }

    @FunctionalInterface
    public static interface TestFormIdMapper
    {
        String toFormID(EJInternalForm form);
    }

    private static TestFormIdMapper mapper = (form) -> form.getProperties().getName();

    public static void setMapper(TestFormIdMapper mapper)
    {
        EJ_RWT.mapper = mapper;
    }
    
   

}
