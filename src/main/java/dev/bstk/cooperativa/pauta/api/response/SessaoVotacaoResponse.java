package dev.bstk.cooperativa.pauta.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class SessaoVotacaoResponse {

    private final UUID uuid;
    private final SessaoVotacaoPauta pauta;
    private final LocalDateTime dataHoraInicio;
    private final LocalDateTime dataHoraTermino;

    @Data
    @Builder
    @AllArgsConstructor
    public static class SessaoVotacaoPauta {
        private final UUID uuid;
        private final String titulo;
    }
}
