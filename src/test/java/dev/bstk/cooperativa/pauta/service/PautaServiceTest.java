package dev.bstk.cooperativa.pauta.service;

import dev.bstk.cooperativa.pauta.model.Pauta;
import dev.bstk.cooperativa.pauta.model.SessaoVotacao;
import dev.bstk.cooperativa.pauta.model.Status;
import dev.bstk.cooperativa.pauta.model.Votacao;
import dev.bstk.cooperativa.pauta.repository.PautaRepository;
import dev.bstk.cooperativa.pauta.repository.SessaoVotacaoRepository;
import dev.bstk.cooperativa.pauta.repository.VotacaoRepository;
import org.assertj.core.api.Assertions;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @InjectMocks
    private PautaService pautaService;

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private VotacaoRepository votacaoRepository;

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Captor
    private ArgumentCaptor<SessaoVotacao> sessaoVotacaoCaptor;

    @Test
    @DisplayName("Deve cadastrar uma nova pauta")
    void t1() {
        final var novaPauta = Pauta.builder().titulo("Nova Pauta").build();

        Mockito.when(pautaRepository.existePautaJaCadastrada(Mockito.anyString())).thenReturn(false);
        Mockito.when(pautaRepository.save(Mockito.any())).thenReturn(novaPauta);

        final var novaPautaCadastrada = pautaService.cadastrarNovaPauta(novaPauta);

        Assertions.assertThat(novaPautaCadastrada).isNotNull();
        Assertions.assertThat(novaPautaCadastrada.getTitulo()).isNotNull().isNotEmpty();

        Mockito.verify(pautaRepository, Mockito.times(1)).existePautaJaCadastrada(Mockito.anyString());
        Mockito.verify(pautaRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar expception quando tentar cadastrar uma pauta que já existe")
    void t2() {
        Mockito.when(pautaRepository.existePautaJaCadastrada(Mockito.anyString())).thenReturn(true);

        final var novaPauta = Pauta.builder().titulo("Nova Pauta").build();
        Assertions
            .assertThatThrownBy(() -> pautaService.cadastrarNovaPauta(novaPauta))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(String.format("Pauta [ %s ] já cadastrada!", novaPauta.getTitulo()));

        Mockito.verify(pautaRepository, Mockito.times(1)).existePautaJaCadastrada(Mockito.anyString());
        Mockito.verify(pautaRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar expception quando tentar inciar uma nova sessão sem uma pauta cadastrada")
    void t3() {
        Mockito.when(pautaRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        final Long pautaId = 1L;
        Assertions
            .assertThatThrownBy(() -> pautaService.iniciarSessaoVotacao(pautaId, 2L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(String.format("Não existe pauta cadastrada [ id: %s ]!", pautaId));
    }

    @Test
    @DisplayName("Deve lançar expception quando tentar inciar uma nova sessão de uma pauta já encerrada")
    void t4() {
        final var pautaEncerrada = Pauta.builder().status(Status.PautaStatus.ENCERRADA).build();
        Mockito.when(pautaRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(pautaEncerrada));

        final Long pautaId = 1L;
        Assertions
            .assertThatThrownBy(() -> pautaService.iniciarSessaoVotacao(pautaId, 2L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(String.format("Está pauta já está encerrada [ id: %s ]!", pautaId));
    }

    @Test
    @DisplayName("Deve lançar expception quando tentar inciar uma nova sessão de uma pauta já em votação")
    void t5() {
        final var pautaCriada = Pauta.builder().status(Status.PautaStatus.CRIADA).build();
        Mockito.when(pautaRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(pautaCriada));
        Mockito.when(sessaoVotacaoRepository.pautaEstaEmVotacao(Mockito.anyLong())).thenReturn(true);

        final Long pautaId = 1L;
        Assertions
            .assertThatThrownBy(() -> pautaService.iniciarSessaoVotacao(pautaId, 2L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(String.format("Está pauta já está em votação [ id: %s ]!", pautaId));
    }

    @ParameterizedTest
    @CsvSource({"null, 1", "0, 1", "2, 2"})
    @DisplayName("Deve inciar uma nova sessão de votação com tempo de duração válido")
    void t6(@ConvertWith(ParameterizedTestNullConverter.class) final Long tempoDuracao,
                                                               final Long duracaoEmMinutos) {

        final var pautaCriada = Pauta.builder().status(Status.PautaStatus.CRIADA).build();
        final var sessaoVotacaoSalva = SessaoVotacao.builder().pauta(pautaCriada).build();

        Mockito.when(pautaRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(pautaCriada));
        Mockito.when(sessaoVotacaoRepository.pautaEstaEmVotacao(Mockito.anyLong())).thenReturn(false);
        Mockito.when(sessaoVotacaoRepository.save(Mockito.any())).thenReturn(sessaoVotacaoSalva);

        final var sessaoVotacaoCriada = pautaService.iniciarSessaoVotacao(1L, tempoDuracao);
        Assertions.assertThat(sessaoVotacaoCriada).isNotNull();
        Assertions.assertThat(sessaoVotacaoCriada.getPauta()).isNotNull();

        Mockito.verify(sessaoVotacaoRepository).save(sessaoVotacaoCaptor.capture());

        final var sessaoVotacaoIniciada = sessaoVotacaoCaptor.getValue();
        Assertions.assertThat(sessaoVotacaoIniciada.getStatus()).isEqualTo(Status.SessaoVotacaoStatus.INICIADA);
        Assertions.assertThat(sessaoVotacaoIniciada.getPauta().getStatus()).isEqualTo(Status.PautaStatus.EM_VOTACAO);

        final var duracaoEmMinutosCalculada = Duration
                .between(sessaoVotacaoIniciada.getDataHoraInicio(), sessaoVotacaoIniciada.getDataHoraTermino())
                .toMinutes();

        Assertions.assertThat(duracaoEmMinutosCalculada).isEqualTo(duracaoEmMinutos);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    @DisplayName("Deve computar um voto para uma pauta e sessão válida")
    void t7(final Boolean votoInformado) {
        Mockito.when(sessaoVotacaoRepository.pautaNaoEstaEmVotacao(Mockito.anyLong())).thenReturn(false);
        Mockito.when(votacaoRepository.associadoJaVotou(Mockito.anyLong())).thenReturn(false);

        final var voto = Votacao.builder().voto(votoInformado).associadoId(1L).build();
        pautaService.votar(1L, voto);

        Mockito.verify(sessaoVotacaoRepository, Mockito.times(1)).pautaNaoEstaEmVotacao(Mockito.anyLong());
        Mockito.verify(votacaoRepository, Mockito.times(1)).associadoJaVotou(Mockito.anyLong());
        Mockito.verify(votacaoRepository, Mockito.times(1)).saveAndFlush(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar expception quando tentar votar para uma sessão não iniciada.")
    void t8() {
        Mockito.when(sessaoVotacaoRepository.pautaNaoEstaEmVotacao(Mockito.anyLong())).thenReturn(true);

        final Long pautaId = 1L;
        Assertions
           .assertThatThrownBy(() -> pautaService.votar(pautaId, null))
           .isInstanceOf(IllegalArgumentException.class)
           .hasMessage(String.format("Não há sessão aberta para está pauta [ id: %s ]!", pautaId));

        Mockito.verify(sessaoVotacaoRepository, Mockito.times(1)).pautaNaoEstaEmVotacao(Mockito.anyLong());
        Mockito.verify(votacaoRepository, Mockito.never()).associadoJaVotou(Mockito.anyLong());
        Mockito.verify(votacaoRepository, Mockito.never()).saveAndFlush(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar expception ao votar mais de uma vez para o mesmo associado")
    void t9() {
        Mockito.when(sessaoVotacaoRepository.pautaNaoEstaEmVotacao(Mockito.anyLong())).thenReturn(false);
        Mockito.when(votacaoRepository.associadoJaVotou(Mockito.anyLong())).thenReturn(true);

        final Long pautaId = 1L;
        final var voto = Votacao.builder().associadoId(1L).build();
        Assertions
                .assertThatThrownBy(() -> pautaService.votar(pautaId, voto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Associado Já votou. Permitido apenas um voto por associado!");

        Mockito.verify(sessaoVotacaoRepository, Mockito.times(1)).pautaNaoEstaEmVotacao(Mockito.anyLong());
        Mockito.verify(votacaoRepository, Mockito.times(1)).associadoJaVotou(Mockito.anyLong());
        Mockito.verify(votacaoRepository, Mockito.never()).saveAndFlush(Mockito.any());
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
