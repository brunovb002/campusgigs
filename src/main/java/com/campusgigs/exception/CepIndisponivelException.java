package com.campusgigs.exception;

// Falha de rede, timeout ou erro inesperado do serviço externo de CEP.
// Nunca deixamos o cadastro/atualização seguir "pela metade" nesse caso — melhor
// recusar com uma mensagem clara do que salvar um usuário com cidade/uf incompletos.
public class CepIndisponivelException extends RuntimeException {

    public CepIndisponivelException(Throwable causa) {
        super("não foi possível consultar o CEP no momento, tente novamente em instantes", causa);
    }
}
