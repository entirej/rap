package org.entirej.applicationframework.rwt.spring.ext;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public interface EJSpringSecurityConfigProvider
{

    Class<? extends WebSecurityConfigurerAdapter>[] getOtherSecurityConfigurer();

    void configure(HttpSecurity http, EJSpringSecurityContext context) throws Exception;;
}
