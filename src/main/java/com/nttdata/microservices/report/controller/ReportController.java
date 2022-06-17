package com.nttdata.microservices.report.controller;

import com.nttdata.microservices.report.service.ReportService;
import com.nttdata.microservices.report.service.dto.BalanceDto;
import com.nttdata.microservices.report.service.dto.MovementDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/report")
public class ReportController {

  private final ReportService reportService;

  @GetMapping("/balance/credit/{accountNumber}")
  public Mono<ResponseEntity<BalanceDto>> getBalanceCredit(@PathVariable String accountNumber) {
    log.info("get Balance Credit by accountNumber: {}", accountNumber);
    return reportService.getBalanceCredit(accountNumber)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping("/balance/account/{accountNumber}")
  public Mono<ResponseEntity<BalanceDto>> getBalanceAccount(@PathVariable String accountNumber) {
    log.info("get Balance Account by accountNumber: {}", accountNumber);
    return reportService.getBalanceAccount(accountNumber)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping("/movement/credit/{accountNumber}")
  public Flux<MovementDto> findAllCreditMovements(@PathVariable String accountNumber) {
    log.info("find Movements Credit by accountNumber: {}", accountNumber);
    return reportService.findAllCreditMovementsByAccountNumber(accountNumber);
  }

  @GetMapping("/movement/account/{accountNumber}")
  public Flux<MovementDto> findAllAccountMovements(@PathVariable String accountNumber) {
    log.info("find Movements Account by accountNumber: {}", accountNumber);
    return reportService.findAllAccountMovementsByAccountNumber(accountNumber);
  }

}
