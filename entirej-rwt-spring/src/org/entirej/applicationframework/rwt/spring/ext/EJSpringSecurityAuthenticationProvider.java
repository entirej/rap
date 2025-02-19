package org.entirej.applicationframework.rwt.spring.ext;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface EJSpringSecurityAuthenticationProvider
{
    UserDetailsService configureGlobal()  ;
}
