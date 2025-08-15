package com.alelo.desafio.votacao.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.alelo.desafio.votacao.entity.Pauta;
import com.alelo.desafio.votacao.repository.PautaRepository;
import com.alelo.desafio.votacao.service.PautaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCriarPauta() {
        Pauta pauta = new Pauta();
        pauta.setTitulo("Nova Pauta");
        pauta.setDescricao("Descrição");

        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);

        Pauta resultado = pautaService.create(pauta);
        assertNotNull(resultado);
        assertEquals("Nova Pauta", resultado.getTitulo());
    }
}