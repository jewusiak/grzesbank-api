package pl.jewusiak.grzesbankapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.jewusiak.grzesbankapi.exceptions.InvalidResetPasswordToken;
import pl.jewusiak.grzesbankapi.exceptions.UserExistsException;
import pl.jewusiak.grzesbankapi.model.domain.*;
import pl.jewusiak.grzesbankapi.model.mapper.UserMapper;
import pl.jewusiak.grzesbankapi.model.request.RegistrationRequest;
import pl.jewusiak.grzesbankapi.model.response.PasswordCombinationResponse;
import pl.jewusiak.grzesbankapi.repository.PasswordResetRequestRepository;
import pl.jewusiak.grzesbankapi.repository.UserLoginAttemptRepository;
import pl.jewusiak.grzesbankapi.repository.UserRepository;
import pl.jewusiak.grzesbankapi.utils.PasswordCombinationsGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final UserLoginAttemptRepository userLoginAttemptRepository;
    private final PasswordCombinationsGenerator passwordCombinationsGenerator;
    @Value("${pl.jewusiak.grzesbankapi.passwordreset.urlprefix}")
    private String passwordResetUrlPrefix;

    @Value("pl.jewusiak.grzesbankapi.business.bank_acn")
    private String bankAccountNumber;

    @Transactional
    public void register(RegistrationRequest request) {
        request.setEmail(request.getEmail() != null ? request.getEmail().toLowerCase() : null);
        if (userRepository.existsById(request.getEmail())) {
            throw new UserExistsException(request.getEmail());
        }
        var user = userMapper.map(request);
        if (request.getInitialBalance() != null && request.getInitialBalance().compareTo(BigDecimal.ZERO) > 0) {
            var transaction = Transaction.builder()
                    .amount(request.getInitialBalance())
                    .senderAccountNumber(bankAccountNumber)
                    .senderName("Grzesbank - 1st Branch")
                    .senderAddress("Złota 10, Warsaw")
                    .recipientName(user.getFirstName() + " " + user.getLastName())
                    .recipientAddress(user.getAddress().toString())
                    .executionTime(ZonedDateTime.now())
                    .title("Initial balance")
                    .build();
            userService.createUser(user, transaction);
        } else {
            userService.createUser(user);
        }
    }
    
    public PasswordCombinationResponse getRandomPasswordCombination(String email) {
        return passwordCombinationsGenerator.getRandomPasswordCombination(userRepository.findById(email));
    }

    public Optional<User> auth(UUID pcid, String email, String password) {
        email = email.toLowerCase();
        var user = userRepository.findById(email);
        if (user.isPresent() && canUserAccessLogin(user.get())) {
            for (var pc : user.get().getPasswordCombinations()) {
                if (pc.getId().equals(pcid) && passwordEncoder.matches(password, pc.getPasswordHash())) {
                    addLoginAttempt(user.get(), true);
                    return user;
                }
            }
            addLoginAttempt(user.get(), false);
        }
        return Optional.empty();
    }

    private boolean canUserAccessLogin(User user) {
        if (user.isLoginLocked()) {
            log.info("User {} is already locked out until {}.", user.getEmail(), user.getLoginLockTime());
            return false;
        }
        return true;
    }

    public void generatePasswordResetRequest(String email) {
        email = email.toLowerCase();
        var user = userRepository.findById(email);
        if (user.isEmpty()) {
            log.info("Requested a password reset with non-existent email {}!", email);
            return;
        }
        var request = passwordResetRequestRepository.save(PasswordResetRequest.builder().user(user.get()).validity(ZonedDateTime.now().plusMinutes(20)).isUsed(false).build());
        log.info("Requested password reset for user {} who has been sent an email with a link {}{} to restore their password. Valid until {}.\nUUID: {}", request.getUser().getEmail(), passwordResetUrlPrefix, request.getId(), request.getValidity(), request.getId());
    }

    @Transactional
    public void changePasswordWithToken(UUID token, String newPass) {
        var req = passwordResetRequestRepository.findById(token).orElseThrow(() -> new InvalidResetPasswordToken("Token not found"));
        if (req.isUsed()) {
            throw new InvalidResetPasswordToken("Token has been already used.");
        }
        changePasswordForUser(req.getUser(), newPass, true);
        req.setUsed(true);
        passwordResetRequestRepository.save(req);
        userLoginAttemptRepository.overrideLoginsForUser(ZonedDateTime.now(), req.getUser());
    }

    public void changePasswordForUser(User user, String newPassword, boolean resetLoginLock) {
        
        List<PasswordCombination> combinations = passwordCombinationsGenerator.generatePasswordCombinations(newPassword);
        combinations.forEach(c -> c.setUser(user));
        user.getPasswordCombinations().clear();
        user.getPasswordCombinations().addAll(combinations);
        if (resetLoginLock) {
            user.setLoginLockTime(null);
        }
        userRepository.save(user);
    }

    private void addLoginAttempt(User user, boolean isSuccessful) {
        log.info("Adding {}successful login attempt for {}.", isSuccessful ? "" : "un", user.getEmail());
        var att = UserLoginAttempt.builder().successful(isSuccessful).date(ZonedDateTime.now()).user(user).build();
        userLoginAttemptRepository.save(att);
        if (isSuccessful) {
            userLoginAttemptRepository.overrideLoginsForUser(ZonedDateTime.now(), user);
            log.info("Overridden login attempts for user {}.", user.getEmail());
        } else {
            var numberOfAttempts = userLoginAttemptRepository.countLoginAttemptsByDateAfterAndUserAndOverrideDateIsNullAndSuccessfulIsFalse(
                    ZonedDateTime.now().minusMinutes(30), user);
            if (numberOfAttempts >= 5) {
                //block account for 1 hr
                user.setLoginLockTime(LocalDateTime.now().plusHours(1));
                userRepository.save(user);

                log.info("User {} has been locked out until {}.", user.getEmail(), user.getLoginLockTime().toString());
            }
        }
    }
}
