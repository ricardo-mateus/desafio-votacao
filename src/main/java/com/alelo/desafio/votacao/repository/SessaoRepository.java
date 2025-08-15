package com.alelo.desafio.votacao.repository;

import com.alelo.desafio.votacao.entity.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SessaoRepository extends JpaRepository<SessaoVotacao, Long> {
    List<SessaoVotacao> findByAbertaTrue();
    SessaoVotacao findByPautaId(Long pautaId);
}
