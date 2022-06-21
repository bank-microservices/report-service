package com.nttdata.microservices.report.service;

import com.nttdata.microservices.report.service.dto.BalanceDto;
import com.nttdata.microservices.report.service.dto.MovementDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountReportService {

  Mono<BalanceDto> getBalanceAccount(String accountNumber);

  Flux<MovementDto> findAllTransactionsAccountByAccountNumber(String accountNumber);

}
