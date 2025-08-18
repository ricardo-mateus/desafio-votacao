package com.alelo.desafio.votacao.service;

import com.alelo.desafio.votacao.dto.ResultadoAgregadoDTO;
import com.alelo.desafio.votacao.dto.ResultadoDTO;
import com.alelo.desafio.votacao.entity.SessaoVotacao;
import com.alelo.desafio.votacao.entity.Voto;
import com.alelo.desafio.votacao.exception.BusinessException;
import com.alelo.desafio.votacao.repository.VotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class VotoService {
    private final VotoRepository repo;
    private final SessaoService sessaoService;
    private final CpfValidationClient cpfClient;
    private final VotoService self;

    public VotoService(VotoRepository repo, SessaoService sessaoService, CpfValidationClient cpfClient, @Lazy VotoService self) {
        this.repo = repo;
        this.sessaoService = sessaoService;
        this.cpfClient = cpfClient;
        this.self = self;
    }

    @Transactional
    public Voto votar(String pautaId, String associadoId, String cpf, Boolean votoSim) {
        // valida sessão
        SessaoVotacao sessao = sessaoService.findByPautaId(pautaId);
        if (sessao == null || !sessao.isAtivaNow()) {
            log.warn("Sessão não aberta ou já encerrada");
            throw new com.alelo.desafio.votacao.exception.BusinessException("Sessão não aberta ou já encerrada");
        }
        // valida CPF externo: se retornar UNABLE_TO_VOTE -> erro
        if (cpf != null && !cpf.trim().isEmpty()) {
            CpfValidationClient.Status status = cpfClient.validate(cpf);
            if (status == CpfValidationClient.Status.UNABLE_TO_VOTE) {
                log.warn("CPF não pode votar (externo)");
                throw new com.alelo.desafio.votacao.exception.BusinessException("CPF não pode votar (externo)");
            }
        }
        // único voto por associado/pauta: constraint DB + checagem
        repo.findByPautaIdAndAssociadoId(pautaId, associadoId).ifPresent(v -> {
            log.warn("Associado já votou nesta pauta");
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
        SessaoVotacao sessao = sessaoService.findByPautaId(pautaId);
        if (sessao == null) {
            log.warn("Pauta não encontrada ou sem sessão de votação.");
            throw new BusinessException("Pauta não encontrada ou sem sessão de votação.");
        }

        // Se a sessão ainda está ativa, calcula o resultado em tempo real, sem usar o cache.
        if (sessao.isAtivaNow()) {
            log.warn("Calculando resultado para sessão ainda aberta (sem cache): {}", pautaId);
            return calcularResultadoFinal(pautaId);
        }

        log.info("Sessão encerrada. Tentando obter resultado do cache para a pauta: {}", pautaId);
        return self.obterResultadoCacheado(pautaId);
    }

    @Cacheable(value = "resultados", key = "#pautaId")
    public ResultadoDTO obterResultadoCacheado(String pautaId) {
        log.warn("CACHE MISS! Resultado não encontrado no cache. Calculando e salvando no cache para a pauta: {}", pautaId);
        return calcularResultadoFinal(pautaId);
    }

    private ResultadoDTO calcularResultadoFinal(String pautaId) {
        ResultadoAgregadoDTO resultadoAgregado = repo.contarVotosPorPauta(pautaId);

        if (resultadoAgregado == null) {
            return new ResultadoDTO(pautaId, 0L, 0L, "EMPATE");
        }

        String resultadoFinal;
        if (resultadoAgregado.getTotalSim() > resultadoAgregado.getTotalNao()) {
            resultadoFinal = "APROVADO";
        } else if (resultadoAgregado.getTotalNao() > resultadoAgregado.getTotalSim()) {
            resultadoFinal = "REPROVADO";
        } else {
            resultadoFinal = "EMPATE";
        }

        return new ResultadoDTO(
                resultadoAgregado.getPautaId(),
                resultadoAgregado.getTotalSim(),
                resultadoAgregado.getTotalNao(),
                resultadoFinal
        );
    }
}
