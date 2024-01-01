package pl.jewusiak.grzesbankapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.jewusiak.grzesbankapi.exceptions.InvalidResetPasswordToken;
import pl.jewusiak.grzesbankapi.model.mapper.ResponseMapper;
import pl.jewusiak.grzesbankapi.model.request.ChangePasswordWithTokenRequest;
import pl.jewusiak.grzesbankapi.model.request.LoginRequest;
import pl.jewusiak.grzesbankapi.model.request.RegistrationRequest;
import pl.jewusiak.grzesbankapi.model.response.PasswordCombinationResponse;
import pl.jewusiak.grzesbankapi.service.AuthService;
import pl.jewusiak.grzesbankapi.service.ResourceAccessAttemptsService;
import pl.jewusiak.grzesbankapi.utils.IpAddressExtractor;
import pl.jewusiak.grzesbankapi.utils.ValidationService;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final ResponseMapper responseMapper;
    private final ResourceAccessAttemptsService raaService;

    @GetMapping("/login")
    @SneakyThrows
    public ResponseEntity<PasswordCombinationResponse> getPasswordCombinations(@RequestParam @Pattern(regexp = ValidationService.emailRegex) @NotBlank String email) {
        Thread.sleep(1500);
        return ResponseEntity.ok(authService.getRandomPasswordCombination(email));
    }

    @PostMapping("/login")
    @SneakyThrows
    @Transactional
    public ResponseEntity<?> auth(@RequestBody @Valid LoginRequest loginRequest, HttpSession session, HttpServletRequest request) {
        Thread.sleep(1500);
        if (!raaService.canIpAccessLogin()) return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        var userOpt = authService.auth(loginRequest.getPcid(), loginRequest.getEmail(), loginRequest.getPassword());
        if (userOpt.isPresent()) {
            SecurityContext sc = SecurityContextHolder.getContext();
            var auth = UsernamePasswordAuthenticationToken.authenticated(userOpt.get().getEmail(), null, userOpt.get().getAuthorities());
            sc.setAuthentication(auth);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
            raaService.addIpLoginAttempt(true);
            return ResponseEntity.ok(responseMapper.map(userOpt.get()));
        }
        raaService.addIpLoginAttempt(false);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login credentials");
    }

    @PostMapping("/register")
    @SneakyThrows
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) {
        Thread.sleep(1500);
        authService.register(request);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/resetpassword")
    @SneakyThrows
    public ResponseEntity<?> resetPassword(@RequestParam @Pattern(regexp = ValidationService.emailRegex) @NotBlank String email) {
        Thread.sleep(1500);
        if (!raaService.canIpAccessResetRequest()) return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        authService.generatePasswordResetRequest(email);
        raaService.addIpResetRequestAttempt();
        return ResponseEntity.ok("OK");
    }

    @PutMapping("/resetpassword")
    @SneakyThrows
    public ResponseEntity<?> doResetPassword(@RequestBody @Valid ChangePasswordWithTokenRequest request) {
        Thread.sleep(1500);
        if (!raaService.canIpAccessReset2ndStep()) return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        try {
            authService.changePasswordWithToken(request.getToken(), request.getPassword());
        } catch (InvalidResetPasswordToken e) {
            raaService.addIpReset2ndStepAttempt(false);
            return ResponseEntity.badRequest().build();
        }
        raaService.addIpReset2ndStepAttempt(true);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/ip")
    public String ip(HttpServletRequest request) {
        String ip = IpAddressExtractor.getClientIpAddressIfServletRequestExist(request);
        return ip;
    }
}
