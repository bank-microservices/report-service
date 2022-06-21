package com.nttdata.microservices.report.service.impl;

import static com.nttdata.microservices.report.util.MessageUtils.getMsg;

import com.nttdata.microservices.report.entity.credit.CreditProduct;
import com.nttdata.microservices.report.entity.transaction.Consumption;
import com.nttdata.microservices.report.entity.transaction.Payment;
import com.nttdata.microservices.report.exception.CreditNotFoundException;
import com.nttdata.microservices.report.proxy.CreditProductProxy;
import com.nttdata.microservices.report.proxy.TransactionProxy;
import com.nttdata.microservices.report.service.CreditReportService;
import com.nttdata.microservices.report.service.dto.BalanceDto;
import com.nttdata.microservices.report.service.dto.MovementDto;
import com.nttdata.microservices.report.service.dto.MovementType;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Class responsible for communicating with the Credit, Account and Transaction Microservices.
 */
@Service
@RequiredArgsConstructor
public class CreditReportServiceImpl implements CreditReportService {

  private final CreditProductProxy creditProductProxy;
  private final TransactionProxy transactionProxy;

  /**
   * If the credit exists, return it, otherwise throw an exception.
   *
   * @param accountNumber String
   * @return Mono of Credit
   */
  private Mono<CreditProduct> existCredit(String accountNumber) {
    return creditProductProxy.findCreditByAccountNumber(accountNumber)
        .switchIfEmpty(Mono.error(new CreditNotFoundException(getMsg("credit.not.found"))));
  }

  private Mono<CreditProduct> existCreditCard(String accountNumber) {
    return creditProductProxy.findCreditCardByAccountNumber(accountNumber)
        .switchIfEmpty(Mono.error(new CreditNotFoundException(getMsg("credit.card.not.found"))));
  }

  /**
   * It takes an account number, checks if it exists, and if it does,
   * it returns a BalanceDto object with the consumed, limit, available,
   * and accountNumber fields set.
   *
   * @param accountNumber String
   * @return A Mono of BalanceDto
   */
  @Override
  public Mono<BalanceDto> getBalanceCredit(String accountNumber) {
    return Mono.just(accountNumber)
        .flatMap(this::existCredit)
        .map(dto -> BalanceDto.builder()
            .consumed(dto.getAmount())
            .limit(dto.getCreditLimit())
            .available(dto.getCreditLimit() - dto.getAmount())
            .accountNumber(dto.getAccountNumber())
            .build());
  }

  @Override
  public Mono<BalanceDto> getBalanceCreditCard(String accountNumber) {
    return Mono.just(accountNumber)
        .flatMap(this::existCreditCard)
        .map(dto -> BalanceDto.builder()
            .consumed(dto.getAmount())
            .limit(dto.getCreditLimit())
            .available(dto.getCreditLimit() - dto.getAmount())
            .accountNumber(dto.getAccountNumber())
            .build());
  }

  /**
   * It takes a credit account number, checks if it exists,
   * then returns a Flux of MovementDto objects, which are either payments or consumptions,
   * sorted by date.
   *
   * @param accountNumber String
   * @return A Flux of MovementDto
   */
  @Override
  public Flux<MovementDto> findAllTransactionsCreditByAccountNumber(String accountNumber) {

    return Flux.just(accountNumber)
        .flatMap(this::existCredit)
        .flatMap(credit -> {
          final Flux<MovementDto> movementPay = findPaymentsByCreditId(credit)
              .map(payment -> MovementDto.builder()
                  .movementType(MovementType.PAYMENT)
                  .amount(payment.getAmount())
                  .transactionCode(payment.getTransactionCode())
                  .registerDate(payment.getRegisterDate())
                  .build());
          final Flux<MovementDto> movementCons = findConsumptionsByCreditId(credit)
              .map(consumption -> MovementDto.builder()
                  .movementType(MovementType.CONSUMPTION)
                  .amount(consumption.getAmount() * -1)
                  .transactionCode(consumption.getTransactionCode())
                  .registerDate(consumption.getRegisterDate())
                  .build());
          return Flux.concat(movementPay, movementCons)
              .sort(Comparator.comparing(MovementDto::getRegisterDate));
        })
        .subscribeOn(Schedulers.boundedElastic());

  }

  /**
   * > Find payments by credit id, if there are no payments, return an empty flux.
   *
   * @param credit Credit
   * @return A Flux of Payment objects.
   */
  private Flux<Payment> findPaymentsByCreditId(CreditProduct credit) {
    return transactionProxy.findPaymentByCreditId(credit.getId())
        .switchIfEmpty(Flux.empty());
  }


  /**
   * Find consumptions by credit id, if there are no consumptions, return an empty flux.
   *
   * @param credit Credit
   * @return A Flux of Consumption objects.
   */
  private Flux<Consumption> findConsumptionsByCreditId(CreditProduct credit) {
    return transactionProxy.findConsumptionByCreditId(credit.getId())
        .switchIfEmpty(Flux.empty());
  }


}
