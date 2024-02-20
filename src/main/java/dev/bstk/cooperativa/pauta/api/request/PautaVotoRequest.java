package dev.bstk.cooperativa.pauta.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PautaVotoRequest {

    private Long associadoId;
    private Boolean voto;
}
