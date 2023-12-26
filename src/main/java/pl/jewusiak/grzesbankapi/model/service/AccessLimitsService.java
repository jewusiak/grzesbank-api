package pl.jewusiak.grzesbankapi.model.service;

import org.springframework.stereotype.Service;

@Service
public class AccessLimitsService {
    
    void addIpLoginAttempt(String ip, boolean successful) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    void addResetAttempt(String ip, boolean successful) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    boolean canAccessLogin(String ip){
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    boolean canAccessReset(String ip) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    String getIp(String servletAddr, String xforwarded) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
