package com.nttdata.microservices.report.proxy;

import com.nttdata.microservices.report.entity.account.Account;
import com.nttdata.microservices.report.entity.account.AccountType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountProxy {

  Flux<Account> findByAccountNumberAndClientDocument(String accountNumber, String documentNumber);

  Mono<Account> findByAccountNumber(String accountNumber);

  Mono<AccountType> findAccountTypeById(String accountTypeId);

  Flux<Account> findByClientDocument(String documentNumber);

}
