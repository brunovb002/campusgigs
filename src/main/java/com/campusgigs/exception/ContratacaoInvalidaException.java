package com.campusgigs.exception;

// Violação de regra de negócio da contratação: contratar o próprio serviço,
// ou um serviço que não está ATIVO.
public class ContratacaoInvalidaException extends RuntimeException {

    public ContratacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
