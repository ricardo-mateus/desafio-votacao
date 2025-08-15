package com.alelo.desafio.votacao.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class VotoDTO {
    private String associadoId;
    private String cpf;
    private Boolean votoSim;
}
