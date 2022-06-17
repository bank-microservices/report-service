package com.nttdata.microservices.report.service;

import com.nttdata.microservices.report.service.dto.BalanceDto;
import com.nttdata.microservices.report.service.dto.MovementDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReportService {

  Mono<BalanceDto> getBalanceCredit(String accountNumber);

  Flux<MovementDto> findAllCreditMovementsByAccountNumber(String accountNumber);

  Mono<BalanceDto> getBalanceAccount(String accountNumber);

  Flux<MovementDto> findAllAccountMovementsByAccountNumber(String accountNumber);

}
