package dev.bstk.cooperativa.pauta.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PautaVotoRequest {

    private final Long associadoId;
    private final Boolean voto;
}
