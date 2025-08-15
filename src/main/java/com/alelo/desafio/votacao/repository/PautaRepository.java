package com.alelo.desafio.votacao.repository;

import com.alelo.desafio.votacao.entity.Pauta;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PautaRepository extends MongoRepository<Pauta, String> { }

