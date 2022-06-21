package com.nttdata.microservices.report.service;

import com.nttdata.microservices.report.service.dto.BalanceDto;
import com.nttdata.microservices.report.service.dto.MovementDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditReportService {

  Mono<BalanceDto> getBalanceCredit(String accountNumber);

  Mono<BalanceDto> getBalanceCreditCard(String accountNumber);

  Flux<MovementDto> findAllTransactionsCreditByAccountNumber(String accountNumber);

}
