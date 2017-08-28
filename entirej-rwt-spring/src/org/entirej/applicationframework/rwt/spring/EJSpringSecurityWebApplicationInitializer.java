package org.entirej.applicationframework.rwt.spring; 

import org.springframework.core.annotation.Order;
import org.springframework.security.web.context.*;
@Order(1)
public class EJSpringSecurityWebApplicationInitializer
      extends AbstractSecurityWebApplicationInitializer {

    public EJSpringSecurityWebApplicationInitializer() {
        super(new EJSpringRestSecurityConfig(true).getConfigClasses());
        //super( SecurityConfig.class);
    }
    
    
}