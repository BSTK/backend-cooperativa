package dev.bstk.cooperativa.pauta.api;

import dev.bstk.cooperativa.pauta.api.request.PautaRequest;
import dev.bstk.cooperativa.pauta.api.request.PautaVotoRequest;
import dev.bstk.cooperativa.pauta.api.response.PautaResponse;
import dev.bstk.cooperativa.pauta.api.response.SessaoVotacaoResponse;
import dev.bstk.cooperativa.pauta.api.response.SessaoVotacaoResponse.SessaoVotacaoPauta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/api/pautas")
public class PautaResource {


    @PostMapping
    public ResponseEntity<PautaResponse> cadastrarNovaPauta(@RequestBody final PautaRequest request) {
        log.info("Cadastrando uma nova Pauta - [ {} ]", request.getTitulo());

        /// TODO - CHAMAR SERVICE PARA CADASTRAR NOVA PAUTA
        final var response = PautaResponse
                .builder()
                .uuid(UUID.randomUUID())
                .titulo(request.getTitulo())
                .descricao(request.getDescricao())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{pautaId}/iniciar-sessao-votacao/{tempoDuracao}")
    public ResponseEntity<SessaoVotacaoResponse> iniciarSessaoVotacao(@PathVariable("pautaId") final UUID pautaId,
                                                                      @PathVariable("tempoDuracao") final Long tempoDuracao) {
        log.info("Iniciando sessão de votação para pauta: [ {} ] com duração de [ {} ] minutos", pautaId, tempoDuracao);

        /// TODO - CHAMAR SERVICE PARA CADASTRAR NOVA PAUTA
        final var response = SessaoVotacaoResponse
                .builder()
                .uuid(UUID.randomUUID())
                .pauta(
                  SessaoVotacaoPauta
                     .builder()
                     .uuid(pautaId)
                     .build()
                )
                .dataHoraInicio(LocalDateTime.now())
                .dataHoraTermino(LocalDateTime.now().plus(tempoDuracao, ChronoUnit.MINUTES))
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{pautaId}/votar")
    public ResponseEntity<Void> votarPauta(@PathVariable("pautaId") final UUID pautaId,
                                           @RequestBody final PautaVotoRequest request) {
        log.info("Votacao - Pauta: [ {} ], Associado: [ {} ], Voto: [ {} ]", pautaId, request.getVoto(), request.getAssociadoId());
        return ResponseEntity.noContent().build();
    }
}
