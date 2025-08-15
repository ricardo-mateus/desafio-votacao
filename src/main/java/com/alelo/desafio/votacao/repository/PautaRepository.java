package com.alelo.desafio.votacao.repository;

import com.alelo.desafio.votacao.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PautaRepository extends JpaRepository<Pauta, Long> { }

