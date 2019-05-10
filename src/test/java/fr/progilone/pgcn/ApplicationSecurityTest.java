package fr.progilone.pgcn;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;

import fr.progilone.pgcn.security.AjaxAuthenticationFailureHandler;
import fr.progilone.pgcn.security.AjaxAuthenticationSuccessHandler;
import fr.progilone.pgcn.security.AjaxLogoutSuccessHandler;
import fr.progilone.pgcn.security.Http401UnauthorizedEntryPoint;
import fr.progilone.pgcn.service.LockService;

@Configuration
public class ApplicationSecurityTest {

    @Bean
    public UserDetailsService userDetailsService() {
        return Mockito.mock(UserDetailsService.class);
    }
    
    @Mock
    public LockService lockService;

    @Bean
    public RememberMeServices rememberMeServices() {
        return new NullRememberMeServices();
    }

    @Bean
    public AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler() {
        return new AjaxAuthenticationSuccessHandler();
    }

    @Bean
    public AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler() {
        return new AjaxAuthenticationFailureHandler();
    }

    @Bean
    public AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler() {
        return new AjaxLogoutSuccessHandler(lockService);
    }

    @Bean
    public Http401UnauthorizedEntryPoint authenticationEntryPoint() {
        return new Http401UnauthorizedEntryPoint();
    }
}
