package org.entirej.applicationframework.rwt.spring;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class EjRwtSpringAuthUtil
{
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
}
