package dev.bstk.cooperativa.pauta.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import dev.bstk.cooperativa.pauta.model.Enums.PautaResultado;
import dev.bstk.cooperativa.pauta.model.Enums.PautaStatus;
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
    private PautaStatus status;

    @JsonInclude(Include.NON_NULL)
    private PautaResultado resultado;

    @JsonInclude(Include.NON_NULL)
    private Integer totalVotos;

    @JsonInclude(Include.NON_NULL)
    private Integer totalVotosSim;

    @JsonInclude(Include.NON_NULL)
    private Integer totalVotosNao;
}
