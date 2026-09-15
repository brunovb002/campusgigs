package com.campusgigs.exception;

// CEP com formato válido, mas que o serviço externo não reconhece.
public class CepNaoEncontradoException extends RuntimeException {

    public CepNaoEncontradoException(String cep) {
        super("CEP não encontrado: " + cep);
    }
}
