package it.uniroma3.siw.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {

    // Ruoli definiti nel nostro sistema
    public static final String DEFAULT_ROLE = "DEFAULT";
    public static final String TRAINER_ROLE = "TRAINER";
    public static final String ADMIN_ROLE = "ADMIN";

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
            .dataSource(dataSource)
            .authoritiesByUsernameQuery("SELECT username, role from credentials WHERE username=?")
            .usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().and().cors().disable()
                .authorizeHttpRequests()
                    // Chiunque può accedere a queste pagine
                    .requestMatchers(HttpMethod.GET, "/", "/index", "/register", "/css/**", "/images/**", "favicon.ico").permitAll()
                    // Chiunque può inviare richieste POST a questi endpoint
                    .requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()
                    // Solo gli ADMIN possono accedere alle pagine /admin/**
                    .requestMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
                    .requestMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
                    // Solo gli ADMIN e i TRAINER possono accedere alle pagine /staff/**
                    .requestMatchers(HttpMethod.GET, "/staff/**").hasAnyAuthority(ADMIN_ROLE, TRAINER_ROLE)
                    .requestMatchers(HttpMethod.POST, "/staff/**").hasAnyAuthority(ADMIN_ROLE, TRAINER_ROLE)
                    // Tutti gli utenti autenticati possono accedere alle pagine rimanenti
                    .anyRequest().authenticated()
                .and().formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .defaultSuccessUrl("/success", true)
                    .failureUrl("/login?error=true")
                .and().logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .clearAuthentication(true).permitAll();
        return httpSecurity.build();
    }
}