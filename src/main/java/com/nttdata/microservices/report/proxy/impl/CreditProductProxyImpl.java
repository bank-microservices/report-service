package com.nttdata.microservices.report.proxy.impl;

import static com.nttdata.microservices.report.util.MessageUtils.getMsg;

import com.nttdata.microservices.report.entity.credit.CreditProduct;
import com.nttdata.microservices.report.entity.credit.CreditProductType;
import com.nttdata.microservices.report.exception.CreditNotFoundException;
import com.nttdata.microservices.report.proxy.CreditProductProxy;
import com.nttdata.microservices.report.util.RestUtils;
import java.time.Duration;
import java.util.function.Function;
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
public class CreditProductProxyImpl implements CreditProductProxy {

  private static final String STATUS_CODE = "Status code : {}";
  private final WebClient webClient;

  public CreditProductProxyImpl(@Value("${service.credit.uri}") final String url,
                                WebClient.Builder loadBalancedWebClientBuilder) {
    this.webClient = loadBalancedWebClientBuilder
        .clientConnector(RestUtils.getDefaultClientConnector())
        .baseUrl(url).build();
  }

  @Override
  public final Mono<CreditProduct> findCreditByAccountNumber(final String accountNumber) {
    return getCreditMono("/account-number/{number}", accountNumber);
  }

  @Override
  public Flux<CreditProduct> findCreditByClientDocument(String documentNumber) {
    final String errorMessage = getMsg("credit.not.available", documentNumber);
    return this.webClient.get()
        .uri("/client-document/{document-number}", documentNumber)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToFlux(CreditProduct.class)
        .map(setCreditProductType(CreditProductType.CREDIT))
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)));
  }

  private Mono<CreditProduct> getCreditMono(final String uri, final String findValue) {
    String errorMessage = getMsg("credit.not.available", findValue);
    return this.webClient.get()
        .uri(uri, findValue)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(CreditProduct.class)
        .map(setCreditProductType(CreditProductType.CREDIT))
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)));
  }

  @Override
  public Mono<CreditProduct> findCreditCardByAccountNumber(String accountNumber) {
    final String errorMessage = getMsg("credit.card.not.found", accountNumber);
    return this.webClient.get()
        .uri("/card/account/{number}", accountNumber)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(CreditProduct.class)
        .map(setCreditProductType(CreditProductType.CREDIT_CARD))
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)));
  }

  @Override
  public Flux<CreditProduct> findCreditCardByClientDocument(String documentNumber) {
    final String errorMessage = getMsg("credit.not.available", documentNumber);
    return this.webClient.get()
        .uri("/card/client/{number}", documentNumber)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToFlux(CreditProduct.class)
        .map(setCreditProductType(CreditProductType.CREDIT_CARD))
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)));
  }

  private Function<CreditProduct, CreditProduct> setCreditProductType(CreditProductType credit) {
    return (creditProduct) -> {
      creditProduct.setCreditProductType(credit);
      return creditProduct;
    };
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
