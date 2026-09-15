package com.campusgigs.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

// Cliente HTTP declarativo (Spring HttpExchange) para o ViaCEP.
// A interface só descreve a chamada; o proxy real é montado em CepClientConfig.
public interface CepClient {

    @GetExchange("/{cep}/json")
    ViaCepResponse buscarCep(@PathVariable("cep") String cep);
}
