/*******************************************************************************
 * Copyright 2013 Mojave Innovations GmbH
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
 * Contributors: Mojave Innovations GmbH - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.application.launcher;

import java.io.InputStream;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.internal.textsize.TextSizeUtil;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class RWTUtils
{

    public static Image getImage(String name, ClassLoader loader)
    {
        InputStream resourceAsStream = loader.getResourceAsStream(name);
        if (resourceAsStream == null)
        {
            return null;
        }
        return new Image(Display.getDefault(), resourceAsStream);

    }

    public static float getAvgCharWidth(Font font)
    {
        return TextSizeUtil.getAvgCharWidth(font);
    }

    public static int getCharHeight(Font font)
    {
        return TextSizeUtil.getCharHeight(font);
    }

    public static void patchClient(String conext,String timeoutPage)
    {
        //https://github.com/eclipse/rap/blob/master/bundles/org.eclipse.rap.rwt/js/rwt/runtime/ErrorHandler.js
        JavaScriptExecutor executor = RWT.getClient().getService(JavaScriptExecutor.class);
        StringBuilder builder = new StringBuilder();
        builder.append("(function() {");
        builder.append("console.log('patchClient');");
        builder.append(" var errorHandler = rwt.runtime.ErrorHandler;");
        builder.append(" var origShowErrorBox = rwt.util.Functions.bind(errorHandler.showErrorBox, errorHandler );");
        builder.append(" errorHandler.showErrorBox = function( errorType,freeze, errorDetails ) {");
        builder.append("console.log(errorType +' - '+errorDetails);");
        //enable if IE issue with connections       
        //        builder.append(" if( errorType === \"connection error\" ) {");
        //        builder.append(" settimeout( function() {");
        //        builder.append(" rwt.remote.Connection.getInstance()._retry();");
        //        builder.append(" }, 100 );} else");
        builder.append(" if( errorType === \"session timeout\" || errorType === \"client error\" ) {");
        builder.append(" parent.window.location.href = this._getRestartURL()");
        builder.append("                                            ");
        builder.append("                                            ");
        builder.append("                                            ");
        builder.append("                                            ");
        builder.append(" } else {");
        builder.append(" origShowErrorBox( errorType, freeze,errorDetails );");
        builder.append(" }");
        builder.append(" };");
        builder.append("}() );");
        //System.err.println(builder.toString());
        executor.execute(builder.toString());
        
    }

}
