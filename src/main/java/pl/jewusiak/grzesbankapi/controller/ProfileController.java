package pl.jewusiak.grzesbankapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.jewusiak.grzesbankapi.model.mapper.ResponseMapper;
import pl.jewusiak.grzesbankapi.model.request.ChangePasswordRequest;
import pl.jewusiak.grzesbankapi.model.service.AccountService;
import pl.jewusiak.grzesbankapi.model.service.AuthService;
import pl.jewusiak.grzesbankapi.model.service.UserService;
import pl.jewusiak.grzesbankapi.utils.EasterEggHandler;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final AccountService accountService;
    private final UserService userService;
    private final ResponseMapper responseMapper;
    private final AuthService authService;

    @GetMapping("/summary")
    public ResponseEntity<?> getMainPageSummary(Authentication authentication) {
        return ResponseEntity.ok(accountService.getAccountSummaryForUser(userService.getUser(authentication)));
    }
    
    @GetMapping("/sensitive/data")
    public ResponseEntity<?> getSensitiveUserData(Authentication authentication) {
        var user = userService.getUser(authentication);
        return ResponseEntity.ok(responseMapper.map(user.getSensitiveUserData()));
    }
    
    @GetMapping("/sensitive/cc")
    public ResponseEntity<?> getSensitiveCreditCard(Authentication authentication) {
        var user = userService.getUser(authentication);
        return ResponseEntity.ok(responseMapper.map(user.getCreditCard()));
    }

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.status(202).body("Welcome to Grzesbank, please hold to talk to a representative. You're %s in the queue.".formatted(EasterEggHandler.returnRandomSeqNumber()));
    }
    
    @PostMapping("/changepassword") 
    public ResponseEntity<?> changepassword(@RequestBody @Valid ChangePasswordRequest request, Authentication authentication) {
        var user = userService.getUser(authentication);
        authService.changePasswordForUser(user, request.getPassword(), false);
        return ResponseEntity.ok().build();
    }
}
