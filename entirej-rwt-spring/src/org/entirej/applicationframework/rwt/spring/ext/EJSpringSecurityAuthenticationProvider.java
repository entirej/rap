package org.entirej.applicationframework.rwt.spring.ext;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface EJSpringSecurityAuthenticationProvider
{
    default void configure(AuthenticationManagerBuilder authentication) throws Exception
    {
        UserDetailsService userDetailsService = userDetailsService();
        if (userDetailsService != null)
        {
            authentication.userDetailsService(userDetailsService);
        }
    }

    default UserDetailsService userDetailsService()
    {
        return null;
    }
}
