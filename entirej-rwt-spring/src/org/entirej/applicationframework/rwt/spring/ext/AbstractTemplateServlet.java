package org.entirej.applicationframework.rwt.spring.ext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractTemplateServlet extends HttpServlet
{

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ServletContext context = request.getServletContext();

        InputStream resourceAsStream = context.getResourceAsStream(getTemplatePath());
        String loginPageHtml = convertStreamToString(resourceAsStream);
        loginPageHtml = substituteVariables(loginPageHtml, getVariables(request));
        response.setContentType(getContentType());
        response.setContentLength(loginPageHtml.length());
        response.getWriter().write(loginPageHtml);
    }

    protected String getContentType()
    {
        return "text/html;charset=UTF-8";
    }

    protected abstract String getTemplatePath();

    protected abstract Map<String, String> getVariables(HttpServletRequest request);

    protected static String substituteVariables(String template, Map<String, String> variables)
    {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(template);
        // StringBuilder cannot be used here because Matcher expects
        // StringBuffer
        StringBuffer buffer = new StringBuffer();
        while (matcher.find())
        {
            if (variables.containsKey(matcher.group(1)))
            {
                String replacement = variables.get(matcher.group(1));
                // quote to work properly with $ and {,} signs
                matcher.appendReplacement(buffer, replacement != null ? Matcher.quoteReplacement(replacement) : "null");
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    static String convertStreamToString(java.io.InputStream is)
    {
        if (is == null)
        {
            return "";
        }

        java.util.Scanner s = new java.util.Scanner(is);
        s.useDelimiter("\\A");

        String streamString = s.hasNext() ? s.next() : "";

        s.close();

        return streamString;
    }

}
