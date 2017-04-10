package org.entirej.applicationframework.rwt.spring.localhost;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * {@code LocalhostAuthenticationProvider} is Kerberos authentication provider
 * for local user if the application is accessed from same machine as server
 * since if the server and client are same machine browser will always send NTLM
 * token insted of Kerberos token.
 *
 * 
 * @see LocalhostAuthenticationFilter
 * @see LocalhostAuthenticationToken
 */
public class LocalhostAuthenticationProvider implements AuthenticationProvider, InitializingBean
{

    private UserDetailsService userDetailsService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    /** {@inheritDoc} */
    @Override
    public void afterPropertiesSet()
    {
        if (userDetailsService == null)
        {
            throw new SecurityException("property userDetailsService is null");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.authentication.AuthenticationProvider#
     * authenticate(org.springframework.security.core.Authentication)
     */
    /** {@inheritDoc} */
    @Override
    public Authentication authenticate(Authentication authentication)
    {
        LocalhostAuthenticationToken auth = (LocalhostAuthenticationToken) authentication;
        String username = auth.getName();
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        LocalhostAuthenticationToken output = new LocalhostAuthenticationToken(userDetails, userDetails.getAuthorities());
        return output;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.authentication.AuthenticationProvider#supports
     * (java.lang.Class)
     */
    /** {@inheritDoc} */
    @Override
    public boolean supports(Class<?> authentication)
    {
        return LocalhostAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * <p>
     * Setter for the field <code>userDetailsService</code>.
     * </p>
     *
     * @param detailsService
     *            a
     *            {@link org.springframework.security.core.userdetails.UserDetailsService}
     *            object.
     */
    public void setUserDetailsService(UserDetailsService detailsService)
    {
        this.userDetailsService = detailsService;
    }
}