package com.nttdata.microservices.report.service.impl;

import static com.nttdata.microservices.report.util.MessageUtils.getMsg;

import com.nttdata.microservices.report.entity.account.Account;
import com.nttdata.microservices.report.entity.client.Client;
import com.nttdata.microservices.report.entity.credit.CreditProduct;
import com.nttdata.microservices.report.exception.ClientException;
import com.nttdata.microservices.report.proxy.AccountProxy;
import com.nttdata.microservices.report.proxy.ClientProxy;
import com.nttdata.microservices.report.proxy.CreditProductProxy;
import com.nttdata.microservices.report.service.CommonReportService;
import com.nttdata.microservices.report.service.dto.ProductDto;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CommonReportServiceImpl implements CommonReportService {

  private final AccountProxy accountProxy;
  private final CreditProductProxy creditProductProxy;
  private final ClientProxy clientProxy;

  @Override
  public Flux<ProductDto> findAllBankProductByClientDocument(final String clientDocument) {

    return Flux.just(clientDocument)
        .flatMap(this::existClient)
        .flatMap(client -> {

          final Flux<CreditProduct> credit = creditProductProxy
              .findCreditByClientDocument(client.getDocumentNumber());
          final Flux<CreditProduct> card = creditProductProxy
              .findCreditCardByClientDocument(client.getDocumentNumber());
          final Flux<Account> account = accountProxy
              .findByClientDocument(client.getDocumentNumber());

          return Flux.concat(credit, card, account)
              .map(prod -> {
                if (prod instanceof CreditProduct) {
                  CreditProduct credProd = (CreditProduct) prod;
                  return ProductDto.builder()
                      .accountNumber(credProd.getAccountNumber())
                      .cci(credProd.getCci())
                      .amount(ObjectUtils.defaultIfNull(credProd.getAmount(), 0D))
                      .productType(credProd.getCreditProductType().name())
                      .client(client)
                      .build();
                } else {
                  Account accountProd = (Account) prod;
                  return ProductDto.builder()
                      .accountNumber(accountProd.getAccountNumber())
                      .cci(accountProd.getCci())
                      .amount(ObjectUtils.defaultIfNull(accountProd.getAmount(), 0D))
                      .productType(accountProd.getAccountType().getDescription())
                      .client(client)
                      .build();
                }
              });
        });

  }

  private Flux<Client> existClient(String clientDocument) {
    log.debug("Request to proxy Client by documentNumber: {}", clientDocument);
    return clientProxy.getClientByDocumentNumber(clientDocument)
        .switchIfEmpty(Mono.error(new ClientException(getMsg("client.not.found"))))
        .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
        .flux();
  }

}
