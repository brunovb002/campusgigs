package com.campusgigs.service;

import com.campusgigs.client.CepClient;
import com.campusgigs.client.ViaCepResponse;
import com.campusgigs.exception.CepIndisponivelException;
import com.campusgigs.exception.CepNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class CepService {

    private final CepClient cepClient;

    public CepService(CepClient cepClient) {
        this.cepClient = cepClient;
    }

    // Qualquer falha de rede, timeout ou erro do serviço externo vira
    // CepIndisponivelException (503) — nunca deixamos o chamador seguir com um
    // endereço incompleto por causa de uma falha que não é dele.
    public EnderecoCep consultar(String cep) {
        String cepLimpo = cep.replaceAll("\\D", "");

        ViaCepResponse resposta;
        try {
            resposta = cepClient.buscarCep(cepLimpo);
        } catch (RestClientException ex) {
            throw new CepIndisponivelException(ex);
        }

        if (resposta == null || Boolean.TRUE.equals(resposta.erro())) {
            throw new CepNaoEncontradoException(cep);
        }

        return new EnderecoCep(resposta.localidade(), resposta.uf());
    }
}
