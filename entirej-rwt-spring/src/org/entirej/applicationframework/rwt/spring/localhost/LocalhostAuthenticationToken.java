package org.entirej.applicationframework.rwt.spring.localhost;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * {@code LocalhostAuthenticationToken} is Kerberos authentication token for
 * local user if the application is accessed from same machine as server since
 * if the server and client are same machine browser will always send NTLM token
 * insted of Kerberos token.
 *
 * @see LocalhostAuthenticationFilter
 * @see LocalhostAuthenticationProvider
 */
public class LocalhostAuthenticationToken extends AbstractAuthenticationToken
{

    /**
	 *
	 */
    private static final long serialVersionUID = -8313121312116264280L;

    private final Object      principal;

    /**
     * <p>
     * Constructor for LocalhostAuthenticationToken.
     * </p>
     *
     * @param principal
     *            a {@link java.lang.Object} object.
     */
    public LocalhostAuthenticationToken(Object principal)
    {
        super(null);
        this.principal = principal;
        setAuthenticated(false);
    }

    /**
     * <p>
     * Constructor for LocalhostAuthenticationToken.
     * </p>
     *
     * @param principal
     *            a {@link java.lang.Object} object.
     * @param authorities
     *            a {@link java.util.Collection} object.
     */
    public LocalhostAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities)
    {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    /**
     * <p>
     * getCredentials.
     * </p>
     *
     * @return a {@link java.lang.Object} object.
     */
    public Object getCredentials()
    {
        return null;
    }

    /**
     * <p>
     * Getter for the field <code>principal</code>.
     * </p>
     *
     * @return the principal
     */
    public Object getPrincipal()
    {
        return this.principal;
    }

}