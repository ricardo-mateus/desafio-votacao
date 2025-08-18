package com.alelo.desafio.votacao.service;

import com.alelo.desafio.votacao.dto.ResultadoDTO;
import com.alelo.desafio.votacao.entity.SessaoVotacao;
import com.alelo.desafio.votacao.entity.Voto;
import com.alelo.desafio.votacao.dto.ResultadoAgregadoDTO;
import com.alelo.desafio.votacao.exception.BusinessException;
import com.alelo.desafio.votacao.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private SessaoService sessaoService;

    @Mock
    private CpfValidationClient cpfValidationClient;

    private VotoService votoService;

    @BeforeEach
    void setUp() {
        // 1. Criamos a instância real do serviço, passando null para a auto-referência por enquanto.
        VotoService realService = new VotoService(votoRepository, sessaoService, cpfValidationClient, null);

        votoService = spy(realService);

        Field selfField = ReflectionUtils.findField(VotoService.class, "self");
        ReflectionUtils.makeAccessible(selfField);
        ReflectionUtils.setField(selfField, realService, votoService);
    }

    @Test
    @DisplayName("Deve registrar um voto com sucesso quando todas as condições são válidas")
    void votar_comSucesso() {
        // Arrange (Preparação)
        String pautaId = "pauta123";
        String associadoId = "associado456";
        String cpf = "12345678900";
        SessaoVotacao sessaoAtiva = new SessaoVotacao("sessao1", pautaId, LocalDateTime.now().minusMinutes(10), 1200L, true);

        when(sessaoService.findByPautaId(pautaId)).thenReturn(sessaoAtiva);
        when(cpfValidationClient.validate(cpf)).thenReturn(CpfValidationClient.Status.ABLE_TO_VOTE);
        when(votoRepository.findByPautaIdAndAssociadoId(pautaId, associadoId)).thenReturn(Optional.empty());
        when(votoRepository.save(any(Voto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (Ação)
        Voto votoRegistrado = votoService.votar(pautaId, associadoId, cpf, true);

        // Assert (Verificação)
        assertNotNull(votoRegistrado);
        assertEquals(pautaId, votoRegistrado.getPautaId());
        assertEquals(associadoId, votoRegistrado.getAssociadoId());
        assertTrue(votoRegistrado.getVotoSim());
        verify(votoRepository, times(1)).save(any(Voto.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar votar em uma sessão encerrada")
    void votar_quandoSessaoEncerrada_deveLancarExcecao() {
        // Arrange
        String pautaId = "pauta123";
        SessaoVotacao sessaoEncerrada = new SessaoVotacao("sessao1", pautaId, LocalDateTime.now().minusMinutes(20), 600L, true);
        when(sessaoService.findByPautaId(pautaId)).thenReturn(sessaoEncerrada);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            votoService.votar(pautaId, "associado1", "12345678900", true);
        });

        assertEquals("Sessão não aberta ou já encerrada", exception.getMessage());
        verify(votoRepository, never()).save(any()); // Verifica que o save nunca foi chamado
    }

    @Test
    @DisplayName("Deve lançar BusinessException se o associado já votou")
    void votar_quandoAssociadoJaVotou_deveLancarExcecao() {
        // Arrange
        String pautaId = "pauta123";
        String associadoId = "associado456";
        SessaoVotacao sessaoAtiva = new SessaoVotacao("sessao1", pautaId, LocalDateTime.now().minusMinutes(10), 1200L, true);

        when(sessaoService.findByPautaId(pautaId)).thenReturn(sessaoAtiva);
        // Simula que o repositório encontrou um voto para este associado/pauta
        when(votoRepository.findByPautaIdAndAssociadoId(pautaId, associadoId)).thenReturn(Optional.of(new Voto()));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            votoService.votar(pautaId, associadoId, "123", true);
        });

        assertEquals("Associado já votou nesta pauta", exception.getMessage());
        verify(votoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException se o CPF não pode votar")
    void votar_quandoCpfInapto_deveLancarExcecao() {
        // Arrange
        String pautaId = "pauta123";
        String cpf = "11122233344";
        SessaoVotacao sessaoAtiva = new SessaoVotacao("sessao1", pautaId, LocalDateTime.now().minusMinutes(10), 1200L, true);

        when(sessaoService.findByPautaId(pautaId)).thenReturn(sessaoAtiva);
        // Simula que o cliente externo retornou que o CPF não pode votar
        when(cpfValidationClient.validate(cpf)).thenReturn(CpfValidationClient.Status.UNABLE_TO_VOTE);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            votoService.votar(pautaId, "associado1", cpf, true);
        });

        assertEquals("CPF não pode votar (externo)", exception.getMessage());
        verify(votoRepository, never()).save(any());
    }

}