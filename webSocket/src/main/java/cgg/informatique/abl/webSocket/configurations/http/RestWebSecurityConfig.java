package cgg.informatique.abl.webSocket.configurations.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
@Order(0)
public class RestWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private RestAuthenticationEntryPoint authenticationEntryPoint;

    public RestWebSecurityConfig(@Autowired RestAuthenticationEntryPoint authenticationEntryPoint) {

        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .antMatcher("/api**")
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/comptes").permitAll()
                .antMatchers("/api/monCompte").authenticated()
                .antMatchers("/api/comptes*").hasAuthority("ROLE_Venerable")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler(new SavedRequestAwareAuthenticationSuccessHandler())
                .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                .and()
                .logout();
    }
}
