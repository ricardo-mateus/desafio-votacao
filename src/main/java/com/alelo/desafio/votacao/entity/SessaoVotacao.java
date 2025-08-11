package com.alelo.desafio.votacao.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "sessao_votacao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SessaoVotacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pauta_id", nullable = false)
    private Long pautaId;

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