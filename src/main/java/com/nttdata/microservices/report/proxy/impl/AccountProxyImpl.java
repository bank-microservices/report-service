package com.nttdata.microservices.report.proxy.impl;

import static com.nttdata.microservices.report.util.MessageUtils.getMsg;

import com.nttdata.microservices.report.entity.account.Account;
import com.nttdata.microservices.report.entity.account.AccountType;
import com.nttdata.microservices.report.exception.AccountException;
import com.nttdata.microservices.report.proxy.AccountProxy;
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
public class AccountProxyImpl implements AccountProxy {

  private static final String STATUS_CODE = "Status code : {}";

  private final WebClient webClient;

  public AccountProxyImpl(@Value("${service.account.uri}") final String url) {
    this.webClient = WebClient.builder()
        .clientConnector(RestUtils.getDefaultClientConnector())
        .baseUrl(url).build();
  }

  @Override
  public final Flux<Account> findByAccountNumberAndClientDocument(final String accountNumber,
                                                                  final String documentNumber) {

    Map<String, String> params = Map
        .of("accountNumber", accountNumber,
            "documentNumber", documentNumber);

    String errorMessage = getMsg("account.not.available.for.client", params.values().toArray());

    return this.webClient.get()
        .uri("/number/{accountNumber}/client/{documentNumber}", params)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToFlux(Account.class)
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)));
  }

  @Override
  public final Mono<Account> findByAccountNumber(final String accountNumber) {

    String errorMessage = getMsg("account.number.not.available", accountNumber);

    return this.webClient.get()
        .uri("/account-number/{accountNumber}", accountNumber)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(Account.class)
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)));
  }

  @Override
  public Mono<AccountType> findAccountTypeById(String accountTypeId) {

    String errorMessage = getMsg("account.type.not.available", accountTypeId);

    return this.webClient.get()
        .uri("/type/{id}", accountTypeId)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(AccountType.class)
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)));

  }

  private Mono<? extends Throwable> applyError4xx(final ClientResponse creditResponse,
                                                  final String errorMessage) {
    log.info(STATUS_CODE, creditResponse.statusCode().value());
    if (creditResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
      return Mono.error(new AccountException(errorMessage, creditResponse.statusCode().value()));
    }
    return creditResponse.bodyToMono(String.class)
        .flatMap(response -> Mono.error(
            new AccountException(response, creditResponse.statusCode().value())));
  }

  private Mono<? extends Throwable> applyError5xx(final ClientResponse clientResponse) {
    log.info(STATUS_CODE, clientResponse.statusCode().value());
    return clientResponse.bodyToMono(String.class)
        .flatMap(response -> Mono.error(
            new AccountException(response, clientResponse.statusCode().value())));
  }
}
