package com.alelo.desafio.votacao.repository;

import com.alelo.desafio.votacao.entity.SessaoVotacao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SessaoRepository extends MongoRepository<SessaoVotacao, String> {
    List<SessaoVotacao> findByAbertaTrue();
    SessaoVotacao findByPautaId(String pautaId);
}
