package org.entirej.applicationframework.rwt.spring.ext;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface EJSpringSecurityConfigProvider
{
  
    
    void configure(HttpSecurity http,EJSpringSecurityContext context) throws Exception;;
}
