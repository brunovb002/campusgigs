package com.campusgigs.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class CepClientConfig {

    // base-url e timeout configuráveis por variável de ambiente (CEP_BASE_URL, CEP_TIMEOUT_MS)
    // justamente para dar pra simular o serviço externo fora do ar/lento em teste manual,
    // sem depender de derrubar a internet de verdade.
    @Bean
    public CepClient cepClient(@Value("${app.cep.base-url}") String baseUrl,
                                @Value("${app.cep.timeout-ms}") int timeoutMs) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutMs);
        requestFactory.setReadTimeout(timeoutMs);

        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return factory.createClient(CepClient.class);
    }
}
