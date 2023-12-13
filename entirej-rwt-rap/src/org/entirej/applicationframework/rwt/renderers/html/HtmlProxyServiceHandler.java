package org.entirej.applicationframework.rwt.renderers.html;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.service.ServiceHandler;

public class HtmlProxyServiceHandler implements ServiceHandler
{
    public final static String SERVICE_HANDLER = "HtmlProxyServiceHandler";

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        String req_id = request.getParameter("req_id");
        // Get the file content
        byte[] download = HtmlProxy.INSTANCE.regs.get(req_id).html().getBytes("UTF-8");
        // Send the file in the response
        response.setContentType("text/html;charset=UTF-8");
        response.setContentLength(download.length);
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        response.getOutputStream().write(download);
    }

    public static class HtmlProxy
    {
        public static final HtmlProxy INSTANCE = new HtmlProxy(); 
        
        private Map<String, HtmlGet> regs = new HashMap<>();

        public String register(HtmlGet provilder)
        {
            String randomUUID = UUID.randomUUID().toString();
            regs.put(randomUUID.toString(), provilder);
            return randomUUID;
        }

        public HtmlGet unregister(String key)
        {
            return regs.remove(key);
        }

    }

    public static interface HtmlGet
    {
        String html();
    }
}