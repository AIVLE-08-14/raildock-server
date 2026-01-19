package kr.co.raildock.raildock_server.config

import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
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
                    "/swagger-ui/**",
                    "/health",
                    "/docs/**",
                    "/v3/api-docs/**",
                    "/public/**").permitAll()
                    auth.anyRequest().authenticated()
            }
            .exceptionHandling { e ->
                e.authenticationEntryPoint { _, res, _ ->
                    res.status = HttpServletResponse.SC_UNAUTHORIZED
                    res.contentType = MediaType.APPLICATION_JSON_VALUE
                    res.writer.write("""{"message":"unauthorized"}""")
                }
            }
            .formLogin { form ->
                form.loginProcessingUrl("/auth/login")
                    .usernameParameter("employeeId")
                    .permitAll()
            }
            .logout { logout ->
                logout.logoutUrl("/logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("SESSION")
                    .permitAll()
            }
        return http.build()
    }
}