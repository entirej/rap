package org.entirej.applicationframework.rwt.spring;

import org.entirej.applicationframework.rwt.spring.ext.EJDefaultSpringSecurityAuthenticationProvider;
import org.entirej.applicationframework.rwt.spring.ext.EJSpringSecurityAuthenticationProvider;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
public class EJSpringSecurityConfig
{
    public static final String SPRING_SECURITY = "SPRING_SECURITY";
    public static final String SPRING_SECURITY_CONFIG = "SPRING_SECURITY_AUTH";
    private EJSpringSecurityAuthenticationProvider provider;

    public EJSpringSecurityConfig()
    {
        EJCoreProperties instance = EJCoreProperties.getInstance();
        EJFrameworkExtensionProperties definedProperties = instance.getApplicationDefinedProperties();
        if (definedProperties != null)
        {
            EJFrameworkExtensionProperties settings = definedProperties.getPropertyGroup(SPRING_SECURITY);
            if(settings!=null)
            {
                String configClass = settings.getStringProperty(SPRING_SECURITY_CONFIG);
                if(configClass!=null && !configClass.isEmpty())
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
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
    {

        provider.configureGlobal(auth);
    }

}