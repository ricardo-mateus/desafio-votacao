// Em um novo arquivo: ResultadoAgregadoDTO.java
package com.alelo.desafio.votacao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoAgregadoDTO {
    private String pautaId;
    private long totalSim;
    private long totalNao;
}