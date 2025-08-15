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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pautas")
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
    public ResponseEntity<PautaDTO> criar(@RequestBody PautaDTO dto) {
        Pauta p = Pauta.builder().titulo(dto.getTitulo()).descricao(dto.getDescricao()).build();
        Pauta criado = pautaService.create(p);
        PautaDTO out = new PautaDTO(criado.getId(), criado.getTitulo(), criado.getDescricao());
        return ResponseEntity.created(URI.create("/api/v1/pautas/" + criado.getId())).body(out);
    }

    @GetMapping
    public List<PautaDTO> listar() {
        return pautaService.list().stream()
                .map(p -> new PautaDTO(p.getId(), p.getTitulo(), p.getDescricao()))
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/abrir-sessao")
    public ResponseEntity<String> abrirSessao(@PathVariable String id, @RequestBody(required = false) AbrirSessaoDTO dto) {
        Long dur = dto == null ? null : dto.getDuracaoSegundos();
        SessaoVotacao s = sessaoService.abrirSessao(id, dur);
        return ResponseEntity.ok("Sessão aberta até: " + s.getFim());
    }

    @PostMapping("/{id}/votos")
    public ResponseEntity<String> votar(@PathVariable String id, @RequestBody VotoDTO dto) {
        Voto v = votoService.votar(id, dto.getAssociadoId(), dto.getCpf(), dto.getVotoSim());
        return ResponseEntity.ok("Voto registrado id=" + v.getId());
    }

    @GetMapping("/{id}/resultado")
    public ResultadoDTO resultado(@PathVariable String id) {
        return votoService.resultado(id);
    }
}
