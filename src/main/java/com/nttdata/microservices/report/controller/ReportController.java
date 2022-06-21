package com.nttdata.microservices.report.controller;

import com.nttdata.microservices.report.service.AccountReportService;
import com.nttdata.microservices.report.service.CreditReportService;
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

  private final AccountReportService accountReportService;
  private final CreditReportService creditReportService;

  @GetMapping("/balance/credit/{account-number}")
  public Mono<ResponseEntity<BalanceDto>> getBalanceCredit(
      @PathVariable("account-number") String accountNumber) {

    log.info("get Balance Credit by accountNumber: {}", accountNumber);
    return creditReportService.getBalanceCredit(accountNumber)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping("/balance/card/{account-number}")
  public Mono<ResponseEntity<BalanceDto>> getBalanceCreditCard(
      @PathVariable("account-number") String accountNumber) {

    log.info("get Balance Credit Card by accountNumber: {}", accountNumber);
    return creditReportService.getBalanceCreditCard(accountNumber)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping("/balance/account/{account-number}")
  public Mono<ResponseEntity<BalanceDto>> getBalanceAccount(
      @PathVariable("account-number") String accountNumber) {
    log.info("get Balance Account by accountNumber: {}", accountNumber);
    return accountReportService.getBalanceAccount(accountNumber)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping("/transaction/credit/{account-number}")
  public Flux<MovementDto> findAllTransactionsCredit(
      @PathVariable("account-number") String accountNumber) {
    log.info("find Movements Credit by accountNumber: {}", accountNumber);
    return creditReportService.findAllTransactionsCreditByAccountNumber(accountNumber);
  }

  @GetMapping("/transaction/account/{account-number}")
  public Flux<MovementDto> findAllAccountMovements(
      @PathVariable("account-number") String accountNumber) {
    log.info("find Movements Account by accountNumber: {}", accountNumber);
    return accountReportService.findAllTransactionsAccountByAccountNumber(accountNumber);
  }

}
