package com.alelo.desafio.votacao.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ResultadoDTO {
    private Long pautaId;
    private long totalSim;
    private long totalNao;
    private String resultado; // "APROVADO" / "REPROVADO" / "EMPATE"
}
