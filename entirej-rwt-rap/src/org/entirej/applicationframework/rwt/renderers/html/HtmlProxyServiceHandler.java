package org.entirej.applicationframework.rwt.renderers.html;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HtmlProxyServiceHandler implements ServiceHandler
{
    public final static String SERVICE_HANDLER = "HtmlProxyServiceHandler";

    private final HtmlProxy proxy;

    public HtmlProxyServiceHandler()
    {
        this(HtmlProxy.INSTANCE);
    }

    HtmlProxyServiceHandler(HtmlProxy proxy)
    {
        this.proxy = proxy;
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        response.setHeader("Content-Security-Policy", "frame-ancestors 'self';");
        response.setHeader("X-Content-Type-Options", "nosniff");

        if (!"GET".equals(request.getMethod()) && !"POST".equals(request.getMethod()))
        {
            response.setHeader("Allow", "GET, POST");
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        HtmlGet provider = proxy.lookup(request.getParameter("req_id"));
        if (provider == null)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String action = request.getParameter("action");
        if (action != null && (action.isBlank() || action.length() > 256
                || action.codePoints().anyMatch(Character::isISOControl)))
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        // Keep existing HTML action links working, but reject cross-site action requests.
        if (action != null && "cross-site".equals(request.getHeader("Sec-Fetch-Site")))
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String html = provider.render(action);
        if (html == null)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        byte[] download = html.getBytes(StandardCharsets.UTF_8);
        response.setContentType("text/html;charset=UTF-8");
        response.setContentLength(download.length);
        response.getOutputStream().write(download);
    }

    public static class HtmlProxy
    {
        public static final HtmlProxy INSTANCE = new HtmlProxy();
        private static final String REGISTRY_ATTRIBUTE = HtmlProxy.class.getName() + ".registry";
        private final Supplier<UISession> currentSession;

        public HtmlProxy()
        {
            this(RWT::getUISession);
        }

        HtmlProxy(Supplier<UISession> currentSession)
        {
            this.currentSession = currentSession;
        }

        public String register(HtmlGet provider)
        {
            return registerResource(provider).getKey();
        }

        public Registration registerResource(HtmlGet provider)
        {
            Objects.requireNonNull(provider, "HTML provider");
            Registry registry = registry(true);
            if (registry == null)
            {
                throw new IllegalStateException("Cannot register HTML for a destroyed UI session");
            }
            return registry.register(provider);
        }

        public HtmlGet unregister(String key)
        {
            Registry registry = registry(false);
            return registry == null ? null : registry.remove(key);
        }

        HtmlGet lookup(String key)
        {
            Registry registry = registry(false);
            return registry == null ? null : registry.get(key);
        }

        private Registry registry(boolean create)
        {
            UISession session = currentSession.get();
            synchronized (session)
            {
                if (!session.isBound())
                {
                    return null;
                }
                Registry registry = (Registry) session.getAttribute(REGISTRY_ATTRIBUTE);
                if (registry == null && create)
                {
                    registry = new Registry();
                    if (!session.setAttribute(REGISTRY_ATTRIBUTE, registry) || !session.addUISessionListener(registry))
                    {
                        registry.close();
                        return null;
                    }
                }
                return registry;
            }
        }
    }

    public static final class Registration implements AutoCloseable
    {
        private final Registry registry;
        private final String key;

        private Registration(Registry registry, String key)
        {
            this.registry = registry;
            this.key = key;
        }

        public String getKey()
        {
            return key;
        }

        public boolean isActive()
        {
            return registry.get(key) != null;
        }

        @Override
        public void close()
        {
            // Closing a registration must not depend on an ambient RAP request context.
            registry.remove(key);
        }
    }

    private static final class Registry implements UISessionListener
    {
        private static final long serialVersionUID = 1L;
        private final Map<String, HtmlGet> providers = new HashMap<>();
        private boolean closed;

        synchronized Registration register(HtmlGet provider)
        {
            if (closed)
            {
                throw new IllegalStateException("Cannot register HTML for a destroyed UI session");
            }
            String key = UUID.randomUUID().toString();
            providers.put(key, provider);
            return new Registration(this, key);
        }

        synchronized HtmlGet get(String key)
        {
            return key == null || closed ? null : providers.get(key);
        }

        synchronized HtmlGet remove(String key)
        {
            return providers.remove(key);
        }

        @Override
        public void beforeDestroy(UISessionEvent event)
        {
            close();
        }

        synchronized void close()
        {
            closed = true;
            providers.clear();
        }
    }

    public static interface HtmlGet
    {
        String html();
        void action(String action);

        default String render(String action)
        {
            if (action != null)
            {
                action(action);
            }
            return html();
        }
    }
}
