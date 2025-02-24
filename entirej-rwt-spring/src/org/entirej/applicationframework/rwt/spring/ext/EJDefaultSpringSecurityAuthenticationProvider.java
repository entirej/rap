package org.entirej.applicationframework.rwt.spring.ext;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EJDefaultSpringSecurityAuthenticationProvider implements EJSpringSecurityAuthenticationProvider
{


    @Override
    public void configureGlobal(AuthenticationManagerBuilder auth)
    {

        try
        {

            PasswordEncoder passwordEncoder = passwordEncoder();
            auth.inMemoryAuthentication().withUser("user").password(passwordEncoder.encode("password")).roles("USER");
            auth.inMemoryAuthentication().withUser("admin").password(passwordEncoder.encode("admin")).roles("ADMIN");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public UserDetailsService customUserDetailsService()
    {
        PasswordEncoder passwordEncoder = passwordEncoder();
        UserDetails user = User.builder().username("user").password(passwordEncoder.encode("password")).roles("USER").build();

        UserDetails admin = User.builder().username("admin").password(passwordEncoder.encode("admin")).roles("USER", "ADMIN").build();
        return new UserDetailsService()
        {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
            {

                // Example: load user from database (replace with actual DB
                // logic)
                if ("admin".equals(username))
                {
                    return admin;
                }
                if ("user".equals(username))
                {
                    return user;
                }
                throw new UsernameNotFoundException("User not found");

            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

}
