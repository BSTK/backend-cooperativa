package dev.bstk.cooperativa.pauta.schedule;

import dev.bstk.cooperativa.pauta.service.SessaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class SessaoSchedule {

    private final SessaoService sessaoService;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    void executarFinalizarSessao() {
        sessaoService.finalizarSessao();
    }
}
