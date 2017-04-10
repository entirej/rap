package org.entirej.applicationframework.rwt.spring.localhost;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

/**
 * {@code LocalhostAuthenticationFilter} is Kerberos authentication filter local
 * user if the application is accessed from same machine as server since if the
 * server and client are same machine browser will always send NTLM token insted
 * of Kerberos token.
 *
 * 
 * @see LocalhostAuthenticationProvider
 * @see LocalhostAuthenticationToken
 */
public class LocalhostAuthenticationFilter extends GenericFilterBean
{

    private static final Logger                                LOGGER                      = LoggerFactory.getLogger(LocalhostAuthenticationFilter.class);

    private AuthenticationManager                              authenticationManager;
    private AuthenticationSuccessHandler                       successHandler;
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    /**
     * <p>
     * Getter for the field <code>authenticationManager</code>.
     * </p>
     *
     * @return the authenticationManager
     */
    public AuthenticationManager getAuthenticationManager()
    {
        return authenticationManager;
    }

    /**
     * <p>
     * Setter for the field <code>authenticationManager</code>.
     * </p>
     *
     * @param authenticationManager
     *            the authenticationManager to set
     */
    public void setAuthenticationManager(AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationSuccessHandler getSuccessHandler()
    {
        return successHandler;
    }

    public void setSuccessHandler(AuthenticationSuccessHandler successHandler)
    {
        this.successHandler = successHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */

    /**
     * {@inheritDoc}
     * <p/>
     * In this filter if the remote address and the local address is same then
     * the request is from the same machine so
     * {@link LocalhostAuthenticationToken} will be created using the username
     * which running the server and using authentication manager the user will
     * be validated for the application by
     * {@link LocalhostAuthenticationProvider}. The above process will be skiped
     * if the user is already authenticated.
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        if (request.getLocalAddr().equals(request.getRemoteAddr()))
        {
            final String username = System.getProperty("user.name").toUpperCase();
            if (authenticationIsRequired(username))
            {
                LOGGER.info("Request is local");
                LocalhostAuthenticationToken authRequest = new LocalhostAuthenticationToken(username);
                authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
                Authentication authResult = authenticationManager.authenticate(authRequest);
                SecurityContextHolder.getContext().setAuthentication(authResult);
                if (null != successHandler)
                {
                    successHandler.onAuthenticationSuccess(request, response, authResult);
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * Checks for is authentication required for the user.
     *
     * @param username
     *            authenticating username
     * @return boolean (is authentication required)
     */
    private boolean authenticationIsRequired(String username)
    {

        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth == null || !existingAuth.isAuthenticated())
        {
            return true;
        }

        if (existingAuth instanceof LocalhostAuthenticationToken)
        {
            String existingUsername = existingAuth.getName();
            if (existingUsername.indexOf('@') != -1)
            {
                existingUsername = existingUsername.substring(0, existingUsername.indexOf('@'));
            }
            return !existingUsername.equalsIgnoreCase(username);
        }
        if (existingAuth instanceof AnonymousAuthenticationToken)
        {
            return true;
        }
        return false;
    }
}