package dev.bstk.cooperativa.pauta.service;

import dev.bstk.cooperativa.pauta.infra.NotificarEventoMq;
import dev.bstk.cooperativa.pauta.model.Enums.PautaResultado;
import dev.bstk.cooperativa.pauta.model.Enums.PautaStatus;
import dev.bstk.cooperativa.pauta.model.Enums.SessaoStatus;
import dev.bstk.cooperativa.pauta.model.Pauta;
import dev.bstk.cooperativa.pauta.model.Sessao;
import dev.bstk.cooperativa.pauta.model.Votacao;
import dev.bstk.cooperativa.pauta.repository.PautaRepository;
import dev.bstk.cooperativa.pauta.repository.SessaoRepository;
import dev.bstk.cooperativa.pauta.repository.VotacaoRepository;
import dev.bstk.cooperativa.pauta.repository.projections.VotacaoPautaResultado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.DefaultArgumentConverter;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessaoServiceTest {

    @InjectMocks
    private SessaoService sessaoService;

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private VotacaoRepository votacaoRepository;

    @Mock
    private NotificarEventoMq notificarEventoMq;

    @Captor
    private ArgumentCaptor<Sessao> sessaoCaptor;

    @ParameterizedTest
    @CsvSource({"null, 1", "0, 1", "2, 2"})
    @DisplayName("Deve iniciar uma nova sessão com tempo informado")
    void t1(@ConvertWith(ParameterizedTestNullConverter.class) final Long tempoDuracao,
                                                               final Long duracaoEmMinutos) {
        final var pautaId = 1L;
        final var pauta = Pauta.builder().id(pautaId).status(PautaStatus.ABERTA).build();

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.buscarSessaoPorPauta(pautaId)).thenReturn(Optional.empty());
        when(sessaoRepository.save(any(Sessao.class))).thenAnswer(invocation -> {
            final var novaSessao = (Sessao) invocation.getArgument(0);
            novaSessao.setId(1L);
            return novaSessao;
        });

        final var novaSessaoCriada = sessaoService.iniciarSessao(pautaId, tempoDuracao);
        assertThat(novaSessaoCriada).isNotNull();
        assertThat(novaSessaoCriada.getPauta()).isEqualTo(pauta);

        verify(sessaoRepository).save(sessaoCaptor.capture());

        final var sessaoIniciada = sessaoCaptor.getValue();
        assertThat(sessaoIniciada.getStatus()).isEqualTo(SessaoStatus.ABERTA);
        assertThat(sessaoIniciada.getPauta().getStatus()).isEqualTo(PautaStatus.EM_VOTACAO);

        final var duracaoEmMinutosCalculada = Duration
                .between(sessaoIniciada.getDataHoraInicio(), sessaoIniciada.getDataHoraFim())
                .toMinutes();

        assertThat(duracaoEmMinutosCalculada).isEqualTo(duracaoEmMinutos);

        verify(pautaRepository).findById(pautaId);
        verify(sessaoRepository).buscarSessaoPorPauta(pautaId);
        verify(sessaoRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar exception quando não existir pauta cadastrada")
    void t2() {
        final var pautaId = 1L;
        final var tempoDuracao = 10L;
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessaoService.iniciarSessao(pautaId, tempoDuracao))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("Não existe pauta cadastrada [ id: %s ]!", pautaId));

        verify(pautaRepository).findById(pautaId);
        verifyNoMoreInteractions(sessaoRepository);
    }

    @Test
    @DisplayName("Deve lançar exception quando uma sessão já estiver iniciada para pauta")
    void t3() {
        final var pautaId = 1L;
        final var tempoDuracao = 10L;
        final var pauta = Pauta.builder().id(pautaId).status(PautaStatus.ABERTA).build();
        final var sessaoExistente = Sessao.builder().id(1L).pauta(pauta).status(SessaoStatus.ABERTA).build();

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.buscarSessaoPorPauta(pautaId)).thenReturn(Optional.of(sessaoExistente));

        assertThatThrownBy(() -> sessaoService.iniciarSessao(pautaId, tempoDuracao))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("Está pauta está [ %s ]!", pauta.getStatus()));

        verify(pautaRepository).findById(pautaId);
        verify(sessaoRepository).buscarSessaoPorPauta(pautaId);
        verifyNoMoreInteractions(sessaoRepository);
    }

    @Test
    @DisplayName("Não pode fechar uma sessão nem notificar evento quando não há sessões abertas")
    void t4() {
        when(sessaoRepository.buscarSessoesAberta()).thenReturn(List.of());

        sessaoService.finalizarSessao();

        verify(sessaoRepository, never()).saveAndFlush(any());
        verify(notificarEventoMq, never()).notificarEvento(any());
    }

    @Test
    @DisplayName("Deve finalizar sessão quando há sessões abertas")
    void t5() {
        final var sessao1 = Sessao.builder().id(1L).status(SessaoStatus.ABERTA)
                .pauta(Pauta.builder().build())
                .dataHoraFim(LocalDateTime.now().minus(2, ChronoUnit.MINUTES)).build();

        final var sessao2 = Sessao.builder().id(2L).status(SessaoStatus.ABERTA)
                .pauta(Pauta.builder().build())
                .dataHoraFim(LocalDateTime.now().minus(2, ChronoUnit.MINUTES)).build();
        final var sessoesAbertas = List.of(sessao1, sessao2);

        when(sessaoRepository.buscarSessoesAberta()).thenReturn(sessoesAbertas);
        when(votacaoRepository.contabilizarResultado(anyLong())).thenReturn(resultadoMock());

        sessaoService.finalizarSessao();

        assertThat(sessao1.getStatus()).isEqualTo(SessaoStatus.FECHADA);
        assertThat(sessao1.getPauta().getStatus()).isEqualTo(PautaStatus.FECHADA);
        assertThat(sessao1.getPauta().getResultado()).isEqualTo(PautaResultado.APROVADA);
        assertThat(sessao1.getPauta().getTotalVotos()).isEqualTo(10);
        assertThat(sessao1.getPauta().getTotalVotosSim()).isEqualTo(7);
        assertThat(sessao1.getPauta().getTotalVotosNao()).isEqualTo(3);

        verify(sessaoRepository, times(sessoesAbertas.size())).saveAndFlush(any());
        verify(notificarEventoMq, times(sessoesAbertas.size())).notificarEvento(any());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    @DisplayName("Deve votar quando uma sessao estiver aberta e voto deve ser computado")
    void t6(final Boolean voto) {
        final var pautaId = 1L;
        final var votacao = Votacao.builder().voto(voto).build();
        final var sessao = Sessao.builder().id(1L).status(SessaoStatus.ABERTA)
                .pauta(Pauta.builder().build())
                .dataHoraFim(LocalDateTime.now().minus(2, ChronoUnit.MINUTES)).build();

        when(sessaoRepository.buscarSessaoAberta(pautaId)).thenReturn(Optional.of(sessao));
        when(votacaoRepository.associadoJaVotou(pautaId, votacao.getAssociadoId())).thenReturn(false);

        sessaoService.votar(pautaId, votacao);

        verify(votacaoRepository).saveAndFlush(any());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    @DisplayName("Deve lançar exception quando sessão estiver fechada")
    void t7(final Boolean voto) {
        final var pautaId = 1L;
        final var votacao = Votacao.builder().voto(voto).build();

        when(sessaoRepository.buscarSessaoAberta(pautaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessaoService.votar(pautaId, votacao))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("Não há sessão aberta para está pauta [ id: %s ]!", pautaId));

        verify(votacaoRepository, never()).saveAndFlush(any());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    @DisplayName("Deve lançar exception quando associado já votou")
    void t8(final Boolean voto) {
        final var pautaId = 1L;
        final var votacao = Votacao.builder().voto(voto).build();
        final var sessao = Sessao.builder().id(1L).status(SessaoStatus.ABERTA)
                .pauta(Pauta.builder().build())
                .dataHoraFim(LocalDateTime.now().minus(2, ChronoUnit.MINUTES)).build();

        when(sessaoRepository.buscarSessaoAberta(pautaId)).thenReturn(Optional.of(sessao));
        when(votacaoRepository.associadoJaVotou(pautaId, votacao.getAssociadoId())).thenReturn(true);

        assertThatThrownBy(() -> sessaoService.votar(pautaId, votacao))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Associado Já votou. Permitido apenas um voto por associado!");

        verify(votacaoRepository, never()).saveAndFlush(any());
    }

    private VotacaoPautaResultado resultadoMock() {
        return new VotacaoPautaResultado() {
            @Override
            public Integer getTotalVotos() { return 10; }

            @Override
            public Integer getTotalVotosSim() {  return 7; }

            @Override
            public Integer getTotalVotosNao() {  return 3; }
        };
    }

    private static class ParameterizedTestNullConverter extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object object, Class<?> clazz) throws ArgumentConversionException {
            if ("null".equals(object)) {
                return null;
            }

            return DefaultArgumentConverter.INSTANCE.convert(object, clazz);
        }
    }
}
