package dev.bstk.cooperativa.pauta.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class PautaResponse {

    private final UUID uuid;
    private final String titulo;
    private final String descricao;
}
