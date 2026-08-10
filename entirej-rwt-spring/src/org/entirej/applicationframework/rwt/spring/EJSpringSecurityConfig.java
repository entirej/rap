package org.entirej.applicationframework.rwt.spring;

import org.entirej.applicationframework.rwt.spring.ext.EJDefaultSpringSecurityAuthenticationProvider;
import org.entirej.applicationframework.rwt.spring.ext.EJSpringSecurityAuthenticationProvider;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
public class EJSpringSecurityConfig extends GlobalAuthenticationConfigurerAdapter
{
    public static final String                     SPRING_SECURITY           = "SPRING_SECURITY";
    public static final String                     SPRING_SECURITY_CONFIG    = "SPRING_SECURITY_AUTH";
    private EJSpringSecurityAuthenticationProvider provider;

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
                    provider = instantiateProvider(configClass);
                }
            }
        }
        if (provider == null)
        {
            provider = new EJDefaultSpringSecurityAuthenticationProvider();
        }
    }

    static EJSpringSecurityAuthenticationProvider instantiateProvider(String configClass)
    {
        try
        {
            Object candidate = Class.forName(configClass).getDeclaredConstructor().newInstance();
            if (candidate instanceof EJSpringSecurityAuthenticationProvider authenticationProvider)
            {
                return authenticationProvider;
            }
            throw new IllegalStateException(configClass + " does not implement " + EJSpringSecurityAuthenticationProvider.class.getName());
        }
        catch (ReflectiveOperationException | LinkageError e)
        {
            throw new IllegalStateException("Unable to create configured Spring authentication provider " + configClass, e);
        }
    }

    @Override
    public void init(AuthenticationManagerBuilder authentication) throws Exception
    {
        provider.configure(authentication);
    }

}
