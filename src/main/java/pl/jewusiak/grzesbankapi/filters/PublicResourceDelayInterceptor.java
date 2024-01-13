package pl.jewusiak.grzesbankapi.filters;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Random;


@Component
@Slf4j
public class PublicResourceDelayInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        int millis = new Random().nextInt(1000, 2000);
        log.info("Waiting {} ms at PR Delay Interceptor.", millis);
        Thread.sleep(millis);
        log.info("{} ms have passed.", millis);
        return true;
    }
}
