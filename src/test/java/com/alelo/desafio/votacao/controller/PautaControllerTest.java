package com.alelo.desafio.votacao.controller;

import com.alelo.desafio.votacao.dto.PautaDTO;
import com.alelo.desafio.votacao.entity.Pauta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PautaControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCriarPautaEndpoint() {
        PautaDTO pautaDTO = new PautaDTO(null, "Título Teste", "Descrição Teste");

        ResponseEntity<PautaDTO> response = restTemplate.postForEntity("/api/v1/pautas", pautaDTO, PautaDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Título Teste", response.getBody().getTitulo());
    }
}