package dev.bstk.cooperativa.pauta.infra;

import dev.bstk.cooperativa.pauta.config.ConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificarEventoMq {

    private final ConfigProperties properties;
    private final RabbitTemplate rabbitTemplate;

    public void notificarEvento(final Evento evento) {
        rabbitTemplate.convertAndSend(
          properties.getExchange(),
          properties.getQueue(),
          evento
        );
    }
}
