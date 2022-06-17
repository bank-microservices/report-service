package com.nttdata.microservices.report.proxy;

import com.nttdata.microservices.report.entity.transaction.Consumption;
import com.nttdata.microservices.report.entity.transaction.Payment;
import com.nttdata.microservices.report.entity.transaction.Transaction;
import reactor.core.publisher.Flux;

public interface TransactionProxy {

  Flux<Consumption> findConsumptionByCreditId(String creditId);

  Flux<Payment> findPaymentByCreditId(String creditId);

  Flux<Transaction> findTransactionByAccountId(String accountId);
}
