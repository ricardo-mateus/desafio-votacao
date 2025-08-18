package com.alelo.desafio.votacao.repository;

import com.alelo.desafio.votacao.dto.ResultadoAgregadoDTO;
import com.alelo.desafio.votacao.entity.Voto;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VotoRepository extends MongoRepository<Voto, String> {
    Optional<Voto> findByPautaIdAndAssociadoId(String pautaId, String associadoId);
    long countByPautaIdAndVotoSimTrue(String pautaId);
    long countByPautaIdAndVotoSimFalse(String pautaId);
    List<Voto> findByPautaId(Long pautaId);
    @Aggregation(pipeline = {
            "{ '$match': { 'pautaId': ?0 } }",
            "{ '$group': { '_id': '$pautaId', 'totalSim': { '$sum': { '$cond': [ '$votoSim', 1, 0 ] } }, 'totalNao': { '$sum': { '$cond': [ '$votoSim', 0, 1 ] } } } }",
            "{ '$project': { '_id': 0, 'pautaId': '$_id', 'totalSim': 1, 'totalNao': 1 } }"
    })
    ResultadoAgregadoDTO contarVotosPorPauta(String pautaId);
}