package pl.jewusiak.grzesbankapi.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import pl.jewusiak.grzesbankapi.model.domain.User;
import pl.jewusiak.grzesbankapi.model.response.AccountSummaryResponse;
import pl.jewusiak.grzesbankapi.model.service.TransactionService;

@Mapper(componentModel = "spring", uses = {TransactionMapper.class, TransactionService.class})
public abstract class ProfileMapper {
    @Autowired    
    protected TransactionService transactionService;
    
    @Autowired
    protected TransactionMapper transactionMapper;

    @Mapping(target = "name", expression = "java(user.getFirstName()+\" \"+user.getLastName())")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "accountNumber", source = "account.accountNumber")
    @Mapping(target = "balance", source = "account.balance")
    @Mapping(target = "lastTransactions", expression = "java(transactionMapper.map(transactionService.getLast5TransactionsForAccount(user.getAccount().getAccountNumber())))")
    public abstract AccountSummaryResponse map(User user);
}
