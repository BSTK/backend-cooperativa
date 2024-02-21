package dev.bstk.cooperativa.pauta.api;

import dev.bstk.cooperativa.pauta.api.request.PautaRequest;
import dev.bstk.cooperativa.pauta.api.request.PautaVotoRequest;
import dev.bstk.cooperativa.pauta.api.response.PautaResponse;
import dev.bstk.cooperativa.pauta.api.response.SessaoResponse;
import dev.bstk.cooperativa.pauta.helper.Mapper;
import dev.bstk.cooperativa.pauta.model.Pauta;
import dev.bstk.cooperativa.pauta.model.Votacao;
import dev.bstk.cooperativa.pauta.service.PautaService;
import dev.bstk.cooperativa.pauta.service.SessaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/pautas")
public class PautaResource {

    private final PautaService pautaService;
    private final SessaoService sessaoService;

    @GetMapping("/{pautaId}/resultado")
    public ResponseEntity<PautaResponse> ok(@PathVariable("pautaId") final Long pautaId) {
        final var pauta = pautaService.buscarPautaFinalizada(pautaId);
        final var pautaResponse = Mapper.to(pauta, PautaResponse.class);

        return ResponseEntity.ok(pautaResponse);
    }

    @PostMapping
    public ResponseEntity<PautaResponse> cadastrarNovaPauta(@RequestBody final PautaRequest request) {
        final var novaPauta = Mapper.to(request, Pauta.class);
        final var novaPautaCadastrada = pautaService.cadastrarNovaPauta(novaPauta);
        final var novaPautaCadastradaResponse = Mapper.to(novaPautaCadastrada, PautaResponse.class);

        return ResponseEntity.ok(novaPautaCadastradaResponse);
    }

    @PostMapping("/{pautaId}/iniciar-sessao")
    public ResponseEntity<SessaoResponse> iniciarSessao(@PathVariable("pautaId") final Long pautaId,
                                                        @RequestParam(value = "tempoDuracao", required = false) final Long tempoDuracao) {
        final var novaSessaoIniciada = sessaoService.iniciarSessao(pautaId, tempoDuracao);
        final var novaSessaoIniciadaResponse = Mapper.to(novaSessaoIniciada, SessaoResponse.class);

        return ResponseEntity.ok(novaSessaoIniciadaResponse);
    }

    @PostMapping("/{pautaId}/votar")
    public ResponseEntity<Void> votarPauta(@PathVariable("pautaId") final Long pautaId,
                                           @RequestBody final PautaVotoRequest request) {
        final var voto = Mapper.to(request, Votacao.class);
        sessaoService.votar(pautaId, voto);

        return ResponseEntity.noContent().build();
    }
}
