package com.campusgigs.exception;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException(String email) {
        super("já existe um usuário cadastrado com o email: " + email);
    }
}
