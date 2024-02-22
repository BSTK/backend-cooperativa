package dev.bstk.cooperativa.pauta.infra;

import dev.bstk.cooperativa.pauta.config.ConfigProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificarEventoMqTest {

    @InjectMocks
    private NotificarEventoMq notificarEventoMq;

    @Mock
    private ConfigProperties properties;

    @Mock
    private RabbitTemplate rabbitTemplate;


    @Test
    @DisplayName("Deve notificar evento para exchange e queue configurada")
    void t1() {
        final var queue = "mock-queue";
        final var exchange = "mock-exchange";

        Mockito.when(properties.getQueue()).thenReturn(queue);
        Mockito.when(properties.getExchange()).thenReturn(exchange);

        final var evento = Evento.builder().build();

        notificarEventoMq.notificarEvento(evento);

        verify(rabbitTemplate).convertAndSend(exchange, queue, evento);
    }
}
