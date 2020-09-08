package org.entirej.applicationframework.rwt.application.launcher;

import java.io.IOException;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import org.apache.commons.fileupload.util.Closeable;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;

public class EJRWTSessionCleanup
{
    Logger LOG = Logger.getLogger(EJRWTSessionCleanup.class.getName());
    
    private WeakHashMap<Closeable, Closeable> closeables = new WeakHashMap<>();

    public EJRWTSessionCleanup()
    {
        LOG.info("EJRWTSessionCleanup session for :"+RWT.getUISession().getId());
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
    
    public void addCloseable(Closeable closeable)
    {
        closeables.put(closeable, closeable);
    }

    protected void cleanup()
    {
        LOG.info("EJRWTSessionCleanup cleanup for session for :"+RWT.getUISession().getId());
        for (Closeable cloneable : closeables.values())
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

    public static EJRWTSessionCleanup getSession()
    {
        return SingletonUtil.getSessionInstance(EJRWTSessionCleanup.class);
    }

}
