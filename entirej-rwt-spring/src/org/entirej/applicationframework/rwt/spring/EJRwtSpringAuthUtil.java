package org.entirej.applicationframework.rwt.spring;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.entirej.applicationframework.rwt.spring.ext.EJSpringWebAuthenticationDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

public class EJRwtSpringAuthUtil
{

    @Deprecated
    public static void logout(String contextpath)
    {
        StringBuffer url = new StringBuffer();
        url.append(RWT.getRequest().getContextPath());
        url.append(RWT.getRequest().getServletPath());
        String encodeURL = RWT.getResponse().encodeURL(url.toString());
        if (encodeURL.contains("jsessionid"))
        {
            encodeURL = encodeURL.substring(0, encodeURL.indexOf("jsessionid"));
        }
        int patchIndex = encodeURL.lastIndexOf(contextpath);
        if (patchIndex > -1)
        {
            encodeURL = encodeURL.substring(0, patchIndex) + "logout";
        }
        String browserText = MessageFormat.format("parent.window.location.href = \"{0}\";", encodeURL);
        JavaScriptExecutor executor = RWT.getClient().getService(JavaScriptExecutor.class);
        if (executor != null)
        {
            executor.execute(browserText);
        }
    }

    public static void logout()
    {
        StringBuffer url = new StringBuffer();
        url.append(RWT.getRequest().getContextPath());

        String encodeURL = RWT.getResponse().encodeURL(url.toString());
        if (encodeURL.contains("jsessionid"))
        {
            encodeURL = encodeURL.substring(0, encodeURL.indexOf("jsessionid"));
        }
        
        encodeURL+= "logout";

        String browserText = MessageFormat.format("parent.window.location.href = \"{0}\";", encodeURL);
        JavaScriptExecutor executor = RWT.getClient().getService(JavaScriptExecutor.class);
        if (executor != null)
        {
            executor.execute(browserText);
        }
    }
    public static void logoutAndRerirect(String redirect)
    {
        StringBuffer url = new StringBuffer();
        url.append(RWT.getRequest().getContextPath());
        
        String encodeURL = RWT.getResponse().encodeURL(url.toString());
        if (encodeURL.contains("jsessionid"))
        {
            encodeURL = encodeURL.substring(0, encodeURL.indexOf("jsessionid"));
        }
        
        encodeURL+= "logout";
        
        String browserText = MessageFormat.format("parent.window.location.href = \"{0}\";", encodeURL);
        JavaScriptExecutor executor = RWT.getClient().getService(JavaScriptExecutor.class);
        if (executor != null)
        {
            executor.execute(browserText);
        }
    }

    public static String getRemoteUser()
    {

        SecurityContext context = getSecurityContext();
        if (context != null)
        {
            return context.getAuthentication().getName();
        }

        return null;
    }

    public static Authentication getAuthentication()
    {

        SecurityContext context = getSecurityContext();
        if (context != null)
        {
            return context.getAuthentication();
        }

        return null;
    }

    public static SecurityContext getSecurityContext()
    {
        HttpSession session = RWT.getRequest().getSession(true);
        SecurityContext context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        return context;
    }

    public static String getUrlParameter(String paramName)
    {

        Object details = getSecurityContext().getAuthentication().getDetails();
        if (details instanceof EJSpringWebAuthenticationDetails)
        {

            String state = ((EJSpringWebAuthenticationDetails) details).getQueryString();
            
            if (state != null && state.startsWith("#"))
            {
                state = state.substring(1);
            }

            String paramState = state;
            if (paramState != null && !paramState.trim().isEmpty())
            {
                Map<String, String> queryMap = getQueryMap(paramState);
                return queryMap.get(paramName);

            }
        }

        return null;
    }

    static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String[] split = param.split("=");
            if (split.length == 2)
            {
                String name = split[0];
                String value = split[1];
                map.put(name, value);
            }
        }
        return map;
    }

}
