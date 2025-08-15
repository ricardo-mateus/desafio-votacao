package com.alelo.desafio.votacao.service;

import com.alelo.desafio.votacao.dto.ResultadoDTO;
import com.alelo.desafio.votacao.entity.SessaoVotacao;
import com.alelo.desafio.votacao.entity.Voto;
import com.alelo.desafio.votacao.repository.VotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VotoService {
    private final VotoRepository repo;
    private final SessaoService sessaoService;
    private final CpfValidationClient cpfClient;

    public VotoService(VotoRepository repo, SessaoService sessaoService, CpfValidationClient cpfClient) {
        this.repo = repo;
        this.sessaoService = sessaoService;
        this.cpfClient = cpfClient;
    }

    @Transactional
    public Voto votar(String pautaId, String associadoId, String cpf, Boolean votoSim) {
        // valida sessão
        SessaoVotacao sessao = sessaoService.findByPautaId(pautaId);
        if (sessao == null || !sessao.isAtivaNow()) {
            throw new com.alelo.desafio.votacao.exception.BusinessException("Sessão não aberta ou já encerrada");
        }
        // valida CPF externo: se retornar UNABLE_TO_VOTE -> erro
        if (cpf != null && !cpf.trim().isEmpty()) {
            CpfValidationClient.Status status = cpfClient.validate(cpf);
            if (status == CpfValidationClient.Status.UNABLE_TO_VOTE) {
                throw new com.alelo.desafio.votacao.exception.BusinessException("CPF não pode votar (externo)");
            }
        }
        // único voto por associado/pauta: constraint DB + checagem
        repo.findByPautaIdAndAssociadoId(pautaId, associadoId).ifPresent(v -> {
            throw new com.alelo.desafio.votacao.exception.BusinessException("Associado já votou nesta pauta");
        });

        Voto voto = Voto.builder()
                .pautaId(pautaId)
                .associadoId(associadoId)
                .cpf(cpf)
                .votoSim(Boolean.TRUE.equals(votoSim))
                .momento(LocalDateTime.now())
                .build();

        return repo.save(voto);
    }

    public ResultadoDTO resultado(String pautaId) {
        long sim = repo.countByPautaIdAndVotoSimTrue(pautaId);
        long nao = repo.countByPautaIdAndVotoSimFalse(pautaId);
        String res;
        if (sim > nao) res = "APROVADO";
        else if (nao > sim) res = "REPROVADO";
        else res = "EMPATE";
        return new ResultadoDTO(pautaId, sim, nao, res);
    }
}
