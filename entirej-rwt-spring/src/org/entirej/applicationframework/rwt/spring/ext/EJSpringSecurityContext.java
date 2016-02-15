package org.entirej.applicationframework.rwt.spring.ext;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface EJSpringSecurityContext
{
    public AuthenticationManager authenticationManagerBean() throws Exception;

    public UserDetailsService userDetailsServiceBean() throws Exception;
}
