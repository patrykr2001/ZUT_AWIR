package edu.zut.awir.awir5.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsManager users(DataSource dataSource, PasswordEncoder encoder) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("""
                create table if not exists users (
                    username varchar(50) not null primary key,
                    password varchar(100) not null,
                    enabled boolean not null
                )
                """);
        jdbc.execute("""
                create table if not exists authorities (
                    username varchar(50) not null,
                    authority varchar(50) not null,
                    constraint fk_authorities_users foreign key(username) references users(username)
                )
                """);
        jdbc.execute("create unique index if not exists ix_auth_username_authority on authorities (username,authority)");

        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);

        UserDetails user = User.withUsername("user")
                .password(encoder.encode("user123"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        if (!manager.userExists("user")) {
            manager.createUser(user);
        }
        if (!manager.userExists("admin")) {
            manager.createUser(admin);
        }

        return manager;
    }

    @Bean
    SecurityFilterChain http(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/h2/**"))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers(
                                "/", "/error", "/login", "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/swagger-ui/**", "/v3/api-docs/**", "/h2/**"
                        ).permitAll()
                        .requestMatchers("/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"))
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/users/list", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .httpBasic(basic -> {
                })
                .rememberMe(rm -> rm
                        .rememberMeParameter("remember-me")
                        .key("awir-remember-me-secret-key")
                        .tokenValiditySeconds(60 * 60 * 24 * 14)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                )
                .headers(h -> h.frameOptions(f -> f.sameOrigin()));

        return http.build();
    }
}

