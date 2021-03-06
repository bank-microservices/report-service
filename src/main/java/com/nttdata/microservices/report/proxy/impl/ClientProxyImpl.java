package com.nttdata.microservices.report.proxy.impl;

import static com.nttdata.microservices.report.util.MessageUtils.getMsg;

import com.nttdata.microservices.report.entity.client.Client;
import com.nttdata.microservices.report.exception.ClientException;
import com.nttdata.microservices.report.proxy.ClientProxy;
import com.nttdata.microservices.report.util.RestUtils;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * Class responsible for communicating with the Client Microservice.
 */
@Slf4j
@Service
public class ClientProxyImpl implements ClientProxy {

  private static final String STATUS_CODE = "Status code : {}";
  private final WebClient webClient;

  public ClientProxyImpl(@Value("${service.client.uri}") final String url,
                         WebClient.Builder loadBalancedWebClientBuilder) {
    this.webClient = loadBalancedWebClientBuilder
        .clientConnector(RestUtils.getDefaultClientConnector())
        .baseUrl(url)
        .build();
  }

  @Override
  public final Mono<Client> getClientByDocumentNumber(final String documentNumber) {
    String errorMessage = getMsg("client.document.number.not.available", documentNumber);
    return this.webClient.get()
        .uri("/document-number/{number}", documentNumber)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(Client.class)
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)));
  }

  @Override
  public final Mono<Client> getClientById(final String id) {
    String errorMessage = getMsg("client.not.available", id);
    return this.webClient.get()
        .uri("/{id}", id)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(Client.class)
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)));
  }

  private Mono<? extends Throwable> applyError4xx(final ClientResponse clientResponse,
                                                  final String errorMessage) {
    log.info(STATUS_CODE, clientResponse.statusCode().value());
    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
      return Mono.error(new ClientException(errorMessage, HttpStatus.NOT_FOUND.value()));
    }
    return clientResponse.bodyToMono(String.class)
        .flatMap(response -> Mono.error(
            new ClientException(response, clientResponse.statusCode().value())));
  }

  private Mono<? extends Throwable> applyError5xx(final ClientResponse clientResponse) {
    log.info(STATUS_CODE, clientResponse.statusCode().value());
    return clientResponse.bodyToMono(String.class)
        .flatMap(response -> Mono.error(
            new ClientException(response, clientResponse.statusCode().value())));
  }
}
