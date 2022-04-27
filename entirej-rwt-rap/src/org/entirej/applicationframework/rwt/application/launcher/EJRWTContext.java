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

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.SingletonUtil;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.component.EJRWTH2Canvas;
import org.entirej.framework.report.EJReportFrameworkManager;

public class EJRWTContext
{

    private static volatile ThreadLocal<EJReportFrameworkManager> reportLocal = new ThreadLocal<>();
    private Consumer<String> screenshotHandler;

    void setState(String state)
    {
        RWT.getUISession().setAttribute("state", state);
    }

    String getState()
    {
        return (String) RWT.getUISession().getAttribute("state");
    }

    public void setManager(EJRWTApplicationManager manager)
    {
        RWT.getUISession().setAttribute("ej.applicationManager", manager);
    }

    public EJRWTApplicationManager getManager()
    {
        return (EJRWTApplicationManager) RWT.getUISession().getAttribute("ej.applicationManager");
    }

    public static void setReportManager(EJReportFrameworkManager manager)
    {
        reportLocal.set(manager);
    }

    public static EJReportFrameworkManager getReportManager()
    {
        return reportLocal.get();
    }

    private EJRWTContext()
    {
        // keep private
    }

    public static EJRWTContext getPageContext()
    {
        return SingletonUtil.getSessionInstance(EJRWTContext.class);
    }

    public void screenshot(Consumer<String> callback)
    {
        EJRWTH2Canvas ejrwth2Canvas = new EJRWTH2Canvas();

        ejrwth2Canvas.screenshot(data -> {
            byte[] imagedata = java.util.Base64.getDecoder().decode(data.substring(data.indexOf(",") + 1));
            try
            {

                Path tempFile = Files.createTempFile("screenshot", ".png");
                try
                {
                    Files.write(tempFile, imagedata);
                    callback.accept(tempFile.toFile().getAbsolutePath());
                }
                finally
                {
                    // clear it
                    if (!tempFile.toFile().delete())
                        tempFile.toFile().deleteOnExit();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        });

    }
    public void setScreenshotHandler(Consumer<String> callback)
    {
        screenshotHandler= callback;
    }
    
    public void fireScreenshotHandler()
    {
        if(screenshotHandler!=null) {
            EJRWTH2Canvas ejrwth2Canvas = new EJRWTH2Canvas();
            
            ejrwth2Canvas.screenshot(data -> {
                byte[] imagedata = java.util.Base64.getDecoder().decode(data.substring(data.indexOf(",") + 1));
                try
                {
                    
                    Path tempFile = Files.createTempFile("screenshot", ".png");
                    try
                    {
                        Files.write(tempFile, imagedata);
                        screenshotHandler.accept(tempFile.toFile().getAbsolutePath());
                    }
                    finally
                    {
                        // clear it
                        if (!tempFile.toFile().delete())
                            tempFile.toFile().deleteOnExit();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                
            });
        }
    }

    
    public static EJRWTApplicationManager getEJRWTApplicationManager()
    {
        EJRWTContext pageContext = getPageContext();
        EJRWTApplicationManager manager = pageContext.getManager();
        return manager != null ? manager : (EJRWTApplicationManager) getContext().getUISession().getAttribute("ej.applicationManager");
    }

    public String getUrlParameter(String paramName)
    {

        String paramState = getState();
        if (paramState != null)
        {
            Map<String, String> queryMap = getQueryMap(paramState);
            return queryMap.get(paramName);

        }

        return null;
    }

    static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    public static String getClientIpFromHeader()
    {
        HttpServletRequest request = RWT.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
