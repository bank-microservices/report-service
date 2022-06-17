package com.nttdata.microservices.report.proxy;

import com.nttdata.microservices.report.entity.client.Client;
import reactor.core.publisher.Mono;

public interface ClientProxy {

  Mono<Client> getClientByDocumentNumber(String documentNumber);

  Mono<Client> getClientById(String id);
}
