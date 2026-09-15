package com.campusgigs.service;

// Resultado já traduzido de uma consulta de CEP bem-sucedida — o resto dos campos
// que o ViaCEP devolve (logradouro, bairro, ddd etc.) não faz parte do nosso domínio.
public record EnderecoCep(String cidade, String uf) {
}
