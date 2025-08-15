package com.alelo.desafio.votacao.service;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class CpfValidationClient {
    private final Random rnd = new Random();

    public enum Status { ABLE_TO_VOTE, UNABLE_TO_VOTE }

    /**
     * Simula uma chamada externa.
     * - 5% chance de retornar "CPF inválido" (lançar NotFoundException)
     * - Senão, retorna ABLE_TO_VOTE ou UNABLE_TO_VOTE aleatoriamente.
     */
    public Status validate(String cpf) {
        if (cpf == null || cpf.trim().isEmpty())
            return Status.ABLE_TO_VOTE; // aceita se não informado
        int r = rnd.nextInt(100);
        if (r < 5) {
            throw new CpfNotFoundException("CPF inválido (simulado): " + cpf);
        }
        return (rnd.nextBoolean() ? Status.ABLE_TO_VOTE : Status.UNABLE_TO_VOTE);
    }

    public static class CpfNotFoundException extends RuntimeException {
        public CpfNotFoundException(String msg) { super(msg); }
    }
}
