package com.nttdata.microservices.report.proxy.impl;

import static com.nttdata.microservices.report.util.MessageUtils.getMsg;

import com.nttdata.microservices.report.entity.credit.Credit;
import com.nttdata.microservices.report.exception.CreditNotFoundException;
import com.nttdata.microservices.report.proxy.CreditProxy;
import com.nttdata.microservices.report.util.RestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CreditProxyImpl implements CreditProxy {

  private static final String STATUS_CODE = "Status code : {}";
  private final WebClient webClient;

  public CreditProxyImpl(@Value("${service.credit.uri}") final String url) {
    this.webClient = WebClient.builder()
        .clientConnector(RestUtils.getDefaultClientConnector())
        .baseUrl(url).build();
  }

  @Override
  public final Mono<Credit> findById(final String id) {
    return getCreditMono("/{id}", id);
  }

  @Override
  public final Mono<Credit> findByAccountNumber(final String accountNumber) {
    return getCreditMono("/account-number/{number}", accountNumber);
  }

  private Mono<Credit> getCreditMono(final String uri, final String findValue) {
    String errorMessage = getMsg("credit.not.available", findValue);
    return this.webClient.get()
        .uri(uri, findValue)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(Credit.class);
  }

  private Mono<? extends Throwable> applyError4xx(final ClientResponse clientResponse,
                                                  final String errorMessage) {
    log.info(STATUS_CODE, clientResponse.statusCode().value());
    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
      return Mono.error(
          new CreditNotFoundException(errorMessage, clientResponse.statusCode().value()));
    }
    return clientResponse.bodyToMono(String.class)
        .flatMap(response -> Mono.error(
            new CreditNotFoundException(response, clientResponse.statusCode().value())));
  }

  private Mono<? extends Throwable> applyError5xx(final ClientResponse clientResponse) {
    log.info(STATUS_CODE, clientResponse.statusCode().value());
    return clientResponse.bodyToMono(String.class)
        .flatMap(response -> Mono.error(
            new CreditNotFoundException(response, clientResponse.statusCode().value())));
  }

}
