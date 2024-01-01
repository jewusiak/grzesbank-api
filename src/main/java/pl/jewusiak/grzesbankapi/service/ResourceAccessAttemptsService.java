package pl.jewusiak.grzesbankapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.jewusiak.grzesbankapi.model.domain.IpAccessAttempt;
import pl.jewusiak.grzesbankapi.repository.IpAccessAttemptRepository;
import pl.jewusiak.grzesbankapi.utils.IpAddressExtractor;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceAccessAttemptsService {
    private final IpAccessAttemptRepository repository;


    public void addIpLoginAttempt(boolean successful) {
        String ip = getIp();
        ZonedDateTime now = ZonedDateTime.now();
        if (successful) {
            ZonedDateTime since = now.minusMinutes(5);
            repository.overrideAccessAttemptsForResource(now, IpAccessAttempt.AccessibleResource.IP_LOGIN, ip, since);
            log.info("Overridden IP access for {} since {} at now({}), due to successful login.", ip, since, now);
            log.info("Added successful login attempt for IP {} at {}.", ip, now);
        } else log.info("Added failed login attempt for IP {} at {}.", ip, now);
        repository.save(IpAccessAttempt.builder().ip(ip).date(ZonedDateTime.now()).successful(successful).resource(IpAccessAttempt.AccessibleResource.IP_LOGIN).build());
    }

    public void addIpResetRequestAttempt() {
        String ip = getIp();
        log.info("Added PR request (1st step) for IP {}.", ip);
        repository.save(IpAccessAttempt.builder().ip(ip).resource(IpAccessAttempt.AccessibleResource.IP_PASS_RESET_REQUEST).date(ZonedDateTime.now()).build());
    }

    public void addIpReset2ndStepAttempt(boolean successful) {
        String ip = getIp();
        ZonedDateTime now = ZonedDateTime.now();
        if (successful) {
            //override 1st step requests
            ZonedDateTime since = now.minusMinutes(5);
            repository.overrideAccessAttemptsForResource(now, IpAccessAttempt.AccessibleResource.IP_PASS_RESET_2ND, ip, since);
            log.info("Overridden IP access {} since {} at now({}), due to successful PR 2nd step.", ip, since, now);
            log.info("Added PR 2nd step attempt for IP {} at {}.", ip, now);
        } else log.info("Added unsuccessful attempt of 2nd step PR for IP {}.", ip);
        repository.save(IpAccessAttempt.builder().ip(ip).date(now).successful(successful).resource(IpAccessAttempt.AccessibleResource.IP_PASS_RESET_2ND).build());
    }

    public boolean canIpAccessLogin() {
        String ip = getIp();
        //ip logins in the last 5 minutes
        int ipLogins = repository.countResourceAccessAttemptsByResourceAndIpAndDateAfterAndOverrideDateIsNullAndSuccessfulIsFalse(IpAccessAttempt.AccessibleResource.IP_LOGIN, ip, ZonedDateTime.now().minusMinutes(5));
        if (ipLogins >= 7) {
            log.info("IP {} has already tried to access login 7 times in the last 5 minutes.", ip);
            return false;
        }
        log.info("IP {} was granted access to login.", ip);
        return true;
    }

    public boolean canIpAccessResetRequest() {
        String ip = getIp();
        //ip reset requests in the last 5 mins (max. 1)
        int ipRequests = repository.countResourceAccessAttemptsByResourceAndIpAndDateAfterAndOverrideDateIsNull(IpAccessAttempt.AccessibleResource.IP_PASS_RESET_REQUEST, ip, ZonedDateTime.now().minusMinutes(5));
        if (ipRequests >= 1) {
            log.info("IP {} has already tried to access reset password request in the last 5 minutes.", ip);
            return false;
        }
        log.info("IP {} was granted access to PR 1st step.", ip);
        return true;
    }

    public boolean canIpAccessReset2ndStep() {
        String ip = getIp();
        //ip reset requests in the last 5 mins
        int ipRequests = repository.countResourceAccessAttemptsByResourceAndIpAndDateAfterAndOverrideDateIsNullAndSuccessfulIsFalse(IpAccessAttempt.AccessibleResource.IP_PASS_RESET_2ND, ip, ZonedDateTime.now().minusMinutes(5));
        if (ipRequests >= 1) {
            log.info("IP {} has already tried to access PR 2nd step with incorrect token in the last 5 minutes.", ip);
            return false;
        }
        log.info("IP {} was granted access to PR 2nd step.", ip);
        return true;
    }

    public String getIp() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            throw new RuntimeException("Failed to extract IP address");
        }
        return IpAddressExtractor.getClientIpAddressIfServletRequestExist(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }
}
