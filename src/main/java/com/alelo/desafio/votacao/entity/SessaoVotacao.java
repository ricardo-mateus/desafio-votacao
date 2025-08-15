package com.alelo.desafio.votacao.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.*;

@Document(collection = "sessoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessaoVotacao {
    @Id
    private String id;

    private String pautaId;

    private LocalDateTime inicio;
    private Long duracaoSegundos;
    private Boolean aberta;

    public LocalDateTime getFim() {
        if (inicio == null || duracaoSegundos == null) return null;
        return inicio.plusSeconds(duracaoSegundos);
    }

    public boolean isAtivaNow() {
        if (Boolean.FALSE.equals(aberta)) return false;
        LocalDateTime now = LocalDateTime.now();
        return inicio != null && now.isAfter(inicio.minusNanos(1)) && now.isBefore(getFim().plusNanos(1));
    }
}