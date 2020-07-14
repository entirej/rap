package org.entirej.applicationframework.rwt.renderers.lov;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.rap.rwt.SingletonUtil;

public class EJRWTLovContext
{

    private volatile AtomicInteger lovDialogs = new AtomicInteger(0);

    private EJRWTLovContext()
    {
        // keep private
    }

    public static EJRWTLovContext get()
    {
        return SingletonUtil.getSessionInstance(EJRWTLovContext.class);
    }

    public void openLov()
    {
        lovDialogs.incrementAndGet();
    }

    public void closeLov()
    {
        lovDialogs.decrementAndGet();
    }

    public boolean isLovOpen()
    {
        return lovDialogs.get() > 0;
    }
}
