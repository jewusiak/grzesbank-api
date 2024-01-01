package pl.jewusiak.grzesbankapi.filters;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pl.jewusiak.grzesbankapi.service.ResourceAccessAttemptsService;
import pl.jewusiak.grzesbankapi.utils.IpAddressExtractor;

@Slf4j(topic = "RAA Login")
@Component
@RequiredArgsConstructor
public class LoginRaaInterceptor implements HandlerInterceptor {
    private final ResourceAccessAttemptsService raaService;
    private final IpAddressExtractor ipAddressExtractor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("PRE RAA Login: {} {}", request.getMethod(), request.getServletPath());
        if (!request.getMethod().equals("POST")) {
            log.info("Request to login is not POST. Skipping PRE LoginRaaInterceptor...");
            return true;
        }
        if (!raaService.canIpAccessLogin(ipAddressExtractor.getClientIpAddress(request))) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
            log.info("Refused access @ Login RAA Interceptor.");
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("AFT-COMPL RAA Login: {} {}", request.getMethod(), request.getServletPath());
        if (!request.getMethod().equals("POST")) {
            log.info("Request to login is not POST. Skipping POST LoginRaaInterceptor...");
            return;
        }
        raaService.addIpLoginAttempt(ipAddressExtractor.getClientIpAddress(request), response.getStatus() == HttpServletResponse.SC_OK);
    }
}