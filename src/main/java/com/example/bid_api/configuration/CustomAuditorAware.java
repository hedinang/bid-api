//package com.example.bid_api.configuration;
//
//import org.example.authentication.model.entity.User;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.Optional;
//
//@Configuration
//@EnableJpaAuditing(auditorAwareRef = "auditorAware")
//public class CustomAuditorAware implements AuditorAware<String> {
//    @Bean
//    public AuditorAware<String> auditorAware() {
//        return new CustomAuditorAware();
//    }
//
//    @Override
//    public Optional<String> getCurrentAuditor() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null && authentication.isAuthenticated()) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof User user) {
//                return Optional.of(user.getUserCode());
//            }
//        }
//
//        return Optional.empty();
//    }
//}
