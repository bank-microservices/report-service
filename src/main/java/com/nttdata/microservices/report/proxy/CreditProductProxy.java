package com.nttdata.microservices.report.proxy;

import com.nttdata.microservices.report.entity.credit.CreditProduct;
import reactor.core.publisher.Mono;

public interface CreditProductProxy {

  Mono<CreditProduct> findCreditByAccountNumber(String accountNumber);

  Mono<CreditProduct> findCreditCardByAccountNumber(String accountNumber);
}
