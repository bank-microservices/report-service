package com.nttdata.microservices.report.service.impl;

import static com.nttdata.microservices.report.util.MessageUtils.getMsg;

import com.nttdata.microservices.report.entity.account.Account;
import com.nttdata.microservices.report.entity.account.AccountType;
import com.nttdata.microservices.report.entity.transaction.Transaction;
import com.nttdata.microservices.report.exception.AccountException;
import com.nttdata.microservices.report.proxy.AccountProxy;
import com.nttdata.microservices.report.proxy.TransactionProxy;
import com.nttdata.microservices.report.service.AccountReportService;
import com.nttdata.microservices.report.service.dto.BalanceDto;
import com.nttdata.microservices.report.service.dto.MovementDto;
import com.nttdata.microservices.report.service.dto.MovementType;
import com.nttdata.microservices.report.service.dto.ProductFeeDto;
import com.nttdata.microservices.report.util.Sum;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Class responsible for communicating with the Credit, Account and Transaction Microservices.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountReportServiceImpl implements AccountReportService {

  private final AccountProxy accountProxy;
  private final TransactionProxy transactionProxy;

  /**
   * If the account exists, return it, otherwise throw an exception.
   *
   * @param accountNumber The account number of the account to be debited.
   * @return Mono of Account
   */
  private Mono<Account> existAccount(String accountNumber) {
    return accountProxy.findByAccountNumber(accountNumber)
        .switchIfEmpty(Mono.error(new AccountException(getMsg("account.not.found"))));
  }

  /**
   * If the account exists, return a Mono of the BalanceDto, otherwise return a Mono of the error
   * message.
   *
   * @param accountNumber String
   * @return A Mono of BalanceDto
   */
  @Override
  public Mono<BalanceDto> getBalanceAccount(String accountNumber) {
    return Mono.just(accountNumber)
        .flatMap(this::existAccount)
        .map(dto -> BalanceDto.builder()
            .balance(dto.getAmount())
            .accountType(dto.getAccountType().getDescription())
            .accountNumber(dto.getAccountNumber())
            .build());
  }

  /**
   * It takes an account number, checks if the account exists, then finds all transactions for that
   * account, and returns a list of movements for that account.
   *
   * @param accountNumber String
   * @return A Flux of MovementDto
   */
  @Override
  public Flux<MovementDto> findAllTransactionsAccountByAccountNumber(String accountNumber) {
    return Flux.just(accountNumber)
        .flatMap(this::existAccount)
        .flatMap(this::findTransactionByAccountId)
        .map(transaction -> MovementDto.builder()
            .movementType(MovementType.valueOf(transaction.getTransactionType().name()))
            .amount(this.getAmountByTransactionType(transaction))
            .transactionCode(transaction.getTransactionCode())
            .registerDate(transaction.getRegisterDate())
            .build()
        )
        .sort(Comparator.comparing(MovementDto::getRegisterDate))
        .subscribeOn(Schedulers.boundedElastic());
  }

  @Override
  public Flux<ProductFeeDto> findTransactionFeeAccountsByRangeDate(String dateFrom,
                                                                   String dateTo) {
    return transactionProxy.findByDateRange(dateFrom, dateTo)
        .groupBy(transaction -> new AccountType(
            transaction.getAccount().getAccountType().getId(),
            transaction.getAccount().getAccountType().getCode()))
        .flatMap(keyGroup -> keyGroup
            .map(Transaction::getTransactionFee)
            .reduce(Sum.empty(), Sum::add)
            .map(sum -> ProductFeeDto.builder()
                .totalFee(sum.getValue())
                .accountType(new AccountType(keyGroup.key().getId(), keyGroup.key().getCode()))
                .build()))
        .flatMap(this::findAccountTypeById)
        .sort(Comparator.comparing(ProductFeeDto::getTotalFee));
  }

  private Mono<ProductFeeDto> findAccountTypeById(ProductFeeDto feeDto) {
    return accountProxy.findAccountTypeById(feeDto.getAccountType().getId())
        .doOnNext(feeDto::setAccountType)
        .thenReturn(feeDto);
  }

  /**
   * If the transaction type is a deposit, return the amount multiplied by -1.
   *
   * @param transaction The transaction object.
   * @return The amount of the transaction, or the amount of the transaction multiplied by -1.
   */
  private Double getAmountByTransactionType(Transaction transaction) {
    return transaction.getTransactionType().isDeposit()
        ? transaction.getAmount() : transaction.getAmount() * -1;
  }

  /**
   * If the transactionProxy returns a Flux of transactions, return it.
   * Otherwise, return an empty Flux.
   *
   * @param account The account object that is being passed in.
   * @return A Flux of Transactions
   */
  private Flux<Transaction> findTransactionByAccountId(Account account) {
    return transactionProxy.findTransactionByAccountId(account.getId())
        .switchIfEmpty(Flux.empty());
  }

}
