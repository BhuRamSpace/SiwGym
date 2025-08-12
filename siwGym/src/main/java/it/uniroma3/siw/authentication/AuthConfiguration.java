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

    public static final String DEFAULT_ROLE = "DEFAULT";
    public static final String TRAINER_ROLE = "TRAINER";
    public static final String ADMIN_ROLE = "ADMIN";

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
            .dataSource(dataSource)
            .authoritiesByUsernameQuery("SELECT username, role FROM credentials WHERE username=?")
            .usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?")
            .rolePrefix(""); // ✅ Disabilita il prefisso "ROLE_"
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
                .csrf().disable() // ✅ Disabilitato per semplicità in fase di test
                .cors().disable()
                .authorizeHttpRequests()
                    // ✅ Accesso libero alle pagine pubbliche
                    .requestMatchers(HttpMethod.GET, "/", "/index", "/register", "/login", "/css/**", "/images/**", "/favicon.ico").permitAll()
                    .requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()
                    // Solo ADMIN
                    .requestMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
                    .requestMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
                    // Solo ADMIN e TRAINER
                    .requestMatchers(HttpMethod.GET, "/staff/**").hasAnyAuthority(ADMIN_ROLE, TRAINER_ROLE)
                    .requestMatchers(HttpMethod.POST, "/staff/**").hasAnyAuthority(ADMIN_ROLE, TRAINER_ROLE)
                    // Tutti gli altri endpoint richiedono autenticazione
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
