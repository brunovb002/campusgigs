package com.campusgigs.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Mapeia só os campos que usamos da resposta do ViaCEP (https://viacep.com.br).
// ignoreUnknown protege contra o serviço externo adicionar campos novos no futuro.
// Para um CEP com formato válido mas inexistente, o ViaCEP responde 200 com "erro": true.
@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(String localidade, String uf, Boolean erro) {
}
