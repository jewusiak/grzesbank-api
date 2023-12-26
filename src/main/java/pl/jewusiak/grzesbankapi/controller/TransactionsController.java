package pl.jewusiak.grzesbankapi.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.jewusiak.grzesbankapi.exceptions.TransactionRejected;
import pl.jewusiak.grzesbankapi.model.mapper.TransactionMapper;
import pl.jewusiak.grzesbankapi.model.request.TransferOrderRequest;
import pl.jewusiak.grzesbankapi.model.service.TransactionService;
import pl.jewusiak.grzesbankapi.model.service.UserService;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionsController {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createTransferOrder(@RequestBody @Valid TransferOrderRequest transferOrderRequest, Authentication authentication) {
        if (transferOrderRequest.getAmount() == null || transferOrderRequest.getAmount().signum() != 1) {
            throw new TransactionRejected("Amount has to be >0");
        }
        var transaction = transactionMapper.map(transferOrderRequest);
        var user = userService.getUser(authentication);
        transactionMapper.insertUserAsSender(transaction, user);

        transactionService.processTransaction(transaction);
        return ResponseEntity.ok("OK");
    }

    @GetMapping
    public ResponseEntity<?> getUsersTransactions(@RequestParam(required = false, defaultValue = "10") int size, @RequestParam(required = false, defaultValue = "0") int page, Authentication authentication) {
        var user = userService.getUser(authentication);
        return ResponseEntity.ok(transactionService.getPagedTransactionsForAccount(user.getAccount().getAccountNumber(), PageRequest.of(page, size)).map(transactionMapper::map));
    }
}
