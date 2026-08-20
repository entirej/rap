package org.entirej.applicationframework.rwt.application.launcher;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;

public class EJRWTSessionCleanup
{
    private static final String                     CLOSEABLES_ATTRIBUTE = "EJRWTSessionCleanup.closeables";
    private static final Logger                     LOG                  = Logger.getLogger(EJRWTSessionCleanup.class.getName());
    private final String                            sessionId;
    private final WeakHashMap<Closeable, Closeable> closeables;

    @SuppressWarnings("unchecked")
    public EJRWTSessionCleanup()
    {
        sessionId = RWT.getUISession().getId();
        WeakHashMap<Closeable, Closeable> sessionCloseables = (WeakHashMap<Closeable, Closeable>) RWT.getUISession().getAttribute(CLOSEABLES_ATTRIBUTE);
        if (sessionCloseables == null)
        {
            sessionCloseables = new WeakHashMap<>();
            RWT.getUISession().setAttribute(CLOSEABLES_ATTRIBUTE, sessionCloseables);
        }
        closeables = sessionCloseables;

        LOG.info("EJRWTSessionCleanup session for :" + sessionId);
        try
        {
            RWT.getUISession().addUISessionListener(new UISessionListener()
            {
                private static final long serialVersionUID = 1L;

                @Override
                public void beforeDestroy(UISessionEvent event)
                {
                    cleanup();
                }
            });
        }
        catch (RuntimeException e)
        {
            LOG.log(Level.WARNING, "Unable to register session cleanup listener for " + sessionId, e);
        }
    }

    public WeakHashMap<Closeable, Closeable> getCloseables()
    {
        return closeables;
    }

    public synchronized void addCloseable(Closeable closeable)
    {
        closeables.put(closeable, closeable);
    }

    public synchronized void removeCloseable(Closeable closeable)
    {
        closeables.remove(closeable);
    }

    public synchronized void cleanup()
    {
        LOG.info("EJRWTSessionCleanup cleanup for session for :" + sessionId + ", size:" + closeables.size());

        Collection<Closeable> collection = new ArrayList<>(closeables.values());
        closeables.clear();
        for (Closeable closeable : collection)
        {
            try
            {
                closeable.close();
            }
            catch (Exception e)
            {
                LOG.log(Level.WARNING, "Unable to close session resource " + closeable.getClass().getName(), e);
            }
        }
    }

    public static Optional<EJRWTSessionCleanup> getSession()
    {
        if (ContextProvider.hasContext() && RWT.getUISession() != null)
        {
            return Optional.of(SingletonUtil.getSessionInstance(EJRWTSessionCleanup.class));
        }
        return Optional.empty();
    }
}
