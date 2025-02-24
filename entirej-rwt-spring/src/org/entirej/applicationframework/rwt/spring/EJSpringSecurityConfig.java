package org.entirej.applicationframework.rwt.spring;

import org.entirej.applicationframework.rwt.spring.ext.EJDefaultSpringSecurityAuthenticationProvider;
import org.entirej.applicationframework.rwt.spring.ext.EJSpringSecurityAuthenticationProvider;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@EnableWebSecurity
public class EJSpringSecurityConfig
{
    public static final String                     SPRING_SECURITY           = "SPRING_SECURITY";
    public static final String                     SPRING_SECURITY_CONFIG    = "SPRING_SECURITY_AUTH";
    private EJSpringSecurityAuthenticationProvider provider;
    private UserDetailsService                     defaultUserDetailsService = null;
    private UserDetailsService                     userDetailsServiceProxy   = new UserDetailsService()
                                                                             {

                                                                                 @Override
                                                                                 public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
                                                                                 {

                                                                                     return (defaultUserDetailsService != null) ? defaultUserDetailsService.loadUserByUsername(username) : null;
                                                                                 }
                                                                             };

    public EJSpringSecurityConfig()
    {
        EJCoreProperties instance = EJCoreProperties.getInstance();
        EJFrameworkExtensionProperties definedProperties = instance.getApplicationDefinedProperties();
        if (definedProperties != null)
        {
            EJFrameworkExtensionProperties settings = definedProperties.getPropertyGroup(SPRING_SECURITY);
            if (settings != null)
            {
                String configClass = settings.getStringProperty(SPRING_SECURITY_CONFIG);
                if (configClass != null && !configClass.isEmpty())
                {
                    Class<?> factoryClass;
                    try
                    {
                        factoryClass = Class.forName(configClass);
                        Object obj = factoryClass.newInstance();

                        if (obj instanceof EJSpringSecurityAuthenticationProvider)
                        {
                            provider = (EJSpringSecurityAuthenticationProvider) obj;
                        }
                        else

                        {
                            System.err.println("invalid EJSpringSecurityAuthenticationProvider switch to default");
                        }
                    }
                    catch (ClassNotFoundException e)
                    {
                        System.err.println("invalid EJSpringSecurityAuthenticationProvider switch to default");
                        e.printStackTrace();
                    }
                    catch (InstantiationException e)
                    {
                        System.err.println("invalid EJSpringSecurityAuthenticationProvider switch to default");
                        e.printStackTrace();
                    }
                    catch (IllegalAccessException e)
                    {
                        System.err.println("invalid EJSpringSecurityAuthenticationProvider switch to default");
                        e.printStackTrace();
                    }

                }
            }
        }
        if (provider == null)
        {
            provider = new EJDefaultSpringSecurityAuthenticationProvider();
        }
        defaultUserDetailsService = provider.customUserDetailsService();
        
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception
    {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        provider.configureGlobal(authenticationManagerBuilder);
        AuthenticationManager manager = authenticationManagerBuilder.build();
        return manager;
    }

    @Bean
    public UserDetailsService userDetailsService()
    {
        return userDetailsServiceProxy;
    }

}