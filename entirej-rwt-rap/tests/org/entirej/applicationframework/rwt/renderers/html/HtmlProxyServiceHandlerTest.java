package org.entirej.applicationframework.rwt.renderers.html;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.entirej.applicationframework.rwt.renderers.html.HtmlProxyServiceHandler.HtmlGet;
import org.entirej.applicationframework.rwt.renderers.html.HtmlProxyServiceHandler.HtmlProxy;
import org.junit.Test;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HtmlProxyServiceHandlerTest
{
    @Test
    public void owningSessionCanReadUtf8HtmlAndRunItsAction() throws Exception
    {
        Session session = new Session();
        HtmlProxy proxy = new HtmlProxy(() -> session.value);
        Provider provider = new Provider();
        String key = proxy.register(provider);
        Response response = request(proxy, "GET", key, "OPEN_DETAIL", null);

        assertEquals(200, response.status);
        assertEquals("OPEN_DETAIL", provider.lastAction);
        assertEquals(1, provider.actions);
        assertEquals("<b>caf\u00e9</b>", response.body.toString(StandardCharsets.UTF_8));
        assertEquals(response.body.size(), response.contentLength);
        assertEquals("text/html;charset=UTF-8", response.contentType);
        assertEquals("frame-ancestors 'self';", response.headers.get("Content-Security-Policy"));
        assertTrue(response.headers.get("Cache-Control").contains("no-store"));
    }

    @Test
    public void anotherUiSessionCannotReadExecuteOrUnregisterTheOwnersToken() throws Exception
    {
        Session owner = new Session();
        Session other = new Session();
        AtomicReference<UISession> current = new AtomicReference<>(owner.value);
        HtmlProxy proxy = new HtmlProxy(current::get);
        Provider provider = new Provider();
        String key = proxy.register(provider);

        current.set(other.value);
        assertEquals(404, request(proxy, "GET", key, null, null).status);
        assertEquals(404, request(proxy, "POST", key, "DELETE", null).status);
        assertNull(proxy.unregister(key));
        assertEquals(0, provider.actions);
        assertEquals(0, provider.reads);
        assertTrue(other.attributes.isEmpty());

        current.set(owner.value);
        assertEquals(200, request(proxy, "GET", key, null, null).status);
    }

    @Test
    public void missingAndUnknownTokensReturnNotFoundWithoutCreatingARegistry() throws Exception
    {
        Session session = new Session();
        HtmlProxy proxy = new HtmlProxy(() -> session.value);
        assertEquals(404, request(proxy, "GET", null, null, null).status);
        assertEquals(404, request(proxy, "GET", "unknown", "ACTION", null).status);
        assertTrue(session.attributes.isEmpty());
    }

    @Test
    public void invalidCommandsAndCrossSiteActionsNeverReachTheProvider() throws Exception
    {
        Session session = new Session();
        HtmlProxy proxy = new HtmlProxy(() -> session.value);
        Provider provider = new Provider();
        String key = proxy.register(provider);
        for (String action : List.of("", "  ", "bad\ncommand", "bad\u0000command", "x".repeat(257)))
        {
            assertEquals(400, request(proxy, "GET", key, action, null).status);
        }
        assertEquals(403, request(proxy, "GET", key, "ACTION", "cross-site").status);
        assertEquals(405, request(proxy, "DELETE", key, "ACTION", null).status);
        assertEquals(0, provider.actions);
        assertEquals(0, provider.reads);
    }

    @Test
    public void closingRegistrationIsIdempotentAndIndependentOfCurrentSession() throws Exception
    {
        Session session = new Session();
        AtomicReference<UISession> current = new AtomicReference<>(session.value);
        HtmlProxy proxy = new HtmlProxy(current::get);
        var registration = proxy.registerResource(new Provider());
        assertTrue(registration.isActive());
        current.set(null);
        registration.close();
        registration.close();
        assertFalse(registration.isActive());
        current.set(session.value);
        assertEquals(404, request(proxy, "GET", registration.getKey(), null, null).status);
    }

    @Test
    public void sessionDestructionClearsAllRegistrationsWithOneListener() throws Exception
    {
        Session session = new Session();
        HtmlProxy proxy = new HtmlProxy(() -> session.value);
        String first = proxy.register(new Provider());
        String second = proxy.register(new Provider());
        assertEquals(1, session.listeners.size());
        session.listeners.get(0).beforeDestroy(null);
        // Even before HttpSession unbinding completes, the destroyed registry stays closed.
        assertNull(proxy.lookup(first));
        assertNull(proxy.lookup(second));
        assertThrows(IllegalStateException.class, () -> proxy.register(new Provider()));
        session.bound = false;
        assertEquals(404, request(proxy, "GET", first, "ACTION", null).status);
    }

    @Test
    public void nullHtmlIsReportedAsNotFound() throws Exception
    {
        Session session = new Session();
        HtmlProxy proxy = new HtmlProxy(() -> session.value);
        String key = proxy.register(new HtmlGet()
        {
            public String html() { return null; }
            public void action(String action) { }
        });
        assertEquals(404, request(proxy, "GET", key, null, null).status);
    }

    private static Response request(HtmlProxy proxy, String method, String key, String action, String fetchSite) throws Exception
    {
        HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(), new Class<?>[] { HttpServletRequest.class },
                (instance, called, args) -> switch (called.getName()) {
                    case "getMethod" -> method;
                    case "getParameter" -> "req_id".equals(args[0]) ? key : "action".equals(args[0]) ? action : null;
                    case "getHeader" -> "Sec-Fetch-Site".equals(args[0]) ? fetchSite : null;
                    default -> null;
                });
        Response response = new Response();
        new HtmlProxyServiceHandler(proxy).service(request, response.value);
        return response;
    }

    private static final class Provider implements HtmlGet
    {
        int actions;
        int reads;
        String lastAction;

        public String html() { reads++; return "<b>caf\u00e9</b>"; }
        public void action(String action) { actions++; lastAction = action; }
    }

    private static final class Session
    {
        final Map<String, Object> attributes = new HashMap<>();
        final List<UISessionListener> listeners = new ArrayList<>();
        boolean bound = true;
        final UISession value = (UISession) Proxy.newProxyInstance(UISession.class.getClassLoader(),
                new Class<?>[] { UISession.class }, (instance, method, args) -> switch (method.getName()) {
                    case "isBound" -> bound;
                    case "getAttribute" -> attributes.get(args[0]);
                    case "setAttribute" -> { attributes.put((String) args[0], args[1]); yield bound; }
                    case "addUISessionListener" -> { listeners.add((UISessionListener) args[0]); yield bound; }
                    default -> null;
                });
    }

    private static final class Response
    {
        int status = 200;
        int contentLength;
        String contentType;
        final Map<String, String> headers = new HashMap<>();
        final ByteArrayOutputStream body = new ByteArrayOutputStream();
        final ServletOutputStream output = new ServletOutputStream()
        {
            public void write(int value) { body.write(value); }
            public boolean isReady() { return true; }
            public void setWriteListener(WriteListener listener) { }
        };
        final HttpServletResponse value = (HttpServletResponse) Proxy.newProxyInstance(HttpServletResponse.class.getClassLoader(),
                new Class<?>[] { HttpServletResponse.class }, (instance, method, args) -> {
                    switch (method.getName())
                    {
                        case "setHeader": headers.put((String) args[0], (String) args[1]); break;
                        case "sendError": status = (Integer) args[0]; break;
                        case "setContentLength": contentLength = (Integer) args[0]; break;
                        case "setContentType": contentType = (String) args[0]; break;
                        case "getOutputStream": return output;
                        default: break;
                    }
                    return null;
                });
    }
}
