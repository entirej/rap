package org.entirej.applicationframework.rwt.application.launcher;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTAbstractDialog;

public class EJRWTSessionCleanup
{
    Logger                                    LOG        = Logger.getLogger(EJRWTSessionCleanup.class.getName());


    public EJRWTSessionCleanup()
    {
        LOG.info("EJRWTSessionCleanup session for :" + RWT.getUISession().getId());
        try
        {
            RWT.getUISession().addUISessionListener(new UISessionListener()
            {

                private static final long serialVersionUID = 1L;

                @Override
                public void beforeDestroy(UISessionEvent arg0)
                {
                    cleanup();

                }
            });
        }
        catch (Throwable e)
        {
            // fallback

        }
    }
    
    public WeakHashMap<Closeable, Closeable> getCloseables()
    {
        
        WeakHashMap<Closeable, Closeable> closeables = null;
        closeables  = (WeakHashMap<Closeable, Closeable>) RWT.getUISession().getAttribute("EJRWTSessionCleanup.closeables");
        if(closeables==null) {
            closeables  = new WeakHashMap<>();
            RWT.getUISession().setAttribute("EJRWTSessionCleanup.closeables",closeables);
        }
        return closeables;
    }

    public void addCloseable(Closeable closeable)
    {
        getCloseables().put(closeable, closeable);
    }
    public void removeCloseable(Closeable closeable)
    {
        getCloseables().remove(closeable);
    }

    public void cleanup()
    {
        LOG.info("EJRWTSessionCleanup cleanup for session for :" + RWT.getUISession().getId() + ", size:"+getCloseables().size());
        
        WeakHashMap<Closeable, Closeable> closeables = getCloseables();
        Collection<Closeable> collection = closeables.values();
        closeables.clear();
        for (Closeable cloneable : collection)
        {
            try
            {
                    cloneable.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }

    }

    public static Optional<EJRWTSessionCleanup> getSession()
    {
        if (ContextProvider.hasContext() && RWT.getUISession() != null)
            return Optional.of(SingletonUtil.getSessionInstance(EJRWTSessionCleanup.class));
        else
            return Optional.empty();

    }

}
