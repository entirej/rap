package org.entirej.applicationframework.rwt.spring.ext;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class EJDefaultSpringSecurityConfigProvider implements EJSpringSecurityConfigProvider
{
    
    
    public void configure(HttpSecurity http,EJSpringSecurityContext context) throws Exception
    {
        http.csrf().disable();
       
        http.authorizeRequests().antMatchers("/resources/**", "/login/**").
        permitAll().anyRequest().authenticated().and().formLogin();

    }
}
