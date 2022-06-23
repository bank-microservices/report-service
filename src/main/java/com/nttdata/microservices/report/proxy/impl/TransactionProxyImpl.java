package com.nttdata.microservices.report.proxy.impl;

import static com.nttdata.microservices.report.util.MessageUtils.getMsg;

import com.nttdata.microservices.report.entity.transaction.Consumption;
import com.nttdata.microservices.report.entity.transaction.Payment;
import com.nttdata.microservices.report.entity.transaction.Transaction;
import com.nttdata.microservices.report.exception.TransactionException;
import com.nttdata.microservices.report.proxy.TransactionProxy;
import com.nttdata.microservices.report.util.RestUtils;
import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Service
public class TransactionProxyImpl implements TransactionProxy {

  private static final String STATUS_CODE = "Status code : {}";
  private final WebClient webClient;

  public TransactionProxyImpl(@Value("${service.transaction.uri}") final String url,
                              WebClient.Builder loadBalancedWebClientBuilder) {
    this.webClient = loadBalancedWebClientBuilder
        .clientConnector(RestUtils.getDefaultClientConnector())
        .baseUrl(url)
        .build();
  }

  @Override
  public final Flux<Consumption> findConsumptionByCreditId(final String creditId) {
    String errorMessage = getMsg("transaction.consumption.not.available", creditId);
    return this.webClient.get()
        .uri("/consumption/credit/{credit-id}", creditId)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToFlux(Consumption.class)
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)));
  }

  @Override
  public final Flux<Payment> findPaymentByCreditId(final String creditId) {
    String errorMessage = getMsg("transaction.payment.not.available", creditId);
    return this.webClient.get()
        .uri("/payment/credit/{credit-id}", creditId)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToFlux(Payment.class)
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)));
  }

  @Override
  public final Flux<Transaction> findTransactionByAccountId(final String accountId) {
    String errorMessage = getMsg("transaction.account.not.available", accountId);
    return this.webClient.get()
        .uri("/account/{account-id}", accountId)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToFlux(Transaction.class)
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)));
  }

  @Override
  public Flux<Transaction> findByDateRange(String from, String to) {
    String errorMessage = getMsg("transaction.range.date.not.available", from, to);
    Map<String, String> params = Map
        .of("date-from", from,
            "date-to", to);
    return this.webClient.get()
        .uri("/date-range/{date-from}/{date-to}", params)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToFlux(Transaction.class)
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)));
  }

  private Mono<? extends Throwable> applyError4xx(final ClientResponse clientResponse,
                                                  final String errorMessage) {
    log.info(STATUS_CODE, clientResponse.statusCode().value());
    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
      return Mono.error(
          new TransactionException(errorMessage, clientResponse.statusCode().value()));
    }
    return clientResponse.bodyToMono(String.class)
        .flatMap(response -> Mono.error(
            new TransactionException(response, clientResponse.statusCode().value())));
  }

  private Mono<? extends Throwable> applyError5xx(final ClientResponse clientResponse) {
    log.info(STATUS_CODE, clientResponse.statusCode().value());
    return clientResponse.bodyToMono(String.class)
        .flatMap(response -> Mono.error(
            new TransactionException(response, clientResponse.statusCode().value())));
  }

}
