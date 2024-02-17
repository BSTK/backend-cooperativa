package dev.bstk.cooperativa.pauta.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PautaRequest {

    private final String titulo;
    private final String descricao;
}
