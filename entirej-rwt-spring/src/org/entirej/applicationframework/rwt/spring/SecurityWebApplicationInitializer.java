package org.entirej.applicationframework.rwt.spring; 

import org.springframework.security.web.context.*;

public class SecurityWebApplicationInitializer
      extends AbstractSecurityWebApplicationInitializer {

    public SecurityWebApplicationInitializer() {
        super(RestSecurityConfig.class, SecurityConfig.class);
        //super( SecurityConfig.class);
    }
    
    
}