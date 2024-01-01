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

@Slf4j(topic = "RAA PR")
@Component
@RequiredArgsConstructor
public class PasswordResetRaaInterceptor implements HandlerInterceptor {
    private final IpAddressExtractor ipAddressExtractor;
    private final ResourceAccessAttemptsService raaService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("PRE RAA PR: {} {}", request.getMethod(), request.getServletPath());
        String ip = ipAddressExtractor.getClientIpAddress(request);
        switch (request.getMethod()) {
            case "POST":
                if (!raaService.canIpAccessResetRequest(ip)) {
                    response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
                    log.info("Refused access to 1st step @ PR RAA Interceptor.");
                    return false;
                }
                break;
            case "PUT":
                if (!raaService.canIpAccessReset2ndStep(ip)) {
                    response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
                    log.info("Refused access to 2nd step @ PR RAA Interceptor.");
                    return false;
                }
                break;
            default:
                log.info("Request to PR is not POST/PUT. Skipping PRE PR RAA interceptor...");
                return true;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("AFT-COMPL RAA PR: {} {}", request.getMethod(), request.getServletPath());
        String ip = ipAddressExtractor.getClientIpAddress(request);
        switch (request.getMethod()) {
            case "POST":
                raaService.addIpResetRequestAttempt(ip);
                return;
            case "PUT":
                raaService.addIpReset2ndStepAttempt(ip, response.getStatus() == HttpServletResponse.SC_OK);
                return;
            default:
                log.info("Request to PR is not POST/PUT. Skipping POST PR RAA interceptor...");
        }
    }
}
