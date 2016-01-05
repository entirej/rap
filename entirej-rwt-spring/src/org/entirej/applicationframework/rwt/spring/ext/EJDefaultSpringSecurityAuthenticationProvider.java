package org.entirej.applicationframework.rwt.spring.ext;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

public class EJDefaultSpringSecurityAuthenticationProvider implements EJSpringSecurityAuthenticationProvider
{

    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
    {

        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
    }

}
