package pl.jewusiak.grzesbankapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.jewusiak.grzesbankapi.filters.LoginRaaInterceptor;
import pl.jewusiak.grzesbankapi.filters.PasswordResetRaaInterceptor;
import pl.jewusiak.grzesbankapi.filters.PublicResourceDelayInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginRaaInterceptor loginRaaInterceptor;
    private final PasswordResetRaaInterceptor passwordResetRaaInterceptor;
    private final PublicResourceDelayInterceptor publicResourceDelayInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginRaaInterceptor).addPathPatterns("/auth/login");
        registry.addInterceptor(passwordResetRaaInterceptor).addPathPatterns("/auth/resetpassword");
        registry.addInterceptor(publicResourceDelayInterceptor).addPathPatterns("/auth", "/auth/**").order(Ordered.HIGHEST_PRECEDENCE);
    }

}
