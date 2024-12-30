package com.example.bid_api.configuration;
//
//import lombok.RequiredArgsConstructor;
//import org.apache.catalina.connector.Connector;
////import org.example.authentication.model.dto.CustomUserDetails;
////import org.example.authentication.model.entity.User;
////import org.example.authentication.repository.postgres.UserRepository;
////import org.example.authentication.util.jwt.JwtAuthenticationFilter;
//import org.hibernate.service.spi.ServiceException;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.server.WebServerFactoryCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.AuditorAware;
////import org.springframework.security.authentication.AuthenticationManager;
////import org.springframework.security.authentication.AuthenticationProvider;
////import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
////import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
////import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
////import org.springframework.security.config.http.SessionCreationPolicy;
////import org.springframework.security.core.Authentication;
////import org.springframework.security.core.context.SecurityContextHolder;
////import org.springframework.security.core.userdetails.UserDetailsService;
////import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
////import org.springframework.security.crypto.password.PasswordEncoder;
////import org.springframework.security.web.SecurityFilterChain;
////import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//@Configuration
////@EnableWebSecurity
////@EnableMethodSecurity
//@RequiredArgsConstructor
//public class WebSecurityConfig {
////    private final UserRepository userRepository;
//
////    @Bean
////    AuditorAware<String> auditorProvider() {
////        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////
////        if (authentication == null || !authentication.isAuthenticated()) {
////            return null;
////        }
////
////        return () -> Optional.ofNullable(((User) authentication.getPrincipal()).getUserCode());
////    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        final CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://localhost", "http://localhost:80", "http://localhost:3000", "http://localhost:8000", "http://10.1.1.230:3000", "http://10.1.1.230", "http://10.1.1.230:80", "http://10.1.1.230:8000", "http://winitechvina.iptime.org"));
//        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTION"));
//        configuration.setAllowCredentials(true);
//        // configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
////    @Bean
////    public PasswordEncoder passwordEncoder() {
////        return new BCryptPasswordEncoder();
////    }
////
////    @Bean
////    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
////        return config.getAuthenticationManager();
////    }
////
////    @Bean
////    UserDetailsService userDetailsService() {
////        return userId -> new CustomUserDetails(userRepository.findByUserId(userId).orElseThrow(() -> new ServiceException("User not found")));
////    }
////
////    @Bean
////    AuthenticationProvider authenticationProvider() {
////        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
////        authProvider.setUserDetailsService(userDetailsService());
////        authProvider.setPasswordEncoder(passwordEncoder());
////
////        return authProvider;
////    }
////
////    @Bean
////    public JwtAuthenticationFilter authenticationJwtTokenFilter() {
////        return new JwtAuthenticationFilter();
////    }
////
////    String[] resources = new String[]{"plugins/**", "login", "css/**", "icons/**", "images/**", "js/**", "res/**",};
////
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////
////        http.cors().and().csrf().disable()
////                .authorizeHttpRequests(req -> req.requestMatchers("/api/auth/login").permitAll()
////                        .requestMatchers("/api/auth/login").permitAll()
////                        .requestMatchers("/api/upload/**").permitAll()
////                        .requestMatchers("/api/**").authenticated()
////                        .requestMatchers("/").authenticated()
////                        .requestMatchers("/script/**").permitAll()
////                        .requestMatchers(resources).permitAll().anyRequest().permitAll()
////                ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////
////                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
////
////        return http.build();
////    }
//
////    @Bean
////    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
////        return factory -> factory.addAdditionalTomcatConnectors(createHttpConnector());
////    }
////
////    private Connector createHttpConnector() {
////        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
////        connector.setScheme("http");
////        connector.setPort(9000);  // Set the port for HTTP
////        connector.setSecure(false);
////        return connector;
////    }
//}


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply to all endpoints
                .allowedOrigins("http://localhost:3000", "http://103.75.182.242", "https://stjtrading.com", "http://stjtrading.com") // Allow your frontend origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow credentials if needed
    }
}