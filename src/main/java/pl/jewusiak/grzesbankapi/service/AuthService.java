package pl.jewusiak.grzesbankapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.jewusiak.grzesbankapi.exceptions.InvalidResetPasswordToken;
import pl.jewusiak.grzesbankapi.model.domain.*;
import pl.jewusiak.grzesbankapi.model.mapper.UserMapper;
import pl.jewusiak.grzesbankapi.model.request.RegistrationRequest;
import pl.jewusiak.grzesbankapi.model.response.PasswordCombinationResponse;
import pl.jewusiak.grzesbankapi.repository.PasswordResetRequestRepository;
import pl.jewusiak.grzesbankapi.repository.UserLoginAttemptRepository;
import pl.jewusiak.grzesbankapi.repository.UserRepository;
import pl.jewusiak.grzesbankapi.utils.AccountFactory;
import pl.jewusiak.grzesbankapi.utils.CreditCardFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final CreditCardFactory creditCardFactory;
    private final AccountFactory accountFactory;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final UserLoginAttemptRepository userLoginAttemptRepository;
    private final ResourceAccessAttemptsService raaService;
    @Value("${pl.jewusiak.grzesbankapi.passwordreset.urlprefix}")
    private String passwordResetUrlPrefix;

    @Value("pl.jewusiak.grzesbankapi.business.bank_acn")
    private String bankAccountNumber;

    @Transactional
    public void register(RegistrationRequest request) {
        request.setEmail(request.getEmail() != null ? request.getEmail().toLowerCase() : null);
        var user = userMapper.mapBasicData(request);
        List<PasswordCombination> combinations = generatePasswordCombinations(request.getPassword(), user);
        user.setPasswordCombinations(combinations);
        var account = accountFactory.prepareAccount(user);
        user.setAccount(account);
        var cc = creditCardFactory.prepareCard(user);
        user.setCreditCard(cc);
        var savedUser = userRepository.save(user);
        if (request.getInitialBalance() != null && request.getInitialBalance().compareTo(BigDecimal.ZERO) > 0) {
            transactionService.processTransaction(Transaction.builder()
                    .amount(request.getInitialBalance())
                    .senderAccountNumber(bankAccountNumber)
                    .senderName("Grzesbank - initial balance Acc")
                    .senderAddress("1 Grzesbank Strasse, Berlin")
                    .recipientName(user.getFirstName() + " " + user.getLastName())
                    .recipientAddress(user.getAddress().toString())
                    .recipientAccountNumber(savedUser.getAccount().getAccountNumber())
                    .executionTime(ZonedDateTime.now())
                    .title("Initial balance")
                    .build());
        }
    }


    public List<PasswordCombination> generatePasswordCombinations(String rawPassword, User user) {
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("Password has to be >= 8 chars long.");
        }
        List<PasswordCombination> list = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            var pc = new PasswordCombination();
            pc.setIndices(selectIndices(rawPassword.length()));
            var selectedChars = Stream.of(pc.getIndices())
                    .map(rawPassword::charAt)
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString();
            var hashedPassword = passwordEncoder.encode(selectedChars);
            pc.setPasswordHash(hashedPassword);
            pc.setUser(user);
            list.add(pc);
        }
        return list;
    }

    private Integer[] selectIndices(int length) {
        List<Integer> indices = new ArrayList<>(5);
        Random random = new Random();
        while (indices.size() < 5) {
            var index = random.nextInt(length);
            if (!indices.contains(index)) {
                indices.add(index);
            }
        }
        return indices.stream().mapToInt(i -> i).sorted().boxed().toArray(Integer[]::new);
    }

    public PasswordCombinationResponse getRandomPasswordCombination(String email) {
        email = email.toLowerCase();
        var opt = userRepository.findById(email);
        if (opt.isEmpty()) {
            // return dummy password combination response with password between 8 and 16 chars
            return PasswordCombinationResponse.builder().pcid(UUID.randomUUID()).indices(selectIndices(new Random().nextInt(8, 17))).build();
        }
        List<PasswordCombination> passwordCombinations = opt.get().getPasswordCombinations();
        var pc = passwordCombinations.get(new Random().nextInt(passwordCombinations.size()));
        return PasswordCombinationResponse.builder().pcid(pc.getId()).indices(pc.getIndices()).build();
    }

    public Optional<User> auth(UUID pcid, String email, String password) {
        email = email.toLowerCase();
        var user = userRepository.findById(email);
        if (user.isPresent() && canUserAccessLogin(user.get())) {
            //todo: remove bypass
//            if (true)
//                return user;
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
        List<PasswordCombination> combinations = generatePasswordCombinations(newPassword, user);
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
