package org.entirej.applicationframework.rwt.spring.ext;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

public interface EJSpringSecurityAuthenticationProvider
{
    void configureGlobal(AuthenticationManagerBuilder auth) throws Exception ;
}
