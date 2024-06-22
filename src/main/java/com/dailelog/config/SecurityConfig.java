package com.dailelog.config;

import com.dailelog.config.handler.Http401Handler;
import com.dailelog.config.handler.Http403Handler;
import com.dailelog.config.handler.LoginFailHandler;
import com.dailelog.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;


@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                        .requestMatchers("/favicon.ico")
                        .requestMatchers("/error");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers("/auth/login","/auth/signup").permitAll()
                        .requestMatchers("/user").hasAnyRole("ADMIN","USER")
                        .requestMatchers("/admin").hasAnyRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin ->{
                     formLogin.loginPage("/auth/login")
                            .loginProcessingUrl("/auth/login")//post로 던지는 주소
                            .usernameParameter("username")
                            .passwordParameter("password")
                            .defaultSuccessUrl("/",true)
                            .failureHandler(new LoginFailHandler(objectMapper));
                })
                .exceptionHandling(e->{
                    e.accessDeniedHandler(new Http403Handler(objectMapper));
                    e.authenticationEntryPoint(new Http401Handler(objectMapper));
                })
                .rememberMe(rm-> rm
                        .rememberMeParameter("remember")
                        .alwaysRemember(false)
                        .tokenValiditySeconds(2592000)
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            com.dailelog.domain.User user = userRepository.findByAccount(username).orElseThrow(() -> new UsernameNotFoundException(username + "is not found"));

            return new UserPrincipal(user);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new SCryptPasswordEncoder(
                16,
                8,
                1,
                32,
                64);
    }
}
