package dev.bstk.cooperativa.pauta.handlerexception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErroResponse {

    @JsonProperty("mensagem")
    private final String mensagem;

    @JsonProperty("data_hora")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private final LocalDateTime dataHora;

    public ErroResponse(final String mensagem) {
        this.mensagem = mensagem;
        this.dataHora = LocalDateTime.now();
    }
}
