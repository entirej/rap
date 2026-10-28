package org.entirej.applicationframework.rwt.renderers.html;

import static org.junit.Assert.*;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.serverpush.ServerPushManager;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EJRWTExtHtmlViewItemRendererTest
{
    @Rule
    public TestContext context = new TestContext();

    private Shell shell;

    @Before
    public void setUp()
    {
        shell = new Shell(new Display());
        RWT.getServiceManager().registerServiceHandler(HtmlProxyServiceHandler.SERVICE_HANDLER, new HtmlProxyServiceHandler());
    }

    @Test
    public void replacementAndClearInvalidateOldTokensAndCapturedProviders()
    {
        Renderer renderer = new Renderer(shell);
        renderer.setValue("first %RAP_URL_PATH%");
        String firstKey = renderer.key();
        var first = HtmlProxyServiceHandler.HtmlProxy.INSTANCE.lookup(firstKey);
        assertTrue(first.render(null).contains(firstKey));

        renderer.setValue("second");
        String secondKey = renderer.key();
        assertNotEquals(firstKey, secondKey);
        assertNull(HtmlProxyServiceHandler.HtmlProxy.INSTANCE.lookup(firstKey));
        assertNull(first.render("STALE_ACTION"));
        assertEquals("second", HtmlProxyServiceHandler.HtmlProxy.INSTANCE.lookup(secondKey).render(null));

        renderer.clearValue();
        assertNull(renderer.key());
        assertNull(HtmlProxyServiceHandler.HtmlProxy.INSTANCE.lookup(secondKey));
        assertNull(renderer.getValue());
        assertEquals("about:blank", ((Browser) renderer.getGuiComponent()).getUrl());
    }

    @Test
    public void browserDisposalUnregistersAndReleasesContent()
    {
        Renderer renderer = new Renderer(shell);
        renderer.setValue("dispose me");
        String key = renderer.key();
        var provider = HtmlProxyServiceHandler.HtmlProxy.INSTANCE.lookup(key);
        renderer.getGuiComponent().dispose();

        assertNull(renderer.key());
        assertNull(renderer.getValue());
        assertNull(HtmlProxyServiceHandler.HtmlProxy.INSTANCE.lookup(key));
        assertNull(provider.render("STALE_ACTION"));
    }

    @Test
    public void emptyValueReleasesRegistrationAndPreCreationValueIsPreserved()
    {
        Renderer renderer = new Renderer();
        renderer.setValue("before browser");
        assertEquals("before browser", renderer.getValue());
        assertNull(renderer.key());
        renderer.create(shell);
        renderer.setValue(renderer.getValue());
        String key = renderer.key();
        assertNotNull(key);
        renderer.setValue(null);
        assertNull(renderer.key());
        assertNull(HtmlProxyServiceHandler.HtmlProxy.INSTANCE.lookup(key));
    }

    @Test
    public void emptyInitialValueUsesAboutBlankWithoutFetchingAServerResource()
    {
        Renderer renderer = new Renderer(shell);
        renderer.setValue(null);

        assertNull(renderer.key());
        assertEquals("about:blank", ((Browser) renderer.getGuiComponent()).getUrl());
        renderer.setValue("");
        assertEquals("about:blank", ((Browser) renderer.getGuiComponent()).getUrl());
    }

    @Test
    public void revokedRegistrationCannotServeOrActEvenIfProviderWasAlreadyCaptured()
    {
        Renderer renderer = new Renderer(shell);
        renderer.setValue("revoke me");
        String key = renderer.key();
        var provider = HtmlProxyServiceHandler.HtmlProxy.INSTANCE.lookup(key);
        HtmlProxyServiceHandler.HtmlProxy.INSTANCE.unregister(key);

        assertNull(provider.render(null));
        // A stale action must never reach the item's controller (unset in this fixture).
        assertNull(provider.render("STALE_ACTION"));
    }

    @Test
    public void firstReadAfterEmptyValueReturnsWithoutAnotherUiRequest() throws Exception
    {
        Renderer renderer = new Renderer(shell);
        renderer.setValue(null);
        renderer.setValue("from request thread");
        var provider = HtmlProxyServiceHandler.HtmlProxy.INSTANCE.lookup(renderer.key());
        FutureTask<String> render = new FutureTask<>(() -> provider.render(null));
        Thread request = new Thread(render, "html-proxy-test-request");
        request.setDaemon(true);
        request.start();
        try
        {
            // Do not pump the display here: the user must not have to click or scroll.
            assertEquals("from request thread", render.get(1, TimeUnit.SECONDS));
        }
        finally
        {
            // Release a queued call on failure so it cannot outlive this test's context.
            while (shell.getDisplay().readAndDispatch()) { }
            render.cancel(true);
        }
    }

    @Test
    public void serverPushIsScopedToTheRegisteredHtmlView()
    {
        Renderer renderer = new Renderer(shell);
        assertFalse(ServerPushManager.getInstance().isServerPushActive());
        renderer.setValue("first");
        assertTrue(ServerPushManager.getInstance().isServerPushActive());
        renderer.setValue("replacement");
        assertTrue(ServerPushManager.getInstance().isServerPushActive());
        renderer.clearValue();
        assertFalse(ServerPushManager.getInstance().isServerPushActive());
        renderer.setValue("dispose");
        assertTrue(ServerPushManager.getInstance().isServerPushActive());
        renderer.getGuiComponent().dispose();
        assertFalse(ServerPushManager.getInstance().isServerPushActive());
    }

    @Test
    public void actionRunsOnceOnTheUiThreadAndCanReplaceContent() throws Exception
    {
        Renderer renderer = new Renderer(shell);
        renderer.setValue("before action");
        var provider = HtmlProxyServiceHandler.HtmlProxy.INSTANCE.lookup(renderer.key());
        assertTrue(ServerPushManager.getInstance().isServerPushActive());
        FutureTask<String> render = new FutureTask<>(() -> provider.render("REFRESH"));
        Thread request = new Thread(render, "html-proxy-action-test");
        request.setDaemon(true);
        request.start();
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        try
        {
            // The browser's server-push request supplies this UI cycle in production.
            while (!render.isDone() && System.nanoTime() < deadline)
            {
                if (!shell.getDisplay().readAndDispatch())
                {
                    Thread.sleep(1);
                }
            }
            assertEquals("action updated", render.get(1, TimeUnit.SECONDS));
            assertEquals(1, renderer.actionCount);
            assertSame(Thread.currentThread(), renderer.actionThread);
        }
        finally
        {
            render.cancel(true);
        }
    }

    private static class Renderer extends EJRWTExtHtmlViewItemRenderer
    {
        int actionCount;
        Thread actionThread;
        Renderer() { }
        Renderer(Shell shell) { create(shell); }
        void create(Shell shell) { _textField = createBrowser(shell); }
        String key() { return _baseValueKey; }
        @Override
        protected void setMandatoryBorder(boolean mandatory) { }
        @Override
        protected void executeHtmlAction(String action)
        {
            actionCount++;
            actionThread = Thread.currentThread();
            setValue("action updated");
        }
    }
}
