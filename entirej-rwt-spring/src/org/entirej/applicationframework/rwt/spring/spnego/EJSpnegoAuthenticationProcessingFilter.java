package org.entirej.applicationframework.rwt.spring.spnego;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.kerberos.authentication.KerberosServiceRequestToken;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.util.Assert;

/**
 * THIS IS COPY of SpnegoAuthenticationProcessingFilter with FALLBACK on
 * GSSEXCEPTIONS
 * 
 * */
public class EJSpnegoAuthenticationProcessingFilter extends SpnegoAuthenticationProcessingFilter
{

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private AuthenticationManager                              authenticationManager;
    private AuthenticationSuccessHandler                       successHandler;
    private AuthenticationFailureHandler                       failureHandler;
    private SessionAuthenticationStrategy                      sessionStrategy             = new NullAuthenticatedSessionStrategy();
    private boolean                                            skipIfAlreadyAuthenticated  = true;
    private String                                             redirectUrl                 ;

    public void setRedirectUrl(String redirectUrl)
    {
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl()
    {
        return redirectUrl;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (skipIfAlreadyAuthenticated)
        {
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

            if (existingAuth != null && existingAuth.isAuthenticated() && (existingAuth instanceof AnonymousAuthenticationToken) == false)
            {
                chain.doFilter(request, response);
                return;
            }
        }

        String header = request.getHeader("Authorization");

        if (header != null && (header.startsWith("Negotiate ") || header.startsWith("Kerberos ")))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Received Negotiate Header for request " + request.getRequestURL() + ": " + header);
            }
            byte[] base64Token = header.substring(header.indexOf(" ") + 1).getBytes("UTF-8");
            byte[] kerberosTicket = Base64.decode(base64Token);
            KerberosServiceRequestToken authenticationRequest = new KerberosServiceRequestToken(kerberosTicket);
            authenticationRequest.setDetails(authenticationDetailsSource.buildDetails(request));
            Authentication authentication;
            try
            {
                authentication = authenticationManager.authenticate(authenticationRequest);
            }
            catch (AuthenticationException e)
            {
                // That shouldn't happen, as it is most likely a wrong
                // configuration on the server side
                logger.warn("Negotiate Header was invalid: " + header, e);
                 SecurityContextHolder.clearContext();
                // if (failureHandler != null)
                // {
                // failureHandler.onAuthenticationFailure(request, response, e);
                // }
                // else
                // {
                // response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                // response.flushBuffer();
                // }
                String url = getCustomRedirectUrl(request);
                if(url==null)
                {
                    url=  request.getContextPath()+"/login";
                }
                ((HttpServletResponse) response).sendRedirect(url);
                return;
            }
            sessionStrategy.onAuthentication(authentication, request, response);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            if (successHandler != null)
            {
                successHandler.onAuthenticationSuccess(request, response, authentication);
            }

        }

        chain.doFilter(request, response);

    }

    protected String getCustomRedirectUrl(HttpServletRequest request)
    {
        return getRedirectUrl();
    }

    @Override
    public void afterPropertiesSet() throws ServletException
    {
        super.afterPropertiesSet();
        Assert.notNull(this.authenticationManager, "authenticationManager must be specified");
    }

    /**
     * The authentication manager for validating the ticket.
     *
     * @param authenticationManager
     *            the authentication manager
     */
    public void setAuthenticationManager(AuthenticationManager authenticationManager)
    {
        super.setAuthenticationManager(authenticationManager);
        this.authenticationManager = authenticationManager;
    }

    /**
     * <p>
     * This handler is called after a successful authentication. One can add
     * additional authentication behavior by setting this.
     * </p>
     * <p>
     * Default is null, which means nothing additional happens
     * </p>
     *
     * @param successHandler
     *            the authentication success handler
     */
    public void setSuccessHandler(AuthenticationSuccessHandler successHandler)
    {
        super.setSuccessHandler(successHandler);
        this.successHandler = successHandler;
    }

    /**
     * <p>
     * This handler is called after a failure authentication. In most cases you
     * only get Kerberos/SPNEGO failures with a wrong server or network
     * configurations and not during runtime. If the client encounters an error,
     * he will just stop the communication with server and therefore this
     * handler will not be called in this case.
     * </p>
     * <p>
     * Default is null, which means that the Filter returns the HTTP 500 code
     * </p>
     *
     * @param failureHandler
     *            the authentication failure handler
     */
    public void setFailureHandler(AuthenticationFailureHandler failureHandler)
    {
        super.setFailureHandler(failureHandler);
        this.failureHandler = failureHandler;
    }

    /**
     * Should Kerberos authentication be skipped if a user is already
     * authenticated for this request (e.g. in the HTTP session).
     *
     * @param skipIfAlreadyAuthenticated
     *            default is true
     */
    public void setSkipIfAlreadyAuthenticated(boolean skipIfAlreadyAuthenticated)
    {
        this.skipIfAlreadyAuthenticated = skipIfAlreadyAuthenticated;
    }

    /**
     * The session handling strategy which will be invoked immediately after an
     * authentication request is successfully processed by the
     * <tt>AuthenticationManager</tt>. Used, for example, to handle changing of
     * the session identifier to prevent session fixation attacks.
     *
     * @param sessionStrategy
     *            the implementation to use. If not set a null implementation is
     *            used.
     */
    public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy sessionStrategy)
    {
        super.setSessionAuthenticationStrategy(sessionStrategy);
        this.sessionStrategy = sessionStrategy;
    }

    /**
     * Sets the authentication details source.
     *
     * @param authenticationDetailsSource
     *            the authentication details source
     */
    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource)
    {
        super.setAuthenticationDetailsSource(authenticationDetailsSource);

        Assert.notNull(authenticationDetailsSource, "AuthenticationDetailsSource required");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

}
