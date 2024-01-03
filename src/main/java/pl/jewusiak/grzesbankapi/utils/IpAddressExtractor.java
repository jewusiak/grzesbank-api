package pl.jewusiak.grzesbankapi.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IpAddressExtractor {
    
    public String getClientIpAddress(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
