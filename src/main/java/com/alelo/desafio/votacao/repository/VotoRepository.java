package com.alelo.desafio.votacao.repository;

import com.alelo.desafio.votacao.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Long> {
    Optional<Voto> findByPautaIdAndAssociadoId(Long pautaId, String associadoId);
    long countByPautaIdAndVotoSimTrue(Long pautaId);
    long countByPautaIdAndVotoSimFalse(Long pautaId);
    List<Voto> findByPautaId(Long pautaId);
}