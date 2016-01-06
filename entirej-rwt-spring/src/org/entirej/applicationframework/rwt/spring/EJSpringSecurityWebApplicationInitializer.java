package org.entirej.applicationframework.rwt.spring; 

import org.springframework.security.web.context.*;

public class EJSpringSecurityWebApplicationInitializer
      extends AbstractSecurityWebApplicationInitializer {

    public EJSpringSecurityWebApplicationInitializer() {
        super(EJSpringRestSecurityConfig.class, EJSpringSecurityConfig.class);
        //super( SecurityConfig.class);
    }
    
    
}