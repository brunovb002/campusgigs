package com.campusgigs.exception;

// Violação de autorização decidida na regra de negócio (ex.: "só o dono edita"),
// diferente do 403 do Spring Security (RestAccessDeniedHandler), que trata negações
// de acesso no nível do filtro. Ambas viram HTTP 403 para o cliente.
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
