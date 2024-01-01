package pl.jewusiak.grzesbankapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.jewusiak.grzesbankapi.filters.LoginRaaInterceptor;
import pl.jewusiak.grzesbankapi.filters.PasswordResetRaaInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginRaaInterceptor loginRaaInterceptor;
    private final PasswordResetRaaInterceptor passwordResetRaaInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginRaaInterceptor).addPathPatterns("/auth/login");
        registry.addInterceptor(passwordResetRaaInterceptor).addPathPatterns("/auth/resetpassword");
    }

}
