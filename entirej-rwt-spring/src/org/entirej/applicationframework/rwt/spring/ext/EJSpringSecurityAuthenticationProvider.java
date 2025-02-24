package org.entirej.applicationframework.rwt.spring.ext;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface EJSpringSecurityAuthenticationProvider
{
    void configureGlobal(AuthenticationManagerBuilder auth)  ;

    UserDetailsService customUserDetailsService();

    
    
}
