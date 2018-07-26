package org.entirej.applicationframework.rwt.spring;

import java.util.ArrayList;
import java.util.List;

import org.entirej.applicationframework.rwt.spring.ext.EJDefaultSpringSecurityConfigProvider;
import org.entirej.applicationframework.rwt.spring.ext.EJSpringSecurityConfigProvider;
import org.entirej.applicationframework.rwt.spring.ext.EJSpringSecurityContext;
import org.entirej.framework.core.EJFrameworkInitialiser;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

@Configuration
@EnableWebSecurity
@Order(1)
public class EJSpringRestSecurityConfig extends WebSecurityConfigurerAdapter
{

    public static final String             SPRING_SECURITY      = "SPRING_SECURITY";
    public static final String             SPRING_SECURITY_AUTH = "SPRING_SECURITY_CONFIG";
    private EJSpringSecurityConfigProvider provider;

    public EJSpringRestSecurityConfig()
    {
        provider = getProvider();
    }
    public EJSpringRestSecurityConfig(boolean init)
    {
        if(init)
            EJFrameworkInitialiser.initialiseFramework("application.ejprop");
        provider = getProvider();
    }

    private static EJSpringSecurityConfigProvider  getProvider()
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
                            return (EJSpringSecurityConfigProvider) obj;
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
        return new EJDefaultSpringSecurityConfigProvider();
        
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

    public Class<?>[] getConfigClasses()
    {
        
        
        List<Class<?>> configs = new ArrayList<Class<?>>();
        configs.add(this.getClass());
        System.out.println("EJSpringRestSecurityConfig.getConfigClasses()");
        
        Class<? extends WebSecurityConfigurerAdapter>[] otherSecurityConfigurer = provider.getOtherSecurityConfigurer();
        for (Class<? extends WebSecurityConfigurerAdapter> class1 : otherSecurityConfigurer)
        {
           
            configs.add(class1);
        }
        configs.add(EJSpringSecurityConfig.class);
        return configs.toArray(new Class<?>[configs.size()]);
    }
    
    @Bean
    public HttpSessionStrategy httpSessionStrategy() {
        return new HeaderHttpSessionStrategy();
    }

}