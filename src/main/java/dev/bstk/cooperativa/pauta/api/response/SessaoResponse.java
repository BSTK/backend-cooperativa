package dev.bstk.cooperativa.pauta.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessaoResponse {

    private Long id;

    @DateTimeFormat(pattern = "dd/MM/yy hh:mm:ss")
    private LocalDateTime dataHoraInicio;

    @DateTimeFormat(pattern = "dd/MM/yy hh:mm:ss")
    private LocalDateTime dataHoraFim;
}
