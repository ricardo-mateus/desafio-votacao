package com.alelo.desafio.votacao.service;

import com.alelo.desafio.votacao.entity.Pauta;
import com.alelo.desafio.votacao.repository.PautaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PautaService {
    private final PautaRepository repo;
    public PautaService(PautaRepository repo) { this.repo = repo; }

    public Pauta create(Pauta p) { return repo.save(p); }
    public List<Pauta> list() { return repo.findAll(); }
    public Pauta find(String id) { return repo.findById(id).orElse(null); }
}
