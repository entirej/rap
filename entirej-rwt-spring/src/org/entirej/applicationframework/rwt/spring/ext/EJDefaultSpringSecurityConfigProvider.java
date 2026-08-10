package org.entirej.applicationframework.rwt.spring.ext;

import org.entirej.applicationframework.rwt.spring.EJSecurityConfig;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class EJDefaultSpringSecurityConfigProvider implements EJSpringSecurityConfigProvider
{
    
    
    public SecurityFilterChain configure(HttpSecurity http,EJSpringSecurityContext context) throws Exception
    {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().denyAll());
        return http.build();

    }
    
    @Override
    public Class< EJSecurityConfig>[] getOtherSecurityConfigurer()
    {
       
        return new Class[0];
    }
}
