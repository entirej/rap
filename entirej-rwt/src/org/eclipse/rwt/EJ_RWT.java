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
    public static final java.lang.String CSS_CV_ITEM_TEXTAREA = "itemtextarea";
    public static final java.lang.String CSS_CV_ITEM_IMAGE    = "itemimage";
    public static final java.lang.String CSS_CV_ITEM_LABEL    = "itemlabel";
    public static final java.lang.String CSS_CV_ITEM_LIST     = "itemlist";
    public static final java.lang.String CSS_CV_ITEM_RADIO    = "itemradio";

    public static final java.lang.String PROPERTY_CSS_KEY     = "CSS_KEY";

    
    
    
    
    public static  class TextContext {
        private  final AtomicBoolean TESTMODE = new AtomicBoolean(false); 
        private TextContext() {
          // prevent instantiation from outside
        }

        public static TextContext getInstance() {
          return SingletonUtil.getSessionInstance( TextContext.class );
        }

      }
    
    
    public static void setTestId(Widget widget, String value)
    {
        if(!TextContext.getInstance().TESTMODE.get() && value !=null && widget!=null)
            return ;
        
        if ( !widget.isDisposed())
        {
            
            
            String $el = widget instanceof Text ? "$input" : "$el";
            String id = WidgetUtil.getId(widget);
            exec("rap.getObject( '", id, "' ).", $el, ".attr( 'test-id', '", value + "' );");
        }
    }
    
   
    
    public static void setAttribute(Widget widget,String attid, String value)
    {
        if(!TextContext.getInstance().TESTMODE.get() && value !=null && widget!=null)
            return ;
        
        if ( !widget.isDisposed())
        {
            
            
            String $el = widget instanceof Text ? "$input" : "$el";
            String id = WidgetUtil.getId(widget);
            exec("rap.getObject( '", id, "' ).", $el, ".attr( '"+attid+"', '", value + "' );");
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
        if(string==null)
            return string;
        
        String escapeHtml =  escapeHtml(string);
         
        
        
         for (String tag : xhtmlSet)
         {
             escapeHtml=escapeHtml.replaceAll(escapeHtml(tag),tag);
         }
        
        return escapeHtml;
    }
    
    public static String escapeHtml(String string)
    {
        if(string==null)
            return string;
        
        
        return HtmlEscapers.htmlEscaper().escape(string);
    }
    
    
    /*
    <b>text</b>
    renders its content in bold font style
    <i>text</i>
    renders its content in italic font style
    <br/>
    inserts a line break
    <sub>
    renders its content as subscript
    <sup>
    renders its content as superscript
    <big>
    renders its content with bigger font size
    <small>
    renders its content with smaller font size
    <del>
    renders its content as deleted text
    <ins>
    renders its content as inserted text
    <em>
    renders its content as emphasized text
    <strong>
    renders its content as strong emphasized text
    <dfn>
    renders its content as instance definition
    <code>
    renders its content as computer code fragment
    <samp>
    renders its content as sample program output
    <kbd>
    renders its content as text to be entered by the user
    <var>
    renders its content as instance of a variable or program argument
    <cite>
    renders its content as citation
    <q>
    renders its content as short inline quotation
    <abbr>
    renders its content as abbreviation
    <span>
    generic style container
    <img>
    renders an image
    <a>
    renders a hyperlink
     */
    
    private static Set<String> xhtmlSet = new HashSet<>();
    static {
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
//NOT support yet to escape as text can go in-between start tag          
//        xhtmlSet.add("<span>");
//        xhtmlSet.add("</span>");
//        xhtmlSet.add("<img>");
//        xhtmlSet.add("</img>");
        xhtmlSet.add("<a>");
        xhtmlSet.add("</a>");
        
    }
    
    public static void main(String[] args)
    {
        System.out.println(clearHtml("<p><strong>PORTFOLIO OVERVIEW</strong></p>\n" + 
                "<p>&nbsp;</p>\n" + 
                "<p><strong>In General </strong></p>\n" + 
                "<ul style=\"color: #4a4a4a; font-family: Verdana,&amp;quot; lucida sans&amp;quot;,arial,helvetica,sans-serif; font-size: 11px; font-style: normal; font-variant: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: left; text-decoration: none; text-indent: 0px; text-transform: none; -webkit-text-stroke-width: 0px; white-space: normal; word-spacing: 0px;\">\n" + 
                "<li style=\"color: #4a4a4a; font-family: Verdana,&amp;quot;\">The presentation of the Portfolio Overview depends on customer type (Private, Bank, Inst. Client, Corporate, Broker, Sovereign, Nostro). It will be adjust on different information requirements.</li>\n" + 
                "<li style=\"color: #4a4a4a; font-family: Verdana,&amp;quot;\">The data visible in TEMOS are always&nbsp;<strong>end of the previous business day;</strong> no intraday changes will occur (e.g. positions).</li>\n" + 
                "<li style=\"color: #4a4a4a; font-family: Verdana,&amp;quot;\">Condensed risk information of a client with a first assessment.\n" + 
                "<ul>\n" + 
                "<li><strong>Risk calculation</strong>\n" + 
                "<ul>\n" + 
                "<li><strong>Market Value:</strong> Current price for acted shares, Precious metals and currencies. In contrast to the nominal value, the exchange rate value of a security is constantly to fluctuations depending upon supply and demand.</li>\n" + 
                "<li><strong>Lending Value:</strong> Value, which a bank assigns to a collateral. The value of the collaterals must cover the amount of the exposure.</li>\n" + 
                "<li><strong>Risk Exposure:</strong> Loans, guarantees, etc.</li>\n" + 
                "<li><strong>Netting:</strong> Cash balances are netted per currency; FX Forward deals get netted per currency, currency tier, maturity - unrealized loss gets displayed and added to the direct exposure - on the other side gains are considered as collateral like a cash balance.</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "<li><strong>Limits</strong>\n" + 
                "<ul>\n" + 
                "<li>Several limit types: e.g. Lombard Limit etc.</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "<li><strong>Connections:</strong> third party liabilities\n" + 
                "<ul>\n" + 
                "<li>Single, Pledge or Group&nbsp;</li>\n" + 
                "<li>Hierarchy</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "</ul>\n" + 
                "<p>&nbsp;</p>\n" + 
                "<p><strong>Functionalities</strong></p>\n" + 
                "<ul style=\"color: #4a4a4a; font-family: Verdana,&amp;quot; lucida sans&amp;quot;,arial,helvetica,sans-serif; font-size: 11px; font-style: normal; font-variant: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: left; text-decoration: none; text-indent: 0px; text-transform: none; -webkit-text-stroke-width: 0px; white-space: normal; word-spacing: 0px;\">\n" + 
                "<li style=\"color: #4a4a4a; font-family: Verdana,&amp;quot;\"><span style=\"color: #ff0000; font-family: Verdana,&amp;quot;\"><strong style=\"color: #4a4a4a; font-family: Verdana,&amp;quot; lucida sans&amp;quot;,arial,helvetica,sans-serif; font-size: 11px; font-style: normal; font-variant: normal; font-weight: bold; letter-spacing: normal; orphans: 2; text-align: left; text-decoration: none; text-indent: 0px; text-transform: none; -webkit-text-stroke-width: 0px; white-space: normal; word-spacing: 0px;\">Customer Alert</strong></span><span style=\"color: #4a4a4a; font-family: Verdana,&amp;quot; lucida sans&amp;quot;,arial,helvetica,sans-serif; font-size: 11px;\"> <span style=\"color: #4a4a4a; font-family: verdana,geneva,sans-serif;\"><span style=\"color: #001000; font-family: Verdana,&amp;quot;\">I<span style=\"background-color: #ffffff; color: #333333; display: inline; float: none; font-family: verdana,geneva,sans-serif; font-style: normal; font-variant: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: left; text-decoration: none; text-indent: 0px; text-transform: none; -webkit-text-stroke-width: 0px; white-space: normal; word-spacing: 0px;\">mportant Information, e.g. all types of Restrictions</span></span></span></span></li>\n" + 
                "</ul>\n" + 
                "<p>&nbsp;</p>\n" + 
                "<ul style=\"color: #4a4a4a; font-family: Verdana,&amp;quot;\">\n" + 
                "<li style=\"color: #4a4a4a; font-family: Verdana,&amp;quot;\"><span style=\"color: #339966; font-family: Verdana,&amp;quot;\">Open Requests:</span> Pending for Approval</li>\n" + 
                "<li style=\"color: #4a4a4a; font-family: Verdana,&amp;quot;\"><span style=\"color: #ff0000; font-family: Verdana,&amp;quot;\">Excesses: </span>actual Excesses</li>\n" + 
                "<li style=\"color: #4a4a4a; font-family: Verdana,&amp;quot;\">Condition: actual Special Condition for the client</li>\n" + 
                "<li style=\"color: #4a4a4a; font-family: Verdana,&amp;quot;\"><span style=\"color: #0000ff; font-family: Verdana,&amp;quot;\">Consolidation Level:</span> client within a Hierarchy</li>\n" + 
                "<li style=\"color: #4a4a4a; font-family: Verdana,&amp;quot;\">Calculation Error: Gives an indication of an incorrect position in the clients portfolio</li>\n" + 
                "</ul>\n" + 
                "<p>&nbsp;</p>\n" + 
                "<p><strong>Details&nbsp;</strong></p>\n" + 
                "<p><strong>Total Lombard</strong></p>\n" + 
                "<ul>\n" + 
                "<li>Total Lombard&nbsp;: own Portfolio\n" + 
                "<ul>\n" + 
                "<li>Limits&nbsp;: Lombard, Fix Unsecured, Fix Secured others, Fix Secured by Property</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "<li>Content of Clients Portfolio&nbsp;: e.g. Securities, Cash, etc.</li>\n" + 
                "<li>Total Lombard Available\n" + 
                "<ul>\n" + 
                "<li>Available amount after deduction of exposure\n" + 
                "<ul>\n" + 
                "<li>The calculation depends on the Limite type&nbsp;:\n" + 
                "<ul>\n" + 
                "<li>Lombard Limit&nbsp;: Lending Value &ndash; Exposure</li>\n" + 
                "<li>Product Limits (within Lombard Limit):\n" + 
                "<ul>\n" + 
                "<li><strong>Restricted Product Utilisation (U)</strong> Utilisation of credit limit is restricted to risk position(s) in specified product(s). In case of utilisation in any other not specified product a restricted product excess will be created. The available margin will not be affected.</li>\n" + 
                "<li><strong>Product Ceiling (C)</strong> Specific product(s) shall be limited to an amount. The unused part of the limit, and even the unused part of the product limit, is available to all other,&nbsp; not selected products. Any exposure exceeding the product ceiling will cause a product excess. All other product utilisation is supervised on the overall limit. The available margin will not be affected.</li>\n" + 
                "<li><strong>Reservation for selected Products (R)</strong> Amount of product limit(s) will be reserved and reduces the availble margin accordingly.</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "<li>Fix Limits&nbsp;: Valid Limit + Lending Value - Exposure</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "</ul>\n" + 
                "<p>&nbsp;</p>\n" + 
                "<p><strong>Total Pledge</strong></p>\n" + 
                "<ul>\n" + 
                "<li>Connections are shown with arrows next to the accounts.</li>\n" + 
                "<li>Further Collateral from third parties</li>\n" + 
                "<li><strong>The pledgor is allowed to pledge only his own assets.</strong></li>\n" + 
                "<li>There are three Types of Portfolio:\n" + 
                "<ul>\n" + 
                "<li><strong>Single Portfolio&nbsp;: </strong>no calculated connection to other clients. If the client has more than one account (Hierarchy), assets and liabilities are consolidated.</li>\n" + 
                "<li><strong>Pledge Portfolio</strong> = one-sided Third Party Liabilities</li>\n" + 
                "<li><strong>Group Portfolio </strong>= Mutual Third Party Liabilities</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "<li>Further Pledges&nbsp;:\n" + 
                "<ul>\n" + 
                "<li><strong>Pledge for Mortgages&nbsp;: </strong>Pledge for own or third Mortgages</li>\n" + 
                "<li><strong>Reservation&nbsp;: </strong>a Reservation will reduce the Total Lombard Available&nbsp;; it is considered in the ARR-Calculation --&gt; see Portfolio Rating</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "</ul>\n" + 
                "<p style=\"padding-left: 90px;\"><span style=\"color: #ff0000;\">The whole amount of the Reservation will be taken into the calculations, independent if the client has enough availability!</span></p>\n" + 
                "<p>&nbsp;</p>\n" + 
                "<p>&nbsp;</p>\n" + 
                "<p><strong>Availabilities </strong><strong>(</strong><strong>own Portfolio)</strong></p>\n" + 
                "<p><strong>Available Margin</strong></p>\n" + 
                "<ul>\n" + 
                "<li><strong>Single</strong>\n" + 
                "<ul>\n" + 
                "<li><strong>Calculation:&nbsp;</strong>Total Lombard Available - Exposure</li>\n" + 
                "<li><strong>Specialities:</strong> normal case: Lombard Available is identical with Available Margin</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "<li><strong>Pledge (Single Third Party Liabilities)</strong>\n" + 
                "<ul>\n" + 
                "<li><strong>Calculation:&nbsp;</strong>Total Lombard Available + Total Pledge (positive or negative);&nbsp;<strong>Pledgor and Pledgee together</strong></li>\n" + 
                "<li><strong>Specialities:</strong> <strong>Pledgor</strong> --&gt;Only the amount will be deducted, which is used from the Pledgee. <strong>Pledgee</strong> --&gt; The max. available amount (Total Available of Pledgor or limited liability) will be given.</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "<li><strong>Group (Mutual Third Party Liabilities)</strong>\n" + 
                "<ul>\n" + 
                "<li><strong>Calculation: Pledge</strong> --&gt; Total Lombard Available + Total Pledge (positive or negative);&nbsp;<strong>whole group is considered; Fullpledge:&nbsp;</strong>Total Lombard Available + Total Pledge (positive or negative);&nbsp;<strong>only single figures</strong></li>\n" + 
                "<li><strong>Specialities: Pledge&nbsp;</strong>--&gt; Each partner (Pledgor and Pledgee) can take all into own account;&nbsp;<strong>Fullpledge&nbsp;</strong>--&gt; The availability of each partner is limited. Each partner receives only the part, which he needs to cover his exposure.</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "</ul>\n" + 
                "<p>&nbsp;</p>\n" + 
                "<p><strong>Available Margin Ratio</strong></p>\n" + 
                "<ul>\n" + 
                "<li><strong>Single/Pledge/Group</strong>\n" + 
                "<ul>\n" + 
                "<li><strong>Calculation:</strong> Lending Value - Exposure in relation to Lending Value</li>\n" + 
                "<li><strong>Specialities:</strong> The Available Margin Ratio will not be listed:\n" + 
                "<ul>\n" + 
                "<li>if the client has none exposure</li>\n" + 
                "<li>if the client has a Fix Secured others, Fix Unsecured Limit, Fix Secured by Property, Fix Secured by Insurance Policy</li>\n" + 
                "<li>If the Available Margin Ration will be negative</li>\n" + 
                "<li><strong style=\"color: #4a4a4a; font-family: Verdana,&amp;quot; lucida sans&amp;quot;,arial,helvetica,sans-serif; font-size: 11px; font-style: normal; font-variant: normal; font-weight: bold; letter-spacing: normal; orphans: 2; text-align: left; text-decoration: none; text-indent: 0px; text-transform: none; -webkit-text-stroke-width: 0px; white-space: normal; word-spacing: 0px;\"><span style=\"color: #ff0000; font-family: Verdana,&amp;quot;\">The Available Margin Ratio will be written in red, as soon as the value drops below 10%: Early Warning&nbsp;!</span></strong></li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "</ul>\n" + 
                "<p>&nbsp;</p>\n" + 
                "<p><strong style=\"text-align: left; text-transform: none; text-indent: 0px; letter-spacing: normal; font-size: 11px; font-style: normal; font-variant: normal; font-weight: bold; text-decoration: none; word-spacing: 0px; white-space: normal; orphans: 2; -webkit-text-stroke-width: 0px; background-color: #ffffff;\">Available Limit</strong></p>\n" + 
                "<ul>\n" + 
                "<li><strong style=\"text-align: left; text-transform: none; text-indent: 0px; letter-spacing: normal; font-size: 11px; font-style: normal; font-variant: normal; font-weight: bold; text-decoration: none; word-spacing: 0px; white-space: normal; orphans: 2; -webkit-text-stroke-width: 0px; background-color: #ffffff;\">Single/Pledge/Group</strong>\n" + 
                "<ul>\n" + 
                "<li><strong style=\"text-align: left; text-transform: none; text-indent: 0px; letter-spacing: normal; font-size: 11px; font-style: normal; font-variant: normal; font-weight: bold; text-decoration: none; word-spacing: 0px; white-space: normal; orphans: 2; -webkit-text-stroke-width: 0px; background-color: #ffffff;\">Calculation:</strong> Valid Limit - Exposure</li>\n" + 
                "<li><strong>Specialities:&nbsp;</strong>For Group --&gt; only single partner mentioned</li>\n" + 
                "</ul>\n" + 
                "</li>\n" + 
                "</ul>\n" + 
                "<p>&nbsp;</p>\n" + 
                "<p><strong>Property Construction Limit, Mortgage, Fix Secured by Insurance Policy</strong><strong>&nbsp;</strong></p>\n" + 
                "<ul>\n" + 
                "<li>Both Limit and their exposures are shown and calculated separately.</li>\n" + 
                "</ul>\n" + 
                "<p>&nbsp;</p>\n" + 
                "<p><strong>Pledge for Mortgage</strong></p>\n" + 
                "<ul>\n" + 
                "<li>Pledge for Mortgage <strong>given</strong> --&gt; included in the Pledge calculation</li>\n" + 
                "<li>Pledge for Mortgage <strong>receive</strong> --&gt; included in the Mortgage calculation</li>\n" + 
                "<li>Lombard Limit is mandatory.</li>\n" + 
                "<li>Insufficient Coverage for Mortgage&nbsp;:</li>\n" + 
                "</ul>\n" + 
                "<p>If the Pledgor has not enough availability to cover the Pledge for Mortgage, an excess will occur in the Excess Management.</p>\n" + 
                "<p>The Excess will listed twice&nbsp;: on the Mortgage side (Mortgage Officer has to monitor) and on the Lombard side (Credit Officer has to monitor) --&gt; further Information see Limit Management.</p>\n" + 
                "<p>&nbsp;</p>\n" + 
                "<p><strong>Personal Guarantee</strong></p>\n" + 
                "<ul>\n" + 
                "<li>A Personal Guarantee is granted from a third party (client or non-client).</li>\n" + 
                "<li>It has no impact on the availability ot the client&rsquo;s portfolio.</li>\n" + 
                "</ul>\n" + 
                "<p>&nbsp;</p>\n" + 
                "<p>&nbsp;</p>\n" + 
                "<p>2020-02-03</p>"));
    }
    
    
    public static String clearHtml(String html){
        final StringBuilder sb = new StringBuilder();
        HTMLEditorKit.ParserCallback parserCallback = new HTMLEditorKit.ParserCallback() {
            public boolean readyForNewline;

            @Override public void handleText(final char[] data, final int pos) {
                String s = new String(data);
                sb.append(s.trim());
                readyForNewline = true;
            }
            @Override public void handleStartTag(final HTML.Tag t, final MutableAttributeSet a, final int pos) {
                if (readyForNewline && (t == HTML.Tag.DIV || t == HTML.Tag.BR || t == HTML.Tag.P || t == HTML.Tag.LI)) {
                    sb.append("\n");
                    readyForNewline = false;
                }
            }
            @Override public void handleSimpleTag(final HTML.Tag t, final MutableAttributeSet a, final int pos) {
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
    
}
