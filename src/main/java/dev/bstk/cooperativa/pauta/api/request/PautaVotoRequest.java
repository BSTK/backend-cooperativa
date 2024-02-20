package dev.bstk.cooperativa.pauta.api.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PautaVotoRequest {

    @NotNull
    private Long associadoId;

    @NotNull
    @JsonDeserialize(using = VotoDeserializer.class)
    private Boolean voto;
}
