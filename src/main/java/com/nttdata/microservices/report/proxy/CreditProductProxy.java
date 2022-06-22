package com.nttdata.microservices.report.proxy;

import com.nttdata.microservices.report.entity.credit.CreditProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditProductProxy {

  Mono<CreditProduct> findCreditByAccountNumber(String accountNumber);

  Flux<CreditProduct> findCreditByClientDocument(String documentNumber);

  Mono<CreditProduct> findCreditCardByAccountNumber(String accountNumber);

  Flux<CreditProduct> findCreditCardByClientDocument(String documentNumber);
}
