package com.alelo.desafio.votacao.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voto",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"pauta_id", "associado_id"})})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Voto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pauta_id", nullable = false)
    private Long pautaId;

    @Column(name = "associado_id", nullable = false)
    private String associadoId; // id único do associado (string para flexibilidade)

    private String cpf; // opcional, para validar via client fake

    private Boolean votoSim; // true = sim, false = nao

    private LocalDateTime momento;
}