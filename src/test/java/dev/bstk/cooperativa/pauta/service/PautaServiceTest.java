package dev.bstk.cooperativa.pauta.service;

import dev.bstk.cooperativa.pauta.model.Enums.PautaStatus;
import dev.bstk.cooperativa.pauta.model.Pauta;
import dev.bstk.cooperativa.pauta.repository.PautaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @InjectMocks
    private PautaService pautaService;

    @Mock
    private PautaRepository pautaRepository;


    @Test
    @DisplayName("Deve cadastrar uma nova pauta")
    void t1() {
        final var novaPauta = Pauta.builder().titulo("Nova Pauta").build();

        when(pautaRepository.existePautaJaCadastrada(anyString())).thenReturn(false);
        when(pautaRepository.save(any())).thenReturn(novaPauta);

        final var novaPautaCadastrada = pautaService.cadastrarNovaPauta(novaPauta);

        assertThat(novaPautaCadastrada).isNotNull();
        assertThat(novaPautaCadastrada.getTitulo()).isNotNull().isNotEmpty();

        verify(pautaRepository, times(1)).existePautaJaCadastrada(anyString());
        verify(pautaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar expception quando tentar cadastrar uma pauta que já existe")
    void t2() {
        when(pautaRepository.existePautaJaCadastrada(anyString())).thenReturn(true);

        final var novaPauta = Pauta.builder().titulo("Nova Pauta").build();
        assertThatThrownBy(() -> pautaService.cadastrarNovaPauta(novaPauta))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(String.format("Pauta [ %s ] já cadastrada!", novaPauta.getTitulo()));

        verify(pautaRepository, times(1)).existePautaJaCadastrada(anyString());
        verify(pautaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar uma pauta já finalizada")
    void t3() {
        final var pautaId = 1L;
        final var pautaEncontrada = Pauta.builder().id(pautaId).status(PautaStatus.FECHADA).build();
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pautaEncontrada));

        final var pautaCadastrada = pautaService.buscarPautaFinalizada(pautaId);

        assertThat(pautaCadastrada).isEqualTo(pautaEncontrada);
        verify(pautaRepository).findById(pautaId);
    }

    @Test
    @DisplayName("Deve lançar exception quando não existye pauta cadastrada para id informado")
    void t4() {
        final var pautaId = 1L;
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pautaService.buscarPautaFinalizada(pautaId))
           .isInstanceOf(IllegalArgumentException.class)
           .hasMessage(String.format("Não existe pauta cadastrada [ id: %s ]!", pautaId));

        verify(pautaRepository).findById(pautaId);
    }

    @Test
    @DisplayName("Deve lançar exception de pauta n]ao finalizada")
    void t5() {
        final var pautaId = 1L;
        final var pautaEncontrada = Pauta.builder().id(pautaId).status(PautaStatus.ABERTA).build();
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pautaEncontrada));

        assertThatThrownBy(() -> pautaService.buscarPautaFinalizada(pautaId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(String.format("Pauta ainda não está finalizada. Status: [ %s ]!", pautaEncontrada.getStatus()));

        verify(pautaRepository).findById(pautaId);
    }
}
