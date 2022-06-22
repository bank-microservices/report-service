package com.nttdata.microservices.report.service;

import com.nttdata.microservices.report.service.dto.ProductDto;
import reactor.core.publisher.Flux;

public interface CommonReportService {

  Flux<ProductDto> findAllBankProductByClientDocument(String clientDocument);

}
