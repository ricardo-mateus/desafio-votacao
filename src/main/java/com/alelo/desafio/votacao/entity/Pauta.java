package com.alelo.desafio.votacao.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "pauta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pauta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descricao;
}
