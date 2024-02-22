package dev.bstk.cooperativa.pauta.schedule;

import dev.bstk.cooperativa.pauta.service.SessaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SessaoScheduleTest {

    @InjectMocks
    private SessaoSchedule sessaoSchedule;

    @Mock
    private SessaoService sessaoService;


    @Test
    @DisplayName("Deve executar squedule para finalizar sessões abertas")
    void t1() {
        sessaoSchedule.executarFinalizarSessao();
        verify(sessaoService).finalizarSessao();
    }
}
