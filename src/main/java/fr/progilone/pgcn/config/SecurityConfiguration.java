package fr.progilone.pgcn.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.csrf.CsrfFilter;

import fr.progilone.pgcn.security.AjaxAuthenticationFailureHandler;
import fr.progilone.pgcn.security.AjaxAuthenticationSuccessHandler;
import fr.progilone.pgcn.security.AjaxLogoutSuccessHandler;
import fr.progilone.pgcn.security.Http401UnauthorizedEntryPoint;
import fr.progilone.pgcn.web.filter.CsrfCookieGeneratorFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Inject
    private Environment env;

    @Inject
    private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

    @Inject
    private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    @Inject
    private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

    @Inject
    private Http401UnauthorizedEntryPoint authenticationEntryPoint;

    @Inject
    private UserDetailsService userDetailsService;

    @Inject
    private RememberMeServices rememberMeServices;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL); 
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
           .antMatchers("/scripts/**/*.{js,html}")
           .antMatchers("/bower_components/**")
           .antMatchers("/i18n/**")
           .antMatchers("/assets/**")
           .antMatchers("/swagger-ui.html")
           .antMatchers("/test/**")
           .antMatchers("/.tmp/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf()
            .ignoringAntMatchers("/websocket/**")
            .and()
            .addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
            .and()
            .rememberMe()
            .rememberMeServices(rememberMeServices)
            .rememberMeParameter("remember-me")
            .key(env.getProperty("jhipster.security.rememberme.key"))
            .and()
            .formLogin()
            .loginProcessingUrl("/api/authentication")
            .successHandler(ajaxAuthenticationSuccessHandler)
            .failureHandler(ajaxAuthenticationFailureHandler)
            .usernameParameter("j_username")
            .passwordParameter("j_password")
            .permitAll()
            .and()
            .logout()
            .logoutUrl("/api/logout")
            .logoutSuccessHandler(ajaxLogoutSuccessHandler)
            .deleteCookies("JSESSIONID", "hazelcast.sessionId")
            .permitAll()
            .and()
            .headers()
            .frameOptions()
            .disable()
            .and()
            .authorizeRequests()
            .antMatchers("/api/authenticate")
            .permitAll()
            .antMatchers("/api/rest/reset")
            .permitAll()
            .antMatchers("/api/rest/logs/**")
            .authenticated()
            .antMatchers("/api/**")
            .authenticated()
            .antMatchers("/protected/**")
            .authenticated()
            .antMatchers("/actuator/**")
            .permitAll();
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
