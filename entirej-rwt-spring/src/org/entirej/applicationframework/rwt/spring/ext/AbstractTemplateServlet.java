package org.entirej.applicationframework.rwt.spring.ext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class AbstractTemplateServlet extends HttpServlet
{

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ServletContext context = request.getServletContext();

        InputStream resourceAsStream = context.getResourceAsStream(getTemplatePath());
        String loginPageHtml = convertStreamToString(resourceAsStream);
        loginPageHtml = substituteVariables(loginPageHtml, getVariables(request));
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(getContentType());
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
                matcher.appendReplacement(buffer, replacement != null ? Matcher.quoteReplacement(escapeHtml(replacement)) : "null");
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    static String escapeHtml(String value)
    {
        StringBuilder escaped = new StringBuilder(value.length());
        value.codePoints().forEach(codePoint -> {
            switch (codePoint)
            {
                case '&':
                    escaped.append("&amp;");
                    break;
                case '<':
                    escaped.append("&lt;");
                    break;
                case '>':
                    escaped.append("&gt;");
                    break;
                case '"':
                    escaped.append("&quot;");
                    break;
                case '\'':
                    escaped.append("&#39;");
                    break;
                default:
                    escaped.appendCodePoint(codePoint);
                    break;
            }
        });
        return escaped.toString();
    }

    static String convertStreamToString(java.io.InputStream is)
    {
        if (is == null)
        {
            return "";
        }

        java.util.Scanner s = new java.util.Scanner(is, StandardCharsets.UTF_8);
        s.useDelimiter("\\A");

        String streamString = s.hasNext() ? s.next() : "";

        s.close();

        return streamString;
    }

}
