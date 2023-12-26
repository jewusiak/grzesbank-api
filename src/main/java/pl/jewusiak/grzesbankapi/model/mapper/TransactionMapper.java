package pl.jewusiak.grzesbankapi.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.jewusiak.grzesbankapi.model.domain.Transaction;
import pl.jewusiak.grzesbankapi.model.domain.User;
import pl.jewusiak.grzesbankapi.model.request.TransferOrderRequest;
import pl.jewusiak.grzesbankapi.model.response.TransactionSummaryDto;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "executionTime", target = "date")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "amount", target = "amount")
    @Mapping(expression = "java(transaction.getAmount().signum()==1 ? transaction.getSenderName() : transaction.getRecipientName())", target = "contraSideName")
    TransactionSummaryDto map(Transaction transaction);
    
    Iterable<TransactionSummaryDto> map(Iterable<Transaction> transactions);
    
    @Mapping(source = "recipientName", target = "recipientName")
    @Mapping(source = "recipientAddress", target = "recipientAddress")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "recipientAccountNumber", target = "recipientAccountNumber")
    Transaction map(TransferOrderRequest transferOrderRequest);
    
    @Mapping(expression = "java(user.getFirstName()+\" \"+user.getLastName())", target = "senderName")
    @Mapping(expression = "java(user.getAddress().toString())", target = "senderAddress")
    @Mapping(source = "user.account.accountNumber", target = "senderAccountNumber")
    void insertUserAsSender(@MappingTarget Transaction transaction, User user);
}
