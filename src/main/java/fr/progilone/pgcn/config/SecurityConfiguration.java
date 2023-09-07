package fr.progilone.pgcn.config;

import fr.progilone.pgcn.security.AjaxAuthenticationFailureHandler;
import fr.progilone.pgcn.security.AjaxAuthenticationSuccessHandler;
import fr.progilone.pgcn.security.AjaxLogoutSuccessHandler;
import fr.progilone.pgcn.security.Http401UnauthorizedEntryPoint;
import fr.progilone.pgcn.web.filter.CsrfCookieGeneratorFilter;
import fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

    private final Environment env;
    private final AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;
    private final AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;
    private final AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;
    private final Http401UnauthorizedEntryPoint authenticationEntryPoint;
    private final RememberMeServices rememberMeServices;

    static {
        // Pour que la session se propage vers les threads enfants
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    public SecurityConfiguration(final Environment env,
                                 final AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler,
                                 final AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler,
                                 final AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler,
                                 final Http401UnauthorizedEntryPoint authenticationEntryPoint,
                                 final RememberMeServices rememberMeServices) {
        this.env = env;
        this.ajaxAuthenticationSuccessHandler = ajaxAuthenticationSuccessHandler;
        this.ajaxAuthenticationFailureHandler = ajaxAuthenticationFailureHandler;
        this.ajaxLogoutSuccessHandler = ajaxLogoutSuccessHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.rememberMeServices = rememberMeServices;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/websocket/**")))
            .addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class)
            .exceptionHandling(c -> c.authenticationEntryPoint(authenticationEntryPoint))
            .rememberMe(c -> c.rememberMeServices(rememberMeServices).rememberMeParameter("remember-me").key(env.getProperty("jhipster.security.rememberme.key")))
            .formLogin(c -> c.loginProcessingUrl("/api/authentication")
                             .successHandler(ajaxAuthenticationSuccessHandler)
                             .failureHandler(ajaxAuthenticationFailureHandler)
                             .usernameParameter("j_username")
                             .passwordParameter("j_password")
                             .permitAll())
            .logout(c -> c.logoutUrl("/api/logout").logoutSuccessHandler(ajaxLogoutSuccessHandler).deleteCookies("JSESSIONID", "hazelcast.sessionId").permitAll())
            .headers(c -> c.frameOptions().disable())
            .authorizeHttpRequests(authorize -> authorize.requestMatchers(new AntPathRequestMatcher("/api/authenticate"), new AntPathRequestMatcher("/api/rest/reset"))
                                                         .permitAll()
                                                         .requestMatchers(new AntPathRequestMatcher("/api/**"), new AntPathRequestMatcher("/protected/**"))
                                                         .authenticated()
                                                         .requestMatchers(new AntPathRequestMatcher("/api_int/**"))
                                                         .hasRole(AuthorizationConstants.SUPER_ADMIN)
                                                         .requestMatchers(new AntPathRequestMatcher("/websocket/**"),
                                                                          new AntPathRequestMatcher("/actuator/**"),
                                                                          new AntPathRequestMatcher("/scripts/**/*.{js,html}"),
                                                                          new AntPathRequestMatcher("/libs/**"),
                                                                          new AntPathRequestMatcher("/i18n/**"),
                                                                          new AntPathRequestMatcher("/assets/**"),
                                                                          new AntPathRequestMatcher("/swagger-ui.html"))
                                                         .permitAll()
                                                         .anyRequest()
                                                         .permitAll());
        return http.build();
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
