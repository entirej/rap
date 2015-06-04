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
package org.eclipse.rwt;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

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

    public static void setTestId(Widget widget, String value)
    {
        //TODO: add test mode
        if (!widget.isDisposed())
        {
            StartupParameters service = RWT.getClient().getService(StartupParameters.class);
            if(service== null || !Boolean.valueOf(service.getParameter("TEST_MODE")))
            {
                return;
            }
            
            String $el = widget instanceof Text ? "$input" : "$el";
            String id = WidgetUtil.getId(widget);
            exec("rap.getObject( '", id, "' ).", $el, ".attr( 'test-id', '", value + "' );");
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
}
