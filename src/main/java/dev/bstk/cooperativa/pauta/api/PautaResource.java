package dev.bstk.cooperativa.pauta.api;

import dev.bstk.cooperativa.pauta.api.request.PautaRequest;
import dev.bstk.cooperativa.pauta.api.request.PautaVotoRequest;
import dev.bstk.cooperativa.pauta.api.response.PautaResponse;
import dev.bstk.cooperativa.pauta.api.response.SessaoVotacaoResponse;
import dev.bstk.cooperativa.pauta.helper.Mapper;
import dev.bstk.cooperativa.pauta.model.Pauta;
import dev.bstk.cooperativa.pauta.service.PautaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/pautas")
public class PautaResource {

    private final PautaService pautaService;

    @PostMapping
    public ResponseEntity<PautaResponse> cadastrarNovaPauta(@RequestBody final PautaRequest request) {
        final var novaPauta = Mapper.to(request, Pauta.class);
        final var novaPautaCadastrada = pautaService.cadastrarNovaPauta(novaPauta);
        final var novaPautaCadastradaResponse = Mapper.to(novaPautaCadastrada, PautaResponse.class);

        return ResponseEntity.ok(novaPautaCadastradaResponse);
    }

    @PostMapping("/{pautaId}/iniciar-sessao-votacao/{tempoDuracao}")
    public ResponseEntity<SessaoVotacaoResponse> iniciarSessaoVotacao(@PathVariable("pautaId") final Long pautaId,
                                                                      @PathVariable("tempoDuracao") final Long tempoDuracao) {
        final var novaSessaoVotacaoIniciada = pautaService.iniciarSessaoVotacao(pautaId, tempoDuracao);
        final var novaSessaoVotacaoIniciadaResponse = Mapper.to(novaSessaoVotacaoIniciada, SessaoVotacaoResponse.class);

        return ResponseEntity.ok(novaSessaoVotacaoIniciadaResponse);
    }

    @PostMapping("/{pautaId}/votar")
    public ResponseEntity<Void> votarPauta(@PathVariable("pautaId") final UUID pautaId,
                                           @RequestBody final PautaVotoRequest request) {
        log.info("Votacao - Pauta: [ {} ], Associado: [ {} ], Voto: [ {} ]", pautaId, request.getVoto(), request.getAssociadoId());
        return ResponseEntity.noContent().build();
    }
}
