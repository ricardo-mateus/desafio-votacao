package com.alelo.desafio.votacao.service;

import com.alelo.desafio.votacao.entity.SessaoVotacao;
import com.alelo.desafio.votacao.repository.SessaoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SessaoService {
    private final SessaoRepository repo;
    public SessaoService(SessaoRepository repo) { this.repo = repo; }

    @Transactional
    public SessaoVotacao abrirSessao(String pautaId, Long duracaoSegundos) {
        log.info("Abrindo sessão para pauta: {}", pautaId);
        if (duracaoSegundos == null || duracaoSegundos <= 0) duracaoSegundos = 60L;
        // se já existe sessão para pauta, reabre/atualiza
        SessaoVotacao s = repo.findByPautaId(pautaId);
        if (s == null) {
            s = SessaoVotacao.builder()
                    .pautaId(pautaId)
                    .inicio(LocalDateTime.now())
                    .duracaoSegundos(duracaoSegundos)
                    .aberta(true)
                    .build();
        } else {
            s.setInicio(LocalDateTime.now());
            s.setDuracaoSegundos(duracaoSegundos);
            s.setAberta(true);
        }
        return repo.save(s);
    }

    public SessaoVotacao findByPautaId(String pautaId) { return repo.findByPautaId(pautaId); }

    @Scheduled(fixedRate = 10_000)
    public void fecharSessoesExpiradas() {
        List<SessaoVotacao> abertas = repo.findByAbertaTrue();
        LocalDateTime now = LocalDateTime.now();
        for (SessaoVotacao s : abertas) {
            if (s.getFim() != null && now.isAfter(s.getFim())) {
                s.setAberta(false);
                repo.save(s);
            }
        }
    }
}
