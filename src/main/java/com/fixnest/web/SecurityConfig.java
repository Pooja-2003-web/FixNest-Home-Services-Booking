package com.fixnest.web;
import org.springframework.beans.factory.annotation.Value; import org.springframework.context.annotation.*; import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager; import org.springframework.security.web.SecurityFilterChain; import org.springframework.web.servlet.config.annotation.*;
@Configuration
public class SecurityConfig implements WebMvcConfigurer {
  @Bean SecurityFilterChain chain(HttpSecurity http) throws Exception {
    http.csrf(c -> c.disable()).cors(Customizer.withDefaults())
        .authorizeHttpRequests(a -> a.requestMatchers("/api/admin/**").hasRole("ADMIN").anyRequest().permitAll())
        .httpBasic(Customizer.withDefaults());
    return http.build();
  }
  @Bean UserDetailsService users(@Value("${fixnest.admin.username}") String u, @Value("${fixnest.admin.password}") String p) {
    return new InMemoryUserDetailsManager(User.withUsername(u).password("{noop}" + p).roles("ADMIN").build());
  }
  @Override public void addCorsMappings(CorsRegistry r) { r.addMapping("/api/**").allowedOrigins("*").allowedMethods("GET","POST","PUT","PATCH","DELETE"); }
}
