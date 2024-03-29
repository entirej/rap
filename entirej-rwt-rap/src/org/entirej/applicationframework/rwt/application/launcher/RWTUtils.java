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
package org.entirej.applicationframework.rwt.application.launcher;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
        //System.err.println(builder.toString());
        executor.execute(errorBoxPatch(timeoutPage).toString());
        executor.execute(getWeekStartPatch().toString());
       
        
    }

    private static StringBuilder errorBoxPatch(String timeoutPage)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("(function() {");
        builder.append("console.log('patchClient-errorBoxPatch');");
        builder.append(" var errorHandler = rwt.runtime.ErrorHandler;");
        builder.append(" var origShowErrorBox = rwt.util.Functions.bind(errorHandler.showErrorBox, errorHandler );");
        builder.append(" errorHandler.showErrorBox = function( errorType,freeze, errorDetails ) {");
        builder.append("console.log(errorType +' - '+errorDetails);");
        //enable if IE issue with connections       
//        builder.append(" if( errorType === \"connection error\" ) {");
//        builder.append(" settimeout( function() {");
//        builder.append(" rwt.remote.Connection.getInstance()._retry();");
//        builder.append(" }, 100 );} else");
        builder.append(" if( errorType === \"session timeout\" || errorType === \"client error\"  || errorType === \"connection error\") {");
        if(timeoutPage==null || timeoutPage.isEmpty())
            builder.append(" parent.window.location.href = this._getRestartURL();");
        else
            builder.append(" parent.window.location.href = \"").append(timeoutPage).append("\";");   
        builder.append("                                            ");
        builder.append("                                            ");
        builder.append("                                            ");
        builder.append("                                            ");
        builder.append(" } else {");
        builder.append(" origShowErrorBox( errorType, freeze,errorDetails );");
        builder.append(" }");
        builder.append(" };");
        builder.append("}() );");
        return builder;
    }
    
    private static StringBuilder getWeekStartPatch()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("(function() {");
        builder.append("console.log('patchClient-getTerritory');");
        builder.append(" var client = rwt.client.Client;");
        builder.append(" client.getTerritory = function(  ) {");
        builder.append("console.log('getWeekStartPatch');");
        builder.append("return ''};}() );");
        return builder;
    }
    

    
     public static void main(String[] args) throws UnknownHostException
    {
        System.out.println(InetAddress.getLocalHost().getHostName());
    }
    
    
 
    
    
    

}
