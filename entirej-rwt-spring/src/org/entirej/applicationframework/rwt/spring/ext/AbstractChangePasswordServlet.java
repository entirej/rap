package org.entirej.applicationframework.rwt.spring.ext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

public abstract class AbstractChangePasswordServlet extends AbstractTemplateServlet
{

    private static final long serialVersionUID = 7535581804395759866L;

    protected Map<String, String> getVariables(HttpServletRequest request)
    {
        Map<String, String> variables = new HashMap<String, String>();

        variables.put("title", "Change User Password");
        variables.put("header", "Change Password for <"+getUser( request)+">");
        variables.put("path", request.getContextPath() + getPath());
        variables.put("error-text", asText(request));

        return variables;
    }


    private String asText(HttpServletRequest request)
    
    {
        Object attribute = request.getSession(true).getAttribute("error-text");
        return attribute!=null ? attribute.toString():"";
    }


    protected abstract String getPath();
    
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String current_password = request.getParameter("current_password");
        String new_password = request.getParameter("new_password");
        String conform_password = request.getParameter("conform_password");
        
        
        if(current_password==null || !validatePassword(request,response,  current_password))
        {
            response.sendRedirect(request.getContextPath() + getPath()+"?error-incorrect");
            return;
        }
        if(new_password==null || !new_password.equals(conform_password))
        {
            response.sendRedirect(request.getContextPath() + getPath()+"?error-miss-match");
            return;
        }
        String errorCode = checkFormat(request,response,current_password, new_password);
        if(errorCode!=null)
        {
            response.sendRedirect(request.getContextPath() + getPath()+"?"+errorCode);
            return;
        }
        
        
        try {
            updatePassword(request,response, new_password);
            //if all correctly updated 
            request.logout();
            response.sendRedirect(request.getContextPath() + getRedirectPatch());
        }catch (Throwable t) {
            request.getSession(true).setAttribute("error-text",t.getMessage());
            response.sendRedirect(request.getContextPath() + getPath()+"?error-other");
        }
        
        
    }


    protected String getRedirectPatch()
    {
        return "/login?password-updated";
    }



    protected abstract void updatePassword(HttpServletRequest request, HttpServletResponse response,String new_password);
   


    protected abstract String checkFormat(HttpServletRequest request, HttpServletResponse response, String current_password,String new_password);


    protected abstract boolean validatePassword(HttpServletRequest request, HttpServletResponse response,String current_password);


    protected abstract String getUser(HttpServletRequest request);
    

    public  Authentication getAuthentication(HttpServletRequest request)
    {

        SecurityContext context = getSecurityContext(request);
        if (context != null)
        {
            return context.getAuthentication();
        }

        return null;
    }
    
    public  SecurityContext getSecurityContext(HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);
        SecurityContext context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        return context;
    }
    
    
    @Override
    protected String getTemplatePath()
    {
        return "change-password.html";
    }
    

}
