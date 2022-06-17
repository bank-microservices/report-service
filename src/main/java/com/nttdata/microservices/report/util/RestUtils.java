package com.nttdata.microservices.report.util;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestUtils {

  public static ReactorClientHttpConnector getDefaultClientConnector() {
    return getClientConnector(5000, 5000, 5000);
  }

  public static ReactorClientHttpConnector getClientConnector(int connectionTimeout,
                                                              int readTimeout, int writeTimeout) {
    HttpClient httpClient = HttpClient.create()
        .resolver(DefaultAddressResolverGroup.INSTANCE)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
            connectionTimeout)
        .doOnConnected(conn -> conn
            .addHandlerLast(new ReadTimeoutHandler(
                readTimeout, TimeUnit.MILLISECONDS))
            .addHandlerLast(new WriteTimeoutHandler(
                writeTimeout, TimeUnit.MILLISECONDS)));

    return new ReactorClientHttpConnector(httpClient);
  }

}
