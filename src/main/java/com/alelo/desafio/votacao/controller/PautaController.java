package com.alelo.desafio.votacao.controller;

import com.alelo.desafio.votacao.dto.AbrirSessaoDTO;
import com.alelo.desafio.votacao.dto.PautaDTO;
import com.alelo.desafio.votacao.dto.ResultadoDTO;
import com.alelo.desafio.votacao.dto.VotoDTO;
import com.alelo.desafio.votacao.entity.Pauta;
import com.alelo.desafio.votacao.entity.SessaoVotacao;
import com.alelo.desafio.votacao.entity.Voto;
import com.alelo.desafio.votacao.service.PautaService;
import com.alelo.desafio.votacao.service.SessaoService;
import com.alelo.desafio.votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pautas")
@Tag(name = "Votação", description = "Endpoints para gerenciar votações em assembléia")
@Log
public class PautaController {
    private final PautaService pautaService;
    private final SessaoService sessaoService;
    private final VotoService votoService;

    public PautaController(PautaService pautaService, SessaoService sessaoService, VotoService votoService) {
        this.pautaService = pautaService;
        this.sessaoService = sessaoService;
        this.votoService = votoService;
    }

    @PostMapping
    @Operation(summary = "Criar uma pauta", description = "Retorna a pauta solicitada para criar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Status retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<PautaDTO> criar(@RequestBody PautaDTO dto) {
        Pauta p = Pauta.builder().titulo(dto.getTitulo()).descricao(dto.getDescricao()).build();
        Pauta criado = pautaService.create(p);
        PautaDTO out = new PautaDTO(criado.getId(), criado.getTitulo(), criado.getDescricao());
        log.info("....# Pauta criada com sucesso #....");
        return ResponseEntity.created(URI.create("/api/v1/pautas/" + criado.getId())).body(out);
    }

    @GetMapping
    @Operation(summary = "Listar pautas", description = "Retorna uma lista de pautas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public List<PautaDTO> listar() {
        log.info("....# Lista de pautas #....");
        return pautaService.list().stream()
                .map(p -> new PautaDTO(p.getId(), p.getTitulo(), p.getDescricao()))
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/abrir-sessao")
    @Operation(summary = "Abrir sessão", description = "Abre uma sessão para votação, com o tempo default de 1min")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<String> abrirSessao(@PathVariable String id, @RequestBody(required = false) AbrirSessaoDTO dto) {
        Long dur = dto == null ? null : dto.getDuracaoSegundos();
        SessaoVotacao s = sessaoService.abrirSessao(id, dur);
        log.info("....# Sessção aberta id: " + s.getId() + " #....");
        return ResponseEntity.ok("Sessão aberta até: " + s.getFim());
    }

    @PostMapping("/{id}/votos")
    @Operation(summary = "Realizar votos", description = "Permite votar oara uma pauta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<String> votar(@PathVariable String id, @RequestBody VotoDTO dto) {
        Voto v = votoService.votar(id, dto.getAssociadoId(), dto.getCpf(), dto.getVotoSim());
        log.info("....# Voto realizado id: " + v.getId() + " #....");
        return ResponseEntity.ok("Voto registrado id=" + v.getId());
    }

    @GetMapping("/{id}/resultado")
    @Operation(summary = "Listar resultado", description = "Lista o resultado da votação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResultadoDTO resultado(@PathVariable String id) {
        log.info("....# Lista de resultados #....");
        return votoService.resultado(id);
    }
}
