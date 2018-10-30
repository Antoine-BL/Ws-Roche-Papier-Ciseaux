package cgg.informatique.abl.webSocket.configurations.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Order(1)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl monUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        mainConfig(http);
        loginConfig(http);
        logoutConfig(http);
        miscConfig(http);
    }

    private void mainConfig(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/", "/connexion", "/compte/creer").permitAll()
            .antMatchers("/passage").hasAnyAuthority("ROLE_Sensei", "ROLE_Venerable")
            .antMatchers("/kumite").authenticated()
            .anyRequest().permitAll();
    }

    private void loginConfig(HttpSecurity http) throws Exception {
        http.formLogin()
            .passwordParameter("password")
            .usernameParameter("username")
            .loginProcessingUrl("/connexion")
            .loginPage("/connexion")
            .defaultSuccessUrl("/")
            .failureUrl("/connexion?error=true");
    }

    private void logoutConfig(HttpSecurity http) throws Exception {
        http.logout()
            .logoutUrl("/deconnexion")
            .logoutSuccessUrl("/");
    }

    private void miscConfig(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(monUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
