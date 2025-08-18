package com.alelo.desafio.votacao.service;

import com.alelo.desafio.votacao.entity.Pauta;
import com.alelo.desafio.votacao.repository.PautaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class PautaService {
    private final PautaRepository repo;
    public PautaService(PautaRepository repo) { this.repo = repo; }

    public Pauta create(Pauta p) {
        log.info("Criando pauta.");
        return repo.save(p);
    }
    public List<Pauta> list() {
        log.info("Listando pautas.");
        return repo.findAll();
    }
    public Pauta find(String id) {
        log.info("Procurando pauta para id: {}", id);
        return repo.findById(id).orElse(null);
    }
}
