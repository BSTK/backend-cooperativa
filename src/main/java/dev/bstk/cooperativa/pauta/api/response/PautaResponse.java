package dev.bstk.cooperativa.pauta.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PautaResponse {

    private Long id;
    private String titulo;
}
