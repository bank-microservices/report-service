package com.nttdata.microservices.report.proxy;

import com.nttdata.microservices.report.entity.credit.Credit;
import reactor.core.publisher.Mono;

public interface CreditProxy {

  Mono<Credit> findById(String id);

  Mono<Credit> findByAccountNumber(String accountNumber);

}
