package org.entirej.applicationframework.rwt.spring;

import org.entirej.applicationframework.rwt.spring.ext.EJDefaultSpringSecurityConfigProvider;
import org.entirej.applicationframework.rwt.spring.ext.EJSpringSecurityConfigProvider;
import org.entirej.applicationframework.rwt.spring.ext.EJSpringSecurityContext;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class EJSpringRestSecurityConfig extends WebSecurityConfigurerAdapter
{

    public static final String             SPRING_SECURITY      = "SPRING_SECURITY";
    public static final String             SPRING_SECURITY_AUTH = "SPRING_SECURITY_CONFIG";
    private EJSpringSecurityConfigProvider provider;

    public EJSpringRestSecurityConfig()
    {
        EJCoreProperties instance = EJCoreProperties.getInstance();
        EJFrameworkExtensionProperties definedProperties = instance.getApplicationDefinedProperties();
        if (definedProperties != null)
        {
            EJFrameworkExtensionProperties settings = definedProperties.getPropertyGroup(SPRING_SECURITY);
            if (settings != null)
            {
                String configClass = settings.getStringProperty(SPRING_SECURITY_AUTH);
                if (configClass != null && !configClass.isEmpty())
                {
                    Class<?> factoryClass;
                    try
                    {
                        factoryClass = Class.forName(configClass);
                        Object obj = factoryClass.newInstance();

                        if (obj instanceof EJSpringSecurityConfigProvider)
                        {
                            provider = (EJSpringSecurityConfigProvider) obj;
                        }
                        else

                        {
                            System.err.println("invalid EJSpringSecurityConfigProvider switch to default");
                        }
                    }
                    catch (ClassNotFoundException e)
                    {
                        System.err.println("invalid EJSpringSecurityConfigProvider switch to default");
                        e.printStackTrace();
                    }
                    catch (InstantiationException e)
                    {
                        System.err.println("invalid EJSpringSecurityConfigProvider switch to default");
                        e.printStackTrace();
                    }
                    catch (IllegalAccessException e)
                    {
                        System.err.println("invalid EJSpringSecurityConfigProvider switch to default");
                        e.printStackTrace();
                    }

                }
            }
        }
        if (provider == null)
        {
            provider = new EJDefaultSpringSecurityConfigProvider();
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        provider.configure(http, new EJSpringSecurityContext()
        {

            @Override
            public UserDetailsService userDetailsServiceBean() throws Exception
            {

                return EJSpringRestSecurityConfig.this.userDetailsServiceBean();
            }

            @Override
            public AuthenticationManager authenticationManagerBean() throws Exception
            {

                return EJSpringRestSecurityConfig.this.authenticationManagerBean();
            }
        });

    }

}