package com.nttdata.microservices.report.controller;

import com.nttdata.microservices.report.service.AccountReportService;
import com.nttdata.microservices.report.service.CommonReportService;
import com.nttdata.microservices.report.service.CreditReportService;
import com.nttdata.microservices.report.service.dto.BalanceDto;
import com.nttdata.microservices.report.service.dto.MovementDto;
import com.nttdata.microservices.report.service.dto.ProductDto;
import com.nttdata.microservices.report.service.dto.ProductFeeDto;
import com.nttdata.microservices.report.util.validator.ValidDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/report")
public class ReportController {

  private final AccountReportService accountReportService;
  private final CreditReportService creditReportService;
  private final CommonReportService commonReportService;

  @GetMapping("/client/{document-number}")
  public Flux<ProductDto> findAllBankProduct(
      @PathVariable("document-number") String documentNumber) {

    log.info("find all Bank Product by documentNumber: {}", documentNumber);
    return commonReportService.findAllBankProductByClientDocument(documentNumber);
  }

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

  @GetMapping("/transaction/date-range/{date-from}/{date-to}")
  public Flux<ProductFeeDto> findByDateRange(@PathVariable("date-from")
                                             @ValidDate String dateFrom,
                                             @PathVariable("date-to")
                                             @ValidDate String dateTo) {
    return accountReportService.findTransactionFeeAccountsByRangeDate(dateFrom, dateTo);
  }

}
