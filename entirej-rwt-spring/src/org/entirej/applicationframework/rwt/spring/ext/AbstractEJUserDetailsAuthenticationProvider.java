package org.entirej.applicationframework.rwt.spring.ext;

import org.entirej.framework.core.EJConnectionHelper;
import org.entirej.framework.core.EJFrameworkInitialiser;
import org.entirej.framework.core.EJManagedFrameworkConnection;
import org.entirej.framework.core.interfaces.EJApplicationManager;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;

public abstract class AbstractEJUserDetailsAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider
{
    
    final protected EJApplicationManager appManager;
    
    public AbstractEJUserDetailsAuthenticationProvider()
    {
        appManager = EJFrameworkInitialiser.initialiseFramework("application.ejprop");
    }
    
    public final EJManagedFrameworkConnection getConnection()
    {
        
        
        return EJConnectionHelper.getConnection();
    }
    
    
}
