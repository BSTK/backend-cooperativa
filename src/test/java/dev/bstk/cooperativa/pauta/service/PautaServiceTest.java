package dev.bstk.cooperativa.pauta.service;

import dev.bstk.cooperativa.pauta.model.Pauta;
import dev.bstk.cooperativa.pauta.repository.PautaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
