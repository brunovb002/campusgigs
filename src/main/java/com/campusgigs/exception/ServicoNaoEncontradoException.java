package com.campusgigs.exception;

public class ServicoNaoEncontradoException extends RuntimeException {

    public ServicoNaoEncontradoException(Long id) {
        super("serviço não encontrado: id " + id);
    }
}
