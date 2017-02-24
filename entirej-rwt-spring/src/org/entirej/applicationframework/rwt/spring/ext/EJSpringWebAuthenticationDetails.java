package org.entirej.applicationframework.rwt.spring.ext;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class EJSpringWebAuthenticationDetails extends WebAuthenticationDetails
{

    private final String queryString;

    public EJSpringWebAuthenticationDetails(HttpServletRequest request)
    {
        super(request);
        queryString = request.getParameter("hash");
    }

    public String getQueryString()
    {
        return queryString;
    }

}