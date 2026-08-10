package org.entirej.applicationframework.rwt.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.entirej.applicationframework.rwt.spring.ext.EJDefaultSpringSecurityConfigProvider;
import org.entirej.applicationframework.rwt.spring.ext.EJSpringSecurityAuthenticationProvider;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.KerberosServiceRequestToken;
import org.springframework.security.kerberos.authentication.KerberosTicketValidation;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;

public class SpringSecurityRegressionTest
{
    @Test
    public void authenticationProviderRegistersUserDetailsService() throws Exception
    {
        AuthenticationManagerBuilder builder = new AuthenticationManagerBuilder(new IdentityObjectPostProcessor());
        UserDetailsService users = new InMemoryUserDetailsManager(
                User.withUsername("user").password("{noop}password").roles("USER").build());
        EJSpringSecurityAuthenticationProvider provider = new EJSpringSecurityAuthenticationProvider()
        {
            @Override
            public UserDetailsService userDetailsService()
            {
                return users;
            }
        };

        provider.configure(builder);
        AuthenticationManager manager = builder.build();
        Authentication result = manager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated("user", "password"));

        assertTrue(result.isAuthenticated());
    }

    @Test
    public void invalidAuthenticationProviderDoesNotFallBack()
    {
        assertThrows(IllegalStateException.class,
                () -> EJSpringSecurityConfig.instantiateProvider(String.class.getName()));
        assertThrows(IllegalStateException.class,
                () -> EJSpringSecurityConfig.instantiateProvider("missing.AuthenticationProvider"));
    }

    @Test
    public void kerberosProviderAuthenticatesWithAlignedSpringDependencies() throws Exception
    {
        KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
        provider.setTicketValidator(token -> new KerberosTicketValidation(
                "user", "HTTP/service.example", new byte[0], null));
        provider.setUserDetailsService(new InMemoryUserDetailsManager(
                User.withUsername("user").password("{noop}unused").roles("USER").build()));
        provider.afterPropertiesSet();

        Authentication result = provider.authenticate(new KerberosServiceRequestToken(new byte[] { 1 }));

        assertTrue(result.isAuthenticated());
        assertEquals("user", result.getName());

        SpnegoAuthenticationProcessingFilter filter = new SpnegoAuthenticationProcessingFilter();
        filter.setAuthenticationManager(provider::authenticate);
        filter.afterPropertiesSet();
    }

    @Test
    public void invalidRestProviderDoesNotFallBack()
    {
        assertThrows(IllegalStateException.class,
                () -> EJSpringRestSecurityConfig.instantiateProvider(String.class.getName()));
        assertThrows(IllegalStateException.class,
                () -> EJSpringRestSecurityConfig.instantiateProvider("missing.SecurityConfigProvider"));
    }

    @Test
    public void defaultRestConfigurationKeepsCsrfAndDeniesRequests() throws Exception
    {
        try (AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext())
        {
            context.setServletContext(new MockServletContext());
            context.register(DefaultSecurityConfiguration.class);
            context.refresh();

            FilterChainProxy proxy = context.getBean(FilterChainProxy.class);
            List<Filter> filters = proxy.getFilters("/");
            assertNotNull(filters);
            assertTrue(filters.stream().anyMatch(CsrfFilter.class::isInstance));
            assertTrue(filters.stream().anyMatch(AuthorizationFilter.class::isInstance));

            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
            MockHttpServletResponse response = new MockHttpServletResponse();
            proxy.doFilter(request, response, new MockFilterChain());
            assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        }
    }

    private static final class IdentityObjectPostProcessor implements ObjectPostProcessor<Object>
    {
        @Override
        public <O extends Object> O postProcess(O object)
        {
            return object;
        }
    }

    @Configuration
    @EnableWebSecurity
    static class DefaultSecurityConfiguration
    {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
        {
            return new EJDefaultSpringSecurityConfigProvider().configure(http, null);
        }
    }
}
