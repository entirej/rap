package org.entirej.applicationframework.rwt.spring.ext;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class EJDefaultSpringSecurityConfigProvider implements EJSpringSecurityConfigProvider
{
    
    
    public void configure(HttpSecurity http,EJSpringSecurityContext context) throws Exception
    {
        http.csrf().disable();
       
       

    }
    
    @Override
    public Class< WebSecurityConfigurerAdapter>[] getOtherSecurityConfigurer()
    {
       
        return new Class[0];
    }
}
