package org.entirej.applicationframework.rwt.application.launcher;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;

public abstract class EJRWTApplicationLauncherProxy implements ApplicationConfiguration
{

    @Override
    public void configure(Application application)
    {
        EJRWTApplicationLauncher[] applicationLaunchers = getApplicationLaunchers();
        for (EJRWTApplicationLauncher ejrwtApplicationLauncher : applicationLaunchers)
        {
            ejrwtApplicationLauncher.configure(application);
        }

    }

   public abstract EJRWTApplicationLauncher[] getApplicationLaunchers();

}
