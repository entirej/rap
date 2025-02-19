package org.entirej.applicationframework.rwt.spring.ext;

import org.entirej.applicationframework.rwt.spring.EJSecurityConfig;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public interface EJSpringSecurityConfigProvider
{

    Class<? extends EJSecurityConfig>[] getOtherSecurityConfigurer();

    SecurityFilterChain configure(HttpSecurity http, EJSpringSecurityContext context) throws Exception;;
}
