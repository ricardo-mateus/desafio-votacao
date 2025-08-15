package com.alelo.desafio.votacao.repository;

import com.alelo.desafio.votacao.entity.Voto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VotoRepository extends MongoRepository<Voto, String> {
    Optional<Voto> findByPautaIdAndAssociadoId(String pautaId, String associadoId);
    long countByPautaIdAndVotoSimTrue(String pautaId);
    long countByPautaIdAndVotoSimFalse(String pautaId);
    List<Voto> findByPautaId(Long pautaId);
}