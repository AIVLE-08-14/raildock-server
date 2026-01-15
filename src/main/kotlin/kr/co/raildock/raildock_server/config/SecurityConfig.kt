package kr.co.raildock.raildock_server.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.disable() }
            .csrf { it.disable() }
            .authorizeHttpRequests {auth ->
                auth.requestMatchers(
                    "/auth/**",
                    "/login",
                    "/logout",
                    "/swagger-ui/**",
                    "/docs",
                    "/v3/api-docs/**",
                    "/public/**").permitAll()
                    auth.anyRequest().authenticated()
            }
            .formLogin { form ->
                form.loginProcessingUrl("/login")
                    .usernameParameter("email")
                    .permitAll()
            }
            .logout { logout ->
                logout.logoutUrl("/logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            }
        return http.build()
    }
}