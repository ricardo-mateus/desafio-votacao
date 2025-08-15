package com.alelo.desafio.votacao.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "votos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndex(name = "pauta_associado_idx", def = "{'pautaId' : 1, 'associadoId': 1}", unique = true)
public class Voto {
    @Id
    private String id;
    private String pautaId;
    private String associadoId;
    private String cpf;
    private Boolean votoSim;
    private LocalDateTime momento;
}