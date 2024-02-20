package dev.bstk.cooperativa.pauta.api.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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

    @JsonDeserialize(using = VotoDeserializer.class)
    private Boolean voto;
}
