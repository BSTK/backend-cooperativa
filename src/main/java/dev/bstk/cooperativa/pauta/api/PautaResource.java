package dev.bstk.cooperativa.pauta.api;

import dev.bstk.cooperativa.pauta.api.request.PautaRequest;
import dev.bstk.cooperativa.pauta.api.response.PautaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
