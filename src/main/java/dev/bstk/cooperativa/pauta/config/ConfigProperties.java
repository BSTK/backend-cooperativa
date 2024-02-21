package dev.bstk.cooperativa.pauta.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ConfigProperties {

    @Value("${cooperativa.exange}")
    private String exchange;

    @Value("${cooperativa.queue.pauta-finalizada}")
    private String queue;
}
